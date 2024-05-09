package io.github.jooas.adapters.openapi.definitions

enum class PropertyType(val openApiType: String) {
    STRING("string"),
    INTEGER("integer"),
    NUMBER("number"),
    ARRAY("array"),
    OBJECT("object"),
    BOOLEAN("boolean"),
}