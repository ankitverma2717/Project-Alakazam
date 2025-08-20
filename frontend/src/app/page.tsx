'use client';

import { ApolloProvider } from '@apollo/client';
import client from '@/lib/apollo-client';
import QueryInterface from '@/components/QueryInterface';

export default function Home() {
    return (
        <ApolloProvider client={client}>
            <main className="flex min-h-screen flex-col items-center p-12 md:p-24 bg-gray-100">
                <h1 className="text-3xl md:text-4xl font-bold text-gray-800">Project Alakazam</h1>
                <div className="w-full max-w-4xl flex justify-center items-center my-8">
                    <a href="/suggestions" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                        Indexing Suggestions
                    </a>
                </div>
                <QueryInterface />
            </main>
        </ApolloProvider>
    );
}