package io.github.jooas.adapters.openapi.definitions

data class ExtendedFieldDefinition(
    private val fieldDefinition: FieldDefinition,
) : PropertyDefinition {
    var example: Any? = null

    constructor(fieldDefinition: FieldDefinition, example: Any? = null) : this(fieldDefinition) {
        this.example = example
    }

    override fun fieldName() = this.fieldDefinition.fieldName()

    override fun type() = this.fieldDefinition.type()

    override fun definition() = this.fieldDefinition
}
