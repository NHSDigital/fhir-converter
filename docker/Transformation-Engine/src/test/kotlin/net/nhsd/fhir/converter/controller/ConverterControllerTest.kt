package net.nhsd.fhir.converter.controller

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import net.nhsd.fhir.converter.service.ConverterService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(ConverterController::class)
class ConverterControllerTest {

    @MockkBean
    private lateinit var converterService: ConverterService

    @Autowired
    private lateinit var mvc: MockMvc

    companion object {
        const val ENDPOINT = "/\$convert"
        const val BODY = "a resource"

        const val R3_JSON_CONTENT_TYPE_HEADER = "application/fhir+json; fhirVersion=3.0"
        const val R4_JSON_CONTENT_TYPE_HEADER = "application/fhir+json; fhirVersion=4.0"
        const val R3_JSON_ACCEPT_HEADER = "application/fhir+json; fhirVersion=3.0"
        const val R4_JSON_ACCEPT_HEADER = "application/fhir+json; fhirVersion=4.0"

        const val BAD_CONTENT_TYPE_HEADER = "application/text"
        const val BAD_CONTENT_TYPE_VERSION = "application/fhir+json; fhirVersion=2.0"
        const val BAD_ACCEPT_HEADER = "application/fhir+json; fhirVersion"


        private val JSON = MediaType.APPLICATION_JSON

        fun body(result: MvcResult) = result.response.contentAsString
    }

    @Test
    fun `it should convert r4 json to r3 json`() {
        // Given
        every { converterService.convert(BODY, JSON, R4, JSON, DSTU3) } returns BODY

        val request = post(ENDPOINT)
            .header("Content-Type", R4_JSON_CONTENT_TYPE_HEADER)
            .header("Accept", R3_JSON_ACCEPT_HEADER)
            .content(BODY)

        // When
        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect { assertThat(body(it)).isEqualTo("a resource") }
    }

    @Test
    fun `it should convert r3 json to r4 json`() {
        // Given
        every { converterService.convert(BODY, JSON, DSTU3, JSON, R4) } returns BODY

        val request = post(ENDPOINT)
            .header("Content-Type", R3_JSON_CONTENT_TYPE_HEADER)
            .header("Accept", R4_JSON_ACCEPT_HEADER)
            .content(BODY)

        // When
        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect { assertThat(body(it)).isEqualTo("a resource") }
    }

    @Test
    fun `it should fail on contentType header checks` () {
        // Given
        every { converterService.convert(BODY, JSON, DSTU3, JSON, R4) } returns BODY

        val request = post(ENDPOINT)
            .header("Content-Type", BAD_CONTENT_TYPE_HEADER)
            .header("Accept", R4_JSON_ACCEPT_HEADER)
            .content(BODY)

        // When
        mvc.perform(request)
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun `it should fail on accept header checks` () {
        // Given
        every { converterService.convert(BODY, JSON, DSTU3, JSON, R4) } returns BODY

        val request = post(ENDPOINT)
            .header("Content-Type", R3_JSON_CONTENT_TYPE_HEADER)
            .header("Accept", BAD_ACCEPT_HEADER)
            .content(BODY)

        // When
        mvc.perform(request)
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `it should fail on invalid fhirVersion in contentType` () {
        // Given
        every { converterService.convert(BODY, JSON, DSTU3, JSON, R4) } returns BODY

        val request = post(ENDPOINT)
            .header("Content-Type", BAD_CONTENT_TYPE_VERSION)
            .header("Accept",R4_JSON_ACCEPT_HEADER)
            .content(BODY)

        // When
        mvc.perform(request)
            .andExpect(status().isBadRequest)
    }

}

