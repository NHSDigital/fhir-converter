package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

@Component
public class ResourceTypeFactory {
    public static Class<? extends IBaseResource> createResourceType(String fhirResource, MediaType mediaType, FhirVersionEnum fhirVersion) throws ParserConfigurationException, IOException, SAXException {
        String resourceType = getResourceType(fhirResource, mediaType);
        switch (resourceType) {
            case "MedicationRequest":
                return FhirVersionEnum.R4 == fhirVersion ?
                        MedicationRequest.class
                        : org.hl7.fhir.dstu3.model.MedicationRequest.class;
            case "MedicationStatement":
                return FhirVersionEnum.R4 == fhirVersion ?
                        MedicationStatement.class
                        : org.hl7.fhir.dstu3.model.MedicationStatement.class;
        }
        throw new IllegalStateException("Resource not supported");
    }

    private static String getResourceType(final String fhirSchema, MediaType mediaType) throws ParserConfigurationException, IOException, SAXException {
        if ("xml".equals(mediaType.getSubtype())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(fhirSchema)));

            return doc.getFirstChild().getNodeName();
        } else {
            JSONObject json = new JSONObject(fhirSchema);

            return json.getString("resourceType");
        }
    }
}
