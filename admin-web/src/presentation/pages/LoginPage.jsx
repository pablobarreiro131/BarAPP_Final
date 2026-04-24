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
      Object.keys(localStorage).forEach(key => {
        if (key.startsWith('sb-')) localStorage.removeItem(key);
      });

      const supabaseUrl = import.meta.env.VITE_SUPABASE_URL;
      const supabaseKey = import.meta.env.VITE_SUPABASE_ANON_KEY;
      
      const response = await fetch(`${supabaseUrl}/auth/v1/token?grant_type=password`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'apikey': supabaseKey,
        },
        body: JSON.stringify({ email: email.trim(), password }),
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.msg || 'Correo o contraseña incorrectos');
      }

      const authData = await response.json();
      
      localStorage.setItem('supabase_token', authData.access_token);

      const profile = await apiClient.getMe();

      if (profile && profile.rol === 'admin') {
        localStorage.setItem('isAdmin', 'true');
        onLoginSuccess();
      } else {
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
