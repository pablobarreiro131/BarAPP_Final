import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { supabase } from './data/supabaseClient';
import { apiClient } from './data/apiClient';
import LoginPage from './presentation/pages/LoginPage';
import DashboardLayout from './presentation/components/DashboardLayout';
import DashboardPage from './presentation/pages/DashboardPage';
import TablesPage from './presentation/pages/TablesPage';
import MenuPage from './presentation/pages/MenuPage';
import StaffPage from './presentation/pages/StaffPage';

const App = () => {
  const [isAdmin, setIsAdmin] = useState(false);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    supabase.auth.getSession().then(async ({ data: { session } }) => {
      if (session) {
        try {
          const profile = await apiClient.getMe();
          setIsAdmin(profile?.rol === 'admin');
        } catch {
          setIsAdmin(false);
        }
      } else {
        setIsAdmin(false);
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('supabase_token');
      }
      setReady(true);
    });

    const { data: { subscription } } = supabase.auth.onAuthStateChange(async (event, session) => {
      if (event === 'SIGNED_OUT') {
        setIsAdmin(false);
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('supabase_token');
      } else if (event === 'SIGNED_IN' && session) {
        try {
          const profile = await apiClient.getMe();
          const adminStatus = profile?.rol === 'admin';
          setIsAdmin(adminStatus);
          localStorage.setItem('isAdmin', adminStatus ? 'true' : 'false');
        } catch {
          setIsAdmin(false);
        }
      }
    });

    return () => subscription.unsubscribe();
  }, []);

  if (!ready) {
    return (
      <div style={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#0c0c0c', color: '#6b6560' }}>
        <p style={{ letterSpacing: '5px', textTransform: 'uppercase', fontSize: '0.7rem', fontFamily: 'Inter, sans-serif' }}>Cargando...</p>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            isAdmin ? <Navigate to="/" /> : <LoginPage onLoginSuccess={() => setIsAdmin(true)} />
          }
        />

        <Route
          path="/"
          element={
            isAdmin ? (
              <DashboardLayout>
                <DashboardPage />
              </DashboardLayout>
            ) : (
              <Navigate to="/login" />
            )
          }
        />

        <Route
          path="/tables"
          element={
            isAdmin ? (
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
            isAdmin ? (
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
            isAdmin ? (
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
