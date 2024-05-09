package io.github.jooas.adapters.openapi.yaml

import io.github.jooas.adapters.openapi.definitions.PropertyDefinition

interface YamlObjectAdapter {
    fun convert(objectDefinitions: List<PropertyDefinition>): String
}