<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { UserPlus, User, Mail, Lock, Eye, EyeOff, CheckCircle2, AlertCircle } from '@lucide/vue'

const router = useRouter()
const authStore = useAuthStore()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const acceptTerms = ref(true)
const statusMessage = ref({ type: '', text: '' })

// Password strength calculator
const passwordStrength = computed(() => {
  const p = password.value
  if (!p) return { score: 0, label: '', color: 'bg-slate-200' }
  if (p.length < 6) return { score: 1, label: 'Weak', color: 'bg-rose-500' }
  if (p.length >= 8 && /[A-Z]/.test(p) && /[0-9]/.test(p)) {
    return { score: 3, label: 'Strong', color: 'bg-emerald-500' }
  }
  return { score: 2, label: 'Medium', color: 'bg-orange-500' }
})

async function handleSignUp() {
  if (!name.value || !email.value || !password.value) {
    statusMessage.value = { type: 'error', text: 'Please fill in all required fields.' }
    return
  }

  if (password.value !== confirmPassword.value) {
    statusMessage.value = { type: 'error', text: 'Passwords do not match!' }
    return
  }

  if (!acceptTerms.value) {
    statusMessage.value = { type: 'error', text: 'Please accept the Terms and Conditions.' }
    return
  }

  const result = await authStore.signup(name.value, email.value, password.value)
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
    <!-- Background Light Blur Circles -->
    <div class="absolute -top-40 -right-40 w-96 h-96 bg-orange-500/10 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute -bottom-40 -left-40 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>

    <div class="w-full max-w-md relative z-10">
      <!-- Card Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-tr from-emerald-600 via-teal-500 to-orange-500 p-0.5 shadow-lg shadow-emerald-500/20 mb-4">
          <div class="w-full h-full bg-white rounded-[14px] flex items-center justify-center">
            <UserPlus class="w-7 h-7 text-emerald-600" />
          </div>
        </div>
        <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">Create Account</h1>
        <p class="text-xs text-slate-500 mt-2">Create your CinemaSeat account to book movie tickets</p>
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



        <!-- Registration Form -->
        <form @submit.prevent="handleSignUp" class="space-y-4">
          <!-- Full Name -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Full Name</label>
            <div class="relative flex items-center">
              <User class="w-4 h-4 text-slate-400 absolute left-3.5" />
              <input 
                v-model="name" 
                type="text" 
                required 
                placeholder="John Doe"
                class="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 pl-10 pr-4 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
              />
            </div>
          </div>

          <!-- Email Address -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Email Address</label>
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

          <!-- Password -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Password</label>
            <div class="relative flex items-center">
              <Lock class="w-4 h-4 text-slate-400 absolute left-3.5" />
              <input 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                required 
                placeholder="Minimum 8 characters"
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
            <!-- Password Strength Bar -->
            <div v-if="password" class="mt-1.5 flex items-center gap-2">
              <div class="flex-1 h-1 bg-slate-200 rounded-full overflow-hidden">
                <div :class="['h-full transition-all duration-300', passwordStrength.color]" :style="{ width: (passwordStrength.score * 33.3) + '%' }"></div>
              </div>
              <span class="text-[10px] font-semibold text-slate-500">{{ passwordStrength.label }}</span>
            </div>
          </div>

          <!-- Confirm Password -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 mb-1">Confirm Password</label>
            <div class="relative flex items-center">
              <Lock class="w-4 h-4 text-slate-400 absolute left-3.5" />
              <input 
                v-model="confirmPassword" 
                :type="showPassword ? 'text' : 'password'" 
                required 
                placeholder="Re-enter password"
                class="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 pl-10 pr-4 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
              />
            </div>
          </div>

          <!-- Terms Checkbox -->
          <div class="flex items-center">
            <input 
              v-model="acceptTerms" 
              id="accept-terms" 
              type="checkbox" 
              class="w-4 h-4 rounded bg-slate-50 border-slate-300 text-emerald-600 focus:ring-emerald-500/20" 
            />
            <label for="accept-terms" class="ml-2 text-xs text-slate-600">
              I agree to the <a href="#" class="text-emerald-600 font-semibold hover:underline">Terms of Service</a> & <a href="#" class="text-emerald-600 font-semibold hover:underline">Privacy Policy</a>
            </label>
          </div>

          <!-- Submit Button -->
          <button 
            type="submit" 
            :disabled="authStore.loading"
            class="w-full py-3 bg-gradient-to-r from-indigo-600 to-indigo-700 hover:opacity-95 text-white font-bold text-xs uppercase tracking-wider rounded-xl shadow-md shadow-indigo-500/20 flex items-center justify-center gap-2 transition-all hover:scale-[1.01] active:scale-[0.99] disabled:opacity-50 cursor-pointer"
          >
            <span v-if="!authStore.loading">Create Account</span>
            <span v-else class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4 text-white" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
              </svg>
              Creating Account...
            </span>
          </button>
        </form>
      </div>

      <!-- Footer Switch -->
      <p class="text-center text-xs text-slate-500 mt-6">
        Already have an account? 
        <router-link to="/signin" class="text-emerald-600 font-bold hover:underline">Sign in here</router-link>
      </p>
    </div>
  </div>
</template>
