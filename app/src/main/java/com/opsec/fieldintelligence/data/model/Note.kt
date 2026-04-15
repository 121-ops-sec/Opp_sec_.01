package com.opsec.fieldintelligence.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Pin::class,
        parentColumns = ["id"],
        childColumns = ["pinId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pinId")]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pinId: Long,
    val title: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
