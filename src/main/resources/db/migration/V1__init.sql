-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    email VARCHAR(255),
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP
);

CREATE INDEX idx_users_spotify_id ON users(spotify_id);

-- Artists table
CREATE TABLE artists (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    popularity INTEGER,
    image_url TEXT
);

CREATE INDEX idx_artists_spotify_id ON artists(spotify_id);

-- Artist genres (many-to-many)
CREATE TABLE artist_genres (
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    genre VARCHAR(255) NOT NULL,
    PRIMARY KEY (artist_id, genre)
);

-- Albums table
CREATE TABLE albums (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    release_date VARCHAR(50),
    release_date_precision VARCHAR(20),
    image_url TEXT,
    total_tracks INTEGER
);

CREATE INDEX idx_albums_spotify_id ON albums(spotify_id);

-- Album artists (many-to-many)
CREATE TABLE album_artists (
    album_id UUID NOT NULL REFERENCES albums(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL,
    PRIMARY KEY (album_id, artist_id)
);

-- Tracks table
CREATE TABLE tracks (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    album_id UUID REFERENCES albums(id),
    duration_ms BIGINT NOT NULL,
    explicit BOOLEAN NOT NULL DEFAULT FALSE,
    popularity INTEGER,
    preview_url TEXT
);

CREATE INDEX idx_tracks_spotify_id ON tracks(spotify_id);
CREATE INDEX idx_tracks_album_id ON tracks(album_id);

-- Track artists (many-to-many)
CREATE TABLE track_artists (
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL,
    PRIMARY KEY (track_id, artist_id)
);

-- Track external URLs
CREATE TABLE track_external_urls (
    id BIGSERIAL PRIMARY KEY,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    platform VARCHAR(50) NOT NULL,
    url TEXT NOT NULL
);

CREATE INDEX idx_track_external_urls_track_id ON track_external_urls(track_id);

-- Listening sessions table
CREATE TABLE listening_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    played_at TIMESTAMP NOT NULL,
    context_type VARCHAR(50),
    context_uri TEXT
);

CREATE INDEX idx_listening_sessions_user_id ON listening_sessions(user_id);
CREATE INDEX idx_listening_sessions_played_at ON listening_sessions(played_at);
CREATE INDEX idx_listening_sessions_track_id ON listening_sessions(track_id);

-- Unique constraint: same user cannot have duplicate session for same track at same time
CREATE UNIQUE INDEX uk_listening_sessions_user_track_played 
    ON listening_sessions(user_id, track_id, played_at);

-- Annotations table
CREATE TABLE annotations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_annotations_user_id ON annotations(user_id);
CREATE INDEX idx_annotations_track_id ON annotations(track_id);
CREATE INDEX idx_annotations_user_track ON annotations(user_id, track_id);

-- Ratings table
CREATE TABLE ratings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    overall_score INTEGER CHECK (overall_score IS NULL OR (overall_score >= 1 AND overall_score <= 5)),
    vocal_score INTEGER CHECK (vocal_score IS NULL OR (vocal_score >= 1 AND vocal_score <= 5)),
    mood_score INTEGER CHECK (mood_score IS NULL OR (mood_score >= 1 AND mood_score <= 5)),
    lyrics_score INTEGER CHECK (lyrics_score IS NULL OR (lyrics_score >= 1 AND lyrics_score <= 5)),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_ratings_user_track UNIQUE (user_id, track_id)
);

CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_ratings_track_id ON ratings(track_id);

-- Tags table
CREATE TABLE tags (
    id UUID PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    value VARCHAR(255)
);

CREATE INDEX idx_tags_key ON tags(key);

-- Track tags (many-to-many)
CREATE TABLE track_tags (
    id BIGSERIAL PRIMARY KEY,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    CONSTRAINT uk_track_tags UNIQUE (track_id, tag_id)
);

CREATE INDEX idx_track_tags_track_id ON track_tags(track_id);
CREATE INDEX idx_track_tags_tag_id ON track_tags(tag_id);

-- Playlists table
CREATE TABLE playlists (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    spotify_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_playlists_user_id ON playlists(user_id);
CREATE INDEX idx_playlists_spotify_id ON playlists(spotify_id);

-- Playlist tracks (many-to-many)
CREATE TABLE playlist_tracks (
    id BIGSERIAL PRIMARY KEY,
    playlist_id UUID NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    position INTEGER NOT NULL,
    added_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_playlist_tracks UNIQUE (playlist_id, track_id)
);

CREATE INDEX idx_playlist_tracks_playlist_id ON playlist_tracks(playlist_id);
CREATE INDEX idx_playlist_tracks_track_id ON playlist_tracks(track_id);

