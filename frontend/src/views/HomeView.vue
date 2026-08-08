<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { apiFetch } from '../api'
import {
  Film,
  Search,
  MapPin,
  Calendar,
  Clock,
  Ticket,
  ChevronRight,
  Sparkles,
  Zap,
  ShieldCheck,
  Star,
  Tv,
  CheckCircle2,
  AlertCircle
} from '@lucide/vue'

const router = useRouter()
const route = useRoute()

const activeTab = ref('Now Showing')
const searchQuery = ref(route.query.search || '')
const selectedLocation = ref('Sony Square, Mirpur, Dhaka')
const selectedDate = ref('Today, Aug 8')
const isLoading = ref(true)

const showtimeSlots = ['10:30 AM', '1:05 PM', '4:15 PM', '7:30 PM', '10:30 PM']

const movies = ref([
  {
    id: 1,
    title: 'Spider-Man: Brand New Day',
    category: 'IMAX 3D / Dolby Atmos',
    status: 'Now Showing',
    rating: '9.8',
    votes: '14.2K',
    duration: '2h 35m',
    language: 'English (Subbed)',
    releaseDate: 'July 31, 2026',
    image: 'https://images.unsplash.com/photo-1635805737707-575885ab0820?w=800&auto=format&fit=crop&q=80',
    synopsis: 'Peter Parker faces a brand new challenge as Spider-Man fights to protect New York City against an emerging villain syndicate.',
    showtimeId: 1
  },
  {
    id: 2,
    title: 'The Odyssey',
    category: '2D Atmos',
    status: 'Now Showing',
    rating: '9.4',
    votes: '8.7K',
    duration: '2h 18m',
    language: 'English',
    releaseDate: 'July 17, 2026',
    image: 'https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80',
    synopsis: 'An epic cinematic journey of courage and survival across mythical tempestuous seas.',
    showtimeId: 2
  },
  {
    id: 3,
    title: 'Fast X: Part II',
    category: '2D / IMAX',
    status: 'Coming Soon',
    rating: '9.1',
    votes: '5.4K',
    duration: '2h 22m',
    language: 'English',
    releaseDate: 'August 28, 2026',
    image: 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=800&auto=format&fit=crop&q=80',
    synopsis: 'The final high-octane installment of the saga accelerates towards an explosive climax.',
    showtimeId: 3
  },
  {
    id: 4,
    title: 'Avatar: Fire and Ash',
    category: '3D / IMAX 4K',
    status: 'Coming Soon',
    rating: '9.9',
    votes: '32.1K',
    duration: '3h 10m',
    language: 'English',
    releaseDate: 'December 18, 2026',
    image: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80',
    synopsis: 'Return to Pandora to uncover the aggressive Ash People of fire in uncharted lands.',
    showtimeId: 4
  }
])

const filteredMovies = computed(() => {
  return movies.value.filter(m => {
    const matchesTab = activeTab.value === 'View All Movies' || m.status === activeTab.value
    const matchesSearch = !searchQuery.value || m.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchesTab && matchesSearch
  })
})

onMounted(async () => {
  try {
    const res = await apiFetch('/movies')
    if (Array.isArray(res) && res.length > 0) {
      movies.value = res.map(m => ({
        ...m,
        rating: m.rating || '9.5',
        votes: m.votes || '10K',
        category: m.category || '2D / 3D',
        image: m.image || 'https://images.unsplash.com/photo-1635805737707-575885ab0820?w=800&auto=format&fit=crop&q=80'
      }))
    }
  } catch (err) {
    console.log('Using static movies catalog:', err.message)
  } finally {
    isLoading.value = false
  }
})

function goToBooking(movie, time = '1:05 PM') {
  router.push({
    path: `/booking/${movie.id}`,
    query: {
      location: selectedLocation.value,
      time: time,
      date: selectedDate.value,
      showtimeId: movie.showtimeId || movie.id
    }
  })
}
</script>

