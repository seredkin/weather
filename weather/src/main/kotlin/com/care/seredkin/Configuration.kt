package com.care.seredkin

object Configuration {
    //https://www.quora.com/Which-countries-use-Fahrenheit-as-a-measurement-of-temperature
    val imperialUnitCountries = setOf("us", "pw", "fm", "mh", "bs", "bz", "ky")
    const val apiKey = "06dc5f76382447d7de223d102903f432"
    val defaultCities = mapOf(Pair(2950159L,"DE.Berlin"), Pair(4954380L, "US.Waltham"))
}
