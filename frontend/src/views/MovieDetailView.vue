<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiFetch } from '../api'
import {
  Film,
  Calendar,
  Clock,
  MapPin,
  Star,
  Play,
  Ticket,
  ArrowLeft,
  ChevronRight
} from '@lucide/vue'

const route = useRoute()
const router = useRouter()

const movieId = route.params.id || 1
const selectedLocation = ref('Sony Square, Mirpur, Dhaka')
const isLoading = ref(true)

const locations = [
  'Sony Square, Mirpur, Dhaka',
  'Bali Arcade, Chattogram',
  'Bashundhara City, Dhaka',
  'SKS Tower, Mohakhali, Dhaka'
]

const movie = ref({
  id: 1,
  title: 'Spider-Man: Brand New Day (2D)',
  category: '2D',
  actor: 'TOM HOLLAND, ZENDAYA, SADIE SINK, LIZA COLÓN, JACOB BATALON',
  genre: 'Action, Adventure, Sci-Fi',
  releaseDate: '30-07-2026',
  language: 'English',
  duration: '2h 24m',
  image: 'https://images.unsplash.com/photo-1635805737707-575885ab0820?w=600&auto=format&fit=crop&q=80',
  synopsis: 'Four years have gone by since we last caught up with our friendly neighborhood hero.'
})

const showtimes = ref([])

// Group showtimes by date for display
const groupedShowtimes = computed(() => {
  const grouped = {}
  const filtered = showtimes.value.filter(s => 
    s.theatre === selectedLocation.value
  )
  
  for (const st of filtered) {
    const key = st.showDate
    if (!grouped[key]) {
      grouped[key] = {
        day: st.dayOfWeek,
        date: st.showDate,
        times: [],
        showtimeIds: {}
      }
    }
    if (!grouped[key].times.includes(st.showTime)) {
      grouped[key].times.push(st.showTime)
      grouped[key].showtimeIds[st.showTime] = st.id
    }
  }
  return Object.values(grouped)
})

// Fallback dates if API not available
const fallbackDates = [
  { day: 'Saturday', date: '8th, August 2026', times: ['11:00 AM', '02:00 PM', '04:45 PM', '07:45 PM'] },
  { day: 'Sunday', date: '9th, August 2026', times: ['10:50 AM', '01:50 PM', '04:45 PM', '07:45 PM'] },
  { day: 'Monday', date: '10th, August 2026', times: ['10:50 AM', '01:50 PM', '04:45 PM', '07:45 PM'] },
]

const displayDates = computed(() => {
  return groupedShowtimes.value.length > 0 ? groupedShowtimes.value : fallbackDates
})

onMounted(async () => {
  try {
    // Fetch movie details
    const movieRes = await apiFetch(`/movies/${movieId}`)
    if (movieRes && movieRes.title) {
      movie.value = { ...movie.value, ...movieRes }
    }
  } catch (err) {
    console.log('Using default movie details:', err.message)
  }

  try {
    // Fetch showtimes for this movie
    const stRes = await apiFetch(`/movies/${movieId}/showtimes`)
    if (Array.isArray(stRes) && stRes.length > 0) {
      showtimes.value = stRes
    }
  } catch (err) {
    console.log('Using fallback showtimes:', err.message)
  }

  isLoading.value = false
})

