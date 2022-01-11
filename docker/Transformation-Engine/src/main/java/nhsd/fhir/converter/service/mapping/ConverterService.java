package nhsd.fhir.converter.service.mapping;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Service
public class ConverterService {

    private final Converter converter;
    private final Transformer transformer;
    private final FhirParser fhirParser;

    public ConverterService(Converter converter, Transformer transformer, FhirParser fhirParser) {
        this.converter = converter;
        this.transformer = transformer;
        this.fhirParser = fhirParser;
    }

    public String convert(String resource, MediaType mediaType, FhirVersionEnum fhirVersion) throws ParserConfigurationException, IOException, SAXException {
        Class<? extends IBaseResource> resourceType = ResourceTypeFactory.createResourceType(resource, mediaType, fhirVersion);

        IBaseResource fhirResource = fhirParser.parse(resource, resourceType, mediaType);
        IBaseResource converted = converter.convert(fhirResource, fhirVersion);
        IBaseResource transformed = transformer.transform(converted);

        FhirVersionEnum outVersion = fhirVersion == FhirVersionEnum.R4 ? FhirVersionEnum.DSTU3 : FhirVersionEnum.R4;

        return fhirParser.encode(transformed, mediaType, outVersion);
    }
}
