package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.assertj.core.api.Assertions
import org.hl7.fhir.dstu3.model.Extension
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.Meta as R3Meta
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import org.hl7.fhir.dstu3.model.UriType

internal class TransformProfileUrlTest {
//    @ParameterizedTest
//    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
//    internal fun `it should transform a Allergy Intolerance CareConnect profile url to UK Core url`(extUrl: String) {
//        // Given
//        val r3Resource = R3AllergyIntolerance().apply {
//            meta = R3Meta().apply {
//                var url = UriType("https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-AllergyIntolerance-1")
//                profile = mutableListOf(url)
//            }
//        }
//        print(r3Resource.meta.profile)
//
//        val r4Resource = R4AllergyIntolerance()
//
//        // When
//        transformCareConnectProfile(r3Resource, r4Resource)
//
//        // Then
//        val transformedUrlArray = r4Resource.meta.profile
//        Assertions.assertThat(transformedUrlArray).isNotNull
//        Assertions.assertThat(transformedUrlArray).hasSize(1)
//        Assertions.assertThat(transformedUrlArray[0]).isEqualTo("https://fhir.hl7.org.uk/StructureDefinition/UKCore-AllergyIntolerance")
//    }

//    @ParameterizedTest
//    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
//    internal fun `it should transform a Bundle CareConnect profile url to UK Core url`(extUrl: String) {
//        // Given
//        val r3Resource = R3Bundle().apply {
//            meta = R3Meta().apply {
//                var url = UriType("https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-StructuredRecord-Bundle-1")
//                profile = mutableListOf(url)
//            }
//        }
//        print(r3Resource.meta.profile)
//
//        val r4Resource = R4Bundle()
//
//        // When
//        transformCareConnectProfile(r3Resource, r4Resource)
//
//        // Then
//        val transformedUrlArray = r4Resource.meta.profile
//        Assertions.assertThat(transformedUrlArray).isNotNull
//        Assertions.assertThat(transformedUrlArray).hasSize(1)
//        Assertions.assertThat(transformedUrlArray[0]).isEqualTo("https://fhir.hl7.org.uk/StructureDefinition/UKCore-Bundle")
//    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should transform a Bundle CareConnect profile url and its containing CareConnect resources to UK Core url`(extUrl: String) {
        // Given
        val r3Bundle = R3Bundle().apply {
            meta = R3Meta().apply {
                var url = UriType("https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-StructuredRecord-Bundle-1")
                profile = mutableListOf(url)
            }
        }
        val r3allergyIntolerance = R3AllergyIntolerance().apply{
            meta = R3Meta().apply {
                var url = UriType("https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-AllergyIntolerance-1")
                profile = mutableListOf(url)
            }
        }
        r3Bundle.apply {
            var bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3allergyIntolerance

            addEntry(bundleEntry)
        }


        val r4Resource = R4Bundle()

        // When
        val transformer =  CareconnectTransformer()
        transformer.transform(r3Bundle, r4Resource)

        // Then
        val transformedUrlArray = r4Resource.meta.profile
        Assertions.assertThat(transformedUrlArray).isNotNull
        Assertions.assertThat(transformedUrlArray).hasSize(1)
        Assertions.assertThat(transformedUrlArray[0]).isEqualTo("https://fhir.hl7.org.uk/StructureDefinition/UKCore-Bundle")
    }

//    @ParameterizedTest(name = "Test transformer with prfile url")
//    internal fun `it should handle IOPS examples`(input: String, expected: String) {
//        val converterService = makeConverterService()
//
//        val actualResource = converterService.convert(input,
//            MediaType.APPLICATION_JSON,
//            FhirVersionEnum.DSTU3,
//            MediaType.APPLICATION_JSON,
//            FhirVersionEnum.R4
//        )
//        JsonAssert.assertThatJson(actualResource).isEqualTo(expected)
//    }
}
