package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class NumberProperty(
    val type: String = PropertyType.NUMBER.openApiType,
    val example: Double? = null
): PojoProperty
