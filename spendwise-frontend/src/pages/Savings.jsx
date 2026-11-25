
import { useEffect, useState } from 'react'

const emptyForm = {
  amount: '',
  type: 'DEPOSIT',
  savingsDate: new Date().toISOString().slice(0, 10),
  notes: ''
}

function Savings() {
  const [transactions, setTransactions] = useState([])
  const [balance, setBalance] = useState({
    openingBalance: 0,
    totalDeposits: 0,
    totalWithdrawals: 0,
    currentBalance: 0
  })

  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [openingBalance, setOpeningBalance] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const formatMoney = (value) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(Number(value || 0))

  
async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    }
  })

  const responseText = await response.text()

  if (!response.ok) {
    let message = 'Something went wrong.'

    if (responseText) {
      try {
        const data = JSON.parse(responseText)
        message = data.message || data.detail || data.error || message
      } catch {
        message = responseText
      }
    }

    throw new Error(message)
  }

  if (!responseText) return null

  // Support both JSON and plain-text backend responses.
  try {
    return JSON.parse(responseText)
  } catch {
    return responseText
  }
}


  async function loadSavings() {
    setLoading(true)
    setError('')

    try {
      const [transactionData, balanceData, settingsData] = await Promise.all([
        apiRequest('/api/savings'),
        apiRequest('/api/savings/balance'),
        apiRequest('/api/savings/settings/opening-balance')
      ])

      setTransactions(transactionData)
      setBalance(balanceData)
      setOpeningBalance(String(settingsData.openingBalance ?? 0))
    } catch (err) {
      setError(err.message || 'Unable to load savings data.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadSavings()
  }, [])

  function handleFormChange(event) {
    const { name, value } = event.target
    setForm((previous) => ({
      ...previous,
      [name]: value
    }))
  }

  function resetForm() {
    setForm(emptyForm)
    setEditingId(null)
  }

  
async function handleSubmit(event) {
  event.preventDefault()
  setError('')
  setSuccess('')

  const amount = Number(form.amount)

  // Check available balance before submitting a withdrawal.
  if (form.type === 'WITHDRAWAL') {
    let availableBalance = Number(balance.currentBalance || 0)

    // When editing, exclude the old transaction from the calculation.
    if (editingId !== null) {
      const oldTransaction = transactions.find(
        (transaction) => transaction.id === editingId
      )

      if (oldTransaction) {
        if (oldTransaction.type === 'DEPOSIT') {
          availableBalance -= Number(oldTransaction.amount)
        } else if (oldTransaction.type === 'WITHDRAWAL') {
          availableBalance += Number(oldTransaction.amount)
        }
      }
    }

    if (amount > availableBalance + 0.001) {
      setError(
        `Insufficient savings! You can withdraw up to ${formatMoney(
          Math.max(0, availableBalance)
        )}.`
      )
      return
    }
  }

  const payload = {
    amount,
    type: form.type,
    savingsDate: form.savingsDate,
    notes: form.notes
  }

  try {
    if (editingId !== null) {
      await apiRequest(`/api/savings/${editingId}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      setSuccess('Savings transaction updated successfully.')
    } else {
      await apiRequest('/api/savings', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      setSuccess('Savings transaction added successfully.')
    }

    resetForm()
    await loadSavings()
  } catch (err) {
    setError(
      err.message === 'Bad Request'
        ? 'Unable to complete this transaction. Please check the amount and available balance.'
        : err.message
    )
  }
}


  function handleEdit(transaction) {
    setEditingId(transaction.id)
    setForm({
      amount: String(transaction.amount),
      type: transaction.type,
      savingsDate: transaction.savingsDate,
      notes: transaction.notes || ''
    })
    setError('')
    setSuccess('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }



async function handleDelete(id) {
  const confirmed = window.confirm(
    'Are you sure you want to delete this savings transaction?'
  )

  if (!confirmed) return

  setError('')
  setSuccess('')

  try {
    // Delete transaction in the backend.
    await apiRequest(`/api/savings/${id}`, {
      method: 'DELETE'
    })

    // Immediately update the transaction list.
    setTransactions((previous) =>
      previous.filter((transaction) => transaction.id !== id)
    )

    if (editingId === id) {
      resetForm()
    }

    setSuccess('Transaction deleted successfully.')

    // Refresh balance separately.
    try {
      const updatedBalance = await apiRequest('/api/savings/balance')
      setBalance(updatedBalance)
    } catch {
      setError(
        'Transaction deleted successfully, but balance refresh failed. Please refresh the page.'
      )
    }
  } catch (err) {
    setError(err.message || 'Unable to delete transaction.')
  }
}

  async function handleOpeningBalanceSubmit(event) {
    event.preventDefault()
    setError('')
    setSuccess('')

    try {
      const result = await apiRequest(
        '/api/savings/settings/opening-balance',
        {
          method: 'PUT',
          body: JSON.stringify({
            openingBalance: Number(openingBalance)
          })
        }
      )

      setOpeningBalance(String(result.openingBalance ?? openingBalance))
      setSuccess('Opening balance updated successfully.')
      await loadSavings()
    } catch (err) {
      setError(err.message || 'Unable to update opening balance.')
    }
  }

  return (
    <div className="savings-page">
      <div className="breadcrumb">Workspace / Savings</div>

      <div className="savings-heading">
        <div>
          <h1>Savings</h1>
          <p className="subtitle">
            Track your deposits, withdrawals and total savings.
          </p>
        </div>
      </div>

      {error && <div className="savings-alert error">{error}</div>}
      {success && <div className="savings-alert success">{success}</div>}

      <div className="savings-summary-grid">
        <div className="savings-summary-card">
          <span>Opening Balance</span>
          <h2>{formatMoney(balance.openingBalance)}</h2>
          <small>Your starting savings</small>
        </div>

        <div className="savings-summary-card">
          <span>Total Deposits</span>
          <h2 className="deposit-value">
            {formatMoney(balance.totalDeposits)}
          </h2>
          <small>Money added to savings</small>
        </div>

        <div className="savings-summary-card">
          <span>Total Withdrawals</span>
          <h2 className="withdrawal-value">
            {formatMoney(balance.totalWithdrawals)}
          </h2>
          <small>Money taken from savings</small>
        </div>

        <div className="savings-summary-card current-savings-card">
          <span>Current Savings</span>
          <h2>{formatMoney(balance.currentBalance)}</h2>
          <small>Your available savings balance</small>
        </div>
      </div>

      <div className="savings-settings-panel">
        <div>
          <h2>Opening Balance Settings</h2>
          <p>Enter the savings you had before starting SpendWise.</p>
        </div>

        <form
          className="opening-balance-form"
          onSubmit={handleOpeningBalanceSubmit}
        >
          <input
            type="number"
            min="0"
            step="0.01"
            value={openingBalance}
            onChange={(event) => setOpeningBalance(event.target.value)}
            placeholder="Enter opening balance"
            required
          />
          <button type="submit" className="primary-button">
            Update Balance
          </button>
        </form>
      </div>

      <div className="savings-form-panel">
        <div className="savings-section-heading">
          <div>
            <h2>{editingId !== null ? 'Edit Transaction' : 'Add Savings Transaction'}</h2>
            <p>
              {editingId !== null
                ? 'Update the selected savings transaction.'
                : 'Record money deposited into or withdrawn from savings.'}
            </p>
          </div>
        </div>

        <form className="savings-form" onSubmit={handleSubmit}>
          <div className="savings-form-grid">
            <div className="form-group">
              <label>Transaction Type</label>
              <select
                name="type"
                value={form.type}
                onChange={handleFormChange}
                required
              >
                <option value="DEPOSIT">Deposit</option>
                <option value="WITHDRAWAL">Withdrawal</option>
              </select>
            </div>

            <div className="form-group">
              <label>Amount (₹)</label>
              <input
                type="number"
                name="amount"
                min="0.01"
                step="0.01"
                value={form.amount}
                onChange={handleFormChange}
                placeholder="Enter amount"
                required
              />
            </div>

            <div className="form-group">
              <label>Date</label>
              <input
                type="date"
                name="savingsDate"
                value={form.savingsDate}
                onChange={handleFormChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Notes</label>
              <input
                type="text"
                name="notes"
                value={form.notes}
                onChange={handleFormChange}
                placeholder="Optional notes"
              />
            </div>
          </div>

          <div className="savings-form-actions">
            <button type="submit" className="primary-button">
              {editingId !== null ? 'Save Changes' : 'Add Transaction'}
            </button>

            {editingId !== null && (
              <button
                type="button"
                className="secondary-button"
                onClick={resetForm}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="savings-table-panel">
        <div className="savings-section-heading">
          <div>
            <h2>Savings Transactions</h2>
            <p>All your recorded deposits and withdrawals.</p>
          </div>
          <span className="transaction-count">
            {transactions.length} transactions
          </span>
        </div>

        {loading ? (
          <p className="savings-empty">Loading savings...</p>
        ) : transactions.length === 0 ? (
          <p className="savings-empty">
            No savings transactions yet. Add your first transaction above.
          </p>
        ) : (
          <div className="table-wrapper">
            <table className="savings-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Notes</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {[...transactions]
                  .sort((a, b) =>
                    b.savingsDate.localeCompare(a.savingsDate)
                  )
                  .map((transaction) => (
                    <tr key={transaction.id}>
                      <td>{transaction.savingsDate}</td>
                      <td>
                        <span
                          className={`savings-type ${
                            transaction.type === 'DEPOSIT'
                              ? 'deposit-type'
                              : 'withdrawal-type'
                          }`}
                        >
                          {transaction.type}
                        </span>
                      </td>
                      <td className={
                        transaction.type === 'DEPOSIT'
                          ? 'deposit-value'
                          : 'withdrawal-value'
                      }>
                        {formatMoney(transaction.amount)}
                      </td>
                      <td>{transaction.notes || '—'}</td>
                      <td>
                        <div className="savings-actions">
                          <button
                            type="button"
                            onClick={() => handleEdit(transaction)}
                          >
                            Edit
                          </button>
                          <button
                            type="button"
                            className="delete-action"
                            onClick={() => handleDelete(transaction.id)}
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

export default Savings
