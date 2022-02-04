package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.FhirVersionEnum.R4
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_XML
import org.springframework.stereotype.Component

@Component
class FhirParser(
    private val r3JsonParser: IParser,
    private val r4JsonParser: IParser,
    private val r3XmlParser: IParser,
    private val r4XmlParser: IParser
) {
    fun <T : IBaseResource> parse(resource: String, mediaType: MediaType, resourceType: Class<T>): IBaseResource {
        val isR4 = resourceType.name.contains("r4")

        if (APPLICATION_JSON == mediaType) {
            return if (isR4) {
                r4JsonParser.parseResource(resourceType, resource)
            } else {
                r3JsonParser.parseResource(resourceType, resource)
            }
        } else if (APPLICATION_XML == mediaType) {
            return if (isR4) {
                r4XmlParser.parseResource(resourceType, resource)
            } else {
                r3XmlParser.parseResource(resourceType, resource)
            }
        }
        throw IllegalStateException("Invalid Content-Type")
    }

    fun encode(resource: IBaseResource, mediaType: MediaType, fhirVersion: FhirVersionEnum): String {
        val isR4 = R4 == fhirVersion

        if (APPLICATION_JSON == mediaType) {
            return if (isR4) {
                r4JsonParser.encodeResourceToString(resource)
            } else {
                r3JsonParser.encodeResourceToString(resource)
            }
        } else if (APPLICATION_XML == mediaType) {
            return if (isR4) {
                r4XmlParser.encodeResourceToString(resource)
            } else {
                r3XmlParser.encodeResourceToString(resource)
            }
        }
        throw IllegalStateException("Invalid Accept header")
    }
}
