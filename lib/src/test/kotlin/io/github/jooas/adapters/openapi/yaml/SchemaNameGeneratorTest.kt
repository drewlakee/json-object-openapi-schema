package io.github.jooas.adapters.openapi.yaml

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class SchemaNameGeneratorTest {
    @ParameterizedTest
    @MethodSource("pascalCaseTestCases")
    fun `Convert pascal case`(
        value: String,
        expected: String,
    ) {
        val actual = SchemaNameGenerator.pascalCase(value)
        assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        fun pascalCaseTestCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of("object_value", "ObjectValue"),
                Arguments.of("asd-1", "Asd"),
                Arguments.of("Object", "Object"),
                Arguments.of("Schema_2", "Schema"),
                Arguments.of("1241", ""),
                Arguments.of("89-/", ""),
                Arguments.of("meta-information", "MetaInformation"),
            )
    }
}
