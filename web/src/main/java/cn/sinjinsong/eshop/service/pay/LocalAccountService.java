package cn.sinjinsong.eshop.service.pay;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;

/**
 * @author sinjinsong
 * @date 2017/12/24
 */
public interface LocalAccountService {
    void pay(OrderDO order, String paymentPassword);
}
