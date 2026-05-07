-- Reset local student-side test baseline data.
-- Usage:
--   mysql -h127.0.0.1 -uroot onepass_practice < sql/reset-student-test-baseline.sql
--   mysql -h127.0.0.1 -uroot onepass_practice < sql/seed.sql

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM practice_answer_record;
DELETE FROM practice_session_question;
DELETE FROM practice_session;
DELETE FROM question_wrong_stat;

DELETE FROM recite_day_record;
DELETE FROM recite_plan_day;
DELETE FROM recite_plan;

DELETE FROM vocabulary_word;
DELETE FROM vocabulary_book;

DELETE FROM student_dashboard_block;
DELETE FROM student_dashboard_template;

DELETE FROM question_answer;
DELETE FROM question_tag;
DELETE FROM question_option;
DELETE FROM question;

DELETE FROM category;
DELETE FROM student;

SET FOREIGN_KEY_CHECKS = 1;
