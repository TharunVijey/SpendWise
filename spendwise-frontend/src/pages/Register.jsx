import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { apiRequest } from '../api'

function Register() {
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSuccess('')

    if (password.length < 8) {
      setError('Password must contain at least 8 characters.')
      return
    }

    setLoading(true)

    try {
      await apiRequest('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          name: name.trim(),
          email: email.trim(),
          password
        })
      })

      setSuccess('Registration successful! Redirecting to login...')

      setTimeout(() => {
        navigate('/login')
      }, 1200)
    } catch (err) {
      setError(err.message || 'Unable to register.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-brand">
          <div className="brand-icon">S</div>
          <h1>SpendWise</h1>
        </div>

        <h2>Create your account</h2>
        <p className="auth-subtitle">
          Start managing your money smarter.
        </p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-field">
            <label>Full Name</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Enter your name"
              maxLength="100"
              required
            />
          </div>

          <div className="form-field">
            <label>Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-field">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="At least 8 characters"
              minLength="8"
              maxLength="72"
              required
            />
          </div>

          {error && <p className="auth-error">{error}</p>}
          {success && <p className="auth-success">{success}</p>}

          <button
            type="submit"
            className="primary-button auth-submit"
            disabled={loading}
          >
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="auth-switch">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  )
}

export default Register