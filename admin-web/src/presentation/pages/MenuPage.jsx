import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import AddIcon from '@mui/icons-material/Add';
import CategoryIcon from '@mui/icons-material/Category';
import FastfoodIcon from '@mui/icons-material/Fastfood';
import InventoryIcon from '@mui/icons-material/Inventory';
import ImageIcon from '@mui/icons-material/Image';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

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

  return (
    <div className="menu-container">
      <div className="menu-tabs">
        <button 
          className={activeTab === 'products' ? 'tab-btn active' : 'tab-btn'}
          onClick={() => setActiveTab('products')}
        >
          <FastfoodIcon />
          Productos
        </button>
        <button 
          className={activeTab === 'categories' ? 'tab-btn active' : 'tab-btn'}
          onClick={() => setActiveTab('categories')}
        >
          <CategoryIcon />
          Categorías
        </button>
      </div>

      <div className="tab-content">
        {activeTab === 'categories' ? (
          <div className="categories-layout">
            <section className="premium-card form-section">
              <div className="section-header">
                <CategoryIcon />
                <h3>Nueva Categoría</h3>
              </div>
              <form onSubmit={handleCreateCategory} className="full-form">
                <div className="input-group">
                  <label>Nombre de la Categoría</label>
                  <input 
                    type="text" 
                    placeholder="Ej: Bebidas, Raciones..."
                    value={newCat.nombre}
                    onChange={(e) => setNewCat({...newCat, nombre: e.target.value})}
                    required
                  />
                </div>
                <div className="input-group">
                  <label>Descripción (Opcional)</label>
                  <textarea 
                    placeholder="Breve descripción de la categoría..."
                    value={newCat.descripcion}
                    onChange={(e) => setNewCat({...newCat, descripcion: e.target.value})}
                    rows="3"
                  />
                </div>
                <button type="submit" className="add-btn-full">Crear Categoría</button>
              </form>
            </section>

            <section className="list-section">
              <div className="section-header">
                <h3>Categorías Existentes</h3>
                <span className="badge">{categories.length}</span>
              </div>
              <div className="categories-grid">
                {categories.map(cat => (
                  <div key={cat.id} className="category-card premium-card">
                    <div className="cat-info">
                      <h4>{cat.nombre}</h4>
                      <p>{cat.descripcion || 'Sin descripción'}</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </div>
        ) : (
          <div className="products-layout">
            <section className="premium-card form-section">
              <div className="section-header">
                <FastfoodIcon />
                <h3>Nuevo Producto</h3>
              </div>
              <form onSubmit={handleCreateProduct} className="full-form">
                <div className="input-row">
                  <div className="input-group">
                    <label>Nombre del Producto</label>
                    <input 
                      type="text" 
                      placeholder="Ej: Caña, Hamburguesa..."
                      value={newProd.nombre}
                      onChange={(e) => setNewProd({...newProd, nombre: e.target.value})}
                      required
                    />
                  </div>
                  <div className="input-group">
                    <label>Categoría</label>
                    <select 
                      value={newProd.categoriaId}
                      onChange={(e) => setNewProd({...newProd, categoriaId: e.target.value})}
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
                      value={newProd.precio}
                      onChange={(e) => setNewProd({...newProd, precio: e.target.value})}
                      required
                    />
                  </div>
                  <div className="input-group">
                    <label>Stock Inicial</label>
                    <div className="stock-input">
                      <InventoryIcon />
                      <input 
                        type="number" 
                        value={newProd.stock}
                        onChange={(e) => setNewProd({...newProd, stock: e.target.value})}
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
                      value={newProd.imagenUrl}
                      onChange={(e) => setNewProd({...newProd, imagenUrl: e.target.value})}
                    />
                  </div>
                </div>

                {newProd.imagenUrl && (
                  <div className="image-preview">
                    <img src={newProd.imagenUrl} alt="Preview" onError={(e) => e.target.style.display='none'} />
                  </div>
                )}

                <div className="input-group checkbox-group">
                  <label className="switch-label">
                    <span>Producto Activo</span>
                    <input 
                      type="checkbox" 
                      checked={newProd.activo}
                      onChange={(e) => setNewProd({...newProd, activo: e.target.checked})}
                    />
                    <span className="slider"></span>
                  </label>
                </div>

                <button type="submit" className="add-btn-full">Añadir al Menú</button>
              </form>
            </section>

            <section className="list-section">
              <div className="section-header">
                <h3>Carta de Productos</h3>
                <span className="badge">{products.length}</span>
              </div>
              <div className="products-grid">
                {products.map(prod => (
                  <div key={prod.id} className={`product-card premium-card ${!prod.activo ? 'inactive' : ''}`}>
                    <div className="prod-img">
                      {prod.imagenUrl ? (
                        <img src={prod.imagenUrl} alt={prod.nombre} />
                      ) : (
                        <FastfoodIcon />
                      )}
                    </div>
                    <div className="prod-details">
                      <div className="prod-header">
                        <h4>{prod.nombre}</h4>
                        <span className="price">{prod.precio.toFixed(2)}€</span>
                      </div>
                      <p className="prod-cat-name">
                        {categories.find(c => c.id === prod.categoriaId)?.nombre}
                      </p>
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
            </section>
          </div>
        )}
      </div>
    </div>
  );
};

export default MenuPage;
