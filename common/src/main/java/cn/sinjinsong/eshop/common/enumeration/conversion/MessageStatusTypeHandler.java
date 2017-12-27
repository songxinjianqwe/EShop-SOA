package cn.sinjinsong.eshop.common.enumeration.conversion;

import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
public class MessageStatusTypeHandler  implements TypeHandler<MessageStatus> {
    
    @Override
    public void setParameter(PreparedStatement ps, int i, MessageStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public MessageStatus getResult(ResultSet rs, String columnName) throws SQLException {
         return MessageStatus.getByCode(rs.getInt(columnName));
    }

    @Override
    public MessageStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
         return MessageStatus.getByCode(rs.getInt(columnIndex));
    }
    
    @Override
    public MessageStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MessageStatus.getByCode(cs.getInt(columnIndex));
    }
}

    
