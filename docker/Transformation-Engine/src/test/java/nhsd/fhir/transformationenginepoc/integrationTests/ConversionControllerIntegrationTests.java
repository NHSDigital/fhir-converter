package nhsd.fhir.transformationenginepoc.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nhsd.fhir.transformationenginepoc.controller.ConversionController;
import nhsd.fhir.transformationenginepoc.testConfig.ITConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ITConfig.class})
public class ConversionControllerIntegrationTests {

    @Autowired
    private ConversionController conversionController;

    private MockMvc mvc;
    private String r4Json;
    private String r3Json;
    private ObjectMapper objectMapper;

    @Before
    public void init() {
        mvc = standaloneSetup(conversionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            r4Json = FileUtils.readFileToString(new File("src/test/resources/R4Medicationrequestexample.json"), StandardCharsets.UTF_8);
            r3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEndointWith() throws Exception {
        final ResultActions resultActions = mvc.perform(post("/convert")
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/fhir+json; fhirVersion=4.0")
                .header("Accept-type", "application/fhir=json; fhirVersion=3.0")
                .content(r4Json));
        resultActions.andExpect(status().isOk());
    }
}
