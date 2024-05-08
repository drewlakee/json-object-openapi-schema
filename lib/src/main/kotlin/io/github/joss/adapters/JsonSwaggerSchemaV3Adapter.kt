package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.ConsoleSwaggerSchemaOutputStream
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.joss.adapters.definitions.*
import io.github.joss.adapters.exceptions.JsonIsEmptyObject

open class JsonSwaggerSchemaV3Adapter(
    private val outputStream: SwaggerSchemaOutputStream = ConsoleSwaggerSchemaOutputStream(),
    private val jsonMapper: ObjectMapper = ObjectMapper()
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObjectException()
        } else if (node.size() == 0) {
            throw JsonIsEmptyObject()
        }

        getSchemaProperties(node)

        outputStream.flush(json)
    }

    private fun getSchemaProperties(node: JsonNode): List<SwaggerSchemaPropertyDefinition> {
        return listOf()
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObjectException()
    }
}