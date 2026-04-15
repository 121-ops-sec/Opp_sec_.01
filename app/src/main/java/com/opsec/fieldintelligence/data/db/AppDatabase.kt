package com.opsec.fieldintelligence.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.opsec.fieldintelligence.data.db.converter.DateConverter
import com.opsec.fieldintelligence.data.db.converter.EnumConverters
import com.opsec.fieldintelligence.data.db.dao.FrequencyLogDao
import com.opsec.fieldintelligence.data.db.dao.HazardDao
import com.opsec.fieldintelligence.data.db.dao.NoteDao
import com.opsec.fieldintelligence.data.db.dao.PinDao
import com.opsec.fieldintelligence.data.model.FrequencyLog
import com.opsec.fieldintelligence.data.model.Hazard
import com.opsec.fieldintelligence.data.model.Note
import com.opsec.fieldintelligence.data.model.Pin

@Database(
    entities = [Pin::class, Note::class, FrequencyLog::class, Hazard::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, EnumConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pinDao(): PinDao
    abstract fun noteDao(): NoteDao
    abstract fun frequencyLogDao(): FrequencyLogDao
    abstract fun hazardDao(): HazardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "field_intelligence.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