function goToBooking(time, dateStr, showtimeId) {
  // Find the actual showtime ID for the selected time/date/location
  const matchingShowtime = showtimes.value.find(s =>
    s.showTime === time && s.showDate === dateStr && s.theatre === selectedLocation.value
  )
  const stId = showtimeId || matchingShowtime?.id || 1

  router.push({
    path: `/booking/${movie.value.id}`,
    query: {
      location: selectedLocation.value,
      time,
      date: dateStr,
      showtimeId: stId
    }
  })
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900 pb-20">
    
    <!-- Top Navigation -->
    <div class="bg-white border-b border-slate-200 py-4">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between text-sm text-slate-600">
        <button @click="router.back()" class="flex items-center gap-2 hover:text-indigo-600 font-bold transition-colors cursor-pointer">
          <ArrowLeft class="w-4 h-4" />
          <span>Back to Movies</span>
        </button>
        <div class="flex items-center gap-2">
          <router-link to="/" class="hover:text-indigo-600">Home</router-link>
          <ChevronRight class="w-4 h-4 text-slate-400" />
          <span class="text-slate-900 font-bold">{{ movie.title }}</span>
        </div>
      </div>
    </div>

    <!-- Movie Details Section -->
    <section class="bg-white border-b border-slate-200 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-12 gap-10 items-start">
        
        <!-- Poster Card Left -->
        <div class="md:col-span-4 lg:col-span-3 flex justify-center">
          <div class="rounded-2xl overflow-hidden shadow-md border border-slate-200 max-w-xs">
            <img :src="movie.image" :alt="movie.title" class="w-full h-100 object-cover" />
          </div>
        </div>

        <!-- Movie Info Right -->
        <div class="md:col-span-8 lg:col-span-9 space-y-6">
          <div>
            <span class="text-xs font-bold bg-indigo-50 text-indigo-700 px-3 py-1 rounded-md border border-indigo-100 uppercase">
              {{ movie.category }}
            </span>
            <h1 class="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight mt-3">
              {{ movie.title }}
            </h1>
          </div>

          <!-- Metadata Table -->
          <div class="space-y-3 text-sm text-slate-700 border-t border-b border-slate-200 py-5 max-w-2xl">
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Category</span>
              <span class="col-span-3 font-medium">: {{ movie.category }}</span>
            </div>
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Actors</span>
              <span class="col-span-3 font-medium">: {{ movie.actor }}</span>
            </div>
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Genre</span>
              <span class="col-span-3 font-medium">: {{ movie.genre }}</span>
            </div>
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Release Date</span>
              <span class="col-span-3 font-medium">: {{ movie.releaseDate }}</span>
            </div>
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Language</span>
              <span class="col-span-3 font-medium">: {{ movie.language }}</span>
            </div>
            <div class="grid grid-cols-4 gap-2">
              <span class="font-bold text-slate-500">Duration</span>
              <span class="col-span-3 font-medium">: {{ movie.duration }}</span>
            </div>
          </div>

          <!-- Synopsis -->
          <div class="space-y-2 max-w-3xl">
            <h3 class="text-sm font-bold text-slate-900 uppercase tracking-wider">Synopsis</h3>
            <p class="text-sm text-slate-600 leading-relaxed">{{ movie.synopsis }}</p>
          </div>
        </div>

      </div>
    </section>

    <!-- SHOWTIME SELECTION SECTION -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-8">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 class="text-xl font-bold text-slate-900 uppercase tracking-wider border-l-4 border-indigo-600 pl-3">
            Available Showtimes
          </h2>
          <p class="text-sm text-slate-500 mt-1">Location: <strong>{{ selectedLocation }}</strong></p>
        </div>

        <div class="flex items-center gap-3">
          <label class="text-sm text-slate-700 font-bold">Select Theater Location:</label>
          <select v-model="selectedLocation" class="bg-white border border-slate-300 rounded-xl px-4 py-2 text-sm font-semibold text-slate-800 focus:outline-none focus:border-indigo-600">
            <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-5">
        <div v-for="i in 6" :key="i" class="bg-white border border-slate-200 rounded-2xl p-5 animate-pulse space-y-3">
          <div class="h-4 bg-slate-200 rounded w-2/3 mx-auto"></div>
          <div class="h-3 bg-slate-200 rounded w-1/2 mx-auto"></div>
          <div class="space-y-2 mt-4">
            <div v-for="j in 3" :key="j" class="h-8 bg-slate-200 rounded"></div>
          </div>
        </div>
      </div>

      <!-- Days Grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-5">
        <div 
          v-for="d in displayDates" 
          :key="d.date" 
          class="bg-white border border-slate-200 rounded-2xl p-5 space-y-4 shadow-xs text-center flex flex-col justify-between"
        >
          <div class="border-b border-slate-100 pb-3">
            <p class="text-sm font-bold text-slate-900">{{ d.day }}</p>
            <p class="text-xs text-slate-500 font-mono mt-0.5">{{ d.date }}</p>
          </div>

          <div class="space-y-2">
            <button 
              v-for="t in d.times" 
              :key="t"
              @click="goToBooking(t, d.date, d.showtimeIds?.[t])"
              class="w-full py-2 bg-slate-100 hover:bg-indigo-50 hover:text-indigo-600 text-slate-800 font-bold text-xs rounded-lg transition-all cursor-pointer"
            >
              {{ t }}
            </button>
          </div>

          <button 
            @click="goToBooking(d.times[0], d.date, d.showtimeIds?.[d.times[0]])"
            class="w-full py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs uppercase tracking-wider rounded-xl shadow-xs transition-all mt-2 cursor-pointer"
          >
            Get Tickets
          </button>
        </div>
      </div>
    </section>

  </div>
</template>
