package cn.sinjinsong.eshop.service.pay.confirm;

import cn.sinjinsong.eshop.common.domain.entity.order.EnterpriseDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.common.properties.DbResult;
import cn.sinjinsong.eshop.service.order.EnterpriseService;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.pay.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sinjinsong
 * @date 2017/12/23
 */
@Service("accountServiceConfirm")
@Slf4j
public class AccountServiceConfirm implements AccountService {
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private OrderService orderService;
    /**
     * confirm:
     * 1. 读取收款方余额表的version
     * 2. 读取订单状态
     * 3. 如果订单是paying，则将订单状态设置为paid，执行收款方余额表的增加，version++(where中检查version)；
     * 如果没有执行更新，则表示有请求已经增加过余额了，则不再次增加。
     * @param order
     * @param paymentPassword
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        log.info("支付:confirm阶段");
        EnterpriseDO enterpriseDO = enterpriseService.find();
        if(order.getOrderStatus() != OrderStatus.PAYING){
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        // 更新订单状态
        order.setOrderStatus(OrderStatus.PAID);
        orderService.updateOrder(order);
        
        enterpriseDO.setBalance(enterpriseDO.getBalance() + order.getTotalPrice());
        int result = enterpriseService.updateEnterpriseMVCC(enterpriseDO);
        if(result == DbResult.FAILURE) {
            log.info("{} 订单confirm出现并发，MVCC更新失败",order.getId());
        }
    }
}
