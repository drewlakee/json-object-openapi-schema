package io.github.jooas.adapters.openapi

import com.fasterxml.jackson.databind.JsonNode

interface SchemaDefinitionExtractor {
    fun getObjectDefinitions(node: JsonNode): DefinitionsExtract
}