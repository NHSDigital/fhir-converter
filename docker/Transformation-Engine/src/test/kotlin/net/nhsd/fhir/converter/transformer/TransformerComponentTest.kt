package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirContext
import net.nhsd.fhir.converter.ConverterAdvisor
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TransformerComponentTest {
    lateinit var converter: VersionConvertor_30_40
    val r3Context = FhirContext.forDstu3()
    val r3Parser = r3Context.newJsonParser()
//    lateinit var advisor: ConverterAdvisor
//    val r3RepeatInfo = this::class.java.classLoader.getResource("profiles/MedicationRequestRepeatInformation-Extension-3to4_001-input.json").readText()
//    val r4RepeatInfo = this::class.java.classLoader.getResource("profiles/MedicationRequestRepeatInformation-Extension-3to4_001-output.json").readText()

    @BeforeEach
    internal fun setUp() {
        converter = VersionConvertor_30_40(ConverterAdvisor())
    }

    @Test
    internal fun `it should`() {
/*
        val res = r3Parser.parseResource(MedicationRequest::class.java, r3RepeatInfo)
        val allergy = AllergyIntolerance()
        val extension = Extension("www.foo.com", StringType("foo"))
        allergy.addExtension(extension)

        val c = converter.convertResource(res)
*/


    }
}
