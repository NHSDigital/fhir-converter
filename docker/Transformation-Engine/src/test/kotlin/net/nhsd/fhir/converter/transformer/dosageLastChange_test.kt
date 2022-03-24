package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.Extension
import org.hl7.fhir.r4.model.MedicationStatement
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType

internal class DosageLastChangeTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_DOSAGE_LAST_CHANGE_URL])
    internal fun `it should ignore dosageLastChange extension`(extUrl: String) {
        // Given
        val r3Extension = Extension().apply {
            url = extUrl
        }

        val r4Resource = MedicationStatement()

        // When
        dosageLastChange(r3Extension, r4Resource)

        // Then
        assertThat(r4Resource.hasExtension()).isFalse
    }

    @Test
    internal fun `it should handle IOPS derived examples`() {
        val input = loadExtraExample("extra-examples/input/MedicationStatementDosageLastChange-Extension-3to4_000.json")
        val expected =
            loadExtraExample("extra-examples/expected/MedicationStatementDosageLastChange-Extension-3to4_000.json")
        val converterService = makeConverterService()

        val actualResource = converterService.convert(
            input,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.DSTU3,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.R4
        )
        assertThatJson(actualResource).isEqualTo(expected)
    }
}
