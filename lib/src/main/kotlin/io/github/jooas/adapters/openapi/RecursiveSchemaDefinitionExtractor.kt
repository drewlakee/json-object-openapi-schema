package io.github.jooas.adapters.openapi

import com.fasterxml.jackson.databind.JsonNode
import io.github.jooas.adapters.Features
import io.github.jooas.adapters.exceptions.JsonEmptyArrayException
import io.github.jooas.adapters.exceptions.JsonGenericArrayTypeException
import io.github.jooas.adapters.openapi.definitions.*

class RecursiveSchemaDefinitionExtractor(
    private val features: Features,
) : SchemaDefinitionExtractor {
    override fun getObjectDefinitions(node: JsonNode): DefinitionsExtract {
        val fieldNames = node.fieldNames()
        val definitions =
            node.elements()
                .asSequence()
                .map { getPropertyDefinition(it, fieldNames.next()) }
                .toList()

        return DefinitionsExtract(definitions)
    }

    private fun getPropertyDefinition(
        node: JsonNode,
        fieldName: String,
    ): PropertyDefinition {
        return when {
            node.isNumber -> getNumberDefinition(fieldName, node)
            node.isBoolean -> getBooleanDefinition(fieldName, node)
            node.isTextual -> getStringDefinition(fieldName, node)
            node.isObject -> getObjectPropertyDefinition(fieldName, node)
            node.isArray -> getArrayPropertyDefinition(fieldName, node)
            else -> throw UnsupportedOperationException("Unsupported type of json node [${node.nodeType}]: \"$fieldName\"=$node")
        }
    }

    private fun getStringDefinition(
        fieldName: String,
        node: JsonNode,
    ): PropertyDefinition {
        val example = features.isEnabled(Features.Feature.WITH_EXAMPLE)
        val fieldDefinition = FieldDefinition(fieldName, PropertyType.STRING)

        if (example) {
            return ExtendedFieldDefinition(fieldDefinition, node.asText())
        }

        return fieldDefinition
    }

    private fun getBooleanDefinition(
        fieldName: String,
        node: JsonNode,
    ): PropertyDefinition {
        val example = features.isEnabled(Features.Feature.WITH_EXAMPLE)
        val fieldDefinition = FieldDefinition(fieldName, PropertyType.BOOLEAN)

        if (example) {
            return ExtendedFieldDefinition(fieldDefinition, node.asBoolean())
        }

        return fieldDefinition
    }

    private fun getNumberDefinition(
        fieldName: String,
        node: JsonNode,
    ): PropertyDefinition {
        val example = features.isEnabled(Features.Feature.WITH_EXAMPLE)

        val fieldDefinition =
            when {
                node.isLong || node.isInt || node.isShort -> FieldDefinition(fieldName, PropertyType.INTEGER)
                else -> FieldDefinition(fieldName, PropertyType.NUMBER)
            }

        val exampleValue: Any?
        if (example) {
            exampleValue =
                when {
                    node.isLong -> node.longValue()
                    node.isInt -> node.intValue()
                    node.isShort -> node.shortValue()
                    node.isDouble -> node.doubleValue()
                    else -> node.floatValue()
                }
            return ExtendedFieldDefinition(fieldDefinition, exampleValue)
        }

        return fieldDefinition
    }

    private fun getArrayPropertyDefinition(
        fieldName: String,
        node: JsonNode,
    ): ArrayDefinition {
        if (node.size() == 0) {
            throw JsonEmptyArrayException("Json node is expected to be not empty: \"$fieldName[]\"=$node")
        }

        val elementsIterator = node.elements()
        val firstElement = elementsIterator.next()
        val firstElementPropertyDefinition = getPropertyDefinition(firstElement, fieldName)
        while (elementsIterator.hasNext()) {
            val secondElement = elementsIterator.next()
            val elementDefinition = getPropertyDefinition(secondElement, fieldName)

            if (firstElementPropertyDefinition != elementDefinition) {
                val firstTypeIsObject = firstElementPropertyDefinition.type() == PropertyType.OBJECT
                val secondTypeIsObject = elementDefinition.type() == PropertyType.OBJECT
                if (firstTypeIsObject && secondTypeIsObject) {
                    throw JsonGenericArrayTypeException(
                        "Json array node \"$fieldName[]\" expected to be strongly typed, " +
                            "expected [(OBJECT) ${firstElement.fields().asSequence().map { "${it.key}:${it.value.nodeType}" }.toList()}] " +
                            "but there's also [(OBJECT) ${secondElement.fields().asSequence().map { "${it.key}:${it.value.nodeType}" }.toList()}]",
                    )
                }

                throw JsonGenericArrayTypeException(
                    "Json array node expected to be strongly typed, " +
                        "expected only [${firstElementPropertyDefinition.type()}] but there's also [${elementDefinition.type()}]:" +
                        " \"$fieldName[]\"=$node",
                )
            }
        }

        return ArrayDefinition(fieldName, firstElementPropertyDefinition)
    }

    private fun getObjectPropertyDefinition(
        fieldName: String,
        node: JsonNode,
    ): ObjectDefinition {
        val fieldNames = node.fieldNames()
        return ObjectDefinition(
            fieldName,
            node.elements()
                .asSequence()
                .map { getPropertyDefinition(it, fieldNames.next()) }
                .toList(),
        )
    }
}
