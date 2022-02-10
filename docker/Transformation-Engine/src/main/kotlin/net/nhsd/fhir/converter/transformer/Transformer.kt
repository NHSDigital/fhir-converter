package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.instance.model.api.IBaseResource

interface Transformer<S : IBaseResource, T : IBaseResource> {
    fun transform(src: S, tgt: T)
}
