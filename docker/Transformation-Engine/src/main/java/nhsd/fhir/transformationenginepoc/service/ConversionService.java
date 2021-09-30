package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirVersionEnum;
import nhsd.fhir.transformationenginepoc.service.transformers.MedicationRequestTransformer;
import nhsd.fhir.transformationenginepoc.service.transformers.MedicationStatementTransformer;
import nhsd.fhir.transformationenginepoc.service.transformers.Transformer;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class ConversionService {

    public String convertFhirSchema(final String currentVersion, final String targetVersion, final MediaType content_type, final MediaType return_type, final String fhirSchema) {

        final String resourceType = getResourceType(fhirSchema);

        final Transformer transformerToUse = getTransformer(resourceType);

        return transformerToUse.transform(getFhirVerion(currentVersion), getFhirVerion(targetVersion), content_type, return_type, fhirSchema);
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

            default:
                throw new IllegalStateException("Unexpected value: " + resourceType);
        }

        return transformerToUse;
    }

    private String getResourceType(final String fhirSchema) {

        if (fhirSchema.startsWith("<")) {
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

        } else {
            try {
                final JSONObject json = new JSONObject(fhirSchema);
                return json.getString("resourceType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
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
