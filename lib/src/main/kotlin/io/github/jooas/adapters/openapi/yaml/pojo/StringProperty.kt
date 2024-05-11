package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class StringProperty(
    val type: String = PropertyType.STRING.openApiType,
    val example: String? = null,
): PojoProperty
