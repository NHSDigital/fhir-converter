package nhsd.fhir.transformationenginepoc.service.converter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

public class BundleConverter extends Converter {

    private static final VersionConvertor_30_40 CONVERTER = new VersionConvertor_30_40(new BaseAdvisor_30_40());

    private final List<String> includeResources;

    public BundleConverter(List<String> desiredResources) {
        this.includeResources = desiredResources;
    }

    @Override
    public String transform(FhirVersionEnum inVersion, FhirVersionEnum outVersion, MediaType inMime, MediaType outMime, String resourceString) throws Exception {
        FhirContext inContext = getSuitableContext(inVersion);
        IParser inParser = getSuitableParser(inContext, inMime);

        switch (inVersion) {
            case DSTU3:
                org.hl7.fhir.dstu3.model.Bundle stu3Resource = inParser.parseResource(org.hl7.fhir.dstu3.model.Bundle.class, resourceString);
                // This section is specific to handling Bundles with ReferralRequest resources in, which were migrated to ServiceRequest between STU3 and R4.
                // Sadly we're currently binning them EVEN IF we're outputting STU3, that needs resolving really.
                List<BundleEntryComponent> filteredResources = stu3Resource.getEntry().stream()
                        .filter(e -> includeResources.contains(e.getResource().getResourceType().name())
                        ).collect(Collectors.toList());

                stu3Resource.setEntry(filteredResources);

                return convert(stu3Resource, inVersion, outVersion, outMime);

            case R4:
                //TODO: I guess we should add equivalent here to catch ServiceRequests?
                org.hl7.fhir.r4.model.Resource r4Resource = inParser.parseResource(org.hl7.fhir.r4.model.Bundle.class, resourceString);

                return convert(r4Resource, inVersion, outVersion, outMime);

            default:
                return "";
        }
    }

    private <T extends IBaseResource> String convert(T resource, FhirVersionEnum inVersion, FhirVersionEnum outVersion, MediaType outMime) {
        if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.R4) {
            org.hl7.fhir.r4.model.Resource converted = CONVERTER.convertResource((org.hl7.fhir.dstu3.model.Bundle) resource);
            return encode(converted, outVersion, outMime);

        } else if (inVersion == FhirVersionEnum.R4 && outVersion == FhirVersionEnum.DSTU3) {
            org.hl7.fhir.dstu3.model.Resource converted = CONVERTER.convertResource((org.hl7.fhir.r4.model.Bundle) resource);
            return encode(converted, outVersion, outMime);

        } else if (inVersion == FhirVersionEnum.R4 && outVersion == FhirVersionEnum.R4) {
            return encode(resource, outVersion, outMime);

        } else if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.DSTU3) {
            return encode(resource, outVersion, outMime);

        } else {
            return "";
        }
    }
}
