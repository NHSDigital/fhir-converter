package nhsd.fhir.transformationenginepoc.controller;

import nhsd.fhir.transformationenginepoc.service.ConversionService;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

@Validated
@RestController
@RequestMapping(value = "/$convert")
public class ConversionController {

    private final ConversionService fileConversionService;

    public ConversionController(ConversionService conversionService) {
        this.fileConversionService = conversionService;
    }

    @PostMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> convert(@RequestHeader("Content-Type") final String content_type,
                                     @RequestHeader("Accept") final String accept,
                                     @NotNull @RequestBody final String fhirSchema) {


        String currentVersion, targetVersion;
        MediaType mediaTypeIn, mediaTypeInOut;

        try {
            currentVersion = content_type.split(";")[1].split("=")[1];
            targetVersion = accept.split(";")[1].split("=")[1];
            mediaTypeIn = content_type.split(";")[0].contains("xml") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
            mediaTypeInOut = accept.split(";")[0].contains("xml") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Invalid syntax for this request was provided. " + e);
        }

        if (mediaTypeIn.getSubtype().equals("json")) {
            try {
                new JSONObject(fhirSchema);
            } catch (JSONException jse) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Invalid syntax for this request was provided. Please check your JSON payload");
            }
        }

        if (mediaTypeIn.getSubtype().equals("xml")) {
            try {
                SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(fhirSchema)));
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Invalid syntax for this request was provided. Please check your XML payload");
            }
        }

        String convertedFhir = Strings.EMPTY;
        try {
            convertedFhir = fileConversionService.convertFhirSchema(currentVersion, targetVersion, mediaTypeIn, mediaTypeInOut, fhirSchema);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity()
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getMessage());
        }

        return ResponseEntity.ok()
            .contentType(mediaTypeInOut)
            .body(convertedFhir);

    }

}
