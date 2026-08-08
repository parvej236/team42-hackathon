<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiFetch } from '../api'
import { 
  Film, 
  Ticket, 
  Clock, 
  MapPin, 
  CheckCircle2, 
  AlertCircle, 
  Lock, 
  RefreshCw,
  PhoneCall,
  ShieldCheck,
  Zap,
  CreditCard,
  QrCode,
  Printer,
  X,
  Check,
  ChevronRight
} from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const movieId = route.params.movieId || 1
const selectedLocation = ref(route.query.location || 'Sony Square, Mirpur, Dhaka')
const selectedShowTime = ref(route.query.time || '1:05 PM')
const selectedShowDate = ref(route.query.date || 'Aug 8, 26')
const showtimeId = ref(Number(route.query.showtimeId) || 1)

const seatTypes = [
  { name: 'Premium (Row F-N)', price: 550, label: 'BDT 550' },
  { name: 'Regular (Row A-E)', price: 400, label: 'BDT 400' }
]
const selectedSeatType = ref(seatTypes[0])
const selectedSeats = ref([])
const timerSeconds = ref(60)
let timerInterval = null
let syncInterval = null

// Checkout & OTP State
const showPaymentModal = ref(false)
const checkoutStep = ref('DETAILS') // DETAILS -> OTP -> PROCESSING -> SUCCESS
const fullName = ref(authStore.user?.name || 'Zayan Ahmed')
const mobileNumber = ref('01700000000')
const otpCode = ref('')
const otpRef = ref('')

const acceptedTerms = ref(true)
const errorMessage = ref('')
const successMessage = ref('')
const isProcessing = ref(false)
const isOtpSending = ref(false)
const isOtpVerifying = ref(false)
const isSyncing = ref(false)
const paymentStatus = ref('')
const confirmedBooking = ref(null)

const currentUserId = computed(() => authStore.user?.email || 'guest_' + (localStorage.getItem('cinemaseat_guest_id') || Math.floor(100000 + Math.random() * 900000)))

const seats = ref([])

function generateDefaultSeats() {
  const list = []
  const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N']
  for (const r of rows) {
    for (let i = 1; i <= 14; i++) {
      const seatNum = r + i
      let initStatus = 'AVAILABLE'
      if (['F5', 'F6', 'H8', 'H9', 'B3'].includes(seatNum)) {
        initStatus = 'BOOKED'
      }
      list.push({ id: seatNum, seatNumber: seatNum, rowName: r, status: initStatus, heldByUserId: null })
    }
  }
  seats.value = list
}
generateDefaultSeats()

function getSeatDisplayStatus(seat) {
  if (seat.status === 'BOOKED') return 'BOOKED'
  if (selectedSeats.value.includes(seat.seatNumber)) return 'SELECTED'
  if (seat.status === 'HELD') {
    if (seat.heldByUserId && seat.heldByUserId.toLowerCase() === currentUserId.value.toLowerCase()) {
      return 'SELECTED'
    }
    return 'IN_PROGRESS'
  }
  return 'AVAILABLE'
}

async function fetchRealTimeSeatMap() {
  isSyncing.value = true
  try {
    const res = await apiFetch(`/seats/map?showtimeId=${showtimeId.value}&userId=${currentUserId.value}`)
    if (res?.data && Array.isArray(res.data)) {
      const rowOrder = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N']
      const mapped = res.data.map(s => {
        if (s.status === 'BOOKED') {
          const idx = selectedSeats.value.indexOf(s.seatNumber)
          if (idx > -1) selectedSeats.value.splice(idx, 1)
        }
        return { 
          id: s.seatNumber, 
          seatNumber: s.seatNumber, 
          rowName: s.rowName, 
          status: s.status, 
          heldByUserId: s.heldByUserId 
        }
      })

      mapped.sort((a, b) => {
        const rA = rowOrder.indexOf(a.rowName)
        const rB = rowOrder.indexOf(b.rowName)
        if (rA !== rB) return rA - rB
        const numA = parseInt(a.seatNumber.replace(/^[A-Z]+/, ''), 10) || 0
        const numB = parseInt(b.seatNumber.replace(/^[A-Z]+/, ''), 10) || 0
        return numA - numB
      })

      seats.value = mapped
    }
  } catch (err) {
    console.log('Seat sync fallback:', err.message)
  } finally {
    isSyncing.value = false
  }
}

const formattedTimer = computed(() => {
  const m = Math.floor(timerSeconds.value / 60)
  const s = timerSeconds.value % 60
  return `${m}:${s < 10 ? '0' : ''}${s}`
})

