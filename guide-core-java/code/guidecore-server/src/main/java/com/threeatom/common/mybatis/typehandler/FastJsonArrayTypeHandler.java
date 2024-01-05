//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.mybatis.typehandler;

import com.alibaba.fastjson.JSONArray;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastJsonArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonArrayTypeHandler.class);

    public FastJsonArrayTypeHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, "[]");
        } else {
            ps.setString(i, parameter.toJSONString());
        }

    }

    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        } else {
            return JSONArray.parseArray(jsonString);
        }
    }

    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        } else {
            return JSONArray.parseArray(jsonString);
        }
    }

    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (StringUtils.isEmpty(jsonString)) {
            LOGGER.warn("json字符串为Empty");
            return null;
        } else {
            return JSONArray.parseArray(jsonString);
        }
    }
}
