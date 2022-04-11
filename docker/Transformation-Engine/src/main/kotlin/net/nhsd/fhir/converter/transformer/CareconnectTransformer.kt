package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.dstu3.model.AllergyIntolerance
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.DomainResource as R3DomainResource
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.DomainResource as R4DomainResource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4DomainResource) -> Unit
typealias ProfileUrlTransformer = (tgt: IBaseResource) -> Unit

@Component
class CareconnectTransformer(
    private val extensionsMap: HashMap<String, ExtensionTransformer> = careconnectTransformers,
    private val profileMap: HashMap<String, ProfileUrlTransformer> = careconnectProfileUrlMap
) : Transformer {

    override fun transform(source: IBaseResource, target: IBaseResource): IBaseResource {
        // Check if incoming resource is a Bundle. If bundle go through each entry and call transformers
        return if (source is R3Bundle && target is R4Bundle) {
            handleProfileUrlTransformations(source, target)

            for (i in source.entry.indices) {
                val sourceEntryResource = source.entry[i].resource
                val targetEntryResource = target.entry[i].resource
                handleResourceExtensionTransformations(sourceEntryResource, targetEntryResource)
                handleProfileUrlTransformations(sourceEntryResource, targetEntryResource)
            }
            target
        } else {
            handleResourceExtensionTransformations(source, target)
            handleProfileUrlTransformations(source, target)
        }
    }

    private fun handleProfileUrlTransformations(source: IBaseResource, target: IBaseResource): IBaseResource {
        val profile = source.meta.profile
        profile.filter { profileMap.containsKey(it.valueAsString) }
            .forEach { profileMap[it.valueAsString]?.invoke(target) }

        return target
    }

    fun handleResourceExtensionTransformations(source: IBaseResource, target: IBaseResource): IBaseResource {
        return if (source is R3DomainResource && target is R4DomainResource) {
            source.extension.filter { extensionsMap.containsKey(it.url) }
                .also { target.extension.removeIf { extensionsMap.containsKey(it.url) } }
                .forEach { extensionsMap[it.url]?.invoke(it, target) }

            if (source is AllergyIntolerance) {
                handleAllergyIntolerance(source, target as R4AllergyIntolerance)
            }

            target
        } else {
            target
        }
    }

    private fun handleAllergyIntolerance(source: R3AllergyIntolerance, target: R4AllergyIntolerance) {
        // Handle code.coding[*].extension with descriptionId and descriptionDisplay
        source.code?.coding?.forEachIndexed { index, coding ->
            val r3Ext = coding.getExtensionsByUrl(CARECONNECT_DESCRIPTION_ID_URL)?.firstOrNull()
            if (r3Ext != null) {
                val r4Ext = descriptionIdAndDisplay(r3Ext)
                target.code.coding[index].addExtension(r4Ext)
            }
        }
        // Remove uccore extensions that are carried over by base converter
        target.code?.coding?.forEach { coding -> coding.extension.removeIf { it.url == CARECONNECT_DESCRIPTION_ID_URL } }
    }
}
