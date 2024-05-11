package io.github.jooas.adapters.openapi.definitions

class ExtendedFieldDefinition(
    private val fieldDefinition: FieldDefinition,
    val example: Any?,
): PropertyDefinition {

    override fun fieldName() = this.fieldDefinition.fieldName()
    override fun type() = this.fieldDefinition.type()
    override fun definition() = this.fieldDefinition
}