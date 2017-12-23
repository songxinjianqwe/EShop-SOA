package cn.sinjinsong.eshop.service.pay;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;

/**
 * 仅包含支付
 * @author sinjinsong
 * @date 2017/12/23
 */
public interface AccountService {
    void pay(OrderDO order, String paymentPassword);
}
