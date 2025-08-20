'use client';

import { useState } from 'react';
import { gql, useMutation } from '@apollo/client';

const SUBMIT_QUERY_MUTATION = gql`
    mutation SubmitQuery($naturalLanguageQuery: String!) {
        submitQuery(naturalLanguageQuery: $naturalLanguageQuery) {
            id
            naturalLanguageQuery
            generatedSql
            predictedPerformance
            executionTimeMs
        }
    }
`;

const EXPLAIN_QUERY_MUTATION = gql`
    mutation ExplainQuery($sql: String!) {
        explainQuery(sql: $sql)
    }
`;

export default function QueryInterface() {
    const [query, setQuery] = useState('');
    const [explanation, setExplanation] = useState('');
    const [submitQuery, { data, loading, error }] = useMutation(SUBMIT_QUERY_MUTATION);
    const [explainQuery, { loading: explaining }] = useMutation(EXPLAIN_QUERY_MUTATION, {
        onCompleted: (data) => {
            setExplanation(data.explainQuery);
        },
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setExplanation(''); // Clear previous explanation
        submitQuery({ variables: { naturalLanguageQuery: query } });
    };

    const handleExplain = () => {
        if (data && data.submitQuery.generatedSql) {
            explainQuery({ variables: { sql: data.submitQuery.generatedSql } });
        }
    };

    const getPerformanceColor = (performance: string) => {
        switch (performance) {
            case 'Fast':
                return 'text-green-400';
            case 'Moderate':
                return 'text-yellow-400';
            case 'Complex':
                return 'text-red-400';
            default:
                return 'text-gray-400';
        }
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

            {error && <p className="text-red-500">Error: {error.message}</p>}

            {data && (
                <div className="bg-gray-800 text-white p-4 rounded-md shadow-lg mt-6">
                    <div className="flex justify-between items-center mb-2">
                        <h3 className="text-lg font-bold">Generated SQL:</h3>
                        <div className="flex items-center">
                            <div className="text-sm mr-4">
                                <span>Predicted Performance: </span>
                                <span className={getPerformanceColor(data.submitQuery.predictedPerformance)}>
                                    {data.submitQuery.predictedPerformance}
                                </span>
                                <span className="mx-2">|</span>
                                <span>Execution Time: {data.submitQuery.executionTimeMs} ms</span>
                            </div>
                            <button
                                type="button"
                                onClick={handleExplain}
                                disabled={!data || loading || explaining}
                                className="bg-green-500 hover:bg-green-700 text-white font-bold py-1 px-2 rounded text-sm focus:outline-none focus:shadow-outline disabled:bg-gray-400"
                            >
                                {explaining ? '...' : 'Explain'}
                            </button>
                        </div>
                    </div>
                    <pre className="whitespace-pre-wrap">
                        <code>{data.submitQuery.generatedSql}</code>
                    </pre>
                </div>
            )}

            {explanation && (
                <div className="bg-gray-700 text-white p-4 rounded-md shadow-lg mt-6">
                    <h3 className="text-lg font-bold mb-2">Explanation:</h3>
                    <p>{explanation}</p>
                </div>
            )}
        </div>
    );
}
