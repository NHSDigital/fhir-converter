package net.nhsd.fhir.converter.transformer

import ca.uhn.fhir.context.FhirVersionEnum
import net.javacrumbs.jsonunit.assertj.JsonAssert
import net.nhsd.fhir.converter.FhirParser
import org.assertj.core.api.Assertions
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.Coding as R3Coding
import org.hl7.fhir.dstu3.model.IdType as R3IdType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept
import org.hl7.fhir.r4.model.Coding as R4Coding
import org.hl7.fhir.r4.model.IdType as R4IdType
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType

internal class BundleOfResourcesTest {
    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should convert R3 extensions found in resources within bundle`(extUrl: String) {
        // Given
        // Allergy Intolerance 1
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

    @ParameterizedTest
    @ValueSource(strings = [CARECONNECT_ALLERGY_INTOLERANCE_END_URL])
    internal fun `it should convert R3 coding-sctdescid extensions found in resources within bundle`(extUrl: String) {
        val r3desId = R3Extension("descriptionId", R3IdType("descriptionId value"))
        val r3desDisplay = R3Extension("descriptionDisplay", R3IdType("descriptionDisplay value"))
        val r3Extension = R3Extension(CARECONNECT_DESCRIPTION_ID_URL).apply {
            addExtension(r3desId)
            addExtension(r3desDisplay)
        }
        val r3Allergy = R3AllergyIntolerance().apply {
            code = R3CodeableConcept().apply {
                val code1 = R3Coding().apply { addExtension(r3Extension) }
                coding = listOf(code1)
            }
        }

        val r3Bundle = R3Bundle().apply{
            val bundleEntry = R3Bundle.BundleEntryComponent()
            bundleEntry.resource = r3Allergy
            this.addEntry(bundleEntry)
        }

        val r4desId = R4Extension("descriptionId", R4IdType("descriptionId value"))
        val r4desDisplay = R4Extension("descriptionDisplay", R4IdType("descriptionDisplay value"))
        val r4Extension = R4Extension(CARECONNECT_DESCRIPTION_ID_URL).apply {
            addExtension(r4desId)
            addExtension(r4desDisplay)
        }
        val r4Allergy = R4AllergyIntolerance().apply {
            code = R4CodeableConcept().apply {
                val code1 = R4Coding().apply { addExtension(r4Extension) }
                coding = listOf(code1)
            }
        }

        val r4Bundle = R4Bundle().apply{
            val bundleEntry = R4Bundle.BundleEntryComponent()
            bundleEntry.resource = r4Allergy
            this.addEntry(bundleEntry)
        }

        val transformer = CareconnectTransformer()

        // When
        transformer.transform(r3Bundle, r4Bundle)

        // Then
        val transformedAllergyIntolerance = r4Bundle.entry[0].resource
        Assertions.assertThat(transformedAllergyIntolerance).isNotNull

        (transformedAllergyIntolerance as R4AllergyIntolerance).code.coding.forEach { coding ->
            Assertions.assertThat(coding.getExtensionByUrl(CARECONNECT_DESCRIPTION_ID_URL)).isNull()

            Assertions.assertThat(coding.getExtensionByUrl(UKCORE_DESCRIPTION_ID_URL)).isNotNull()
        }



    }


