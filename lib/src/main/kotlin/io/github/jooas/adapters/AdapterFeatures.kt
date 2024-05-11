package io.github.jooas.adapters

class AdapterFeatures {

    private val features: HashMap<Feature, Boolean> = HashMap()

    enum class Feature {
        WITH_EXAMPLE
    }

    fun enable(feature: Feature): AdapterFeatures {
        features.putIfAbsent(feature, true)
        return this
    }

    fun disable(feature: Feature): AdapterFeatures {
        features.computeIfPresent(feature) { _, _ -> false }
        return this
    }

    fun isEnabled(feature: Feature) = features[feature] ?: false
}