<template>
  <div class="min-h-screen bg-slate-950 text-slate-100 pb-24 font-sans antialiased">

    <!-- HERO SECTION WITH DARK GLASSMORPHISM & VIBRANT GRADIENT -->
    <section class="relative bg-gradient-to-b from-slate-900 via-slate-950 to-slate-950 border-b border-slate-800/80 pt-10 pb-16 px-4 sm:px-6 lg:px-8 overflow-hidden">
      <!-- Glow background circles -->
      <div class="absolute top-0 left-1/4 w-96 h-96 bg-indigo-600/15 blur-3xl rounded-full pointer-events-none"></div>
      <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-purple-600/15 blur-3xl rounded-full pointer-events-none"></div>

      <div class="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 items-center relative z-10">
        
        <!-- Left Hero Headline -->
        <div class="lg:col-span-7 space-y-6">
          <div class="inline-flex items-center gap-2.5 px-4 py-2 rounded-full bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 text-xs font-extrabold uppercase tracking-widest backdrop-blur-md">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
            <Sparkles class="w-4 h-4 text-indigo-400" />
            <span>Spider-Man Midnight Premiere Tickets Live</span>
          </div>

          <h1 class="text-4xl sm:text-6xl font-black tracking-tight leading-tight text-white">
            When Everyone Wants <br />
            <span class="bg-clip-text text-transparent bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400">
              The Exact Same Seat
            </span>
          </h1>

          <p class="text-slate-300 text-base sm:text-lg leading-relaxed max-w-xl font-normal">
            Experience ultra-fast cinema seat holds protected by real-time atomic locking. Zero double-booking, 60-second instant seat holds, and seamless payment callbacks.
          </p>

          <!-- Quick Filter Selector Bar -->
          <div class="p-4 bg-slate-900/90 border border-slate-800 rounded-2xl shadow-2xl backdrop-blur-xl grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1">Cinema Location</label>
              <select v-model="selectedLocation" class="w-full bg-slate-800 text-white text-xs font-bold px-3 py-2 rounded-xl border border-slate-700 focus:outline-none focus:border-indigo-500">
                <option>Sony Square, Mirpur, Dhaka</option>
                <option>Bali Arcade, Chattogram</option>
                <option>Bashundhara City, Dhaka</option>
                <option>SKS Tower, Mohakhali</option>
              </select>
            </div>
            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1">Show Date</label>
              <select v-model="selectedDate" class="w-full bg-slate-800 text-white text-xs font-bold px-3 py-2 rounded-xl border border-slate-700 focus:outline-none focus:border-indigo-500">
                <option>Today, Aug 8</option>
                <option>Tomorrow, Aug 9</option>
                <option>Sunday, Aug 10</option>
              </select>
            </div>
            <div class="flex items-end">
              <a href="#movies-catalog" class="w-full py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition-all shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2">
                <Ticket class="w-4 h-4" />
                <span>Select Seats</span>
              </a>
            </div>
          </div>

          <!-- Feature Badges -->
          <div class="flex flex-wrap items-center gap-6 pt-2 text-xs font-bold text-slate-400">
            <span class="flex items-center gap-2"><Zap class="w-4 h-4 text-amber-400" /> Atomic DB Locking</span>
            <span class="flex items-center gap-2"><ShieldCheck class="w-4 h-4 text-emerald-400" /> Webhook Idempotency</span>
            <span class="flex items-center gap-2"><Tv class="w-4 h-4 text-indigo-400" /> Live Visual Seat Sync</span>
          </div>
        </div>

        <!-- Right Featured Card Showcase -->
        <div class="lg:col-span-5 flex justify-center">
          <div class="relative w-full max-w-md bg-slate-900 rounded-3xl overflow-hidden shadow-2xl border border-slate-800 group hover:border-indigo-500/50 transition-all duration-300">
            <img 
              :src="movies[0]?.image" 
              :alt="movies[0]?.title" 
              class="w-full h-[420px] object-cover group-hover:scale-105 transition-transform duration-500"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent p-7 flex flex-col justify-end">
              <div class="flex items-center gap-2">
                <span class="text-[11px] font-black bg-indigo-600 text-white px-3 py-1 rounded-md uppercase tracking-wider">FEATURED PREMIERE</span>
                <span class="flex items-center gap-1 text-xs font-bold bg-amber-500/20 text-amber-300 px-2.5 py-1 rounded-md border border-amber-500/30">
                  <Star class="w-3.5 h-3.5 fill-amber-400 text-amber-400" /> 9.8 Rating
                </span>
              </div>
              <h3 class="text-2xl font-black text-white mt-2">{{ movies[0]?.title }}</h3>
              <p class="text-xs text-slate-300 mt-1 font-medium">{{ movies[0]?.category }} • 2h 35m • Now Showing</p>

              <!-- Quick Time Buttons -->
              <div class="mt-4 pt-4 border-t border-slate-800/80 space-y-2">
                <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Select Showtime:</p>
                <div class="flex flex-wrap gap-2">
                  <button 
                    v-for="time in showtimeSlots" 
                    :key="time"
                    @click="goToBooking(movies[0], time)"
                    class="px-3 py-1.5 bg-slate-800/90 hover:bg-indigo-600 text-white text-xs font-bold rounded-lg border border-slate-700 transition-all cursor-pointer"
                  >
                    {{ time }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- MOVIE CATALOG SECTION -->
    <section id="movies-catalog" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 space-y-10">
      
      <!-- Header & Tabs -->
      <div class="flex flex-wrap items-center justify-between gap-6 border-b border-slate-800 pb-6">
        <div>
          <h2 class="text-3xl font-black text-white tracking-tight">Movie Catalog</h2>
          <p class="text-xs text-slate-400 font-medium mt-1">Select your favorite movie and book guaranteed seats instantly.</p>
        </div>

        <div class="flex flex-wrap items-center gap-3">
          <button 
            v-for="tab in ['Now Showing', 'Coming Soon', 'View All Movies']" 
            :key="tab"
            @click="activeTab = tab"
            :class="[
              'px-5 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer border',
              activeTab === tab 
                ? 'bg-indigo-600 border-indigo-500 text-white shadow-lg shadow-indigo-600/30' 
                : 'bg-slate-900 border-slate-800 text-slate-400 hover:text-white hover:bg-slate-850'
            ]"
          >
            {{ tab }}
          </button>
        </div>
      </div>

      <!-- Movie Cards Grid -->
      <div v-if="isLoading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <div v-for="i in 4" :key="i" class="bg-slate-900 border border-slate-800 rounded-2xl h-[480px] animate-pulse"></div>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <div 
          v-for="m in filteredMovies" 
          :key="m.id"
          class="bg-slate-900 border border-slate-800/90 hover:border-indigo-500/50 rounded-2xl overflow-hidden shadow-xl hover:shadow-2xl transition-all duration-300 flex flex-col justify-between group"
        >
          <div>
            <div class="relative overflow-hidden h-96">
              <img :src="m.image" :alt="m.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
              
              <!-- Badges -->
              <div class="absolute top-3 left-3 flex flex-col gap-1.5">
                <span class="bg-slate-950/90 backdrop-blur-md text-indigo-400 border border-indigo-500/30 text-[10px] font-extrabold px-2.5 py-1 rounded-md uppercase tracking-wider">
                  {{ m.category }}
                </span>
              </div>

              <div class="absolute top-3 right-3 bg-amber-500/20 backdrop-blur-md text-amber-300 border border-amber-500/40 text-[11px] font-bold px-2.5 py-1 rounded-md flex items-center gap-1">
                <Star class="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                <span>{{ m.rating }}</span>
              </div>
            </div>

            <div class="p-5 space-y-2">
              <h3 class="text-lg font-bold text-white group-hover:text-indigo-400 transition-colors leading-snug">
                {{ m.title }}
              </h3>
              <p class="text-xs text-slate-400 line-clamp-2 leading-relaxed font-normal">{{ m.synopsis }}</p>
              <div class="flex items-center gap-3 text-[11px] text-slate-500 font-mono pt-1">
                <span>{{ m.duration }}</span>
                <span>•</span>
                <span>{{ m.language }}</span>
              </div>
            </div>
          </div>

          <!-- Bottom Action -->
          <div class="p-5 pt-0 space-y-3">
            <div v-if="m.status === 'Now Showing'" class="space-y-1.5">
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Showtimes:</p>
              <div class="grid grid-cols-3 gap-1.5">
                <button 
                  v-for="time in ['1:05 PM', '4:15 PM', '7:30 PM']" 
                  :key="time"
                  @click="goToBooking(m, time)"
                  class="py-1 bg-slate-800 hover:bg-indigo-600 text-white text-[11px] font-bold rounded-lg border border-slate-700 hover:border-indigo-500 transition-all cursor-pointer text-center"
                >
                  {{ time }}
                </button>
              </div>
            </div>

            <button 
              @click="goToBooking(m)"
              :class="[
                'w-full py-3 font-extrabold text-xs uppercase tracking-wider rounded-xl transition-all flex items-center justify-center gap-2 cursor-pointer shadow-md',
                m.status === 'Now Showing'
                  ? 'bg-indigo-600 hover:bg-indigo-500 text-white shadow-indigo-600/30'
                  : 'bg-slate-800 text-slate-500 border border-slate-700 cursor-not-allowed'
              ]"
              :disabled="m.status !== 'Now Showing'"
            >
              <Ticket class="w-4 h-4" />
              <span>{{ m.status === 'Now Showing' ? 'Book Tickets' : 'Release Soon' }}</span>
            </button>
          </div>

        </div>
      </div>

    </section>

    <!-- LIVE PLATFORM FEATURES -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div class="bg-gradient-to-r from-indigo-950 via-slate-900 to-purple-950 border border-indigo-800/50 rounded-3xl p-8 sm:p-12 shadow-2xl relative overflow-hidden">
        <div class="max-w-3xl space-y-6 relative z-10">
          <div class="inline-flex items-center gap-2 text-indigo-400 text-xs font-extrabold uppercase tracking-widest">
            <Zap class="w-4 h-4 text-amber-400" />
            <span>Architectural Concurrency Engine</span>
          </div>

          <h2 class="text-3xl sm:text-4xl font-black text-white tracking-tight leading-snug">
            Guaranteed No Double-Booking Engine
          </h2>

          <p class="text-slate-300 text-sm sm:text-base leading-relaxed">
            Our platform isolates the atomic seat hold engine using Spring Boot microservices and SQL pessimistic locking (<code class="text-amber-400 bg-slate-900 px-1.5 py-0.5 rounded font-mono text-xs">SELECT FOR UPDATE</code>). When hundreds of moviegoers click the same seat simultaneously, exactly one user receives the hold.
          </p>

          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2 text-xs font-bold">
            <div class="p-4 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
              <p class="text-indigo-400 font-extrabold text-base">Pessimistic DB Locking</p>
              <p class="text-slate-400 font-normal">Prevents race conditions on high concurrency</p>
            </div>
            <div class="p-4 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
              <p class="text-emerald-400 font-extrabold text-base">Idempotent Callbacks</p>
              <p class="text-slate-400 font-normal">Deduplicates gateway webhook events</p>
            </div>
            <div class="p-4 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
              <p class="text-amber-400 font-extrabold text-base">60s Hold Expiration</p>
              <p class="text-slate-400 font-normal">Auto-releases unpurchased seats back to map</p>
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>
