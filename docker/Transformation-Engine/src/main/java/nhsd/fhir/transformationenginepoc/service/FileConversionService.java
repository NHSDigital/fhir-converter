package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirVersionEnum;
import nhsd.fhir.transformationenginepoc.service.transformers.*;
import nhsd.fhir.transformationenginepoc.model.PayloadTypeEnum;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class FileConversionService {

    public String convertFhirSchema(final String currentVersion, final String targetVersion, final String content_type, final String return_type, final String fhirSchema) {

        final String resourceType = getResourceType(content_type.contains("application") && content_type.contains("xml") ? PayloadTypeEnum.XML : PayloadTypeEnum.JSON, fhirSchema);

        final Transformer transformerToUse = getTransformer(resourceType);

        return transformerToUse.transform(getFhirVerion(currentVersion),
                getFhirVerion(targetVersion),
                getFhirPayloadtype(content_type),
                getFhirPayloadtype(return_type),
                fhirSchema);

    }

    private Transformer getTransformer(final String resourceType) {

        final Transformer transformerToUse;
        switch (resourceType) {
            case "MedicationStatement":
                transformerToUse = new MedicationStatementTransformer();
                break;

            case "MedicationRequest":
                transformerToUse = new MedicationRequestTransformer();
                break;

            case "Medication":
                transformerToUse = new MedicationTransformer();
                break;

            default:
                transformerToUse = new ResourceTransformer();
        }

        return transformerToUse;
    }

    private String getResourceType(final PayloadTypeEnum type, final String fhirSchema) {

        if (PayloadTypeEnum.XML.equals(type)) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();

                final Document doc = builder.parse(new InputSource(new StringReader(fhirSchema)));
                return doc.getFirstChild().getNodeName();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        if (PayloadTypeEnum.JSON.equals(type)) {
            final JSONObject json = new JSONObject(fhirSchema);
            return json.getString("resourceType");
        }
        return Strings.EMPTY;
    }

    private PayloadTypeEnum getFhirPayloadtype(final String paylaod) {
        return paylaod.contains("application") && paylaod.contains("xml") ? PayloadTypeEnum.XML : PayloadTypeEnum.JSON;
    }

    private FhirVersionEnum getFhirVerion(final String version) {

        switch (version) {
            case "0.0":
                return FhirVersionEnum.DSTU2_1;
            case "1.0":
                return FhirVersionEnum.DSTU2;
            case "3.0":
                return FhirVersionEnum.DSTU3;
            default:
                return FhirVersionEnum.R4;
        }
    }
}
