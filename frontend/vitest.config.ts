import * as path from "path"
import * as VitestConfig from "vitest/config"
import react from "@vitejs/plugin-react"

export default VitestConfig.defineConfig({
  test: {
    environment: "jsdom",
    environmentOptions: {
      jsdom: {
        url: "http://localhost:3000",
      },
    },
    globals: true,
    includeSource: ["app/**/*.{ts,tsx}"],
    exclude: ["node_modules"],
    coverage: {
      reporter: process.env.CI ? "json" : "html-spa",
    },
    setupFiles: ["./app/vitest.ts"],
  },
  resolve: {
    alias: {
      "~": path.resolve(__dirname, "app"),
    },
  },
  plugins: [react()],
})
