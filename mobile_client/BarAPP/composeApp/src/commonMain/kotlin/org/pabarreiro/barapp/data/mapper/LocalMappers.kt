package org.pabarreiro.barapp.data.mapper

import kotlinx.datetime.Instant
import org.pabarreiro.barapp.data.local.entity.*
import org.pabarreiro.barapp.domain.model.*

fun MesaEntity.toDomain() = Mesa(id, numeroMesa, capacidad, estado)
fun Mesa.toEntity() = MesaEntity(id, numeroMesa, capacidad, estado)

fun CategoriaEntity.toDomain() = Categoria(id, nombre, descripcion)
fun Categoria.toEntity() = CategoriaEntity(id, nombre, descripcion)

fun ProductoEntity.toDomain() = Producto(id, nombre, precio, stock, categoriaId, imagenUrl, activo)
fun Producto.toEntity() = ProductoEntity(id, nombre, precio, stock, categoriaId, imagenUrl, activo)

fun ComandaEntity.toDomain(detalles: List<DetalleComanda> = emptyList()) = Comanda(
    id = id,
    mesaId = mesaId,
    camareroId = camareroId,
    fechaApertura = Instant.fromEpochMilliseconds(fechaAperturaMillis),
    fechaCierre = fechaCierreMillis?.let { Instant.fromEpochMilliseconds(it) },
    estadoPago = estadoPago,
    total = total,
    detalles = detalles
)

fun Comanda.toEntity(isSynced: Boolean) = ComandaEntity(
    id = id,
    mesaId = mesaId,
    camareroId = camareroId,
    fechaAperturaMillis = fechaApertura.toEpochMilliseconds(),
    fechaCierreMillis = fechaCierre?.toEpochMilliseconds(),
    estadoPago = estadoPago,
    total = total,
    isSynced = isSynced
)

fun DetalleComandaEntity.toDomain() = DetalleComanda(
    id = id,
    comandaId = comandaId,
    productoId = productoId,
    cantidad = cantidad,
    precioUnitario = precioUnitario
)

fun DetalleComanda.toEntity(isSynced: Boolean) = DetalleComandaEntity(
    id = id ?: 0,
    comandaId = comandaId,
    productoId = productoId,
    cantidad = cantidad,
    precioUnitario = precioUnitario,
    isSynced = isSynced
)
