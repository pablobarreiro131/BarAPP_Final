import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import BadgeIcon from '@mui/icons-material/Badge';
import EmailIcon from '@mui/icons-material/Email';
import ShieldIcon from '@mui/icons-material/Shield';

import '../styles/StaffPage.css';

const StaffPage = () => {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    nombre: '',
    rol: 'camarero'
  });

  const fetchStaff = async () => {
    try {
      setLoading(true);
      const data = await apiClient.getPerfiles();
      setStaff(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStaff();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiClient.fetchWithAuth('/perfiles/auth', {
        method: 'POST',
        body: JSON.stringify(formData)
      });
      setShowModal(false);
      setFormData({ email: '', password: '', nombre: '', rol: 'camarero' });
      fetchStaff();
      alert('¡Usuario creado correctamente!');
    } catch (err) {
      alert('Error: ' + err.message);
    }
  };

  return (
    <div className="staff-container">
      <div className="action-bar">
        <button onClick={() => setShowModal(true)} className="add-btn-premium">
          <PersonAddIcon />
          <span>Registrar Nuevo Personal</span>
        </button>
      </div>

      <div className="staff-grid">
        {staff.map(member => (
          <div key={member.id} className="staff-card premium-card">
            <div className="card-header">
              <div className="avatar">
                {member.nombre.charAt(0).toUpperCase()}
              </div>
              <div className="role-badge" data-role={member.rol}>
                {member.rol}
              </div>
            </div>
            <div className="card-body">
              <h3>{member.nombre}</h3>
              <p className="staff-email" style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '5px' }}>
                {member.email}
              </p>
              <p className="staff-id">ID: {member.id.substring(0, 8)}...</p>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-card glass">
            <h2>Nuevo Miembro del Equipo</h2>
            <form onSubmit={handleSubmit} className="staff-form">
              <div className="input-field">
                <BadgeIcon />
                <input 
                  placeholder="Nombre Completo"
                  value={formData.nombre}
                  onChange={e => setFormData({...formData, nombre: e.target.value})}
                  required
                />
              </div>
              <div className="input-field">
                <EmailIcon />
                <input 
                  type="email"
                  placeholder="Correo Electrónico"
                  value={formData.email}
                  onChange={e => setFormData({...formData, email: e.target.value})}
                  required
                />
              </div>
              <div className="input-field">
                <ShieldIcon />
                <input 
                  type="password"
                  placeholder="Contraseña Inicial"
                  value={formData.password}
                  onChange={e => setFormData({...formData, password: e.target.value})}
                  required
                />
              </div>
              <div className="input-field">
                <select 
                  value={formData.rol}
                  onChange={e => setFormData({...formData, rol: e.target.value})}
                >
                  <option value="camarero">Camarero</option>
                  <option value="admin">Administrador</option>
                </select>
              </div>

              <div className="form-actions">
                <button type="button" onClick={() => setShowModal(false)} className="cancel-btn">Cancelar</button>
                <button type="submit" className="save-btn">Crear Cuenta</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style jsx>{`
        .staff-container {
          animation: fadeIn 0.5s ease-out;
        }

        .action-bar {
          margin-bottom: 30px;
        }

        .add-btn-premium {
          display: flex;
          align-items: center;
          gap: 12px;
          background: var(--primary);
          color: black;
          padding: 14px 24px;
          border-radius: 12px;
          font-weight: 700;
          box-shadow: 0 4px 15px var(--primary-glow);
        }

        .staff-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
          gap: 25px;
        }

        .staff-card {
          text-align: center;
          padding: 30px;
        }

        .card-header {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 15px;
          margin-bottom: 20px;
        }

        .avatar {
          width: 70px;
          height: 70px;
          background: var(--bg-surface-elevated);
          border: 2px solid var(--primary);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.8rem;
          font-weight: 800;
          color: var(--primary);
        }

        .role-badge {
          font-size: 0.75rem;
          font-weight: 800;
          text-transform: uppercase;
          padding: 4px 12px;
          border-radius: 20px;
          background: rgba(255, 255, 255, 0.05);
        }

        .role-badge[data-role='admin'] {
          color: var(--primary);
          background: var(--primary-glow);
        }

        .staff-id {
          font-size: 0.8rem;
          color: var(--text-muted);
          margin-top: 5px;
        }

        /* Modal Styles */
        .modal-overlay {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0,0,0,0.8);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
          padding: 20px;
        }

        .modal-card {
          width: 100%;
          max-width: 450px;
          padding: 40px;
          border-radius: 24px;
        }

        h2 { margin-bottom: 30px; text-align: center; }

        .staff-form {
          display: flex;
          flex-direction: column;
          gap: 15px;
        }

        .input-field {
          display: flex;
          align-items: center;
          gap: 12px;
          background: rgba(255,255,255,0.05);
          border: 1px solid var(--border-color);
          padding: 12px 18px;
          border-radius: 12px;
        }

        .input-field svg { color: var(--text-muted); }

        input, select {
          flex: 1;
          background: none;
          border: none;
          color: white;
          outline: none;
          font-size: 1rem;
        }

        select option { background: var(--bg-surface); }

        .form-actions {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 15px;
          margin-top: 20px;
        }

        .cancel-btn {
          padding: 12px;
          border: 1px solid var(--border-color);
          border-radius: 12px;
          color: var(--text-secondary);
        }

        .save-btn {
          padding: 12px;
          background: var(--primary);
          color: black;
          font-weight: 700;
          border-radius: 12px;
        }
      `}</style>
    </div>
  );
};

export default StaffPage;
