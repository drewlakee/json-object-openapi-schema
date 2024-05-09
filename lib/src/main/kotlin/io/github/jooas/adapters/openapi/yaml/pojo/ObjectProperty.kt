package io.github.jooas.adapters.openapi.yaml.pojo

import io.github.jooas.adapters.openapi.definitions.PropertyType

data class ObjectProperty(
    val type: String = PropertyType.OBJECT.openApiType,
    val properties: Map<String, PojoProperty>
): PojoProperty
