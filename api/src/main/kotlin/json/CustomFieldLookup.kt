package com.weronka.golonka.json

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.geojson.GeoJsonObject
import org.http4k.contract.jsonschema.v3.Field
import org.http4k.contract.jsonschema.v3.FieldMetadataRetrievalStrategy
import org.http4k.contract.jsonschema.v3.FieldRetrieval
import org.http4k.contract.jsonschema.v3.NoFieldFound
import org.http4k.contract.jsonschema.v3.NoOpFieldMetadataRetrievalStrategy
import kotlin.reflect.KProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Custom Json Field Lookup for GeoJsonObject instances
 *
 */
class CustomFieldLookup(
    private val renamingStrategy: (String) -> String = { it },
    private val metadataRetrievalStrategy: FieldMetadataRetrievalStrategy = NoOpFieldMetadataRetrievalStrategy(),
) : FieldRetrieval {
    override fun invoke(
        target: Any,
        name: String,
    ): Field {
        if (target is GeoJsonObject && name == "type") {
            val asGeoJson =
                target::class.allSuperclasses.find {
                    it.simpleName == "GeoJsonObject"
                }
            val typeInfoAnnotation = asGeoJson?.annotations?.find { it is JsonTypeInfo } as? JsonTypeInfo
            if (typeInfoAnnotation != null && typeInfoAnnotation.use == JsonTypeInfo.Id.NAME && typeInfoAnnotation.property == "type") {
                return Field(target::class.java.simpleName, false, metadataRetrievalStrategy(target, name))
            }
        }

        val classFields =
            try {
                target::class.memberProperties.associateBy { renamingStrategy(it.name) }
            } catch (e: Error) {
                emptyMap<String, KProperty1<out Any, Any?>>()
            }

        return classFields[name]
            ?.let { field ->
                field.javaGetter
                    ?.let { it(target) }
                    ?.let { it to field.returnType.isMarkedNullable }
                    ?: classFields[name]
                        ?.javaField
                        ?.takeIf { it.trySetAccessible() }
                        ?.get(target)
                        ?.let { it to true }
            }?.let { Field(it.first, it.second, metadataRetrievalStrategy(target, name)) } ?: throw NoFieldFound(
            name,
            target,
        )
    }
}
