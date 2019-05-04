package com.care.seredkin

import java.time.Instant

data class CityGroupResponse(
        val cnt: Int,
        val list: List<CityG>
)
data class CityG(
        val clouds: CloudsG,
        val coord: CoordG,
        val dt: Instant,
        val id: Long,
        val main: MainG,
        val name: String,
        val sys: SysG,
        val visibility: Int,
        val weather: List<WeatherG>,
        val wind: Wind
)
data class CloudsG(
        val all: Int
)
data class CoordG(
        val lat: Double,
        val lon: Double
)
data class MainG(
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
)
data class SysG(
        val country: String,
        val id: Int,
        val message: Double,
        val sunrise: Instant,
        val sunset: Instant,
        val type: Int
)
data class WeatherG(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
)

data class WindG(
        val deg: Int,
        val speed: Double
)