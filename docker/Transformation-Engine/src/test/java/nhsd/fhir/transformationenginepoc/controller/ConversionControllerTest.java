package nhsd.fhir.transformationenginepoc.controller;

import nhsd.fhir.transformationenginepoc.service.ConversionService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionControllerTest {

    private ConversionController conversionController;
    private ConversionService conversionService;
    private String staticR4Json, staticR3Json, staticR4JsonWrongSchema, staticR3_MedicationStatement_Worng_Schema;


    @BeforeEach
    void setUp() throws IOException {
        conversionService = new ConversionService();
        conversionController = new ConversionController(conversionService);
        staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
        staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);
        staticR4JsonWrongSchema = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest_Invalid_schema.json"), StandardCharsets.UTF_8);
        staticR3_MedicationStatement_Worng_Schema = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationStatement_Wrong_schema.xml"), StandardCharsets.UTF_8);

    }

    @Test
    public void callConverterToConvert_R3_to_R4_json_json() {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=3.0", "application/fhir+json; fhirVersion=4.0", staticR3Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR4Json);
    }

    @Test
    public void callConverterToConvert_R4_to_R3_json_json() throws Exception {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=4.0", "application/fhir+json; fhirVersion=3.0", staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), staticR3Json);
    }

    @Test
    public void callConverterToConvert_Wrong_Headers() {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("", "application/fhir+json; fhirVersion=3.0", staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity.getBody(), "Invalid syntax for this request was provided. java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1");
    }


    @Test
    public void callConverterToConvert_Wrong_Json_Payload() {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+json; fhirVersion=3.0", "application/fhir+json; fhirVersion=3.0", staticR4JsonWrongSchema);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity.getBody(), "Invalid syntax for this request was provided. Please check your JSON payload");

    }


    @Test
    public void callConverterToConvert_Wrong_XML_Payload() {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+xml; fhirVersion=3.0", "application/fhir+json; fhirVersion=3.0", staticR3_MedicationStatement_Worng_Schema);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity.getBody(), "Invalid syntax for this request was provided. Please check your XML payload");

    }

    @Test
    public void callConverterToConvert_Sending_A_different_version_ON_header() {
        //when
        ResponseEntity<?> responseEntity = conversionController.convert("application/fhir+xml; fhirVersion=3.0", "application/fhir+json; fhirVersion=3.0", staticR4Json);

        //then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}
