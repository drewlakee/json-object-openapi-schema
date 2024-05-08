package io.github.joss.adapters.swagger.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.github.joss.adapters.swagger.definitions.*
import io.github.joss.adapters.swagger.yaml.pojo.*

class RecursiveYamlObjectAdapter(
    private val yamlMapper: ObjectMapper = ObjectMapper(
        YAMLFactory()
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    )
): YamlObjectAdapter {

    override fun convert(objectDefinitions: List<PropertyDefinition>): String {
        val properties = objectDefinitions.map {
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

        val obj = Pair("Schema", ObjectProperty(properties = properties.toMap()))
        val schema = mapOf(obj)
        return yamlMapper.writeValueAsString(schema).trimIndent()
    }
}