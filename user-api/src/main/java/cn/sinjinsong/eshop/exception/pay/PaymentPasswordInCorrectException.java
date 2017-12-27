package cn.sinjinsong.eshop.exception.pay;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import org.springframework.http.HttpStatus;

/**
 * Created by SinjinSong on 2017/10/14.
 */
@RestResponseStatus(value= HttpStatus.FORBIDDEN,code=2)
@RestField("userId")
public class PaymentPasswordInCorrectException extends BaseRestException {
    public PaymentPasswordInCorrectException(Long userId){
        super(userId);
    }
}
