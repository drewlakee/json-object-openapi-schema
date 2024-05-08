package io.github.joss.adapters.swagger.definitions

sealed interface PropertyDefinition {
    fun fieldName(): String
    fun type(): PropertyType
}
