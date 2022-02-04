package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.FhirVersionEnum.R4
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.Resource as R3Resource
import org.hl7.fhir.r4.model.Resource as R4Resource

@Component
class Converter(private val convertor30to40: VersionConvertor_30_40) {

    fun convert(
        resource: IBaseResource,
        sourceVersion: FhirVersionEnum,
        targetVersion: FhirVersionEnum
    ): IBaseResource =
        if (targetVersion != sourceVersion) {
            if (R4 == targetVersion) {
                convertor30to40.convertResource(resource as R3Resource)
            } else {
                convertor30to40.convertResource(resource as R4Resource)
            }
        } else {
            resource
        }
}
