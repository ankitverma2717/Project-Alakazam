'use client';

import { ApolloProvider } from '@apollo/client';
import client from '@/lib/apollo-client';
import QueryInterface from '@/components/QueryInterface';

export default function Home() {
    return (
        <ApolloProvider client={client}>
            <main className="flex min-h-screen flex-col items-center p-12 md:p-24 bg-gray-100">
                <h1 className="text-3xl md:text-4xl font-bold mb-8 text-gray-800">Project Alakazam</h1>
                <QueryInterface />
            </main>
        </ApolloProvider>
    );
}