package io.github.jooas

import java.io.IOException
import java.nio.charset.Charset

fun readJsonResourceAsText(path: String, clazz: Class<out Any>) =
    clazz.classLoader.getResource("json/objects/$path")
        ?.readText(Charset.defaultCharset())
        ?: throw IOException("File json/objects/$path not found")

fun readYamlSchemaResourceAsText(path: String, clazz: Class<out Any>) =
    clazz.classLoader.getResource("yaml/openapi/schemas/$path")
        ?.readText(Charset.defaultCharset())
        ?: throw IOException("File json/objects/$path not found")