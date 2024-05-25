package io.github.jooas.adapters.openapi.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jooas.adapters.Features
import io.github.jooas.adapters.openapi.definitions.*
import io.github.jooas.adapters.openapi.yaml.pojo.*
import io.github.jooas.adapters.openapi.yaml.wrappers.SchemaComponents

private data class ObjectReferences(
    val references: Map<PropertyDefinition, Map<String, PojoProperty>>,
    val objectSchemas: Map<String, PojoProperty>,
) {
    companion object {
        fun empty() = ObjectReferences(mapOf(), mapOf())
    }
}

class RecursiveYamlObjectAdapter(
    private val yamlMapper: ObjectMapper,
    private val features: Features,
) : YamlObjectAdapter {
    override fun convert(objectDefinitions: List<PropertyDefinition>): String {
        val objectReferences =
            if (isObjectReferenceEnabled()) {
                extractObjectReferencesInDepth(objectDefinitions)
            } else {
                ObjectReferences.empty()
            }

        val properties =
            if (isObjectReferenceEnabled()) {
                replaceObjectReferencesInPlane(
                    objectDefinitions,
                    objectReferences.references,
                )
            } else {
                objectDefinitions.map { toProperty(it) }.toList()
            }

        val rootSchema = mapOf(Pair("Schema", ObjectProperty(properties = properties.toMap())))

        if (isObjectReferenceEnabled()) {
            return yamlMapper.writeValueAsString(
                SchemaComponents(rootSchema, objectReferences.objectSchemas),
            )
        }

        return yamlMapper.writeValueAsString(rootSchema)
    }

    private fun replaceObjectReferencesInPlane(
        objectDefinitions: List<PropertyDefinition>,
        objectReferences: Map<PropertyDefinition, Map<String, PojoProperty>>,
    ): List<Pair<String, PojoProperty>> {
        return objectDefinitions
            .map {
                when (it) {
                    is ObjectDefinition -> {
                        val ref = objectReferences[it]!!.entries.first()
                        Pair(ref.key, ref.value)
                    }
                    is ArrayDefinition -> {
                        when (it.itemsDefinition) {
                            is ObjectDefinition -> {
                                val ref = objectReferences[it.itemsDefinition]!!.entries.first()
                                Pair(it.fieldName, ref.value)
                            }
                            else -> toProperty(it)
                        }
                    }
                    else -> toProperty(it)
                }
            }
            .toList()
    }

    private fun extractObjectReferencesInDepth(objectDefinitions: List<PropertyDefinition>): ObjectReferences {
        val refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>> = LinkedHashMap()
        val objectSchemas: MutableMap<String, PojoProperty> = LinkedHashMap()
        objectDefinitions.forEach { recursiveExtract(it, refs, objectSchemas) }
        return ObjectReferences(refs, objectSchemas)
    }

    private fun recursiveExtract(
        it: PropertyDefinition,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
        objectSchemas: MutableMap<String, PojoProperty>,
    ) {
        val schemaName =
            SchemaNameGenerator.tryPascalCaseWhile(it.fieldName()) { schemaName ->
                objectSchemas.containsKey(schemaName)
            }
        when (it) {
            is ArrayDefinition -> extractArrayObject(schemaName, it, objectSchemas, refs)
            is ObjectDefinition -> extractObjects(schemaName, it, objectSchemas, refs)
            else -> return
        }
    }

    private fun extractArrayObject(
        schemaName: String,
        it: ArrayDefinition,
        objectSchemas: MutableMap<String, PojoProperty>,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
    ) {
        when (val itemsDefinition = it.itemsDefinition) {
            is ObjectDefinition -> {
                extractObjects(schemaName, itemsDefinition, objectSchemas, refs)
                refs[itemsDefinition] = mapOf(schemaName to ArrayObjectReferenceProperty(schemaName = schemaName))
            }
            else -> return
        }
    }

    private fun extractObjects(
        schemaName: String,
        it: PropertyDefinition,
        objectSchemas: MutableMap<String, PojoProperty>,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
    ) {
        when (it) {
            is ObjectDefinition -> {
                val objectProperty = toProperty(it).second as ObjectProperty
                objectSchemas[schemaName] = objectProperty
                val objectSchema = ObjectReferenceProperty(schemaName)
                refs[it] = mapOf(it.fieldName to objectSchema)

                it.properties.forEach { recursiveExtract(it, refs, objectSchemas) }

                val pojoProperties: MutableMap<String, PojoProperty> = LinkedHashMap()
                it.properties.forEach {
                    when (it) {
                        is ObjectDefinition -> {
                            if (refs.containsKey(it)) {
                                pojoProperties[it.fieldName] = refs[it]!!.entries.first().value
                            }
                        }
                        is ArrayDefinition -> {
                            if (it.itemsDefinition is ObjectDefinition) {
                                pojoProperties[it.fieldName] = refs[it.itemsDefinition]!!.entries.first().value
                            }
                        }
                        else -> {
                            pojoProperties[it.fieldName()] = objectProperty.properties[it.fieldName()]!!
                        }
                    }
                }
                objectSchemas[schemaName] = ObjectProperty(properties = pojoProperties)
            }

            else -> return
        }
    }

    private fun toProperty(it: PropertyDefinition): Pair<String, PojoProperty> {
        return when (it) {
            is ArrayDefinition -> Pair(it.fieldName, getArrayProperty(it))
            is ObjectDefinition -> Pair(it.fieldName, getObjectProperty(it))
            is ExtendedFieldDefinition ->
                Pair(
                    it.definition().fieldName(),
                    getPojoProperty(it.definition(), it.example),
                )
            is FieldDefinition -> Pair(it.fieldName, getPojoProperty(it, null))
        }
    }

    private fun getPojoProperty(
        it: FieldDefinition,
        example: Any?,
    ) = when (it.type) {
        PropertyType.STRING -> StringProperty(example = example as? String)
        PropertyType.INTEGER -> IntegerProperty(example = example as? Int)
        PropertyType.NUMBER -> NumberProperty(example = example as? Double)
        PropertyType.BOOLEAN -> BooleanProperty(example = example as? Boolean)
        PropertyType.ARRAY -> throw IllegalStateException(
            "Property definition of array is expected to be as another type. " +
                "See #${ArrayDefinition::class.simpleName}",
        )
        PropertyType.OBJECT -> throw IllegalStateException(
            "Property definition of object is expected to be as another type. " +
                "See #${ObjectDefinition::class.simpleName}",
        )
    }

    private fun getArrayProperty(arrayDefinition: ArrayDefinition): ArrayProperty {
        return ArrayProperty(
            items = toProperty(arrayDefinition.itemsDefinition).second,
        )
    }

    private fun getObjectProperty(objectDefinition: ObjectDefinition): ObjectProperty {
        return ObjectProperty(
            properties = objectDefinition.properties.associate { toProperty(it) },
        )
    }

    private fun isObjectReferenceEnabled() = this.features.isEnabled(Features.Feature.OBJECT_REFERENCE)
}
