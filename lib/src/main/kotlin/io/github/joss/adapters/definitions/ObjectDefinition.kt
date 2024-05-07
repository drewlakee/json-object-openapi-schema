package io.github.joss.adapters.definitions

data class ObjectDefinition(
    val fieldName: String,
    val fields: List<SwaggerSchemaPropertyDefinition>
): SwaggerSchemaPropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return PropertyType.OBJECT
    }
}
