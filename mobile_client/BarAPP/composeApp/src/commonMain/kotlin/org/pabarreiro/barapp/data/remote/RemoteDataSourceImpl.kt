package org.pabarreiro.barapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.pabarreiro.barapp.data.remote.dto.*

@Serializable
private data class ErrorResponse(
    val message: String? = null,
    val error: String? = null
)

class RemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8081"
) : RemoteDataSource {    override suspend fun getMesas(): List<MesaDTO> {
        println("[BarApp] [Remote] Obteniendo LISTADO DE MESAS")
        return safeRequest { httpClient.get("$baseUrl/api/mesas") }
    }

    override suspend fun getCategorias(): List<CategoriaDTO> {
        println("[BarApp] [Remote] Obteniendo CATEGORÍAS")
        return safeRequest { httpClient.get("$baseUrl/api/categorias") }
    }

    override suspend fun getProductos(categoriaId: Long?): List<ProductoDTO> {
        println("[BarApp] [Remote] Obteniendo PRODUCTOS (categoría: $categoriaId)")
        val url = if (categoriaId != null) "$baseUrl/api/productos?categoriaId=$categoriaId" else "$baseUrl/api/productos"
        return safeRequest { httpClient.get(url) }
    }

    override suspend fun getComandasByMesa(mesaId: Long): List<ComandaDTO> {
        println("[BarApp] [Remote] Obteniendo COMANDAS para mesa $mesaId")
        return safeRequest { httpClient.get("$baseUrl/api/comandas/mesa/$mesaId") }
    }

    override suspend fun createComanda(comanda: ComandaDTO): ComandaDTO {
        println("[BarApp] [Remote] CREANDO COMANDA en servidor para mesa ${comanda.mesaId}")
        return safeRequest {
            httpClient.post("$baseUrl/api/comandas") {
                contentType(ContentType.Application.Json)
                setBody(comanda)
            }
        }
    }

    override suspend fun addDetalle(comandaId: String, detalle: DetalleComandaDTO): DetalleComandaDTO {
        println("[BarApp] [Remote] AÑADIENDO DETALLE a comanda $comandaId")
        return safeRequest {
            httpClient.post("$baseUrl/api/comandas/$comandaId/detalles") {
                contentType(ContentType.Application.Json)
                setBody(detalle)
            }
        }
    }

    override suspend fun deleteDetalle(comandaId: String, detalleId: Long) {
        println("[BarApp] [Remote] ELIMINANDO DETALLE $detalleId de comanda $comandaId")
        safeRequest<Unit> {
            httpClient.delete("$baseUrl/api/comandas/$comandaId/detalles/$detalleId")
        }
    }

    override suspend fun pagareComanda(comandaId: String): ComandaDTO {
        println("[BarApp] [Remote] PAGANDO COMANDA $comandaId")
        return safeRequest { httpClient.post("$baseUrl/api/comandas/$comandaId/pagar") }
    }

    override suspend fun getMe(): PerfilDTO {
        println("[BarApp] [Remote] Obteniendo PERFIL PROPIO del backend")
        return safeRequest { httpClient.get("$baseUrl/api/perfiles/me") }
    }

    private suspend inline fun <reified T> safeRequest(block: () -> HttpResponse): T {
        val response = block()
        if (!response.status.isSuccess()) {
            val errorBody = try {
                val bodyText = response.bodyAsText()
                Json { ignoreUnknownKeys = true }.decodeFromString<ErrorResponse>(bodyText)
            } catch (e: Exception) {
                null
            }
            val message = errorBody?.message ?: errorBody?.error ?: "Error del servidor (${response.status.value})"
            throw Exception(message)
        }
        return response.body()
    }
}
