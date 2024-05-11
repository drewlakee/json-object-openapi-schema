package io.github.jooas.adapters.openapi.definitions

data class ArrayDefinition(
    val fieldName: String,
    val itemsDefinition: PropertyDefinition,
): PropertyDefinition {

    override fun fieldName() = fieldName
    override fun type() = PropertyType.ARRAY
    override fun definition() = this
}