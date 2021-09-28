package nhsd.fhir.transformationenginepoc.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

class StatusControllerTest {

    @InjectMocks
    private StatusController statusController;


    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    public void call_status_endpoint(){
        ResponseEntity<Status> status = statusController.status();
        assertEquals(status.getStatusCode(), HttpStatus.OK);
    }
}
