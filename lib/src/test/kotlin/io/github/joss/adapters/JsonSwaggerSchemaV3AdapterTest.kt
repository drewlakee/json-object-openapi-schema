package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonIsEmptyObject
import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
import io.github.joss.readJsonResourceAsText
import io.github.joss.readYamlSchemaResourceAsText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class JsonSwaggerSchemaV3AdapterTest {

    private val output = mock(SwaggerSchemaOutputStream::class.java)
    private val sut = JsonSwaggerSchemaV3Adapter(output)

    @Test
    fun `Throw exception if json is valid and object is empty itself`() {
        val value = "{}"
        assertThrows<JsonIsEmptyObject> { sut.convert(value) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1000", "1000.0", "string", "true", "c"])
    fun `Throw exception if json is valid and it's not an object itself`(value: String) {
        assertThrows<JsonIsNotAnObjectException> { sut.convert(value) }
    }

    @Test
    fun `Convert json object without nested objects into a schema`() {
        val json = readJsonResourceAsText("object-0.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-0.yaml", this::class.java)
        verify(output).flush(expected)
    }
}