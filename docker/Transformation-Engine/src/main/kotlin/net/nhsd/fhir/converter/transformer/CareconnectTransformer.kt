package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.DomainResource as R4Resource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4Resource) -> Unit

@Component
class CareconnectTransformer(private val extensionsMap: HashMap<String, ExtensionTransformer> = careconnectTransformers) :
    Transformer {

    override fun transform(source: IBaseResource, target: IBaseResource): IBaseResource {
        return if (source is R3Resource && target is R4Resource) {
            applyTransformers(source.extension, target)
            if (source is R3AllergyIntolerance) {
                applyTransformers(source.code.extension, target)
            }
            target

        } else {
            target
        }
    }

    private fun applyTransformers(extensions: List<R3Extension>, target: R4Resource) {
        extensions
            .filter { extensionsMap.containsKey(it.url) }
            .also { target.extension.removeIf { extensionsMap.containsKey(it.url) } }
            .forEach { extensionsMap[it.url]?.invoke(it, target) }
    }
}
