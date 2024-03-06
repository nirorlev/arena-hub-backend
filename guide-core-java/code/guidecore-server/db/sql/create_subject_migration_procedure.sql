CREATE PROCEDURE courses_populate_temp_table()
BEGIN
    DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE v_access_id INT DEFAULT 0;
    DECLARE v_course_id INT;
    DECLARE v_update_time TIMESTAMP;
    DECLARE v_subject_json JSON DEFAULT '[]';
    DECLARE v_must_subject_json JSON DEFAULT '[]';

    -- cursor to iterate over each row in the gc_access table
    DECLARE access_cursor CURSOR FOR
        SELECT id, subject_json, must_subject_json, update_time FROM gc_access;

    -- handler for when no more rows are found
    DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;

    CREATE TEMPORARY TABLE courses_temp_table
    (
        access_id   INT,
        course_id   INT,
        mandatory   BOOLEAN,
        update_time TIMESTAMP
    );

    OPEN access_cursor;

    get_access:
    LOOP
        FETCH access_cursor INTO v_access_id, v_subject_json, v_must_subject_json, v_update_time;

        IF v_finished = 1 THEN
            LEAVE get_access;
        END IF;

        SET @i = 0;
        SET @json_length = JSON_LENGTH(v_subject_json);

        WHILE @i < @json_length
            DO
                SET v_course_id = JSON_UNQUOTE(JSON_EXTRACT(v_subject_json, CONCAT('$[', @i, ']')));

                If EXISTS(SELECT 1 FROM gc_subject WHERE id = v_course_id) THEN
                    INSERT INTO courses_temp_table (access_id, course_id, update_time, mandatory)
                    SELECT v_access_id                                                                      AS access_id,
                           v_course_id                                                                      AS course_id,
                           v_update_time                                                                    AS update_time,
                           JSON_CONTAINS(v_must_subject_json,
                                         JSON_UNQUOTE(JSON_EXTRACT(v_subject_json, CONCAT('$[', @i, ']')))) AS mandatory;
                END IF;
                SET @i = @i + 1;
            END WHILE;
    END LOOP get_access;

    CLOSE access_cursor;
END;
