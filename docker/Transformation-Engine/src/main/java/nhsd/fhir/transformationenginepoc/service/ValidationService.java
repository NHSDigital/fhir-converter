package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private FhirValidator myValidator;

    public ValidationResult validateSchema(final String currentVersion, final String fhirSchema) {

        setMyValidationSupport(getFhirContext(currentVersion));

        return myValidator.validateWithResult(fhirSchema);
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
            case "0.0":
                return FhirContext.forDstu2_1();
            case "1.0":
                return FhirContext.forDstu2();
            case "3.0":
                return FhirContext.forDstu3();
            default:
                return FhirContext.forR4();
        }
    }

}
