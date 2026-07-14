CREATE TABLE IF NOT EXISTS student (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    wechat_openid VARCHAR(128) NULL,
    wechat_unionid VARCHAR(128) NULL,
    display_name VARCHAR(128) NOT NULL,
    avatar_url VARCHAR(255) NOT NULL DEFAULT '',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_student_wechat_openid (wechat_openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @has_fk_recite_plan_student := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_plan'
      AND CONSTRAINT_NAME = 'fk_recite_plan_student'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @drop_fk_recite_plan_student_sql := IF(
    @has_fk_recite_plan_student > 0,
    'ALTER TABLE recite_plan DROP FOREIGN KEY fk_recite_plan_student',
    'SELECT 1'
);
PREPARE drop_fk_recite_plan_student_stmt FROM @drop_fk_recite_plan_student_sql;
EXECUTE drop_fk_recite_plan_student_stmt;
DEALLOCATE PREPARE drop_fk_recite_plan_student_stmt;

SET @has_fk_recite_day_record_student := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_day_record'
      AND CONSTRAINT_NAME = 'fk_recite_day_record_student'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @drop_fk_recite_day_record_student_sql := IF(
    @has_fk_recite_day_record_student > 0,
    'ALTER TABLE recite_day_record DROP FOREIGN KEY fk_recite_day_record_student',
    'SELECT 1'
);
PREPARE drop_fk_recite_day_record_student_stmt FROM @drop_fk_recite_day_record_student_sql;
EXECUTE drop_fk_recite_day_record_student_stmt;
DEALLOCATE PREPARE drop_fk_recite_day_record_student_stmt;

SET @student_id_extra := (
    SELECT EXTRA
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND COLUMN_NAME = 'id'
);
SET @student_id_auto_sql := IF(
    @student_id_extra IS NOT NULL AND LOCATE('auto_increment', @student_id_extra) = 0,
    'ALTER TABLE student MODIFY COLUMN id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT',
    'SELECT 1'
);
PREPARE student_id_auto_stmt FROM @student_id_auto_sql;
EXECUTE student_id_auto_stmt;
DEALLOCATE PREPARE student_id_auto_stmt;

SET @has_recite_plan_table := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_plan'
);
SET @has_fk_recite_plan_student_after := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_plan'
      AND CONSTRAINT_NAME = 'fk_recite_plan_student'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @add_fk_recite_plan_student_sql := IF(
    @has_recite_plan_table > 0 AND @has_fk_recite_plan_student_after = 0,
    'ALTER TABLE recite_plan ADD CONSTRAINT fk_recite_plan_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE add_fk_recite_plan_student_stmt FROM @add_fk_recite_plan_student_sql;
EXECUTE add_fk_recite_plan_student_stmt;
DEALLOCATE PREPARE add_fk_recite_plan_student_stmt;

SET @has_recite_day_record_table := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_day_record'
);
SET @has_fk_recite_day_record_student_after := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_day_record'
      AND CONSTRAINT_NAME = 'fk_recite_day_record_student'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @add_fk_recite_day_record_student_sql := IF(
    @has_recite_day_record_table > 0 AND @has_fk_recite_day_record_student_after = 0,
    'ALTER TABLE recite_day_record ADD CONSTRAINT fk_recite_day_record_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE add_fk_recite_day_record_student_stmt FROM @add_fk_recite_day_record_student_sql;
EXECUTE add_fk_recite_day_record_student_stmt;
DEALLOCATE PREPARE add_fk_recite_day_record_student_stmt;

SET @has_student_wechat_openid := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND COLUMN_NAME = 'wechat_openid'
);
SET @student_wechat_openid_sql := IF(
    @has_student_wechat_openid = 0,
    'ALTER TABLE student ADD COLUMN wechat_openid VARCHAR(128) NULL AFTER id',
    'SELECT 1'
);
PREPARE student_wechat_openid_stmt FROM @student_wechat_openid_sql;
EXECUTE student_wechat_openid_stmt;
DEALLOCATE PREPARE student_wechat_openid_stmt;

SET @has_student_wechat_unionid := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND COLUMN_NAME = 'wechat_unionid'
);
SET @student_wechat_unionid_sql := IF(
    @has_student_wechat_unionid = 0,
    'ALTER TABLE student ADD COLUMN wechat_unionid VARCHAR(128) NULL AFTER wechat_openid',
    'SELECT 1'
);
PREPARE student_wechat_unionid_stmt FROM @student_wechat_unionid_sql;
EXECUTE student_wechat_unionid_stmt;
DEALLOCATE PREPARE student_wechat_unionid_stmt;

SET @has_student_wechat_openid_key := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'student'
      AND INDEX_NAME = 'uk_student_wechat_openid'
);
SET @student_wechat_openid_key_sql := IF(
    @has_student_wechat_openid_key = 0,
    'ALTER TABLE student ADD UNIQUE KEY uk_student_wechat_openid (wechat_openid)',
    'SELECT 1'
);
PREPARE student_wechat_openid_key_stmt FROM @student_wechat_openid_key_sql;
EXECUTE student_wechat_openid_key_stmt;
DEALLOCATE PREPARE student_wechat_openid_key_stmt;

