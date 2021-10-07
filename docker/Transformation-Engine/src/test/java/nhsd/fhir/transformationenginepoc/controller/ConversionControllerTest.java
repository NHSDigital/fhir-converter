package nhsd.fhir.transformationenginepoc.controller;

import nhsd.fhir.transformationenginepoc.service.ConversionService;
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
    public void callConverterToConvert_R3_to_R4_json_json() {
        //given
        when(conversionService.convertFhirSchema(anyString(), anyString(), anyObject(), anyObject(), anyString())).thenReturn(staticR4Json);

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=3.0", "application/fhir+json; fhirVersion=4.0", staticR3Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR4Json);
    }

    @Test
    public void callConverterToConvert_R4_to_R3_json_json() {
        //given
        when(conversionService.convertFhirSchema(anyString(), anyString(), anyObject(), anyObject(), anyString())).thenReturn(staticR3Json);

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=4.0", "application/fhir+json; fhirVersion=3.0", staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR3Json);
    }

    @Test
    public void callConverterToConvert_Wrong_Headers() {
        //given
        //init mocks

        //when
        ResponseEntity<?> responseEntity = conversionController.convert("", "application/fhir+json; fhirVersion=3.0", staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity.getBody(), "Invalid syntax for this request was provided. java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1");
    }


}
