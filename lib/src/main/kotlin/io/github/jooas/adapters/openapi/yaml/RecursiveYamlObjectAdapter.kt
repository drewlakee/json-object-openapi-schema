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
        val objectStructureRefs: MutableMap<List<PropertyDefinition>, ObjectReferenceProperty> = LinkedHashMap()
        val arrayStructureRefs: MutableMap<List<PropertyDefinition>, ArrayObjectReferenceProperty> = LinkedHashMap()
        objectDefinitions.forEach {
            recursiveExtract(
                propertyDefinition = it,
                refs = refs,
                objectSchemas = objectSchemas,
                objectStructureRefs = objectStructureRefs,
                arrayStructureRefs = arrayStructureRefs,
            )
        }
        return ObjectReferences(refs, objectSchemas)
    }

    private fun recursiveExtract(
        propertyDefinition: PropertyDefinition,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
        objectSchemas: MutableMap<String, PojoProperty>,
        objectStructureRefs: MutableMap<List<PropertyDefinition>, ObjectReferenceProperty>,
        arrayStructureRefs: MutableMap<List<PropertyDefinition>, ArrayObjectReferenceProperty>,
    ) {
        val schemaName = SchemaNameGenerator.tryPascalCaseWhile(propertyDefinition.fieldName()) { objectSchemas.containsKey(it) }
        when (propertyDefinition) {
            is ArrayDefinition -> extractArrayObject(
                schemaName = schemaName,
                arrayDefinition = propertyDefinition,
                objectSchemas = objectSchemas,
                refs = refs,
                objectStructureRefs = objectStructureRefs,
                arrayStructureRefs = arrayStructureRefs,
            )
            is ObjectDefinition -> extractObjects(
                schemaName = schemaName,
                objectDefinition = propertyDefinition,
                objectSchemas = objectSchemas,
                refs = refs,
                objectStructureRefs = objectStructureRefs,
                arrayStructureRefs = arrayStructureRefs,
            )
            else -> return
        }
    }

    private fun extractArrayObject(
        schemaName: String,
        arrayDefinition: ArrayDefinition,
        objectSchemas: MutableMap<String, PojoProperty>,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
        objectStructureRefs: MutableMap<List<PropertyDefinition>, ObjectReferenceProperty>,
        arrayStructureRefs: MutableMap<List<PropertyDefinition>, ArrayObjectReferenceProperty>,
    ) {
        when (val itemsDefinition = arrayDefinition.itemsDefinition) {
            is ObjectDefinition -> {
                extractObjects(
                    schemaName = schemaName,
                    objectDefinition = itemsDefinition,
                    objectSchemas = objectSchemas,
                    refs = refs,
                    objectStructureRefs = objectStructureRefs,
                    arrayStructureRefs = arrayStructureRefs,
                )
                val arrayReference = arrayStructureRefs[itemsDefinition.properties] ?: ArrayObjectReferenceProperty(schemaName = schemaName)
                arrayStructureRefs[itemsDefinition.properties] = arrayReference
                refs[itemsDefinition] = mapOf(arrayReference.schemaName to arrayReference)
            }
            else -> return
        }
    }

    private fun extractObjects(
        schemaName: String,
        objectDefinition: ObjectDefinition,
        objectSchemas: MutableMap<String, PojoProperty>,
        refs: MutableMap<PropertyDefinition, Map<String, PojoProperty>>,
        objectStructureRefs: MutableMap<List<PropertyDefinition>, ObjectReferenceProperty>,
        arrayStructureRefs: MutableMap<List<PropertyDefinition>, ArrayObjectReferenceProperty>,
    ) {
        val propertyDefinitions = objectDefinition.propertyDefinitions
        val objectProperty = toProperty(objectDefinition).second as ObjectProperty
        objectSchemas[schemaName] = objectProperty
        val objectSchemaReference = objectStructureRefs[propertyDefinitions] ?: ObjectReferenceProperty(schemaName)
        refs[objectDefinition] = mapOf(objectDefinition.fieldName to objectSchemaReference)

        objectDefinition.properties.forEach {
            recursiveExtract(
                propertyDefinition = it,
                refs = refs,
                objectSchemas = objectSchemas,
                objectStructureRefs = objectStructureRefs,
                arrayStructureRefs = arrayStructureRefs,
            )
        }

        if (objectStructureRefs.containsKey(propertyDefinitions)) {
            objectSchemas.remove(schemaName)
            return
        }

        objectStructureRefs[propertyDefinitions] = objectSchemaReference

        val pojoProperties: MutableMap<String, PojoProperty> = LinkedHashMap()
        objectDefinition.properties.forEach {
            when (it) {
                is ObjectDefinition -> {
                    if (refs.containsKey(it)) {
                        pojoProperties[it.fieldName] = refs[it]!!.entries.first().value
                    }
                }
                is ArrayDefinition -> {
                    if (it.itemsDefinition is ObjectDefinition) {
                        pojoProperties[it.fieldName] = refs[it.itemsDefinition]!!.entries.first().value
                    } else {
                        pojoProperties[it.fieldName()] = objectProperty.properties[it.fieldName()]!!
                    }
                }
                else -> {
                    pojoProperties[it.fieldName()] = objectProperty.properties[it.fieldName()]!!
                }
            }
        }
        objectSchemas[schemaName] = ObjectProperty(properties = pojoProperties)
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
