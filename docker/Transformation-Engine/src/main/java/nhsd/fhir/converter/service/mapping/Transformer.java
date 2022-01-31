package nhsd.fhir.converter.service.mapping;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.context.SimpleWorkerContext;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.utils.StructureMapUtilities;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class Transformer {
    static private String medicationRequestStu3ToR4Fml;
    private final StructureMap structureMap;
    private final StructureMapUtilities structureMapUtilities;

    public Transformer() throws IOException {
        IWorkerContext worker = new SimpleWorkerContext();
        structureMapUtilities = new StructureMapUtilities(worker);
        structureMap = structureMapUtilities.parse(medicationRequestStu3ToR4Fml, "src");
    }

    IBaseResource transform(IBaseResource resource) {
        org.hl7.fhir.r4.model.Resource source = (org.hl7.fhir.r4.model.Resource) resource;
        org.hl7.fhir.r4.model.Resource target = source.copy();

        structureMapUtilities.transform("source", source, structureMap, target);

        return target;
    }

    static {
//        InputStream is = Transformer.class.getClassLoader().getResourceAsStream("fml/MedicationRequest/STU3_to_R4.fml");
        InputStream is = Transformer.class.getClassLoader().getResourceAsStream("fml/MedicationRequest/extension.fml");
        try {
            medicationRequestStu3ToR4Fml = new String(is.readAllBytes());
        } catch (IOException e) {
            System.err.println("Can't load fml mapping file");
        }

    }
}
