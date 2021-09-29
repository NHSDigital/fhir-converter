package nhsd.fhir.transformationenginepoc.controller;

import nhsd.fhir.transformationenginepoc.service.FileConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/convert")
public class ConversionController {


    @Autowired
    private FileConversionService fileConversionService;

    @PostMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> convert(@RequestHeader("Content-Type") final String content_type,
                                     @RequestHeader("Accept") final String accept,
                                     @RequestBody final String fhirSchema) {


        final String currentVersion = content_type.split(";")[1].split("=")[1];
        final String targetVersion = accept.split(";")[1].split("=")[1];
        final MediaType mediaTypeIn = content_type.split(";")[0].startsWith("<") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        final MediaType mediaTypeInOut = accept.split(";")[0].startsWith("<") ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;

        final String convertedFhir = fileConversionService.convertFhirSchema(currentVersion, targetVersion, mediaTypeIn, mediaTypeInOut, fhirSchema);

        return ResponseEntity.ok()
                .contentType(mediaTypeInOut)
                .body(convertedFhir);

    }

}
