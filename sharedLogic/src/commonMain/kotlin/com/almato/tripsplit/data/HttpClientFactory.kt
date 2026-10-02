package com.almato.tripsplit.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

fun createHttpClient(): HttpClient {
    return HttpClient {
        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }
    }
}