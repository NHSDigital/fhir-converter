import net.nhsd.fhir.converter.findResolvedAllergy
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.AllergyIntolerance
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.MedicationRequest
import org.hl7.fhir.dstu3.model.Resource
import org.hl7.fhir.instance.model.api.IBaseResource
import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import org.junit.jupiter.api.Test

internal class FindResolvedAllergyTest{
    companion object {
        val R3_MEDICATION_RESOURCE: IBaseResource = MedicationRequest().apply { id = "r3-med-resource" }
        val R3_RESOLVED_ALLERGY: IBaseResource = AllergyIntolerance().apply {
            id = "r3-resolved-allergy"
            clinicalStatus = AllergyIntolerance.AllergyIntoleranceClinicalStatus.RESOLVED
        }

        val R3_BUNDLE :IBaseResource = Bundle().apply {
            val bundleEntry1 = Bundle.BundleEntryComponent().apply {
               this.resource = (R3_MEDICATION_RESOURCE as Resource)
            }
            val bundleEntry2 = Bundle.BundleEntryComponent().apply {
                this.resource = (R3_RESOLVED_ALLERGY as Resource)
            }
            this.addEntry(bundleEntry1)
            this.addEntry(bundleEntry2)
        }
    }

    @Test
    fun `it should return empty list when resource is not r3 resolved allergy`() {
        // Given
        val r3Resource = R3_MEDICATION_RESOURCE

        // When
        val listIds = findResolvedAllergy(r3Resource, DSTU3, R4)

        // Then
        assertThat(listIds).isEmpty()
    }

    @Test
    fun `it should return list with id when resource is r3 resolved allergy`() {
        // Given
        val r3Resource = R3_RESOLVED_ALLERGY

        // When
        val listIds = findResolvedAllergy(r3Resource, DSTU3, R4)

        // Then
        assertThat(listIds).isNotEmpty
        assertThat(listIds).hasSize(1)
        assertThat(listIds[0]).isEqualTo("r3-resolved-allergy")

    }

    @Test
    fun `it should return list with only resolved allergy ids if resource is bundle `() {
        // Given
        val r3Resource = R3_BUNDLE

        // When
        val listIds = findResolvedAllergy(r3Resource, DSTU3, R4)

        // Then
        assertThat(listIds).isNotEmpty
        assertThat(listIds).hasSize(1)
        assertThat(listIds[0]).isEqualTo("r3-resolved-allergy")

    }
}
