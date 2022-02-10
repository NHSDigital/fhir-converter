package net.nhsd.fhir.converter

import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.Extension as R4Extension

fun repeatInformation(src: R3Extension, tgt: R4Extension) {
//    src.copyValues(tgt as Element)


}

class ConverterAdvisor : BaseAdvisor_30_40() {
    companion object {
//        private val resources = listOf<String>(
//            MedicationRequest::class.java.simpleName
//        )

        private val extToTransformFunc: HashMap<String, (src: R3Extension, tgt: R4Extension) -> Unit> = hashMapOf(
            "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation" to ::repeatInformation
        )
    }

    override fun useAdvisorForExtension(path: String, ext: R4Extension): Boolean {
        return true
    }

    override fun useAdvisorForExtension(path: String, ext: R3Extension): Boolean {
//        return resources.contains(path) && extToTransformFunc.containsKey(ext.url)
        return extToTransformFunc.containsKey(ext.url)
    }

    override fun handleExtension(path: String, src: R4Extension, tgt: R3Extension) {
        super.handleExtension(path, src, tgt)
    }

    override fun handleExtension(path: String, src: R3Extension, tgt: R4Extension) {
        val transformer = extToTransformFunc[src.url]
        if (transformer != null) {
            transformer(src, tgt)
        }
    }
}
