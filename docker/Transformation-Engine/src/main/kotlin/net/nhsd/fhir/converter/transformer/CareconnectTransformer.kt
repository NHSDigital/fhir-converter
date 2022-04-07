package net.nhsd.fhir.converter.transformer


import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.ResourceType.Bundle as R4_BUNDLE
import org.hl7.fhir.r4.model.DomainResource as R4Resource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4Resource) -> Unit
typealias ProfileUrlTransformer = (tgtEntry: R4Resource) -> Unit

@Component
class CareconnectTransformer(private val extensionsMap: HashMap<String, ExtensionTransformer> = careconnectTransformers,
private val profileUrlMap: HashMap<String, ProfileUrlTransformer> = careconnectProfileUrlMap) :
    Transformer {

    override fun transform(src: IBaseResource, tgt: IBaseResource): IBaseResource {
        // Check if resource is Bundle. If bundle go through each entry and transform its extensions.


        return if (src is R3Resource && tgt is R4Resource) {
            // Go through each Resource and any contained resources.
            // For each resource check the targets profile url and change in place.
                // if bundle - do through each entry iteratively
                // else just transform resource.
//
//            if (tgt.resourceType == R4_BUNDLE) {
//                for (entry in ) {
//                    var url = entry.resource?.meta?.profile
//                    profileUrlMap[url]?.invoke(entry.resource)
//                }
//            }


            // sort extensions
            src.extension
                .filter { extensionsMap.containsKey(it.url) }
                .also { tgt.extension.removeIf { extensionsMap.containsKey(it.url) } }
                .forEach { extensionsMap[it.url]?.invoke(it, tgt) }

            tgt

        } else {
            tgt
        }
    }
}
