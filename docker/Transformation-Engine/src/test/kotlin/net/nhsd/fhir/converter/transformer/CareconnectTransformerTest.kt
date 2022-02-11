package net.nhsd.fhir.converter.transformer

import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.StringType
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest

internal class CareconnectTransformerTest {
    private lateinit var transformer: Transformer

    companion object {
        val R3_RESOURCE = R3MedicationRequest()
        val R4_RESOURCE = R4MedicationRequest()
    }

    @Test
    internal fun `it should ignore source if it is R4 and return target as it is`() {
        // Given
        transformer = CareconnectTransformer(hashMapOf())

        // When
        val expected = transformer.transform(R4_RESOURCE, R3_RESOURCE)

        // Then
        assertThat(expected).isEqualTo(R3_RESOURCE)
    }

    @Test
    internal fun `it should only call transform functions for the given extensions`() {
        // Given
        val extUrl = "www.should-be-transformed-extension!.com"
        val extension = R3Extension(extUrl, StringType("value doesn't matter"))

        R3_RESOURCE.addExtension(extension)

        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            extUrl to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        val expectedResource = transformer.transform(R3_RESOURCE as IBaseResource, R4_RESOURCE as IBaseResource)

        // Then
        assertThat(expectedResource).isEqualTo(R4_RESOURCE) // it's r4 resource but those transform functions are mutating extensions inside this r4 resource
        verify { extTransformer(extension, R4_RESOURCE) }
    }

    @Test
    internal fun `it should ignore extensions when there is no entry for their url in the hashmap`() {
        // Given
        val extUrl = "www.should-NOT-be-transformed-extension!.com"
        val extension = R3Extension(extUrl, StringType("value doesn't matter"))

        R3_RESOURCE.addExtension(extension)

        val extTransformer = spyk({ _: R3Extension, _: R4Resource -> Unit })

        val extensionTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
            "www.some-other-extension-url.com" to extTransformer
        )

        transformer = CareconnectTransformer(extensionTransformers)

        // When
        val expectedResource = transformer.transform(R3_RESOURCE as IBaseResource, R4_RESOURCE as IBaseResource)

        // Then
        assertThat(expectedResource).isEqualTo(R4_RESOURCE) // it's r4 resource but those transform functions are mutating extensions inside this r4 resource
        verify { extTransformer wasNot Called }
    }
}
