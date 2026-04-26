import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import AddIcon from '@mui/icons-material/Add';
import TableBarIcon from '@mui/icons-material/TableBar';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';

import '../styles/TablesPage.css';

const TablesPage = () => {
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newTableNumber, setNewTableNumber] = useState('');
  const [newTableCapacity, setNewTableCapacity] = useState('4');

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

  const handleCreateMesa = async (e) => {
    e.preventDefault();
    
    const nextNumber = tables.length > 0 
      ? Math.max(...tables.map(t => t.numeroMesa)) + 1 
      : 1;

    try {
      await apiClient.createMesa({ 
        numeroMesa: nextNumber,
        capacidad: parseInt(newTableCapacity) || 4,
        estado: 'libre'
      });
      setNewTableNumber('');
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

  const nextTableNum = tables.length > 0 
    ? Math.max(...tables.map(t => t.numeroMesa)) + 1 
    : 1;

  return (
    <div className="tables-container">
      <div className="action-bar-compact">
        <div className="info-badge">
          <span className="label">Siguiente:</span>
          <span className="value">Mesa {nextTableNum}</span>
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
        <button onClick={handleCreateMesa} className="add-btn-compact">
          <AddIcon />
          <span>Añadir Mesa</span>
        </button>
      </div>

      {loading ? (
        <div className="loader">Cargando mesas...</div>
      ) : error ? (
        <div className="error-banner">{error}</div>
      ) : (
        <div className="tables-grid">
          {tables.map((mesa) => (
            <div key={mesa.id} className={`table-card premium-card status-${mesa.estado}`}>
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
              <button 
                onClick={() => handleDeleteMesa(mesa.id)}
                className="delete-btn"
                title="Eliminar mesa"
              >
                <DeleteOutlinedIcon />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TablesPage;
