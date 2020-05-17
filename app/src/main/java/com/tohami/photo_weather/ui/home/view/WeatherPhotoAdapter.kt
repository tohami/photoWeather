package com.tohami.photo_weather.ui.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tohami.photo_weather.R
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.ui.home.view.WeatherPhotoAdapter.WeatherPhotoViewHolder
import com.tohami.photo_weather.utils.ToFormattedTime
import kotlinx.android.synthetic.main.item_weather_photo.view.*
import java.io.File
import kotlin.math.roundToInt

class WeatherPhotoAdapter(
    private val weatherPhotosList: ArrayList<WeatherPhoto>?,
    private val onWeatherPhotoClickListener: OnWeatherPhotoClickListener
) : RecyclerView.Adapter<WeatherPhotoViewHolder>() {

    fun setItems(items: List<WeatherPhoto>) {
        weatherPhotosList?.clear()
        weatherPhotosList?.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherPhotoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_photo, parent, false)
        return WeatherPhotoViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: WeatherPhotoViewHolder,
        position: Int
    ) {
        val weatherPhoto = weatherPhotosList!![position]
        holder.bind(weatherPhoto)
    }

    override fun getItemCount(): Int {
        return if (weatherPhotosList != null && !weatherPhotosList.isEmpty()) {
            weatherPhotosList.size
        } else {
            0
        }
    }

    inner class WeatherPhotoViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        fun bind(weatherPhoto: WeatherPhoto) {
            Picasso.get().load(File(weatherPhoto.photoPath))
                .into(itemView.weatherPhotoIv)

            itemView.locationNameTv.text = String.format(
                "%s, %s",
                weatherPhoto.currentWeather.name,
                weatherPhoto.currentWeather.sys?.country
            )
            itemView.dateTimeTv.text = weatherPhoto.currentWeather.dt?.toLong()?.ToFormattedTime()
            itemView.currentTempTv.text =
                weatherPhoto.currentWeather.main?.temp?.roundToInt().toString()
            itemView.currentTempUnit.text = "C"

            itemView.weatherPhotoIv.transitionName = "weatherPhotoIv" + weatherPhoto.photoPath
            itemView.currentTempTv.transitionName = "currentTempTv" + weatherPhoto.photoPath
            itemView.locationNameTv.transitionName = "locationNameTv" + weatherPhoto.photoPath

            itemView.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    itemView.weatherPhotoIv to itemView.weatherPhotoIv.transitionName,
                    itemView.currentTempTv to itemView.currentTempTv.transitionName,
                    itemView.locationNameTv to itemView.locationNameTv.transitionName
                )
                onWeatherPhotoClickListener.onWeatherPhotoClick(weatherPhoto, extras)
            }
        }
    }

}