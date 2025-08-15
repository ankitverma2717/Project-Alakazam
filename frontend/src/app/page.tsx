// src/app/page.tsx
'use client'; // This page now depends on the Apollo Client provider

import { ApolloProvider } from '@apollo/client';
import client from '@/lib/apollo-client';
import QueryInterface from '@/components/QueryInterface';

export default function Home() {
    return (
        <ApolloProvider client={client}>
            <main className="flex min-h-screen flex-col items-center p-24 bg-gray-100">
                <h1 className="text-4xl font-bold mb-8">QueryMaster AI</h1>
                <QueryInterface />
            </main>
        </ApolloProvider>
    );
}