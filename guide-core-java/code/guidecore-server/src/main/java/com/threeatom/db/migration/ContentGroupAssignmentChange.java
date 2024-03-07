package com.threeatom.db.migration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.threeatom.db.migration.exception.MigrationFailedException;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;

public class ContentGroupAssignmentChange implements CustomTaskChange {

    private static final String GET_COURSES = "SELECT id, subject_json, must_subject_json, update_time FROM gc_access";
    private static final String INSERT_COURSE_ASSIGNMENTS =
        "INSERT INTO gc_content_group_course_assignment (content_group_id, course_id, is_mandatory, modified_date) VALUES (?, ?, ?, ?)";
    private static final String COURSE_EXISTS_QUERY = "SELECT 1 FROM gc_subject WHERE id = ?";
    private static final String CONFIRMATION_MESSAGE = "Content Group Assignment migration is completed!";

    @SneakyThrows
    @Override
    public void execute(Database database) {
        JdbcConnection connection = (JdbcConnection) database.getConnection();

        try (
            PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSES);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            connection.setAutoCommit(false);
            List<GcContentGroupCourseAssignment> contentGroupCourseAssignments = new ArrayList<>();

            while (resultSet.next()) {
                String subjectJson = resultSet.getString("subject_json");
                if (subjectJson == null) {
                    continue;
                }
                List<Integer> courseIds = JSON.parseObject(subjectJson, new TypeReference<List<Integer>>() {
                });

                for (Integer courseId : courseIds) {
                    if (courseExists(courseId, connection)) {
                        contentGroupCourseAssignments.add(createContentGroupAssignments(resultSet, courseId));
                    }
                }
            }

            saveCourseAssignments(contentGroupCourseAssignments, connection);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new MigrationFailedException("Failed to perform Content Group Assignment migration!", e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private GcContentGroupCourseAssignment createContentGroupAssignments(ResultSet resultSet, Integer courseId)
        throws SQLException {
        GcContentGroupCourseAssignment contentGroupCourseAssignment = new GcContentGroupCourseAssignment();

        contentGroupCourseAssignment.setContentGroupId(resultSet.getInt("id"));
        contentGroupCourseAssignment.setCourseId(courseId);
        contentGroupCourseAssignment.setMandatory(
            isMandatoryCourse(courseId, resultSet.getString("must_subject_json")));

        return contentGroupCourseAssignment;
    }

    private boolean courseExists(Integer courseId, JdbcConnection connection) throws SQLException, DatabaseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(COURSE_EXISTS_QUERY)) {
            preparedStatement.setInt(1, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Boolean isMandatoryCourse(Integer courseId, String mustSubjectJson) {
        return mustSubjectJson != null && mustSubjectJson.contains(String.valueOf(courseId));
    }

    private void saveCourseAssignments(
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments, JdbcConnection connection)
        throws SQLException, DatabaseException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_COURSE_ASSIGNMENTS);

        for (GcContentGroupCourseAssignment contentGroupCourseAssignment : contentGroupCourseAssignments) {
            preparedStatement.setInt(1, contentGroupCourseAssignment.getContentGroupId());
            preparedStatement.setInt(2, contentGroupCourseAssignment.getCourseId());
            preparedStatement.setBoolean(3, contentGroupCourseAssignment.getMandatory());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(contentGroupCourseAssignment.getModifiedDate()));
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
    }

    @Override
    public String getConfirmationMessage() {
        return CONFIRMATION_MESSAGE;
    }

    @Override
    public void setUp() {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}