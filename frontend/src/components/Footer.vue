<script setup>
import { Film, MapPin, Phone, Mail } from '@lucide/vue'
</script>

<template>
  <footer class="bg-white border-t border-slate-200 text-slate-600 pt-12 pb-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
      
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 text-xs">
        
        <!-- Column 1: Brand -->
        <div class="space-y-3">
          <div class="flex items-center gap-2">
            <Film class="w-6 h-6 text-indigo-600" />
            <span class="text-base font-extrabold text-slate-900 uppercase tracking-wider">CinemaSeat</span>
          </div>
          <p class="text-slate-500 leading-relaxed">
            The modern microservices-powered cinema ticketing platform built for zero-oversell seat holding.
          </p>
        </div>

        <!-- Column 2: Navigation -->
        <div class="space-y-3">
          <h4 class="text-xs font-bold text-slate-900 uppercase tracking-wider">Navigation</h4>
          <ul class="space-y-2 font-medium">
            <li><router-link to="/" class="hover:text-indigo-600">Home</router-link></li>
            <li><a href="#movies-catalog" class="hover:text-indigo-600">Now Showing</a></li>
            <li><a href="#movies-catalog" class="hover:text-indigo-600">Coming Soon</a></li>
            <li><a href="#news-section" class="hover:text-indigo-600">Notice & Poll</a></li>
          </ul>
        </div>

        <!-- Column 3: Locations -->
        <div class="space-y-3">
          <h4 class="text-xs font-bold text-slate-900 uppercase tracking-wider">Locations</h4>
          <ul class="space-y-2 text-slate-500 font-medium">
            <li>• Sony Square, Mirpur, Dhaka</li>
            <li>• Bali Arcade, Chattogram</li>
            <li>• Bashundhara City, Dhaka</li>
            <li>• SKS Tower, Mohakhali, Dhaka</li>
          </ul>
        </div>

        <!-- Column 4: Contact -->
        <div class="space-y-3">
          <h4 class="text-xs font-bold text-slate-900 uppercase tracking-wider">Support</h4>
          <div class="space-y-2 text-slate-500 font-medium">
            <p class="flex items-center gap-2"><MapPin class="w-3.5 h-3.5 text-indigo-600" /> Dhaka & Chattogram, Bangladesh</p>
            <p class="flex items-center gap-2"><Phone class="w-3.5 h-3.5 text-indigo-600" /> +880 9612-666666</p>
            <p class="flex items-center gap-2"><Mail class="w-3.5 h-3.5 text-indigo-600" /> support@cinemaseat.com</p>
          </div>
        </div>

      </div>

      <div class="border-t border-slate-100 pt-6 text-center text-[11px] text-slate-400 flex flex-col sm:flex-row items-center justify-between gap-2">
        <p>© 2026 CinemaSeat Team 42. All rights reserved.</p>
        <p>Built with Spring Boot Microservices & Vue 3 for IEEE CS Hackathon.</p>
      </div>

    </div>
  </footer>
</template>
