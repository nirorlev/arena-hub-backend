package com.arena.hub.migration;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;

public class DecodeVideoNameMigration implements CustomTaskChange {
    private static final String GET_SYS_FILES =
        "SELECT id, name FROM sys_file WHERE file_type_index = 2 AND file_type = 'resource/link'";
    private static final String GET_VIDEOS_TEMPLATE_SQL = "SELECT id, video_name FROM gc_video WHERE file_id IN (%s)";
    private static final String UPDATE_VIDEO_NAME = "UPDATE gc_video SET video_name = ? WHERE id = ?";
    private static final String UPDATE_VIDEO_NAME_SYS_FILE = "UPDATE sys_file SET name = ? WHERE id = ?";

    @SneakyThrows
    @Override
    public void execute(Database database) {
        JdbcConnection connection = (JdbcConnection) database.getConnection();
        try (
            Statement statement = connection.createStatement();
            ResultSet sysFilesResultSet = statement.executeQuery(GET_SYS_FILES)
        ) {
            connection.setAutoCommit(false);
            List<String> sysFileIds = updateSysFileNames(connection, sysFilesResultSet);
            updateVideoNames(connection, sysFileIds);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private List<String> updateSysFileNames(JdbcConnection connection, ResultSet sysFilesResultSet)
        throws SQLException, DatabaseException {
        List<String> sysFileIds = new ArrayList<>();

        try (
            PreparedStatement updateSysFilePreparedStatement = connection.prepareStatement(
                UPDATE_VIDEO_NAME_SYS_FILE)
        ) {
            while (sysFilesResultSet.next()) {
                int sysFileId = sysFilesResultSet.getInt("id");
                sysFileIds.add(String.valueOf(sysFileId));

                String name = sysFilesResultSet.getString("name");
                String decodedName = java.net.URLDecoder.decode(name, StandardCharsets.UTF_8);

                updateSysFilePreparedStatement.setString(1, decodedName);
                updateSysFilePreparedStatement.setInt(2, sysFileId);
                updateSysFilePreparedStatement.addBatch();
            }
            updateSysFilePreparedStatement.executeBatch();
        }

        return sysFileIds;
    }

    private void updateVideoNames(JdbcConnection connection, List<String> sysFileIds)
        throws SQLException, DatabaseException {

        final String getVideosSql = String.format(GET_VIDEOS_TEMPLATE_SQL, String.join(",", sysFileIds));
        try (
            Statement statement = connection.createStatement();
            ResultSet videosResultSet = statement.executeQuery(getVideosSql);
            PreparedStatement updateVideoNamePreparedStatement = connection.prepareStatement(UPDATE_VIDEO_NAME)
        ) {
            while (videosResultSet.next()) {
                int videoId = videosResultSet.getInt("id");
                String videoName = videosResultSet.getString("video_name");
                String decodedVideoName = java.net.URLDecoder.decode(videoName, StandardCharsets.UTF_8);

                updateVideoNamePreparedStatement.setString(1, decodedVideoName);
                updateVideoNamePreparedStatement.setInt(2, videoId);
                updateVideoNamePreparedStatement.addBatch();
            }
            updateVideoNamePreparedStatement.executeBatch();
        }
    }

    @Override
    public String getConfirmationMessage() {
        return "Video Name decoding migration completed successfully";
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
