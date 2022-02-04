package net.nhsd.fhir.converter.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class Status(
    var status: String? = null
)

@RestController
class StatusController {
    @GetMapping(path = ["/_status"])
    fun status() = Status("pass")
}
