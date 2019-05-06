package com.care.seredkin

object Configuration {
    //https://www.quora.com/Which-countries-use-Fahrenheit-as-a-measurement-of-temperature
    val imperialUnitCountries = setOf("US", "PW", "FM", "MH", "BS", "BZ", "KY")
    const val apiKey = "06dc5f76382447d7de223d102903f432"
    val defaultCities = mapOf(Pair(2950159L,"Berlin,DE"), Pair(4954380L, "Waltham,US"))
}
