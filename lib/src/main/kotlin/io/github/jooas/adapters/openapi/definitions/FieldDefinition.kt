package io.github.jooas.adapters.openapi.definitions

data class FieldDefinition(
    val fieldName: String,
    val type: PropertyType,
): PropertyDefinition {

    override fun fieldName() = fieldName
    override fun type() = type
    override fun definition() = this
}
