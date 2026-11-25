const TOKEN_KEY = 'spendwise_token'

export async function apiRequest(url, options = {}) {
  const token = sessionStorage.getItem(TOKEN_KEY)

  const headers = {
    ...options.headers
  }

  if (options.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json'
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(url, {
    ...options,
    headers
  })

  const responseText = await response.text()

  if (response.status === 401) {
    if (token) {
      sessionStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem('spendwise_user')
      window.dispatchEvent(new Event('spendwise:unauthorized'))
    }

    throw new Error('Your session has expired. Please log in again.')
  }

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

  try {
    return JSON.parse(responseText)
  } catch {
    return responseText
  }
}