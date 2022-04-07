package net.nhsd.fhir.converter.transformer

import org.hl7.fhir.instance.model.api.IBaseResource
import org.springframework.stereotype.Component

@Component
interface Transformer {
    fun transform(source: IBaseResource, target: IBaseResource): IBaseResource
}
