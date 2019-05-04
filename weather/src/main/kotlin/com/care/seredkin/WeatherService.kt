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
import org.apache.commons.compress.compressors.CompressorStreamFactory
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

    open fun currentWeather(cityName: CityNameRequest): CityG {
        val units = if (imperialUnitCountries.contains(cityName.countryCode)) IMPERIAL else METRIC
        val request = HttpRequest.GET<Any>("/data/2.5/weather?q=${cityName.name},${cityName.countryCode}&appid=$apiKey&units=$units")
        val cityResponse = client.toBlocking().retrieve(request, CityG::class.java)
        val cityEntity = CityEntity(cityResponse.id, cityResponse.name, cityResponse.sys.country)
        cache.put(cityEntity.owmKey(), cityEntity)
        return cityResponse
    }

    fun fake(interval: Int = 1, timeUnit: TimeUnit = TimeUnit.SECONDS): Flowable<CityNameResponse> {
        //data streaming simulation
        return Flowable.interval(interval.toLong(), timeUnit)
                .onBackpressureDrop()
                .map { generateFakeWeather(randomCity(defaultCities)) }
    }

    fun currentWeatherByIds(cityIds: LongArray, units: String, interval: Int): Flowable<Map<String, CityG>> {
        val request = HttpRequest.GET<Any>("/data/2.5/group?" +
                "id=${cityIds.joinToString(",")}&" +
                "appid=$apiKey&" +
                "units=${if (units == IMPERIAL) IMPERIAL else METRIC}")
        return Flowable.interval(interval.toLong(), TimeUnit.SECONDS).onBackpressureDrop()
                .map { client.toBlocking().retrieve(request, CityGroupResponse::class.java).list.associateBy { it.owmKey() } }
    }

    fun init() {
        val loader = ResourceResolver().getResourceAsStream("classpath:city.list.zip")
        if (loader.isPresent) {
            val owmData: LinkedList<CityEntityOwm> = mapper.readerFor(TypeFactory.defaultInstance()
                    .constructCollectionType(LinkedList::class.java, CityEntityOwm::class.java))
                    .readValue(CompressorStreamFactory().createCompressorInputStream((loader.get())))
            owmData.filter { it.name.contains("Berlin") }.forEach { println(it) }
        }
    }
}

data class CityEntity(val id: Long, val name: String, val countryCode: String)
data class CityNameRequest(val name: String, val countryCode: String)
data class CityEntityOwm(val id: Long, val name: String, val country: String, val coord: Coord)

private fun CityEntity.owmKey() = "${this.name}.${this.countryCode}"
private fun CityG.owmKey() = "${this.name}.${this.sys.country}"



