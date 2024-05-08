package io.github.joss.adapters.swagger

import com.fasterxml.jackson.databind.JsonNode

interface SchemaDefinitionExtractor {
    fun getObjectDefinitions(node: JsonNode): DefinitionsExtract
}