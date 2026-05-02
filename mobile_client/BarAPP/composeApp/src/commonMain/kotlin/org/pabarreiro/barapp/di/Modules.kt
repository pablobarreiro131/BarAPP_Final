package org.pabarreiro.barapp.di

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import org.pabarreiro.barapp.data.local.RoomSessionManager
import org.pabarreiro.barapp.data.BarRepositoryImpl
import org.pabarreiro.barapp.data.local.BarDatabase
import org.pabarreiro.barapp.data.local.LocalDataSource
import org.pabarreiro.barapp.data.local.LocalDataSourceImpl
import org.pabarreiro.barapp.data.remote.RemoteDataSource
import org.pabarreiro.barapp.data.remote.RemoteDataSourceImpl
import org.pabarreiro.barapp.domain.repository.BarRepository
import org.pabarreiro.barapp.domain.usecase.*
import org.pabarreiro.barapp.presentation.viewmodel.*

val appModule = module {
    // Network
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(io.ktor.client.plugins.auth.Auth) {
                bearer {
                    loadTokens {
                        val supabaseClient = getKoin().get<SupabaseClient>()
                        val currentToken = supabaseClient.auth.currentSessionOrNull()?.accessToken
                        if (currentToken != null) {
                            BearerTokens(currentToken, currentToken) // Supabase handles refresh itself
                        } else null
                    }
                }
            }
        }
    }
    
    // Supabase
    single {
        createSupabaseClient(
            supabaseUrl = "https://lrtbmykbvjhslabsfpir.supabase.co",
            supabaseKey = "sb_publishable_yjE0ZwVxZ8Ft9QmKTJ5X9w_v-75JacN"
        ) {
            install(Auth) {
                sessionManager = RoomSessionManager(get())
            }
        }
    }
    
    // Remote Data Source
    single<RemoteDataSource> { RemoteDataSourceImpl(get()) }
    
    // Local Data Source
    single { get<BarDatabase>().barDao() }
    single<LocalDataSource> { LocalDataSourceImpl(get()) }
}

val repositoryModule = module {
    single<BarRepository> { BarRepositoryImpl(get(), get(), get()) }
}

val useCaseModule = module {
    single { GetTables(get()) }
    single { GetMenu(get()) }
    single { GetActiveComandaUseCase(get()) }
    single { CreateComandaUseCase(get()) }
    single { AddDetalleUseCase(get()) }
    single { RemoveDetalleUseCase(get()) }
    single { PagarComandaUseCase(get()) }
}

val viewModelModule = module {
    viewModel { TablesViewModel(get(), get()) }
    viewModel { MenuViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ComandaViewModel(get(), get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
}

fun initKoin(config: org.koin.dsl.KoinAppDeclaration? = null) = org.koin.core.context.startKoin {
    config?.invoke(this)
    modules(appModule, repositoryModule, useCaseModule, viewModelModule)
}
