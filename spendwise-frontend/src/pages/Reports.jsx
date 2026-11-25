
import { useEffect, useState } from 'react'
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts'

const monthNames = [
  'January', 'February', 'March', 'April',
  'May', 'June', 'July', 'August',
  'September', 'October', 'November', 'December'
]

function Reports() {
  const today = new Date()

  const [selectedMonth, setSelectedMonth] = useState(
    String(today.getMonth() + 1)
  )
  const [selectedYear, setSelectedYear] = useState(
    String(today.getFullYear())
  )

  const [monthlyReport, setMonthlyReport] = useState(null)
  const [yearlyReport, setYearlyReport] = useState(null)
  const [monthlyTrend, setMonthlyTrend] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const formatMoney = (value) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(Number(value || 0))

  async function fetchReport(url) {
    const response = await fetch(url)
    const responseText = await response.text()

    if (!response.ok) {
      let message = 'Unable to load report.'

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

    try {
      return JSON.parse(responseText)
    } catch {
      throw new Error('The server returned an invalid report response.')
    }
  }

  useEffect(() => {
    let cancelled = false

    async function loadReports() {
      setLoading(true)
      setError('')

      try {
        const year = Number(selectedYear)
        const month = Number(selectedMonth)

        const [monthlyData, yearlyData, trendData] = await Promise.all([
          fetchReport(
            `/api/reports/monthly?year=${year}&month=${month}`
          ),
          fetchReport(
            `/api/reports/yearly?year=${year}`
          ),
          Promise.all(
            monthNames.map((_, index) =>
              fetchReport(
                `/api/reports/monthly?year=${year}&month=${index + 1}`
              )
            )
          )
        ])

        if (cancelled) return

        setMonthlyReport(monthlyData)
        setYearlyReport(yearlyData)

        const formattedTrend = trendData.map((report, index) => ({
          month: monthNames[index].slice(0, 3),
          income: Number(report.totalIncome || 0),
          expenses: Number(report.totalExpenses || 0),
          surplus: Number(report.monthlySurplus || 0)
        }))

        setMonthlyTrend(formattedTrend)
      } catch (err) {
        if (!cancelled) {
          setError(err.message || 'Unable to load reports.')
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadReports()

    return () => {
      cancelled = true
    }
  }, [selectedMonth, selectedYear])

  const availableYears = Array.from(
    { length: 8 },
    (_, index) => today.getFullYear() - index
  )

  const selectedMonthName = monthNames[Number(selectedMonth) - 1]

  return (
    <div className="reports-page">
      <div className="breadcrumb">Workspace / Reports</div>

      <div className="reports-heading">
        <div>
          <h1>Reports</h1>
          <p className="subtitle">
            Understand your financial activity with monthly and yearly reports.
          </p>
        </div>
      </div>

      <div className="reports-filter-panel">
        <div>
          <label htmlFor="report-month">Month</label>
          <select
            id="report-month"
            value={selectedMonth}
            onChange={(event) => setSelectedMonth(event.target.value)}
          >
            {monthNames.map((month, index) => (
              <option key={month} value={String(index + 1)}>
                {month}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="report-year">Year</label>
          <select
            id="report-year"
            value={selectedYear}
            onChange={(event) => setSelectedYear(event.target.value)}
          >
            {availableYears.map((year) => (
              <option key={year} value={String(year)}>
                {year}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && (
        <div className="reports-alert">
          {error}
        </div>
      )}

      {loading ? (
        <div className="reports-loading">Loading your reports...</div>
      ) : monthlyReport && yearlyReport ? (
        <>
          <section className="reports-section">
            <div className="reports-section-heading">
              <div>
                <h2>{selectedMonthName} {selectedYear}</h2>
                <p>Monthly financial summary</p>
              </div>
            </div>

            <div className="reports-summary-grid">
              <div className="report-card">
                <span>Total Income</span>
                <h3 className="report-income">
                  {formatMoney(monthlyReport.totalIncome)}
                </h3>
              </div>

              <div className="report-card">
                <span>Total Expenses</span>
                <h3 className="report-expenses">
                  {formatMoney(monthlyReport.totalExpenses)}
                </h3>
              </div>

              <div className="report-card report-surplus-card">
                <span>Monthly Surplus</span>
                <h3>
                  {formatMoney(monthlyReport.monthlySurplus)}
                </h3>
                <small>Income minus expenses</small>
              </div>

              <div className="report-card">
                <span>Savings Deposits</span>
                <h3 className="report-income">
                  {formatMoney(monthlyReport.savingsDeposits)}
                </h3>
              </div>

              <div className="report-card">
                <span>Savings Withdrawals</span>
                <h3 className="report-expenses">
                  {formatMoney(monthlyReport.savingsWithdrawals)}
                </h3>
              </div>
            </div>
          </section>

          <section className="reports-section">
            <div className="reports-section-heading">
              <div>
                <h2>{selectedYear} Yearly Summary</h2>
                <p>Your overall financial performance this year</p>
              </div>
            </div>

            <div className="reports-summary-grid yearly-summary-grid">
              <div className="report-card">
                <span>Annual Income</span>
                <h3 className="report-income">
                  {formatMoney(yearlyReport.totalIncome)}
                </h3>
              </div>

              <div className="report-card">
                <span>Annual Expenses</span>
                <h3 className="report-expenses">
                  {formatMoney(yearlyReport.totalExpenses)}
                </h3>
              </div>

              <div className="report-card report-surplus-card">
                <span>Yearly Surplus</span>
                <h3>
                  {formatMoney(yearlyReport.yearlySurplus)}
                </h3>
                <small>Income minus expenses</small>
              </div>

              <div className="report-card">
                <span>Yearly Savings Deposits</span>
                <h3 className="report-income">
                  {formatMoney(yearlyReport.savingsDeposits)}
                </h3>
              </div>

              <div className="report-card">
                <span>Yearly Savings Withdrawals</span>
                <h3 className="report-expenses">
                  {formatMoney(yearlyReport.savingsWithdrawals)}
                </h3>
              </div>
            </div>
          </section>

          <section className="reports-chart-panel">
            <div className="reports-section-heading">
              <div>
                <h2>Income vs Expenses</h2>
                <p>Month-by-month comparison for {selectedYear}</p>
              </div>
            </div>

            {monthlyTrend.length === 0 ? (
              <p className="reports-empty">No report data available.</p>
            ) : (
              <div className="reports-chart">
                <ResponsiveContainer width="100%" height={330}>
                  <BarChart
                    data={monthlyTrend}
                    margin={{ top: 10, right: 15, left: 10, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="month" />
                    <YAxis
                      tickFormatter={(value) =>
                        value >= 1000
                          ? `₹${(value / 1000).toFixed(0)}k`
                          : `₹${value}`
                      }
                    />
                    <Tooltip
                      formatter={(value) => formatMoney(value)}
                    />
                    <Legend />
                    <Bar
                      dataKey="income"
                      name="Income"
                      fill="#22a477"
                      radius={[5, 5, 0, 0]}
                    />
                    <Bar
                      dataKey="expenses"
                      name="Expenses"
                      fill="#e46d75"
                      radius={[5, 5, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            )}
          </section>

          <section className="reports-chart-panel">
            <div className="reports-section-heading">
              <div>
                <h2>Monthly Surplus Trend</h2>
                <p>How much remained after expenses each month</p>
              </div>
            </div>

            <div className="reports-chart">
              <ResponsiveContainer width="100%" height={280}>
                <BarChart
                  data={monthlyTrend}
                  margin={{ top: 10, right: 15, left: 10, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="month" />
                  <YAxis
                    tickFormatter={(value) =>
                      value >= 1000
                        ? `₹${(value / 1000).toFixed(0)}k`
                        : `₹${value}`
                    }
                  />
                  <Tooltip
                    formatter={(value) => formatMoney(value)}
                  />
                  <Bar
                    dataKey="surplus"
                    name="Monthly Surplus"
                    fill="#8170dc"
                    radius={[5, 5, 0, 0]}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </section>
        </>
      ) : (
        <div className="reports-empty">
          No report data available for this period.
        </div>
      )}
    </div>
  )
}

export default Reports
