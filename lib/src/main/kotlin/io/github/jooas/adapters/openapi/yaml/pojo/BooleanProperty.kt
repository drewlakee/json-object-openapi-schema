package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class BooleanProperty(
    val type: String = PropertyType.BOOLEAN.openApiType,
    val example: Boolean? = null
): PojoProperty
