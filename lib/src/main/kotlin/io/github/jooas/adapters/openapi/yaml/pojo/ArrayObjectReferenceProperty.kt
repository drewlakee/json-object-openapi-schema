package io.github.jooas.adapters.openapi.yaml.pojo

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.jooas.adapters.openapi.definitions.PropertyType

data class ArrayObjectReferenceProperty(
    val type: String = PropertyType.ARRAY.openApiType,
    @JsonIgnore val schemaName: String,
) : PojoProperty {
    val items: Map<String, String>
        get() =
            mapOf(
                "\$ref" to "#/components/schemas/$schemaName",
            )
}
