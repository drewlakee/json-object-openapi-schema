package io.github.joss.adapters.swagger.definitions

data class ArrayDefinition(
    val fieldName: String,
    val itemsType: PropertyDefinition,
): PropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return PropertyType.ARRAY
    }
}