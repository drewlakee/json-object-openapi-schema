package io.github.joss.adapters.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class NumberProperty(
    val type: String = PropertyType.NUMBER.swaggerType,
): PojoProperty
