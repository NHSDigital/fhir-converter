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
import org.hl7.fhir.dstu3.model.MedicationStatement as R3MedicationStatement
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept
import org.hl7.fhir.r4.model.MedicationStatement as R4MedicationStatement

internal class PrescribingAgencyTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_PRESCRIBING_AGENCY_URL, CARECONNECT_GPC_PRESCRIBING_AGENCY_URL])
    internal fun `it should create extension with the associated ukcore url`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4MedicationStatement()

        // When
        prescribingAgency(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_PRESCRIBING_ORGANIZATION_URL)
        assertThat(transformedExt).isNotNull
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_PRESCRIBING_AGENCY_URL, CARECONNECT_GPC_PRESCRIBING_AGENCY_URL])
    internal fun `it should create codings and move over CareConnect-PrescribingAgency-1 and text field if present`(
        extUrl: String
    ) {
        // Given
        val r3CodingSystem = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-PrescribingAgency-1"
        val expR4CodingSystem = "https://fhir.hl7.org.uk/CodeSystem/UKCore-MedicationPrescribingOrganization"

        val r3Extension = R3Extension().apply {
            url = extUrl
            val codeableConcept = R3CodeableConcept().apply {
                coding = listOf(
                    Coding(r3CodingSystem, "the-code", "the-display")
                )
                text = "the-text"
            }
            setValue(codeableConcept)
        }

        val r4Resource = R4MedicationStatement()

        // When
        prescribingAgency(r3Extension, r4Resource)

        // Then
        val transformedCoding =
            r4Resource.getExtensionByUrl(UKCORE_PRESCRIBING_ORGANIZATION_URL).value as R4CodeableConcept

        assertThat(transformedCoding.coding).hasSize(1)
        val coding = transformedCoding.coding[0]
        assertThat(coding.system).isEqualTo(expR4CodingSystem)
        assertThat(coding.code).isEqualTo("the-code")
        assertThat(coding.display).isEqualTo("the-display")

        assertThat(transformedCoding.text).isEqualTo("the-text")
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_PRESCRIBING_AGENCY_URL, CARECONNECT_GPC_PRESCRIBING_AGENCY_URL])
    internal fun `it should create codings and move over display and code if url is NOT present`(extUrl: String) {
        // Given
        val r3CodingSystem = null
        val expR4CodingSystem = null

        val r3Extension = R3Extension().apply {
            url = extUrl
            val codeableConcept = R3CodeableConcept().apply {
                coding = listOf(
                    Coding(r3CodingSystem, "the-code", "the-display")
                )
            }
            setValue(codeableConcept)
        }

        val r4Resource = R4MedicationStatement()

        // When
        prescribingAgency(r3Extension, r4Resource)

        // Then
        val transformedCoding =
            r4Resource.getExtensionByUrl(UKCORE_PRESCRIBING_ORGANIZATION_URL).value as R4CodeableConcept

        assertThat(transformedCoding.coding).hasSize(1)
        val coding = transformedCoding.coding[0]
        assertThat(coding.system).isEqualTo(expR4CodingSystem)
        assertThat(coding.code).isEqualTo("the-code")
        assertThat(coding.display).isEqualTo("the-display")
    }

    @ParameterizedTest(name = "Test MedicationStatement PrescribingAgency extension with IOPS example index: {index} (zero based indexing)")
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
            val pairs = careconnectExampleLoader.loadExample(R3MedicationStatement::class.java, "PrescribingAgency", 0)
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
