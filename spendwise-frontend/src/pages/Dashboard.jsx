
import { useEffect, useState } from 'react'
import { apiRequest } from '../api'

const money = (value) =>
  new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2
  }).format(value ?? 0)

function Dashboard() {
  const today = new Date()
  const year = today.getFullYear()
  const month = today.getMonth() + 1

  const [dashboard, setDashboard] = useState(null)
  const [activities, setActivities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true)
        setError('')

        const [
  dashboardData,
  incomes,
  expenses,
  savings
] = await Promise.all([
  apiRequest(`/api/dashboard?year=${year}&month=${month}`),
  apiRequest('/api/incomes'),
  apiRequest('/api/expenses'),
  apiRequest('/api/savings')
])

        const combined = [
          ...incomes.map(item => ({
            ...item,
            activityType: 'Income',
            date: item.incomeDate
          })),
          ...expenses.map(item => ({
            ...item,
            activityType: 'Expense',
            date: item.expenseDate
          })),
          ...savings.map(item => ({
            ...item,
            activityType: item.type,
            date: item.savingsDate
          }))
        ]

        combined.sort((a, b) => new Date(b.date) - new Date(a.date))

        setDashboard(dashboardData)
        setActivities(combined.slice(0, 6))
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    loadDashboard()
  }, [year, month])

  const monthName = new Date(year, month - 1).toLocaleString('en-IN', {
    month: 'long'
  })

  if (loading) return <div className="message">Loading dashboard...</div>

  if (error) {
    return <div className="error-message">{error} Check that your backend is running.</div>
  }

  if (!dashboard) return null

  return (
    <>
      <header className="topbar">
        <div>
          <div className="breadcrumb">Workspace / Overview</div>
          <h1>Dashboard</h1>
          <p className="subtitle">Here's your financial overview.</p>
        </div>

        <div className="month-badge">▦ {monthName} {year}</div>
      </header>

      <section className="welcome-banner">
        <div>
          <span className="banner-label">YOUR FINANCES AT A GLANCE</span>
          <h2>Take control of your money.</h2>
          <p>Track your income, manage expenses and grow your savings.</p>
        </div>
        <div className="banner-symbol">₹</div>
      </section>

      <section className="section-heading">
        <h2>Monthly overview</h2>
        <p>Your financial activity for {monthName}.</p>
      </section>

      <section className="stats-grid">
        <StatCard label="Total Income" amount={money(dashboard.totalIncome)}
          icon="↗" color="green" description="Money received this month" />

        <StatCard label="Total Expenses" amount={money(dashboard.totalExpenses)}
          icon="↘" color="red" description="Money spent this month" />

        <StatCard label="Monthly Surplus" amount={money(dashboard.monthlySurplus)}
          icon="⌁" color="blue" description="Income minus expenses" />

        <StatCard label="Overall Savings" amount={money(dashboard.overallSavingsBalance)}
          icon="◈" color="purple" description="Your current savings balance" />
      </section>

      <section className="lower-grid">
        <div className="panel activity-panel">
          <div className="panel-heading">
            <div>
              <h2>Recent transactions</h2>
              <p>Your latest financial activity</p>
            </div>
            <span className="view-label">Latest 6</span>
          </div>

          {activities.length === 0 ? (
            <div className="empty-state">No transactions yet.</div>
          ) : (
            <div className="transaction-list">
              {activities.map((item, index) => {
                const positive =
                  item.activityType === 'Income' ||
                  item.activityType === 'DEPOSIT'

                return (
                  <div className="transaction"
                    key={`${item.activityType}-${item.id ?? index}`}>
                    <div className={`transaction-icon ${positive ? 'positive' : 'negative'}`}>
                      {positive ? '+' : '−'}
                    </div>

                    <div className="transaction-info">
                      <strong>{item.title || item.activityType}</strong>
                      <small>{item.activityType} · {item.date}</small>
                    </div>

                    <strong className={positive ? 'amount-positive' : 'amount-negative'}>
                      {positive ? '+' : '−'}{money(item.amount)}
                    </strong>
                  </div>
                )
              })}
            </div>
          )}
        </div>

        <div className="panel savings-panel">
          <div className="panel-heading">
            <div>
              <h2>Savings overview</h2>
              <p>Your savings position</p>
            </div>
            <span className="savings-icon">◈</span>
          </div>

          <div className="savings-total">
            {money(dashboard.overallSavingsBalance)}
          </div>
          <p className="savings-caption">Current savings balance</p>

          <div className="savings-divider" />

          <div className="savings-row">
            <span>Deposits this month</span>
            <strong className="amount-positive">
              {money(dashboard.savingsDeposits)}
            </strong>
          </div>

          <div className="savings-row">
            <span>Withdrawals this month</span>
            <strong className="amount-negative">
              {money(dashboard.savingsWithdrawals)}
            </strong>
          </div>
        </div>
      </section>
    </>
  )
}

function StatCard({ label, amount, icon, color, description }) {
  return (
    <div className="stat-card">
      <div className="stat-top">
        <span>{label}</span>
        <div className={`stat-icon ${color}`}>{icon}</div>
      </div>
      <h3>{amount}</h3>
      <p>{description}</p>
    </div>
  )
}

export default Dashboard
