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

      <style jsx>{`
        .tables-container {
          animation: fadeIn 0.5s ease-out;
        }

        .action-bar-compact {
          display: flex;
          align-items: center;
          gap: 20px;
          background: var(--bg-surface-elevated);
          padding: 12px 20px;
          border-radius: 16px;
          border: 1px solid var(--border-color);
          width: fit-content;
          margin-bottom: 40px;
          box-shadow: 0 8px 30px rgba(0,0,0,0.2);
        }

        .info-badge {
          display: flex;
          align-items: center;
          gap: 8px;
          padding-right: 20px;
          border-right: 1px solid var(--border-color);
        }

        .info-badge .label {
          font-size: 0.8rem;
          color: var(--text-muted);
          text-transform: uppercase;
          font-weight: 700;
        }

        .info-badge .value {
          font-weight: 800;
          color: var(--primary);
        }

        .compact-form-group {
          display: flex;
          align-items: center;
          gap: 10px;
        }

        .compact-form-group label {
          font-size: 0.85rem;
          font-weight: 600;
          color: var(--text-secondary);
        }

        .mini-input {
          width: 60px;
          background: rgba(255,255,255,0.05);
          border: 1px solid var(--border-color);
          padding: 8px;
          border-radius: 8px;
          color: white;
          text-align: center;
          font-weight: 700;
          outline: none;
        }

        .add-btn-compact {
          background: var(--primary);
          color: black;
          padding: 8px 18px;
          border-radius: 10px;
          font-weight: 700;
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 0.9rem;
          transition: all 0.2s;
        }

        .add-btn-compact:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 15px var(--primary-glow);
        }

        .tables-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
          gap: 25px;
        }

        .table-card {
          display: flex;
          align-items: center;
          gap: 20px;
          position: relative;
          padding: 25px;
          transition: all 0.3s ease;
          border: 1px solid transparent;
        }

        .table-card:hover {
          transform: translateY(-5px);
          border-color: rgba(255,255,255,0.1);
        }

        .table-icon {
          width: 60px;
          height: 60px;
          background: var(--bg-surface-elevated);
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--primary);
          box-shadow: inset 0 0 10px rgba(0,0,0,0.2);
        }

        .table-info { flex: 1; }

        .table-header {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 4px;
        }

        .table-header h4 {
          font-size: 1.2rem;
          font-weight: 800;
        }

        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
        }

        .status-dot.libre { background: #4ade80; box-shadow: 0 0 10px #4ade80; }
        .status-dot.ocupada { background: #f87171; box-shadow: 0 0 10px #f87171; }
        .status-dot.reservada { background: #fbbf24; box-shadow: 0 0 10px #fbbf24; }

        .status-text {
          font-size: 0.75rem;
          font-weight: 900;
          letter-spacing: 0.5px;
          margin-bottom: 8px;
        }

        .status-text.libre { color: #4ade80; }
        .status-text.ocupada { color: #f87171; }
        .status-text.reservada { color: #fbbf24; }

        .capacity-info {
          font-size: 0.85rem;
          color: var(--text-muted);
          background: rgba(255,255,255,0.03);
          padding: 4px 10px;
          border-radius: 6px;
          width: fit-content;
        }

        .delete-btn {
          position: absolute;
          top: 10px;
          right: 10px;
          color: var(--text-muted);
          opacity: 0;
          background: none;
          border: none;
          cursor: pointer;
          padding: 5px;
          transition: all 0.2s;
        }

        .table-card:hover .delete-btn { opacity: 0.5; }
        .delete-btn:hover { opacity: 1 !important; color: #ff4b4b; }

        .loader {
          grid-column: 1 / -1;
          text-align: center;
          padding: 100px;
          color: var(--text-muted);
        }

        .error-banner {
          background: rgba(248, 113, 113, 0.1);
          color: #f87171;
          padding: 15px;
          border-radius: 12px;
          border: 1px solid rgba(248, 113, 113, 0.2);
          text-align: center;
        }
      `}</style>
    </div>
  );
};

export default TablesPage;
