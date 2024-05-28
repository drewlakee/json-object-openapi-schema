package io.github.jooas.adapters.openapi.yaml.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class ObjectReferenceProperty(
    private val schemaName: String,
) : PojoProperty {
    @get:JsonProperty("\$ref")
    val ref: String
        get() = "#/components/schemas/$schemaName"
}
