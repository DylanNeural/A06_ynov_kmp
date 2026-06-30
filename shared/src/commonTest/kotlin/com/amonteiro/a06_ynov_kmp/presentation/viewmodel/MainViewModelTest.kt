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
        
        viewModel.loadWeathers("Paris")
        
        // Attente de la fin de la requête (max 5s)
        var count = 0
        while (viewModel.runInProgress.value && count < 50) {
            delay(100)
            count++
        }
        
        assertTrue(viewModel.dataList.value.isNotEmpty(), "La liste de météo devrait être remplie")
        assertTrue(viewModel.errorMessage.value.isEmpty(), "Il ne devrait pas y avoir d'erreur : ${viewModel.errorMessage.value}")
    }
}
