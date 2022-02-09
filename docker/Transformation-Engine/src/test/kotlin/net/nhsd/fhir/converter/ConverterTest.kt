package net.nhsd.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum.DSTU3
import ca.uhn.fhir.context.FhirVersionEnum.R4
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.convertors.conv30_40.VersionConvertor_30_40
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.hl7.fhir.dstu3.model.MedicationRequest as R3MedicationRequest
import org.hl7.fhir.dstu3.model.Resource as R3Resource
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.Resource as R4Resource


internal class ConverterTest {
    private val converter30To40 = mockk<VersionConvertor_30_40>()

    lateinit var converter: Converter

    companion object {
        val R3_RESOURCE: IBaseResource = R3MedicationRequest()
        val R4_RESOURCE: IBaseResource = R4MedicationRequest()
    }

    @BeforeEach
    internal fun setUp() {
        converter = Converter(converter30To40);
    }

    @Test
     fun `it should return resource as it is, if source and target are the same version`() {
        // When
        val r4Converted = converter.convert(R4_RESOURCE, R4, R4)
        val r3Converted = converter.convert(R3_RESOURCE, DSTU3, DSTU3)

        // Then
        assertThat(r4Converted).isEqualTo(R4_RESOURCE)
        assertThat(r3Converted).isEqualTo(R3_RESOURCE)
        verify { converter30To40 wasNot Called }
    }

    @Test
    fun `it should convert r4 to r3`() {
        // Given
        every { converter30To40.convertResource(R4_RESOURCE as R4Resource) } returns R3_RESOURCE as R3Resource

        // When
        val converted = converter.convert(R4_RESOURCE, R4, DSTU3)

        // Then
        assertThat(converted as R3MedicationRequest).isNotNull
        verify { converter30To40.convertResource(R4_RESOURCE as R4Resource) }
    }

    @Test
    fun `it should convert r3 to r4`() {
        // Given
        every { converter30To40.convertResource(R3_RESOURCE as R3Resource) } returns R4_RESOURCE as R4Resource

        // When
        val converted = converter.convert(R3_RESOURCE, DSTU3, R4)

        // Then
        assertThat(converted as R4MedicationRequest).isNotNull
        verify { converter30To40.convertResource(R3_RESOURCE as R3Resource) }
    }
}
