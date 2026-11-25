
import { useEffect, useState } from 'react'

const emptyForm = {
  title: '',
  amount: '',
  source: '',
  incomeDate: new Date().toLocaleDateString('en-CA'),
  notes: ''
}

function Income() {
  const [incomes, setIncomes] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const formatCurrency = (amount) =>
    Number(amount || 0).toLocaleString('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    })

  const fetchIncomes = async () => {
    try {
      setLoading(true)
      const response = await fetch('/api/incomes')

      if (!response.ok) {
        throw new Error('Unable to load income transactions.')
      }

      const data = await response.json()
      setIncomes(data)
      setError('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchIncomes()
  }, [])

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    })
  }

  const openAddForm = () => {
    setForm(emptyForm)
    setEditingId(null)
    setError('')
    setShowForm(true)
  }

  const openEditForm = (income) => {
    setForm({
      title: income.title,
      amount: income.amount,
      source: income.source,
      incomeDate: income.incomeDate,
      notes: income.notes || ''
    })

    setEditingId(income.id)
    setError('')
    setShowForm(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const payload = {
      title: form.title.trim(),
      amount: Number(form.amount),
      source: form.source.trim(),
      incomeDate: form.incomeDate,
      notes: form.notes.trim()
    }

    try {
      const url = editingId
        ? `/api/incomes/${editingId}`
        : '/api/incomes'

      const response = await fetch(url, {
        method: editingId ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const message = await response.text()
        throw new Error(message || 'Unable to save income.')
      }

      await fetchIncomes()
      setShowForm(false)
      setForm(emptyForm)
      setEditingId(null)
    } catch (err) {
      setError(err.message)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this income?')) {
      return
    }

    try {
      const response = await fetch(`/api/incomes/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        throw new Error('Unable to delete income.')
      }

      await fetchIncomes()
    } catch (err) {
      setError(err.message)
    }
  }

  const totalIncome = incomes.reduce(
    (total, income) => total + Number(income.amount || 0),
    0
  )

  return (
    <div className="income-page">

      <div className="breadcrumb">Workspace / Income</div>

      <div className="income-header">
        <div>
          <h1>Income</h1>
          <p className="subtitle">
            Track and manage all your income sources.
          </p>
        </div>

        <button className="primary-button" onClick={openAddForm}>
          + Add Income
        </button>
      </div>

      <div className="income-summary panel">
        <div>
          <span className="summary-label">Total Income</span>
          <h2>{formatCurrency(totalIncome)}</h2>
          <span className="summary-description">
            Across all recorded transactions
          </span>
        </div>

        <div className="income-summary-icon">↗</div>
      </div>

      {showForm && (
        <div className="panel income-form-panel">
          <div className="form-heading">
            <h2>{editingId ? 'Edit Income' : 'Add New Income'}</h2>
            <button
              className="close-button"
              type="button"
              onClick={() => setShowForm(false)}
            >
              ✕
            </button>
          </div>

          <form onSubmit={handleSubmit} className="income-form">

            <div className="form-grid">

              <div className="form-field">
                <label>Income Title</label>
                <input
                  name="title"
                  value={form.title}
                  onChange={handleChange}
                  placeholder="e.g. Monthly Salary"
                  required
                />
              </div>

              <div className="form-field">
                <label>Amount (₹)</label>
                <input
                  name="amount"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={form.amount}
                  onChange={handleChange}
                  placeholder="e.g. 45000"
                  required
                />
              </div>

              <div className="form-field">
                <label>Income Source</label>
                <input
                  name="source"
                  value={form.source}
                  onChange={handleChange}
                  placeholder="e.g. Salary, Freelancing"
                  required
                />
              </div>

              <div className="form-field">
                <label>Income Date</label>
                <input
                  name="incomeDate"
                  type="date"
                  value={form.incomeDate}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-field full-width">
                <label>Notes (Optional)</label>
                <textarea
                  name="notes"
                  value={form.notes}
                  onChange={handleChange}
                  placeholder="Add any additional details..."
                  rows="3"
                />
              </div>

            </div>

            {error && <p className="income-error">{error}</p>}

            <div className="form-actions">
              <button
                type="button"
                className="secondary-button"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>

              <button type="submit" className="primary-button">
                {editingId ? 'Update Income' : 'Save Income'}
              </button>
            </div>

          </form>
        </div>
      )}

      {error && !showForm && (
        <p className="income-error">{error}</p>
      )}

      <div className="panel income-table-panel">

        <div className="table-heading">
          <div>
            <h2>Income Transactions</h2>
            <p className="summary-description">
              Your recorded income history
            </p>
          </div>

          <span className="transaction-count">
            {incomes.length} transactions
          </span>
        </div>

        {loading ? (
          <p className="table-message">Loading income transactions...</p>
        ) : incomes.length === 0 ? (
          <p className="table-message">
            No income transactions yet. Click "Add Income" to get started.
          </p>
        ) : (
          <div className="table-scroll">
            <table className="income-table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Source</th>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Notes</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {incomes.map((income) => (
                  <tr key={income.id}>
                    <td className="income-title-cell">{income.title}</td>
                    <td>{income.source}</td>
                    <td>{income.incomeDate}</td>
                    <td className="income-amount">
                      {formatCurrency(income.amount)}
                    </td>
                    <td>{income.notes || '—'}</td>
                    <td>
                      <div className="table-actions">
                        <button
                          className="edit-button"
                          onClick={() => openEditForm(income)}
                        >
                          Edit
                        </button>

                        <button
                          className="delete-button"
                          onClick={() => handleDelete(income.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>

            </table>
          </div>
        )}

      </div>

    </div>
  )
}

export default Income
