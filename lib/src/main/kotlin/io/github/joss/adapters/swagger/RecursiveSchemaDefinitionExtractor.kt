package io.github.joss.adapters.swagger

import com.fasterxml.jackson.databind.JsonNode
import io.github.joss.adapters.exceptions.JsonEmptyArrayException
import io.github.joss.adapters.exceptions.JsonGenericArrayTypeException
import io.github.joss.adapters.swagger.definitions.*

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
            node.isArray -> getArrayPropertyDefinition(fieldName, node)
            else -> throw UnsupportedOperationException("Unsupported type of json node [${node.nodeType}]: \"$fieldName\"=$node")
        }
    }

    private fun getArrayPropertyDefinition(fieldName: String, node: JsonNode): ArrayDefinition {
        if (node.size() == 0) {
            throw JsonEmptyArrayException("Json node is expected to be not empty: \"$fieldName[]\"=$node")
        }

        val elementsIterator = node.elements()
        val firstElement = elementsIterator.next()
        val firstElementPropertyDefinition = getPropertyDefinition(firstElement, fieldName)
        while (elementsIterator.hasNext()) {
            val secondElement = elementsIterator.next()
            val element = getPropertyDefinition(secondElement, fieldName)

            if (firstElementPropertyDefinition != element) {
                val firstTypeIsObject = firstElementPropertyDefinition.type() == PropertyType.OBJECT
                val secondTypeIsObject = element.type() == PropertyType.OBJECT
                if (firstTypeIsObject && secondTypeIsObject) {
                    throw JsonGenericArrayTypeException(
                        "Json array node \"$fieldName[]\" expected to be strongly typed, expected [(OBJECT) $firstElement] but there's also [(OBJECT) $secondElement]"
                    )
                }

                throw JsonGenericArrayTypeException(
                    "Json array node expected to be strongly typed, " +
                            "expected only [${firstElementPropertyDefinition.type()}] but there's also [${element.type()}]:" +
                            " \"$fieldName[]\"=$node"
                )
            }
        }

        return ArrayDefinition(fieldName, firstElementPropertyDefinition)
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