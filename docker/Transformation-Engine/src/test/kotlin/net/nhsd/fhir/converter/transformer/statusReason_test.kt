package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import net.javacrumbs.jsonunit.assertj.JsonAssert
import net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType.APPLICATION_JSON
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest


internal class StatusReasonTest {

    @BeforeEach
    internal fun setUp() {

    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL])
    internal fun `it should map R3 extension to the associated field on R4`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4MedicationRequest()

        // When
        medicationStatusReason(r3Extension, r4Resource)

        // Then
        val transformedStatusReason = r4Resource.statusReason
        assertThat(transformedStatusReason).isNotNull
    }


    @Test
    internal fun runOneExample() {
        val exampleIndex = 0
        val careconnectExampleLoader = CareconnectExampleLoader()
        val converterService = makeConverterService()

        val pair = careconnectExampleLoader.loadExample(
            R3MedicationRequest::class.java, "StatusReason"
        )[exampleIndex] // Careful with the index! some files start with 0 and some with 1

        // When
        val actualResource = converterService.convert(
            pair.input,
            APPLICATION_JSON,
            DSTU3,
            APPLICATION_JSON,
            R4
        )
//        assertJsonEquals(actualResource, pair.output)
        JsonAssert.assertThatJson(actualResource).isEqualTo(pair.output)


    }


    @ParameterizedTest(name = "Test MedicationRequest StatusReason extension with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(input, APPLICATION_JSON, DSTU3, APPLICATION_JSON, R4)

        assertThatJson(actualResource).isEqualTo(expected)
    }


    companion object {
        @JvmStatic
        fun provideExamples(): Stream<Arguments> {
            val careconnectExampleLoader = CareconnectExampleLoader()
            val pairs = careconnectExampleLoader.loadExample(R3MedicationRequest::class.java, "StatusReason")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
