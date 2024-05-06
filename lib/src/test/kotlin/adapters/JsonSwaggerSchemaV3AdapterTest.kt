package adapters

import adapters.output.SwaggerSchemaOutputStream
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class JsonSwaggerSchemaV3AdapterTest {

    private val output = mock(SwaggerSchemaOutputStream::class.java)
    private val sut = JsonSwaggerSchemaV3Adapter(output)

    @Test
    fun `Output as is it is`() {
        val value = "{}"

        sut.convert(value)

        verify(output).flush(value)
    }
}