package io.github.jooas.adapters.openapi.yaml.wrappers

import io.github.jooas.adapters.openapi.yaml.pojo.ObjectProperty
import io.github.jooas.adapters.openapi.yaml.pojo.PojoProperty

data class SchemaComponents(
    private val schema: Map<String, ObjectProperty>,
    private val objectReferences: Map<String, PojoProperty>,
) {
    val components: Map<String, Map<String, PojoProperty>>
        get() =
            mapOf(
                "schemas" to
                    mapOf(
                        *schema.entries.map { Pair(it.key, it.value) }.toTypedArray(),
                        *objectReferences.entries.map { Pair(it.key, it.value) }.toTypedArray(),
                    ),
            )
}
