-- Create statistics table
CREATE TABLE statistics (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL UNIQUE,
    total_trails_completed INTEGER DEFAULT 0,
    total_distance_km DECIMAL(10,2) DEFAULT 0.0,
    total_time_minutes INTEGER DEFAULT 0,
    total_elevation_gain_m DECIMAL(10,2) DEFAULT 0.0,
    longest_trail_km INTEGER DEFAULT 0,
    highest_elevation_m INTEGER DEFAULT 0,
    total_photos_shared INTEGER DEFAULT 0,
    total_reviews_posted INTEGER DEFAULT 0,
    total_likes_received INTEGER DEFAULT 0,
    total_comments_received INTEGER DEFAULT 0,
    total_badges_earned INTEGER DEFAULT 0,
    total_points INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    total_followers INTEGER DEFAULT 0,
    total_following INTEGER DEFAULT 0,
    total_guides_booked INTEGER DEFAULT 0,
    global_rank INTEGER DEFAULT 0,
    local_rank INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

-- Create trigger to update updated_at column
CREATE TRIGGER update_statistics_updated_at
    BEFORE UPDATE ON statistics
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for better performance
CREATE INDEX idx_statistics_user_profile_id ON statistics(user_profile_id);
CREATE INDEX idx_statistics_total_points ON statistics(total_points DESC);
CREATE INDEX idx_statistics_global_rank ON statistics(global_rank);
CREATE INDEX idx_statistics_last_activity_at ON statistics(last_activity_at);