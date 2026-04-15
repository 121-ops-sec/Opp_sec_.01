package com.opsec.fieldintelligence.data.db.dao

import androidx.room.*
import com.opsec.fieldintelligence.data.model.FrequencyLog
import com.opsec.fieldintelligence.data.model.SignalQuality
import kotlinx.coroutines.flow.Flow

@Dao
interface FrequencyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FrequencyLog): Long

    @Update
    suspend fun update(log: FrequencyLog)

    @Delete
    suspend fun delete(log: FrequencyLog)

    @Query("SELECT * FROM frequency_logs WHERE pinId = :pinId ORDER BY loggedAt DESC")
    fun getByPinFlow(pinId: Long): Flow<List<FrequencyLog>>

    @Query("SELECT * FROM frequency_logs ORDER BY loggedAt DESC")
    fun getAllFlow(): Flow<List<FrequencyLog>>

    @Query("SELECT * FROM frequency_logs WHERE frequencyMhz BETWEEN :minMhz AND :maxMhz")
    suspend fun getByFrequencyRange(minMhz: Double, maxMhz: Double): List<FrequencyLog>

    @Query("SELECT * FROM frequency_logs WHERE signalQuality = :quality")
    suspend fun getBySignalQuality(quality: String): List<FrequencyLog>

    @Query("SELECT * FROM frequency_logs WHERE id = :id")
    suspend fun getById(id: Long): FrequencyLog?
}
