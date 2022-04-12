package net.nhsd.fhir.converter.transformer


import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.Meta as R3Meta
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.Meta as R4Meta
import org.hl7.fhir.r4.model.CanonicalType as R4CanonicalType
import org.hl7.fhir.dstu3.model.UriType as R3UriType

internal class TransformProfileUrlTest {
    @Test
    internal fun `it should transform a Bundle's and Allergy Intolerance entries CareConnect profile urls to UK Core url`() {
        // Given
        // Set up R3 source
        val r3Allergy = R3AllergyIntolerance().apply {
            meta = R3Meta().apply {
                val url = R3UriType(CARECONNECT_ALLERGY_PROFILE_URL)
                profile = mutableListOf(url)
            }
        }
        val r3Bundle = R3Bundle().apply {
            meta = R3Meta().apply {
                val url = R3UriType(GPCONNECT_BUNDLE_PROFILE_URL)
                profile = mutableListOf(url)
            }
            val bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3Allergy
            this.addEntry(bundleEntry)
        }

        // Set up R4 target
        val r4Allergy = R4AllergyIntolerance().apply {
            meta = R4Meta().apply {
                // R4 meta uses Canonical type, whereas R3 uses Uritype
                val url = R4CanonicalType(CARECONNECT_ALLERGY_PROFILE_URL)
                profile = mutableListOf(url)
            }
        }
        val r4Bundle = R4Bundle().apply {
            meta = R4Meta().apply {
                val url = R4CanonicalType(GPCONNECT_BUNDLE_PROFILE_URL)
                profile = mutableListOf(url)
            }
            val bundleEntry = R4Bundle.BundleEntryComponent()
            bundleEntry.resource = r4Allergy
            this.addEntry(bundleEntry)
        }

        // When
        val transformer = CareconnectTransformer()
        transformer.transform(r3Bundle, r4Bundle)

        // Then
        val transformedBundleUrlArray = r4Bundle.meta.profile
        Assertions.assertThat(transformedBundleUrlArray).isNotNull
        Assertions.assertThat(transformedBundleUrlArray).hasSize(1)
        Assertions.assertThat(transformedBundleUrlArray[0].asStringValue()).isEqualTo(UKCORE_BUNDLE_PROFILE_URL)

        // check allergy
        val transformedAllergyUrlArray = r4Bundle.entry[0].resource.meta.profile
        Assertions.assertThat(transformedAllergyUrlArray).isNotNull
        Assertions.assertThat(transformedAllergyUrlArray).hasSize(1)
        Assertions.assertThat(transformedAllergyUrlArray[0].asStringValue()).isEqualTo(UKCORE_ALLERGY_PROFILE_URL)
    }

    @Test
    internal fun `it should handle resources without Meta`() {
        // Given
        // Set up R3 source
        val r3Allergy = R3AllergyIntolerance().apply {}
        val r3Bundle = R3Bundle().apply {
            val bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3Allergy
            this.addEntry(bundleEntry)
        }

        // Set up R4 target
        val r4Allergy = R4AllergyIntolerance()

        val r4Bundle = R4Bundle().apply {
            val bundleEntry = R4Bundle.BundleEntryComponent()
            bundleEntry.resource = r4Allergy
            this.addEntry(bundleEntry)
        }
        // When
        val transformer = CareconnectTransformer()
        transformer.transform(r3Bundle, r4Bundle)

        // Then
        val transformedBundleUrlArray = r4Bundle.meta.profile
        Assertions.assertThat(transformedBundleUrlArray).isEmpty()

    }

    @Test
    internal fun `it should transform a Allergy Intolerance CareConnect profile url to UK Core url`() {
        // Given
        // Set up R3 source
        val r3Allergy = R3AllergyIntolerance().apply {
            meta = R3Meta().apply {
                val url = R3UriType(CARECONNECT_ALLERGY_PROFILE_URL)
                profile = mutableListOf(url)
            }
        }

        // Set up R4 target
        val r4Allergy = R4AllergyIntolerance().apply {
            meta = R4Meta().apply {
                val url = R4CanonicalType(CARECONNECT_ALLERGY_PROFILE_URL)
                profile = mutableListOf(url)
            }
        }

        // When
        val transformer = CareconnectTransformer()
        transformer.transform(r3Allergy, r4Allergy)

        // Then
        val transformedAllergyUrlArray = r4Allergy.meta.profile
        Assertions.assertThat(transformedAllergyUrlArray).isNotNull
        Assertions.assertThat(transformedAllergyUrlArray).hasSize(1)
        Assertions.assertThat(transformedAllergyUrlArray[0].asStringValue()).isEqualTo(UKCORE_ALLERGY_PROFILE_URL)
    }
}
