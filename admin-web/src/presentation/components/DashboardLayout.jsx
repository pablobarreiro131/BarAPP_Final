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

      <style jsx>{`
        .layout-container {
          display: flex;
          min-height: 100vh;
          background-color: var(--bg-dark);
        }

        .sidebar {
          width: 280px;
          height: 100vh;
          position: fixed;
          left: 0;
          top: 0;
          display: flex;
          flex-direction: column;
          padding: 30px 20px;
          border-right: 1px solid var(--glass-border);
          z-index: 100;
        }

        .sidebar-header {
          padding: 0 10px 40px;
          text-align: center;
        }

        .logo-text {
          font-size: 1.8rem;
          font-weight: 800;
          letter-spacing: -1px;
        }

        .logo-accent {
          color: var(--primary);
        }

        .admin-badge {
          display: inline-block;
          background: var(--primary-glow);
          color: var(--primary);
          padding: 2px 10px;
          border-radius: 20px;
          font-size: 0.7rem;
          font-weight: 700;
          margin-top: 5px;
        }

        .sidebar-nav {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .nav-link {
          display: flex;
          align-items: center;
          gap: 15px;
          padding: 14px 18px;
          border-radius: 12px;
          color: var(--text-secondary);
          transition: var(--transition-fast);
        }

        .nav-link:hover {
          background: rgba(255, 255, 255, 0.05);
          color: white;
        }

        .nav-link.active {
          background: var(--primary);
          color: black;
          box-shadow: 0 4px 12px var(--primary-glow);
        }

        .nav-icon {
          display: flex;
          font-size: 22px;
        }

        .nav-label {
          font-weight: 600;
        }

        .sidebar-footer {
          margin-top: auto;
          padding-top: 20px;
          border-top: 1px solid var(--border-color);
        }

        .logout-button {
          width: 100%;
          display: flex;
          align-items: center;
          gap: 15px;
          padding: 14px 18px;
          color: #ff4b4b;
          border-radius: 12px;
        }

        .logout-button:hover {
          background: rgba(255, 75, 75, 0.1);
        }

        .content-area {
          flex: 1;
          margin-left: 280px;
          padding: 40px;
        }

        .content-header {
          margin-bottom: 30px;
        }

        h1 {
          font-size: 2rem;
          font-weight: 700;
        }

        .page-content {
          animation: fadeIn 0.4s ease-out;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
};

export default DashboardLayout;
