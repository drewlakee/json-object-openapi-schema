package io.github.joss.adapters.swagger.definitions

enum class PropertyType(val swaggerType: String) {
    STRING("string"),
    INTEGER("integer"),
    NUMBER("number"),
    ARRAY("array"),
    OBJECT("object"),
    BOOLEAN("boolean"),
}