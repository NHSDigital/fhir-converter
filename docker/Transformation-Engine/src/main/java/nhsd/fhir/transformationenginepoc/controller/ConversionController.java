package nhsd.fhir.transformationenginepoc.controller;

import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import nhsd.fhir.transformationenginepoc.service.ConversionService;
import nhsd.fhir.transformationenginepoc.service.ValidationService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/convert")
public class ConversionController {


    @Autowired
    private ConversionService fileConversionService;

    @Autowired
    private ValidationService validationService;

    @PostMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> convert(@RequestHeader("Content-Type") final String content_type,
                                     @RequestHeader("Accept") final String accept,
                                     @RequestHeader("Validate") final Boolean toValidate,
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

        if(mediaTypeIn.getType().equals("json")){
            try {
                JsonParser parser = new JsonParser();
                parser.parse(fhirSchema);
            } catch (JsonSyntaxException jse) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Invalid syntax for this request was provided. Please check your Fhir payload");
            }
        }

        if(mediaTypeIn.getType().equals("xml")) {
            try {
                SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(fhirSchema)));
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Invalid syntax for this request was provided. Please check your Fhir payload");
            }
        }

        if (toValidate) {
            final ValidationResult checkedInputFile = validationService.validateSchema(currentVersion, fhirSchema);

            if (!checkedInputFile.isSuccessful()) {
                return ResponseEntity.unprocessableEntity().body(checkedInputFile.getMessages()
                    .stream()
                    .map(SingleValidationMessage::getMessage)
                    .collect(Collectors.joining(",")));
            }
        }

        String convertedFhir = Strings.EMPTY;
        try {
            convertedFhir = fileConversionService.convertFhirSchema(currentVersion, targetVersion, mediaTypeIn, mediaTypeInOut, fhirSchema);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Invalid syntax for this request was provided. Please check your Fhir payload");
        }

        return ResponseEntity.ok()
            .contentType(mediaTypeInOut)
            .body(convertedFhir);

    }

}
