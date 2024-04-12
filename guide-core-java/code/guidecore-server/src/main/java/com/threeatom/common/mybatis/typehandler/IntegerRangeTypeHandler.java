package com.threeatom.common.mybatis.typehandler;

import com.threeatom.guidecore.entity.NumberRange;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.SneakyThrows;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgresql.util.PGobject;

public class IntegerRangeTypeHandler implements TypeHandler<NumberRange<Integer>> {

    private static final String RANGE_VALUE_FORMAT = "(%s, %s)";
    private static final String INT_4_RANGE_TYPE = "int4range";

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, NumberRange<Integer> numberRange,
                             JdbcType jdbcType)
        throws SQLException {
        PGobject int4range = new PGobject();
        int4range.setType(INT_4_RANGE_TYPE);
        int4range.setValue(String.format(RANGE_VALUE_FORMAT, numberRange.getFrom(), numberRange.getFrom()));
        preparedStatement.setObject(i, int4range);
    }

    @Override
    public NumberRange<Integer> getResult(ResultSet resultSet, String s) throws SQLException {
        PGobject int4range = (PGobject) resultSet.getObject(s);
        return parse(int4range);
    }

    @SneakyThrows
    private NumberRange<Integer> parse(PGobject int4range) {
        NumberRange<Integer> numberRange = new NumberRange<>();
        String rangeValue = int4range.getValue();

        if (rangeValue == null) {
            return null;
        }

        String withoutParentheses = rangeValue.substring(1, rangeValue.length() - 1);
        String[] parts = withoutParentheses.split(",");

        numberRange.setFrom(Integer.valueOf(parts[0]));
        numberRange.setTo(Integer.valueOf(parts[1]));

        return numberRange;
    }

    @Override
    public NumberRange<Integer> getResult(ResultSet resultSet, int i) throws SQLException {
        PGobject int4range = (PGobject) resultSet.getObject(i);
        return parse(int4range);
    }

    @Override
    public NumberRange<Integer> getResult(CallableStatement callableStatement, int i) throws SQLException {
        PGobject int4range = (PGobject) callableStatement.getObject(i);
        return parse(int4range);
    }
}
