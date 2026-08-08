// CinemaSeat API Client - Connects to Spring Boot Microservices via API Gateway
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

export async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('cinemaseat_token')
  
  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers
    }
  }

  try {
    const response = await fetch(`${BASE_URL}${endpoint}`, config)
    
    // For 204 No Content
    if (response.status === 204) return null
    
    const text = await response.text()
    if (!text) return null
    
    const data = JSON.parse(text)
    
    if (!response.ok) {
      throw new Error(data.message || `API Error ${response.status}`)
    }
    
    return data
  } catch (error) {
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      console.warn(`API unreachable [${endpoint}] - backend may be starting up`)
    }
    throw error
  }
}

// Convenience helper for raw fetch (no JSON parsing)
export async function apiRawFetch(endpoint, options = {}) {
  const token = localStorage.getItem('cinemaseat_token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers
  }
  return fetch(`${BASE_URL}${endpoint}`, { ...options, headers })
}
