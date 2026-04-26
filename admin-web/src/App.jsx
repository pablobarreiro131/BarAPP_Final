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
