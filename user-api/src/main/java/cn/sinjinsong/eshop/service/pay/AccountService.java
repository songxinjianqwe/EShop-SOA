package cn.sinjinsong.eshop.service.pay;

import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;

/**
 * 业务接口，需要实现commit和rollback方法，执行本地事务
 * @author sinjinsong
 * @date 2017/12/23
 */
public interface AccountService {
    void commit(OrderDO order, String paymentPassword);
    void rollback(ProducerTransactionMessageDO message );
}
