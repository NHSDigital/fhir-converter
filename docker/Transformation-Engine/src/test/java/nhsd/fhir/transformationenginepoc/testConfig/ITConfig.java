package nhsd.fhir.transformationenginepoc.testConfig;

import nhsd.fhir.transformationenginepoc.controller.ConversionController;
import nhsd.fhir.transformationenginepoc.service.FileConversionService;
import nhsd.fhir.transformationenginepoc.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ITConfig {

    @Bean
    public ConversionController conversionController() {
        return new ConversionController();
    }

    @Bean
    public FileConversionService fileConversionService() {
        return new FileConversionService();
    }

    @Bean
    public ValidationService validationService() {
        return new ValidationService();
    }
}
