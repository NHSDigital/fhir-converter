package net.nhsd.fhir.converter.transformer

import io.mockk.Called
import io.mockk.spyk
import io.mockk.verify
import org.hl7.fhir.dstu3.model.StringType
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest

internal class CareconnectTransformerTest {
    private lateinit var transformer: Transformer<R3Resource, R4Resource>

    companion object {
        val R3_RESOURCE = R3MedicationRequest()
        val R4_RESOURCE = R4MedicationRequest()
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
        transformer.transform(R3_RESOURCE, R4_RESOURCE)

        // Then
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
        transformer.transform(R3_RESOURCE, R4_RESOURCE)

        // Then
        verify { extTransformer wasNot Called }
    }
}
