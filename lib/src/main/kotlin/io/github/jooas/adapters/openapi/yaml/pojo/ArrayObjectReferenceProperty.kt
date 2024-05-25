package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class ArrayObjectReferenceProperty(
    val type: String = PropertyType.ARRAY.openApiType,
    private val schemaName: String,
) : PojoProperty {
    val items: Map<String, String>
        get() =
            mapOf(
                "\$ref" to "#/components/schemas/$schemaName",
            )
}
