package nhsd.fhir.transformationenginepoc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class Status {
    private String status;

    public Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

@RestController
class StatusController {
    @GetMapping(path = "/_status")
    public ResponseEntity<Status> status() {

        Status pass = new Status("pass");

        return ResponseEntity.ok().body(pass);
    }

}
