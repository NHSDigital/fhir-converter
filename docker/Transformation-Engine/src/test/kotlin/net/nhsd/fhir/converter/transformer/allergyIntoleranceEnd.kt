package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance


internal class AllergyIntoleranceEndTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL, UKCORE_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should create extension with the associated ukcore url`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4AllergyIntolerance()

        // When
        allergyIntoleranceEnd(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_ALLERGY_INTOLERANCE_END_URL)
        assertThat(transformedExt).isNotNull
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL, UKCORE_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should move endDate across`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val endDate = R3Extension().apply {
                url = "endDate"
                setValue(org.hl7.fhir.dstu3.model.DateTimeType("2016-11-01T00:00:00+00:00"))
            }

            addExtension(endDate)
        }

        val r4Resource = R4AllergyIntolerance()

        // When
        allergyIntoleranceEnd(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_ALLERGY_INTOLERANCE_END_URL)
            .getExtensionByUrl("endDate")

        assertThat(transformedExt).isNotNull
        assertThat((transformedExt.value as DateTimeType).valueAsString).isEqualTo("2016-11-01T00:00:00+00:00")
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL, UKCORE_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should move reasonEnded across`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val endDate = R3Extension().apply {
                url = "reasonEnded"
                setValue(org.hl7.fhir.dstu3.model.StringType("Desensitised to Peanuts"))
            }

            addExtension(endDate)
        }

        val r4Resource = R4AllergyIntolerance()

        // When
        allergyIntoleranceEnd(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_ALLERGY_INTOLERANCE_END_URL)
            .getExtensionByUrl("reasonEnded")

        assertThat(transformedExt).isNotNull
        assertThat((transformedExt.value as StringType).valueAsString).isEqualTo("Desensitised to Peanuts")
    }

    @ParameterizedTest(name = "Test AllergyIntoleranceEnd extension with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(input,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.DSTU3,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.R4
        )
        print(actualResource)
        JsonAssert.assertThatJson(actualResource).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun provideExamples(): Stream<Arguments> {
            val careconnectExampleLoader = CareconnectExampleLoader()
            val pairs = careconnectExampleLoader.loadExample(R3AllergyIntolerance::class.java, "AllergyIntoleranceEnd")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
