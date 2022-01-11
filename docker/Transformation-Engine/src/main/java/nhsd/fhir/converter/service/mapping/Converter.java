package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

@Component
public class Converter {
    private final VersionConvertor_30_40 converter;

    public Converter(VersionConvertor_30_40 converter) {
        this.converter = converter;
    }

    public IBaseResource convert(IBaseResource resource, FhirVersionEnum version) {
        if (FhirVersionEnum.R4 == version) {
            return converter.convertResource((org.hl7.fhir.r4.model.Resource) resource);
        } else {
            return converter.convertResource((Resource) resource);
        }
    }
}
