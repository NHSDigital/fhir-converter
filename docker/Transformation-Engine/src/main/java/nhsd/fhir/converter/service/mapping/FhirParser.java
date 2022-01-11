package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class FhirParser {
    private final IParser stu3JsonParser;
    private final IParser r4JsonParser;
    private final IParser stu3XmlParser;
    private final IParser r4XmlParser;

    public FhirParser(IParser stu3JsonParser, IParser r4JsonParser, IParser stu3XmlParser, IParser r4XmlParser) {
        this.stu3JsonParser = stu3JsonParser;
        this.r4JsonParser = r4JsonParser;
        this.stu3XmlParser = stu3XmlParser;
        this.r4XmlParser = r4XmlParser;
    }

    public <T extends IBaseResource> T parse(String resource, Class<T> resourceType, MediaType mediaType) {
        boolean isR4 = resourceType.getName().contains("r4");

        if (MediaType.APPLICATION_JSON == mediaType) {
            if (isR4) {
                return r4JsonParser.parseResource(resourceType, resource);
            } else {
                return stu3JsonParser.parseResource(resourceType, resource);
            }
        } else if (MediaType.APPLICATION_XML == mediaType) {
            if (isR4) {
                return r4XmlParser.parseResource(resourceType, resource);
            } else {
                return stu3XmlParser.parseResource(resourceType, resource);
            }
        }
        throw new IllegalStateException("Invalid Content-Type");
    }

    public String encode(IBaseResource resource, MediaType mediaType, FhirVersionEnum fhirVersion) {
        boolean isR4 = FhirVersionEnum.R4 == fhirVersion;

        if (MediaType.APPLICATION_JSON == mediaType) {
            if (isR4) {
                return r4JsonParser.encodeResourceToString(resource);
            } else {
                return stu3JsonParser.encodeResourceToString(resource);
            }
        } else if (MediaType.APPLICATION_XML == mediaType) {
            if (isR4) {
                return r4XmlParser.encodeResourceToString(resource);
            } else {
                return stu3XmlParser.encodeResourceToString(resource);
            }
        }
        throw new IllegalStateException("Invalid Accept header");
    }
}
