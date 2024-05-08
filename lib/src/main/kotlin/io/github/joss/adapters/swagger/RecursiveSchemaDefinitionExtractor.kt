package io.github.joss.adapters.swagger

import com.fasterxml.jackson.databind.JsonNode
import io.github.joss.adapters.swagger.definitions.FieldDefinition
import io.github.joss.adapters.swagger.definitions.ObjectDefinition
import io.github.joss.adapters.swagger.definitions.PropertyDefinition
import io.github.joss.adapters.swagger.definitions.PropertyType

class RecursiveSchemaDefinitionExtractor: SchemaDefinitionExtractor {

    override fun getObjectDefinitions(node: JsonNode): DefinitionsExtract {
        val fieldNames = node.fieldNames()
        val definitions = node.elements()
            .asSequence()
            .map { getPropertyDefinition(it, fieldNames.next()) }
            .toList()

        return DefinitionsExtract(definitions)
    }

    private fun getPropertyDefinition(node: JsonNode, fieldName: String): PropertyDefinition {
        return when {
            node.isNumber && (node.isLong || node.isInt || node.isShort) ->
                FieldDefinition(fieldName, PropertyType.INTEGER)
            node.isNumber -> FieldDefinition(fieldName, PropertyType.NUMBER)
            node.isBoolean -> FieldDefinition(fieldName, PropertyType.BOOLEAN)
            node.isTextual -> FieldDefinition(fieldName, PropertyType.STRING)
            node.isObject -> getObjectPropertyDefinition(fieldName, node)
            else -> throw UnsupportedOperationException("Unsupported type of json node [${node.nodeType}] for \"$fieldName\"=${node}")
        }
    }

    private fun getObjectPropertyDefinition(fieldName: String, node: JsonNode): ObjectDefinition {
        val fieldNames = node.fieldNames()
        return ObjectDefinition(
            fieldName,
            node.elements()
                .asSequence()
                .map { getPropertyDefinition(it, fieldNames.next()) }
                .toList()
        )
    }
}