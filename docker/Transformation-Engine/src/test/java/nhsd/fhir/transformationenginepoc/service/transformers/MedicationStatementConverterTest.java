package nhsd.fhir.transformationenginepoc.service.transformers;

import ca.uhn.fhir.context.FhirVersionEnum;
import nhsd.fhir.transformationenginepoc.service.converter.MedicationStatementConverter;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

class MedicationStatementConverterTest {

    private MedicationStatementConverter medicationStatementConverter;

    private String medicationStatement_staticR4Json, medicationStatement_staticR3Json, medicationStatement_staticR3Xml, medicationStatement_staticR4Xml;

    @BeforeEach
    void setUp() throws IOException {
        medicationStatementConverter = new MedicationStatementConverter();
        medicationStatement_staticR4Json = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationStatement.json"), StandardCharsets.UTF_8);
        medicationStatement_staticR3Json = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationStatement.json"), StandardCharsets.UTF_8);
        medicationStatement_staticR3Xml = FileUtils.readFileToString(new File("src/test/resources/R3_MedicationStatement.xml"), StandardCharsets.UTF_8);
        medicationStatement_staticR4Xml = FileUtils.readFileToString(new File("src/test/resources/R4_MedicationStatement.xml"), StandardCharsets.UTF_8);
    }

    @Test
    public void convert_MedicationStatement_from_STU3_to_R4_Json_to_Json() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationStatement_staticR3Json);

        //then
        final JSONObject r3Json = new JSONObject(transform);
        final JSONObject r4JSon = new JSONObject(medicationStatement_staticR4Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r3Json.toString(), r4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_to_STU3_Json_to_Json() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.R4, FhirVersionEnum.DSTU3, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, medicationStatement_staticR4Json);

        //then
        final JSONObject r4Json = new JSONObject(transform);
        final JSONObject r3JSon = new JSONObject(medicationStatement_staticR3Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r4Json.toString(), r3JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_STU3_Xml_to_R4_Json() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, medicationStatement_staticR3Xml);

        //then
        final JSONObject r4Json = new JSONObject(transform);
        final JSONObject staticR4JSon = new JSONObject(medicationStatement_staticR4Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r4Json.toString(), staticR4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_Xml_to_STU3_Json() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.R4, FhirVersionEnum.DSTU3, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, medicationStatement_staticR4Xml);

        //then
        final JSONObject r4Json = new JSONObject(transform);
        final JSONObject staticR4JSon = new JSONObject(medicationStatement_staticR3Json);

        assertNotNull(transform);
        //it needs toString to ignore json spaces.
        assertEquals(r4Json.toString(), staticR4JSon.toString());
    }

    @Test
    public void convert_MedicationStatement_from_R4_to_STU3_Json_to_Xml() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.R4, FhirVersionEnum.DSTU3, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationStatement_staticR4Json);

        //then
        assertNotNull(transform);

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(transform)));

        String modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationStatement");
    }

    @Test
    public void convert_MedicationStatement_from_STU3_to_R4_Json_to_Xml() throws Exception {
        //when
        String transform = medicationStatementConverter.convert(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, medicationStatement_staticR3Json);

        //then
        assertNotNull(transform);

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(transform)));

        String modelName = doc.getFirstChild().getNodeName();

        assertEquals(modelName, "MedicationStatement");
    }
}
