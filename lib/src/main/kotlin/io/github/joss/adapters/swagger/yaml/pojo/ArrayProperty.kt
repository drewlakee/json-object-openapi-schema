package io.github.joss.adapters.swagger.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class ArrayProperty(
    val type: String = PropertyType.ARRAY.swaggerType,
    val items: PojoProperty
): PojoProperty
