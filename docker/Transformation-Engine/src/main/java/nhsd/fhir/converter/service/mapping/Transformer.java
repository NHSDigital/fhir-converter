package nhsd.fhir.converter.service.mapping;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

@Component
public class Transformer {
    IBaseResource transform(IBaseResource resource) {
        org.hl7.fhir.r4.model.Resource source = (org.hl7.fhir.r4.model.Resource) resource;
        org.hl7.fhir.r4.model.Resource target = source.copy();

        return target;
    }
}
