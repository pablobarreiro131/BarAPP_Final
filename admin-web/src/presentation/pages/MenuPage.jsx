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

      <style jsx>{`
        .menu-container {
          animation: fadeIn 0.5s ease-out;
        }

        .menu-tabs {
          display: flex;
          gap: 10px;
          margin-bottom: 30px;
          background: rgba(255, 255, 255, 0.03);
          padding: 6px;
          border-radius: 14px;
          width: fit-content;
        }

        .tab-btn {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 20px;
          border-radius: 10px;
          font-weight: 600;
          color: var(--text-muted);
          transition: all 0.3s ease;
        }

        .tab-btn.active {
          background: var(--primary);
          color: black;
          box-shadow: 0 4px 12px var(--primary-glow);
        }

        .section-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 25px;
        }

        .section-header h3 {
          font-size: 1.3rem;
          color: var(--primary);
        }

        .badge {
          background: var(--primary-glow);
          color: var(--primary);
          padding: 2px 10px;
          border-radius: 20px;
          font-size: 0.8rem;
          font-weight: 800;
        }

        /* Layouts */
        .categories-layout, .products-layout {
          display: grid;
          grid-template-columns: 420px 1fr;
          gap: 30px;
          align-items: start;
        }

        @media (max-width: 1200px) {
          .categories-layout, .products-layout {
            grid-template-columns: 1fr;
          }
        }

        /* Forms */
        .full-form {
          display: flex;
          flex-direction: column;
          gap: 18px;
        }

        .input-group {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .input-group label {
          font-size: 0.85rem;
          color: var(--text-secondary);
          font-weight: 600;
        }

        .input-row {
          display: flex;
          flex-direction: column;
          gap: 18px;
        }

        input, select, textarea {
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid var(--border-color);
          padding: 12px 15px;
          border-radius: 10px;
          color: white;
          outline: none;
          font-size: 0.95rem;
          width: 100%;
          box-sizing: border-box;
        }

        textarea { resize: none; }

        .image-input, .stock-input {
          display: flex;
          align-items: center;
          gap: 10px;
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid var(--border-color);
          padding: 0 15px;
          border-radius: 10px;
        }

        .image-input input, .stock-input input {
          border: none;
          padding: 12px 0;
          background: none;
        }

        .image-preview {
          width: 100%;
          height: 150px;
          border-radius: 10px;
          overflow: hidden;
          background: black;
        }

        .image-preview img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .add-btn-full {
          background: var(--primary);
          color: black;
          padding: 14px;
          border-radius: 12px;
          font-weight: 800;
          margin-top: 10px;
        }

        /* Grids */
        .categories-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
          gap: 15px;
        }

        .category-card {
          padding: 20px;
          border-left: 4px solid var(--primary);
        }

        .category-card h4 { margin-bottom: 8px; }
        .category-card p {
          font-size: 0.85rem;
          color: var(--text-muted);
          line-height: 1.4;
        }

        .products-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
          gap: 20px;
        }

        .product-card {
          display: flex;
          flex-direction: column;
          overflow: hidden;
          padding: 0;
          transition: transform 0.3s ease;
        }

        .product-card:hover {
          transform: translateY(-5px);
        }

        .product-card.inactive {
          opacity: 0.6;
          filter: grayscale(0.5);
        }

        .prod-img {
          height: 160px;
          background: var(--bg-surface-elevated);
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--text-muted);
        }

        .prod-img img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .prod-details {
          padding: 20px;
        }

        .prod-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 5px;
        }

        .prod-header h4 { font-size: 1.1rem; }

        .price {
          color: var(--primary);
          font-weight: 800;
          font-size: 1.1rem;
        }

        .prod-cat-name {
          font-size: 0.8rem;
          color: var(--text-muted);
          margin-bottom: 15px;
        }

        .prod-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 15px;
          border-top: 1px solid var(--border-color);
        }

        .stock-badge {
          font-size: 0.75rem;
          font-weight: 700;
          padding: 4px 10px;
          background: rgba(255,255,255,0.05);
          border-radius: 6px;
        }

        .stock-badge.low {
          color: #f87171;
          background: rgba(248, 113, 113, 0.1);
        }

        .status-icon {
          font-size: 1.2rem;
        }

        .status-icon.active { color: #4ade80; }
        .status-icon.inactive { color: #f87171; }

        /* Switch Toggle */
        .switch-label {
          display: flex;
          align-items: center;
          justify-content: space-between;
          cursor: pointer;
          padding: 5px 0;
        }

        .switch-label input { display: none; }

        .slider {
          width: 40px;
          height: 20px;
          background: #444;
          border-radius: 20px;
          position: relative;
          transition: 0.3s;
        }

        .slider:before {
          content: "";
          position: absolute;
          width: 14px;
          height: 14px;
          background: white;
          border-radius: 50%;
          top: 3px;
          left: 3px;
          transition: 0.3s;
        }

        input:checked + .slider { background: var(--primary); }
        input:checked + .slider:before { transform: translateX(20px); }
      `}</style>
    </div>
  );
};

export default MenuPage;
