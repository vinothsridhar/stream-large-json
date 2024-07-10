package com.sridhar.core

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.PrintWriter

class Parser {

    fun <T> stream(jsonFile: File, cls: Class<T>, callback: (obj: T) -> Unit = {}) {
        val objectMapper = ObjectMapper()
        val jFactory = JsonFactory()
        var totalProcessed = 0
        jFactory.createParser(jsonFile).use {
            it.nextToken()
            while (it.nextToken() != JsonToken.END_ARRAY) {
                val obj = objectMapper.readValue(it, cls)
                callback(obj)
                totalProcessed++
            }
        }
        println("total process: $totalProcessed")
    }

    fun <T> merge(files: List<File>, outputFile: File, cls: Class<T>) {
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
                        val obj = objectMapper.readValue(it, cls)
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