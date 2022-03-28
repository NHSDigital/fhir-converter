package net.nhsd.fhir.converter.transformer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.IdType as R3IdType
import org.hl7.fhir.dstu3.model.StringType as R3StringType
import org.hl7.fhir.r4.model.IdType as R4IdType
import org.hl7.fhir.r4.model.StringType as R4StringType

internal class DescriptionIdAndDescriptionDisplayTest {

    @Test
    internal fun `it should transform descriptionId extension`() {
        // Given
        val desId = R3Extension("descriptionId", R3IdType("descriptionId value"))
        val desDisplay = R3Extension("descriptionDisplay", R3StringType("descriptionDisplay value"))
        val r3Extension = R3Extension().apply {
            url = CARECONNECT_DESCRIPTION_ID_URL
            addExtension(desId)
            addExtension(desDisplay)
        }

        // When
        val actualExtension = descriptionIdAndDisplay(r3Extension)

        // Then
        assertThat(actualExtension.url).isEqualTo(UKCORE_DESCRIPTION_ID_URL)

        val r3DesId = actualExtension.getExtensionByUrl("descriptionId")
        assertThat(r3DesId.url).isEqualTo("descriptionId")
        assertThat(r3DesId.value).isInstanceOf(R4IdType::class.java)
        assertThat(r3DesId.value.primitiveValue()).isEqualTo("descriptionId value")

        val r3DesDisplay = actualExtension.getExtensionByUrl("descriptionDisplay")
        assertThat(r3DesDisplay.url).isEqualTo("descriptionDisplay")
        assertThat(r3DesDisplay.value).isInstanceOf(R4StringType::class.java)
        assertThat(r3DesDisplay.value.primitiveValue()).isEqualTo("descriptionDisplay value")
    }
}
