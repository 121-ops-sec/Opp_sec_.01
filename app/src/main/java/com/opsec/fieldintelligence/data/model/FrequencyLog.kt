package com.opsec.fieldintelligence.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "frequency_logs",
    foreignKeys = [ForeignKey(
        entity = Pin::class,
        parentColumns = ["id"],
        childColumns = ["pinId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pinId")]
)
data class FrequencyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pinId: Long,
    val frequencyMhz: Double,
    val modulationType: String,
    val signalQuality: SignalQuality,
    val rxNotes: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)
