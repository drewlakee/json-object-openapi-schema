package io.github.joss.adapters.definitions

enum class PropertyType(val type: String) {
    STRING("string"),
    INTEGER("integer"),
    NUMBER("number"),
    ARRAY("array"),
    OBJECT("object")
}