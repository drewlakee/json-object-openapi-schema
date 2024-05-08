package io.github.joss.adapters.swagger.yaml

import io.github.joss.adapters.swagger.definitions.PropertyDefinition

interface YamlObjectAdapter {
    fun convert(objectDefinitions: List<PropertyDefinition>): String
}