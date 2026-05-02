import React, { useState, useEffect } from 'react';
import { apiClient } from '../../data/apiClient';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import BadgeIcon from '@mui/icons-material/Badge';
import EmailIcon from '@mui/icons-material/Email';
import ShieldIcon from '@mui/icons-material/Shield';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

import '../styles/StaffPage.css';

const StaffPage = () => {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [showModal, setShowModal] = useState(false);
  const [editingMember, setEditingMember] = useState(null);
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
      if (editingMember) {
        await apiClient.updatePerfil(editingMember.id, {
          nombre: formData.nombre,
          rol: formData.rol
        });
        alert('¡Usuario actualizado!');
      } else {
        await apiClient.fetchWithAuth('/perfiles/auth', {
          method: 'POST',
          body: JSON.stringify(formData)
        });
        alert('¡Usuario creado correctamente!');
      }
      handleCloseModal();
      fetchStaff();
    } catch (err) {
      alert('Error: ' + err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este usuario? Se borrará también de la autenticación.')) return;
    try {
      await apiClient.deletePerfil(id);
      fetchStaff();
    } catch (err) {
      alert('Error: ' + err.message);
    }
  };

  const handleEdit = (member) => {
    setEditingMember(member);
    setFormData({
      nombre: member.nombre,
      rol: member.rol,
      email: member.email,
      password: ''
    });
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingMember(null);
    setFormData({ email: '', password: '', nombre: '', rol: 'camarero' });
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
            <div className="card-actions-mini">
              <button onClick={() => handleEdit(member)} className="staff-action-btn">
                <EditIcon fontSize="small" />
              </button>
              <button onClick={() => handleDelete(member.id)} className="staff-action-btn delete">
                <DeleteIcon fontSize="small" />
              </button>
            </div>
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
              <p className="staff-email">
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
            <h2>{editingMember ? 'Editar Miembro' : 'Nuevo Miembro del Equipo'}</h2>
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
              {!editingMember && (
                <>
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
                </>
              )}
              {editingMember && (
                 <div className="input-field disabled">
                    <EmailIcon />
                    <input value={formData.email} disabled />
                 </div>
              )}
              <div className="input-field">
                <ShieldIcon />
                <select 
                  value={formData.rol}
                  onChange={e => setFormData({...formData, rol: e.target.value})}
                >
                  <option value="camarero">Camarero</option>
                  <option value="admin">Administrador</option>
                </select>
              </div>

              <div className="form-actions">
                <button type="button" onClick={handleCloseModal} className="cancel-btn">Cancelar</button>
                <button type="submit" className="save-btn">{editingMember ? 'Guardar Cambios' : 'Crear Cuenta'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default StaffPage;
