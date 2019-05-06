package com.care.seredkin

import java.time.Instant

data class CityGroupResponse(val cnt: Int, val list: List<City>)
data class City(
        var base: String? = null,//appears only in CityByName response
        var cod: Int? = null,
        val clouds: Clouds,
        val coord: Coord,
        val dt: Instant,
        val id: Long,
        val main: Main,
        val name: String,
        val sys: Sys,
        val visibility: Int,
        val weather: List<Weather>,
        val wind: Wind
)

data class Clouds(val all: Int)
data class Coord(val lat: Double, val lon: Double)
data class Main(
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
)

data class Wind(val deg: Int, val speed: Double)
data class Sys(
        val country: String,
        val id: Long,
        val message: Double,
        val sunrise: Instant,
        val sunset: Instant,
        val type: Int
)

data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
)
