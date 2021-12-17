package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;


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

    @BeforeEach
    void setUp() {
        fhirParser = new FhirParser(stu3JsonParser, stu3XmlParser, r4JsonParser, r4XmlParser);
    }

    @Test
    void it_should_parse_stu3_json_resource() {
        // When
        MedicationRequest resource = fhirParser.parse("", MedicationRequest.class, MediaType.APPLICATION_JSON);
    }
}
