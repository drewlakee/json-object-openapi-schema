package io.github.jooas.adapters

class Features(
    private val features: Map<Feature, Boolean>,
) {
    enum class Feature {
        WITH_EXAMPLE,
        OBJECT_REFERENCE,
    }

    fun isEnabled(feature: Feature) = features[feature] ?: false
}
