package net.nhsd.fhir.converter.transformer

import net.nhsd.fhir.converter.ConverterAdvisor
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40
import org.hl7.fhir.dstu3.model.AllergyIntolerance
import org.hl7.fhir.dstu3.model.Extension
import org.hl7.fhir.dstu3.model.StringType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TransformerComponentTest {
    lateinit var converter: VersionConvertor_30_40
//    lateinit var advisor: ConverterAdvisor

    @BeforeEach
    internal fun setUp() {
        converter = VersionConvertor_30_40(ConverterAdvisor())
    }

    @Test
    internal fun `it should`() {
        val allergy = AllergyIntolerance()
        val extension = Extension("www.foo.com", StringType("foo"))
        allergy.addExtension(extension)

        val c = converter.convertResource(allergy)


    }
}
