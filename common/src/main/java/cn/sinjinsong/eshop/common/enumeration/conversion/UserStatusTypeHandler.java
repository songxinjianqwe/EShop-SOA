package cn.sinjinsong.eshop.common.enumeration.conversion;


import cn.sinjinsong.eshop.common.enumeration.user.UserStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by SinjinSong on 2017/4/28.
 */
public class UserStatusTypeHandler implements TypeHandler<UserStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, UserStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public UserStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return UserStatus.getByCode(rs.getInt(columnName));
    }

    @Override
    public UserStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return UserStatus.getByCode(rs.getInt(columnIndex));
    }

    @Override
    public UserStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return UserStatus.getByCode(cs.getInt(columnIndex));
    }
}
