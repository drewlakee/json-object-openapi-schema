package io.github.joss.adapters.swagger.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class BooleanProperty(
    val type: String = PropertyType.BOOLEAN.swaggerType,
): PojoProperty
