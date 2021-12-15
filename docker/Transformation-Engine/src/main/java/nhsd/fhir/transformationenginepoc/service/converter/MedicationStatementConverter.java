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
package nhsd.fhir.transformationenginepoc.service.converter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.apache.logging.log4j.util.Strings;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.springframework.http.MediaType;


public class MedicationStatementConverter extends Converter {

    @Override
    public String transform(final FhirVersionEnum inVersion, final FhirVersionEnum outVersion, final MediaType inMime, final MediaType outMime, final String resourceString) {

        //base converter
        final BaseAdvisor_30_40 baseAdvisor_30_40 = new BaseAdvisor_30_40();
        final VersionConvertor_30_40 versionConvertor_30_40 = new VersionConvertor_30_40(baseAdvisor_30_40);
        String converstionResult = Strings.EMPTY;


        // Set up contexts
        final FhirContext inContext = getSuitableContext(inVersion);
        final FhirContext outContext = getSuitableContext(outVersion);

        // Instantiate parsers
        final IParser inParser = getSuitableParser(inContext, inMime);
        inParser.setParserErrorHandler(new StrictErrorHandler());
        final IParser outParser = getSuitableParser(outContext, outMime);
        outParser.setParserErrorHandler(new StrictErrorHandler());

        // Initialize resource with the right version
        Object resource = null;

        //handling with multiple formats and same version.
        if (inVersion == FhirVersionEnum.DSTU3 && outVersion == FhirVersionEnum.DSTU3) {
            resource = (org.hl7.fhir.dstu3.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.dstu3.model.MedicationStatement.class, resourceString);
            return outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.MedicationStatement) resource);
        } else if (inVersion == FhirVersionEnum.R4 && outVersion == FhirVersionEnum.R4) {
            resource = (org.hl7.fhir.r4.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.r4.model.MedicationStatement.class, resourceString);
            return outParser.encodeResourceToString((org.hl7.fhir.r4.model.MedicationStatement) resource);
        }

        //create resource from the incoming payload
        if (inVersion.equals(FhirVersionEnum.DSTU3)) {
            resource = (org.hl7.fhir.dstu3.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.dstu3.model.MedicationStatement.class, resourceString);
            resource = versionConvertor_30_40.convertResource((org.hl7.fhir.dstu3.model.MedicationStatement) resource);
        } else {
            resource = (org.hl7.fhir.r4.model.MedicationStatement) inParser.parseResource(org.hl7.fhir.r4.model.MedicationStatement.class, resourceString);
            resource = versionConvertor_30_40.convertResource((org.hl7.fhir.r4.model.MedicationStatement) resource);
        }

        //conversation between versions
        if (outVersion.equals(FhirVersionEnum.DSTU3)) {
            converstionResult = outParser.encodeResourceToString((org.hl7.fhir.dstu3.model.MedicationStatement) resource);
        } else {
            converstionResult = outParser.encodeResourceToString((org.hl7.fhir.r4.model.MedicationStatement) resource);
        }


        return converstionResult;

    }
}
