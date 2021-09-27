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
import org.springframework.http.MediaType;


public abstract class Transformer {
    abstract public String transform(FhirVersionEnum inVersion, FhirVersionEnum outVersion, MediaType inMime, MediaType outMime, String resourceString);


    FhirContext getSuitableContext(final FhirVersionEnum selectedVersion) {
        switch (selectedVersion) {
            case DSTU3:
                return FhirContext.forDstu3();
            case R4:
                return FhirContext.forR4();
            default:
                throw new IllegalStateException("Unexpected FHIR version: " + selectedVersion);
        }
    }


    IParser getSuitableParser(final FhirContext ctx, final MediaType type) {
        return type.getSubtype().equals("xml") ? ctx.newXmlParser() : ctx.newJsonParser();
    }
}
