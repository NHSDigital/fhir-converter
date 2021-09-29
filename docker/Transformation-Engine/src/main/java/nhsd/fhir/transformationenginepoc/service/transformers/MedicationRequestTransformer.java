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
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.springframework.http.MediaType;


public class MedicationRequestTransformer extends Transformer {

    @Override
    public String transform(final FhirVersionEnum inVersion, final FhirVersionEnum outVersion, final MediaType inMime, final MediaType outMime, final String resourceString) {

        //base converter
      /*  final BaseAdvisor_30_40 baseAdvisor_30_40 = new BaseAdvisor_30_40();
        final VersionConvertor_30_40 versionConvertor_30_40 = new VersionConvertor_30_40(baseAdvisor_30_40);*/

        // Set up contexts
        final FhirContext inContext = getSuitableContext(inVersion);
        final FhirContext outContext = getSuitableContext(outVersion);

        // Instantiate parsers
        final IParser inParser = getSuitableParser(inContext, inMime);
        inParser.setParserErrorHandler(new StrictErrorHandler());
        final IParser outParser = getSuitableParser(outContext, outMime);
        outParser.setParserErrorHandler(new StrictErrorHandler());

        // Initialize resource with the right version
        Object resource = getSuitableResource(inParser, inVersion, resourceString);

        //create resource from the incoming payload
        if (inVersion.equals(FhirVersionEnum.DSTU3)) {
            resource = VersionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.MedicationRequest) resource, true);
        } else {
            resource = VersionConvertor_30_40.convertResource((org.hl7.fhir.r4.model.MedicationRequest) resource, true);
        }
        //conversation between versions
        if (outVersion.equals(FhirVersionEnum.DSTU3)) {
            return outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.MedicationRequest) resource);
        } else {
            return outParser.encodeResourceToString((org.hl7.fhir.r4.model.MedicationRequest) resource);
        }

    }

    private Object getSuitableResource(final IParser inParser, final FhirVersionEnum inVersion, final String resourceString) {
        switch (inVersion) {
            case DSTU3:
                return inParser.parseResource(org.hl7.fhir.dstu3.model.MedicationRequest.class, resourceString);
            case R4:
                return inParser.parseResource(org.hl7.fhir.r4.model.MedicationRequest.class, resourceString);
            default:
                throw new IllegalStateException("Unexpected error creating the resource for version: " + inVersion);

        }
    }

}