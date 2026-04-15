package com.opsec.fieldintelligence.data.db.dao

import androidx.room.*
import com.opsec.fieldintelligence.data.model.Hazard
import com.opsec.fieldintelligence.data.model.HazardSeverity
import kotlinx.coroutines.flow.Flow

@Dao
interface HazardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hazard: Hazard): Long

    @Update
    suspend fun update(hazard: Hazard)

    @Delete
    suspend fun delete(hazard: Hazard)

    @Query("SELECT * FROM hazards WHERE pinId = :pinId ORDER BY reportedAt DESC")
    fun getByPinFlow(pinId: Long): Flow<List<Hazard>>

    @Query("SELECT * FROM hazards ORDER BY severity DESC, reportedAt DESC")
    fun getAllFlow(): Flow<List<Hazard>>

    @Query("SELECT * FROM hazards WHERE severity = :severity")
    fun getBySeverityFlow(severity: String): Flow<List<Hazard>>

    @Query("SELECT * FROM hazards WHERE expiresAt IS NULL OR expiresAt > :now ORDER BY severity DESC")
    fun getActiveFlow(now: Long = System.currentTimeMillis()): Flow<List<Hazard>>

    @Query("SELECT * FROM hazards WHERE id = :id")
    suspend fun getById(id: Long): Hazard?
}
