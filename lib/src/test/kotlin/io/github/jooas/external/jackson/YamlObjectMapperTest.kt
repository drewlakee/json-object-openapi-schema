package io.github.jooas.external.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class YamlObjectMapperTest {

    @Test
    @DisplayName("Convert data class into yaml schema")
    fun convertClassIntoYaml() {
        val objectMapper = ObjectMapper(
            YAMLFactory()
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        ).writer()

        data class Type(val type: String)

        data class Fields(
            val type: String,
            val properties: Map<String, Type>,
        )

        val result = mapOf(
            "Schema" to Fields(
                "object",
                mapOf(
                    "integer_value" to Type("integer"),
                    "float_value" to Type("number"),
                    "string_value" to Type("string"),
                    "boolean_value" to Type("boolean")
                ))
        )

        val actual = objectMapper.writeValueAsString(result).trimIndent()

        val expected = """
Schema:
  type: object
  properties:
    integer_value:
      type: integer
    float_value:
      type: number
    string_value:
      type: string
    boolean_value:
      type: boolean
        """.trimIndent()

        assertEquals(expected, actual)
    }
}