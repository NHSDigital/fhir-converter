package nhsd.fhir.transformationenginepoc.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/_status")
public class StatusController {
    
    @GetMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> status() {

        return ResponseEntity.ok().body("Don Lucas says I'm Healthy!");
    }
}
