package nhsd.fhir.transformationenginepoc.service.transformers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class BundleTransformer extends Transformer {

    private static final Logger LOG = Logger.getLogger(BundleTransformer.class.getName());

    @Override
    public String transform(FhirVersionEnum inVersion, FhirVersionEnum outVersion, MediaType inMime, MediaType outMime, String resourceString) throws Exception {
        String returnedValue = "";

        // Set up contexts
        FhirContext inContext = getSuitableContext(inVersion);
        FhirContext outContext = getSuitableContext(outVersion);

        // Instantiate parsers
        IParser inParser = getSuitableParser(inContext, inMime);
        IParser outParser = getSuitableParser(outContext, outMime);

        Object resource; // We'll first parse the object into this.

        switch(inVersion) {
            case DSTU3:
                resource = (org.hl7.fhir.dstu3.model.Bundle) inParser.parseResource(org.hl7.fhir.dstu3.model.Bundle.class, resourceString);
/**
 * This section is specific to handling Bundles with ReferralRequest resources in, which were migrated to ServiceRequest between STU3 and R4.
 *
 * Sadly we're currently binning them EVEN IF we're outputting STU3, that needs resolving really.
 */
                org.hl7.fhir.dstu3.model.Bundle STU3Bundle = (org.hl7.fhir.dstu3.model.Bundle) resource;
                List<org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent> entries = STU3Bundle.getEntry();
                List<org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent> newEntries = new ArrayList<org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent>();
                Iterator<org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent> iterator = entries.iterator();
                while(iterator.hasNext()) {
                    org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent entry = iterator.next();

                    ResourceType r = entry.getResource().getResourceType();

                    if(r.equals(ResourceType.ReferralRequest) == false) {
                        newEntries.add(entry);
                        LOG.info("Adding entry of type: " + entry.getResource().getResourceType());
                    } else {
                        LOG.info("Skipping entry of type: " + entry.getResource().getResourceType());
                    }
                }
                STU3Bundle.setEntry(newEntries);
                resource = STU3Bundle;

/**
 * End of that special sauce bit.
 */
                break;



            case R4:
                resource = (Bundle) inParser.parseResource(Bundle.class, resourceString); //TODO: I guess we should add equivalent here to catch ServiceRequests?
                break;

            default:
                resource = "";
        }

        // Here we have the resource in an object, convert as necessary...
        // STU3 to R4
        if(inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.R4){
            resource = (Bundle) VersionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.Bundle) resource, true);
        }

        switch(outVersion) {
            case DSTU3:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.Bundle) resource);
                break;
            case R4:
                returnedValue = outParser.encodeResourceToString((Bundle) resource);
        }
        return returnedValue;
    }
}
