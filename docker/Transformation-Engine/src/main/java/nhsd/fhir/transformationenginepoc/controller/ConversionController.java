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
                                     @RequestHeader("Accept") final String accept_type,
                                     @RequestBody final String fhirSchema) {


        final String currentVersion = content_type.split(";")[1].split("=")[1];
        final String targetVersion = accept_type.split(";")[1].split("=")[1];
        final String content_type_in = content_type.split(";")[0];
        final String content_type_out = accept_type.split(";")[0];

        final ValidationResult checkedInputFile = validationService.validateSchema(currentVersion, fhirSchema);

        if (!checkedInputFile.isSuccessful()) {
            return ResponseEntity.unprocessableEntity().body(checkedInputFile.getMessages()
                    .stream()
                    .map(SingleValidationMessage::getMessage)
                    .collect(Collectors.joining(",")));
        }

        final String convertedFhir = fileConversionService.convertFhirSchema(currentVersion, targetVersion, content_type_in, content_type_out, fhirSchema);

        final ValidationResult validationResult2 = validationService.validateSchema(targetVersion, convertedFhir);

        if (validationResult2.isSuccessful()) {
            return ResponseEntity.ok()
                    .contentType(convertedFhir.startsWith("<") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON)
                    .body(convertedFhir);
        }

        return ResponseEntity.unprocessableEntity().body(validationResult2.getMessages()
                .stream()
                .map(SingleValidationMessage::getMessage)
                .collect(Collectors.joining(",")));
    }


}
