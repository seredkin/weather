package com.care.seredkin

import java.time.Instant
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random.Default.nextInt

fun generateFakeWeather(cityName: CityNameRequest): CityG {
    val r = ThreadLocalRandom.current()
    val temperature = r.nextDouble(-50.0, 50.0)
    val id = r.nextLong()
    return CityG(base = "fake",
            clouds = CloudsG(0),
            cod = r.nextInt(1000),
            coord = CoordG(r.nextDouble(-90.0, 90.0), r.nextDouble(-90.0, 90.0)),
            dt = Instant.now().minusMillis(r.nextInt(1000).toLong()),
            id = id,
            main = MainG(humidity = r.nextInt(100), pressure = r.nextInt(200) + 900, temp = temperature,
                    temp_max = temperature + r.nextDouble(2.0), temp_min = temperature - r.nextDouble(2.0)),
            name = cityName.name,
            sys = SysG(cityName.countryCode, id, r.nextDouble(), Instant.now().minusSeconds(r.nextLong(0, 10000)),
                    Instant.now().plusSeconds(r.nextLong(0, 10000)),
                    r.nextInt(1)),
            visibility = r.nextInt(100),
            weather = listOf(),
            wind = WindG(r.nextInt(36) * 10, r.nextDouble(0.0, 30.0))
    )
}

fun randomCity(cities: Set<String>): CityNameRequest {
    val at = cities.elementAt(nextInt(cities.size))
    return cityFromConfigString(at)
}

fun cityFromConfigString(configString: String): CityNameRequest {
    return CityNameRequest(name = configString.substringBefore("."), countryCode = configString.substringAfter("."))
}