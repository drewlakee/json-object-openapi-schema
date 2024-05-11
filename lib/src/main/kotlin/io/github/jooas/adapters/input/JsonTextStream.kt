package io.github.jooas.adapters.input

class JsonTextStream(
    private val text: String
): JsonInputStream {

    override fun get(): String {
        return text
    }
}