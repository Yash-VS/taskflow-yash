import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../hooks/useTheme';
import { LogOut, LayoutDashboard, Sun, Moon } from 'lucide-react';
import './Navbar.css';

export const Navbar: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <div className="container navbar-container">
        <Link to="/projects" className="navbar-brand">
          <LayoutDashboard size={24} color="#E23744" />
          <span>TaskFlow</span>
        </Link>
        
        <div className="navbar-actions">
          <button className="theme-toggle" onClick={toggleTheme} aria-label="Toggle dark mode" title="Toggle dark mode">
            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
          </button>
          
          {isAuthenticated && user && (
            <>
              <span className="navbar-user">Hi, {user.name}</span>
              <button className="btn-secondary logout-btn" onClick={handleLogout}>
                <LogOut size={16} /> Logout
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};
