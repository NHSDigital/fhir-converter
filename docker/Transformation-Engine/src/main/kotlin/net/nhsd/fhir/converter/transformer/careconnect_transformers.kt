package net.nhsd.fhir.converter.transformer


import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.PositiveIntType as R3PositiveIntType
import org.hl7.fhir.dstu3.model.UnsignedIntType as R3UnsignedIntType
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept
import org.hl7.fhir.r4.model.Coding as R4Coding
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.UnsignedIntType as R4UnsignedIntType

internal const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1"

internal const val CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatusReason-1"

internal const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"

internal const val STU3_SCTDEESCID_URL = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid"

internal const val STU3_STATUSCHANGEDATE_URL =
    "http://fhir.nhs.uk/fhir/3.0/StructureDefinition/extension-statusChangeDate"

internal const val CARECONNECT_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationStatementLastIssueDate-1"
internal const val CARECONNECT_GPC_LAST_ISSUE_DATE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatementLastIssueDate-1"

internal const val UKCORE_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationStatementLastIssueDate"

internal const val CARECONNECT_PRESCRIBING_AGENCY_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationPrescribingAgency-1"
internal const val CARECONNECT_GPC_PRESCRIBING_AGENCY_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescribingAgency-1"

internal const val UKCORE_PRESCRIBING_ORGANIZATION_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationPrescribingOrganization"


internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_REPEAT_INFORMATION_URL to ::repeatInformation,

    CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL to ::medicationStatusReason,

    CARECONNECT_GPC_LAST_ISSUE_DATE_URL to ::lastIssueDate,
    CARECONNECT_LAST_ISSUE_DATE_URL to ::lastIssueDate,

    CARECONNECT_PRESCRIBING_AGENCY_URL to ::prescribingAgency,
    CARECONNECT_GPC_PRESCRIBING_AGENCY_URL to ::prescribingAgency

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

fun medicationStatusReason(src: R3Extension, tgt: R4Resource) {

    src.getExtensionsByUrl("statusReason").firstOrNull()?.let { statusReason ->

        if (statusReason.value is R3CodeableConcept) {
            val srcCodeableConcept = statusReason.value as R3CodeableConcept
            val r3Coding = srcCodeableConcept.coding

            r3Coding.forEach {
                val r4Coding = R4Coding(it.system, it.code, it.display)

                if (it.hasUserSelected())
                    r4Coding.userSelected = it.userSelected

                (tgt as R4MedicationRequest).statusReason.coding.add(r4Coding)

                if (it.hasExtension()) {
                    val innerExtenstion =
                        it.extension.firstOrNull { it.url == "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid" }
                    tgt.addExtension(innerExtenstion?.let { innerExt -> buildStatusReasonExtensionsToCarryOver(innerExt) })
                }
            }
            (tgt as R4MedicationRequest).statusReason.text = srcCodeableConcept.text
        }
    }

    val ext = R4Extension().apply {
        url = STU3_STATUSCHANGEDATE_URL
        // Carry over remaining extensions
        src.getExtensionsByUrl("statusChangeDate").firstOrNull()?.let {
            val issuedExt = R4Extension().apply {
                url = "statusChangeDate"
                val v = R4DateTimeType((it.value as R3DateTimeType).asStringValue())
                setValue(v)
            }
            this.addExtension(issuedExt)
        }
    }
    tgt.addExtension(ext)

}

fun buildStatusReasonExtensionsToCarryOver(ext: R3Extension): R4Extension {

    val r4ext = R4Extension().apply {
        url = STU3_SCTDEESCID_URL

        ext.extension.forEach {
            val issuedExt = R4Extension().apply {
                url = it.url
                if (it.url.equals("descriptionDisplay")) {
                    val strg = StringType()
                    strg.value = it.value.toString()
                    setValue(strg)
                } else {
                    val idt = IdType()
                    idt.value = it.value.toString()
                    setValue(idt)
                }
            }
            this.addExtension(issuedExt)
        }
    }
    return r4ext
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

fun prescribingAgency(src: R3Extension, tgt: R4Resource) {
    val ext = R4Extension().apply {
        url = UKCORE_PRESCRIBING_ORGANIZATION_URL

        if (src.value is R3CodeableConcept) {
            val srcCodeableConcept = src.value as R3CodeableConcept
            val r3Coding = srcCodeableConcept.coding.firstOrNull()

            r3Coding?.let {
                val r3CodingSystem =
                    r3Coding.hasSystem() && r3Coding.system == "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-PrescribingAgency-1"

                val r4CodingSystem =
                    if (r3CodingSystem)
                        "https://fhir.hl7.org.uk/CodeSystem/UKCore-MedicationPrescribingOrganization"
                    else null

                val r4Coding = R4Coding(r4CodingSystem, it.code, it.display)

                val tgtCodeableConcept = R4CodeableConcept()

                tgtCodeableConcept.coding = listOf(r4Coding)
                if (srcCodeableConcept.hasText()) {
                    tgtCodeableConcept.text = srcCodeableConcept.text
                }

                setValue(tgtCodeableConcept)
            }
        }
    }

    tgt.addExtension(ext)

}
