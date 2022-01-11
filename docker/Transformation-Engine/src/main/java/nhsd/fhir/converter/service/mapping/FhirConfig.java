package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirConfig {
    @Bean
    public FhirContext stu3FhirContext() {
        return FhirContext.forDstu3();
    }

    @Bean
    public FhirContext r4FhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public IParser stu3XmlParser(FhirContext stu3FhirContext) {
        return stu3FhirContext.newXmlParser();
    }

    @Bean
    public IParser stu3JsonParser(FhirContext stu3FhirContext) {
        return stu3FhirContext.newJsonParser();
    }

    @Bean
    public IParser r4XmlParser(FhirContext r4FhirContext) {
        return r4FhirContext.newXmlParser();
    }

    @Bean
    public IParser r4JsonParser(FhirContext r4FhirContext) {
        return r4FhirContext.newJsonParser();
    }

    @Bean
    public VersionConvertor_30_40 Converter30To40() {
        return new VersionConvertor_30_40(new BaseAdvisor_30_40());
    }
}
