'use client'; // This is a Client Component, as it uses state and handles events

import { useState } from 'react';
import { gql, useMutation } from '@apollo/client';

// Define the GraphQL mutation. This must match the name in your backend schema.
const SUBMIT_QUERY_MUTATION = gql`
    mutation SubmitQuery($naturalLanguageQuery: String!) {
        submitQuery(naturalLanguageQuery: $naturalLanguageQuery) {
            id
            naturalLanguageQuery
            generatedSql
        }
    }
`;

export default function QueryInterface() {
    // State to hold the user's input
    const [query, setQuery] = useState('');

    // The useMutation hook from Apollo Client
    const [submitQuery, { data, loading, error }] = useMutation(SUBMIT_QUERY_MUTATION);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // Call the mutation function with the user's query as a variable
        submitQuery({ variables: { naturalLanguageQuery: query } });
    };

    return (
        <div className="w-full max-w-4xl mx-auto p-4">
            <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="query">
                        Enter your query in natural language
                    </label>
                    <textarea
                        id="query"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline h-24"
                        placeholder="e.g., Show me all users from London"
                    />
                </div>
                <div className="flex items-center justify-between">
                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:bg-gray-400"
                    >
                        {loading ? 'Generating...' : 'Generate SQL'}
                    </button>
                </div>
            </form>

            {/* Display the results below the form */}
            {error && <p className="text-red-500">Error: {error.message}</p>}

            {data && (
                <div className="bg-gray-800 text-white p-4 rounded-md shadow-lg mt-6">
                    <h3 className="text-lg font-bold mb-2">Generated SQL:</h3>
                    <pre className="whitespace-pre-wrap">
                <code>{data.submitQuery.generatedSql}</code>
            </pre>
                </div>
            )}
        </div>
    );
}