ALTER TABLE visits
ADD COLUMN token_number INTEGER,
ADD COLUMN queue_status VARCHAR(50);

CREATE INDEX idx_visits_token_number ON visits(token_number);
CREATE INDEX idx_visits_queue_status ON visits(queue_status);
