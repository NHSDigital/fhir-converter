package nhsd.fhir.transformationenginepoc.service.transformers;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

class MedicationRequestTransformerTest {

    @InjectMocks
    private MedicationRequestTransformer medicationRequestTransformer;

    private String medicationRequest_staticR4Json, medicationRequest_staticR3Json;
    @BeforeEach
    void setUp() {
        initMocks(this);
        try {
            medicationRequest_staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            medicationRequest_staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convert_MedicationStatement_from_STU3_to_R4_Json_to_Json() throws JSONException {
        //given
        //init mocks
        //when
        String transform = medicationRequestTransformer.transform(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationRequest_staticR3Json);

        //then
        final JSONObject r3Json = new JSONObject(transform);
        final JSONObject r4JSon = new JSONObject(medicationRequest_staticR4Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_to_STU3_Json_to_Json() throws JSONException {
        //given
        //init mocks
        //when
        String transform = medicationRequestTransformer.transform(FhirVersionEnum.R4, FhirVersionEnum.DSTU3, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationRequest_staticR4Json);

        //then
        final JSONObject r4Json = new JSONObject(transform);
        final JSONObject r3JSon = new JSONObject(medicationRequest_staticR3Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r4Json.toString(), r3JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_to_STU3_Json_to_Xml() throws ParserConfigurationException, IOException, SAXException {
        //given
        //init mocks
        //when
        String transform = medicationRequestTransformer.transform(FhirVersionEnum.R4, FhirVersionEnum.DSTU3, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationRequest_staticR4Json);

        //then
        assertNotNull(transform);
        String modelName = null;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(transform)));

        modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationRequest");
    }

    @Test
    public void convert_MedicationStatement_from_STU3_to_R4_Json_to_Xml() throws ParserConfigurationException, IOException, SAXException {
        //given
        //init mocks
        //when
        String transform = medicationRequestTransformer.transform(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationRequest_staticR3Json);

        //then
        assertNotNull(transform);
        String modelName = null;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(transform)));

        modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationRequest");
    }
}
