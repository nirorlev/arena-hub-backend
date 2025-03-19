package com.threeatom.common.mybatis.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.threeatom.guidecore.entity.TaskProperties;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import com.threeatom.guidecore.service.impl.TaskPropertiesStrategyImpl;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

@MappedTypes(TaskProperties.class)
public class TaskPropertiesJsonTypeHandler extends BaseTypeHandler<TaskProperties> {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    private final TaskPropertiesStrategy taskPropertiesStrategy = new TaskPropertiesStrategyImpl();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TaskProperties taskProperties, JdbcType jdbcType)
        throws SQLException {
        try {
            ps.setObject(i, wrapInPgobject(objectMapper.writeValueAsString(taskProperties)));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error serializing Answer object to JSON", e);
        }
    }

    @Override
    public TaskProperties getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String answer = rs.getString(columnName.toLowerCase());
        String taskTypeValue = rs.getString("task_type");
        return getTaskProperties(taskTypeValue, answer);
    }

    @Override
    public TaskProperties getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        String taskTypeValue = rs.getString("task_type");
        return getTaskProperties(taskTypeValue, json);
    }

    @Override
    public TaskProperties getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        String taskTypeValue = cs.getString("task_type");
        return getTaskProperties(taskTypeValue, json);
    }

    private TaskProperties getTaskProperties(String taskTypeValue, String answer) throws SQLException {
        if (taskTypeValue == null) {
            return null;
        }
        return parseJson(answer, TaskType.valueOf(taskTypeValue));
    }

    private TaskProperties parseJson(String json, TaskType taskType) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return taskPropertiesStrategy.createProperties(taskType, json);
        } catch (IOException e) {
            throw new SQLException("Error deserializing task properties object from JSON", e);
        }
    }

    private PGobject wrapInPgobject(String value) throws SQLException {
        PGobject pgJsonObject = new PGobject();
        pgJsonObject.setType("json");
        pgJsonObject.setValue(value);
        return pgJsonObject;
    }
}
