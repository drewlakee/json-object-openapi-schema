package io.github.jooas.adapters

class Features(
    private val features: Map<Feature, Boolean>
) {

    companion object {
        val NO_FEATURES = Features(mapOf())
    }

    enum class Feature {
        WITH_EXAMPLE
    }

    fun isEnabled(feature: Feature) = features[feature] ?: false
}