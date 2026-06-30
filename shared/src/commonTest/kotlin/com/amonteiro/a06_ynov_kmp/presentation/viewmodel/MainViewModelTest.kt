package com.amonteiro.a06_ynov_kmp.presentation.viewmodel

import com.amonteiro.a06_ynov_kmp.data.remote.WeatherApiDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertTrue

class MainViewModelTest {

    @Test
    fun testLoadWeathersRealRequest() = runTest {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
        }
        val dataSource = WeatherApiDataSource(client)
        val viewModel = MainViewModel(dataSource)
        
        println("DEBUG CI - Key utilisée : ${com.amonteiro.a06_ynov_kmp.BuildConfig.WEATHER_API_KEY.take(5)}...")
        println("DEBUG CI - Lancement de la requête pour Paris...")
        viewModel.loadWeathers("Paris")
        
        // On attend d'avoir soit de la donnée, soit une erreur (max 15s)
        var count = 0
        while (viewModel.dataList.value.isEmpty() && viewModel.errorMessage.value.isEmpty() && count < 150) {
            delay(100)
            count++
        }
        
        println("DEBUG CI - Fin d'attente après ${count * 100}ms")
        println("DEBUG CI - Liste size : ${viewModel.dataList.value.size}")
        println("DEBUG CI - Erreur : '${viewModel.errorMessage.value}'")

        assertTrue(viewModel.errorMessage.value.isEmpty(), "L'API a renvoyé une erreur : ${viewModel.errorMessage.value}")
        assertTrue(viewModel.dataList.value.isNotEmpty(), "La liste est vide, la requête n'a probablement pas abouti à temps.")
    }
}
