package com.care.seredkin

import com.care.seredkin.Configuration.apiKey
import com.care.seredkin.Configuration.defaultCities
import com.care.seredkin.Configuration.imperialUnitCountries
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.micronaut.core.io.ResourceResolver
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.DefaultHttpClient
import io.reactivex.Flowable
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
open class WeatherService(private val mapper: ObjectMapper) {
    private val client = DefaultHttpClient(URL("https://api.openweathermap.org"))
    private val cache: Cache<String, CityEntity> = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.SECONDS).build()

    companion object {
        const val IMPERIAL = "imperial"
        const val METRIC = "metric"
    }

    open fun currentWeather(cityAndCountry: String): CityG {
        val units = if (imperialUnitCountries.contains(cityAndCountry.substringAfter(".").toLowerCase())) IMPERIAL else METRIC
        val request = HttpRequest.GET<Any>("/data/2.5/weather?q=$cityAndCountry&appid=$apiKey&units=$units")
        val cityResponse = client.toBlocking().retrieve(request, CityG::class.java)
        val cityEntity = CityEntity(cityResponse.id, cityResponse.name, cityResponse.sys.country)
        cache.put(cityEntity.owmKey(), cityEntity)
        return cityResponse
    }

    fun fake(interval: Int = 1, timeUnit: TimeUnit = TimeUnit.SECONDS): Flowable<CityG> {
        //data streaming simulation
        return Flowable.interval(interval.toLong(), timeUnit)
                .onBackpressureDrop()
                .map { generateFakeWeather(randomCity(defaultCities.values.toSet())) }
    }

    fun currentWeatherByIds(cityIds: LongArray, units: String, interval: Int): Flowable<Map<String, CityG>> {
        val request = HttpRequest.GET<Any>("/data/2.5/group?" +
                "id=${cityIds.joinToString(",")}&" +
                "appid=$apiKey&" +
                "units=${if (units == IMPERIAL) IMPERIAL else METRIC}")
        return Flowable.interval(interval.toLong(), TimeUnit.SECONDS).onBackpressureDrop()
                .map { client.toBlocking().retrieve(request, CityGroupResponse::class.java).list.associateBy { it.id.toString() } }
    }

    fun init() {
        val path = "classpath:city.list.json"
        ResourceResolver().getResourceAsStream(path).map {
            val owmData: LinkedList<CityEntityOwm> = mapper.readerFor(TypeFactory.defaultInstance()
                    .constructCollectionType(LinkedList::class.java, CityEntityOwm::class.java)).readValue(it)
            owmData.filter { it.name.contains("Berlin") || it.name.contains("Waltham") }.forEach { println(it) }
        }.orElseThrow() { IllegalStateException("Resource with path $path not found") }
    }

}

data class CityEntity(val id: Long, val name: String, val countryCode: String)
data class CityNameRequest(val name: String, val countryCode: String)
data class CityEntityOwm(val id: Long, val name: String, val country: String, val coord: CoordG)

fun CityEntity.owmKey() = "${this.name}.${this.countryCode}"
fun CityG.owmKey() = "${this.name}.${this.sys.country}"



