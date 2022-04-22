package net.nhsd.fhir.converter
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.instance.model.api.IBaseResource

fun findResolvedAllergy(
    resource: IBaseResource,
    inFhirVersion: FhirVersionEnum,
    outFhirVersion: FhirVersionEnum

): List<String> {
    var resourceIds = mutableListOf<String>()

    if(inFhirVersion != DSTU3 && outFhirVersion != R4) {
        return resourceIds
    }

    if (resource is Bundle) {
        for (entry in resource.entry) {
            val entryResource = entry.resource

            if (isResourceResolvedAllergy(entryResource)) {
                resourceIds.add(entryResource.id)
            }
        }
    } else {
        if (isResourceResolvedAllergy(resource)) {
            resourceIds.add((resource as R3AllergyIntolerance).id)
        }

    }
    return resourceIds
}

fun isResourceResolvedAllergy(resource: IBaseResource): Boolean {
    if (resource is R3AllergyIntolerance &&
        resource.clinicalStatus == R3AllergyIntolerance.AllergyIntoleranceClinicalStatus.RESOLVED
    ) {
        return true
    }
    return false
}
