package com.threeatom.common.mybatis.typehandler;

import com.threeatom.guidecore.entity.GroupToUserPk;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class GroupToUserPkTypeHandler extends BaseTypeHandler<GroupToUserPk> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GroupToUserPk parameter, JdbcType jdbcType)
        throws SQLException {
        ps.setString(i, parameter.getPowtoonGroupCode());
        ps.setInt(i + 1, parameter.getUserId());
    }

    @Override
    public GroupToUserPk getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String powtoonGroupCode = rs.getString("powtoon_group_code");
        Integer userId = rs.getInt("group_user_id");
        return new GroupToUserPk(powtoonGroupCode, userId);
    }

    @Override
    public GroupToUserPk getNullableResult(ResultSet rs, int columnIndex) {
        throw new UnsupportedOperationException("Access by index is not supported for composite keys.");
    }

    @Override
    public GroupToUserPk getNullableResult(CallableStatement cs, int columnIndex) {
        throw new UnsupportedOperationException("Access by index is not supported for composite keys.");
    }
}
