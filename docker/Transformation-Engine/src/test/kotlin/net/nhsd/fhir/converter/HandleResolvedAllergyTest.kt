package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import io.mockk.*
import org.assertj.core.api.Assertions
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.junit.jupiter.api.Test

internal class HandleResolvedAllergyTest{
    companion object {
        val R4_ACTIVE_ALLERGY = AllergyIntolerance().apply { id = "active-allergy" }
        val R4_RESOLVED_ALLERGY = AllergyIntolerance().apply { id = "resolved-allergy" }

        val LIST_OF_RESOLVED = mutableListOf<String>().apply {
            this.add("resolved-allergy")
        }

        val R4_BUNDLE = Bundle().apply {
            val bundleEntry1 = Bundle.BundleEntryComponent().apply{
                this.resource = R4_ACTIVE_ALLERGY
            }
            val bundleEntry2 = Bundle.BundleEntryComponent().apply{
                this.resource = R4_RESOLVED_ALLERGY
            }
            this.addEntry(bundleEntry1)
            this.addEntry(bundleEntry2)
        }

        val R4_BUNDLE_ONE_ENTRY = Bundle().apply {
            val bundleEntry2 = Bundle.BundleEntryComponent().apply{
                this.resource = R4_RESOLVED_ALLERGY
            }
            this.addEntry(bundleEntry2)
        }
    }

    @Test
    fun `it should not add clinical status if Allergy intolerance id is not in list`() {
        // Given
        val transformed = R4_ACTIVE_ALLERGY

        // When
        val r4Resource = handleResolvedAllergy(transformed, LIST_OF_RESOLVED, DSTU3, R4)

        // Then
        val clinicalStatus = (r4Resource as AllergyIntolerance).hasClinicalStatus()
        Assertions.assertThat(clinicalStatus).isFalse
    }

    @Test
    fun `it should add clinical status resolved if Allergy intolerance is in list`() {
        // Given
        val transformed = R4_RESOLVED_ALLERGY

        // When
        val r4Resource = handleResolvedAllergy(transformed, LIST_OF_RESOLVED, DSTU3, R4)

        // Then
        val clinicalStatus = (r4Resource as AllergyIntolerance).clinicalStatus.coding[0].code
        Assertions.assertThat(clinicalStatus).isEqualTo("resolved")
    }
    @Test
    fun `it should add clinical status resolved if Allergy intolerance is in list - in Bundle`() {
        // Given
        val transformed = R4_BUNDLE_ONE_ENTRY

        // When
        val r4Resource = handleResolvedAllergy(transformed, LIST_OF_RESOLVED, DSTU3, R4)

        // Then
        val allergy = (r4Resource as Bundle).entry[0].resource
        val clinicalStatus = (allergy as AllergyIntolerance).clinicalStatus.coding[0].code
        Assertions.assertThat(clinicalStatus).isEqualTo("resolved")
    }

    @Test
    fun `it should create resolved allergies for only resolved Allergy Intolerance in Bundle`() {
        // Given
        val transformed = R4_BUNDLE

        // When
        mockkStatic(::createResolvedClinicalStatus)
        every { createResolvedClinicalStatus() } returns CodeableConcept()

        handleResolvedAllergy(transformed, LIST_OF_RESOLVED, DSTU3, R4)
        // Then
        verify (exactly = 1) {createResolvedClinicalStatus()}
        clearAllMocks()
    }
}
