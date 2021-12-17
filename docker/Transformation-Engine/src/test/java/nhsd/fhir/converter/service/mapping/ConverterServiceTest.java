package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import nhsd.fhir.converter.service.ConverterTest;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {
    private ConverterService converterService;

    @Mock
    private Converter converter;
    @Mock
    private Transformer transformer;
    @Mock
    private FhirParser fhirParser;
    @Mock
    private FhirEncoder fhirEncoder;
    @Mock
    private ResourceTypeFactory resourceTypeFactory;

    private static String stu3JsonResource = "some stu3 json resource";
    private static String r4JsonResource = "some r4 json resource";
    private static String stu3XmlResource = "some stu3 xml resource";

    private static final MediaType jsonIn = MediaType.APPLICATION_JSON;
    private static final MediaType xmlIn = MediaType.APPLICATION_XML;


    @BeforeEach
    void setUp() {
        converterService = new ConverterService(converter, transformer, fhirParser, fhirEncoder, resourceTypeFactory);
    }

    @Test
    void it_should_convert_stu3_json_resource() throws ParserConfigurationException, IOException, SAXException {
        // Given
        String expectedConvertedResource = "a converted resource";
        org.hl7.fhir.dstu3.model.MedicationRequest expStu3Resource = new MedicationRequest();
        org.hl7.fhir.r4.model.MedicationRequest expR4Converted = new org.hl7.fhir.r4.model.MedicationRequest();
        org.hl7.fhir.r4.model.MedicationRequest expR4Transformed = new org.hl7.fhir.r4.model.MedicationRequest();

        Class<? extends IBaseResource> inferredType = MedicationRequest.class;

        doReturn(inferredType)
                .when(resourceTypeFactory)
                .inferResourceType(eq(stu3JsonResource), eq(MediaType.APPLICATION_JSON), eq(FhirVersionEnum.DSTU3));

        doReturn(expStu3Resource)
                .when(fhirParser)
                .parse(eq(stu3JsonResource), eq(inferredType), eq(MediaType.APPLICATION_JSON));

        doReturn(expR4Converted)
                .when(converter)
                .convert(eq(expStu3Resource), eq(inferredType));

        doReturn(expR4Transformed)
                .when(transformer)
                .transform(eq(expR4Converted));

        when(fhirEncoder.encode(eq(expR4Transformed), eq(MediaType.APPLICATION_JSON), eq(FhirVersionEnum.R4)))
                .thenReturn(expectedConvertedResource);

        // When
        String actualConverted = converterService.convert(stu3JsonResource, jsonIn);

        // Then
        assertThat(actualConverted).isEqualTo(expectedConvertedResource);
    }

    /*

        @Test
        void it_should_convert_r4_json_resource() {
            // Given
            org.hl7.fhir.dstu3.model.MedicationRequest expStu3Resource = new org.hl7.fhir.dstu3.model.MedicationRequest();
            when(r4JsonParser.parseResource(any(), eq(r4JsonResource))).thenReturn(expStu3Resource);

            // When
            org.hl7.fhir.dstu3.model.MedicationRequest stu3Resource = converterService.convert(r4JsonResource, jsonIn);

            // Then
            assertThat(stu3Resource).isEqualTo(expStu3Resource);
        }

        @Test
        void it_should_convert_stu3_xml_resource() {
            // Given
            MedicationRequest expR4Resource = new MedicationRequest();
            when(stu3XmlParser.parseResource(any(), eq(stu3XmlResource))).thenReturn(expR4Resource);

            // When
            MedicationRequest r4Resource = converterService.convert(stu3XmlResource, xmlIn);

            // Then
            assertThat(r4Resource).isEqualTo(expR4Resource);
        }

    */
    static {
        InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("GPConnect/MedicationRequest_GPConnect.json");
        try {
            stu3JsonResource = new String(is.readAllBytes());
        } catch (IOException e) {
            fail("Can't open test resource file");
        }
    }
}
