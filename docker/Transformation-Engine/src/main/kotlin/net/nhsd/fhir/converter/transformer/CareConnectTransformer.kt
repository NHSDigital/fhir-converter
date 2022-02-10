package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.dstu3.model.Resource as R3Resource
import org.hl7.fhir.r4.model.Resource as R4Resource

class CareConnectTransformer : Transformer<R3Resource, R4Resource> {
    override fun transform(src: R3Resource, tgt: R4Resource) {

    }
}
