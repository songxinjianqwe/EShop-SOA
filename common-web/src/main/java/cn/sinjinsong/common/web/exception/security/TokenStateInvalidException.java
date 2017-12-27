package cn.sinjinsong.common.web.exception.security;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/5/7.
 */
@RestResponseStatus(value= HttpStatus.UNAUTHORIZED,code=4)
@RestField("tokenStatus")
public class TokenStateInvalidException extends BaseRestException {
    public TokenStateInvalidException(String tokenStatus){
        super(tokenStatus);
    }
}
