/*
 * Copyright (C) 2021 NHS Digital.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nhsd.fhir.transformationenginepoc.service.transformers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import nhsd.fhir.transformationenginepoc.model.PayloadTypeEnum;
import org.apache.logging.log4j.util.Strings;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.r4.model.MedicationRequest;

/**
 * Class to transform a MedicationRequest into another version and / or
 * serialisation format.
 * <p>
 * Handles inputs:
 * STU3
 * R4
 * <p>
 * Handles outputs:
 * STU3 (only if STU3 was supplied)
 * R4
 * <p>
 * Handles formats:
 * XML
 * JSON
 * <p>
 * NB: MedicationRequest did not exist in DSTU2 hence no conversion is available.
 *
 * @author tim.coates@nhs.net
 */
public class MedicationRequestTransformer extends Transformer {

    /**
     * Function that does the actual transformation magic.
     *
     * @param inVersion      The version we've got a resource in.
     * @param outVersion     The version they'd like it in.
     * @param inMime         The incoming mime type (XML or JSON)
     * @param outMime        The type they've asked for (XML or JSON)
     * @param resourceString The resource to be converted, as a String
     * @return Returns a string, the resource converted as requested and
     * serialised into the requested format.
     */
    @Override
    public String transform(final FhirVersionEnum inVersion, final FhirVersionEnum outVersion, final PayloadTypeEnum inMime, final PayloadTypeEnum outMime, final String resourceString) {

        String returnedValue = Strings.EMPTY;

        final BaseAdvisor_30_40 baseAdvisor_30_40 = new BaseAdvisor_30_40();
        final VersionConvertor_30_40 versionConvertor_30_40 = new VersionConvertor_30_40(baseAdvisor_30_40);

        // Set up contexts
        final FhirContext inContext = getSuitableContext(inVersion);
        final FhirContext outContext = getSuitableContext(outVersion);

        // Instantiate parsers

        final IParser inParser = getSuitableParser(inContext, inMime);
        inParser.setParserErrorHandler(new StrictErrorHandler());
        final IParser outParser = getSuitableParser(outContext, outMime);
        outParser.setParserErrorHandler(new StrictErrorHandler());

        Object resource = null; // We'll first parse the object into this.

        org.hl7.fhir.r4.model.Resource r4Resource = null;
        org.hl7.fhir.dstu3.model.MedicationRequest r3MedicationRequest = null;
        switch (inVersion) {
            case DSTU3:
                //resource = (org.hl7.fhir.dstu3.model.MedicationRequest) inParser.parseResource(org.hl7.fhir.dstu3.model.MedicationRequest.class, resourceString);
                r3MedicationRequest = (org.hl7.fhir.dstu3.model.MedicationRequest) inParser.parseResource(resourceString);
                break;

            case R4:
                resource = (MedicationRequest) inParser.parseResource(MedicationRequest.class, resourceString);
                break;

            default:
                resource = "";
        }

        // Here we have the resource in an object, convert as necessary...
        // STU3 to R4
        if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.R4) {
            r4Resource = (MedicationRequest) versionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.MedicationRequest) r3MedicationRequest);
        } else if (inVersion == FhirVersionEnum.R4 && outVersion == FhirVersionEnum.DSTU3) {
            resource = (org.hl7.fhir.dstu3.model.MedicationRequest) versionConvertor_30_40.convertResource((MedicationRequest) resource);
        } else if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.DSTU3) {
            return outParser.encodeResourceToString(r3MedicationRequest);
        } else if (inVersion == FhirVersionEnum.R4 && outVersion == FhirVersionEnum.R4) {
            return outParser.encodeResourceToString((MedicationRequest) resource);
        }

        switch (outVersion) {
            case DSTU3:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.MedicationRequest) resource);
                break;
            case R4:
                final MedicationRequest r4ModelConverted = (MedicationRequest) r4Resource;
                returnedValue = outParser.encodeResourceToString(r4ModelConverted);
        }
        return returnedValue;
    }

}
