
import { useEffect, useState } from 'react'
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid
} from 'recharts'

const emptyForm = {
  title: '',
  amount: '',
  category: '',
  expenseDate: new Date().toLocaleDateString('en-CA'),
  notes: ''
}

const categories = [
  'Food',
  'Travel',
  'Shopping',
  'Bills',
  'Entertainment',
  'Health',
  'Education',
  'Rent',
  'Other'
]

function Expenses() {
  const [expenses, setExpenses] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [chartType, setChartType] = useState('pie')
  const today = new Date()

const [filterMonth, setFilterMonth] = useState(
  String(today.getMonth() + 1)
)

const [filterYear, setFilterYear] = useState(
  String(today.getFullYear())
)

  const formatCurrency = (amount) =>
    Number(amount || 0).toLocaleString('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    })

  const fetchExpenses = async () => {
    try {
      setLoading(true)

      const response = await fetch('/api/expenses')

      if (!response.ok) {
        throw new Error('Unable to load expenses.')
      }

      const data = await response.json()
      setExpenses(data)
      setError('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchExpenses()
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

  const openEditForm = (expense) => {
    setForm({
      title: expense.title,
      amount: expense.amount,
      category: expense.category,
      expenseDate: expense.expenseDate,
      notes: expense.notes || ''
    })

    setEditingId(expense.id)
    setError('')
    setShowForm(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const payload = {
      title: form.title.trim(),
      amount: Number(form.amount),
      category: form.category,
      expenseDate: form.expenseDate,
      notes: form.notes.trim()
    }

    try {
      const url = editingId
        ? `/api/expenses/${editingId}`
        : '/api/expenses'

      const response = await fetch(url, {
        method: editingId ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        const message = await response.text()
        throw new Error(message || 'Unable to save expense.')
      }

      await fetchExpenses()
      setShowForm(false)
      setForm(emptyForm)
      setEditingId(null)
    } catch (err) {
      setError(err.message)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this expense?')) {
      return
    }

    try {
      const response = await fetch(`/api/expenses/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        throw new Error('Unable to delete expense.')
      }

      await fetchExpenses()
    } catch (err) {
      setError(err.message)
    }
  }

  const totalExpenses = expenses.reduce(
    (total, expense) => total + Number(expense.amount || 0),
    0
  )

 const filteredExpenses = expenses.filter((expense) => {
  const date = new Date(`${expense.expenseDate}T00:00:00`)

  return (
    date.getMonth() + 1 === Number(filterMonth) &&
    date.getFullYear() === Number(filterYear)
  )
})

const filteredTotal = filteredExpenses.reduce(
  (total, expense) => total + Number(expense.amount || 0),
  0
)

const categoryTotals = filteredExpenses.reduce((totals, expense) => {
  const category = expense.category || 'Other'

  totals[category] =
    (totals[category] || 0) + Number(expense.amount || 0)

  return totals
}, {})

  return (
    <div className="expenses-page">

      <div className="breadcrumb">Workspace / Expenses</div>

      <div className="income-header">
        <div>
          <h1>Expenses</h1>
          <p className="subtitle">
            Track and manage where your money goes.
          </p>
        </div>

        <button className="primary-button" onClick={openAddForm}>
          + Add Expense
        </button>
      </div>

      <div className="income-summary panel">
        <div>
          <span className="summary-label">Total Expenses</span>
          <h2>{formatCurrency(totalExpenses)}</h2>
          <span className="summary-description">
            Across all recorded transactions
          </span>
        </div>

        <div className="expense-summary-icon">↘</div>
      </div>

      {showForm && (
        <div className="panel income-form-panel">

          <div className="form-heading">
            <h2>{editingId ? 'Edit Expense' : 'Add New Expense'}</h2>

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
                <label>Expense Title</label>
                <input
                  name="title"
                  value={form.title}
                  onChange={handleChange}
                  placeholder="e.g. Grocery Shopping"
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
                  placeholder="e.g. 1500"
                  required
                />
              </div>

              <div className="form-field">
                <label>Category</label>
                <select
                  name="category"
                  value={form.category}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a category</option>

                  {categories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-field">
                <label>Expense Date</label>
                <input
                  name="expenseDate"
                  type="date"
                  value={form.expenseDate}
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
                  placeholder="Add additional details..."
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
                {editingId ? 'Update Expense' : 'Save Expense'}
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
            <h2>Expense Transactions</h2>
            <p className="summary-description">
              Your recorded spending history
            </p>
          </div>

          <span className="transaction-count">
            {expenses.length} transactions
          </span>
        </div>

        {loading ? (
          <p className="table-message">Loading expenses...</p>
        ) : expenses.length === 0 ? (
          <p className="table-message">
            No expenses yet. Click "Add Expense" to get started.
          </p>
        ) : (
          <div className="table-scroll">
            <table className="income-table">

              <thead>
                <tr>
                  <th>Title</th>
                  <th>Category</th>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Notes</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {expenses.map((expense) => (
                  <tr key={expense.id}>
                    <td className="income-title-cell">
                      {expense.title}
                    </td>

                    <td>
                      <span className="category-tag">
                        {expense.category}
                      </span>
                    </td>

                    <td>{expense.expenseDate}</td>

                    <td className="expense-amount">
                      {formatCurrency(expense.amount)}
                    </td>

                    <td>{expense.notes || '—'}</td>

                    <td>
                      <div className="table-actions">

                        <button
                          className="edit-button"
                          onClick={() => openEditForm(expense)}
                        >
                          Edit
                        </button>

                        <button
                          className="delete-button"
                          onClick={() => handleDelete(expense.id)}
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

      
<div className="panel category-panel">

<div className="chart-heading">

  <div>
    <h2>Spending by Category</h2>
    <p className="summary-description">
      Category-wise spending for the selected period
    </p>
  </div>

  <div className="chart-controls">

    <select
      className="chart-selector"
      value={filterMonth}
      onChange={(e) => setFilterMonth(e.target.value)}
    >
      {[
        ['1', 'January'],
        ['2', 'February'],
        ['3', 'March'],
        ['4', 'April'],
        ['5', 'May'],
        ['6', 'June'],
        ['7', 'July'],
        ['8', 'August'],
        ['9', 'September'],
        ['10', 'October'],
        ['11', 'November'],
        ['12', 'December']
      ].map(([value, label]) => (
        <option key={value} value={value}>
          {label}
        </option>
      ))}
    </select>

    <select
      className="chart-selector"
      value={filterYear}
      onChange={(e) => setFilterYear(e.target.value)}
    >
      {Array.from(
        { length: 6 },
        (_, i) => new Date().getFullYear() - 3 + i
      ).map((year) => (
        <option key={year} value={year}>
          {year}
        </option>
      ))}
    </select>

    <select
      className="chart-selector"
      value={chartType}
      onChange={(e) => setChartType(e.target.value)}
    >
      <option value="pie">Pie Chart</option>
      <option value="donut">Donut Chart</option>
      <option value="bar">Bar Graph</option>
    </select>

  </div>

</div>


  {Object.keys(categoryTotals).length === 0 ? (
    <p className="table-message">
      No expenses recorded for this month.
    </p>
  ) : (
    <div className="chart-container">

      <ResponsiveContainer width="100%" height={330}>

        {chartType === 'bar' ? (

          <BarChart
            data={Object.entries(categoryTotals).map(
              ([name, amount]) => ({ name, amount })
            )}
            margin={{ top: 15, right: 20, left: 10, bottom: 10 }}
          >
            <CartesianGrid
              strokeDasharray="3 3"
              vertical={false}
            />

            <XAxis
              dataKey="name"
              tick={{ fontSize: 12 }}
              interval={0}
              angle={-15}
              textAnchor="end"
              height={55}
            />

            <YAxis
              tick={{ fontSize: 12 }}
              tickFormatter={(value) =>
                `₹${Number(value).toLocaleString('en-IN')}`
              }
            />

            <Tooltip
              formatter={(value) => [
                formatCurrency(value),
                'Amount'
              ]}
            />

            <Bar
              dataKey="amount"
              name="Expense"
              fill="#6c5ce7"
              radius={[6, 6, 0, 0]}
            />
          </BarChart>

        ) : (

          <PieChart>

            <Pie
              data={Object.entries(categoryTotals).map(
                ([name, amount]) => ({ name, amount })
              )}
              dataKey="amount"
              nameKey="name"
              cx="50%"
              cy="48%"
              outerRadius={110}
              innerRadius={chartType === 'donut' ? 65 : 0}
              paddingAngle={3}
              label={({ name, percent }) =>
                `${name} ${(percent * 100).toFixed(0)}%`
              }
            >
              {Object.entries(categoryTotals).map(
                ([category], index) => {
                  const colors = [
                    '#6c5ce7',
                    '#22a06b',
                    '#f0a44b',
                    '#e45b78',
                    '#3996cf',
                    '#9b72cf',
                    '#45b8ac',
                    '#e08a43',
                    '#8b929e'
                  ]

                  return (
                    <Cell
                      key={category}
                      fill={colors[index % colors.length]}
                    />
                  )
                }
              )}
            </Pie>

            <Tooltip
              formatter={(value) => [
                formatCurrency(value),
                'Amount'
              ]}
            />

            <Legend />

          </PieChart>

        )}

      </ResponsiveContainer>

      {chartType === 'donut' && (
        <p className="chart-total">
          Total: {formatCurrency(filteredTotal)}
        </p>
      )}

    </div>
  )}

</div>


    </div>
  )
}

export default Expenses
