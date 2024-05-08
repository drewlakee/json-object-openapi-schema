package io.github.joss.adapters.swagger.definitions

data class ObjectDefinition(
    val fieldName: String,
    val properties: List<PropertyDefinition>
): PropertyDefinition {

    override fun fieldName(): String {
        return fieldName
    }

    override fun type(): PropertyType {
        return PropertyType.OBJECT
    }
}
