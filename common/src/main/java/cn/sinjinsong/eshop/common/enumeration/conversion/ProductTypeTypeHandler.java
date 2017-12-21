package cn.sinjinsong.eshop.common.enumeration.conversion;


import cn.sinjinsong.eshop.common.enumeration.product.ProductType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by SinjinSong on 2017/4/28.
 */
public class ProductTypeTypeHandler implements TypeHandler<ProductType> {

    @Override
    public void setParameter(PreparedStatement ps, int i, ProductType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ProductType getResult(ResultSet rs, String columnName) throws SQLException {
        return ProductType.getByCode(rs.getInt(columnName));
    }

    @Override
    public ProductType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return ProductType.getByCode(rs.getInt(columnIndex));
    }

    @Override
    public ProductType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ProductType.getByCode(cs.getInt(columnIndex));
    }
}
