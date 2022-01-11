package nhsd.fhir.converter.service.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static ca.uhn.fhir.context.FhirVersionEnum.DSTU3;
import static ca.uhn.fhir.context.FhirVersionEnum.R4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {
    private ConverterService converterService;

    @Mock
    private Converter converter;
    @Mock
    private Transformer transformer;
    @Mock
    private FhirParser fhirParser;

    private static final String STU3_JSON_RES = "{\"resourceType\":  \"MedicationRequest\"}";
    private static final String STU3_XML_RES = "<MedicationRequest></MedicationRequest>";
    private static final String R4_JSON_RES = "{\"resourceType\":  \"MedicationRequest\"}";
    private static final String R4_XML_RES = "<MedicationRequest></MedicationRequest>";

    private static final MediaType JSON = MediaType.APPLICATION_JSON;
    private static final MediaType XML = MediaType.APPLICATION_XML;

    private static final org.hl7.fhir.dstu3.model.MedicationRequest A_STU3_RES = new org.hl7.fhir.dstu3.model.MedicationRequest();
    private static final org.hl7.fhir.r4.model.MedicationRequest A_R4_RES = new org.hl7.fhir.r4.model.MedicationRequest();

    private static final org.hl7.fhir.dstu3.model.MedicationRequest A_TRANSFORMED_STU3_RES = new org.hl7.fhir.dstu3.model.MedicationRequest();
    private static final org.hl7.fhir.r4.model.MedicationRequest A_TRANSFORMED_R4_RES = new org.hl7.fhir.r4.model.MedicationRequest();

    private static final org.hl7.fhir.dstu3.model.MedicationRequest A_CONVERTED_STU3_RES = new org.hl7.fhir.dstu3.model.MedicationRequest();
    private static final org.hl7.fhir.r4.model.MedicationRequest A_CONVERTED_R4_RES = new org.hl7.fhir.r4.model.MedicationRequest();

    private static final Class<org.hl7.fhir.dstu3.model.MedicationRequest> STU3_CLASS = org.hl7.fhir.dstu3.model.MedicationRequest.class;
    private static final Class<org.hl7.fhir.r4.model.MedicationRequest> R4_CLASS = org.hl7.fhir.r4.model.MedicationRequest.class;

    @BeforeEach
    void setUp() {
        converterService = new ConverterService(converter, transformer, fhirParser);
    }

    @Test
    void it_should_convert_stu3_json_resource() throws ParserConfigurationException, IOException, SAXException {
        // Given
        doReturn(A_STU3_RES)
                .when(fhirParser)
                .parse(STU3_JSON_RES, STU3_CLASS, JSON);

        doReturn(A_CONVERTED_R4_RES)
                .when(converter)
                .convert(A_STU3_RES, DSTU3);

        doReturn(A_TRANSFORMED_R4_RES)
                .when(transformer)
                .transform(A_CONVERTED_R4_RES);

        doReturn(R4_JSON_RES)
                .when(fhirParser)
                .encode(A_TRANSFORMED_R4_RES, JSON, R4);

        // When
        String actualConverted = converterService.convert(STU3_JSON_RES, JSON, DSTU3);

        // Then
        assertThat(actualConverted).isEqualTo(R4_JSON_RES);
    }

    @Test
    void it_should_convert_stu3_xml_resource() throws ParserConfigurationException, IOException, SAXException {
        // Given
        doReturn(A_STU3_RES)
                .when(fhirParser)
                .parse(STU3_XML_RES, STU3_CLASS, XML);

        doReturn(A_CONVERTED_R4_RES)
                .when(converter)
                .convert(A_STU3_RES, DSTU3);

        doReturn(A_TRANSFORMED_R4_RES)
                .when(transformer)
                .transform(A_CONVERTED_R4_RES);

        doReturn(R4_XML_RES)
                .when(fhirParser)
                .encode(A_TRANSFORMED_R4_RES, XML, R4);

        // When
        String actualConverted = converterService.convert(STU3_XML_RES, XML, DSTU3);

        // Then
        assertThat(actualConverted).isEqualTo(R4_XML_RES);
    }

    @Test
    void it_should_convert_r4_json_resource() throws ParserConfigurationException, IOException, SAXException {
        // Given
        doReturn(A_R4_RES)
                .when(fhirParser)
                .parse(R4_JSON_RES, R4_CLASS, JSON);

        doReturn(A_CONVERTED_STU3_RES)
                .when(converter)
                .convert(A_R4_RES, R4);

        doReturn(A_TRANSFORMED_STU3_RES)
                .when(transformer)
                .transform(A_CONVERTED_STU3_RES);

        doReturn(STU3_JSON_RES)
                .when(fhirParser)
                .encode(A_TRANSFORMED_STU3_RES, JSON, DSTU3);

        // When
        String actualConverted = converterService.convert(R4_JSON_RES, JSON, R4);

        // Then
        assertThat(actualConverted).isEqualTo(STU3_JSON_RES);
    }

    @Test
    void it_should_convert_r4_xml_resource() throws ParserConfigurationException, IOException, SAXException {
        // Given
        doReturn(A_R4_RES)
                .when(fhirParser)
                .parse(R4_XML_RES, R4_CLASS, XML);

        doReturn(A_CONVERTED_STU3_RES)
                .when(converter)
                .convert(A_R4_RES, R4);

        doReturn(A_TRANSFORMED_STU3_RES)
                .when(transformer)
                .transform(A_CONVERTED_STU3_RES);

        doReturn(STU3_XML_RES)
                .when(fhirParser)
                .encode(A_TRANSFORMED_STU3_RES, XML, DSTU3);

        // When
        String actualConverted = converterService.convert(R4_XML_RES, XML, R4);

        // Then
        assertThat(actualConverted).isEqualTo(STU3_XML_RES);
    }
}
