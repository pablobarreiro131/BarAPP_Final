import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import AddIcon from '@mui/icons-material/Add';
import TableBarIcon from '@mui/icons-material/TableBar';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';

import '../styles/TablesPage.css';

const TablesPage = () => {
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newTableCapacity, setNewTableCapacity] = useState('4');
  
  const [editingMesa, setEditingMesa] = useState(null);

  const fetchTables = async () => {
    try {
      setLoading(true);
      const data = await apiClient.getMesas();
      setTables(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar las mesas: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTables();
  }, []);

  const handleCreateOrUpdateMesa = async (e) => {
    e.preventDefault();
    
    try {
      if (editingMesa) {
        await apiClient.fetchWithAuth(`/mesas/${editingMesa.id}`, {
          method: 'PUT',
          body: JSON.stringify({
            ...editingMesa,
            capacidad: parseInt(newTableCapacity) || 4
          })
        });
        setEditingMesa(null);
      } else {
        const nextNumber = tables.length > 0 
          ? Math.max(...tables.map(t => t.numeroMesa)) + 1 
          : 1;

        await apiClient.createMesa({ 
          numeroMesa: nextNumber,
          capacidad: parseInt(newTableCapacity) || 4,
          estado: 'libre'
        });
      }
      setNewTableCapacity('4');
      fetchTables();
    } catch (err) {
      alert('Error: ' + err.message);
    }
  };

  const handleDeleteMesa = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar esta mesa?')) return;
    try {
      await apiClient.deleteMesa(id);
      fetchTables();
    } catch (err) {
      alert('Error: ' + err.message);
    }
  };

  const handleEdit = (mesa) => {
    setEditingMesa(mesa);
    setNewTableCapacity(mesa.capacidad.toString());
  };

  const nextTableNum = tables.length > 0 
    ? Math.max(...tables.map(t => t.numeroMesa)) + 1 
    : 1;

  return (
    <div className="tables-container">
      <div className="action-bar-compact">
        <div className="info-badge">
          <span className="label">{editingMesa ? 'Editando:' : 'Siguiente:'}</span>
          <span className="value">Mesa {editingMesa ? editingMesa.numeroMesa : nextTableNum}</span>
        </div>
        <div className="compact-form-group">
          <label>Capacidad:</label>
          <input 
            type="number" 
            value={newTableCapacity}
            onChange={(e) => setNewTableCapacity(e.target.value)}
            min="1"
            className="mini-input"
          />
        </div>
        <div className="compact-actions">
          <button onClick={handleCreateOrUpdateMesa} className="add-btn-compact">
            {editingMesa ? <EditIcon /> : <AddIcon />}
            <span>{editingMesa ? 'Guardar' : 'Añadir Mesa'}</span>
          </button>
          {editingMesa && (
            <button onClick={() => { setEditingMesa(null); setNewTableCapacity('4'); }} className="cancel-btn-compact">
              Cancelar
            </button>
          )}
        </div>
      </div>

      {loading ? (
        <div className="loader">Cargando mesas...</div>
      ) : error ? (
        <div className="error-banner">{error}</div>
      ) : (
        <div className="tables-grid">
          {tables.sort((a,b) => a.numeroMesa - b.numeroMesa).map((mesa) => (
            <div key={mesa.id} className={`table-card premium-card status-${mesa.estado} ${editingMesa?.id === mesa.id ? 'editing' : ''}`}>
              <div className="table-icon">
                <TableBarIcon />
              </div>
              <div className="table-info">
                <div className="table-header">
                  <h4>Mesa {mesa.numeroMesa}</h4>
                  <div className={`status-dot ${mesa.estado}`}></div>
                </div>
                <p className={`status-text ${mesa.estado}`}>
                  {mesa.estado.toUpperCase()}
                </p>
                <div className="capacity-info">
                  <span>{mesa.capacidad} PERSONAS</span>
                </div>
              </div>
              <div className="table-actions-btns">
                <button 
                  onClick={() => handleEdit(mesa)}
                  className="edit-btn"
                  title="Editar mesa"
                >
                  <EditIcon fontSize="small" />
                </button>
                <button 
                  onClick={() => handleDeleteMesa(mesa.id)}
                  className="delete-btn"
                  title="Eliminar mesa"
                >
                  <DeleteOutlinedIcon fontSize="small" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TablesPage;
