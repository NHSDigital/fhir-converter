package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.dstu3.model.AllergyIntolerance
import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component
import org.hl7.fhir.dstu3.model.AllergyIntolerance as R3AllergyIntolerance
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.dstu3.model.Bundle as R3Bundle
import org.hl7.fhir.dstu3.model.Extension as R3Extension
import org.hl7.fhir.r4.model.Bundle as R4Bundle
import org.hl7.fhir.r4.model.AllergyIntolerance as R4AllergyIntolerance
import org.hl7.fhir.r4.model.DomainResource as R4Resource

typealias ExtensionTransformer = (src: R3Extension, tgt: R4Resource) -> Unit

@Component
class CareconnectTransformer(private val extensionsMap: HashMap<String, ExtensionTransformer> = careconnectTransformers) :
    Transformer {

    override fun transform(source: IBaseResource, target: IBaseResource): IBaseResource {
        // Check if incoming resource is a Bundle. if bundle go through each entry and call transformer
        return if (source is R3Bundle && target is R4Bundle) {
            for (i in source.entry.indices) {
                handleResourceExtensionTransformations(source.entry[i].resource, target.entry[i].resource)
            }
            target
        } else {
            handleResourceExtensionTransformations(source, target)
        }
    }

    fun handleResourceExtensionTransformations(source: IBaseResource, target: IBaseResource): IBaseResource {
        return if (source is R3Resource && target is R4Resource) {
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

    fun handleAllergyIntolerance(source: R3AllergyIntolerance, target: R4AllergyIntolerance) {
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
