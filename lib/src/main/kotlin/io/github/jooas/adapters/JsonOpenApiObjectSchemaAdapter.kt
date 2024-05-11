package io.github.jooas.adapters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jooas.adapters.exceptions.JsonEmptyObjectException
import io.github.jooas.adapters.exceptions.JsonIsNotAnObjectException
import io.github.jooas.adapters.input.JsonInputStream
import io.github.jooas.adapters.openapi.SchemaDefinitionExtractor
import io.github.jooas.adapters.openapi.yaml.YamlObjectAdapter
import io.github.jooas.adapters.output.SchemaOutputStream

open class JsonOpenApiObjectSchemaAdapter(
    private val jsonMapper: ObjectMapper,
    private val objectDefinitionExtractor: SchemaDefinitionExtractor,
    private val yamlObjectAdapter: YamlObjectAdapter,
): JsonOpenApiSchemaAdapter {

    override fun convert(json: String): String {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObjectException()
        } else if (node.size() == 0) {
            throw JsonEmptyObjectException()
        }

        val objectDefinitions = objectDefinitionExtractor.getObjectDefinitions(node).propertyDefinitions
        val yamlObjectSchema = yamlObjectAdapter.convert(objectDefinitions)

        return yamlObjectSchema
    }

    override fun convert(input: JsonInputStream, output: SchemaOutputStream) {
        output.flush(convert(input.get()))
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObjectException()
    }
}