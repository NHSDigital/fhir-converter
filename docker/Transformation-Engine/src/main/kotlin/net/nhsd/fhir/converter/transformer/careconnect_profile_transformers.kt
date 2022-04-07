package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource

internal const val GPCONNECT_BUNDLE_PROFILE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-StructuredRecord-Bundle-1"

internal val careconnectProfileUrlMap: HashMap<String, ProfileUrlTransformer> = hashMapOf(
    GPCONNECT_BUNDLE_PROFILE_URL to ::careconnectBundle
)


fun careconnectBundle(tgtEntry: R4Resource) {
    tgtEntry
}
