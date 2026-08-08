<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  Film,
  Search,
  MapPin,
  User,
  LogOut,
  ChevronDown,
  Globe,
  Share2,
  Video,
  Ticket,
  Menu,
  X,
  Sparkles
} from '@lucide/vue'

const router = useRouter()
const authStore = useAuthStore()

const isMobileMenuOpen = ref(false)
const isProfileDropdownOpen = ref(false)
const searchQuery = ref('')
const selectedLocation = ref('Sony Square, Mirpur, Dhaka')

const locations = [
  'Sony Square, Mirpur, Dhaka',
  'Bali Arcade, Chattogram',
  'Bashundhara City, Dhaka',
  'SKS Tower, Mohakhali, Dhaka'
]

function handleLogout() {
  authStore.logout()
  isProfileDropdownOpen.value = false
  router.push('/signin')
}

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/', query: { search: searchQuery.value } })
  }
}
</script>

<template>
  <header class="sticky top-0 z-50 w-full bg-slate-950/90 backdrop-blur-xl border-b border-slate-800 shadow-2xl">
    
    <!-- Top Main Header Bar -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3.5 flex items-center justify-between gap-6">
      
      <!-- Brand Logo -->
      <router-link to="/" class="flex items-center gap-3 group">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-purple-600 flex items-center justify-center text-white shadow-lg shadow-indigo-600/30 group-hover:scale-105 transition-transform">
          <Film class="w-5 h-5" />
        </div>
        <div class="flex flex-col">
          <span class="text-2xl font-black text-white tracking-tight leading-none">
            Cinema<span class="text-indigo-400">Seat</span>
          </span>
          <span class="text-[10px] text-slate-400 font-bold tracking-widest uppercase mt-0.5">High Concurrency Ticketing</span>
        </div>
      </router-link>

      <!-- Search & Location Controls -->
      <div class="hidden md:flex flex-1 max-w-xl items-center gap-3">
        <!-- Location Picker -->
        <div class="flex items-center bg-slate-900 border border-slate-800 rounded-xl px-3.5 py-2 text-xs">
          <MapPin class="w-4 h-4 text-indigo-400 mr-2 shrink-0" />
          <select v-model="selectedLocation" class="bg-transparent text-slate-200 focus:outline-none cursor-pointer font-bold">
            <option v-for="loc in locations" :key="loc" :value="loc" class="bg-slate-900 text-white">
              {{ loc }}
            </option>
          </select>
        </div>

        <!-- Search Input -->
        <form @submit.prevent="handleSearch" class="flex-1 flex items-center bg-slate-900 border border-slate-800 rounded-xl overflow-hidden focus-within:border-indigo-500 transition-all">
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="Search movie title..." 
            class="w-full bg-transparent px-4 py-2 text-xs font-medium text-white placeholder-slate-500 focus:outline-none"
          />
          <button type="submit" class="bg-indigo-600 hover:bg-indigo-500 text-white px-3.5 py-2">
            <Search class="w-3.5 h-3.5" />
          </button>
        </form>
      </div>

      <!-- Navigation & User Actions -->
      <div class="flex items-center gap-5">
        
        <nav class="hidden lg:flex items-center gap-6 text-xs font-extrabold uppercase tracking-wider text-slate-300">
          <router-link to="/" class="hover:text-indigo-400 transition-colors">Movies</router-link>
          <a href="#movies-catalog" class="hover:text-indigo-400 transition-colors">Showtimes</a>
          <a href="#movies-catalog" class="hover:text-indigo-400 transition-colors">Locations</a>
        </nav>

        <!-- User Authentication -->
        <div class="relative border-l border-slate-800 pl-4">
          <template v-if="authStore.isAuthenticated">
            <button 
              @click="isProfileDropdownOpen = !isProfileDropdownOpen"
              class="flex items-center gap-2.5 p-1.5 rounded-xl bg-slate-900 hover:bg-slate-850 border border-slate-800 transition-colors cursor-pointer"
            >
              <div class="w-8 h-8 rounded-lg bg-indigo-600 text-white text-xs font-black flex items-center justify-center shadow-md">
                {{ (authStore.user?.name || 'U').charAt(0) }}
              </div>
              <span class="hidden sm:inline text-xs font-bold text-slate-200">{{ authStore.user?.name }}</span>
              <ChevronDown class="w-4 h-4 text-slate-400" />
            </button>

            <div v-if="isProfileDropdownOpen" class="absolute right-0 mt-2 w-56 bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl py-2 z-50 text-xs">
              <div class="px-4 py-2.5 border-b border-slate-800">
                <p class="font-bold text-white truncate">{{ authStore.user?.name }}</p>
                <p class="text-[11px] text-slate-400 truncate">{{ authStore.user?.email }}</p>
              </div>
              <button @click="handleLogout" class="w-full text-left px-4 py-2.5 text-rose-400 hover:bg-rose-500/10 flex items-center gap-2 font-bold cursor-pointer">
                <LogOut class="w-4 h-4" />
                Sign Out
              </button>
            </div>
          </template>

          <template v-else>
            <router-link 
              to="/signin" 
              class="bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold px-5 py-2.5 rounded-xl text-xs uppercase tracking-wider transition-all shadow-lg shadow-indigo-600/30"
            >
              Sign In
            </router-link>
          </template>
        </div>

        <button @click="isMobileMenuOpen = !isMobileMenuOpen" class="md:hidden text-slate-300">
          <Menu v-if="!isMobileMenuOpen" class="w-6 h-6" />
          <X v-else class="w-6 h-6" />
        </button>

      </div>

    </div>

    <!-- Sub-bar announcement -->
    <div class="bg-slate-900/60 border-t border-slate-800/80 py-1.5 px-4 text-center text-[11px] text-slate-400 font-medium flex items-center justify-center gap-2">
      <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
      <span>Real-Time Concurrency Seat Hold Engine Active • Sony Square & Bali Arcade</span>
    </div>

  </header>
</template>
