package net.nhsd.fhir.converter.transformer


import org.assertj.core.api.Assertions
import org.hl7.fhir.dstu3.model.Extension
import org.junit.jupiter.api.Test
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance

class ResolvedAllergiesTest {
    @Test
    internal fun `it should add clinicalStatus resolved to resources with gpc allergy end extension`() {
        // Given
        // R3 allergy end extension
        val r3Extension = Extension().apply {
            url = CARECONNECT_GPC_ALLERGY_INTOLERANCE_END_URL
        }

        val r4Resource = R4AllergyIntolerance()

        // When transformer is called
        allergyIntoleranceEndGPC(r3Extension, r4Resource)

        // Then
        val clinicalStatus = r4Resource.clinicalStatus.coding[0].code
        Assertions.assertThat(clinicalStatus).isEqualTo("resolved")
    }
}
