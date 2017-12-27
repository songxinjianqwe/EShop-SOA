package cn.sinjinsong.eshop.exception.product;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/5/6.
 */
@RestResponseStatus(value = HttpStatus.NOT_FOUND,code=10)
@RestField("product")
public class ProductNotFoundException extends BaseRestException {
    public ProductNotFoundException(String key){
        super(key);
    }
}
