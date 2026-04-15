package com.opsec.fieldintelligence.data.repository

import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.Pin
import org.junit.Assert.assertEquals
import org.junit.Test

class PinRepositoryTest {

    @Test
    fun pin_dataClass_equality() {
        val pin1 = Pin(
            id = 1L,
            latitude = 51.5074,
            longitude = -0.1278,
            title = "Test Pin",
            markerType = MarkerType.GENERIC
        )
        val pin2 = pin1.copy()
        assertEquals(pin1, pin2)
    }

    @Test
    fun pin_markerType_default() {
        val pin = Pin(
            latitude = 0.0,
            longitude = 0.0,
            title = "Default",
            markerType = MarkerType.GENERIC
        )
        assertEquals(MarkerType.GENERIC, pin.markerType)
    }
}
