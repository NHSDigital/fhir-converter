package nhsd.fhir.converter.service.mapping;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static ca.uhn.fhir.context.FhirVersionEnum.DSTU3;
import static ca.uhn.fhir.context.FhirVersionEnum.R4;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceTypeFactoryTest {
    private static final String MEDICATION_REQUEST_JSON = "{\"resourceType\":  \"MedicationRequest\"}";
    private static final String MEDICATION_REQUEST_XML = "<MedicationRequest></MedicationRequest>";

    private static final String MEDICATION_STATEMENT_JSON = "{\"resourceType\":  \"MedicationStatement\"}";
    private static final String MEDICATION_STATEMENT_XML = "<MedicationStatement></MedicationStatement>";

    private static final MediaType JSON = MediaType.APPLICATION_JSON;
    private static final MediaType XML = MediaType.APPLICATION_XML;

    @Test
    void it_should_return_stu_medication_request_given_json_input() throws ParserConfigurationException, IOException, SAXException {
        // Given
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_REQUEST_JSON, JSON, DSTU3);

        assertThat(resource).isEqualTo(org.hl7.fhir.dstu3.model.MedicationRequest.class);
    }

    @Test
    void it_should_return_stu3_medication_request_given_xml_input() throws ParserConfigurationException, IOException, SAXException {
        // When
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_REQUEST_XML, XML, DSTU3);

        // Then
        assertThat(resource).isEqualTo(org.hl7.fhir.dstu3.model.MedicationRequest.class);
    }

    @Test
    void it_should_return_r4_medication_request_given_json_input() throws ParserConfigurationException, IOException, SAXException {
        // Given
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_REQUEST_JSON, JSON, R4);

        assertThat(resource).isEqualTo(org.hl7.fhir.r4.model.MedicationRequest.class);
    }

    @Test
    void it_should_return_r4_medication_request_given_xml_input() throws ParserConfigurationException, IOException, SAXException {
        // When
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_REQUEST_XML, XML, R4);

        // Then
        assertThat(resource).isEqualTo(org.hl7.fhir.r4.model.MedicationRequest.class);
    }

    @Test
    void it_should_return_stu_medication_statement_given_json_input() throws ParserConfigurationException, IOException, SAXException {
        // Given
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_STATEMENT_JSON, JSON, DSTU3);

        assertThat(resource).isEqualTo(org.hl7.fhir.dstu3.model.MedicationStatement.class);
    }

    @Test
    void it_should_return_stu3_medication_statement_given_xml_input() throws ParserConfigurationException, IOException, SAXException {
        // When
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_STATEMENT_XML, XML, DSTU3);

        // Then
        assertThat(resource).isEqualTo(org.hl7.fhir.dstu3.model.MedicationStatement.class);
    }

    @Test
    void it_should_return_r4_medication_statement_given_json_input() throws ParserConfigurationException, IOException, SAXException {
        // Given
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_STATEMENT_JSON, JSON, R4);

        assertThat(resource).isEqualTo(org.hl7.fhir.r4.model.MedicationStatement.class);
    }

    @Test
    void it_should_return_r4_medication_statement_given_xml_input() throws ParserConfigurationException, IOException, SAXException {
        // When
        Class<?> resource = ResourceTypeFactory.createResourceType(MEDICATION_STATEMENT_XML, XML, R4);

        // Then
        assertThat(resource).isEqualTo(org.hl7.fhir.r4.model.MedicationStatement.class);
    }
}
