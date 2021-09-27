package nhsd.fhir.transformationenginepoc.controller;

import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import nhsd.fhir.transformationenginepoc.service.FileConversionService;
import nhsd.fhir.transformationenginepoc.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/convert")
public class ConversionController {


    @Autowired
    private FileConversionService fileConversionService;

    @Autowired
    private ValidationService validationService;

    @PostMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> convert(@RequestHeader("Content-Type") final String content_type,
                                     @RequestHeader("Accept") final String accept,
                                     @RequestHeader("Validate") final Boolean toValidate,
                                     @RequestBody final String fhirSchema) {

        final String currentVersion = content_type.split(";")[1].split("=")[1];
        final String targetVersion = accept.split(";")[1].split("=")[1];
        final MediaType mediaTypeIn = content_type.split(";")[0].contains("xml") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        final MediaType mediaTypeInOut = accept.split(";")[0].contains("xml") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;

        if (toValidate) {
            final ValidationResult checkedInputFile = validationService.validateSchema(currentVersion, fhirSchema);

            if (!checkedInputFile.isSuccessful()) {
                return ResponseEntity.unprocessableEntity().body(checkedInputFile.getMessages()
                    .stream()
                    .map(SingleValidationMessage::getMessage)
                    .collect(Collectors.joining(",")));
            }
        }

        final String convertedFhir = fileConversionService.convertFhirSchema(currentVersion, targetVersion, mediaTypeIn, mediaTypeInOut, fhirSchema);

        return ResponseEntity.ok()
                .contentType(mediaTypeInOut)
                .body(convertedFhir);

    }

}
