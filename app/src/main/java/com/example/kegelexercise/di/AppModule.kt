package com.example.kegelexercise.di

import android.content.Context
import androidx.room.Room
import com.example.kegelexercise.data.db.AppDatabase
import com.example.kegelexercise.data.db.ExerciseRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideExerciseRecordDao(db: AppDatabase): ExerciseRecordDao =
        db.exerciseRecordDao()
}
