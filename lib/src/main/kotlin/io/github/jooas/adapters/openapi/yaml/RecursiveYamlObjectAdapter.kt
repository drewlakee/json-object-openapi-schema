package io.github.jooas.adapters.openapi.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.github.jooas.adapters.openapi.definitions.*
import io.github.jooas.adapters.openapi.yaml.pojo.*

class RecursiveYamlObjectAdapter(
    private val yamlMapper: ObjectMapper = ObjectMapper(
        YAMLFactory()
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    )
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
            is FieldDefinition -> {
                return Pair(
                    it.fieldName,
                    when (it.type) {
                        PropertyType.STRING -> StringProperty()
                        PropertyType.INTEGER -> IntegerProperty()
                        PropertyType.NUMBER -> NumberProperty()
                        PropertyType.BOOLEAN -> BooleanProperty()
                        PropertyType.ARRAY -> throw IllegalStateException(
                            "Property definition of array is expected to be as another type. " +
                                    "See #${ArrayDefinition::class.simpleName}"
                        )
                        PropertyType.OBJECT -> throw IllegalStateException(
                            "Property definition of object is expected to be as another type. " +
                                    "See #${ObjectDefinition::class.simpleName}"
                        )
                    }
                )
            }
        }
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