import { useEffect, useState } from 'react'
import {
  NavLink,
  Routes,
  Route,
  Navigate,
  Outlet,
  useNavigate
} from 'react-router-dom'

import Dashboard from './pages/Dashboard'
import Income from './pages/Income'
import Expenses from './pages/Expenses'
import Savings from './pages/Savings'
import Reports from './pages/Reports'
import Login from './pages/Login'
import Register from './pages/Register'
import PlaceholderPage from './pages/PlaceholderPage'

import './App.css'

const TOKEN_KEY = 'spendwise_token'
const USER_KEY = 'spendwise_user'

function App() {
  const [auth, setAuth] = useState(() => {
    const token = sessionStorage.getItem(TOKEN_KEY)
    const savedUser = sessionStorage.getItem(USER_KEY)

    let user = null

    try {
      user = savedUser ? JSON.parse(savedUser) : null
    } catch {
      user = null
    }

    return { token, user }
  })

  useEffect(() => {
    function handleUnauthorized() {
      setAuth({ token: null, user: null })
    }

    window.addEventListener(
      'spendwise:unauthorized',
      handleUnauthorized
    )

    return () => {
      window.removeEventListener(
        'spendwise:unauthorized',
        handleUnauthorized
      )
    }
  }, [])

  function handleLogin(data) {
    sessionStorage.setItem(TOKEN_KEY, data.token)

    const user = {
      id: data.id,
      name: data.name,
      email: data.email
    }

    sessionStorage.setItem(USER_KEY, JSON.stringify(user))
    setAuth({ token: data.token, user })
  }

  function handleLogout() {
    sessionStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
    setAuth({ token: null, user: null })
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={
          auth.token
            ? <Navigate to="/dashboard" replace />
            : <Login onLogin={handleLogin} />
        }
      />

      <Route
        path="/register"
        element={
          auth.token
            ? <Navigate to="/dashboard" replace />
            : <Register />
        }
      />

      <Route
        element={
          auth.token
            ? <AppLayout user={auth.user} onLogout={handleLogout} />
            : <Navigate to="/login" replace />
        }
      >
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
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
      </Route>
    </Routes>
  )
}

function AppLayout({ user, onLogout }) {
  const navigate = useNavigate()

  function logout() {
    onLogout()
    navigate('/login', { replace: true })
  }

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
          <div className="profile-avatar">
            {(user?.name || 'U').charAt(0).toUpperCase()}
          </div>

          <div className="profile-details">
            <strong>{user?.name || 'My Account'}</strong>
            <small>{user?.email || 'Personal Finance'}</small>
          </div>

          <button
            className="logout-button"
            onClick={logout}
            title="Logout"
          >
            ↪
          </button>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}

export default App