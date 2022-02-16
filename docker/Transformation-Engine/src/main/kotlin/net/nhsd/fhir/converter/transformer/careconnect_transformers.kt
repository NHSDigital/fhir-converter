package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.dstu3.model.Coding
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.dstu3.model.DateTimeType as R3DateTimeType
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.dstu3.model.PositiveIntType as R3PositiveIntType
import org.hl7.fhir.dstu3.model.UnsignedIntType as R3UnsignedIntType
import org.hl7.fhir.r4.model.DateTimeType as R4DateTimeType
import org.hl7.fhir.r4.model.DomainResource as R4Resource
import org.hl7.fhir.r4.model.Extension as R4Extension
import org.hl7.fhir.r4.model.MedicationRequest as R4MedicationRequest
import org.hl7.fhir.r4.model.UnsignedIntType as R4UnsignedIntType
import org.hl7.fhir.r4.model.Coding as R4Coding
import org.hl7.fhir.dstu3.model.CodeableConcept as R3CodeableConcept

internal const val CARECONNECT_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-MedicationRepeatInformation-1"
internal const val CARECONNECT_GPC_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1"

internal const val CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL =
    "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatusReason-1"

internal const val UKCORE_REPEAT_INFORMATION_URL =
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation"

internal const val STU3_SCTDEESCID_URL = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid"

internal const val STU3_STATUSCHANGEDATE_URL = "http://fhir.nhs.uk/fhir/3.0/StructureDefinition/extension-statusChangeDate"

internal val careconnectTransformers: HashMap<String, ExtensionTransformer> = hashMapOf(
    CARECONNECT_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_REPEAT_INFORMATION_URL to ::repeatInformation,
    CARECONNECT_GPC_MEDICATION_STATUS_REASON_URL to :: medicationStatusReason
)

fun repeatInformation(src: R3Extension, tgt: R4Resource) {
    if (src.url != CARECONNECT_REPEAT_INFORMATION_URL && src.url != CARECONNECT_GPC_REPEAT_INFORMATION_URL) return

    val ext = R4Extension().apply {
        url = UKCORE_REPEAT_INFORMATION_URL

        src.getExtensionsByUrl("numberOfRepeatPrescriptionsIssued").firstOrNull()?.let {
            val issuedExt = R4Extension().apply {
                url = "numberOfRepeatPrescriptionsIssued"
                val v = R4UnsignedIntType((it.value as R3UnsignedIntType).asStringValue())
                setValue(v)
            }
            this.addExtension(issuedExt)
        }

        src.getExtensionsByUrl("authorisationExpiryDate").firstOrNull()?.let {
            val expiry = R4Extension().apply {
                url = "authorisationExpiryDate"
                val v = R4DateTimeType((it.value as R3DateTimeType).asStringValue())
                setValue(v)
            }
            this.addExtension(expiry)
        }

        src.getExtensionsByUrl("numberOfRepeatPrescriptionsAllowed").firstOrNull()?.let {
            val v = when (it.value) {
                is R3PositiveIntType -> (it.value as R3PositiveIntType).value as Int
                is R3UnsignedIntType -> (it.value as R3UnsignedIntType).value as Int
                else -> throw IllegalStateException("Unsupported data type passed to transformer. This happened during transforming \"numberOfRepeatPrescriptionsAllowed\". The DataType is ${it.javaClass.name}")
            }
            (tgt as R4MedicationRequest).dispenseRequest.numberOfRepeatsAllowed = v
        }
    }

    tgt.addExtension(ext)
}












































fun medicationStatusReason(src: R3Extension, tgt: R4Resource) {

    src.getExtensionsByUrl("statusReason").firstOrNull()?.let { statusReason ->

        if (statusReason.value is R3CodeableConcept) {
            val srcCodeableConcept = statusReason.value as R3CodeableConcept
            val r3Coding = srcCodeableConcept.coding

            r3Coding.forEach{
                val r4Coding = R4Coding(it.system, it.code, it.display)

                if (it.hasUserSelected())
                    r4Coding.userSelected = it.userSelected

                (tgt as R4MedicationRequest).statusReason.coding.add(r4Coding)

                if (it.hasExtension()){
                    val innerExtenstion = it.extension.firstOrNull{it.url == "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-coding-sctdescid"}
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

        ext.extension.forEach{
            val issuedExt = R4Extension().apply {
                url = it.url
                if (it.url.equals("descriptionDisplay")){
                    val strg = StringType()
                    strg.value = it.value.toString()
                    setValue(strg)
                }else {
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
