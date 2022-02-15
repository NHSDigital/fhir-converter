package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.PositiveIntType as R3PositiveIntType
import org.hl7.fhir.dstu3.model.UnsignedIntType as R3UnsignedIntType
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.UnsignedIntType as R4UnsignedIntType

internal const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1"

internal const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"

internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_REPEAT_INFORMATION_URL to ::repeatInformation
)

fun repeatInformation(src: R3Extension, tgt: R4Resource) {
    val ext = R4Extension().apply {
        url = UKCORE_REPEAT_INFORMATION_URL

        src.getExtensionsByUrl("numberOfRepeatPrescriptionsIssued").firstOrNull()?.let {
            val issuedExt = R4Extension().apply {
                url = "numberOfRepeatPrescriptionsIssued"
                val newValue = R4UnsignedIntType((it.value as R3UnsignedIntType).asStringValue())
                setValue(newValue)
            }
            this.addExtension(issuedExt)
        }

        src.getExtensionsByUrl("authorisationExpiryDate").firstOrNull()?.let {
            val expiry = R4Extension().apply {
                url = "authorisationExpiryDate"
                val newValue = R4DateTimeType((it.value as R3DateTimeType).asStringValue())
                setValue(newValue)
            }
            this.addExtension(expiry)
        }

        src.getExtensionsByUrl("numberOfRepeatPrescriptionsAllowed").firstOrNull()?.let {
            val newValue = when (it.value) {
                is R3PositiveIntType -> (it.value as R3PositiveIntType).value as Int
                is R3UnsignedIntType -> (it.value as R3UnsignedIntType).value as Int
                else -> throw IllegalStateException("Unsupported data type passed to transformer. This happened during transforming \"numberOfRepeatPrescriptionsAllowed\". The DataType is ${it.javaClass.name}")
            }
            (tgt as R4MedicationRequest).dispenseRequest.numberOfRepeatsAllowed = newValue
        }
    }

    tgt.addExtension(ext)
}

