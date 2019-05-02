package com.care.seredkin

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import com.care.seredkin.Configuration.defaultCities as configuredCities

class WeatherServiceTests {
    @Test
    fun fetchFromOwmIntegrationTest() {
        val service = WeatherService()
        configuredCities.forEach {
            with(service.currentWeather(cityFromConfigString(it))) {
                assert(this.main.temp.isFinite())
                assert(configuredCities.any { str -> cityFromConfigString(str).name == it.substringAfter(".") })
            }
        }
    }

    @Test
    fun streamUnitTest() {
        val service = WeatherService()
        val toList = service.fetch(1, TimeUnit.MILLISECONDS).take(100).toList().blockingGet().toList()
        assert(toList.size == 100)
    }

    @Test
    fun controllerUnitTest() {
        val cityWeatherResponse = WeatherController(WeatherService()).findAll().firstOrError().blockingGet()
        with(cityWeatherResponse) {
            assert(this.name.isNotBlank())
        }

    }

    @Test
    fun controllerIntegrationTest() {
        val controller = WeatherController(WeatherService())
        val berlin = "Berlin"
        val waltham = "Waltham"
        val irkutsk = "Irkutsk"
        with(controller.currentWeather("de", berlin)) {
            assert(this.name == berlin)
        }
        with(controller.currentWeather("us", waltham)) {
            assert(this.name == waltham)
        }
        with(controller.currentWeather("ru", irkutsk)) {
            assert(this.name == irkutsk)
        }
    }

}