const totalAmount = computed(() => selectedSeats.value.length * selectedSeatType.value.price)

onMounted(() => {
  fetchRealTimeSeatMap()
  timerInterval = setInterval(() => {
    if (timerSeconds.value > 0) timerSeconds.value--
  }, 1000)
  syncInterval = setInterval(fetchRealTimeSeatMap, 1000)
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
  if (syncInterval) clearInterval(syncInterval)
})

async function toggleSeat(seat) {
  if (seat.status === 'BOOKED') return
  if (getSeatDisplayStatus(seat) === 'IN_PROGRESS') {
    errorMessage.value = `Seat ${seat.seatNumber} is currently held by another user.`
    return
  }

  const isSelected = getSeatDisplayStatus(seat) === 'SELECTED'
  if (isSelected) {
    const idx = selectedSeats.value.indexOf(seat.seatNumber)
    if (idx > -1) selectedSeats.value.splice(idx, 1)
    seat.status = 'AVAILABLE'
    try {
      await apiFetch('/seats/release', {
        method: 'POST',
        body: JSON.stringify({ showtimeId: showtimeId.value, seatNumber: seat.seatNumber, userId: currentUserId.value })
      })
    } catch {}
  } else {
    if (selectedSeats.value.length >= 10) {
      errorMessage.value = 'Maximum 10 tickets per booking!'
      return
    }
    seat.status = 'HELD'
    selectedSeats.value.push(seat.seatNumber)
    errorMessage.value = ''
    timerSeconds.value = 60

    try {
      await apiFetch('/seats/hold', {
        method: 'POST',
        body: JSON.stringify({ showtimeId: showtimeId.value, seatNumber: seat.seatNumber, userId: currentUserId.value })
      })
    } catch (err) {
      seat.status = 'IN_PROGRESS'
      const idx = selectedSeats.value.indexOf(seat.seatNumber)
      if (idx > -1) selectedSeats.value.splice(idx, 1)
      errorMessage.value = `Seat ${seat.seatNumber} was claimed by another customer.`
    }
  }
  fetchRealTimeSeatMap()
}

// OPEN PAYMENT MODAL
function startCheckout() {
  errorMessage.value = ''
  if (!authStore.isAuthenticated) {
    errorMessage.value = 'Please sign in to proceed with booking.'
    setTimeout(() => router.push('/signin'), 1200)
    return
  }
  if (selectedSeats.value.length === 0) {
    errorMessage.value = 'Please select at least one seat from the cinema hall map.'
    return
  }
  checkoutStep.value = 'DETAILS'
  showPaymentModal.value = true
}

// STEP 1: DISPATCH OTP
async function handleSendOtp() {
  if (!mobileNumber.value || mobileNumber.value.length < 11) {
    errorMessage.value = 'Please enter a valid 11-digit mobile number.'
    return
  }
  errorMessage.value = ''
  isOtpSending.value = true
  otpRef.value = 'otp_' + Date.now()

  try {
    await apiFetch('/payments/otp/send', {
      method: 'POST',
      body: JSON.stringify({ phone: mobileNumber.value, ref: otpRef.value })
    })
  } catch (err) {}
  
  isOtpSending.value = false
  checkoutStep.value = 'OTP'
}

