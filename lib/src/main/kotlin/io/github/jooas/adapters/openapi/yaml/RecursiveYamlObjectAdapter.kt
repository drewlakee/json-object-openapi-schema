package io.github.jooas.adapters.openapi.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jooas.adapters.openapi.definitions.*
import io.github.jooas.adapters.openapi.yaml.pojo.*

class RecursiveYamlObjectAdapter(
    private val yamlMapper: ObjectMapper
): YamlObjectAdapter {

    override fun convert(objectDefinitions: List<PropertyDefinition>): String {
        val properties = objectDefinitions.map { toProperty(it) }.toList()
        val obj = Pair("Schema", ObjectProperty(properties = properties.toMap()))
        val schema = mapOf(obj)
        return yamlMapper.writeValueAsString(schema)
    }

    private fun toProperty(it: PropertyDefinition): Pair<String, PojoProperty> {
        return when (it) {
            is ArrayDefinition -> Pair(it.fieldName, getArrayProperty(it))
            is ObjectDefinition -> Pair(it.fieldName, getObjectProperty(it))
            is ExtendedFieldDefinition -> Pair(
                it.definition().fieldName(),
                getPojoProperty(it.definition(), it.example)
            )
            is FieldDefinition -> Pair(it.fieldName, getPojoProperty(it, null))
        }
    }

    private fun getPojoProperty(it: FieldDefinition, example: Any?) = when (it.type) {
        PropertyType.STRING -> StringProperty(example = example as? String)
        PropertyType.INTEGER -> IntegerProperty(example = example as? Int)
        PropertyType.NUMBER -> NumberProperty(example = example as? Double)
        PropertyType.BOOLEAN -> BooleanProperty(example = example as? Boolean)
        PropertyType.ARRAY -> throw IllegalStateException(
            "Property definition of array is expected to be as another type. " +
                    "See #${ArrayDefinition::class.simpleName}"
        )
        PropertyType.OBJECT -> throw IllegalStateException(
            "Property definition of object is expected to be as another type. " +
                    "See #${ObjectDefinition::class.simpleName}"
        )
    }

    private fun getArrayProperty(arrayDefinition: ArrayDefinition): ArrayProperty {
        return ArrayProperty(
            items = toProperty(arrayDefinition.itemsDefinition).second
        )
    }

    private fun getObjectProperty(objectDefinition: ObjectDefinition): ObjectProperty {
        return ObjectProperty(
            properties = objectDefinition.properties.associate { toProperty(it) }
        )
    }
}