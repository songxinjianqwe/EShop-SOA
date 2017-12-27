package cn.sinjinsong.eshop.exception.mail;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/5/9.
 */
@RestResponseStatus(value= HttpStatus.NOT_FOUND,code=10)
@RestField("sender")
public class MailReceiverNotFoundException extends BaseRestException {
    public MailReceiverNotFoundException(Long sender){
        super(sender);
    }
}
