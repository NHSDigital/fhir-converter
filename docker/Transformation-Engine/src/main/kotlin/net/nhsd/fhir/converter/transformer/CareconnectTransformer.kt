package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.DomainResource as R4Resource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4Resource) -> Unit

@Component
class CareconnectTransformer(private val extensionsMap: HashMap<String, ExtensionTransformer> = careconnectTransformers) :
    Transformer {

    override fun transform(src: IBaseResource, tgt: IBaseResource): IBaseResource {
        return when (src) {
            is R3Resource -> {
                when (tgt) {
                    is R4Resource -> {
                        src.extension
                            .filter { extensionsMap.containsKey(it.url) }
                            .also { tgt.extension.removeIf { extensionsMap.containsKey(it.url) } }
                            .forEach { extensionsMap[it.url]?.invoke(it, tgt) }

                        tgt
                    }
                    else -> tgt
                }
            }
            else -> tgt
        }
    }
}
