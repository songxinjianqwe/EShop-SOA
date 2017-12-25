package cn.sinjinsong.eshop.service.order;


import cn.sinjinsong.eshop.common.domain.dto.order.OrderQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import com.github.pagehelper.PageInfo;

/**
 * Created by SinjinSong on 2017/10/6.
 */
public interface OrderService {
    OrderDO placeOrder(OrderDO order);

    PageInfo<OrderDO> findAll(Integer pageNum, Integer pageSize);

    PageInfo<OrderDO> findAllByCondition(OrderQueryConditionDTO queryDTO, Integer pageNum, Integer pageSize);
    OrderDO findById(Long orderId);
    void updateOrder(OrderDO order);
    void updateTimeOutOrders();
    void finishOrder(OrderDO order);
}
