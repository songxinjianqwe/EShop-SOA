package cn.sinjinsong.eshop.exception.user;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@RestResponseStatus(value = HttpStatus.NOT_FOUND,code =1)
@RestField("queryMode")
public class QueryUserModeNotFoundException extends BaseRestException {
    public QueryUserModeNotFoundException(String mode){
        super(mode);
    }
}
