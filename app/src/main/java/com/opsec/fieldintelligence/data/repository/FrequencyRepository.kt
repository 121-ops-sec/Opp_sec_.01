package com.opsec.fieldintelligence.data.repository

import com.opsec.fieldintelligence.data.db.dao.FrequencyLogDao
import com.opsec.fieldintelligence.data.model.FrequencyLog
import com.opsec.fieldintelligence.data.model.SignalQuality
import kotlinx.coroutines.flow.Flow

class FrequencyRepository(private val dao: FrequencyLogDao) {

    val allLogs: Flow<List<FrequencyLog>> = dao.getAllFlow()

    fun logsForPin(pinId: Long): Flow<List<FrequencyLog>> = dao.getByPinFlow(pinId)

    suspend fun addLog(log: FrequencyLog): Long = dao.insert(log)

    suspend fun updateLog(log: FrequencyLog) = dao.update(log)

    suspend fun deleteLog(log: FrequencyLog) = dao.delete(log)

    suspend fun getLogById(id: Long): FrequencyLog? = dao.getById(id)

    suspend fun getByFrequencyRange(minMhz: Double, maxMhz: Double): List<FrequencyLog> =
        dao.getByFrequencyRange(minMhz, maxMhz)

    suspend fun getBySignalQuality(quality: SignalQuality): List<FrequencyLog> =
        dao.getBySignalQuality(quality.name)
}
