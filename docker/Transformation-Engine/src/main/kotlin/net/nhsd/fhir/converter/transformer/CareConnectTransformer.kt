package net.nhsd.fhir.converter.transformer

import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.DomainResource as R4Resource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4Resource) -> Unit

private val extToTransformFunc: HashMap<String, ExtensionTransformer> = hashMapOf(
    "https://fhir.nhs.uk/StructureDefinition/Extension-UKCore-MedicationRepeatInformation" to ::repeatInformation
)

@Component
class CareConnectTransformer(private val extensionsMap: HashMap<String, ExtensionTransformer> = extToTransformFunc) :
    Transformer<R3Resource, R4Resource> {

    override fun transform(src: R3Resource, tgt: R4Resource) {
        src.extension
            .filter { extensionsMap.containsKey(it.url) }
            .forEach { extensionsMap[it.url]?.invoke(it, tgt) }
    }
}
