package com.threeatom.common.mybatis.typehandler;

import com.alibaba.fastjson.JSONObject;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastJsonObjectTypeHandler implements TypeHandler<JSONObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonObjectTypeHandler.class);

    public FastJsonObjectTypeHandler() {}

    public JSONObject getResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        }
        if (jsonString.startsWith("[")) {
            jsonString = "{\"value\":" + jsonString + "}";
        }

        return JSONObject.parseObject(jsonString);
    }

    public JSONObject getResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        } else {
            return JSONObject.parseObject(jsonString);
        }
    }

    public JSONObject getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        } else {
            return JSONObject.parseObject(jsonString);
        }
    }

    public void setParameter(
            PreparedStatement ps, int columnIndex, JSONObject jsonObject, JdbcType jdbcType)
            throws SQLException {
        ps.setObject(columnIndex, wrapInPgobject(jsonObject));
    }

    private PGobject wrapInPgobject(JSONObject jsonObject) throws SQLException {
        PGobject pgJsonObject = new PGobject();
        pgJsonObject.setType("json");

        String value = jsonObject == null
            ? "{}"
            : jsonObject.toJSONString();

        pgJsonObject.setValue(value);
        return pgJsonObject;
    }
}
