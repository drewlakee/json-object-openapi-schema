package io.github.jooas.adapters

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.github.jooas.adapters.openapi.RecursiveSchemaDefinitionExtractor
import io.github.jooas.adapters.openapi.yaml.RecursiveYamlObjectAdapter

class AdaptersFactory private constructor() {

    companion object {

        fun createObjectAdapter(
            vararg pairs: Pair<Features.Feature, Boolean>
        ): JsonOpenApiSchemaAdapter {
            val jsonMapper = ObjectMapper()
            val yamlMapper = ObjectMapper(
                YAMLFactory()
                    .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            ).setSerializationInclusion(JsonInclude.Include.NON_NULL)

            val schemaExtractor = RecursiveSchemaDefinitionExtractor(
                Features(pairs.toMap())
            )
            val yamlAdapter = RecursiveYamlObjectAdapter(yamlMapper)
            return JsonOpenApiObjectSchemaAdapter(
                jsonMapper = jsonMapper,
                objectDefinitionExtractor = schemaExtractor,
                yamlObjectAdapter = yamlAdapter
            )
        }
    }
}