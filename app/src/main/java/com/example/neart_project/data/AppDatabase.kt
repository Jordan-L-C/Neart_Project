package com.example.neart_project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lift::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun liftDao(): LiftDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Only ever create one database for the whole app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "neart_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}