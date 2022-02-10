package net.nhsd.fhir.converter.transformer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MedicationRequestTransformer {

    @BeforeEach
    internal fun setUp() {
        val pairs = loadExample("MedicationRequest", "RepeatInformation")
    }

    @Test
    internal fun `it should`() {


    }

}
