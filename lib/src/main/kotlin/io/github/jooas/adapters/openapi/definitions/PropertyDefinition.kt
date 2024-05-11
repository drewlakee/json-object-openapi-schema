package io.github.jooas.adapters.openapi.definitions

sealed interface PropertyDefinition {
    fun fieldName(): String
    fun type(): PropertyType
    fun definition(): PropertyDefinition
}
