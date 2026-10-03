CREATE TABLE fitness_user (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE activity (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES fitness_user (id) ON DELETE CASCADE,
    type VARCHAR(64) NOT NULL,
    additional_metrics JSONB,
    duration INTEGER NOT NULL,
    calories_burned INTEGER,
    start_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_activity_user_start_time ON activity (user_id, start_time DESC);

CREATE TABLE recommendation (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES fitness_user (id) ON DELETE CASCADE,
    activity_id VARCHAR(36) NOT NULL REFERENCES activity (id) ON DELETE CASCADE,
    type VARCHAR(255),
    recommendation VARCHAR(2000),
    improvements JSONB,
    suggestions JSONB,
    safety JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_recommendation_user_created ON recommendation (user_id, created_at DESC);
CREATE INDEX idx_recommendation_activity ON recommendation (activity_id);
