CREATE TABLE alumni (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        current_role VARCHAR(255),
                        university VARCHAR(255) NOT NULL,
                        location VARCHAR(255),
                        linkedin_headline VARCHAR(255),
                        passout_year INT NOT NULL,
                        created_at TIMESTAMP(6) NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP(6) NOT NULL DEFAULT NOW(),
                        version BIGINT
);