package net.nhsd.fhir.converter.transformer

import com.fasterxml.jackson.databind.ObjectMapper
import net.nhsd.fhir.converter.Converter
import net.nhsd.fhir.converter.ConverterConfiguration
import net.nhsd.fhir.converter.FhirParser
import net.nhsd.fhir.converter.service.ConverterService
import org.assertj.core.api.Assertions.assertThat
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.dstu3.model.DomainResource as R3Resource
import org.hl7.fhir.r4.model.DomainResource as R4Resource

/** A data class to hold a matching input resource and output resource
 * In a test scenario, it is expected that passing the input will produce the output
 * */
data class R3InputR4Output(private val r3: IBaseResource, private val r4: IBaseResource)

data class InputOutput(val input: String, val output: String)

private val CONFIGURATION = ConverterConfiguration()

private val R3_CONTEXT = CONFIGURATION.r3FhirContext()
private val R4_CONTEXT = CONFIGURATION.r4FhirContext()

private val R3_JSON_PARSER = CONFIGURATION.r3JsonParser(R3_CONTEXT)
private val R4_JSON_PARSER = CONFIGURATION.r4JsonParser(R4_CONTEXT)

private val CONVERTER = CONFIGURATION.converter30To40()

private val OM = ObjectMapper()

/** Compares two json (String) values. NOTE: USE ONLY FOR DEBUG. Do not use this in tests. Even if two json objects are equal the difference in order will
 * cause a failure. It only useful since, intellij will show you a diff of strings, which makes seeing the difference a lot easier */
fun assertJsonEquals(actual: String, expected: String) {
    val act = OM.readTree(actual)
    val exp = OM.readTree(expected)

    assertThat(act.toPrettyString()).isEqualTo(exp.toPrettyString())
}

/** Use this to create an instance of ConverterService for component tests
 * We could have used Spring test context but this is much faster
 */
fun makeConverterService(): ConverterService {
    val fhirParser = FhirParser(
        r3JsonParser = R3_JSON_PARSER,
        r4JsonParser = R4_JSON_PARSER,
        r3XmlParser = CONFIGURATION.r3XmlParser(R3_CONTEXT),
        r4XmlParser = CONFIGURATION.r4XmlParser(R4_CONTEXT),
    )

    return ConverterService(
        fhirParser = fhirParser,
        careconnectTransformer = CareconnectTransformer(),
        converter = Converter(CONVERTER)
    )
}

class CareconnectExampleLoader {

    /** Returns a list of matching input/output json files (see InputOutput class)
     * For each FHIR Resource there are a number of example files. These files are organised in pairs. One is the input and
     * the other is the expected output after conversions.
     * This function receives the resource name and the extension name and returns a list of InputOutput objects
     */
    fun <I : R3Resource> loadExample(
        inputResCls: Class<I>,
        extensionName: String
    ): MutableList<InputOutput> {
        val resourceName = inputResCls.simpleName

        val filename = { no: String -> "${resourceName}${extensionName}-Extension-3to4_${no}.json" }
        val dir = "careconnect-to-ukcore/${resourceName.lowercase()}"
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

    private fun <I : R3Resource, O : R4Resource> parseBulk(
        pairs: List<InputOutput>,
        inputResCls: Class<I>,
        outputResCls: Class<O>
    ): List<R3InputR4Output> =
        pairs.map {
            val i = R3_JSON_PARSER.parseResource(inputResCls, it.input)
            val o = R4_JSON_PARSER.parseResource(outputResCls, it.output)

            R3InputR4Output(i, o)
        }
}

