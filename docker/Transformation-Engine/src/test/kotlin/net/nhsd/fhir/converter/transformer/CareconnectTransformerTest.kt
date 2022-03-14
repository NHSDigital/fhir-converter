package net.nhsd.fhir.converter.transformer

import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.dstu3.model.StringType as R3StringType
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.StringType as R4StringType

internal class CareconnectTransformerTest {
    private lateinit var transformer: Transformer

    lateinit var aR3Resource: IBaseResource
    lateinit var aR4Resource: IBaseResource

    @BeforeEach
    internal fun setUp() {
        aR3Resource = R3MedicationRequest()
        aR4Resource = R4MedicationRequest()
    }

    @Test
    internal fun `it should ignore source if it is R4 and return target as it is`() {
        // Given
        transformer = CareconnectTransformer(hashMapOf())

        // When
        val expected = transformer.transform(aR4Resource, aR3Resource)

        // Then
        assertThat(expected).isEqualTo(aR3Resource)
    }

    @Test
    internal fun `it should ignore target if target is NOT R4 and return target as it is`() {
        // Given
        // We have an extension that we need to transform
        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })
        val extUrl = "www.should-be-transformed-extension!.com"

        val extension = R3Extension(extUrl, R3StringType("value doesn't matter"))
        (aR3Resource as R3Resource).addExtension(extension)

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            extUrl to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        assertDoesNotThrow {
            // Then
            // Expect no cast exception. i.e. if we don't filter non-r4 resources we should see an exception here
            transformer.transform(aR3Resource, aR3Resource)
        }
    }

    @Test
    internal fun `it should remove all the extensions that need transformation before passing them to the transform function`() {
        // Given
        val extUrl = "www.should-be-transformed-extension!.com"
        val r3Extension = R3Extension(extUrl, R3StringType("value doesn't matter"))
        val r4Extension = R4Extension(extUrl, R4StringType("value doesn't matter"))

        (aR3Resource as R3Resource).addExtension(r3Extension)
        (aR4Resource as R4Resource).addExtension(r4Extension)

        val extTransformer = spyk({ _: R3Extension, r4: R4Resource ->
            // Then
            // Inside this spy function body, we make sure that extensions are removed before passing it to transform function
            (assertThat(r4.getExtensionByUrl(extUrl)).isNull())
        })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            extUrl to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        transformer.transform(aR3Resource, aR4Resource)
    }

    @Test
    internal fun `it should ignore (not remove) extensions that do not require transformation`() {
        // Given
        val extUrl = "www.ignore-extension!.com"
        val r4Extension = R4Extension(extUrl, R4StringType("value doesn't matter"))

        (aR4Resource as R4Resource).addExtension(r4Extension)

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf()

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        transformer.transform(aR3Resource, aR4Resource)

        // Then
        assertThat((aR4Resource as R4Resource).getExtensionByUrl(extUrl)).isNotNull
    }

    @Test
    internal fun `it should only call transform functions for the given extensions`() {
        // Given
        val extUrl = "www.should-be-transformed-extension!.com"
        val extension = R3Extension(extUrl, R3StringType("value doesn't matter"))

        (aR3Resource as R3Resource).addExtension(extension)

        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            extUrl to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        val expectedResource = transformer.transform(aR3Resource, aR4Resource)

        // Then
        assertThat(expectedResource).isEqualTo(aR4Resource) // it's r4 resource but those transform functions are mutating extensions inside this r4 resource
        verify { extTransformer(extension, aR4Resource as R4Resource) }
    }

    @Test
    internal fun `it should ignore extensions when there is no entry for their url in the hashmap`() {
        // Given
        val extUrl = "www.should-NOT-be-transformed-extension!.com"
        val extension = R3Extension(extUrl, R3StringType("value doesn't matter"))

        (aR3Resource as R3Resource).addExtension(extension)

        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            "www.some-other-extension-url.com" to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        val expectedResource = transformer.transform(aR3Resource, aR4Resource)

        // Then
        assertThat(expectedResource).isEqualTo(aR4Resource) // it's r4 resource but those transform functions are mutating extensions inside this r4 resource
        verify { extTransformer wasNot Called }
    }

    @Test
    internal fun `it should transform extension in AllergyIntolerance code field`() {
        // Given
        val extUrl = "www.should-be-transformed-extension!.com"
        val extension = R3Extension(extUrl, R3StringType("value doesn't matter"))

        val r3AllergyIntolerance = R3AllergyIntolerance().apply {
            code = R3CodeableConcept()
            code.addExtension(extension)
        }

        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            extUrl to extTransformer
        )

        val r4AllergyIntolerance = R4AllergyIntolerance()

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        val expectedResource = transformer.transform(r3AllergyIntolerance, r4AllergyIntolerance)

        // Then
        assertThat(expectedResource).isEqualTo(r4AllergyIntolerance)
        verify { extTransformer(extension, r4AllergyIntolerance as R4Resource) }
    }
}
