package io.github.joss.adapters.swagger.yaml.pojo

import io.github.joss.adapters.swagger.definitions.PropertyType

data class ObjectProperty(
    val type: String = PropertyType.OBJECT.swaggerType,
    val properties: Map<String, PojoProperty>
): PojoProperty
