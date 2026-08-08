import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('cinemaseat_user') || 'null'))
  const token = ref(localStorage.getItem('cinemaseat_token') || '')
  const isAuthenticated = ref(!!token.value)
  const loading = ref(false)

  const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1') + '/auth'

  async function login(email, password) {
    loading.value = true
    try {
      const res = await fetch(`${API_BASE_URL}/signin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      const data = await res.json()
      
      if (!res.ok || !data.success) {
        throw new Error(data.message || 'Authentication failed')
      }

      const authData = data.data
      const userData = {
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role
      }

      user.value = userData
      token.value = authData.token
      isAuthenticated.value = true

      localStorage.setItem('cinemaseat_user', JSON.stringify(userData))
      localStorage.setItem('cinemaseat_token', authData.token)

      return { success: true, message: 'Signed in successfully!' }
    } catch (error) {
      return { success: false, message: error.message || 'Authentication failed' }
    } finally {
      loading.value = false
    }
  }

  async function signup(name, email, password) {
    loading.value = true
    try {
      const res = await fetch(`${API_BASE_URL}/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password, role: 'ROLE_USER' })
      })
      const data = await res.json()

      if (!res.ok || !data.success) {
        throw new Error(data.message || 'Registration failed')
      }

      const authData = data.data
      const userData = {
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role
      }

      user.value = userData
      token.value = authData.token
      isAuthenticated.value = true

      localStorage.setItem('cinemaseat_user', JSON.stringify(userData))
      localStorage.setItem('cinemaseat_token', authData.token)

      return { success: true, message: 'Account created successfully!' }
    } catch (error) {
      return { success: false, message: error.message || 'Registration failed' }
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    token.value = ''
    isAuthenticated.value = false
    localStorage.removeItem('cinemaseat_user')
    localStorage.removeItem('cinemaseat_token')
  }

  return {
    user,
    token,
    isAuthenticated,
    loading,
    login,
    signup,
    logout
  }
})
