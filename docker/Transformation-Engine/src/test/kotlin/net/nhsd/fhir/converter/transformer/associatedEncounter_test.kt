package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import java.util.stream.Stream
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.Reference as R3Reference
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.Reference as R4Reference

internal class AssociatedEncounterTest {
    @Test
    internal fun `it should create AllergyIntolerance encounter element` () {
        // Given a care connect associated Encounter extension
        val r3Extension = R3Extension().apply{
            url =  "http://hl7.org/fhir/StructureDefinition/encounter-associatedEncounter"
        }

        val r4Resource = R4AllergyIntolerance()

        // When associatedEncounter is called
        associatedEncounter(r3Extension, r4Resource)

        // Then the resulting R4 Allergy Intolerance resource should have an encounter element
        val transformedResourceEncounter = r4Resource.encounter
        assertThat(transformedResourceEncounter).isNotNull
        assertThat(transformedResourceEncounter).isInstanceOf(R4Reference::class.java)
    }

    @Test
    internal fun `it should transfer associatedEncounter reference` () {
        // Given a care connect associated Encounter extension
        val r3Extension = R3Extension().apply{
            url =  "http://hl7.org/fhir/StructureDefinition/encounter-associatedEncounter"
            val valueReference = R3Reference("Encounter/7D179839-BECC-49FB-B58D-97627930D360")
            setValue(valueReference)
        }


        val r4Resource = R4AllergyIntolerance()

        // When associatedEncounter is called
        associatedEncounter(r3Extension, r4Resource)

        // Then the resulting R4 Allergy Intolerance resource should have an encounter element
        val transformedResourceEncounter = r4Resource.encounter
        assertThat(transformedResourceEncounter).isNotNull

        val transformedResourceEncounterReference = r4Resource.encounter.reference
        assertThat(transformedResourceEncounterReference).isEqualTo("Encounter/7D179839-BECC-49FB-B58D-97627930D360")
    }

    @ParameterizedTest(name = "Test AllergyIntolerance extension associatedEncounter with IOPS example index: {index} (zero based indexing)")
    @MethodSource("provideExamples")
    internal fun `it should handle IOPS examples`(input: String, expected: String) {
        val converterService = makeConverterService()

        val actualResource = converterService.convert(input,
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
            val pairs = careconnectExampleLoader.loadExample(R3AllergyIntolerance::class.java, "Encounter-associatedEncounter")
                .map { Arguments.of(it.input, it.output) }

            return pairs.stream()
        }
    }
}
