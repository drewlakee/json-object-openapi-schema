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

    @ParameterizedTest
    @MethodSource("pascalCaseTestWhileCases")
    fun `Convert pascal case with while condition`(
        value: String,
        expected: String,
    ) {
        var counter = 0
        val actual = SchemaNameGenerator.tryPascalCaseWhile(value) { _ -> counter++ != 10 }
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

        @JvmStatic
        fun pascalCaseTestWhileCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of("object_value", "ObjectValue10"),
                Arguments.of("asd-1", "Asd10"),
                Arguments.of("Object", "Object10"),
                Arguments.of("Schema_2", "Schema10"),
                Arguments.of("1241", "10"),
                Arguments.of("89-/", "10"),
                Arguments.of("meta-information", "MetaInformation10"),
            )
    }
}
