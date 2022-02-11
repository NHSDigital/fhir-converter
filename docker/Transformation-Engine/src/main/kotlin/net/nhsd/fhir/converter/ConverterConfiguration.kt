package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConverterConfiguration {
    @Bean
    fun r3FhirContext(): FhirContext = FhirContext.forDstu3()

    @Bean
    fun r4FhirContext(): FhirContext = FhirContext.forR4()

    @Bean
    fun r3XmlParser(r3FhirContext: FhirContext): IParser = r3FhirContext.newXmlParser()

    @Bean
    fun r3JsonParser(r3FhirContext: FhirContext): IParser = r3FhirContext.newJsonParser()

    @Bean
    fun r4XmlParser(r4FhirContext: FhirContext): IParser = r4FhirContext.newXmlParser()

    @Bean
    fun r4JsonParser(r4FhirContext: FhirContext): IParser = r4FhirContext.newJsonParser()

    @Bean
    fun converter30To40(): VersionConvertor_30_40 = VersionConvertor_30_40(BaseAdvisor_30_40())
}
