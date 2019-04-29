package com.care.seredkin

import io.micronaut.cache.annotation.CachePut
import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.DefaultHttpClient
import io.micronaut.retry.annotation.Retryable
import java.net.URL

@Controller
class WeatherController() {
    private val client = DefaultHttpClient(URL("https://api.openweathermap.org"))


    @Get("/weather/{country}/{city}")
    @CachePut("weather")
    @Retryable(attempts = "12", delay = "5s")
    fun currentWeather(country: String, city: String): OpenWeatherMapWeatherResponse {
        val units = if (imperialUnitCountries.contains(country)) "imperial" else "metrics"
        val request = HttpRequest.GET<Any>("/data/2.5/weather?q=$city,$country&appid=$apiKey&units=$units")
        return client.toBlocking().retrieve(request, OpenWeatherMapWeatherResponse::class.java)
    }

    companion object {
        //https://www.quora.com/Which-countries-use-Fahrenheit-as-a-measurement-of-temperature
        private val imperialUnitCountries = setOf("us", "pw", "fm", "mh", "bs", "bz", "ky")
        private const val apiKey = "06dc5f76382447d7de223d102903f432"
    }

}