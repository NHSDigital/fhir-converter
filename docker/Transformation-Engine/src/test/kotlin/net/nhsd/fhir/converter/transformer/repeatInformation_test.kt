package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType.APPLICATION_JSON
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.dstu3.model.PositiveIntType as R3PositiveIntType
import org.hl7.fhir.dstu3.model.UnsignedIntType as R3UnsignedIntType
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.UnsignedIntType as R4UnsignedIntType


internal class RepeatInformationTest {

    @BeforeEach
    internal fun setUp() {

    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_REPEAT_INFORMATION_URL, CARECONNECT_GPC_REPEAT_INFORMATION_URL])
    internal fun `it should create extension with the associated ukcore url`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl
        }

        val r4Resource = R4MedicationRequest()

        // When
        repeatInformation(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource.getExtensionByUrl(UKCORE_REPEAT_INFORMATION_URL)
        assertThat(transformedExt).isNotNull
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_REPEAT_INFORMATION_URL, CARECONNECT_GPC_REPEAT_INFORMATION_URL])
    internal fun `it should move numberOfRepeatPrescriptionsIssued across`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val issuedExt = R3Extension().apply {
                url = "numberOfRepeatPrescriptionsIssued"
                setValue(R3UnsignedIntType(1))
            }

            addExtension(issuedExt)
        }

        val r4Resource = R4MedicationRequest()

        // When
        repeatInformation(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_REPEAT_INFORMATION_URL)
            .getExtensionByUrl("numberOfRepeatPrescriptionsIssued")

        assertThat(transformedExt).isNotNull
        assertThat((transformedExt.value as R4UnsignedIntType).valueAsString).isEqualTo("1")
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_REPEAT_INFORMATION_URL, CARECONNECT_GPC_REPEAT_INFORMATION_URL])
    internal fun `it should move authorisationExpiryDate across`(extUrl: String) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val expiry = R3Extension().apply {
                url = "authorisationExpiryDate"
                setValue(R3DateTimeType("2020-08-10"))
            }

            addExtension(expiry)
        }

        val r4Resource = R4MedicationRequest()

        // When
        repeatInformation(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_REPEAT_INFORMATION_URL)
            .getExtensionByUrl("authorisationExpiryDate")

        assertThat(transformedExt).isNotNull
        assertThat((transformedExt.value as R4DateTimeType).valueAsString).isEqualTo("2020-08-10")
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_REPEAT_INFORMATION_URL, CARECONNECT_GPC_REPEAT_INFORMATION_URL])
    internal fun `it should move numberOfRepeatPrescriptionsAllowed to dispenseRequest$numberOfRepeatsAllowed field when value type is PositiveInt`(
        extUrl: String
    ) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val allowed = R3Extension().apply {
                url = "numberOfRepeatPrescriptionsAllowed"
                setValue(R3PositiveIntType(1))
            }

            addExtension(allowed)
        }

        val r4Resource = R4MedicationRequest()

        // When
        repeatInformation(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_REPEAT_INFORMATION_URL)
            .getExtensionByUrl("numberOfRepeatPrescriptionsAllowed")

        assertThat(transformedExt).isNull() // Make sure we're not accidentally creating an extension
        assertThat(r4Resource.dispenseRequest.numberOfRepeatsAllowed).isEqualTo(1)
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_REPEAT_INFORMATION_URL, CARECONNECT_GPC_REPEAT_INFORMATION_URL])
    internal fun `it should move numberOfRepeatPrescriptionsAllowed to dispenseRequest$numberOfRepeatsAllowed field when value type is UnsignedInt`(
        extUrl: String
    ) {
        // Given
        val r3Extension = R3Extension().apply {
            url = extUrl

            val allowed = R3Extension().apply {
                url = "numberOfRepeatPrescriptionsAllowed"
                setValue(R3UnsignedIntType(1))
            }

            addExtension(allowed)
        }

        val r4Resource = R4MedicationRequest()

        // When
        repeatInformation(r3Extension, r4Resource)

        // Then
        val transformedExt = r4Resource
            .getExtensionByUrl(UKCORE_REPEAT_INFORMATION_URL)
            .getExtensionByUrl("numberOfRepeatPrescriptionsAllowed")

        assertThat(transformedExt).isNull() // Make sure we're not accidentally creating an extension
        assertThat(r4Resource.dispenseRequest.numberOfRepeatsAllowed).isEqualTo(1)
    }

    @ParameterizedTest(name = "Test MedicationRequest RepeatInformation extension with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(input, APPLICATION_JSON, DSTU3, APPLICATION_JSON, R4)

        assertThatJson(actualResource).isEqualTo(expected)
    }


    // Just for debugging
    @Test
    @Disabled("Only use this for debugging one specific example file")
    internal fun runOneExample() {
        val exampleIndex = 4
        val careconnectExampleLoader = CareconnectExampleLoader()
        val converterService = makeConverterService()

        val pair = careconnectExampleLoader.loadExample(
            R3MedicationRequest::class.java,
            "RepeatInformation"
        )[exampleIndex - 1] // Careful with the index! some files start with 0 and some with 1

        // When
        val actualResource = converterService.convert(pair.input, APPLICATION_JSON, DSTU3, APPLICATION_JSON, R4)

        // Only use this to see a string diff during debugging. See function docs
//        assertJsonEquals( actualResource, pair.output )
        assertThatJson(actualResource).isEqualTo(pair.output)
    }

    companion object {
        @JvmStatic
        fun provideExamples(): Stream<Arguments> {
            val careconnectExampleLoader = CareconnectExampleLoader()
            val pairs = careconnectExampleLoader.loadExample(R3MedicationRequest::class.java, "RepeatInformation")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }

    }
}
