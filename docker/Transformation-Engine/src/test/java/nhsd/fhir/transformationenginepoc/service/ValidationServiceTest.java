package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    private String staticR4Json, staticR3Json, staticR3Xml, staticR4Xml, staticR4Json_wrong;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        try {
            staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);

            staticR3Xml = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationRequest.xml"), StandardCharsets.UTF_8);
            staticR4Xml = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationRequest.xml"), StandardCharsets.UTF_8);

            staticR4Json_wrong = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationRequest_Wrong.json"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void happyPathWith_R3_Json() {

        ValidationResult validationResult = validationService.validateSchema("3.0", staticR3Json);
        assertTrue(validationResult.isSuccessful());
    }

    @Test
    void happyPathWith_R4_Json() {

        ValidationResult validationResult = validationService.validateSchema("4.0", staticR4Json);
        assertTrue(validationResult.isSuccessful());
    }

    @Test
    void happyPathWith_R3_xml() {

        ValidationResult validationResult = validationService.validateSchema("3.0", staticR3Xml);
        assertTrue(validationResult.isSuccessful());
    }

    @Test
    void happyPathWith_R4_xml() {

        ValidationResult validationResult = validationService.validateSchema("4.0", staticR4Xml);
        assertTrue(validationResult.isSuccessful());
    }

    @Test
    void failPathWith_R4_json() {

        ValidationResult validationResult = validationService.validateSchema("4.0", staticR4Json_wrong);
        assertFalse(validationResult.isSuccessful());
    }


}
