package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.DomainResource as R4Resource

private const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"

private const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"

internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation
)

fun repeatInformation(src: R3Extension, tgt: R4Resource) {
    if (src.url != CARECONNECT_REPEAT_INFORMATION_URL) return

    val tgtExt = tgt.getExtensionsByUrl(CARECONNECT_REPEAT_INFORMATION_URL).firstOrNull()
    tgtExt?.let {
        it.setUrlElement(UriType(UKCORE_REPEAT_INFORMATION_URL))
    }
}

