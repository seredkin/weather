package com.care.seredkin

import com.care.seredkin.Configuration.apiKey
import com.care.seredkin.Configuration.defaultCities
import com.care.seredkin.Configuration.imperialUnitCountries
import io.micronaut.cache.annotation.CachePut
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.DefaultHttpClient
import io.micronaut.retry.annotation.Retryable
import io.reactivex.Flowable
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
open class WeatherService() {
    private val client = DefaultHttpClient(URL("https://api.openweathermap.org"))


    @CachePut("weather")
    @Retryable(attempts = "12", delay = "5s")
    open fun currentWeather(city: City): CityWeatherResponse {
        val units = if (imperialUnitCountries.contains(city.countryCode)) "imperial" else "metrics"
        val request = HttpRequest.GET<Any>("/data/2.5/weather?q=${city.name},${city.countryCode}&appid=$apiKey&units=$units")
        return client.toBlocking().retrieve(request, CityWeatherResponse::class.java)
    }


    fun findAll(interval:Long = 3L, timeUnit: TimeUnit = TimeUnit.SECONDS): Flowable<CityWeatherResponse> {
        //data streaming simulation
        return Flowable.interval(interval, timeUnit)
                .onBackpressureDrop()
                .map { generateFakeWeather(randomCity(defaultCities)) }
    }
}

data class City(val name: String, val countryCode: String)


