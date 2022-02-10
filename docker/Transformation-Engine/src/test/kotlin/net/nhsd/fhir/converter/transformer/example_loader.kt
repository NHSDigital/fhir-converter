package net.nhsd.fhir.converter.transformer

import com.fasterxml.jackson.databind.ObjectMapper

private val om = ObjectMapper()

/** A data class to hold a matching input json resource and output json resource
 * In a test scenario, it is expected that passing the input will produce the output
 * */
data class InputOutput(val input: String, val output: String)


/** Returns a list of matching input/output json files (see InputOutput class)
 * For each FHIR Resource there are a number of example files. These files are organised in pairs. One is the input and
 * the other is the expected output after conversions.
 * This function receives the resource name and the extension name and returns a list of InputOutput objects
 */
fun loadExample(resource: String, extensionName: String): List<InputOutput> {
    val filename = { no: String -> "${resource}${extensionName}-Extension-3to4_${no}.json" }
    val dir = "careconnect-to-ukcore/${resource.lowercase()}"
    val padZeros = // converts 1 -> "001" i.e: pad 2,1 or 0 number of zeros to int
        { n: Int -> "${"0".repeat(3 - n.toString().length)}${n}" }

    val inputFile = { n: Int -> "${dir}/input/${filename(padZeros(n))}" }
    val outputFile = { n: Int -> "${dir}/expected/${filename(padZeros(n))}" }

    val resourceUri = { path: String -> {}.javaClass.classLoader.getResource(path) }

    // FIXME: start index is from 0 everywhere except for RepeatInformation!. Maybe get index from this function as argument
    var n = 1
    val pairs = mutableListOf<InputOutput>()
    do {
        val i = resourceUri(inputFile(n))
        val o = resourceUri(outputFile(n))
        if (i == null || o == null) break

        pairs.add(InputOutput(input = i.readText(), output = o.readText()))

        n++
    } while (resourceUri(inputFile(n)) != null)

    return pairs
}

/** Compares two json values */
fun assertJsonEquals(input: String, output: String) {
    val i = om.readTree(input)
    val o = om.readTree(output)
    assert(i == o)
}
