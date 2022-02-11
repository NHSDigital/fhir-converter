package net.nhsd.fhir.converter.service

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import net.nhsd.fhir.converter.Converter
import net.nhsd.fhir.converter.FhirParser
import net.nhsd.fhir.converter.getResourceType
import net.nhsd.fhir.converter.transformer.Transformer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.r4.model.DomainResource as R4Resource

@Service
class ConverterService(
    private val fhirParser: FhirParser,
    private val careconnectTransformer: Transformer<R3Resource, R4Resource>,
    private val converter: Converter,
) {

    fun convert(
        resource: String,
        inMediaType: MediaType,
        inFhirVersion: FhirVersionEnum,
        outMediaType: MediaType,
        outFhirVersion: FhirVersionEnum
    ): String {
        val resourceType = getResourceType(resource, inMediaType, inFhirVersion)
        val parsed = fhirParser.parse(resource, inMediaType, resourceType)

        val converted = converter.convert(parsed, inFhirVersion, outFhirVersion)

        if (inFhirVersion == DSTU3) {
            careconnectTransformer.transform(parsed as R3Resource, converted as R4Resource)
        }

        return fhirParser.encode(converted, outMediaType, outFhirVersion)
    }
}
