package nhsd.fhir.transformationenginepoc.service.transformers;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


class BundleTransformerTest {
    private BundleTransformer bundleTransformer;

    private String bundleStu3Json;
    private String bundleR4Xml;
    private List<String> includedResources;

    @BeforeEach
    void setUp() throws IOException {
        includedResources = new ArrayList<>();
        includedResources.add("Patient");
        bundleTransformer = new BundleTransformer(includedResources);

        bundleStu3Json = FileUtils.readFileToString(new File("src/test/resources/Bundle/STU3_Bundle.json"), StandardCharsets.UTF_8);
        bundleR4Xml = FileUtils.readFileToString(new File("src/test/resources/Bundle/R4_Bundle.xml"), StandardCharsets.UTF_8);
    }

    @Test
    public void test_stu3_json_bundle_to_r4_xml_bundle() throws Exception {
        // When
        String actualR4Xml = bundleTransformer.transform(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, bundleStu3Json);

        // Then
        assertXml(actualR4Xml, bundleR4Xml);
    }

    private static void assertXml(String actual, String expected) throws IOException, SAXException {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLAssert.assertXMLEqual(actual, expected);
    }
}
