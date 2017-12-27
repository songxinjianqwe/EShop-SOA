package cn.sinjinsong.common.web.exception.security;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/5/9.
 */
@RestResponseStatus(value = HttpStatus.FORBIDDEN,code=1)
@RestField("role")
public class AccessDeniedException extends BaseRestException {
    public AccessDeniedException(String role){
        super(role);
    }
}
