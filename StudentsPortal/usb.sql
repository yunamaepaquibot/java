-- ================================================================
-- STUDENT PORTAL - DATABASE (MySQL / MariaDB)
-- Lookup tables for the Create Account drop-downs + students table.
-- Run in phpMyAdmin / MySQL Workbench / mysql CLI.
-- ================================================================

CREATE DATABASE IF NOT EXISTS student_portal
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_portal;

-- ----------------------------------------------------------------
-- DROP-DOWN TABLES
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS courses (
    course_id   INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS year_levels (
    year_level_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    year_level    VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS majors (
    major_id   INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    major_name VARCHAR(100) NOT NULL UNIQUE
);

-- ----------------------------------------------------------------
-- STUDENTS (one row per created account)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id      INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   CHAR(64)     NOT NULL,          -- SHA-256 hex
    student_no      VARCHAR(30)  NOT NULL UNIQUE,
    first_name      VARCHAR(60)  NOT NULL,
    middle_name     VARCHAR(60)  NOT NULL,
    last_name       VARCHAR(60)  NOT NULL,
    email           VARCHAR(120) NOT NULL,
    course_id       INT UNSIGNED NOT NULL,
    year_level_id   INT UNSIGNED NOT NULL,
    major_id        INT UNSIGNED NOT NULL,
    contact_no      VARCHAR(20)  NULL,
    birthdate       DATE         NULL,
    gender          VARCHAR(20)  NULL,
    address         VARCHAR(255) NULL,
    date_registered DATE         NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_students_course FOREIGN KEY (course_id)
        REFERENCES courses (course_id),
    CONSTRAINT fk_students_year FOREIGN KEY (year_level_id)
        REFERENCES year_levels (year_level_id),
    CONSTRAINT fk_students_major FOREIGN KEY (major_id)
        REFERENCES majors (major_id)
);

-- ----------------------------------------------------------------
-- DROP-DOWN DATA (51 courses, 5 year levels, 49 majors)
-- ----------------------------------------------------------------
INSERT IGNORE INTO courses (course_name) VALUES
    ('BS Computer Science'),
    ('BS Information Technology'),
    ('BS Information Systems'),
    ('BS Computer Engineering'),
    ('BS Civil Engineering'),
    ('BS Mechanical Engineering'),
    ('BS Electrical Engineering'),
    ('BS Electronics Engineering'),
    ('BS Industrial Engineering'),
    ('BS Chemical Engineering'),
    ('BS Architecture'),
    ('BS Nursing'),
    ('BS Medical Technology'),
    ('BS Pharmacy'),
    ('BS Physical Therapy'),
    ('BS Radiologic Technology'),
    ('BS Nutrition and Dietetics'),
    ('BS Psychology'),
    ('BS Biology'),
    ('BS Chemistry'),
    ('BS Mathematics'),
    ('BS Statistics'),
    ('BS Environmental Science'),
    ('BS Accountancy'),
    ('BS Business Administration'),
    ('BS Entrepreneurship'),
    ('BS Economics'),
    ('BS Hospitality Management'),
    ('BS Tourism Management'),
    ('BS Office Administration'),
    ('BA Communication'),
    ('BA Journalism'),
    ('BA Political Science'),
    ('BA English Language'),
    ('BS Social Work'),
    ('BS Criminology'),
    ('Bachelor of Elementary Education'),
    ('Bachelor of Secondary Education'),
    ('Bachelor of Physical Education'),
    ('Bachelor of Early Childhood Ed.'),
    ('Bachelor of Fine Arts'),
    ('Bachelor of Multimedia Arts'),
    ('Bachelor of Music'),
    ('BS Interior Design'),
    ('BS Agriculture'),
    ('BS Forestry'),
    ('BS Fisheries'),
    ('BS Marine Biology'),
    ('BS Marine Transportation'),
    ('BS Aviation'),
    ('Other Course');

INSERT IGNORE INTO year_levels (year_level) VALUES
    ('1st Year'),
    ('2nd Year'),
    ('3rd Year'),
    ('4th Year'),
    ('5th Year');

INSERT IGNORE INTO majors (major_name) VALUES
    ('Software Engineering'),
    ('Web Development'),
    ('Data Science'),
    ('Artificial Intelligence'),
    ('Networking'),
    ('Cybersecurity'),
    ('Game Development'),
    ('Marketing Management'),
    ('Financial Management'),
    ('Human Resource Management'),
    ('Operations Management'),
    ('Entrepreneurship'),
    ('Banking and Finance'),
    ('Accounting'),
    ('Structural Engineering'),
    ('Construction Management'),
    ('Environmental Engineering'),
    ('Power Systems'),
    ('Mechatronics'),
    ('Clinical Nursing'),
    ('Community Health'),
    ('Clinical Psychology'),
    ('Industrial Psychology'),
    ('Biology'),
    ('Chemistry'),
    ('Mathematics'),
    ('English'),
    ('Filipino'),
    ('General Science'),
    ('Social Studies'),
    ('Special Education'),
    ('Journalism'),
    ('Broadcasting'),
    ('Public Relations'),
    ('Digital Media'),
    ('Hotel and Restaurant'),
    ('Culinary Arts'),
    ('Travel and Tours'),
    ('Animation'),
    ('Graphic Design'),
    ('Music Performance'),
    ('Interior Design'),
    ('Agribusiness'),
    ('Crop Science'),
    ('Forestry'),
    ('Marine Engineering'),
    ('General Studies'),
    ('Not Applicable'),
    ('Undeclared');

-- ----------------------------------------------------------------
-- SAMPLE: create an account using the drop-down choices
-- (replace the hash with the SHA-256 hex of the real password)
-- ----------------------------------------------------------------
INSERT INTO students
    (username, password_hash, student_no, first_name, middle_name, last_name,
     email, course_id, year_level_id, major_id)
VALUES
    ('ana', SHA2('password123', 256), '2024-0001', 'Ana', 'Cruz', 'Reyes',
     'ana@mail.com',
     (SELECT course_id FROM courses WHERE course_name = 'BS Information Technology'),
     (SELECT year_level_id FROM year_levels WHERE year_level = '2nd Year'),
     (SELECT major_id FROM majors WHERE major_name = 'Web Development'));

-- ----------------------------------------------------------------
-- USEFUL QUERIES
-- ----------------------------------------------------------------
-- Fill the three drop-downs:
--   SELECT course_name FROM courses ORDER BY course_id;
--   SELECT year_level  FROM year_levels ORDER BY year_level_id;
--   SELECT major_name  FROM majors ORDER BY major_id;
--
-- Show full student profiles:
--   SELECT s.student_no, CONCAT(s.first_name,' ',s.middle_name,' ',s.last_name) AS full_name,
--          s.email, c.course_name, y.year_level, m.major_name
--   FROM students s
--   JOIN courses c      ON c.course_id = s.course_id
--   JOIN year_levels y  ON y.year_level_id = s.year_level_id
--   JOIN majors m       ON m.major_id = s.major_id;