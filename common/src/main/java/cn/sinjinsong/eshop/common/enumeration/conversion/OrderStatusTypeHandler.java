package cn.sinjinsong.eshop.common.enumeration.conversion;


import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by SinjinSong on 2017/4/28.
 */
public class OrderStatusTypeHandler implements TypeHandler<OrderStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, OrderStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public OrderStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return OrderStatus.getByCode(rs.getInt(columnName));
    }

    @Override
    public OrderStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return OrderStatus.getByCode(rs.getInt(columnIndex));
    }

    @Override
    public OrderStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return OrderStatus.getByCode(cs.getInt(columnIndex));
    }
}
