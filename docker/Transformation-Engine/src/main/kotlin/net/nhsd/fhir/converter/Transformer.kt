package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.Resource as R3Resource
import org.hl7.fhir.r4.model.Resource as R4Resource

@Component
class Transformer {
    fun transform(resource: IBaseResource, version: FhirVersionEnum): IBaseResource =
        if (version == FhirVersionEnum.DSTU3) {
            val source = resource as R3Resource
            source.copy()
        } else {
            val source = resource as R4Resource
            source.copy()
        }
}
