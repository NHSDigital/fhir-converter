package net.nhsd.fhir.converter.service

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import io.mockk.*
import net.nhsd.fhir.converter.Converter
import net.nhsd.fhir.converter.FhirParser
import net.nhsd.fhir.converter.transformer.CareconnectTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest


internal class ConverterServiceTest {
    private val parser = mockk<FhirParser>()
    private val careconnectTransformer = mockk<CareconnectTransformer>()
    private val converter = mockk<Converter>()

    lateinit var converterService: ConverterService

    companion object {
        private const val STU3_JSON_RES = "{\"resourceType\":  \"MedicationRequest\"}"
        private const val STU3_XML_RES = "<MedicationRequest></MedicationRequest>"
        private const val R4_JSON_RES = "{\"resourceType\":  \"MedicationRequest\"}"
        private const val R4_XML_RES = "<MedicationRequest></MedicationRequest>"

        private val JSON = MediaType.APPLICATION_JSON
        private val XML = MediaType.APPLICATION_XML

        private val A_STU3_RES = R3MedicationRequest()
        private val A_R4_RES = R4MedicationRequest()

        private val A_TRANSFORMED_STU3_RES = R3MedicationRequest()
        private val A_TRANSFORMED_R4_RES = R4MedicationRequest()

        private val A_CONVERTED_STU3_RES = R3MedicationRequest()
        private val A_CONVERTED_R4_RES = R4MedicationRequest()

        private val STU3_CLASS = R3MedicationRequest::class.java
        private val R4_CLASS = R4MedicationRequest::class.java
    }

    @BeforeEach
    internal fun setUp() {
        converterService = ConverterService(parser, careconnectTransformer, converter)
    }

    // FIXME: This test needs improvement. Calls to transformers (currently only CareconnectTransformer must be extracted to their own tests scenarios
    @Test
    internal fun `it should convert r4 json resource to r3 json`() {
        // Given
        every { parser.parse(R4_JSON_RES, JSON, R4_CLASS) } returns A_R4_RES
        every { converter.convert(A_R4_RES, R4, DSTU3) } returns A_CONVERTED_STU3_RES
        every { careconnectTransformer.transform(any(), any()) }
        // For R4 as of now we don't run any transformer. This means, encode() will receive converted resource i.e. there is no transformation
        every { parser.encode(A_CONVERTED_STU3_RES, JSON, DSTU3) } returns STU3_JSON_RES

        // When
        val actualConverted = converterService.convert(R4_JSON_RES, JSON, R4, JSON, DSTU3)

        // Then
        assertThat(actualConverted).isEqualTo(STU3_JSON_RES)
        verify { careconnectTransformer wasNot Called }
        verifyOrder {
            parser.parse(R4_JSON_RES, JSON, R4_CLASS)
            converter.convert(A_R4_RES, R4, DSTU3)
            parser.encode(A_CONVERTED_STU3_RES, JSON, DSTU3)
        }
    }

    @Test
    internal fun `it should convert r3 json resource to r4 json`() {
        // Given
        every { parser.parse(STU3_JSON_RES, JSON, STU3_CLASS) } returns A_STU3_RES
        every { converter.convert(A_STU3_RES, DSTU3, R4) } returns A_CONVERTED_R4_RES
        every { careconnectTransformer.transform(A_STU3_RES, A_CONVERTED_R4_RES) } answers {}
        every { parser.encode(A_CONVERTED_R4_RES, JSON, R4) } returns R4_JSON_RES

        // When
        val actualConverted = converterService.convert(STU3_JSON_RES, JSON, DSTU3, JSON, R4)

        // Then
        assertThat(actualConverted).isEqualTo(R4_JSON_RES)
        verifyOrder {
            parser.parse(STU3_JSON_RES, JSON, STU3_CLASS)
            converter.convert(A_STU3_RES, DSTU3, R4)
            careconnectTransformer.transform(A_STU3_RES, A_CONVERTED_R4_RES)
            parser.encode(A_CONVERTED_R4_RES, JSON, R4)
        }
    }
}
