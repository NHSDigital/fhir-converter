package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.StringType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.Coding as R3Coding
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.IdType as R3IdType
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept
import org.hl7.fhir.r4.model.Coding as R4Coding

internal class AllergyIntoleranceCodingTest {
    @Test
    internal fun `it should handle extension inside AllergyIntolerance coding`() {
        // Given
        val desId = R3Extension("descriptionId", R3IdType("descriptionId value"))
        val desDisplay = R3Extension("descriptionDisplay", R3IdType("descriptionDisplay value"))
        val r3Extension = R3Extension(CARECONNECT_DESCRIPTION_ID_URL).apply {
            addExtension(desId)
            addExtension(desDisplay)
        }

        // A Resource can have multiple codings
        val source = R3AllergyIntolerance().apply {
            code = R3CodeableConcept().apply {
                val code1 = R3Coding().apply { addExtension(r3Extension) }
                val code2 = R3Coding().apply { addExtension(r3Extension) }
                coding = listOf(code1, code2)
            }
        }

        val target = R4AllergyIntolerance().apply {
            code = R4CodeableConcept().apply {
                val code1 = R4Coding()
                val code2 = R4Coding()
                coding = listOf(code1, code2)
            }
        }

        val transformer = CareconnectTransformer()

        // When
        val expectedResource = transformer.transform(source, target) as R4AllergyIntolerance

        // Then
        expectedResource.code.coding.forEach { coding ->
            assertThat(coding.getExtensionByUrl(CARECONNECT_DESCRIPTION_ID_URL)).isNull()
        }

        expectedResource.code.coding.forEach { coding ->
            val actualExtension = coding.getExtensionByUrl(UKCORE_DESCRIPTION_ID_URL)

            val r3DesId = actualExtension.getExtensionByUrl("descriptionId")
            assertThat(r3DesId.url).isEqualTo("descriptionId")
            assertThat(r3DesId.value).isInstanceOf(IdType::class.java)
            assertThat(r3DesId.value.primitiveValue()).isEqualTo("descriptionId value")

            val r3DesDisplay = actualExtension.getExtensionByUrl("descriptionDisplay")
            assertThat(r3DesDisplay.url).isEqualTo("descriptionDisplay")
            assertThat(r3DesDisplay.value).isInstanceOf(StringType::class.java)
            assertThat(r3DesDisplay.value.primitiveValue()).isEqualTo("descriptionDisplay value")
        }
    }

    @ParameterizedTest(name = "Test AllergyIntolerance extensionId and extensionDisplay extensions with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(
            input,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.DSTU3,
            MediaType.APPLICATION_JSON,
            FhirVersionEnum.R4
        )

        JsonAssert.assertThatJson(actualResource).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun provideExamples(): Stream<Arguments> {
            val careconnectExampleLoader = CareconnectExampleLoader()
            val pairs = careconnectExampleLoader.loadExample(R3AllergyIntolerance::class.java, "ExtensionId")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
