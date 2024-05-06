package io.github.joss.adapters

import io.github.joss.adapters.exceptions.JsonIsNotAnObject
import io.github.joss.adapters.output.SwaggerSchemaOutputStream
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
    fun `Get empty schema if json is valid and object is empty itself`() {
        val value = "{}"

        sut.convert(value)

        val expected = "Model:"
        verify(output).flush(expected)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1000", "1000.0", "string", "true", "c"])
    fun `Throw exception if json is valid and it's not an object itself`(value: String) {
        assertThrows<JsonIsNotAnObject> { sut.convert(value) }
    }
}