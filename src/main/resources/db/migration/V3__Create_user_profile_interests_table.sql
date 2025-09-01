-- Create user_profile_interests table
CREATE TABLE user_profile_interests (
    user_profile_id BIGINT NOT NULL,
    interest VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_profile_id, interest),
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

-- Create index for better performance
CREATE INDEX idx_user_profile_interests_user_profile_id ON user_profile_interests(user_profile_id);
CREATE INDEX idx_user_profile_interests_interest ON user_profile_interests(interest);