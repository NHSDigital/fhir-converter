package nhsd.fhir.transformationenginepoc.service;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
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

import static junit.framework.TestCase.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
class ConversionServiceTest {

    @InjectMocks
    private ConversionService fileConversionService;

    private String medicationRequest_staticR4Json, medicationRequest_staticR3Json,
        medicationStatement_staticR4Json, medicationStatement_staticR3Json, medicationStatement_staticR3Xml;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        try {
            medicationRequest_staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            medicationRequest_staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);

            medicationStatement_staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationStatement.json"), StandardCharsets.UTF_8);
            medicationStatement_staticR3Json = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationStatement.json"), StandardCharsets.UTF_8);
            medicationStatement_staticR3Xml = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationStatement.xml"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convert_MedicationRequest_from_STU3_to_R4_Json_to_Json() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationRequest_staticR3Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(medicationRequest_staticR4Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationRequest_from_R4_to_STU3_Json_to_Json() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("4.0", "3.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationRequest_staticR4Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(medicationRequest_staticR3Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationRequest_from_STU3_to_R4_Json_to_xml() throws ParserConfigurationException, IOException, SAXException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationRequest_staticR3Json);

        //then
        assertNotNull(convert);
        String modelName = null;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(convert)));

        modelName = doc.getFirstChild().getNodeName();
        assertEquals(modelName, "MedicationRequest");
    }

    @Test
    public void convert_MedicationRequest_from_R4_to_R4_json_to_xml() throws ParserConfigurationException, IOException, SAXException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("4.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationRequest_staticR4Json);

        //then
        assertNotNull(convert);
        String modelName = null;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(convert)));

        modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationRequest");
    }

    @Test
    public void convert_MedicationStatement_from_R3_to_R4_json_to_json() throws JSONException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationStatement_staticR3Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(medicationStatement_staticR4Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_to_R3_json_to_json() throws JSONException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("4.0", "3.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationStatement_staticR4Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(medicationStatement_staticR3Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R3_to_R3_xml_to_json() throws JSONException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "3.0", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, medicationStatement_staticR3Xml);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(medicationStatement_staticR3Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R3_to_R3_xml_to_xml() throws ParserConfigurationException, IOException, SAXException {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "3.0", MediaType.APPLICATION_XML, MediaType.APPLICATION_XML, medicationStatement_staticR3Xml);

        //then
        assertNotNull(convert);
        String modelName = null;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(convert)));

        modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationStatement");
    }


}
