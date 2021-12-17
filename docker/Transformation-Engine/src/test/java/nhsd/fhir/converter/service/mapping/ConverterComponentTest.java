package nhsd.fhir.converter.service.mapping;

import nhsd.fhir.converter.service.ConverterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
public class ConverterComponentTest {
    @Autowired
    private ConverterService converterService;

    private static String stu3JsonMedicationRequest;

    @Test
    void it_should_convert_json_stu3_medication_request_to_r4() throws ParserConfigurationException, IOException, SAXException {
        // When
        String m = converterService.convert(stu3JsonMedicationRequest, MediaType.APPLICATION_JSON);
        System.out.println(m);


    }

    static {
        InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("GPConnect/MedicationRequest_GPConnect.json");
//        InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("STU3_MedRequest.json");
        try {
            stu3JsonMedicationRequest = new String(is.readAllBytes());
        } catch (IOException e) {
            fail("Can't open test resource file");
        }
    }
}