CREATE TABLE IF NOT EXISTS category (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    parent_id VARCHAR(64) NULL,
    name VARCHAR(128) NOT NULL,
    subtitle VARCHAR(128) NOT NULL,
    is_leaf TINYINT(1) NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_category_parent_sort (parent_id, sort_order, id),
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES category(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    category_id VARCHAR(64) NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    stem TEXT NOT NULL,
    analysis TEXT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_question_category_sort (category_id, sort_order, id),
    CONSTRAINT fk_question_category
        FOREIGN KEY (category_id) REFERENCES category(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_option (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL,
    option_key VARCHAR(8) NOT NULL,
    content TEXT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_question_option_question_key (question_id, option_key),
    KEY idx_question_option_question_sort (question_id, sort_order),
    CONSTRAINT fk_question_option_question
        FOREIGN KEY (question_id) REFERENCES question(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_tag (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL,
    tag_name VARCHAR(64) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_question_tag_question_name (question_id, tag_name),
    KEY idx_question_tag_question_sort (question_id, sort_order),
    KEY idx_question_tag_name (tag_name),
    CONSTRAINT fk_question_tag_question
        FOREIGN KEY (question_id) REFERENCES question(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_answer (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL,
    answer_key VARCHAR(8) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_question_answer_question_key (question_id, answer_key),
    KEY idx_question_answer_question_sort (question_id, sort_order),
    CONSTRAINT fk_question_answer_question
        FOREIGN KEY (question_id) REFERENCES question(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_import_batch (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    batch_id VARCHAR(64) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    storage_dir VARCHAR(512) NOT NULL,
    markdown_path VARCHAR(512) NOT NULL,
    total_count INT NOT NULL DEFAULT 0,
    supported_count INT NOT NULL DEFAULT 0,
    unsupported_count INT NOT NULL DEFAULT 0,
    error_count INT NOT NULL DEFAULT 0,
    warning_count INT NOT NULL DEFAULT 0,
    imported_count INT NOT NULL DEFAULT 0,
    error_report_json JSON NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    imported_at DATETIME(3) NULL,
    canceled_at DATETIME(3) NULL,
    UNIQUE KEY uk_question_import_batch_id (batch_id),
    KEY idx_question_import_batch_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_import_item (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    batch_id VARCHAR(64) NOT NULL,
    item_order INT NOT NULL,
    source_question_no VARCHAR(32) NULL,
    question_type VARCHAR(64) NOT NULL,
    normalized_question_type VARCHAR(32) NULL,
    category_path VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    errors_json JSON NOT NULL,
    warnings_json JSON NOT NULL,
    parsed_json JSON NOT NULL,
    markdown_block MEDIUMTEXT NOT NULL,
    target_question_id VARCHAR(64) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_question_import_item_order (batch_id, item_order),
    KEY idx_question_import_item_batch_status (batch_id, status),
    CONSTRAINT fk_question_import_item_batch
        FOREIGN KEY (batch_id) REFERENCES question_import_batch(batch_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_asset (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    batch_id VARCHAR(64) NOT NULL,
    import_item_id BIGINT UNSIGNED NULL,
    question_id VARCHAR(64) NULL,
    asset_type VARCHAR(32) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    relative_path VARCHAR(512) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_question_asset_batch (batch_id),
    KEY idx_question_asset_question (question_id),
    CONSTRAINT fk_question_asset_batch
        FOREIGN KEY (batch_id) REFERENCES question_import_batch(batch_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_question_asset_item
        FOREIGN KEY (import_item_id) REFERENCES question_import_item(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_question_asset_question
        FOREIGN KEY (question_id) REFERENCES question(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student_dashboard_template (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL,
    title VARCHAR(128) NOT NULL,
    template_name VARCHAR(128) NOT NULL,
    current_question_bank VARCHAR(255) NOT NULL,
    current_recite_plan VARCHAR(255) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_student_dashboard_template_code (template_code),
    KEY idx_student_dashboard_template_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student_dashboard_block (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT UNSIGNED NOT NULL,
    block_key VARCHAR(64) NOT NULL,
    label VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_student_dashboard_block_key (template_id, block_key),
    KEY idx_student_dashboard_block_sort (template_id, sort_order),
    CONSTRAINT fk_student_dashboard_block_template
        FOREIGN KEY (template_id) REFERENCES student_dashboard_template(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vocabulary_book (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(255) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_vocabulary_book_active_sort (active, sort_order, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vocabulary_word (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(64) NOT NULL,
    english VARCHAR(128) NOT NULL,
    chinese VARCHAR(255) NOT NULL,
    part_of_speech VARCHAR(32) NOT NULL DEFAULT '',
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_vocabulary_word_book_sort (book_id, sort_order, id),
    CONSTRAINT fk_vocabulary_word_book
        FOREIGN KEY (book_id) REFERENCES vocabulary_book(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recite_plan (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT UNSIGNED NOT NULL,
    book_id VARCHAR(64) NOT NULL,
    book_name VARCHAR(128) NOT NULL,
    daily_count INT NOT NULL,
    total_words INT NOT NULL,
    total_days INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    superseded_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_recite_plan_student_status (student_id, status, created_at),
    CONSTRAINT fk_recite_plan_book
        FOREIGN KEY (book_id) REFERENCES vocabulary_book(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_recite_plan_student
        FOREIGN KEY (student_id) REFERENCES student(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recite_plan_day (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT UNSIGNED NOT NULL,
    day_number INT NOT NULL,
    day_label VARCHAR(32) NOT NULL,
    start_word_order INT NOT NULL,
    end_word_order INT NOT NULL,
    total_count INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    study_completed_at DATETIME(3) NULL,
    last_accuracy VARCHAR(16) NULL,
    last_correct_count INT NULL,
    last_wrong_count INT NULL,
    completed_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_recite_plan_day_plan_number (plan_id, day_number),
    KEY idx_recite_plan_day_plan_status (plan_id, status, day_number),
    CONSTRAINT fk_recite_plan_day_plan
        FOREIGN KEY (plan_id) REFERENCES recite_plan(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @has_recite_study_completed_at := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'recite_plan_day'
      AND COLUMN_NAME = 'study_completed_at'
);
SET @recite_study_completed_at_sql := IF(
    @has_recite_study_completed_at = 0,
    'ALTER TABLE recite_plan_day ADD COLUMN study_completed_at DATETIME(3) NULL AFTER status',
    'SELECT 1'
);
PREPARE recite_study_completed_at_stmt FROM @recite_study_completed_at_sql;
EXECUTE recite_study_completed_at_stmt;
DEALLOCATE PREPARE recite_study_completed_at_stmt;

CREATE TABLE IF NOT EXISTS recite_day_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    plan_day_id BIGINT UNSIGNED NOT NULL,
    plan_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    book_name VARCHAR(128) NOT NULL,
    day_label VARCHAR(32) NOT NULL,
    mode VARCHAR(32) NOT NULL,
    total_count INT NOT NULL,
    correct_count INT NOT NULL,
    wrong_count INT NOT NULL,
    accuracy VARCHAR(16) NOT NULL,
    answers_json JSON NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_recite_day_record_student_created (student_id, created_at),
    KEY idx_recite_day_record_plan_day_created (plan_day_id, created_at),
    CONSTRAINT fk_recite_day_record_plan_day
        FOREIGN KEY (plan_day_id) REFERENCES recite_plan_day(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recite_day_record_plan
        FOREIGN KEY (plan_id) REFERENCES recite_plan(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recite_day_record_student
        FOREIGN KEY (student_id) REFERENCES student(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS practice_session (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    entry_type VARCHAR(32) NOT NULL,
    category_id VARCHAR(64) NOT NULL DEFAULT '',
    category_name VARCHAR(128) NOT NULL,
    feedback_mode VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    current_index INT NOT NULL DEFAULT 0,
    total_count INT NOT NULL DEFAULT 0,
    started_at DATETIME(3) NOT NULL,
    last_active_at DATETIME(3) NOT NULL,
    completed_at DATETIME(3) NULL,
    expired_at DATETIME(3) NULL,
    abandoned_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_practice_session_session_id (session_id),
    KEY idx_practice_session_scope_status (student_id, entry_type, category_id, status),
    KEY idx_practice_session_status_last_active (status, last_active_at),
    KEY idx_practice_session_student_started (student_id, started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS practice_session_question (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    question_order INT NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    stem TEXT NOT NULL,
    tags_json JSON NOT NULL,
    options_json JSON NOT NULL,
    standard_answer_json JSON NOT NULL,
    analysis TEXT NOT NULL,
    user_answer_json JSON NULL,
    user_answer_label VARCHAR(255) NULL,
    answer_label VARCHAR(255) NULL,
    submitted TINYINT(1) NOT NULL DEFAULT 0,
    correct TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_practice_session_question_order (session_id, question_order),
    UNIQUE KEY uk_practice_session_question_id (session_id, question_id),
    KEY idx_practice_session_question_question_id (question_id),
    CONSTRAINT fk_practice_session_question_session
        FOREIGN KEY (session_id) REFERENCES practice_session(session_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS practice_answer_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    submit_seq INT NOT NULL,
    selected_answer_json JSON NOT NULL,
    correct TINYINT(1) NOT NULL DEFAULT 0,
    submitted_at DATETIME(3) NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_practice_answer_record_submit_seq (session_id, question_id, submit_seq),
    KEY idx_practice_answer_record_session (session_id),
    KEY idx_practice_answer_record_question (question_id),
    CONSTRAINT fk_practice_answer_record_session
        FOREIGN KEY (session_id) REFERENCES practice_session(session_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_wrong_stat (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT UNSIGNED NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    answered_count INT NOT NULL DEFAULT 0,
    wrong_count INT NOT NULL DEFAULT 0,
    last_answered_at DATETIME(3) NULL,
    last_wrong_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_question_wrong_stat_student_question (student_id, question_id),
    KEY idx_question_wrong_stat_student_wrong (student_id, wrong_count),
    KEY idx_question_wrong_stat_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
