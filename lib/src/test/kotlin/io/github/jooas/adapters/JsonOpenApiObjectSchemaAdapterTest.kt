package io.github.jooas.adapters

import io.github.jooas.adapters.exceptions.JsonEmptyObjectException
import io.github.jooas.adapters.exceptions.JsonGenericArrayTypeException
import io.github.jooas.adapters.exceptions.JsonIsNotAnObjectException
import io.github.jooas.adapters.input.JsonTextStream
import io.github.jooas.adapters.output.ConsoleSchemaOutputStream
import io.github.jooas.readJsonResourceAsText
import io.github.jooas.readYamlSchemaResourceAsText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

class JsonOpenApiObjectSchemaAdapterTest {
    private val sut = defaultAdapter()

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
    fun `Convert json object without nested objects into a openapi schema`() {
        val json = readJsonResourceAsText("object-0.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-0.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with nested object into a openapi schema`() {
        val json = readJsonResourceAsText("object-1.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-1.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with deep nested objects into a openapi schema`() {
        val json = readJsonResourceAsText("object-2.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-2.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with primitive type arrays into a openapi schema`() {
        val json = readJsonResourceAsText("object-3.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-3.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with object arrays into a openapi schema`() {
        val json = readJsonResourceAsText("object-4.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-4.yaml", this::class.java)
        assertEquals(expected, actual)
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

    @Test
    fun `Pass-through input-output json-text to console`() {
        val jsonTextStream = JsonTextStream(readJsonResourceAsText("object-0.json", this::class.java))
        val consoleOutputStream = spy(ConsoleSchemaOutputStream())

        sut.convert(jsonTextStream, consoleOutputStream)

        val expected = readYamlSchemaResourceAsText("schema-0.yaml", this::class.java)
        verify(consoleOutputStream).flush(expected)
    }

    @Test
    fun `Convert json object without nested objects into a openapi schema with example`() {
        val sut = adapterWithExampleFeature()

        val json = readJsonResourceAsText("object-0.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-0-with-example.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with nested object into a openapi schema with example`() {
        val sut = adapterWithExampleFeature()

        val json = readJsonResourceAsText("object-1.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-1-with-example.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with extra string field into a openapi schema with example`() {
        val sut = adapterWithExampleFeature()

        val json = readJsonResourceAsText("object-8.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-8-with-example.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with object arrays into a openapi schema with example`() {
        val sut = adapterWithExampleFeature()

        val json = readJsonResourceAsText("object-4.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-4-with-example.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with inner object references into a openapi schema with components`() {
        val sut = adapterWithObjectReferenceFeature()

        val json = readJsonResourceAsText("object-1.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-1-object-reference.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object with inner nested object references into a openapi schema with components`() {
        val sut = adapterWithObjectReferenceFeature()

        val json = readJsonResourceAsText("object-2.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-2-object-reference.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `Convert json object without inner nested object references into a openapi schema with components`() {
        val sut = adapterWithObjectReferenceFeature()

        val json = readJsonResourceAsText("object-3.json", this::class.java)

        val actual = sut.convert(json)

        val expected = readYamlSchemaResourceAsText("schema-3-object-reference.yaml", this::class.java)
        assertEquals(expected, actual)
    }

    private fun defaultAdapter() = AdaptersFactory.createObjectAdapter()

    private fun adapterWithExampleFeature() =
        AdaptersFactory.createObjectAdapter(
            Pair(Features.Feature.WITH_EXAMPLE, true),
        )

    private fun adapterWithObjectReferenceFeature() =
        AdaptersFactory.createObjectAdapter(
            Pair(Features.Feature.OBJECT_REFERENCE, true),
        )
}
