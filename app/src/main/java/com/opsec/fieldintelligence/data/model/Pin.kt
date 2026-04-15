package com.opsec.fieldintelligence.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pins")
data class Pin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val markerType: MarkerType,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isVisible: Boolean = true,
    val colorTag: String = "#FF5722"
)
