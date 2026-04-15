package com.opsec.fieldintelligence.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hazards",
    foreignKeys = [ForeignKey(
        entity = Pin::class,
        parentColumns = ["id"],
        childColumns = ["pinId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pinId")]
)
data class Hazard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pinId: Long,
    val severity: HazardSeverity,
    val category: String,
    val description: String,
    val reportedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)
