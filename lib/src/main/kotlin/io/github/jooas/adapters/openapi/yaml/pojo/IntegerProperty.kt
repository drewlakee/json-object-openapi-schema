package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class IntegerProperty(
    val type: String = PropertyType.INTEGER.openApiType,
    val example: Int? = null
): PojoProperty
