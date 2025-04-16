package com.threeatom.common.mybatis.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import com.threeatom.guidecore.service.impl.VideoAnswerStrategyImpl;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

@MappedTypes(Answer.class)
public class TaskAnswerJsonTypeHandler extends BaseTypeHandler<Answer> {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private final VideoAnswerStrategy videoAnswerStrategy = new VideoAnswerStrategyImpl();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Answer answer, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, wrapInPgobject(objectMapper.writeValueAsString(answer)));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error serializing Answer object to JSON", e);
        }
    }

    @Override
    public Answer getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String answer = rs.getString(columnName.toLowerCase());
        String taskTypeValue = rs.getString("task_type");
        return getAnswer(taskTypeValue, answer);
    }

    @Override
    public Answer getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        String taskTypeValue = rs.getString("task_type");
        return getAnswer(taskTypeValue, json);
    }

    @Override
    public Answer getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        String taskTypeValue = cs.getString("task_type");
        return getAnswer(taskTypeValue, json);
    }

    private Answer getAnswer(String taskTypeValue, String json) throws SQLException {
        if (taskTypeValue == null) {
            return null;
        }
        return parseJson(json, TaskType.valueOf(taskTypeValue));
    }

    private Answer parseJson(String json, TaskType taskType) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return videoAnswerStrategy.createAnswer(taskType, json);
        } catch (IOException e) {
            throw new SQLException("Error deserializing Answer object from JSON", e);
        }
    }

    private PGobject wrapInPgobject(String value) throws SQLException {
        PGobject pgJsonObject = new PGobject();
        pgJsonObject.setType("json");
        pgJsonObject.setValue(value);
        return pgJsonObject;
    }
}
