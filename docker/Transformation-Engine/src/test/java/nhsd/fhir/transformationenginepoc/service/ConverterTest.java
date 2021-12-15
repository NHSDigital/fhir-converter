package nhsd.fhir.transformationenginepoc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import nhsd.fhir.transformationenginepoc.service.converter.Converter;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConverterTest {
    static private String patientGPC;
    private TestConverter converterService;

    @BeforeEach
    void setUp() {
        VersionConvertor_30_40 converter = new VersionConvertor_30_40(new BaseAdvisor_30_40());
        converterService = new TestConverter(converter);
    }

    @Test
    void test_convert_3_to_4_without_advisor() throws Exception {
        // Given
        String result = converterService.transform(FhirVersionEnum.DSTU3, FhirVersionEnum.R4, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, patientGPC);

        // When
        org.hl7.fhir.r4.model.Patient r4Patient = parseR4Patient(result);

        // Then
        Extension profileExtension1 = r4Patient.getExtensionByUrl("https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-RegistrationDetails-1");
        assertThat(profileExtension1).isNotNull();

        Extension profileExtension2 = r4Patient.getExtensionByUrl("https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-NHSCommunication-1");
        assertThat(profileExtension2).isNotNull();

        Extension idExtension = r4Patient.getIdentifier().get(0).getExtensionByUrl("https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-NHSNumberVerificationStatus-1");
        assertThat(idExtension).isNotNull();
    }

    private org.hl7.fhir.r4.model.Patient parseR4Patient(String stu3Patient) {
        FhirContext r4Context = FhirContext.forR4();
        IParser parser = r4Context.newJsonParser();

        return parser.parseResource(org.hl7.fhir.r4.model.Patient.class, stu3Patient);
    }

    static {
        InputStream is = ConverterTest.class.getClassLoader().getResourceAsStream("GPConnect/Patient_GPConnect.json");
        try {
            patientGPC = new String(is.readAllBytes());
        } catch (IOException e) {
            fail("Can't open test resource file");
        }
    }
}

class TestConverter extends Converter {

    private final VersionConvertor_30_40 converter;

    public TestConverter(VersionConvertor_30_40 converter) {
        this.converter = converter;
    }

    @Override
    public String transform(FhirVersionEnum inVersion, FhirVersionEnum outVersion, MediaType inMime, MediaType outMime, String resourceString) throws Exception {
        FhirContext inContext = getSuitableContext(inVersion);
        IParser inParser = getSuitableParser(inContext, inMime);

        switch (inVersion) {
            case DSTU3:
                Patient res = inParser.parseResource(Patient.class, resourceString);
                Resource converted = converter.convertResource(res);

                return encode(converted, outVersion, outMime);
            default:
                throw new IllegalStateException("Only STU3 is accepted as input");
        }
    }
}
