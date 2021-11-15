package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.context.SimpleWorkerContext;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.utils.StructureMapUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTransformerTest {
//    private final StructureMapUtilities mapUtilities = new StructureMapUtilities(new SimpleWorkerContext());
    private StructureMapUtilities mapUtilities;
    private String stu3Json;
    private String stu3Fml;

    private final FhirContext r3Context = FhirContext.forDstu3();
    private final FhirContext r4Context = FhirContext.forR4();

    @BeforeEach
    void setUp() throws IOException {
        IWorkerContext worker = SimpleWorkerContext.fromPack(new File("src/test/resources/FML/R3toR4/DomainResource.map").getAbsolutePath());
//        IWorkerContext worker = new SimpleWorkerContext();
//        mapUtilities = new StructureMapUtilities(new SimpleWorkerContext());
        mapUtilities = new StructureMapUtilities(worker);
        stu3Json = FileUtils.readFileToString(new File("src/test/resources/STU3_MedRequest.json"), StandardCharsets.UTF_8);
        stu3Fml = FileUtils.readFileToString(new File("src/test/resources/FML/R3toR4-MedicationRequest.fml"), StandardCharsets.UTF_8);

    }

    @Test
    void test_foo() {
        StructureMap map = mapUtilities.parse(stu3Fml, "src");
        MedicationRequest source = createSource(stu3Json);
        org.hl7.fhir.r4.model.MedicationRequest target = new org.hl7.fhir.r4.model.MedicationRequest();
//        mapUtilities.getTargetType(map).getType();
        mapUtilities.transform("Hello", source, map, target);
        assertThat(true).isTrue();
    }

    private org.hl7.fhir.r4.model.MedicationRequest createSource(String source) {
        IParser r4Parser = r4Context.newJsonParser();
        return (org.hl7.fhir.r4.model.MedicationRequest) r4Parser.parseResource(source);
    }
}
