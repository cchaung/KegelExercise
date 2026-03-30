package com.example.kegelexercise.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ExerciseRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseRecordDao(): ExerciseRecordDao

    companion object {
        const val DATABASE_NAME = "kegel_exercise.db"
    }
}
