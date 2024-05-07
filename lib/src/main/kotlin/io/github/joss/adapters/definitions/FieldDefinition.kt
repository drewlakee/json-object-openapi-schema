package io.github.joss.adapters.definitions

data class FieldDefinition(
    val fieldName: String,
    val type: PropertyType,
): SwaggerSchemaPropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return type
    }
}
