package nhsd.fhir.transformationenginepoc.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import nhsd.fhir.transformationenginepoc.service.ConversionService;
import nhsd.fhir.transformationenginepoc.service.ValidationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ConversionControllerTest {

    @InjectMocks
    private ConversionController conversionController;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ValidationService validationService;

    private String staticR4Json, staticR3Json;


    @BeforeEach
    void setUp() {
        initMocks(this);
        try {
            staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callConverterToConvert_R3_to_R4_json_json() throws Exception {
        //given
        when(conversionService.convertFhirSchema(anyString(), anyString(), anyObject(), anyObject(), anyString())).thenReturn(staticR4Json);

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=3.0", "application/fhir+json; fhirVersion=4.0", false, staticR3Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR4Json);
    }

    @Test
    public void callConverterToConvert_R4_to_R3_json_json() throws Exception {
        //given
        when(conversionService.convertFhirSchema(anyString(), anyString(), anyObject(), anyObject(), anyString())).thenReturn(staticR3Json);

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=4.0", "application/fhir+json; fhirVersion=3.0", false, staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR3Json);
    }

    @Test
    public void callConverterToConvert_Wrong_Headers() {
        //given
        //init mocks

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("", "application/fhir+json; fhirVersion=3.0", false, staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity.getBody(), "Invalid syntax for this request was provided. java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1");
    }

    @Test
    public void callConverterToConvert_R4_json_With_Validation() {
        //given
        FhirContext ctx = FhirContext.forR4();
        List<SingleValidationMessage> theMessages = List.of(new SingleValidationMessage());
        ValidationResult validationResult = new ValidationResult(ctx, theMessages);
        when(validationService.validateSchema(anyString(), anyString())).thenReturn(validationResult);

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=4.0", "application/fhir+json; fhirVersion=3.0", true, staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
