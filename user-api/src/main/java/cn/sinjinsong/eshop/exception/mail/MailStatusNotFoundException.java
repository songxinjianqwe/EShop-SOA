package cn.sinjinsong.eshop.exception.mail;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/5/5.
 */
@RestResponseStatus(value= HttpStatus.NOT_FOUND,code=7)
@RestField("targetId")
public class MailStatusNotFoundException extends BaseRestException {
    public MailStatusNotFoundException(Long targetId){
        super(targetId);
    }
}
