package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ConverterTest {
    @Mock
    private VersionConvertor_30_40 fhirConverter;

    private Converter converter;

    private static final IBaseResource STU3_RESOURCE = new MedicationRequest();
    private static final IBaseResource R4_RESOURCE = new org.hl7.fhir.r4.model.MedicationRequest();

    @Before
    public void setUp() throws Exception {
        converter = new Converter(fhirConverter);
    }

    @Test
    public void it_should_convert_stu3_to_r4() {
        // Given
        when(fhirConverter.convertResource(eq((org.hl7.fhir.dstu3.model.Resource) STU3_RESOURCE)))
                .thenReturn((Resource) R4_RESOURCE);

        // When
        IBaseResource converted = converter.convert(STU3_RESOURCE, FhirVersionEnum.DSTU3);

        // Then
        assertThat((org.hl7.fhir.r4.model.MedicationRequest) converted).isNotNull();
    }

    @Test
    public void it_should_convert_r4_to_stu3() {
        // Given
        when(fhirConverter.convertResource(eq((org.hl7.fhir.r4.model.Resource) R4_RESOURCE)))
                .thenReturn((org.hl7.fhir.dstu3.model.Resource) STU3_RESOURCE);

        // When
        IBaseResource converted = converter.convert(R4_RESOURCE, FhirVersionEnum.R4);

        // Then
        assertThat((org.hl7.fhir.dstu3.model.MedicationRequest) converted).isNotNull();
    }
}
