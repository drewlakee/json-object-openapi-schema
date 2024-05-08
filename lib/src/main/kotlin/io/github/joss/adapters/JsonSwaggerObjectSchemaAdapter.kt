package io.github.joss.adapters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.joss.adapters.exceptions.JsonIsEmptyObject
import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.ConsoleSchemaOutputStream
import io.github.joss.adapters.output.SchemaOutputStream
import io.github.joss.adapters.swagger.RecursiveSchemaDefinitionExtractor
import io.github.joss.adapters.swagger.SchemaDefinitionExtractor
import io.github.joss.adapters.swagger.yaml.RecursiveYamlObjectAdapter
import io.github.joss.adapters.swagger.yaml.YamlObjectAdapter

open class JsonSwaggerObjectSchemaAdapter(
    private val outputStream: SchemaOutputStream = ConsoleSchemaOutputStream(),
    private val jsonMapper: ObjectMapper = ObjectMapper(),
    private val objectDefinitionExtractor: SchemaDefinitionExtractor = RecursiveSchemaDefinitionExtractor(),
    private val yamlObjectAdapter: YamlObjectAdapter = RecursiveYamlObjectAdapter()
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObjectException()
        } else if (node.size() == 0) {
            throw JsonIsEmptyObject()
        }

        val objectDefinitions = objectDefinitionExtractor.getObjectDefinitions(node).propertyDefinitions
        val yamlObjectSchema = yamlObjectAdapter.convert(objectDefinitions)

        outputStream.flush(yamlObjectSchema)
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObjectException()
    }
}