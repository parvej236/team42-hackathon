<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { LogIn, Mail, Lock, Eye, EyeOff, Sparkles, CheckCircle2, AlertCircle } from '@lucide/vue'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)
const statusMessage = ref({ type: '', text: '' })

async function handleSignIn() {
  if (!email.value || !password.value) {
    statusMessage.value = { type: 'error', text: 'Please fill in all required fields.' }
    return
  }

  const result = await authStore.login(email.value, password.value)
  if (result.success) {
    statusMessage.value = { type: 'success', text: result.message }
    setTimeout(() => {
      router.push('/')
    }, 800)
  } else {
    statusMessage.value = { type: 'error', text: result.message }
  }
}


</script>

<template>
  <div class="min-h-[85vh] flex items-center justify-center px-4 py-12 relative overflow-hidden bg-slate-50">
    <!-- Ambient Light Background Glows -->
    <div class="absolute -top-40 -left-40 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute -bottom-40 -right-40 w-96 h-96 bg-orange-500/10 rounded-full blur-3xl pointer-events-none"></div>

    <div class="w-full max-w-md relative z-10">
      <!-- Card Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-tr from-emerald-600 via-teal-500 to-orange-500 p-0.5 shadow-lg shadow-emerald-500/20 mb-4">
          <div class="w-full h-full bg-white rounded-[14px] flex items-center justify-center">
            <LogIn class="w-7 h-7 text-emerald-600" />
          </div>
        </div>
        <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">Welcome Back</h1>
        <p class="text-xs text-slate-500 mt-2">Sign in to your CinemaSeat account to book tickets</p>
        <p class="text-xs text-slate-400 mt-1">Demo: zayan@cinemaseat.com / password123</p>
      </div>

      <!-- Card Body (Light Theme) -->
      <div class="bg-white border border-slate-200 rounded-3xl p-8 shadow-xl shadow-slate-200/50">
        
        <!-- Status Toast Banner -->
        <div v-if="statusMessage.text" :class="[
          'mb-6 p-4 rounded-2xl text-xs font-medium flex items-center gap-2 transition-all',
          statusMessage.type === 'success' ? 'bg-emerald-50 border border-emerald-200 text-emerald-700' : 'bg-rose-50 border border-rose-200 text-rose-700'
        ]">
          <CheckCircle2 v-if="statusMessage.type === 'success'" class="w-4 h-4 shrink-0" />
          <AlertCircle v-else class="w-4 h-4 shrink-0" />
          <span>{{ statusMessage.text }}</span>
        </div>



        <!-- Login Credentials Form -->
        <form @submit.prevent="handleSignIn" class="space-y-5">
          <!-- Email Field -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1.5">Email Address</label>
            <div class="relative flex items-center">
              <Mail class="w-4 h-4 text-slate-400 absolute left-3.5" />
              <input 
                v-model="email" 
                type="email" 
                required 
                placeholder="name@example.com"
                class="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 pl-10 pr-4 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
              />
            </div>
          </div>

          <!-- Password Field -->
          <div>
            <div class="flex items-center justify-between mb-1.5">
              <label class="text-xs font-semibold text-slate-700">Password</label>
              <a href="#" class="text-[11px] text-orange-600 font-semibold hover:underline">Forgot password?</a>
            </div>
            <div class="relative flex items-center">
              <Lock class="w-4 h-4 text-slate-400 absolute left-3.5" />
              <input 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                required 
                placeholder="••••••••"
                class="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 pl-10 pr-10 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
              />
              <button 
                type="button" 
                @click="showPassword = !showPassword" 
                class="absolute right-3.5 text-slate-400 hover:text-slate-600"
              >
                <Eye v-if="!showPassword" class="w-4 h-4" />
                <EyeOff v-else class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Remember Me Checkbox -->
          <div class="flex items-center">
            <input 
              v-model="rememberMe" 
              id="remember-me" 
              type="checkbox" 
              class="w-4 h-4 rounded bg-slate-50 border-slate-300 text-emerald-600 focus:ring-emerald-500/20" 
            />
            <label for="remember-me" class="ml-2 text-xs text-slate-600">Remember me for 30 days</label>
          </div>

          <!-- Submit Button -->
          <button 
            type="submit" 
            :disabled="authStore.loading"
            class="w-full py-3 bg-gradient-to-r from-indigo-600 to-indigo-700 hover:from-indigo-700 hover:to-indigo-800 text-white font-bold text-xs uppercase tracking-wider rounded-xl shadow-md shadow-indigo-500/20 flex items-center justify-center gap-2 transition-all hover:scale-[1.01] active:scale-[0.99] disabled:opacity-50 cursor-pointer"
          >
            <span v-if="!authStore.loading">Sign In to Account</span>
            <span v-else class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4 text-white" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
              </svg>
              Authenticating...
            </span>
          </button>
        </form>
      </div>

      <!-- Footer Switch -->
      <p class="text-center text-xs text-slate-500 mt-6">
        Don't have an account yet? 
        <router-link to="/signup" class="text-emerald-600 font-bold hover:underline">Sign up for free</router-link>
      </p>
    </div>
  </div>
</template>
