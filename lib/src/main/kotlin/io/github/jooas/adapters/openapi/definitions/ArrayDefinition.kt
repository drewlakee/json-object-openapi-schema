package io.github.jooas.adapters.openapi.definitions

data class ArrayDefinition(
    val fieldName: String,
    val itemsDefinition: PropertyDefinition,
): PropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return PropertyType.ARRAY
    }

    override fun definition() = this
}