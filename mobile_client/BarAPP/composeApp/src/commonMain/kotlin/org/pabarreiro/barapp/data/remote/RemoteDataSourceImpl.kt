package org.pabarreiro.barapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.pabarreiro.barapp.data.remote.dto.*

class RemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8081" // Android Emulator localhost
) : RemoteDataSource {    override suspend fun getMesas(): List<MesaDTO> {
        println("[BarApp] [Remote] Obteniendo LISTADO DE MESAS")
        return httpClient.get("$baseUrl/api/mesas").body()
    }

    override suspend fun getCategorias(): List<CategoriaDTO> {
        println("[BarApp] [Remote] Obteniendo CATEGORÍAS")
        return httpClient.get("$baseUrl/api/categorias").body()
    }

    override suspend fun getProductos(categoriaId: Long?): List<ProductoDTO> {
        println("[BarApp] [Remote] Obteniendo PRODUCTOS (categoría: $categoriaId)")
        val url = if (categoriaId != null) "$baseUrl/api/productos?categoriaId=$categoriaId" else "$baseUrl/api/productos"
        return httpClient.get(url).body()
    }

    override suspend fun createComanda(comanda: ComandaDTO): ComandaDTO {
        println("[BarApp] [Remote] CREANDO COMANDA en servidor para mesa ${comanda.mesaId}")
        return httpClient.post("$baseUrl/api/comandas") {
            contentType(ContentType.Application.Json)
            setBody(comanda)
        }.body()
    }

    override suspend fun addDetalle(comandaId: String, detalle: DetalleComandaDTO): DetalleComandaDTO {
        println("[BarApp] [Remote] AÑADIENDO DETALLE a comanda $comandaId")
        return httpClient.post("$baseUrl/api/comandas/$comandaId/detalles") {
            contentType(ContentType.Application.Json)
            setBody(detalle)
        }.body()
    }

    override suspend fun pagareComanda(comandaId: String): ComandaDTO {
        println("[BarApp] [Remote] PAGANDO COMANDA $comandaId")
        return httpClient.post("$baseUrl/api/comandas/$comandaId/pagar").body()
    }

    override suspend fun getMe(): PerfilDTO {
        println("[BarApp] [Remote] Obteniendo PERFIL PROPIO del backend")
        return httpClient.get("$baseUrl/api/perfiles/me").body()
    }
}
