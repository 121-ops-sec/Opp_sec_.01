package com.opsec.fieldintelligence.data.repository

import com.opsec.fieldintelligence.data.db.dao.PinDao
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.Pin
import kotlinx.coroutines.flow.Flow

class PinRepository(private val dao: PinDao) {

    val allPins: Flow<List<Pin>> = dao.getAllFlow()

    fun pinsOfType(type: MarkerType): Flow<List<Pin>> = dao.getByTypeFlow(type.name)

    suspend fun addPin(pin: Pin): Long = dao.insert(pin)

    suspend fun updatePin(pin: Pin) = dao.update(pin)

    suspend fun deletePin(pin: Pin) = dao.delete(pin)

    suspend fun getPinById(id: Long): Pin? = dao.getById(id)

    suspend fun getPinsInViewport(
        north: Double,
        south: Double,
        east: Double,
        west: Double
    ): List<Pin> = dao.getPinsInBounds(north, south, east, west)

    suspend fun setLayerVisibility(type: MarkerType, visible: Boolean) =
        dao.setVisibilityByType(type.name, visible)
}
