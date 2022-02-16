package net.nhsd.fhir.converter.transformer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.IdType as R3IdType
import org.hl7.fhir.dstu3.model.StringType as R3StringType
import org.hl7.fhir.r4.model.MedicationStatement as R4MedicationStatement

internal class PrescriptionIdTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_DESCRIPTION_ID_URL])
    internal fun `it should create extension with the associated ukcore url`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = org.hl7.fhir.r4.model.MedicationStatement()

        // When
        descriptionId(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_DESCRIPTION_ID_URL)
        assertThat(transformedExt).isNotNull
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_DESCRIPTION_ID_URL])
    internal fun `it should carry over the descriptionId extension`(extUrl: String) {
        // Given
        val descId = 22298006L

        val r3Extension = R3Extension().apply {
            addExtension(R3Extension("descriptionId", R3IdType(descId)))
        }

        val r4Resource = R4MedicationStatement()

        // When
        descriptionId(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_DESCRIPTION_ID_URL)

        assertThat(transformedExt.extension).hasSize(1)
        val idType = transformedExt.extension[0].value
        assertThat(idType.toString()).isEqualTo(descId.toString())
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_DESCRIPTION_ID_URL])
    internal fun `it should carry over the descriptionDisplay extension`(extUrl: String) {
        // Given
        val descDisplay = "description-display"

        val r3Extension = R3Extension().apply {
            addExtension(R3Extension("descriptionDisplay", R3StringType(descDisplay)))
        }

        val r4Resource = R4MedicationStatement()

        // When
        descriptionId(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_DESCRIPTION_ID_URL)

        assertThat(transformedExt.extension).hasSize(1)
        val actDescDisplay = transformedExt.extension[0].value
        assertThat(actDescDisplay.toString()).isEqualTo(descDisplay)
    }
}

