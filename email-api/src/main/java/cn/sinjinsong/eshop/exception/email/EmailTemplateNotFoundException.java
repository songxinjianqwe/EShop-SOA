package cn.sinjinsong.eshop.exception.email;

import cn.sinjinsong.eshop.common.base.exception.annotation
        .RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * @author songx
 * @date 2017/12/19
 */
@RestResponseStatus(value = HttpStatus.NOT_FOUND, code = 1)
@RestField("email")
public class EmailTemplateNotFoundException extends BaseRestException {
    public EmailTemplateNotFoundException(String email) {
        super(email);
    }
}
