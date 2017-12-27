package cn.sinjinsong.eshop.exception.pay;

import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestField;
import cn.sinjinsong.eshop.common.base.exception.annotation.RestResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * @author sinjinsong
 * @date 2017/12/27
 */
@RestField("order")
@RestResponseStatus(value=HttpStatus.BAD_REQUEST,code=40410)
public class OrderPaymentException extends BaseRestException {
    public OrderPaymentException(Long orderId){
        super(orderId);
    }
}
