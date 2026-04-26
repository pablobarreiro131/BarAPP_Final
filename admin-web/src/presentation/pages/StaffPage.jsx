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
    </div>
  );
};

export default StaffPage;
