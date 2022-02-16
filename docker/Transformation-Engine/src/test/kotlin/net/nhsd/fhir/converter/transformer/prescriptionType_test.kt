package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.Coding
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest


internal class PrescriptionTypeTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_GPC_PRESCRIPTION_TYPE_URL])
    internal fun `it should create codings and move over system, code and display`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
            val codeableConcept = R3CodeableConcept().apply {
                coding = listOf(
                    Coding("https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-PrescriptionType-1", "code", "display")
                )
            }
            setValue(codeableConcept)
        }

        val r4Resource = R4MedicationRequest()

        // When
        prescriptionType(r3Extension, r4Resource)

        // Then
        val transformedCoding = r4Resource.courseOfTherapyType.coding
        print(transformedCoding[0].code)

        assertThat(transformedCoding).hasSize(1)
        val coding = transformedCoding[0]
        assertThat(coding.system).isEqualTo("http://hl7.org/fhir/ValueSet/medicationrequest-course-of-therapy")
        assertThat(coding.code).isEqualTo("code")
        assertThat(coding.display).isEqualTo("display")
    }

    @ParameterizedTest(name = "Test MedicationRequest PrescriptionType extension with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(input,
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
            val pairs = careconnectExampleLoader.loadExample(R3MedicationRequest::class.java, "PrescriptionType")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}


