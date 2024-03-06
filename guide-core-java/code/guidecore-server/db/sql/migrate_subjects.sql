CALL courses_populate_temp_table();

INSERT INTO gc_content_group_course_assignment (access_id, course_id, is_mandatory, modified_date)
SELECT access_id, course_id, IF(mandatory IS NULL, 0, mandatory), update_time
FROM courses_temp_table ctt
WHERE NOT EXISTS (SELECT 1
                  FROM gc_content_group_course_assignment
                  WHERE access_id = ctt.access_id
                    AND course_id = ctt.course_id);
