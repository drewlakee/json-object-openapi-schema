package io.github.joss.adapters.definitions

data class ArrayDefinition(
    val fieldName: String,
    val itemsType: SwaggerSchemaPropertyDefinition,
): SwaggerSchemaPropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return PropertyType.ARRAY
    }
}