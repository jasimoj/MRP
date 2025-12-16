BEGIN;


CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    email TEXT  UNIQUE,
    favorite_genre TEXT,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE media (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    media_type TEXT NOT NULL,
    release_year INT NOT NULL,
    age_restriction INT NOT NULL,
    created_by_user_id INT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE media_genres (
    media_id INT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (media_id, genre_id)
);


CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id INT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    comment TEXT,
    stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at BIGINT NOT NULL,

    UNIQUE (user_id, media_id)
);


CREATE TABLE rating_likes (
    rating_id INT NOT NULL REFERENCES ratings(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (rating_id, user_id)
);


CREATE TABLE favorites (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id INT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, media_id)
);

COMMIT;
