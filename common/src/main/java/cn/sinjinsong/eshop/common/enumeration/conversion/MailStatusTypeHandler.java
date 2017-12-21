package cn.sinjinsong.eshop.common.enumeration.conversion;


import cn.sinjinsong.eshop.common.enumeration.mail.MailStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by SinjinSong on 2017/4/28.
 */
public class MailStatusTypeHandler implements TypeHandler<MailStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, MailStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public MailStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return MailStatus.getByCode(rs.getInt(columnName));
    }

    @Override
    public MailStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return MailStatus.getByCode(rs.getInt(columnIndex));
    }

    @Override
    public MailStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MailStatus.getByCode(cs.getInt(columnIndex));
    }
}
