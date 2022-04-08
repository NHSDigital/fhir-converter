package net.nhsd.fhir.converter.transformer

import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


internal class BundleOfResourcesTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should call resource transformers for each resource in the bundle`(extUrl: String) {
        val numberEntries = 3
        val r3Allergy = R3AllergyIntolerance()

        val r3Bundle = R3Bundle().apply{
            val bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3Allergy
            for(i in 1..numberEntries){
                this.addEntry(bundleEntry)
            }
        }

        val r4Allergy = R4AllergyIntolerance()
        val r4Bundle = R4Bundle().apply{
            val bundleEntry = R4Bundle.BundleEntryComponent()
            bundleEntry.resource = r4Allergy
            for(i in 1..numberEntries){
                this.addEntry(bundleEntry)
            }
        }

        val transformer = spyk<CareconnectTransformer>()

        // When
        transformer.transform(r3Bundle, r4Bundle)

        // Then
        verify(exactly = numberEntries) {
            transformer.handleResourceExtensionTransformations(any(), any())
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should convert R3 extensions found in resources within bundle`(extUrl: String) {
        // Given
        val r3Allergy = R3AllergyIntolerance().apply{
            val r3Extension = R3Extension().apply {
                url = extUrl
                val endDate = R3Extension().apply {
                    url = "endDate"
                    setValue(org.hl7.fhir.dstu3.model.DateTimeType("2016-11-01T00:00:00+00:00"))
                }
                addExtension(endDate)
            }
            this.addExtension(r3Extension)
        }

        val r3Bundle = R3Bundle().apply{
            val bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3Allergy
            this.addEntry(bundleEntry)
        }

        val r4allergy = R4AllergyIntolerance().apply{
            val r4Extension = R4Extension().apply {
                url = extUrl
                val endDate = R4Extension().apply{
                    url = "endDate"
                    setValue(DateTimeType("2016-11-01T00:00:00+00:00"))
                }
                addExtension(endDate)
            }
            this.addExtension(r4Extension)
        }

        val r4Bundle = R4Bundle().apply {
            val bundleEntry = R4Bundle.BundleEntryComponent()
            bundleEntry.resource = r4allergy
            this.addEntry(bundleEntry)
        }

        val transformer = CareconnectTransformer()         // The transformer

        // When
        transformer.transform(r3Bundle, r4Bundle)

        // Then
        val transformedAllergyIntolerance = r4Bundle.entry[0].resource
        Assertions.assertThat(transformedAllergyIntolerance).isNotNull

        val transformedExt = (transformedAllergyIntolerance as DomainResource)
            .getExtensionByUrl(UKCORE_ALLERGY_INTOLERANCE_END_URL)
            .getExtensionByUrl("endDate")
        Assertions.assertThat(transformedExt).isNotNull
        Assertions.assertThat((transformedExt.value as DateTimeType).valueAsString).isEqualTo("2016-11-01T00:00:00+00:00")
    }
    }


