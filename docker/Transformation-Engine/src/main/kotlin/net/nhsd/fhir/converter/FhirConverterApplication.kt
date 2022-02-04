package net.nhsd.fhir.converter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FhirConverterApplication

fun main(args: Array<String>) {
	runApplication<FhirConverterApplication>(*args)
}
