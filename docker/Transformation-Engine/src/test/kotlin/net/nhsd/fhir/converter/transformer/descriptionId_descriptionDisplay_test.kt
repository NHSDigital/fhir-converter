package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.IdType
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept

internal class DescriptionIdAndDescriptionDisplayTest {

    @Test
    internal fun `it should transform descriptionId extension`() {
        val desId = R3Extension("descriptionId", IdType("2579700012"))
        val desDisplay = R3Extension("descriptionDisplay", IdType("Adverse reaction to erythromycin"))
        val r3Extension = R3Extension().apply {
            url = CARECONNECT_DESCRIPTION_ID_URL
            addExtension(desId)
            addExtension(desDisplay)
        }

        val actualExtension = descriptionIdAndDisplay(r3Extension)

        assertThat(actualExtension.url).isEqualTo(UKCORE_DESCRIPTION_ID_URL)
        val r3DesId = actualExtension.getExtensionByUrl("descriptionId")
        assertThat(r3DesId.url).isEqualTo("descriptionId")
        val r3DesDisplay = actualExtension.getExtensionByUrl("descriptionDisplay")
        assertThat(r3DesDisplay.url).isEqualTo("descriptionDisplay")
    }

    @Test
    internal fun `it should transform AllergyIntolerance_coding containing descriptionId and descriptionDisplay extensions`() {
        val desId = R3Extension("descriptionId", IdType("2579700012"))
        val desDisplay = R3Extension("descriptionDisplay", IdType("Adverse reaction to erythromycin"))
        val r3Extension = R3Extension().apply {
            url = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid"
            addExtension(desId)
            addExtension(desDisplay)
        }

        val source = R3AllergyIntolerance().apply {
            code = R3CodeableConcept().apply {
                addExtension(r3Extension)
            }
        }

        val r4Resource = R4AllergyIntolerance().apply {
            code = R4CodeableConcept()
        }

        medicationStatusReason(r3Extension, r4Resource)
    }

    @Test
    internal fun runOneExample() {
        val input = loadExtraExample("extra-examples/expected/AllergyIntoleranceExtensionId-Extension-3to4_000.json")
        val expected =
            loadExtraExample("extra-examples/expected/AllergyIntoleranceExtensionId-Extension-3to4_000.json")
        val converterService = makeConverterService()

        val actualResource = converterService.convert(
            input,
            APPLICATION_JSON,
            DSTU3,
            APPLICATION_JSON,
            R4
        )

        JsonAssert.assertThatJson(actualResource).isEqualTo(expected)
    }
}
