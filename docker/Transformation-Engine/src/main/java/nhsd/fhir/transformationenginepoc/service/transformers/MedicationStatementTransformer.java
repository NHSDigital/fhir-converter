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
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MedicationStatement;


public class MedicationStatementTransformer extends Transformer {

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
        org.hl7.fhir.dstu2.model.CodeableConcept reasonForUse = null;
        switch (inVersion) {
            case DSTU2:
                resource = (org.hl7.fhir.dstu2.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.dstu2.model.MedicationStatement.class, resourceString);
                reasonForUse = (org.hl7.fhir.dstu2.model.CodeableConcept) ((org.hl7.fhir.dstu2.model.MedicationStatement) resource).getReasonForUse();
                break;

            case DSTU3:
                resource = (org.hl7.fhir.dstu3.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.dstu3.model.MedicationStatement.class, resourceString);
                break;

            case R4:
                resource = (MedicationStatement) inParser.parseResource(MedicationStatement.class, resourceString);
                break;

            default:
                resource = "";
        }

        // Here we have the resource in an object
        // DSTU2 to STU3
        if (inVersion == FhirVersionEnum.DSTU2 && outVersion == FhirVersionEnum.DSTU3) {
            final BaseAdvisor_10_30 baseAdvisor_10_30 = new BaseAdvisor_10_30();
            final VersionConvertor_10_30 versionConvertor_10_30 = new VersionConvertor_10_30(baseAdvisor_10_30);

            resource = (org.hl7.fhir.dstu3.model.MedicationStatement) versionConvertor_10_30.convertResource((org.hl7.fhir.dstu2.model.MedicationStatement) resource);
        }

        // DSTU2 to R4
        if (inVersion == FhirVersionEnum.DSTU2 && outVersion == FhirVersionEnum.R4) {
            final BaseAdvisor_10_40 baseAdvisor_10_40 = new BaseAdvisor_10_40();
            final VersionConvertor_10_40 versionConvertor_10_30 = new VersionConvertor_10_40(baseAdvisor_10_40);

            resource = (MedicationStatement) versionConvertor_10_30.convertResource((org.hl7.fhir.dstu2.model.MedicationStatement) resource);
        }

        // STU3 to R4
        if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.R4) {
            final BaseAdvisor_30_40 baseAdvisor_30_40 = new BaseAdvisor_30_40();
            final VersionConvertor_30_40 versionConvertor_30_40 = new VersionConvertor_30_40(baseAdvisor_30_40);

            resource = (MedicationStatement) versionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.MedicationStatement) resource);
        }

        switch (outVersion) { // Because of the different versions, we need to cast differently for STU3 and R4

            case DSTU2:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu2.model.MedicationStatement) resource);
                if (reasonForUse != null) {
                    final String reasonSystem = reasonForUse.getCoding().get(0).getSystem();
                    final String reasonCode = reasonForUse.getCoding().get(0).getCode();
                    final String reasonDisplay = reasonForUse.getCoding().get(0).getDisplay();
                    final org.hl7.fhir.dstu2.model.Coding reasonCoding = new org.hl7.fhir.dstu2.model.Coding().setSystem(reasonSystem).setCode(reasonCode).setDisplay(reasonDisplay);
                    final org.hl7.fhir.dstu2.model.CodeableConcept reasonConcept = new org.hl7.fhir.dstu2.model.CodeableConcept(reasonCoding);
                    ((org.hl7.fhir.dstu2.model.MedicationStatement) resource).setReasonForUse(reasonConcept);
                }
                break;

            case DSTU3:
                returnedValue = outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.MedicationStatement) resource);
                if (reasonForUse != null) {
                    final String reasonSystem = reasonForUse.getCoding().get(0).getSystem();
                    final String reasonCode = reasonForUse.getCoding().get(0).getCode();
                    final String reasonDisplay = reasonForUse.getCoding().get(0).getDisplay();
                    final org.hl7.fhir.dstu3.model.Coding reasonCoding = new org.hl7.fhir.dstu3.model.Coding().setSystem(reasonSystem).setCode(reasonCode).setDisplay(reasonDisplay);
                    final org.hl7.fhir.dstu3.model.CodeableConcept reasonConcept = new org.hl7.fhir.dstu3.model.CodeableConcept(reasonCoding);
                    ((org.hl7.fhir.dstu3.model.MedicationStatement) resource).addReasonCode(reasonConcept);
                }
                break;

            case R4:
                if (reasonForUse != null) {
                    final String reasonSystem = reasonForUse.getCoding().get(0).getSystem();
                    final String reasonCode = reasonForUse.getCoding().get(0).getCode();
                    final String reasonDisplay = reasonForUse.getCoding().get(0).getDisplay();
                    final Coding reasonCoding = new Coding().setSystem(reasonSystem).setCode(reasonCode).setDisplay(reasonDisplay);
                    final CodeableConcept reasonConcept = new CodeableConcept(reasonCoding);
                    ((MedicationStatement) resource).addReasonCode(reasonConcept);
                }
                returnedValue = outParser.encodeResourceToString((MedicationStatement) resource);
        }
        return returnedValue; // We return the resource converted and serialised to the version they asked for.
    }
}
