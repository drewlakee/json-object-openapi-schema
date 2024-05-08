package io.github.joss.adapters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.github.joss.adapters.exceptions.JsonIsEmptyObject
import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.ConsoleSwaggerSchemaOutputStream
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
import io.github.joss.adapters.swagger.definitions.*
import io.github.joss.adapters.yaml.pojo.*

open class JsonSwaggerSchemaV3Adapter(
    private val outputStream: SwaggerSchemaOutputStream = ConsoleSwaggerSchemaOutputStream(),
    private val jsonMapper: ObjectMapper = ObjectMapper(),
    private val yamlMapper: ObjectMapper = ObjectMapper(
        YAMLFactory()
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    )
): JsonSwaggerSchemaAdapter {

    override fun convert(json: String) {
        val node = parse(json)

        if (!node.isObject) {
            throw JsonIsNotAnObjectException()
        } else if (node.size() == 0) {
            throw JsonIsEmptyObject()
        }

        val schemaProperties = getSchemaProperties(node)
        val yamlSchema = convertSchemaProperties(schemaProperties)

        outputStream.flush(yamlSchema)
    }

    private fun convertSchemaProperties(properties: List<PropertyDefinition>): String {
        val schemaProperties = properties.map {
            when (it) {
                is ArrayDefinition -> TODO()
                is FieldDefinition -> Pair(
                    it.fieldName,
                    when (it.type) {
                        PropertyType.STRING -> StringProperty()
                        PropertyType.INTEGER -> IntegerProperty()
                        PropertyType.NUMBER -> NumberProperty()
                        PropertyType.BOOLEAN -> BooleanProperty()
                        PropertyType.ARRAY -> TODO()
                        PropertyType.OBJECT -> TODO()
                    }
                )

                is ObjectDefinition -> TODO()
            }
        }.toList()

        val obj = Pair("Schema", ObjectProperty(properties = schemaProperties.toMap()))
        val schema = mapOf(obj)
        return yamlMapper.writeValueAsString(schema).trimIndent()
    }

    private fun getSchemaProperties(node: JsonNode): List<PropertyDefinition> {
        val fieldNames = node.fieldNames()
        return node.elements()
            .asSequence()
            .map { getPropertyDefinition(it, fieldNames.next()) }
            .toList()
    }

    private fun getPropertyDefinition(node: JsonNode, fieldName: String): PropertyDefinition {
        return when {
            node.isNumber && (node.isLong || node.isInt || node.isShort) ->
                FieldDefinition(fieldName, PropertyType.INTEGER)
            node.isNumber -> FieldDefinition(fieldName, PropertyType.NUMBER)
            node.isBoolean -> FieldDefinition(fieldName, PropertyType.BOOLEAN)
            node.isTextual -> FieldDefinition(fieldName, PropertyType.STRING)
            else -> throw UnsupportedOperationException("Unknown type ${node.nodeType}")
        }
    }

    private fun parse(json: String): JsonNode = try {
        jsonMapper.readTree(json)
    } catch (e: Exception) {
        throw JsonIsNotAnObjectException()
    }
}