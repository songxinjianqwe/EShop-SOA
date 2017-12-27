package cn.sinjinsong.eshop.exception;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@RestResponseStatus(value = HttpStatus.UNAUTHORIZED, code = 2)
@RestField("captcha")
public class CaptchaValidationException extends BaseRestException {
    public CaptchaValidationException(String value){
        super(value);
    }
}
