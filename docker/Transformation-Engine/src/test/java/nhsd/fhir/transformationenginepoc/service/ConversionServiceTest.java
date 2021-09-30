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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    private String staticR4Json, staticR3Json, staticR3Xml, staticR4Xml;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        try {
            staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            staticR3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);

            staticR3Xml = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationRequest.xml"), StandardCharsets.UTF_8);
            staticR4Xml = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationRequest.xml"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convert_STU3_to_R4_Json_to_Json() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, staticR3Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(staticR4Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_R4_to_STU3_Json_to_Json() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("4.0", "3.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, staticR4Json);

        //then
        final JSONObject r3Json = new JSONObject(convert);
        final JSONObject r4JSon = new JSONObject(staticR3Json);

        assertNotNull(convert);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_STU3_to_R4_Json_to_xml() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("3.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, staticR3Json);

        //then
        assertNotNull(convert);
        assertTrue(convert.startsWith("<"));
        String modelName = null;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            final Document doc = builder.parse(new InputSource(new StringReader(convert)));
            modelName = doc.getFirstChild().getNodeName();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        assertEquals(modelName, "MedicationRequest");
    }

    @Test
    public void convert_R4_to_R4_json_to_xml() throws Exception {
        //given
        //init mocks
        //when
        String convert = fileConversionService.convertFhirSchema("4.0", "4.0", MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, staticR4Json);

        //then
        assertNotNull(convert);
        assertTrue(convert.startsWith("<"));
        String modelName = null;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            final Document doc = builder.parse(new InputSource(new StringReader(convert)));
            modelName = doc.getFirstChild().getNodeName();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        assertEquals(modelName, "MedicationRequest");
    }

}
