CREATE TABLE performance_metrics (
                                     id UUID PRIMARY KEY,
                                     query_hash VARCHAR(64) NOT NULL,
                                     execution_time_ms INTEGER NOT NULL,
                                     cpu_usage_percent DECIMAL(5,2),
                                     memory_usage_mb INTEGER,
                                     io_operations INTEGER,
                                     recorded_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE index_suggestions (
                                   id UUID PRIMARY KEY,
                                   table_name VARCHAR(255) NOT NULL,
                                   column_names TEXT[] NOT NULL,  -- <-- Plural
                                   suggestion_reason TEXT,
                                   impact_score DECIMAL(3,2),
                                   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);