package org.pabarreiro.barapp.data.mapper

import kotlinx.datetime.Instant
import org.pabarreiro.barapp.data.remote.dto.*
import org.pabarreiro.barapp.domain.model.*

fun MesaDTO.toDomain() = Mesa(id ?: 0, numeroMesa, capacidad, estado)

fun CategoriaDTO.toDomain() = Categoria(id ?: 0, nombre, descripcion ?: "")

fun ProductoDTO.toDomain() = Producto(
    id ?: 0,
    nombre,
    precio,
    stock ?: 0,
    categoriaId,
    imagenUrl,
    activo ?: true
)

fun DetalleComandaDTO.toDomain() = DetalleComanda(
    id = id,
    comandaId = comandaId ?: "",
    productoId = productoId,
    cantidad = cantidad,
    precioUnitario = precioUnitario
)

fun ComandaDTO.toDomain() = Comanda(
    id = id ?: "",
    mesaId = mesaId,
    camareroId = camareroId,
    fechaApertura = fechaApertura?.let { Instant.parse(it) } ?: Instant.fromEpochMilliseconds(0),
    fechaCierre = fechaCierre?.let { Instant.parse(it) },
    estadoPago = estadoPago ?: false,
    total = total ?: 0.0,
    detalles = detalles?.map { it.toDomain() } ?: emptyList()
)

fun Comanda.toDTO() = ComandaDTO(
    id = id,
    mesaId = mesaId,
    camareroId = camareroId,
    fechaApertura = fechaApertura.toString(),
    fechaCierre = fechaCierre?.toString(),
    estadoPago = estadoPago,
    total = total,
    detalles = detalles.map { it.toDTO() }
)

fun DetalleComanda.toDTO() = DetalleComandaDTO(
    id = id,
    comandaId = comandaId,
    productoId = productoId,
    cantidad = cantidad,
    precioUnitario = precioUnitario
)

fun PerfilDTO.toDomain() = Perfil(
    id = id,
    nombre = nombre,
    rol = rol
)
