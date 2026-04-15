package com.opsec.fieldintelligence.data.db.converter

import androidx.room.TypeConverter
import com.opsec.fieldintelligence.data.model.HazardSeverity
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.SignalQuality

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value

    @TypeConverter
    fun toTimestamp(value: Long?): Long? = value
}

class EnumConverters {
    @TypeConverter
    fun fromMarkerType(value: MarkerType): String = value.name

    @TypeConverter
    fun toMarkerType(value: String): MarkerType = MarkerType.valueOf(value)

    @TypeConverter
    fun fromHazardSeverity(value: HazardSeverity): String = value.name

    @TypeConverter
    fun toHazardSeverity(value: String): HazardSeverity = HazardSeverity.valueOf(value)

    @TypeConverter
    fun fromSignalQuality(value: SignalQuality): String = value.name

    @TypeConverter
    fun toSignalQuality(value: String): SignalQuality = SignalQuality.valueOf(value)
}
