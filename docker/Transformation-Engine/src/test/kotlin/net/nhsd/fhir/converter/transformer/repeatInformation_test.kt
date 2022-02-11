package net.nhsd.fhir.converter.transformer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest


internal class RepeatInformationTest {
    private val exampleLoader = ExampleLoader(R3MedicationRequest::class.java, R4MedicationRequest::class.java)

    @BeforeEach
    internal fun setUp() {

    }

    @Test
    internal fun `it should handle IOPS examples`() {
        val pairs = exampleLoader.loadExample("MedicationRequest", "RepeatInformation")
        val resPairs = exampleLoader.parseBulk(pairs)

    }
}
