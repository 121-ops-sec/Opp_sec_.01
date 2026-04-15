package com.opsec.fieldintelligence.data.model

enum class HazardSeverity(val level: Int, val label: String) {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical")
}
