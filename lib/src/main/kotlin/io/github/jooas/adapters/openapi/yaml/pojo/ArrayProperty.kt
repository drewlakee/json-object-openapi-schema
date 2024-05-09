package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class ArrayProperty(
    val type: String = PropertyType.ARRAY.openApiType,
    val items: PojoProperty
): PojoProperty
