import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { supabase } from './data/supabaseClient';
import { apiClient } from './data/apiClient';
import LoginPage from './presentation/pages/LoginPage';
import DashboardLayout from './presentation/components/DashboardLayout';
import TablesPage from './presentation/pages/TablesPage';
import MenuPage from './presentation/pages/MenuPage';
import StaffPage from './presentation/pages/StaffPage';

const Dashboard = () => (
  <div className="premium-card">
    <h3 style={{ fontSize: '1.5rem', marginBottom: '15px', color: 'var(--primary)' }}>Estado de la Aplicación</h3>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
      <div className="premium-card" style={{ background: 'var(--bg-surface-elevated)' }}>
        <h4 style={{ color: 'var(--text-secondary)' }}>Ventas de Hoy</h4>
        <p style={{ fontSize: '2rem', fontWeight: '800', marginTop: '10px' }}>€ 0.00</p>
      </div>
      <div className="premium-card" style={{ background: 'var(--bg-surface-elevated)' }}>
        <h4 style={{ color: 'var(--text-secondary)' }}>Mesas Ocupadas</h4>
        <p style={{ fontSize: '2rem', fontWeight: '800', marginTop: '10px' }}>0</p>
      </div>
    </div>
  </div>
);

const App = () => {
  const [isAdmin, setIsAdmin] = useState(localStorage.getItem('isAdmin') === 'true');

  useEffect(() => {
    const { data: { subscription } } = supabase.auth.onAuthStateChange(async (_event, session) => {
      if (session) {
        try {
          const profile = await apiClient.getMe();
          const adminStatus = profile && profile.rol === 'admin';
          setIsAdmin(adminStatus);
          localStorage.setItem('isAdmin', adminStatus ? 'true' : 'false');
        } catch (err) {
          console.error('Error al verificar rol:', err);
          setIsAdmin(false);
          localStorage.removeItem('isAdmin');
        }
      } else {
        setIsAdmin(false);
        localStorage.removeItem('isAdmin');
      }
    });

    return () => subscription.unsubscribe();
  }, []);

  const authenticated = isAdmin;

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            authenticated ? <Navigate to="/" /> : <LoginPage onLoginSuccess={() => setIsAdmin(true)} />
          }
        />

        <Route
          path="/"
          element={
            authenticated ? (
              <DashboardLayout>
                <Dashboard />
              </DashboardLayout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/tables"
          element={
            authenticated ? (
              <DashboardLayout>
                <TablesPage />
              </DashboardLayout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/menu"
          element={
            authenticated ? (
              <DashboardLayout>
                <MenuPage />
              </DashboardLayout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/staff"
          element={
            authenticated ? (
              <DashboardLayout>
                <StaffPage />
              </DashboardLayout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
