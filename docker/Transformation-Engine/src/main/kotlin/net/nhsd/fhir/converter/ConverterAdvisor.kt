package net.nhsd.fhir.converter

import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_40
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.Extension as R4Extension

class ConverterAdvisor : BaseAdvisor_30_40() {
    companion object {
        private val extToTransformFunc: HashMap<String, (src: String) -> String> = hashMapOf()
    }

    override fun ignoreExtension(path: String, ext: R4Extension): Boolean {

        return true
    }

    override fun ignoreExtension(path: String, ext: R3Extension): Boolean {

        return true
    }

    override fun handleExtension(path: String, src: R4Extension, tgt: R3Extension) {
        super.handleExtension(path, src, tgt)
    }

    override fun handleExtension(path: String, src: R3Extension, tgt: R4Extension) {
        super.handleExtension(path, src, tgt)
    }
}
