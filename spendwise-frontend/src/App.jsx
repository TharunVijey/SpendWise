
import { NavLink, Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import PlaceholderPage from './pages/PlaceholderPage'
import './App.css'
import Income from './pages/Income'
import Expenses from './pages/Expenses'
import Savings from './pages/Savings'
import Reports from './pages/Reports'

function App() {
  return (
    <div className="app-layout">

      <aside className="sidebar">

        <div className="brand">
          <div className="brand-icon">S</div>
          <span>SpendWise</span>
        </div>

        <div className="nav-label">MENU</div>

        <nav className="navigation">

          <NavLink to="/dashboard" className={({ isActive }) =>
            `nav-item ${isActive ? 'active' : ''}`
          }>
            <span>▦</span> Dashboard
          </NavLink>

          <NavLink to="/income" className={({ isActive }) =>
            `nav-item ${isActive ? 'active' : ''}`
          }>
            <span>↗</span> Income
          </NavLink>

          <NavLink to="/expenses" className={({ isActive }) =>
            `nav-item ${isActive ? 'active' : ''}`
          }>
            <span>↘</span> Expenses
          </NavLink>

          <NavLink to="/savings" className={({ isActive }) =>
            `nav-item ${isActive ? 'active' : ''}`
          }>
            <span>◈</span> Savings
          </NavLink>

          <NavLink to="/reports" className={({ isActive }) =>
            `nav-item ${isActive ? 'active' : ''}`
          }>
            <span>▤</span> Reports
          </NavLink>

        </nav>

        <div className="sidebar-bottom">
          <div className="profile-avatar">T</div>
          <div>
            <strong>My Account</strong>
            <small>Personal Finance</small>
          </div>
        </div>

      </aside>

      <main className="main-content">

        <Routes>

          <Route
            path="/"
            element={<Navigate to="/dashboard" replace />}
          />

          <Route
            path="/dashboard"
            element={<Dashboard />}
          />

          <Route path="/income" element={<Income />} />

          <Route path="/expenses" element={<Expenses />} />

          <Route path="/savings" element={<Savings />} />

          <Route path="/reports" element={<Reports />} />

          <Route
            path="*"
            element={
              <PlaceholderPage
                title="Page not found"
                description="The page you requested does not exist."
              />
            }
          />

        </Routes>

      </main>

    </div>
  )
}

export default App
