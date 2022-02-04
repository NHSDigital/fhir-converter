package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum
import com.fasterxml.jackson.databind.ObjectMapper
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.http.MediaType
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.dstu3.model.MedicationStatement as R3MedicationStatement
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.MedicationStatement as R4MedicationStatement

private val om: ObjectMapper = ObjectMapper()

fun getResourceType(
    fhirResource: String,
    mediaType: MediaType,
    fhirVersion: FhirVersionEnum
): Class<out IBaseResource> {

    val isR4 = FhirVersionEnum.R4 == fhirVersion
    val resourceType = getResourceType(fhirResource, mediaType)

    if (isR4) {
        when (resourceType) {
            "MedicationRequest" -> return R4MedicationRequest::class.java
            "MedicationStatement" -> return R4MedicationStatement::class.java
            "Bundle" -> return R4Bundle::class.java
        }
    } else {
        when (resourceType) {
            "MedicationRequest" -> return R3MedicationRequest::class.java
            "MedicationStatement" -> return R3MedicationStatement::class.java
            "Bundle" -> return R3Bundle::class.java
        }
    }

    throw IllegalStateException("Resource: $resourceType is invalid or not supported")
}

private fun getResourceType(fhirSchema: String, mediaType: MediaType): String =
    when (mediaType.subtype) {
        "xml" -> {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(InputSource(StringReader(fhirSchema)))
            doc.firstChild.nodeName
        }
        "json" -> {
            val jsonNode = om.readTree(fhirSchema)
            jsonNode.get("resourceType").asText()
        }
        else -> {
            throw IllegalStateException("Content-Type not supported")
        }
    }
