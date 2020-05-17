package com.tohami.photo_weather.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tohami.photo_weather.data.model.WeatherPhoto

@Database(entities = [WeatherPhoto::class], version = 1, exportSchema = false)
abstract class WeatherPhotoDB : RoomDatabase() {
    abstract val weatherPhotoDao: WeatherPhotoDao

    companion object {
        private const val DATA_BASE_NAME = "weather_photo_db"
        private var INSTANCE: WeatherPhotoDB? = null
        @JvmStatic
        @Synchronized
        fun getDatabaseInstance(context: Context): WeatherPhotoDB? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherPhotoDB::class.java,
                    DATA_BASE_NAME
                ).fallbackToDestructiveMigration().build()
            }
            return INSTANCE
        }
    }
}