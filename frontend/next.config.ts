/** @type {import('next').NextConfig} */
const nextConfig = {
    // Add this line to enable static export
    output: 'export',

    // You might need to add this if you use the Image component and have issues
    // with the default loader in a static environment.
    images: {
        unoptimized: true,
    },
};

export default nextConfig;