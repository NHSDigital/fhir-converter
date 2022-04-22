package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding

fun handleResolvedAllergy(
    resource: IBaseResource,
    resolvedAllergyId: List<String>,
    inFhirVersion: FhirVersionEnum,
    outFhirVersion: FhirVersionEnum

): IBaseResource {
    // only handle for dstu3 -> r4
    if (inFhirVersion != FhirVersionEnum.DSTU3 && outFhirVersion != FhirVersionEnum.R4) {
        return resource
    }

    if (resource is Bundle) {
        for (entry in resource.entry) {
            val entryResource = entry.resource

            if (entryResource is R4AllergyIntolerance && resolvedAllergyId.contains(entry.resource.id)) {
                entryResource.clinicalStatus = createResolvedClinicalStatus()
            }
        }

        return resource
    }
    else {
       if(resource is R4AllergyIntolerance && resolvedAllergyId.contains(resource.id)){
           resource.clinicalStatus = createResolvedClinicalStatus()
       }
        return resource
    }

}

fun createResolvedClinicalStatus(): CodeableConcept {
    // create clinicalStatus
    val resolvedCodeableConcept = CodeableConcept().apply {
        val r4Coding = Coding()
        r4Coding.system = "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical"
        r4Coding.code = "resolved"
        this.coding = listOf(r4Coding)
    }
    return resolvedCodeableConcept
}
