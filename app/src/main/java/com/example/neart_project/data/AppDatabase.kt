package com.example.neart_project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase - the app's database, saved on the phone so lifts are kept after the app closes.
 *
 * - entities: the tables in the database (just Lift for now)
 * - version: if the Lift class changes later (e.g. a new field), this number must go up
 *
 * LiftViewModel calls getDatabase() to open it, then liftDao() to run queries.
 */
@Database(entities = [Lift::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // Gives access to the queries in LiftDao
    abstract fun liftDao(): LiftDao

    // companion object: lets us call AppDatabase.getDatabase() without creating an AppDatabase first
    companion object {
        // The one database, saved here once it's been created.
        // @Volatile makes sure every part of the app sees the latest value
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: only one database is ever created, and everyone shares it.
        // Opening a database is slow, so we don't want to do it more than once
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing database, or create it if there isn't one yet.
            // synchronized stops two parts of the app creating it at the same time
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "neart_database"   // the database file name
                ).build().also { INSTANCE = it }   // save it so it's reused next time
            }
        }
    }
}