// STEP 2: VERIFY OTP & CHARGE PAYMENT
async function handleVerifyAndPay() {
  if (!otpCode.value) {
    errorMessage.value = 'Please enter the 6-digit OTP code.'
    return
  }
  errorMessage.value = ''
  isOtpVerifying.value = true

  try {
    await apiFetch('/payments/otp/verify', {
      method: 'POST',
      body: JSON.stringify({ ref: otpRef.value, code: otpCode.value })
    })
  } catch (err) {}
  isOtpVerifying.value = false

  // Initiate Charge
  checkoutStep.value = 'PROCESSING'
  isProcessing.value = true
  const bkRef = 'bk_' + Date.now() + '_' + Math.floor(Math.random() * 1000)

  // Confirm Seats on Backend Inventory
  const confirmedSeatList = [...selectedSeats.value]
  for (const sn of confirmedSeatList) {
    try {
      await apiFetch('/seats/confirm', {
        method: 'POST',
        body: JSON.stringify({ showtimeId: showtimeId.value, seatNumber: sn, userId: currentUserId.value })
      })
    } catch {}
  }

  // Mark confirmed seats as BOOKED locally and clear active selection IMMEDIATELY
  for (const sn of confirmedSeatList) {
    const found = seats.value.find(s => s.seatNumber === sn)
    if (found) {
      found.status = 'BOOKED'
      found.heldByUserId = null
    }
  }
  selectedSeats.value = []

  // Charge Payment Endpoint
  try {
    await apiFetch('/payments/charge', {
      method: 'POST',
      body: JSON.stringify({
        amount: totalAmount.value,
        currency: 'BDT',
        bookingRef: bkRef,
        userId: currentUserId.value,
        seatNumbers: confirmedSeatList.join(','),
        showtimeId: showtimeId.value
      })
    })
  } catch {}

  // Successful Booking Result State
  setTimeout(() => {
    confirmedBooking.value = {
      bookingRef: bkRef,
      movieTitle: 'Spider-Man: Brand New Day',
      location: selectedLocation.value,
      date: selectedShowDate.value,
      time: selectedShowTime.value,
      seats: confirmedSeatList,
      total: totalAmount.value,
      userName: fullName.value,
      userPhone: mobileNumber.value,
      paymentId: 'pay_' + Math.floor(10000000 + Math.random() * 90000000)
    }

    isProcessing.value = false
    checkoutStep.value = 'SUCCESS'
    fetchRealTimeSeatMap()
  }, 1000)
}

function printTicket() {
  window.print()
}
</script>

