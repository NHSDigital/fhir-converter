package nhsd.fhir.converter.controller;

import nhsd.fhir.converter.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConversionControllerUnitTest {
    private static final String STU3_JSON = "application/fhir+json; fhirVersion=3.0";
//    private static final String STU3_XML = "application/fhir+xml; fhirVersion=3.0";
    private static final String R4_JSON = "application/fhir+json; fhirVersion=4.0";
//    private static final String R4_XML = "application/fhir+xml; fhirVersion=4.0";
//    private static final String PAYLOAD_XML = "";
    private static final String PAYLOAD_JSON = "{}";

    private ConversionController conversionController;

    @Mock
    private ConversionService conversionService;


    @BeforeEach
    void setUp() {
        conversionController = new ConversionController(conversionService);
    }

    @Test
    void it_should_accept_x_include_resources_header_and_call_convert_service() throws Exception {
        // Given
        String includeResource = "foo|bar";
        String currentVersion = "3.0";
        String targetVersion = "4.0";
        when(conversionService.convertFhirSchema(anyString(), anyString(), any(), any(), anyString(), any()))
                .thenReturn("response message");

        // When
        ResponseEntity<?> response = conversionController.convert(STU3_JSON, R4_JSON, includeResource, PAYLOAD_JSON);

        // Then
        verify(conversionService).convertFhirSchema(currentVersion, targetVersion, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, PAYLOAD_JSON, Arrays.asList("foo", "bar"));
        assertThat(response.getBody()).isEqualTo("response message");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void it_should_accept_null_x_include_resources_header() throws Exception {
        // Given
        String includeResource = null;
        String currentVersion = "3.0";
        String targetVersion = "4.0";

        // When
        conversionController.convert(STU3_JSON, R4_JSON, includeResource, PAYLOAD_JSON);

        // Then
        verify(conversionService).convertFhirSchema(currentVersion, targetVersion, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, PAYLOAD_JSON, new ArrayList<>());
    }
}
