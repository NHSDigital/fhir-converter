package net.nhsd.fhir.converter

import ca.uhn.fhir.parser.IParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest


internal class FhirParserTest {
    private val r3JsonParser = mockk<IParser>()
    private val r4JsonParser = mockk<IParser>()
    private val r3XmlParser = mockk<IParser>()
    private val r4XmlParser = mockk<IParser>()

    private lateinit var fhirParser: FhirParser

    companion object {
        val JSON = MediaType.APPLICATION_JSON
        val XML = MediaType.APPLICATION_XML

        const val R3_JSON_RES = "a r3 json resource"
        const val R3_XML_RES = "a r3 xml resource"
        const val R4_JSON_RES = "a r4 json resource"
        const val R4_XML_RES = "a r4 xml resource"

        val A_R3_RES = R3MedicationRequest()
        val A_R4_RES = R4MedicationRequest()
        val R3_CLASS = R3MedicationRequest::class.java
        val R4_CLASS = R4MedicationRequest::class.java
    }

    @BeforeEach
    internal fun setUp() {
        fhirParser = FhirParser(
            r3JsonParser = r3JsonParser,
            r4JsonParser = r4JsonParser,
            r3XmlParser = r3XmlParser,
            r4XmlParser = r4XmlParser
        )
    }

    @Test
    fun `it should parse r4 json resource to r4 resource`() {
        // Given
        every { r4JsonParser.parseResource(R4_CLASS, R4_JSON_RES) } returns A_R4_RES

        // When
        val resource = fhirParser.parse(R4_JSON_RES, JSON, R4_CLASS)

        // Then
        assertThat(resource).isEqualTo(A_R4_RES)
        verify { r4JsonParser.parseResource(R4_CLASS, R4_JSON_RES) }
    }

    @Test
    fun `it should parse r4 xml resource to r4 resource`() {
        // Given
        every { r4XmlParser.parseResource(R4_CLASS, R4_XML_RES) } returns A_R4_RES

        // When
        val resource = fhirParser.parse(R4_XML_RES, XML, R4_CLASS)

        // Then
        assertThat(resource).isEqualTo(A_R4_RES)
        verify { r4XmlParser.parseResource(R4_CLASS, R4_XML_RES) }
    }

    @Test
    fun `it should parse r3 json resource to r3 resource`() {
        // Given
        every { r3JsonParser.parseResource(R3_CLASS, R3_JSON_RES) } returns A_R3_RES

        // When
        val resource = fhirParser.parse(R3_JSON_RES, JSON, R3_CLASS)

        // Then
        assertThat(resource).isEqualTo(A_R3_RES)
        verify { r3JsonParser.parseResource(R3_CLASS, R3_JSON_RES) }
    }

    @Test
    fun `it should parse r3 xml resource to r3 resource`() {
        // Given
        every { r3XmlParser.parseResource(R3_CLASS, R3_XML_RES) } returns A_R3_RES

        // When
        val resource = fhirParser.parse(R3_XML_RES, XML, R3_CLASS)

        // Then
        assertThat(resource).isEqualTo(A_R3_RES)
        verify { r3XmlParser.parseResource(R3_CLASS, R3_XML_RES) }
    }
}
