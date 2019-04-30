package com.care.seredkin

import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun generateFakeWeather(city: City): CityWeatherResponse {
    val r = ThreadLocalRandom.current()
    val temperature = r.nextDouble(50.0) * r.plusOrMinus()
    val id = r.nextInt()
    return CityWeatherResponse(base = "fake",
            clouds = Clouds(0),
            cod = r.nextInt(1000),
            coord = Coord(r.nextDouble(90.0) * r.plusOrMinus(), r.nextDouble(90.0) * r.plusOrMinus()),
            dt = Instant.now().minusMillis(r.nextInt(1000).toLong()),
            id = id,
            main = Main(humidity = r.nextInt(100), pressure = r.nextInt(200) + 900, temp = temperature,
                    temp_max = temperature + r.nextDouble(2.0), temp_min = temperature - r.nextDouble(2.0)),
            name = city.name,
            sys = Sys(city.countryCode, id, r.nextDouble(), Instant.now().minusSeconds(r.nextLong(0, 10000)),
                    Instant.now().plusSeconds(r.nextLong(0, 10000)),
                    r.nextInt(1)),
            visibility = r.nextInt(100),
            weather = listOf(),
            wind = Wind(r.nextInt(36) * 10, r.nextDouble(0.0, 30.0))
    )
}

private fun Random.plusOrMinus() = if (this.nextBoolean()) 1 else -1

fun randomCity(cities: Set<String>): City {
    val at = cities.elementAt(Random().nextInt(cities.size))
    return cityFromConfigString(at)
}

fun cityFromConfigString(configString: String): City {
    return City(configString.substringAfter("."), configString.substringBefore("."))
}