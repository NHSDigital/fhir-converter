package net.nhsd.fhir.converter.service

import ca.uhn.fhir.context.FhirVersionEnum
import net.nhsd.fhir.converter.Converter
import net.nhsd.fhir.converter.FhirParser
import net.nhsd.fhir.converter.findResolvedAllergy
import net.nhsd.fhir.converter.getResourceType
import net.nhsd.fhir.converter.transformer.Transformer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class ConverterService(
    private val fhirParser: FhirParser,
    private val careconnectTransformer: Transformer,
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

        // collect Ids for resolved allergies if doing dstu3 -> r4 conversion
        val resolvedAllergyIds = findResolvedAllergy(parsed, inFhirVersion, outFhirVersion)

        val converted = converter.convert(parsed, inFhirVersion, outFhirVersion)

        val transformed = careconnectTransformer.transform(parsed, converted)

        // handle resolved allergy

        return fhirParser.encode(transformed, outMediaType, outFhirVersion)
    }
}
