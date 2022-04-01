package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions
import org.hl7.fhir.dstu3.model.MedicationStatement
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.MedicationStatement as R4MedicationStatement

internal class ChangeSummaryTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_CHANGE_SUMMARY_URL])
    internal fun `it should return an R4 resource with no MedicationChangeSummary info`(extUrl: String) {
        // Given a care connect extension withMedicationChangeSummary extension url
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4MedicationStatement()

        // When changeSummary is called
        changeSummary(r3Extension, r4Resource)

        // Then the resulting R4 resource should have no MedicationChangeSummary extension and base resource unchanged
        val transformedExt = r4Resource.getExtensionByUrl(CARECONNECT_CHANGE_SUMMARY_URL)
        Assertions.assertThat(transformedExt).isNull()

    }

    @ParameterizedTest(name = "Test MedicationStatement changeSummary extensions with IOPS example index: {index} (zero based indexing)")
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
            val pairs = careconnectExampleLoader.loadExample(MedicationStatement::class.java, "ChangeSummary")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
