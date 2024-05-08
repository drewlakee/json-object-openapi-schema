package io.github.joss.adapters.swagger.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class IntegerProperty(
    val type: String = PropertyType.INTEGER.swaggerType,
): PojoProperty
