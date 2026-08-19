import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        background: '#0B0F19',
        surface: '#111827',
        surfaceElevated: '#1F2937',
        primary: '#00E5FF',
        primaryDark: '#00B8D4',
        accent: '#00E676',
        danger: '#FF1744',
        warning: '#FF9100',
        textPrimary: '#F3F4F6',
        textSecondary: '#9CA3AF',
      },
    },
  },
  plugins: [],
}
export default config
