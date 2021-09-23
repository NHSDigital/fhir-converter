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
import nhsd.fhir.transformationenginepoc.model.PayloadTypeEnum;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_10_30;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_10_40;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv10_30.VersionConvertor_10_30;
import org.hl7.fhir.convertors.conv10_40.VersionConvertor_10_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.r4.model.Medication;

public class MedicationTransformer extends Transformer {

    @Override
    public String transform(final FhirVersionEnum inVersion, final FhirVersionEnum outVersion, final PayloadTypeEnum inMime, final PayloadTypeEnum outMime, final String resourceString) {
        String returnedValue = "";

        // Set up contexts
        final FhirContext inContext = getSuitableContext(inVersion);
        final FhirContext outContext = getSuitableContext(outVersion);

        // Instantiate parsers
        final IParser inParser = getSuitableParser(inContext, inMime);
        final IParser outParser = getSuitableParser(outContext, outMime);

        Object resource;
        switch (inVersion) {
            case DSTU2:
                resource = (org.hl7.fhir.dstu2.model.Medication) inParser.parseResource(org.hl7.fhir.dstu2.model.Medication.class, resourceString);
                break;

            case DSTU3:
                resource = (org.hl7.fhir.dstu3.model.Medication) inParser.parseResource(org.hl7.fhir.dstu3.model.Medication.class, resourceString);
                break;

            case R4:
                resource = (Medication) inParser.parseResource(Medication.class, resourceString);
                break;

            default:
                resource = "";
        }

        // Here we have the resource in an object
        //DSTU2 to STU3 conversion
        if (inVersion == FhirVersionEnum.DSTU2 && outVersion == FhirVersionEnum.DSTU3) {
            final BaseAdvisor_10_30 baseAdvisor_10_30 = new BaseAdvisor_10_30();
            final VersionConvertor_10_30 versionConvertor_10_30 = new VersionConvertor_10_30(baseAdvisor_10_30);

            resource = (org.hl7.fhir.dstu3.model.Medication) versionConvertor_10_30.convertResource((org.hl7.fhir.dstu2.model.Medication) resource);
        }
        //DSTU2 to R4 conversion
        if (inVersion == FhirVersionEnum.DSTU2 && outVersion == FhirVersionEnum.R4) {
            final BaseAdvisor_10_40 baseAdvisor_10_40 = new BaseAdvisor_10_40();
            final VersionConvertor_10_40 versionConvertor_10_40 = new VersionConvertor_10_40(baseAdvisor_10_40);

            resource = (Medication) versionConvertor_10_40.convertResource((org.hl7.fhir.dstu2.model.Medication) resource);
            // As we're going to R4, we add the UK core profile
            ((Medication) resource).getMeta().addProfile("https://fhir.hl7.org.uk/StructureDefinition/UKCore-Medication");
        }
        // STU3 to R4 conversion
        if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.R4) {
            final BaseAdvisor_30_40 baseAdvisor_30_40 = new BaseAdvisor_30_40();
            final VersionConvertor_30_40 versionConvertor_30_40 = new VersionConvertor_30_40(baseAdvisor_30_40);

            resource = (Medication) versionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.Medication) resource);
            // As we're going to R4, we add the UK core profile
            ((Medication) resource).getMeta().addProfile("https://fhir.hl7.org.uk/StructureDefinition/UKCore-Medication");
        }

        switch (outVersion) {
            case DSTU2:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu2.model.Medication) resource);
                break;

            case DSTU3:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.Medication) resource);
                break;

            case R4:
                returnedValue = outParser.encodeResourceToString((Medication) resource);
        }
        return returnedValue;
    }
}
