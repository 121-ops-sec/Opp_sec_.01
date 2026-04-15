package com.opsec.fieldintelligence.data.model

enum class SignalQuality(val bars: Int, val label: String) {
    NONE(0, "No Signal"),
    POOR(1, "Poor"),
    FAIR(2, "Fair"),
    GOOD(3, "Good"),
    EXCELLENT(4, "Excellent")
}
