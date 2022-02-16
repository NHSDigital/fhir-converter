package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.MedicationStatement
import org.hl7.fhir.r4.model.MedicationStatement as R4MedicationStatement
import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import java.util.stream.Stream

internal class LastIssueDateTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_GPC_LAST_ISSUE_DATE_URL, CARECONNECT_LAST_ISSUE_DATE_URL])
    internal fun `it should create MedicationStatementLastIssueDate extension with the associated ukcore url`(extUrl: String) {
        // Given a care connect extension with last issue date url
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4MedicationStatement()

        // When lastIssueDate is called
        lastIssueDate(r3Extension, r4Resource)

        // Then the resulting R4 resource should have an extension with the r4 last issue date url
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_LAST_ISSUE_DATE_URL)
        assertThat(transformedExt).isNotNull
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_GPC_LAST_ISSUE_DATE_URL, CARECONNECT_LAST_ISSUE_DATE_URL])
    internal fun `it should carry over the MedicationStatementLastIssueDate valueDateTime`(extUrl: String) {
        // Given a care connect extension with last issue date url and date time value
        val r3Extension = R3Extension().apply {
            url = extUrl
            setValue(R3DateTimeType("2006-09-06"))
        }

        val r4Resource = R4MedicationStatement()

        // When lastIssueDate is called
        lastIssueDate(r3Extension, r4Resource)

        // Then the resulting R4 resource should have an extension with the correct last issue date value
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_LAST_ISSUE_DATE_URL)
        assertThat(transformedExt).isNotNull

        val transformedExtValue = transformedExt.value
        assertThat((transformedExtValue as R4DateTimeType).valueAsString).isEqualTo("2006-09-06")
    }

    @ParameterizedTest(name = "Test MedicationStatementLastIssueDate extension with IOPS example index: {index} (zero based indexing)")
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
            val pairs = careconnectExampleLoader.loadExample(MedicationStatement::class.java, "LastIssueDate")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }

}


