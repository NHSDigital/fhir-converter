package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.parser.IParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FhirParserTest {
    @Mock
    private IParser stu3JsonParser;
    @Mock
    private IParser stu3XmlParser;
    @Mock
    private IParser r4JsonParser;
    @Mock
    private IParser r4XmlParser;

    private FhirParser fhirParser;

    private static final MediaType JSON = MediaType.APPLICATION_JSON;
    private static final MediaType XML = MediaType.APPLICATION_XML;

    private static final String STU3_JSON_RES = "a stu3 json resource";
    private static final String STU3_XML_RES = "a stu3 xml resource";
    private static final String R4_JSON_RES = "a r4 json resource";
    private static final String R4_XML_RES = "a r4 xml resource";

    private static final org.hl7.fhir.dstu3.model.MedicationRequest STU3_RES = new org.hl7.fhir.dstu3.model.MedicationRequest();
    private static final org.hl7.fhir.r4.model.MedicationRequest R4_RES = new org.hl7.fhir.r4.model.MedicationRequest();
    private static final Class<org.hl7.fhir.dstu3.model.MedicationRequest> STU3_CLASS = org.hl7.fhir.dstu3.model.MedicationRequest.class;
    private static final Class<org.hl7.fhir.r4.model.MedicationRequest> R4_CLASS = org.hl7.fhir.r4.model.MedicationRequest.class;

    @BeforeEach
    void setUp() {
        fhirParser = new FhirParser(stu3JsonParser, r4JsonParser, stu3XmlParser, r4XmlParser);
    }

    @Test
    void it_should_parse_stu3_json_resource() {
        // Given
        when(stu3JsonParser.parseResource(STU3_CLASS, STU3_JSON_RES))
                .thenReturn(STU3_RES);

        // When
        org.hl7.fhir.dstu3.model.MedicationRequest resource =
                fhirParser.parse(STU3_JSON_RES, STU3_CLASS, JSON);

        // Then
        assertThat(resource).isEqualTo(STU3_RES);
    }

    @Test
    void it_should_encode_stu3_json_resource() {
        // Given
        when(stu3JsonParser.encodeResourceToString(STU3_RES))
                .thenReturn(STU3_JSON_RES);

        // When
        String resource = fhirParser.encode(STU3_RES, STU3_CLASS, JSON);

        // Then
        assertThat(resource).isEqualTo(STU3_JSON_RES);
    }

    @Test
    void it_should_parse_stu3_xml_resource() {
        // Given
        when(stu3XmlParser.parseResource(STU3_CLASS, STU3_XML_RES))
                .thenReturn(STU3_RES);

        // When
        org.hl7.fhir.dstu3.model.MedicationRequest resource =
                fhirParser.parse(STU3_XML_RES, STU3_CLASS, XML);

        // Then
        assertThat(resource).isEqualTo(STU3_RES);
    }

    @Test
    void it_should_encode_stu3_xml_resource() {
        // Given
        when(stu3XmlParser.encodeResourceToString(STU3_RES))
                .thenReturn(STU3_XML_RES);

        // When
        String resource = fhirParser.encode(STU3_RES, STU3_CLASS, XML);

        // Then
        assertThat(resource).isEqualTo(STU3_XML_RES);
    }

    @Test
    void it_should_parse_r4_json_resource() {
        // Given
        when(r4JsonParser.parseResource(R4_CLASS, R4_JSON_RES))
                .thenReturn(R4_RES);

        // When
        org.hl7.fhir.r4.model.MedicationRequest resource =
                fhirParser.parse(R4_JSON_RES, R4_CLASS, JSON);

        // Then
        assertThat(resource).isEqualTo(R4_RES);
    }

    @Test
    void it_should_encode_r4_json_resource() {
        // Given
        when(r4JsonParser.encodeResourceToString(R4_RES))
                .thenReturn(R4_JSON_RES);

        // When
        String resource = fhirParser.encode(R4_RES, R4_CLASS, JSON);

        // Then
        assertThat(resource).isEqualTo(R4_JSON_RES);
    }

    @Test
    void it_should_parse_r4_xml_resource() {
        // Given
        when(r4XmlParser.parseResource(R4_CLASS, R4_XML_RES))
                .thenReturn(R4_RES);

        // When
        org.hl7.fhir.r4.model.MedicationRequest resource =
                fhirParser.parse(R4_XML_RES, R4_CLASS, XML);

        // Then
        assertThat(resource).isEqualTo(R4_RES);
    }

    @Test
    void it_should_encode_r4_xml_resource() {
        // Given
        when(r4XmlParser.encodeResourceToString(R4_RES))
                .thenReturn(R4_XML_RES);

        // When
        String resource = fhirParser.encode(R4_RES, R4_CLASS, XML);

        // Then
        assertThat(resource).isEqualTo(R4_XML_RES);
    }
}
