package com.threeatom.common.mybatis.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import com.threeatom.guidecore.service.impl.TaskPropertiesStrategyImpl;
import com.threeatom.guidecore.service.impl.VideoAnswerStrategyImpl;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

@MappedTypes(Task.class)
public class TaskJsonTypeHandler extends BaseTypeHandler<Task> {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private final VideoAnswerStrategy videoAnswerStrategy = new VideoAnswerStrategyImpl();
    private final TaskPropertiesStrategy taskPropertiesStrategy = new TaskPropertiesStrategyImpl();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Task task, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, wrapInPgobject(objectMapper.writeValueAsString(task)));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error serializing Task object to JSON", e);
        }
    }

    @SneakyThrows
    @Override
    public Task getNullableResult(ResultSet rs, String columnName) {
        String taskJson = rs.getString(columnName.toLowerCase());
        JsonNode node = objectMapper.readTree(taskJson);
        Task task = objectMapper.readValue(taskJson, Task.class);
        task.setAnswer(videoAnswerStrategy.createAnswer(task.getType(), node.get("answer").toString()));
        task.setProperties(taskPropertiesStrategy.createProperties(task.getType(), node.get("properties").toString()));
        return task;
    }

    @SneakyThrows
    @Override
    public Task getNullableResult(ResultSet rs, int columnIndex) {
        return objectMapper.readValue(rs.getString(columnIndex), Task.class);
    }

    @SneakyThrows
    @Override
    public Task getNullableResult(CallableStatement cs, int columnIndex) {
        return objectMapper.readValue(cs.getString(columnIndex), Task.class);
    }

    private PGobject wrapInPgobject(String value) throws SQLException {
        PGobject pgJsonObject = new PGobject();
        pgJsonObject.setType("json");
        pgJsonObject.setValue(value);
        return pgJsonObject;
    }
}
