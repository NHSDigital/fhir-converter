package nhsd.fhir.transformationenginepoc.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyValidationSupport implements IValidationSupport {
    private List<StructureDefinition> definitions = new ArrayList<>();
    private HashMap<Object, StructureDefinition> definitionsMap = new HashMap<>();
    private FhirContext fhirContext;

    public MyValidationSupport(FhirContext fhirContext) {
        definitions.forEach(def -> definitionsMap.put(def.getUrl(), def));
        this.fhirContext = fhirContext;
    }


    @Nullable
    @Override
    public ValueSetExpansionOutcome expandValueSet(ValidationSupportContext theValidationSupportContext, @Nullable ValueSetExpansionOptions theExpansionOptions, @NotNull IBaseResource theValueSetToExpand) {
        return IValidationSupport.super.expandValueSet(theValidationSupportContext, theExpansionOptions, theValueSetToExpand);
    }

    @Nullable
    @Override
    public List<IBaseResource> fetchAllConformanceResources() {
        return IValidationSupport.super.fetchAllConformanceResources();
    }

    @Nullable
    @Override
    public <T extends IBaseResource> List<T> fetchAllStructureDefinitions() {
        return IValidationSupport.super.fetchAllStructureDefinitions();
    }

    @Nullable
    @Override
    public <T extends IBaseResource> List<T> fetchAllNonBaseStructureDefinitions() {
        return IValidationSupport.super.fetchAllNonBaseStructureDefinitions();
    }

    @Nullable
    @Override
    public IBaseResource fetchCodeSystem(String theSystem) {
        return IValidationSupport.super.fetchCodeSystem(theSystem);
    }

    @Nullable
    @Override
    public <T extends IBaseResource> T fetchResource(@Nullable Class<T> theClass, String theUri) {
        return (T) definitionsMap.get(theUri);
    }

    @Nullable
    @Override
    public StructureDefinition fetchStructureDefinition(String theUrl) {
        return definitionsMap.get(theUrl);
    }

    @Override
    public boolean isCodeSystemSupported(ValidationSupportContext theValidationSupportContext, String theSystem) {
        return IValidationSupport.super.isCodeSystemSupported(theValidationSupportContext, theSystem);
    }

    @Nullable
    @Override
    public IBaseResource fetchValueSet(String theValueSetUrl) {
        return IValidationSupport.super.fetchValueSet(theValueSetUrl);
    }

    @Nullable
    @Override
    public CodeValidationResult validateCode(ValidationSupportContext theValidationSupportContext, ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay, String theValueSetUrl) {
        return IValidationSupport.super.validateCode(theValidationSupportContext, theOptions, theCodeSystem, theCode, theDisplay, theValueSetUrl);
    }

    @Nullable
    @Override
    public CodeValidationResult validateCodeInValueSet(ValidationSupportContext theValidationSupportContext, ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay, @NotNull IBaseResource theValueSet) {
        return IValidationSupport.super.validateCodeInValueSet(theValidationSupportContext, theOptions, theCodeSystem, theCode, theDisplay, theValueSet);
    }

    @Nullable
    @Override
    public LookupCodeResult lookupCode(ValidationSupportContext theValidationSupportContext, String theSystem, String theCode) {
        return IValidationSupport.super.lookupCode(theValidationSupportContext, theSystem, theCode);
    }

    @Override
    public boolean isValueSetSupported(ValidationSupportContext theValidationSupportContext, String theValueSetUrl) {
        return IValidationSupport.super.isValueSetSupported(theValidationSupportContext, theValueSetUrl);
    }

    @Nullable
    @Override
    public IBaseResource generateSnapshot(ValidationSupportContext theValidationSupportContext, IBaseResource theInput, String theUrl, String theWebUrl, String theProfileName) {
        return IValidationSupport.super.generateSnapshot(theValidationSupportContext, theInput, theUrl, theWebUrl, theProfileName);
    }

    @Override
    public FhirContext getFhirContext() {
       return this.fhirContext;
    }

    @Override
    public void invalidateCaches() {
        IValidationSupport.super.invalidateCaches();
    }

    @Nullable
    @Override
    public TranslateConceptResults translateConcept(TranslateCodeRequest theRequest) {
        return IValidationSupport.super.translateConcept(theRequest);
    }
}
