package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest


internal class RepeatInformationTest {

    @BeforeEach
    internal fun setUp() {

    }

    @Test
    internal fun `it should handle IOPS examples`() {
        val careconnectExampleLoader = CareconnectExampleLoader()
        val converterService = makeConverterService()

        val pairs = careconnectExampleLoader.loadExample(R3MedicationRequest::class.java, "RepeatInformation")
        pairs.forEach {
            val actualResource = converterService.convert(it.input, APPLICATION_JSON, DSTU3, APPLICATION_JSON, R4)

            assertThatJson(actualResource).isEqualTo(it.output)
            // assertJsonEquals(actualResource, it.output) // Only use this to see a string diff during debugging. See function docs
        }
    }

    // Just for debugging
    @Test
    @Disabled("Only use this for debugging one specific example file")
    internal fun runOneExample() {
        val exampleIndex = 1
        val careconnectExampleLoader = CareconnectExampleLoader()
        val converterService = makeConverterService()

        val pair = careconnectExampleLoader.loadExample(
            R3MedicationRequest::class.java,
            "RepeatInformation"
        )[exampleIndex - 1] // Careful with the index! some files start with 0 and some with 1

        // When
        val actualResource = converterService.convert(pair.input, APPLICATION_JSON, DSTU3, APPLICATION_JSON, R4)

        assertJsonEquals(
            actualResource,
            pair.output
        ) // Only use this to see a string diff during debugging. See function docs
    }

}
