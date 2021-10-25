package nhsd.fhir.transformationenginepoc.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusControllerTest {

    @Test
    public void call_status_endpoint() {
        StatusController statusController = new StatusController();
        ResponseEntity<Status> status = statusController.status();
        assertEquals(status.getStatusCode(), HttpStatus.OK);
    }
}
