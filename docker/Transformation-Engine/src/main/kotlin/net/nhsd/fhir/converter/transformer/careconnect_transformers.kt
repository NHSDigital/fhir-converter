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
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.r4.model.Coding as R4Coding

internal const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_LAST_ISSUE_DATE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatementLastIssueDate-1"
internal const val CARECONNECT_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationStatementLastIssueDate-1"

internal const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"
internal const val UKCORE_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationStatementLastIssueDate"

internal const val CARECONNECT_GPC_PRESCRIPTION_TYPE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescriptionType-1"

internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_PRESCRIPTION_TYPE_URL to ::prescriptionType,
    CARECONNECT_GPC_LAST_ISSUE_DATE_URL to ::lastIssueDate,
    CARECONNECT_LAST_ISSUE_DATE_URL to ::lastIssueDate
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

fun prescriptionType(src: R3Extension, tgt: R4Resource) {
        if (src.value is R3CodeableConcept) {
            val srcCodeableConcept = src.value as R3CodeableConcept
            val r3Coding = srcCodeableConcept.coding.firstOrNull()

            r3Coding?.let {
                val r3CodingSystem = r3Coding.system
                val r3CodingCode = r3Coding.code
                val r3CodingDisplay = r3Coding.display

                val r4CodingSystem =
                    if (r3CodingSystem == "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-PrescriptionType-1")
                        "http://hl7.org/fhir/ValueSet/medicationrequest-course-of-therapy"
                    else null

                val r4CodingCode =
                    if (r3CodingCode == "acute")
                        "acute"
                    else if (r3CodingCode == "delayed-prescribing")
                        "delayed-prescribing"
                    else if (r3CodingCode == "repeat")
                        "repeat"
                    else if (r3CodingCode == "repeat-dispensing")
                        "repeat-dispensing"
                    else null

                val r4CodingDisplay =
                    if (r3CodingDisplay == "Acute")
                        "Acute"
                    else if (r3CodingDisplay == "Delayed prescribing")
                        "Delayed prescribing"
                    else if (r3CodingDisplay == "Repeat")
                        "Repeat"
                    else if (r3CodingDisplay == "Repeat dispensing")
                        "Repeat dispensing"
                    else null

                val r4Coding = R4Coding(r4CodingSystem, r4CodingCode, r4CodingDisplay)

                (tgt as R4MedicationRequest).courseOfTherapyType.coding = listOf(r4Coding)
            }
        }
    }

fun lastIssueDate(src: R3Extension, tgt: R4Resource) {
    val ext = R4Extension().apply {
        url = UKCORE_LAST_ISSUE_DATE_URL

        if (src.value != null) {
            val newValue = R4DateTimeType((src.value as R3DateTimeType).asStringValue())
            this.setValue(newValue)
        }
    }
    tgt.addExtension(ext)
}