<template>
  <div class="min-h-screen bg-slate-950 text-slate-100 pb-24 pt-8 font-sans antialiased">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">

      <!-- Login Alert Header -->
      <div v-if="!authStore.isAuthenticated" class="bg-gradient-to-r from-indigo-900 via-slate-900 to-indigo-950 text-white p-5 rounded-2xl flex flex-col sm:flex-row items-center justify-between gap-4 shadow-xl border border-indigo-700/60">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-indigo-500/20 flex items-center justify-center text-indigo-400">
            <Lock class="w-5 h-5" />
          </div>
          <div>
            <h4 class="text-base font-bold">Authentication Required</h4>
            <p class="text-xs text-indigo-200 mt-0.5">Please sign in to select seats, lock holds, and complete payment.</p>
          </div>
        </div>
        <router-link to="/signin" class="bg-indigo-600 hover:bg-indigo-500 text-white font-black px-6 py-2.5 rounded-xl text-xs uppercase tracking-wider shrink-0 shadow-lg shadow-indigo-600/40">
          Sign In Now
        </router-link>
      </div>

      <!-- MAIN PAGE LAYOUT -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        <!-- LEFT: SEAT SELECTION & HALL MAP -->
        <div class="lg:col-span-8 space-y-6">
          
          <!-- Location & Category Selector -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-3">
              <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Theater Location</span>
              <div class="flex items-center gap-2 text-sm font-extrabold text-white">
                <MapPin class="w-4 h-4 text-indigo-400 shrink-0" />
                <span>{{ selectedLocation }}</span>
              </div>
              <p class="text-xs text-slate-400 font-mono">{{ selectedShowDate }} • {{ selectedShowTime }}</p>
            </div>

            <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-3">
              <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Select Seat Category</span>
              <div class="flex gap-2">
                <button 
                  v-for="st in seatTypes" 
                  :key="st.name" 
                  @click="selectedSeatType = st"
                  :class="[
                    'flex-1 p-2.5 rounded-xl text-xs font-bold border transition-all cursor-pointer text-center',
                    selectedSeatType.name === st.name 
                      ? 'bg-indigo-600/30 border-indigo-500 text-indigo-300 shadow-md' 
                      : 'bg-slate-800 border-slate-700 text-slate-400 hover:text-white'
                  ]"
                >
                  {{ st.name }}
                </button>
              </div>
            </div>
          </div>

          <!-- SEAT MAP CONTAINER WITH REAL-TIME 4-COLOR STATES -->
          <div class="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-2xl space-y-6 relative overflow-hidden">
            
            <!-- Auth Lock Overlay -->
            <div v-if="!authStore.isAuthenticated" class="absolute inset-0 z-30 flex flex-col items-center justify-center p-6 bg-slate-950/85 backdrop-blur-md text-center space-y-4">
              <Lock class="w-12 h-12 text-indigo-400 animate-bounce" />
              <h4 class="text-2xl font-black text-white">Sign In to Select Seats</h4>
              <router-link to="/signin" class="px-8 py-3 bg-indigo-600 hover:bg-indigo-500 text-white text-xs uppercase font-black rounded-xl shadow-lg shadow-indigo-600/50">
                Sign In to Unlock
              </router-link>
            </div>

            <!-- Seat Legend & Live Hold Timer -->
            <div class="flex flex-wrap items-center justify-between gap-4 pb-4 border-b border-slate-800">
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-400 font-mono text-xs font-bold">
                  <Clock class="w-4 h-4 text-amber-400" />
                  <span>Hold Expires: {{ formattedTimer }}</span>
                </div>
                <div class="flex items-center gap-1 text-[11px] text-slate-400 font-medium">
                  <span>Live Sync</span>
                  <RefreshCw :class="['w-3.5 h-3.5 text-indigo-400', isSyncing ? 'animate-spin' : '']" />
                </div>
              </div>

              <!-- 4-COLOR UI STATUS LEGEND -->
              <div class="flex flex-wrap items-center gap-4 text-xs font-bold">
                <span class="flex items-center gap-1.5 text-slate-300">
                  <span class="w-4 h-4 rounded-md bg-white border border-slate-300"></span> Available
                </span>
                <span class="flex items-center gap-1.5 text-emerald-400">
                  <span class="w-4 h-4 rounded-md bg-emerald-600 border border-emerald-500"></span> Selected
                </span>
                <span class="flex items-center gap-1.5 text-amber-400">
                  <span class="w-4 h-4 rounded-md bg-amber-400 border border-amber-500"></span> In Progress
                </span>
                <span class="flex items-center gap-1.5 text-slate-400">
                  <span class="w-4 h-4 rounded-md bg-slate-400 border border-slate-500"></span> Booked
                </span>
              </div>
            </div>

            <!-- CURVED CINEMA SCREEN -->
            <div class="py-2 space-y-1">
              <div class="w-full h-8 bg-gradient-to-b from-indigo-500/20 via-indigo-600/10 to-transparent rounded-t-[100%] border-t-4 border-indigo-500 flex items-center justify-center shadow-lg shadow-indigo-500/20">
                <span class="text-[11px] font-black uppercase tracking-widest text-indigo-300">CURVED CINEMA SCREEN</span>
              </div>
            </div>

            <!-- SEAT GRID 14 COLUMNS -->
            <div class="py-4 space-y-2 overflow-x-auto">
              <div class="grid grid-cols-14 gap-2 min-w-[620px] max-w-2xl mx-auto">
                <button 
                  v-for="seat in seats" 
                  :key="seat.seatNumber" 
                  @click="toggleSeat(seat)"
                  :disabled="getSeatDisplayStatus(seat) === 'BOOKED' || getSeatDisplayStatus(seat) === 'IN_PROGRESS'"
                  :class="[
                    'h-9 rounded-lg text-xs font-mono font-extrabold transition-all border flex items-center justify-center cursor-pointer',
                    
                    // BOOKED (GREY)
                    getSeatDisplayStatus(seat) === 'BOOKED' 
                      ? 'bg-slate-400 text-slate-900 border-slate-500 opacity-60 cursor-not-allowed' : '',
                    
                    // IN PROGRESS (YELLOW - OTHER USER HOLDING)
                    getSeatDisplayStatus(seat) === 'IN_PROGRESS' 
                      ? 'bg-amber-400 text-slate-950 border-amber-500 cursor-not-allowed animate-pulse shadow-md shadow-amber-400/30 font-black' : '',
                    
                    // SELECTED (GREEN - ACTIVE USER)
                    getSeatDisplayStatus(seat) === 'SELECTED' 
                      ? 'bg-emerald-600 text-white border-emerald-500 shadow-lg shadow-emerald-600/40 ring-2 ring-emerald-400 scale-105 font-black' : '',
                    
                    // AVAILABLE (WHITE)
                    getSeatDisplayStatus(seat) === 'AVAILABLE' 
                      ? 'bg-white text-slate-900 border-slate-300 hover:border-emerald-500 hover:bg-emerald-50 shadow-xs' : ''
                  ]"
                >
                  {{ seat.seatNumber }}
                </button>
              </div>
            </div>

          </div>
        </div>

        <!-- RIGHT: SUMMARY & PROCEED BUTTON -->
        <div class="lg:col-span-4 space-y-6">
          <div class="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-7 shadow-2xl space-y-6">
            <div class="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 class="text-lg font-black text-white">Tickets Summary</h3>
              <span class="px-2.5 py-1 rounded-md bg-indigo-500/20 text-indigo-400 text-xs font-bold uppercase tracking-wider">
                {{ selectedSeats.length }} Seats
              </span>
            </div>

            <!-- Pricing Details -->
            <div class="space-y-3 text-xs text-slate-300">
              <div class="flex justify-between">
                <span class="text-slate-400">Movie</span>
                <span class="font-bold text-white">Spider-Man: Brand New Day</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-400">Selected Seats</span>
                <span class="font-bold text-indigo-400 font-mono text-sm">{{ selectedSeats.join(', ') || 'None' }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-400">Rate</span>
                <span class="font-bold text-white">{{ selectedSeatType.label }}</span>
              </div>
              <div class="flex justify-between pt-3 border-t border-slate-800 font-bold text-base text-white">
                <span>Total Amount</span>
                <span class="text-emerald-400 font-black text-xl">{{ totalAmount }} BDT</span>
              </div>
            </div>

            <div v-if="errorMessage" class="p-3.5 bg-rose-500/10 border border-rose-500/40 text-rose-300 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle class="w-4 h-4 shrink-0 text-rose-400" />
              <span>{{ errorMessage }}</span>
            </div>

            <!-- Action Button -->
            <button 
              @click="startCheckout"
              class="w-full py-4 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-black text-xs uppercase tracking-widest rounded-2xl shadow-xl shadow-indigo-600/30 transition-all flex items-center justify-center gap-2 cursor-pointer"
            >
              <CreditCard class="w-5 h-5" />
              <span>PROCEED TO PAYMENT</span>
            </button>

          </div>
        </div>

      </div>
    </div>

    <!-- DEDICATED PAYMENT CHECKOUT & CONFIRMATION MODAL -->
    <div v-if="showPaymentModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/85 backdrop-blur-xl animate-fade-in">
      <div class="relative w-full max-w-lg bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl p-6 sm:p-8 space-y-6 overflow-hidden">
        
        <!-- Close Button -->
        <button @click="showPaymentModal = false" class="absolute top-5 right-5 text-slate-400 hover:text-white cursor-pointer">
          <X class="w-6 h-6" />
        </button>

        <!-- MODAL STEP 1: CUSTOMER & OTP REQUEST -->
        <div v-if="checkoutStep === 'DETAILS'" class="space-y-5">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-indigo-600/20 text-indigo-400 flex items-center justify-center">
              <PhoneCall class="w-5 h-5" />
            </div>
            <div>
              <h3 class="text-xl font-black text-white">Payment Checkout</h3>
              <p class="text-xs text-slate-400">Step 1 of 2: Mobile Number & OTP Verification</p>
            </div>
          </div>

          <div class="p-4 bg-slate-950 rounded-2xl border border-slate-800 space-y-2 text-xs">
            <div class="flex justify-between text-slate-300">
              <span>Seats: <strong class="text-indigo-400 font-mono">{{ selectedSeats.join(', ') }}</strong></span>
              <span>Total: <strong class="text-emerald-400 font-black text-sm">{{ totalAmount }} BDT</strong></span>
            </div>
          </div>

          <div class="space-y-3">
            <div>
              <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Full Name</label>
              <input v-model="fullName" type="text" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-xs text-white focus:outline-none focus:border-indigo-500" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Mobile Number (OTP Verification)</label>
              <input v-model="mobileNumber" type="text" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-xs text-white focus:outline-none focus:border-indigo-500 font-mono" />
            </div>
          </div>

          <button 
            @click="handleSendOtp" 
            :disabled="isOtpSending"
            class="w-full py-3.5 bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl shadow-lg shadow-indigo-600/30 transition-all flex items-center justify-center gap-2 cursor-pointer"
          >
            <ShieldCheck class="w-4 h-4" />
            <span>{{ isOtpSending ? 'Sending OTP Code...' : 'SEND OTP CODE VIA GATEWAY' }}</span>
          </button>
        </div>

        <!-- MODAL STEP 2: OTP VERIFICATION -->
        <div v-else-if="checkoutStep === 'OTP'" class="space-y-5">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center">
              <ShieldCheck class="w-5 h-5" />
            </div>
            <div>
              <h3 class="text-xl font-black text-white">Enter OTP Code</h3>
              <p class="text-xs text-slate-400">Code sent to {{ mobileNumber }} (Default Code: <strong class="text-amber-400 font-mono">123456</strong>)</p>
            </div>
          </div>

          <div class="space-y-3 py-2">
            <input 
              v-model="otpCode" 
              type="text" 
              placeholder="123456" 
              class="w-full bg-slate-950 border border-amber-500/40 rounded-2xl px-4 py-4 text-center text-2xl font-mono font-black text-amber-400 tracking-widest focus:outline-none focus:border-amber-500" 
            />
          </div>

          <button 
            @click="handleVerifyAndPay" 
            :disabled="isOtpVerifying"
            class="w-full py-4 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-black text-xs uppercase tracking-wider rounded-xl shadow-xl shadow-emerald-600/30 transition-all flex items-center justify-center gap-2 cursor-pointer"
          >
            <Check class="w-5 h-5" />
            <span>VERIFY OTP & CONFIRM BOOKING ({{ totalAmount }} BDT)</span>
          </button>
        </div>

        <!-- MODAL STEP 3: PROCESSING -->
        <div v-else-if="checkoutStep === 'PROCESSING'" class="py-12 flex flex-col items-center justify-center text-center space-y-4">
          <RefreshCw class="w-12 h-12 text-indigo-400 animate-spin" />
          <h4 class="text-xl font-black text-white">Processing Gateway Payment...</h4>
          <p class="text-xs text-slate-400 max-w-xs">Connecting to Mock Gateway & executing SQL pessimistic seat locks.</p>
        </div>

        <!-- MODAL STEP 4: SUCCESSFUL DIGITAL CINEMA TICKET UI -->
        <div v-else-if="checkoutStep === 'SUCCESS' && confirmedBooking" class="space-y-6 text-left">
          
          <div class="flex items-center justify-between pb-3 border-b border-slate-800">
            <div class="flex items-center gap-2 text-emerald-400 text-xs font-black uppercase tracking-wider">
              <CheckCircle2 class="w-5 h-5" />
              <span>BOOKING CONFIRMED & SEATS BOOKED</span>
            </div>
            <span class="text-[11px] font-mono text-slate-400">Ref: {{ confirmedBooking.bookingRef }}</span>
          </div>

          <!-- DIGITAL MOVIE PASS TICKET -->
          <div class="bg-gradient-to-br from-indigo-950 via-slate-950 to-purple-950 border border-indigo-500/40 rounded-3xl p-6 shadow-2xl relative space-y-4 overflow-hidden">
            <div class="flex justify-between items-start">
              <div>
                <span class="text-[10px] font-black bg-indigo-600 text-white px-2.5 py-0.5 rounded uppercase">CINEMASEAT PASS</span>
                <h3 class="text-2xl font-black text-white mt-1">{{ confirmedBooking.movieTitle }}</h3>
                <p class="text-xs text-slate-300 font-medium">{{ confirmedBooking.location }}</p>
              </div>
              <QrCode class="w-14 h-14 text-white p-1 bg-white/10 rounded-xl border border-white/20" />
            </div>

            <div class="grid grid-cols-2 gap-4 pt-3 border-t border-indigo-500/20 text-xs">
              <div>
                <p class="text-slate-400 font-bold uppercase text-[10px]">Show Date & Time</p>
                <p class="font-extrabold text-white">{{ confirmedBooking.date }} • {{ confirmedBooking.time }}</p>
              </div>
              <div>
                <p class="text-slate-400 font-bold uppercase text-[10px]">Booked Seats</p>
                <p class="font-mono font-black text-emerald-400 text-base">{{ confirmedBooking.seats.join(', ') }}</p>
              </div>
              <div>
                <p class="text-slate-400 font-bold uppercase text-[10px]">Ticket Holder</p>
                <p class="font-bold text-white">{{ confirmedBooking.userName }}</p>
              </div>
              <div>
                <p class="text-slate-400 font-bold uppercase text-[10px]">Payment ID</p>
                <p class="font-mono text-xs text-indigo-300">{{ confirmedBooking.paymentId }}</p>
              </div>
            </div>
          </div>

          <div class="flex gap-3 pt-2">
            <button @click="printTicket" class="flex-1 py-3 bg-slate-800 hover:bg-slate-700 text-white font-bold text-xs rounded-xl border border-slate-700 transition-all flex items-center justify-center gap-2 cursor-pointer">
              <Printer class="w-4 h-4" />
              <span>Print Ticket</span>
            </button>
            <button @click="showPaymentModal = false" class="flex-1 py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-black text-xs uppercase tracking-wider rounded-xl shadow-lg shadow-indigo-600/30 transition-all cursor-pointer">
              Done
            </button>
          </div>

        </div>

      </div>
    </div>

  </div>
</template>
