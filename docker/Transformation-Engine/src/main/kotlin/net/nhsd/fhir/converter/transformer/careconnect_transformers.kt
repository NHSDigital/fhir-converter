package net.nhsd.fhir.converter.transformer


import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept
import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.PositiveIntType as R3PositiveIntType
import org.hl7.fhir.dstu3.model.Reference as R3Reference
import org.hl7.fhir.dstu3.model.StringType as R3StringType
import org.hl7.fhir.dstu3.model.UnsignedIntType as R3UnsignedIntType
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.CodeableConcept as R4CodeableConcept
import org.hl7.fhir.r4.model.Coding as R4Coding
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.Reference as R4Reference
import org.hl7.fhir.r4.model.StringType as R4StringType
import org.hl7.fhir.r4.model.UnsignedIntType as R4UnsignedIntType

internal const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1"

internal const val CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatusReason-1"

internal const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"

internal const val UKCORE_SCTDEESCID_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-CodingSCTDescId"

internal const val STU3_STATUSCHANGEDATE_URL =
    "http://fhir.nhs.uk/fhir/3.0/StructureDefinition/extension-statusChangeDate"

internal const val CARECONNECT_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationStatementLastIssueDate-1"
internal const val CARECONNECT_GPC_LAST_ISSUE_DATE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatementLastIssueDate-1"

internal const val UKCORE_LAST_ISSUE_DATE_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationStatementLastIssueDate"

internal const val CARECONNECT_GPC_PRESCRIPTION_TYPE_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescriptionType-1"
internal const val CARECONNECT_PRESCRIPTION_TYPE_URL =
    //TODO: is there non GPC url for this extension?
    ""

internal const val CARECONNECT_PRESCRIBING_AGENCY_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationPrescribingAgency-1"
internal const val CARECONNECT_GPC_PRESCRIBING_AGENCY_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescribingAgency-1"

internal const val UKCORE_PRESCRIBING_ORGANIZATION_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-MedicationPrescribingOrganization"

internal const val CARECONNECT_CHANGE_SUMMARY_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationChangeSummary-1"

internal const val CARECONNECT_ALLERGY_ASSOCIATED_ENCOUNTER_URL =
    "http://hl7.org/fhir/StructureDefinition/encounter-associatedEncounter"

internal const val CARECONNECT_ALLERGY_INTOLERANCE_END_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-AllergyIntoleranceEnd-1"
internal const val UKCORE_ALLERGY_INTOLERANCE_END_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-AllergyIntoleranceEnd"

internal const val CARECONNECT_EVIDENCE_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-Evidence-1"
internal const val UKCORE_EVIDENCE_URL =
    "https://fhir.hl7.org.uk/StructureDefinition/Extension-UKCore-Evidence"


internal const val CARECONNECT_DOSAGE_LAST_CHANGE_URL =
    "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationDosageLastChanged-1"

internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_PRESCRIPTION_TYPE_URL to ::prescriptionType,
    CARECONNECT_PRESCRIPTION_TYPE_URL to ::prescriptionType,
    CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL to ::medicationStatusReason,
    CARECONNECT_GPC_LAST_ISSUE_DATE_URL to ::lastIssueDate,
    CARECONNECT_LAST_ISSUE_DATE_URL to ::lastIssueDate,
    CARECONNECT_PRESCRIBING_AGENCY_URL to ::prescribingAgency,
    CARECONNECT_GPC_PRESCRIBING_AGENCY_URL to ::prescribingAgency,
    CARECONNECT_CHANGE_SUMMARY_URL to ::changeSummary,
    CARECONNECT_EVIDENCE_URL to ::evidence,
    CARECONNECT_ALLERGY_ASSOCIATED_ENCOUNTER_URL to ::associatedEncounter,
    CARECONNECT_ALLERGY_INTOLERANCE_END_URL to ::allergyIntoleranceEnd,
    CARECONNECT_DOSAGE_LAST_CHANGE_URL to ::dosageLastChange,
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

            val r4Coding = R4Coding(r4CodingSystem, r3CodingCode, r3CodingDisplay)

            (tgt as R4MedicationRequest).courseOfTherapyType.coding = listOf(r4Coding)
        }
    }
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

                if (it.hasExtension()) {
                    val innerExtenstion =
                        it.extension.firstOrNull { it.url == "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid" }

                    r4Coding.extension.add(innerExtenstion?.let { innerExt ->
                        buildStatusReasonExtensionsToCarryOver(
                            innerExt
                        )
                    })
                }

                (tgt as R4MedicationRequest).statusReason.coding.add(r4Coding)

            }
            (tgt as R4MedicationRequest).statusReason.text = srcCodeableConcept.text
        }
    }

    val statusChangeDateExt = R4Extension().apply {
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

    tgt.addExtension(statusChangeDateExt)

}

fun buildStatusReasonExtensionsToCarryOver(ext: R3Extension): R4Extension {

    val r4ext = R4Extension().apply {
        url = UKCORE_SCTDEESCID_URL

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

fun changeSummary(src: R3Extension, tgt: R4Resource) {
    tgt
}

fun dosageLastChange(src: R3Extension, tgt: R4Resource) {
    tgt
}

fun evidence(src: R3Extension, tgt: R4Resource) {
    val ext = R4Extension().apply {
        url = UKCORE_EVIDENCE_URL

        if (src.value is R3Reference) {
            val r3EvidenceReference = (src.value as R3Reference).reference

            val r4EvidenceReference = R4Reference(r3EvidenceReference)
            this.setValue(r4EvidenceReference)
        }
    }
    tgt.addExtension(ext)
}

fun associatedEncounter(src: R3Extension, tgt: R4Resource) {
    if (src.value is R3Reference) {
        val associatedEncounterReference = (src.value as R3Reference).reference

        (tgt as R4AllergyIntolerance).encounter.reference = associatedEncounterReference
    }
}

fun allergyIntoleranceEnd(src: R3Extension, tgt: R4Resource) {
    val ext = R4Extension().apply {
        url = UKCORE_ALLERGY_INTOLERANCE_END_URL

        src.getExtensionsByUrl("endDate").firstOrNull()?.let {
            val endDateExt = R4Extension().apply {
                url = "endDate"
                val newValue = R4DateTimeType((it.value as R3DateTimeType).asStringValue())
                setValue(newValue)
            }
            this.addExtension(endDateExt)
        }

        src.getExtensionsByUrl("reasonEnded").firstOrNull()?.let {
            val reasonEndedExt = R4Extension().apply {
                url = "reasonEnded"
                val newValue = R4StringType((it.value as R3StringType).asStringValue())
                setValue(newValue)
            }
            this.addExtension(reasonEndedExt)
        }
    }
    tgt.addExtension(ext)
}
