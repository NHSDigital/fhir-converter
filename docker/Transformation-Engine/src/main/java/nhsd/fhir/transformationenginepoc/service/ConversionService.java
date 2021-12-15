package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirVersionEnum;
import nhsd.fhir.transformationenginepoc.service.converter.BundleConverter;
import nhsd.fhir.transformationenginepoc.service.converter.Converter;
import nhsd.fhir.transformationenginepoc.service.converter.MedicationRequestConverter;
import nhsd.fhir.transformationenginepoc.service.converter.MedicationStatementConverter;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@Service
public class ConversionService {

    public String convertFhirSchema(final String currentVersion, final String targetVersion, final MediaType content_type, final MediaType return_type, final String fhirSchema, List<String> desiredResources) throws Exception {

        final String resourceType = getResourceType(content_type, fhirSchema);

        final Converter converterToUse = getConverter(resourceType, desiredResources);

        return converterToUse.convert(getFhirVerion(currentVersion), getFhirVerion(targetVersion), content_type, return_type, fhirSchema);
    }

    private Converter getConverter(final String resourceType, List<String> desiredResources) {

        final Converter converterToUse;
        switch (resourceType) {
            case "MedicationStatement":
                converterToUse = new MedicationStatementConverter();
                break;

            case "MedicationRequest":
                converterToUse = new MedicationRequestConverter();
                break;

            case "Bundle":
                converterToUse = new BundleConverter(desiredResources);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + resourceType);
        }

        return converterToUse;
    }

    private String getResourceType(MediaType content_type, final String fhirSchema) throws ParserConfigurationException, IOException, SAXException {

        if (content_type.getSubtype().equals("xml")) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new InputSource(new StringReader(fhirSchema)));
            return doc.getFirstChild().getNodeName();
        } else {
            final JSONObject json = new JSONObject(fhirSchema);
            return json.getString("resourceType");
        }
    }


    private FhirVersionEnum getFhirVerion(final String version) {
        return "3.0".equals(version) ? FhirVersionEnum.DSTU3 : FhirVersionEnum.R4;
    }
}
