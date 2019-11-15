CREATE TABLE IF NOT EXISTS source_table (id UUID PRIMARY KEY, balance INT, created_timestamp TIMESTAMP);

CREATE CHANGEFEED FOR TABLE source_table INTO 'kafka://kafka:9092';