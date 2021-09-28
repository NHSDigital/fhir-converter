package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

@Service
public class ValidationService {

    private FhirValidator myValidator;

    public ValidationResult validateSchema(final String currentVersion, final String fhirSchema) {

        setMyValidationSupport(getFhirContext(currentVersion));

        return myValidator.validateWithResult(fhirSchema);
    }

    public boolean isSchemaValid(MediaType mediaTypeIn, String fhirSchema) {
        return mediaTypeIn.getSubtype().equals("xml") ? isXMLValid(fhirSchema) : isJSONValid(fhirSchema);
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private boolean isXMLValid(String string) {
        try {
            SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(string)));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            return false;
        }
    }

    private void setMyValidationSupport(final FhirContext ctx) {

        myValidator = ctx.newValidator();
        myValidator.setValidateAgainstStandardSchema(false);
        myValidator.setValidateAgainstStandardSchematron(false);

        final DefaultProfileValidationSupport myDefaultValidationSupport = new DefaultProfileValidationSupport(ctx);

        final ValidationSupportChain chain = new ValidationSupportChain(myDefaultValidationSupport, new InMemoryTerminologyServerValidationSupport(ctx), new CommonCodeSystemsTerminologyService(ctx), new SnapshotGeneratingValidationSupport(ctx));
        final CachingValidationSupport myValidationSupport = new CachingValidationSupport(chain);
        final FhirInstanceValidator myInstanceVal = new FhirInstanceValidator(myValidationSupport);

        myValidator.registerValidatorModule(myInstanceVal);
    }

    private FhirContext getFhirContext(final String currentVersion) {

        switch (currentVersion) {
            case "3.0":
                return FhirContext.forDstu3();
            default:
                return FhirContext.forR4();
        }
    }


}
