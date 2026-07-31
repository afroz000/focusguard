CREATE TABLE focus_sessions (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                name VARCHAR(100) NOT NULL,
                                start_time DATETIME NOT NULL,
                                end_time DATETIME NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT pk_focus_sessions PRIMARY KEY (id),

                                CONSTRAINT fk_focus_sessions_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_focus_sessions_user_id
    ON focus_sessions(user_id);

CREATE INDEX idx_focus_sessions_status
    ON focus_sessions(status);