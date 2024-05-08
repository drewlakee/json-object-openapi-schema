package io.github.joss.adapters.swagger.definitions

data class FieldDefinition(
    val fieldName: String,
    val type: PropertyType,
): PropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return type
    }
}
