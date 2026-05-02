import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import AddIcon from '@mui/icons-material/Add';
import CategoryIcon from '@mui/icons-material/Category';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import InventoryIcon from '@mui/icons-material/Inventory';
import ImageIcon from '@mui/icons-material/Image';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ConfirmDialog from '../components/ConfirmDialog';

import '../styles/MenuPage.css';

const MenuPage = () => {
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('products');

  const [newCat, setNewCat] = useState({ nombre: '', descripcion: '' });
  const [newProd, setNewProd] = useState({
    nombre: '',
    precio: '',
    categoriaId: '',
    stock: 0,
    imagenUrl: '',
    activo: true
  });

  const [editingItem, setEditingItem] = useState(null); // { type: 'product'|'category', data: {...} }
  const [confirmDelete, setConfirmDelete] = useState({ isOpen: false, type: null, id: null });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [cats, prods] = await Promise.all([
        apiClient.getCategorias(),
        apiClient.getProductos()
      ]);
      setCategories(cats);
      setProducts(prods);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    if (!newCat.nombre.trim()) return;
    try {
      await apiClient.createCategoria(newCat);
      setNewCat({ nombre: '', descripcion: '' });
      fetchData();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleUpdateCategory = async (e) => {
    e.preventDefault();
    try {
      await apiClient.updateCategoria(editingItem.data.id, editingItem.data);
      setEditingItem(null);
      fetchData();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleDeleteCategory = (id) => {
    setConfirmDelete({ isOpen: true, type: 'category', id });
  };

  const handleDeleteProduct = (id) => {
    setConfirmDelete({ isOpen: true, type: 'product', id });
  };

  const executeDelete = async () => {
    const { type, id } = confirmDelete;
    try {
      if (type === 'category') {
        await apiClient.deleteCategoria(id);
        if (editingItem?.type === 'category' && editingItem.data.id === id) {
          setEditingItem(null);
        }
      } else {
        await apiClient.deleteProducto(id);
        if (editingItem?.type === 'product' && editingItem.data.id === id) {
          setEditingItem(null);
        }
      }
      setConfirmDelete({ isOpen: false, type: null, id: null });
      fetchData();
    } catch (err) {
      alert(err.message);
      setConfirmDelete({ isOpen: false, type: null, id: null });
    }
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();
    if (!newProd.nombre || !newProd.precio || !newProd.categoriaId) return;
    try {
      await apiClient.createProducto({
        ...newProd,
        precio: parseFloat(newProd.precio),
        stock: parseInt(newProd.stock),
        categoriaId: parseInt(newProd.categoriaId)
      });
      setNewProd({
        nombre: '',
        precio: '',
        categoriaId: '',
        stock: 0,
        imagenUrl: '',
        activo: true
      });
      fetchData();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleUpdateProduct = async (e) => {
    e.preventDefault();
    try {
      await apiClient.updateProducto(editingItem.data.id, {
        ...editingItem.data,
        precio: parseFloat(editingItem.data.precio),
        stock: parseInt(editingItem.data.stock),
        categoriaId: parseInt(editingItem.data.categoriaId)
      });
      setEditingItem(null);
      fetchData();
    } catch (err) {
      alert(err.message);
    }
  };



  return (
    <div className="menu-container">
      <div className="menu-tabs">
        <button
          className={activeTab === 'products' ? 'tab-btn active' : 'tab-btn'}
          onClick={() => { setActiveTab('products'); setEditingItem(null); }}
        >
          <FastfoodIcon />
          Productos
        </button>
        <button
          className={activeTab === 'categories' ? 'tab-btn active' : 'tab-btn'}
          onClick={() => { setActiveTab('categories'); setEditingItem(null); }}
        >
          <CategoryIcon />
          Categorías
        </button>
      </div>

      <div className="tab-content">
        <div className={`menu-layout ${activeTab}-active`}>
          <section className={`premium-card form-section ${editingItem ? 'editing-mode' : ''}`}>
            <div className="section-header">
              {activeTab === 'categories' ? <CategoryIcon /> : <FastfoodIcon />}
              <h3>
                {editingItem
                  ? (activeTab === 'categories' ? 'Editar Categoría' : 'Editar Producto')
                  : (activeTab === 'categories' ? 'Nueva Categoría' : 'Nuevo Producto')}
              </h3>
            </div>

            <div className="form-container-inner" key={activeTab}>
              {activeTab === 'categories' ? (
                <form onSubmit={editingItem ? handleUpdateCategory : handleCreateCategory} className="full-form">
                  <div className="input-group">
                    <label>Nombre de la Categoría</label>
                    <input
                      type="text"
                      placeholder="Ej: Bebidas, Raciones..."
                      value={editingItem ? editingItem.data.nombre : newCat.nombre}
                      onChange={(e) => editingItem ?
                        setEditingItem({ ...editingItem, data: { ...editingItem.data, nombre: e.target.value } }) :
                        setNewCat({ ...newCat, nombre: e.target.value })}
                      required
                    />
                  </div>
                  <div className="input-group">
                    <label>Descripción (Opcional)</label>
                    <textarea
                      placeholder="Breve descripción de la categoría..."
                      value={editingItem ? (editingItem.data.descripcion || '') : newCat.descripcion}
                      onChange={(e) => editingItem ?
                        setEditingItem({ ...editingItem, data: { ...editingItem.data, descripcion: e.target.value } }) :
                        setNewCat({ ...newCat, descripcion: e.target.value })}
                      rows="3"
                    />
                  </div>
                  <div className="form-actions">
                    <button type="submit" className="add-btn-full">
                      {editingItem ? 'Guardar Cambios' : 'Crear Categoría'}
                    </button>
                    {editingItem && (
                      <button type="button" className="cancel-btn-full" onClick={() => setEditingItem(null)}>
                        Cancelar
                      </button>
                    )}
                  </div>
                </form>
              ) : (
                <form onSubmit={editingItem ? handleUpdateProduct : handleCreateProduct} className="full-form">
                  <div className="input-row">
                    <div className="input-group">
                      <label>Nombre del Producto</label>
                      <input
                        type="text"
                        placeholder="Ej: Caña, Hamburguesa..."
                        value={editingItem ? editingItem.data.nombre : newProd.nombre}
                        onChange={(e) => editingItem ?
                          setEditingItem({ ...editingItem, data: { ...editingItem.data, nombre: e.target.value } }) :
                          setNewProd({ ...newProd, nombre: e.target.value })}
                        required
                      />
                    </div>
                    <div className="input-group">
                      <label>Categoría</label>
                      <select
                        value={editingItem ? editingItem.data.categoriaId : newProd.categoriaId}
                        onChange={(e) => editingItem ?
                          setEditingItem({ ...editingItem, data: { ...editingItem.data, categoriaId: e.target.value } }) :
                          setNewProd({ ...newProd, categoriaId: e.target.value })}
                        required
                      >
                        <option value="">Seleccionar...</option>
                        {categories.map(cat => (
                          <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="input-row">
                    <div className="input-group">
                      <label>Precio (€)</label>
                      <input
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        value={editingItem ? editingItem.data.precio : newProd.precio}
                        onChange={(e) => editingItem ?
                          setEditingItem({ ...editingItem, data: { ...editingItem.data, precio: e.target.value } }) :
                          setNewProd({ ...newProd, precio: e.target.value })}
                        required
                      />
                    </div>
                    <div className="input-group">
                      <label>Stock</label>
                      <div className="stock-input">
                        <InventoryIcon />
                        <input
                          type="number"
                          value={editingItem ? editingItem.data.stock : newProd.stock}
                          onChange={(e) => editingItem ?
                            setEditingItem({ ...editingItem, data: { ...editingItem.data, stock: e.target.value } }) :
                            setNewProd({ ...newProd, stock: e.target.value })}
                        />
                      </div>
                    </div>
                  </div>

                  <div className="input-group">
                    <label>URL de Imagen</label>
                    <div className="image-input">
                      <ImageIcon />
                      <input
                        type="text"
                        placeholder="https://..."
                        value={editingItem ? (editingItem.data.imagenUrl || '') : newProd.imagenUrl}
                        onChange={(e) => editingItem ?
                          setEditingItem({ ...editingItem, data: { ...editingItem.data, imagenUrl: e.target.value } }) :
                          setNewProd({ ...newProd, imagenUrl: e.target.value })}
                      />
                    </div>
                  </div>

                  {(editingItem ? editingItem.data.imagenUrl : newProd.imagenUrl) && (
                    <div className="image-preview">
                      <img src={editingItem ? editingItem.data.imagenUrl : newProd.imagenUrl} alt="Preview" onError={(e) => e.target.style.display = 'none'} />
                    </div>
                  )}

                  <div className="input-group checkbox-group">
                    <label className="switch-label">
                      <span>Producto Activo</span>
                      <input
                        type="checkbox"
                        checked={editingItem ? editingItem.data.activo : newProd.activo}
                        onChange={(e) => editingItem ?
                          setEditingItem({ ...editingItem, data: { ...editingItem.data, activo: e.target.checked } }) :
                          setNewProd({ ...newProd, activo: e.target.checked })}
                      />
                      <span className="slider"></span>
                    </label>
                  </div>

                  <div className="form-actions">
                    <button type="submit" className="add-btn-full">
                      {editingItem ? 'Guardar Cambios' : 'Añadir al Menú'}
                    </button>
                    {editingItem && (
                      <button type="button" className="cancel-btn-full" onClick={() => setEditingItem(null)}>
                        Cancelar
                      </button>
                    )}
                  </div>
                </form>
              )}
            </div>
          </section>

          <section className="list-section" key={activeTab}>
            <div className="section-header">
              <h3>{activeTab === 'categories' ? 'Categorías Existentes' : 'Carta de Productos'}</h3>
              <span className="badge">{activeTab === 'categories' ? categories.length : products.length}</span>
            </div>

            {activeTab === 'categories' ? (
              <div className="categories-grid">
                {categories.map(cat => (
                  <div key={cat.id} className={`category-card premium-card ${editingItem?.type === 'category' && editingItem.data.id === cat.id ? 'editing' : ''}`}>
                    <div className="cat-info">
                      <h4>{cat.nombre}</h4>
                      <p>{cat.descripcion || 'Sin descripción'}</p>
                    </div>
                    <div className="card-actions">
                      <button onClick={() => setEditingItem({ type: 'category', data: cat })} className="edit-icon-btn">
                        <EditIcon />
                      </button>
                      <button onClick={() => handleDeleteCategory(cat.id)} className="delete-icon-btn">
                        <DeleteIcon />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="products-grid">
                {products.map(prod => (
                  <div key={prod.id} className={`product-card premium-card ${!prod.activo ? 'inactive' : ''} ${editingItem?.type === 'product' && editingItem.data.id === prod.id ? 'editing' : ''}`}>
                    <div className="prod-img">
                      {prod.imagenUrl ? (
                        <img src={prod.imagenUrl} alt={prod.nombre} />
                      ) : (
                        <FastfoodIcon />
                      )}
                      <div className="header-actions">
                        <button onClick={() => setEditingItem({ type: 'product', data: prod })} className="mini-action-btn">
                          <EditIcon fontSize="small" />
                        </button>
                        <button onClick={() => handleDeleteProduct(prod.id)} className="mini-action-btn delete">
                          <DeleteIcon fontSize="small" />
                        </button>
                      </div>
                    </div>
                    <div className="prod-details">
                      <div className="prod-header">
                        <h4>{prod.nombre}</h4>
                      </div>
                      <div className="price-row">
                        <span className="price">{prod.precio.toFixed(2)}€</span>
                        <p className="prod-cat-name">
                          {categories.find(c => c.id === prod.categoriaId)?.nombre}
                        </p>
                      </div>
                      <div className="prod-footer">
                        <span className={`stock-badge ${prod.stock < 10 ? 'low' : ''}`}>
                          Stock: {prod.stock}
                        </span>
                        {prod.activo ?
                          <CheckCircleIcon className="status-icon active" /> :
                          <CancelIcon className="status-icon inactive" />
                        }
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      </div>

      <ConfirmDialog
        isOpen={confirmDelete.isOpen}
        title={confirmDelete.type === 'category' ? 'Eliminar Categoría' : 'Eliminar Producto'}
        message={confirmDelete.type === 'category'
          ? '¿Estás seguro de eliminar esta categoría? Los productos asociados podrían dar error.'
          : '¿Estás seguro de eliminar este producto? Esta acción no se puede deshacer.'}
        onConfirm={executeDelete}
        onCancel={() => setConfirmDelete({ isOpen: false, type: null, id: null })}
      />
    </div>
  );
};

export default MenuPage;
