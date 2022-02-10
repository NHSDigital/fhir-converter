package net.nhsd.fhir.converter.transformer

data class InputOutput(val input: String, val output: String)

fun loadExample(resource: String, extensionName: String): List<InputOutput> {
    val filename = { no: Int -> "${resource}${extensionName}-Extension-3to4_${no}.json" }
    val inputDir = "careconnect-to-ukcore/${resource.lowercase()}/input"
    val outputDir = "careconnect-to-ukcore/${resource.lowercase()}/expected"



    return listOf(InputOutput("f", "ds"))
}

class ExampleLoader {

}
