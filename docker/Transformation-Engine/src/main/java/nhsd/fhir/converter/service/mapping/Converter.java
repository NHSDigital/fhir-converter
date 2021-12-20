package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40;
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

@Configuration
class ConverterConfiguration {
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
}

@Component
class ResourceTypeFactory {
    public Class<? extends IBaseResource> createResourceType(String fhirResource, MediaType mediaType, FhirVersionEnum fhirVersion) throws ParserConfigurationException, IOException, SAXException {
        String resourceType = getResourceType(fhirResource, mediaType);
        switch (resourceType) {
            case "MedicationRequest":
                return FhirVersionEnum.R4.equals(fhirVersion) ?
                        MedicationRequest.class
                        : org.hl7.fhir.dstu3.model.MedicationRequest.class;
            case "MedicationStatement":
                return FhirVersionEnum.R4.equals(fhirVersion) ?
                        MedicationStatement.class
                        : org.hl7.fhir.dstu3.model.MedicationStatement.class;
        }
        throw new IllegalStateException("Resource not supported");
    }

    private String getResourceType(final String fhirSchema, MediaType mediaType) throws ParserConfigurationException, IOException, SAXException {
        if ("xml".equals(mediaType.getSubtype())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(fhirSchema)));

            return doc.getFirstChild().getNodeName();
        } else {
            JSONObject json = new JSONObject(fhirSchema);

            return json.getString("resourceType");
        }
    }
}

@Component
class Transformer {
    <T extends IBaseResource> T transform(IBaseResource resource) {
        return null;
    }
}

@Component
public class Converter {
    private static final VersionConvertor_30_40 CONVERTER = new VersionConvertor_30_40(new BaseAdvisor_30_40());

    public <T extends IBaseResource> IBaseResource convert(IBaseResource resource, Class<T> resourceType) {
        boolean isR4 = "r4".contains(resourceType.getName());

        if (isR4) {
            return CONVERTER.convertResource((org.hl7.fhir.r4.model.Resource) resource);
        } else {
            return CONVERTER.convertResource((Resource) resource);
        }
    }
}

@Component
class FhirParser {
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
        }
//        } else if (MediaType.APPLICATION_XML == mediaType) {
//            return stu3XmlParser.parseResource(MedicationRequest.class, resource);
//        }
        throw new IllegalStateException("Invalid Content-Type");
    }
}

@Component
class FhirEncoder {
    private final IParser stu3JsonParser;
    private final IParser r4JsonParser;
    private final IParser stu3XmlParser;
    private final IParser r4XmlParser;

    public FhirEncoder(IParser stu3JsonParser, IParser r4JsonParser, IParser stu3XmlParser, IParser r4XmlParser) {
        this.stu3JsonParser = stu3JsonParser;
        this.r4JsonParser = r4JsonParser;
        this.stu3XmlParser = stu3XmlParser;
        this.r4XmlParser = r4XmlParser;
    }

    public <T extends IBaseResource> String encode(T resource, MediaType outMime, FhirVersionEnum outVersion) {
        return "";
    }
}

@Service
class ConverterService {
    private final Converter converter;
    private final Transformer transformer;
    private final FhirParser fhirParser;
    private final FhirEncoder fhirEncoder;
    private final ResourceTypeFactory resourceTypeFactory;

    public ConverterService(Converter converter, Transformer transformer, FhirParser fhirParser, FhirEncoder fhirEncoder, ResourceTypeFactory resourceTypeFactory) {
        this.converter = converter;
        this.transformer = transformer;
        this.fhirParser = fhirParser;
        this.fhirEncoder = fhirEncoder;
        this.resourceTypeFactory = resourceTypeFactory;
    }

    public String convert(String resource, MediaType mediaType) throws ParserConfigurationException, IOException, SAXException {
        Class<? extends IBaseResource> resourceType = resourceTypeFactory.createResourceType(resource, mediaType, FhirVersionEnum.DSTU3);

        IBaseResource fhirResource = fhirParser.parse(resource, resourceType, mediaType);
        IBaseResource converted = converter.convert(fhirResource, resourceType);
        IBaseResource transformed = transformer.transform(converted);

        String convertedStr = fhirEncoder.encode(transformed, MediaType.APPLICATION_JSON, FhirVersionEnum.R4);

        return convertedStr;
    }
}
