package net.nhsd.fhir.converter.transformer

import com.fasterxml.jackson.databind.ObjectMapper
import net.nhsd.fhir.converter.ConverterConfiguration
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.MedicationRequest

/** A data class to hold a matching input resource and output resource
 * In a test scenario, it is expected that passing the input will produce the output
 * */
data class R3InputR4Output(private val r3: IBaseResource, private val r4: IBaseResource)


data class InputOutput(val input: String, val output: String)

class ExampleLoader<T : IBaseResource>(
    private val inResClass: Class<T>,
    private val outResClass: Class<MedicationRequest>
) {
    companion object {
        private val CONFIGURATION = ConverterConfiguration()

        private val R3_CONTEXT = CONFIGURATION.r3FhirContext()
        private val R4_CONTEXT = CONFIGURATION.r4FhirContext()

        private val R3_JSON_PARSER = CONFIGURATION.r3JsonParser(R3_CONTEXT)
        private val R4_JSON_PARSER = CONFIGURATION.r4JsonParser(R4_CONTEXT)

        private val CONVERTER = CONFIGURATION.converter30To40()

        fun convert(src: IBaseResource, tgt: IBaseResource) {

        }

    }

    fun parseBulk(pairs: List<InputOutput>): List<R3InputR4Output> {
        return pairs
            .map {
                val i = R3_JSON_PARSER.parseResource(inResClass, it.input)
                val o = R4_JSON_PARSER.parseResource(outResClass, it.output)

                R3InputR4Output(i, o)
            }
    }

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
}

private val om = ObjectMapper()

/** Compares two json values */
fun assertJsonEquals(input: String, output: String) {
    val i = om.readTree(input)
    val o = om.readTree(output)
    assert(i == o)
}
