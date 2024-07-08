package com.sridhar

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.PrintWriter

fun main() {

    val filePath = Parser::class.java.getResource("/4gb-data.json")?.path ?: ""

    val parser = Parser()

    //merge, giving two 4gb files
    val files = listOf(
        File(filePath),
        File(filePath)
    )
    val outputFile = File("/file/path/to/8gb-data.json")
    parser.merge(files, outputFile)

    //stream and check the total processed matching
    parser.stream(outputFile)
}

class Parser {

    fun stream(jsonFile: File) {
        val objectMapper = ObjectMapper()
        val jFactory = JsonFactory()
        var totalProcessed = 0
        jFactory.createParser(jsonFile).use {
            it.nextToken()
            while (it.nextToken() != JsonToken.END_ARRAY) {
                objectMapper.readValue(it, CountryPopulation::class.java)
                totalProcessed++
            }
        }
        println("total process: $totalProcessed")
    }

    fun merge(files: List<File>, outputFile: File) {
        val objectMapper = ObjectMapper()
        val jFactory = JsonFactory()
        var totalProcessed = 0
        outputFile.delete()
        outputFile.createNewFile()
        PrintWriter(outputFile).use { writer ->
            var writeAlready = false
            writer.write("[")
            files.map { file ->
                jFactory.createParser(file).use {
                    it.nextToken()
                    var token = it.nextToken()
                    while (token != JsonToken.END_ARRAY) {
                        val obj = objectMapper.readValue(it, CountryPopulation::class.java)
                        if (writeAlready) {
                            writer.write(",")
                        }
                        writer.write("\n")
                        writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
                        token = it.nextToken()
                        totalProcessed++
                        writeAlready = true
                    }
                }
            }
            writer.write("\n")
            writer.write("]")
        }
        println("total processed: $totalProcessed")
    }

}

data class CountryPopulation(
    val country: String,
    val population: Int,
) {
    constructor() : this("", 0)
}