package com.example.eventdicoding.data.database.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity

@Database(entities = [DetailDataEventEntity::class], version = 2)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun eventDAO(): EventDAO

    companion object {
        @Volatile
        private var INSTANCE: EventRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): EventRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventRoomDatabase::class.java, "event_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}