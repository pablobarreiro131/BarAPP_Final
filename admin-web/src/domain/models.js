// src/domain/models.js

export const Rol = {
  ADMIN: 'admin',
  CAMARERO: 'camarero'
};

/**
 * @typedef {Object} Perfil
 * @property {string} id
 * @property {string} nombre
 * @property {string} rol
 */

/**
 * @typedef {Object} Mesa
 * @property {number} id
 * @property {string} nombre
 * @property {boolean} ocupada
 */

/**
 * @typedef {Object} Categoria
 * @property {number} id
 * @property {string} nombre
 */

/**
 * @typedef {Object} Producto
 * @property {number} id
 * @property {string} nombre
 * @property {number} precio
 * @property {number} categoriaId
 */
