import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import DashboardIcon from '@mui/icons-material/Dashboard';
import TableBarIcon from '@mui/icons-material/TableBar';
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import PeopleIcon from '@mui/icons-material/People';
import LogoutIcon from '@mui/icons-material/Logout';

import '../styles/DashboardLayout.css';

const DashboardLayout = ({ children }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('isAdmin');
    localStorage.removeItem('supabase_token');
    Object.keys(localStorage).forEach(key => {
      if (key.startsWith('sb-')) localStorage.removeItem(key);
    });
    window.location.href = '/login';
  };

  const navItems = [
    { path: '/', icon: <DashboardIcon />, label: 'Dashboard' },
    { path: '/tables', icon: <TableBarIcon />, label: 'Mesas' },
    { path: '/menu', icon: <RestaurantMenuIcon />, label: 'Menú' },
    { path: '/staff', icon: <PeopleIcon />, label: 'Personal' },
  ];

  return (
    <div className="layout-container">
      <aside className="sidebar glass">
        <div className="sidebar-header">
          <span className="logo-text">Bar<span className="logo-accent">APP</span></span>
          <p className="admin-badge">ADMIN</p>
        </div>

        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-label">{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <button onClick={handleLogout} className="logout-button">
            <LogoutIcon />
            <span>Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      <main className="content-area">
        <header className="content-header">
          <h1>Panel de Administración</h1>
        </header>
        <div className="page-content">
          {children}
        </div>
      </main>
    </div>
  );
};

export default DashboardLayout;
