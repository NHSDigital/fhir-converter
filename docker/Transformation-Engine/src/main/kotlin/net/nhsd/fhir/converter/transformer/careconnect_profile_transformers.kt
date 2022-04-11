package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.instance.model.api.IBaseResource

internal const val GPCONNECT_BUNDLE_PROFILE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-StructuredRecord-Bundle-1"
internal const val UKCORE_BUNDLE_PROFILE_URL = "https://fhir.hl7.org.uk/StructureDefinition/UKCore-Bundle"

internal const val CARECONNECT_ALLERGY_PROFILE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-AllergyIntolerance-1"
internal const val UKCORE_ALLERGY_PROFILE_URL = "https://fhir.hl7.org.uk/StructureDefinition/UKCore-AllergyIntolerance"

internal val careconnectProfileUrlMap: HashMap<String, ProfileUrlTransformer> = hashMapOf(
    GPCONNECT_BUNDLE_PROFILE_URL to ::careconnectBundle, CARECONNECT_ALLERGY_PROFILE_URL to ::careConnectAllergy
)


fun careconnectBundle(tgt: IBaseResource) {
    updateProfileUrl(tgt, UKCORE_BUNDLE_PROFILE_URL)
}

fun careConnectAllergy(tgt: IBaseResource) {
    updateProfileUrl(tgt, UKCORE_ALLERGY_PROFILE_URL)
}

private fun updateProfileUrl(tgt: IBaseResource, targetUrl: String) {
    val profile = tgt.meta.profile
    profile.forEach { it.value = targetUrl }
}
