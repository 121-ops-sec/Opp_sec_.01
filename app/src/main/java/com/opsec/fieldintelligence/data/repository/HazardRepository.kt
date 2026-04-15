package com.opsec.fieldintelligence.data.repository

import com.opsec.fieldintelligence.data.db.dao.HazardDao
import com.opsec.fieldintelligence.data.model.Hazard
import com.opsec.fieldintelligence.data.model.HazardSeverity
import kotlinx.coroutines.flow.Flow

class HazardRepository(private val dao: HazardDao) {

    val allHazards: Flow<List<Hazard>> = dao.getAllFlow()

    val activeHazards: Flow<List<Hazard>> = dao.getActiveFlow()

    fun hazardsForPin(pinId: Long): Flow<List<Hazard>> = dao.getByPinFlow(pinId)

    fun hazardsBySeverity(severity: HazardSeverity): Flow<List<Hazard>> =
        dao.getBySeverityFlow(severity.name)

    suspend fun addHazard(hazard: Hazard): Long = dao.insert(hazard)

    suspend fun updateHazard(hazard: Hazard) = dao.update(hazard)

    suspend fun deleteHazard(hazard: Hazard) = dao.delete(hazard)

    suspend fun getHazardById(id: Long): Hazard? = dao.getById(id)
}
