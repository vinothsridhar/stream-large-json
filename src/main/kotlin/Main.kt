package com.sridhar

import com.sridhar.core.Parser
import java.io.File

fun main() {

    val filePath = Parser::class.java.getResource("/4gb-data.json")?.path ?: ""

    val parser = Parser()

    //merge, giving two 4gb files
    val files = listOf(
        File(filePath),
        File(filePath)
    )
    val outputFile = File("/file/path/to/8gb-data.json")
    parser.merge(files, outputFile, CountryPopulation::class.java)

    //stream and check the total processed matching
    parser.stream(outputFile, CountryPopulation::class.java) {
        println(it.country)
    }
}

data class CountryPopulation(
    val country: String,
    val population: Int,
) {
    constructor() : this("", 0)
}