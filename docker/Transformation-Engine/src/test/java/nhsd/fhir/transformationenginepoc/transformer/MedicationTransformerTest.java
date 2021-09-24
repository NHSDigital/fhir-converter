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

package nhsd.fhir.transformationenginepoc.transformer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import nhsd.fhir.transformationenginepoc.model.PayloadTypeEnum;
import nhsd.fhir.transformationenginepoc.service.transformers.MedicationTransformer;
import nhsd.fhir.transformationenginepoc.transformer.factories.DSTU2ResourceFactory;
import nhsd.fhir.transformationenginepoc.transformer.factories.R4ResourceFactory;
import nhsd.fhir.transformationenginepoc.transformer.factories.STU3ResourceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author timco
 */
public class MedicationTransformerTest {

    static FhirContext DSTU2Ctx;             // DSTU2 HAPI context
    static IParser XMLDSTU2Parser;           // XML Parser for DSTU2
    static IParser JSONDSTU2Parser;          // JSON Parser for DSTU2
    static String DSTU2JSON;                 // STU2 Resource serialised to JSON
    static String DSTU2XML;                  // STU2 Resource serialised to XML

    static FhirContext STU3Ctx;             // STU3 HAPI context
    static IParser XMLSTU3Parser;           // XML Parser for STU3
    static IParser JSONSTU3Parser;          // JSON Parser for STU3
    static String STU3JSON;                 // STU3 Resource serialised to JSON
    static String STU3XML;                  // STU3 Resource serialised to XML

    static FhirContext R4Ctx;             // STU3 HAPI context
    static IParser XMLR4Parser;           // XML Parser for STU3
    static IParser JSONR4Parser;          // JSON Parser for STU3
    static String R4JSON;                 // STU3 Resource serialised to JSON
    static String R4XML;                  // STU3 Resource serialised to XML


    @BeforeAll
    public static void setUpClass() {
        // Create the factory, and a DSTU2 resource
        final DSTU2ResourceFactory DSTU2Factory = new DSTU2ResourceFactory();
        final org.hl7.fhir.dstu2.model.Medication DSTU2Medication = DSTU2Factory.GetMedication();
        // Get parsers and get both String versions of the DSTU2 resource
        DSTU2Ctx = FhirContext.forDstu2Hl7Org();
        XMLDSTU2Parser = DSTU2Ctx.newXmlParser();
        JSONDSTU2Parser = DSTU2Ctx.newJsonParser();
        DSTU2JSON = JSONDSTU2Parser.encodeResourceToString(DSTU2Medication);
        DSTU2XML = XMLDSTU2Parser.encodeResourceToString(DSTU2Medication);

        // Create the factory, and a STU3 resource
        final STU3ResourceFactory STU3Factory = new STU3ResourceFactory();
        final org.hl7.fhir.dstu3.model.Medication STU3Medication = STU3Factory.GetMedication();
        // Get parsers and get both String versions of the STU3 resource
        STU3Ctx = FhirContext.forDstu3();
        XMLSTU3Parser = STU3Ctx.newXmlParser();
        JSONSTU3Parser = STU3Ctx.newJsonParser();
        STU3JSON = JSONSTU3Parser.encodeResourceToString(STU3Medication);
        STU3XML = XMLSTU3Parser.encodeResourceToString(STU3Medication);

        // Create the factory, and an R4 resource
        final R4ResourceFactory R4Factory = new R4ResourceFactory();
        final org.hl7.fhir.r4.model.Medication R4Medication = R4Factory.GetMedication();
        // Get parsers and get both String versions of the STU3 resource
        R4Ctx = FhirContext.forR4();
        XMLR4Parser = R4Ctx.newXmlParser();
        JSONR4Parser = R4Ctx.newJsonParser();
        R4JSON = JSONR4Parser.encodeResourceToString(R4Medication);
        R4XML = XMLR4Parser.encodeResourceToString(R4Medication);

    }

    /**
     * DSTU2 to DSTU2
     */
    @Test
    public void TestTransform_DSTU2_JSON_to_DSTU2_JSON() {
        System.out.println("Transform DSTU2 JSON to DSTU2 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU2;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = DSTU2JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU2;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = DSTU2JSON;

        final MedicationTransformer instance = new MedicationTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    /**
     * DSTU2 to DSTU3
     */
    @Test
    public void TestTransform_DSTU2_JSON_to_STU3_JSON() {
        System.out.println("Transform DSTU2 JSON to STU3 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU2;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = DSTU2JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = STU3JSON;

        final MedicationTransformer instance = new MedicationTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        fail("Form isn't correctly converted from DSTU2 to STU3");
        assertEquals(expResult, result);
    }

    /**
     * DSTU2 to R4
     */
    @Test
    public void TestTransform_DSTU2_JSON_to_R4_JSON() {
        System.out.println("Transform DSTU2 JSON to R4 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU2;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = DSTU2JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = R4JSON;

        final MedicationTransformer instance = new MedicationTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        fail("Form isn't correctly converted from DSTU2 to R4");
        assertEquals(expResult, result);
    }

    /**
     * STU3 to R4
     */
    @Test
    public void TestTransform_STU3_JSON_to_R4_JSON() {
        System.out.println("Transform STU3 JSON to R4 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = STU3JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = R4JSON;

        final MedicationTransformer instance = new MedicationTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        fail("Form isn't correctly converted from DSTU2 to R4");
        assertEquals(expResult, result);
    }
}
