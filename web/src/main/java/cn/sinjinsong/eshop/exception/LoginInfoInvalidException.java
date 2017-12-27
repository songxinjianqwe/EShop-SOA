package cn.sinjinsong.eshop.exception;

import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.domain.dto.user.LoginDTO;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@RestResponseStatus(value = HttpStatus.UNAUTHORIZED, code = 1)
@RestField("loginInfo")
public class LoginInfoInvalidException extends BaseRestException {
    public LoginInfoInvalidException(LoginDTO loginDTO) {
        super(loginDTO);
    }
}
