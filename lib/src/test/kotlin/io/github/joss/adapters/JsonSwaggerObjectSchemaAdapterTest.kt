package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonEmptyObjectException
import io.github.joss.adapters.exceptions.JsonGenericArrayTypeException
import io.github.joss.adapters.exceptions.JsonIsNotAnObjectException
import io.github.joss.adapters.output.SchemaOutputStream
import io.github.joss.readJsonResourceAsText
import io.github.joss.readYamlSchemaResourceAsText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class JsonSwaggerObjectSchemaAdapterTest {

    private val output = mock(SchemaOutputStream::class.java)
    private val sut = JsonSwaggerObjectSchemaAdapter(output)

    @Test
    fun `Throw exception if json is valid and object is empty itself`() {
        val value = "{}"
        assertThrows<JsonEmptyObjectException> { sut.convert(value) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1000", "1000.0", "string", "true", "c"])
    fun `Throw exception if json is valid and it's not an object itself`(value: String) {
        assertThrows<JsonIsNotAnObjectException> { sut.convert(value) }
    }

    @Test
    fun `Convert json object without nested objects into a swagger schema`() {
        val json = readJsonResourceAsText("object-0.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-0.yaml", this::class.java)
        verify(output).flush(expected)
    }

    @Test
    fun `Convert json object with nested object into a swagger schema`() {
        val json = readJsonResourceAsText("object-1.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-1.yaml", this::class.java)
        verify(output).flush(expected)
    }

    @Test
    fun `Convert json object with deep nested objects into a swagger schema`() {
        val json = readJsonResourceAsText("object-2.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-2.yaml", this::class.java)
        verify(output).flush(expected)
    }

    @Test
    fun `Convert json object with primitive type arrays into a swagger schema`() {
        val json = readJsonResourceAsText("object-3.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-3.yaml", this::class.java)
        verify(output).flush(expected)
    }

    @Test
    fun `Convert json object with object arrays into a swagger schema`() {
        val json = readJsonResourceAsText("object-4.json", this::class.java)

        sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-4.yaml", this::class.java)
        verify(output).flush(expected)
    }

    @Test
    fun `Throw exception if json object with generic object array`() {
        val json = readJsonResourceAsText("object-5.json", this::class.java)
        assertThrows<JsonGenericArrayTypeException> { sut.convert(json) }
    }

    @Test
    fun `Throw exception if json object with generic primitive array (int, float)`() {
        val json = readJsonResourceAsText("object-6.json", this::class.java)
        assertThrows<JsonGenericArrayTypeException> { sut.convert(json) }
    }

    @Test
    fun `Throw exception if json object with generic object array (float, string)`() {
        val json = readJsonResourceAsText("object-7.json", this::class.java)
        assertThrows<JsonGenericArrayTypeException> { sut.convert(json) }
    }
}