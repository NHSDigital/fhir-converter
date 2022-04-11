package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.r4.model.Resource as R4Resource

internal const val GPCONNECT_BUNDLE_PROFILE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-StructuredRecord-Bundle-1"
internal const val UKCORE_BUNDLE_PROFILE_URL = "https://fhir.hl7.org.uk/StructureDefinition/UKCore-Bundle"

internal const val CARECONNECT_ALLERGY_PROFILE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-AllergyIntolerance-1"
internal const val UKCORE_ALLERGY_PROFILE_URL = "https://fhir.hl7.org.uk/StructureDefinition/UKCore-AllergyIntolerance"

internal val careconnectProfileUrlMap: HashMap<String, ProfileUrlTransformer> = hashMapOf(
    GPCONNECT_BUNDLE_PROFILE_URL to ::careconnectBundle,
    CARECONNECT_ALLERGY_PROFILE_URL to ::careConnectAllergy
)


fun careconnectBundle(tgt: R4Resource) {
    updateProfileUrl(tgt, UKCORE_BUNDLE_PROFILE_URL)
}

fun careConnectAllergy(tgt: R4Resource) {
    updateProfileUrl(tgt, UKCORE_ALLERGY_PROFILE_URL)
}

private fun updateProfileUrl(tgt: R4Resource, targetUrl: String) {
    val profile = tgt.meta.profile
    profile.forEach {
        if (it.valueAsString != targetUrl) {
            it.value = targetUrl
        }
    }
}
