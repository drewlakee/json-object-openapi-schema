package io.github.jooas.adapters.openapi.definitions

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

    override fun definition() = this
}
