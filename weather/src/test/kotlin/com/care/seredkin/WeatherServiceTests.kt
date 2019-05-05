package com.care.seredkin

import com.care.seredkin.WeatherService.Companion.METRIC
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import com.care.seredkin.Configuration.defaultCities as configuredCities

class WeatherServiceTests {
    private val weatherService = WeatherService(ObjectMapper().registerKotlinModule())

    @Test
    fun init() {
        WeatherController(weatherService).init()
    }

    @Test
    fun fetchFromOwmIntegrationTest() {
        val service = weatherService
        configuredCities.values.forEach {
            with(service.currentWeather(cityFromConfigString(it))) {
                assert(this.main.temp.isFinite())
                assert(configuredCities.any { str -> cityFromConfigString(str.value).name == it.substringBefore(".") })
            }
        }
    }

    @Test
    fun streamUnitTest() {
        val toList = weatherService.fake(1, TimeUnit.MILLISECONDS).take(100).toList().blockingGet().toList()
        assert(toList.size == 100)
    }

    @Test
    fun controllerUnitTest() {
        val cityWeatherResponse = WeatherController(weatherService)
                .fakeAll(1).firstOrError().blockingGet()
        with(cityWeatherResponse) {
            assert(this.name.isNotBlank())
        }

    }

    @Test
    fun controllerIntegrationTest() {
        val controller = WeatherController(weatherService)
        val berlin = "Berlin"
        val waltham = "Waltham"
        val irkutsk = "Irkutsk"
        with(controller.currentWeather("DE", berlin)) {
            assert(this.name == berlin)
        }
        with(controller.currentWeather("US", waltham)) {
            assert(this.name == waltham)
        }
        with(controller.currentWeather("RU", irkutsk)) {
            assert(this.name == irkutsk)
        }
    }

    @Test
    fun groupOfIdsRequestTest() {
        val controller = WeatherController(weatherService)
        val cityIds = Configuration.defaultCities.keys.joinToString(",")
        val cityMap = controller.currentWeatherInCities(cityIds, METRIC, 1).firstOrError().blockingGet()
        assert(cityMap.size == Configuration.defaultCities.size)
    }

    @Test fun testFakeGeneration(){
        val controller = WeatherController(weatherService)
        val cityG = controller.fakeAll(1).firstOrError().blockingGet()
        assert(Configuration.defaultCities.containsValue(cityG.owmKey()))
    }

}