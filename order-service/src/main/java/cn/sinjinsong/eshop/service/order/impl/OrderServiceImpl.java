package cn.sinjinsong.eshop.service.order.impl;

import cn.sinjinsong.eshop.common.domain.dto.order.OrderQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.dao.order.OrderDOMapper;
import cn.sinjinsong.eshop.properties.OrderProperties;
import cn.sinjinsong.eshop.service.order.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by SinjinSong on 2017/10/6.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDOMapper mapper;

    @Transactional
    @Override
    public void placeOrder(OrderDO order) {
        mapper.insert(order);
    }

    @Transactional(readOnly = true)
    @Override
    public PageInfo<OrderDO> findAll(Integer pageNum, Integer pageSize) {
        return mapper.findAll(pageNum, pageSize).toPageInfo();
    }

    @Transactional(readOnly = true)
    @Override
    public PageInfo<OrderDO> findAllByCondition(OrderQueryConditionDTO queryDTO, Integer pageNum, Integer pageSize) {
        return mapper.findByCondition(queryDTO, pageNum, pageSize).toPageInfo();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDO findById(Long orderId) {
        return mapper.selectByPrimaryKey(orderId);
    }

    @Transactional
    @Override
    public void updateOrder(OrderDO order) {
        mapper.updateByPrimaryKeySelective(order);
    }

    @Transactional
    @Override
    public void updateTimeOutOrders() {
        findAllByCondition(OrderQueryConditionDTO.builder().status(OrderStatus.UNPAID).build(), 0, 0).getList().forEach(
                (OrderDO order) -> {
                    log.info("未付款订单号:{}", order.getId());
                    if (Duration.between(order.getPlaceTime(), LocalDateTime.now()).toMinutes() >= OrderProperties.TIME_OUT_SPAN) {
                        order.setOrderStatus(OrderStatus.TIME_OUT);
                        mapper.updateByPrimaryKeySelective(order);
                        log.info("超时订单:{}", order.getId());
                    }
                }
        );
    }
}
