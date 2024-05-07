package io.github.joss.adapters.definitions

sealed interface SwaggerSchemaPropertyDefinition {
    fun fieldName(): String
    fun type(): PropertyType
}
