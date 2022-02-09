package net.nhsd.fhir.converter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StatusController::class)
internal class StatusControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    companion object {
        const val ENDPOINT = "/_status"
        private val om = ObjectMapper()

        fun toJson(result: MvcResult): Status {
            val content = result.response.contentAsString
            return om.readValue(content, Status::class.java)
        }
    }

    @Test
    internal fun `it should return status pass response`() {
        // Given
        val expectedResponse = Status("pass")

        // When
        mvc.perform(get(ENDPOINT))
            .andExpect(status().isOk)
            .andExpect {
                assertThat(toJson(it)).isEqualTo(expectedResponse)
            }
    }
}
