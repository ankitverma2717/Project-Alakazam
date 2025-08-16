import type { Config } from "tailwindcss";

const config: Config = {
    content: [
        //It tells Tailwind to scan all your source files.
        "./src/**/*.{js,ts,jsx,tsx,mdx}",
    ],
    theme: {
        extend: {},
    },
    plugins: [],
};
export default config;