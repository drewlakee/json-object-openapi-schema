package io.github.jooas.adapters.openapi.definitions

data class ObjectDefinition(
    val fieldName: String,
    val properties: List<PropertyDefinition>
): PropertyDefinition {

    override fun fieldName() = fieldName
    override fun type() = PropertyType.OBJECT
    override fun definition() = this
}
