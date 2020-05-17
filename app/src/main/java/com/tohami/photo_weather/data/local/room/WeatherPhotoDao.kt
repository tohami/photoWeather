package com.tohami.photo_weather.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tohami.photo_weather.data.model.WeatherPhoto
import io.reactivex.Completable
import io.reactivex.Observable


@Dao
interface WeatherPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherPhoto: WeatherPhoto?)

    @Query("SELECT * FROM weatherPhoto")
    fun weatherPhotoList(): Observable<List<WeatherPhoto>?>

    @Insert
    fun insertWeatherPhoto(weatherPhoto: WeatherPhoto): Completable
}