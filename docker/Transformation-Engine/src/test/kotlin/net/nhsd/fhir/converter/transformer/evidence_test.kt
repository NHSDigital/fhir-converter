package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.r4.model.Reference as R4Reference
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.dstu3.model.Reference as R3Reference
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import java.util.stream.Stream

internal class EvidenceTest {

    @Test
    internal fun `it should create AllergyIntolerance Evidence extension`() {
        // Given a care connect allergy intolerance evidence extension
        val r3Extension = R3Extension().apply {
            url = "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-Evidence-1"
        }

        val r4Resource = R4AllergyIntolerance()

        // When evidence is called
        evidence(r3Extension, r4Resource)

        // Then the resulting R4 Allergy Intolerance resource should have an evidence extension
        val transformedExtension = r4Resource.getExtensionByUrl(UKCORE_EVIDENCE_URL)
        assertThat(transformedExtension).isNotNull
    }

    @Test
    internal fun `it should transfer the valueReference to the new extension`() {
        // Given a care connect allergy intolerance evidence extension
        val r3Extension = R3Extension().apply {
            url = "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-Evidence-1"
            val extReference = R3Reference("DiagnosticReport/3BF93498-ABCF")
            setValue(extReference)
        }

        val r4Resource = R4AllergyIntolerance()

        // When evidence is called
        evidence(r3Extension, r4Resource)

        // Then the resulting R4 Allergy Intolerance resource should have an evidence extension and value
        val transformedExtension = r4Resource.getExtensionByUrl(UKCORE_EVIDENCE_URL)
        assertThat(transformedExtension).isNotNull
        assertThat(transformedExtension.value).isInstanceOf(R4Reference::class.java)
        assertThat((transformedExtension.value as R4Reference).reference)
            .isEqualTo("DiagnosticReport/3BF93498-ABCF")
    }

    @ParameterizedTest(name = "Test AllergyIntolerance Evidence extension with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(
            input,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.DSTU3,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.R4
        )

        JsonAssert.assertThatJson(actualResource).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun provideExamples(): Stream<Arguments> {
            val careconnectExampleLoader = CareconnectExampleLoader()
            val pairs = careconnectExampleLoader.loadExample(R3AllergyIntolerance::class.java, "Evidence")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }

}

