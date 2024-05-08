package io.github.joss.adapters.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class StringProperty(
    val type: String = PropertyType.STRING.swaggerType,
): PojoProperty
