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
import nhsd.fhir.transformationenginepoc.service.transformers.MedicationRequestTransformer;
import nhsd.fhir.transformationenginepoc.transformer.factories.R4ResourceFactory;
import nhsd.fhir.transformationenginepoc.transformer.factories.STU3ResourceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author tim.coates@nhs.net
 */
public class MedicationRequestTransformerTest {

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


    /**
     * Ideally we'll generate one STU3 and one R4 HAPI resource and serialise
     * them to use in tests.
     */
    @BeforeAll
    public static void setUpClass() {

        final STU3ResourceFactory STU3Factory = new STU3ResourceFactory();
        final org.hl7.fhir.dstu3.model.MedicationRequest STU3MedRequest;
        STU3MedRequest = STU3Factory.GetMedicationRequest();

        STU3Ctx = FhirContext.forDstu3();
        XMLSTU3Parser = STU3Ctx.newXmlParser();
        JSONSTU3Parser = STU3Ctx.newJsonParser();
        STU3JSON = JSONSTU3Parser.encodeResourceToString(STU3MedRequest);
        STU3XML = XMLSTU3Parser.encodeResourceToString(STU3MedRequest);

        final R4ResourceFactory R4Factory = new R4ResourceFactory();
        final org.hl7.fhir.r4.model.MedicationRequest R4MedRequest = R4Factory.GetMedicationRequest();
        R4Ctx = FhirContext.forR4();
        XMLR4Parser = R4Ctx.newXmlParser();
        JSONR4Parser = R4Ctx.newJsonParser();
        R4JSON = JSONR4Parser.encodeResourceToString(R4MedRequest);
        R4XML = XMLR4Parser.encodeResourceToString(R4MedRequest);

    }

    /**
     * Test of transform method, of class MedicationRequestTransformer.
     * Lots of combinations of versions and mime types.
     */
    @Test
    public void testTransform_STU3_XML_to_STU3_XML() {
        System.out.println("Transform STU3 XML to STU3 XML (passthrough)");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = STU3XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum outMime = PayloadTypeEnum.XML;
        final String expResult = STU3XML;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_XML_to_STU3_JSON() {
        System.out.println("Transform STU3 XML to STU3 JSON (format only)");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = STU3XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = STU3JSON;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_JSON_to_STU3_XML() {
        System.out.println("Transform STU3 JSON to STU3 XML (reverse format)");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = STU3JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum outMime = PayloadTypeEnum.XML;
        final String expResult = STU3XML;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_JSON_to_STU3_JSON() {
        System.out.println("Transform STU3 JSON to STU3 JSON (passthrough)");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = STU3JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = STU3JSON;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_XML_to_R4_XML() {
        System.out.println("Transform STU3 XML to R4 XML");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = STU3XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.XML;
        final String expResult = R4XML;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_XML_to_R4_JSON() {
        System.out.println("Transform STU3 XML to R4 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = STU3XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = R4JSON;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_JSON_to_R4_XML() {
        System.out.println("Transform STU3 JSON to R4 XML");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = STU3JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.XML;
        final String expResult = R4XML;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_STU3_JSON_to_R4_JSON() {
        System.out.println("Transform STU3 JSON to R4 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.DSTU3;
        final PayloadTypeEnum inMime = PayloadTypeEnum.JSON;
        final String resourceString = STU3JSON;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = R4JSON;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_R4_XML_to_R4_XML() {
        System.out.println("Transform R4 XML to R4 XML (passthrough)");

        final FhirVersionEnum inVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = R4XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.XML;
        final String expResult = R4XML;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform_R4_XML_to_R4_JSON() {
        System.out.println("Transform R4 XML to R4 JSON");

        final FhirVersionEnum inVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum inMime = PayloadTypeEnum.XML;
        final String resourceString = R4XML;

        final FhirVersionEnum outVersion = FhirVersionEnum.R4;
        final PayloadTypeEnum outMime = PayloadTypeEnum.JSON;
        final String expResult = R4JSON;

        final MedicationRequestTransformer instance = new MedicationRequestTransformer();

        final String result = instance.transform(inVersion, outVersion, inMime, outMime, resourceString);
        assertEquals(expResult, result);
    }
}
