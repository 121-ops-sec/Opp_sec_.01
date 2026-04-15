package com.opsec.fieldintelligence.data.db.dao

import androidx.room.*
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.Pin
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pin: Pin): Long

    @Update
    suspend fun update(pin: Pin)

    @Delete
    suspend fun delete(pin: Pin)

    @Query("SELECT * FROM pins WHERE id = :id")
    suspend fun getById(id: Long): Pin?

    @Query("SELECT * FROM pins ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<Pin>>

    @Query("SELECT * FROM pins WHERE markerType = :type ORDER BY createdAt DESC")
    fun getByTypeFlow(type: String): Flow<List<Pin>>

    @Query("""
        SELECT * FROM pins
        WHERE latitude BETWEEN :south AND :north
        AND longitude BETWEEN :west AND :east
        AND isVisible = 1
    """)
    suspend fun getPinsInBounds(
        north: Double,
        south: Double,
        east: Double,
        west: Double
    ): List<Pin>

    @Query("UPDATE pins SET isVisible = :visible WHERE markerType = :type")
    suspend fun setVisibilityByType(type: String, visible: Boolean)

    @Query("DELETE FROM pins WHERE id = :id")
    suspend fun deleteById(id: Long)
}
