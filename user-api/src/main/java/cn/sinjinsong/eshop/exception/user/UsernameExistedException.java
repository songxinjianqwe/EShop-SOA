package cn.sinjinsong.eshop.exception.user;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

@RestResponseStatus(value = HttpStatus.CONFLICT, code = 1)
@RestField("name")
public class UsernameExistedException extends BaseRestException {
    public UsernameExistedException(String name) {
        super(name);
    }

}
