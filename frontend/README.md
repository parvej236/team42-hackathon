# 🚀 Frontend Application (Team 42 Hackathon)

This is the frontend client for the **Team 42 Hackathon Project**, built with **Vue 3 (Composition API)**, **Vue Router**, **Pinia**, **Vite**, **Tailwind CSS v4**, and **Lucide Icons**.

---

## ⚡ Quick Start: How to Run the Application

Follow these simple steps to run the frontend application on your local system:

### 1. Prerequisites (Node.js & npm)
Ensure Node.js is installed on your system (Node.js `v24` recommended, `v18+` supported).
- 📥 **Official Download**: [Node.js Download Page](https://nodejs.org/en/download)

> 💡 **For Linux / Ubuntu Users (NVM Setup):**
> ```sh
> # Download and install nvm:
> curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.6/install.sh | bash
>
> # In lieu of restarting the shell:
> \. "$HOME/.nvm/nvm.sh"
>
> # Download and install Node.js:
> nvm install 24
>
> # Verify installation:
> node -v  # Should print "v24.19.0" or similar (e.g. v24.15.0)
> npm -v   # Should print "11.17.0"
> ```

### 2. Install Project Dependencies
```sh
npm install
```

### 3. Start Development Server
```sh
npm run dev
```
Once started, open your browser and navigate to `http://localhost:5173` (or the URL outputted in your terminal).

### 4. Build for Production (Optional)
```sh
npm run build
```

---

## 📚 Tech Stack & Official Documentation Links

| Technology | Role / Purpose | Documentation Link |
| :--- | :--- | :--- |
| **Vue.js 3** | Reactive UI Framework (Composition API) | 📖 [Vue 3 Quick Start Guide](https://vuejs.org/guide/quick-start) |
| **Vue Router** | Official Single Page Application (SPA) Router | 📖 [Vue Router Documentation](https://router.vuejs.org/) |
| **Pinia** | Official Intuitive State Management | 📖 [Pinia Documentation](https://pinia.vuejs.org/) |
| **Vite** | Dev Server & Production Bundler | 📖 [Vite Configuration Guide](https://vite.dev/config/) |
| **Tailwind CSS v4** | Utility-First Styling Engine | 📖 [Tailwind CSS v4 (Vite)](https://tailwindcss.com/docs/installation/using-vite) |
| **Lucide Icons** | SVG Component Icon Library | 📖 [Lucide Vue Documentation](https://lucide.dev/guide/vue/getting-started) |

---

## 🎯 Architectural Rationale & Technical Justification

Below is the technical justification for selecting **Vue.js 3**, **Vue Router**, **Pinia**, **Vite**, **Tailwind CSS v4**, and **Lucide Icons** as the primary frontend architecture.

### 📊 Quick Comparison Matrix

| Technology | Core Architectural Role | Key Advantage vs. Alternatives |
| :--- | :--- | :--- |
| **Vue.js 3 (Composition API)** | **Progressive & Fine-Grained Framework** | **Drop-in Anywhere**: Can be embedded into a single `<div>` or existing HTML/CSS/JS page via CDN, or scaled into a full SPA. Uses native `Proxy` reactivity—eliminating React's stale closures, manual dependency arrays (`useEffect`), and Angular's heavy RxJS boilerplate. |
| **Vue Router** | **SPA Navigation & Route Guards** | **Seamless Routing**: Officially maintained router providing client-side history navigation (`/`, `/signin`, `/signup`), scroll restoration, and route-level code-splitting. |
| **Pinia** | **Modular Reactive Store** | **Zero-Boilerplate State**: Replaces legacy Vuex and Redux ceremonies. Provides type-safe, modular state management (`useAuthStore`, `useCartStore`) leveraging Vue 3's `ref` and `computed` primitives. |
| **Vite** | **ESM-Native Build Tool** | **Sub-Second HMR**: Serves code over native ES Modules during development for instant cold server starts compared to legacy Webpack bundling. |
| **Tailwind CSS v4** | **Rust-Engineered Utility CSS** | **High Velocity & Consistent Tokens**: Powered by the Rust-based Oxide compiler for near-instant CSS compilation and strict design system consistency without stylesheet bloat. |
| **Lucide Icons** | **Tree-Shakeable SVG Components** | **Zero Asset Bloat**: Imported as pure Vue SVG components (`import { Camera } from '@lucide/vue'`), compiling *only* explicitly used icons in the bundle. |

### 🔬 Detailed Engineering Rationale

1. **Why Vue.js 3 over React or Angular?**
   * **Incremental & Progressive Flexibility**: Vue is uniquely designed to adopt progressively. It can enhance a single `<div>` inside any traditional webpage or scale into a full enterprise Single-Page Application (SPA).
   * **Direct Proxy Reactivity**: Vue 3 tracks reactive state via native JS `Proxy` objects (`ref`, `reactive`), triggering targeted DOM updates without needing manual memoization (`useMemo`, `useCallback`) or explicit hook dependency arrays (`useEffect`).
   * **Single File Component (SFC) Elegance**: `.vue` files cleanly package HTML markup, script logic, and scoped styles in one file—reducing context switching compared to React JSX or Angular's 4-file component model (`.ts`, `.html`, `.css`, `.spec.ts`).
   * **Unified Core Ecosystem**: Core primitives (Vue Router, Pinia, Vue DevTools) are officially maintained by the core Vue team, ensuring seamless version compatibility.

2. **Why Tailwind CSS v4?**
   * Eliminates custom CSS class naming friction and leverages the Rust Oxide engine for fast, zero-config compilation.

3. **Why Lucide Icons?**
   * Pure tree-shakeable Vue components that eliminate unused icon fonts or heavy asset bundles.

---

## 🎨 Icon Library Setup & Usage Example

### Installation
```sh
npm install @lucide/vue
```

### Component Usage Example
```vue
<script setup>
import { Camera } from '@lucide/vue';
</script>

<template>
  <Camera />
</template>
```

---

## 🚀 Production & Deployment (GitHub & Hostinger VPS via Coolify)

This frontend application is **100% GitHub Ready** and pre-configured for automated Docker deployment on **Hostinger VPS** using **Coolify**.

### 1. GitHub Repository Push Readiness
Make sure all changes are committed and pushed to your GitHub repository:
```sh
git add .
git commit -m "feat: production-ready Vue 3 frontend with green/orange light theme"
git push origin main
```

### 2. Coolify Deployment on Hostinger VPS (Docker-based)
The project includes a production multi-stage `Dockerfile` and `nginx.conf`:

1. **Log in to your Coolify dashboard** on your Hostinger VPS.
2. Select **+ Add New Resource** → **Public Repository** or **Private Repository**.
3. Paste your **GitHub Repository URL** and set the branch to `main`.
4. Select **Build Pack**: `Dockerfile`.
5. Set the Port to `80`.
6. Add Environment Variable:
   - `VITE_API_BASE_URL`: `https://your-api-domain.com/api/v1` (URL of your Spring Boot backend)
7. Click **Deploy**. Coolify will automatically pull from GitHub, build the static assets, and launch the Nginx container with Gzip compression & SPA fallback!

### 3. Manual Local Docker Build & Test (Optional)
```sh
# Build the Docker image
docker build -t team42-frontend:latest .

# Run container on port 8080
docker run -p 8080:80 team42-frontend:latest
```
Access `http://localhost:8080` to test the production Nginx build locally.

