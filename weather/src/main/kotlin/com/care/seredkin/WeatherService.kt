package com.care.seredkin

import com.care.seredkin.Configuration.apiKey
import com.care.seredkin.Configuration.imperialUnitCountries
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.DefaultHttpClient
import io.reactivex.Flowable
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class WeatherService(private val mapper: ObjectMapper) {
    private val client = DefaultHttpClient(URL("https://api.openweathermap.org"))

    companion object {
        const val IMPERIAL = "imperial"
        const val METRIC = "metric"
    }

    fun currentWeather(cityAndCountry: String): City {
        val units = if (imperialUnitCountries.contains(cityAndCountry.substringAfter("."))) IMPERIAL else METRIC
        val request = HttpRequest.GET<Any>("/data/2.5/weather?q=$cityAndCountry&appid=$apiKey&units=$units")
        val cityResponse = client.toBlocking().retrieve(request, City::class.java)
        return cityResponse
    }

    fun currentWeatherByIds(cityIds: LongArray, units: String, interval: Int): Flowable<Map<String, City>> {
        val request = HttpRequest.GET<Any>("/data/2.5/group?" +
                "id=${cityIds.joinToString(",")}&" +
                "appid=$apiKey&" +
                "units=${if (units == IMPERIAL) IMPERIAL else METRIC}")
        return Flowable.interval(interval.toLong(), TimeUnit.SECONDS).onBackpressureDrop()
                .map { client.toBlocking().retrieve(request, CityGroupResponse::class.java).list.associateBy { it.id.toString() } }
    }

}


