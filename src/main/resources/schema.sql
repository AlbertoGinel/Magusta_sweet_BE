-- Drop the table if it already exists
DROP TABLE IF EXISTS TestTable;

-- Create a simple table
CREATE TABLE TestTable (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
