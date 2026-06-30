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
        
        // Attente de la fin de la requête (max 10s pour être sûr)
        var count = 0
        while (viewModel.runInProgress.value && count < 100) {
            delay(100)
            count++
        }
        
        // On affiche l'erreur dans la console pour la voir dans les logs GitHub
        if (viewModel.errorMessage.value.isNotEmpty()) {
            println("ERREUR DÉTECTÉE : ${viewModel.errorMessage.value}")
        }

        assertTrue(viewModel.errorMessage.value.isEmpty(), "Erreur API détectée : ${viewModel.errorMessage.value}")
        assertTrue(viewModel.dataList.value.isNotEmpty(), "La liste de météo est vide")
    }
}
