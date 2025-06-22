import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { resolve } from "path";
import runtimeErrorOverlay from "@replit/vite-plugin-runtime-error-modal";

export default defineConfig({
  plugins: [
    react(),
    runtimeErrorOverlay(),
  ],
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"),
      "@shared": resolve(__dirname, "shared"),
    },
  },
  root: resolve(__dirname, "."),
  build: {
    outDir: resolve(__dirname, "dist"),
    emptyOutDir: true,
  },
  server: {
    port: 3000,
    // Optional: automatically open the browser when starting the dev server
    open: true,
    // Optional: if port 3000 is in use, try the next available port
    strictPort: false,
  },
});