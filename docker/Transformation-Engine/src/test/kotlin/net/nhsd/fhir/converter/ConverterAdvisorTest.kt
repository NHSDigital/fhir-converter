package net.nhsd.fhir.converter

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ConverterAdvisorTest {
    lateinit var advisor: ConverterAdvisor

    @BeforeEach
    internal fun setUp() {
        advisor = ConverterAdvisor()
    }

    @Test
    internal fun `it should convert extension`() {

    }
}
