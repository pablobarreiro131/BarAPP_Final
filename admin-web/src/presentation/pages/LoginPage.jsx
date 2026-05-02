import React, { useState } from 'react';
import { supabase } from '../../data/supabaseClient';
import { apiClient } from '../../data/apiClient';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import PersonOutlinedIcon from '@mui/icons-material/PersonOutlined';

import '../styles/LoginPage.css';

const LoginPage = ({ onLoginSuccess }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const { data, error } = await supabase.auth.signInWithPassword({
        email: email.trim(),
        password: password
      });
  
      if (error) throw error;
  
      const session = data.session;
      if (!session) throw new Error('No se pudo iniciar sesión');

      localStorage.setItem('supabase_token', session.access_token);
  
      const profile = await apiClient.getMe();
  
      if (profile && profile.rol === 'admin') {
        localStorage.setItem('isAdmin', 'true');
        onLoginSuccess();
      } else {
        await supabase.auth.signOut();
        localStorage.removeItem('supabase_token');
        throw new Error('No tienes permisos de administrador para acceder aquí.');
      }

    } catch (err) {
      console.error('Login error:', err);
      setError(err.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card glass">
        <div className="login-header">
          <div className="app-logo">
            <span className="logo-text">Bar<span className="logo-accent">APP</span></span>
          </div>
          <h2>Admin Panel</h2>
          <p>Introduce tus credenciales para continuar</p>
        </div>

        <form onSubmit={handleLogin} className="login-form">
          {error && <div className="login-error-msg">{error}</div>}

          <div className="input-group">
            <PersonOutlinedIcon className="input-icon" />
            <input
              type="email"
              placeholder="Correo electrónico"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <div className="input-group">
            <LockOutlinedIcon className="input-icon" />
            <input
              type="password"
              placeholder="Contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Iniciando sesión...' : 'Entrar'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
