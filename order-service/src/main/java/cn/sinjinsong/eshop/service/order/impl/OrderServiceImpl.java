package cn.sinjinsong.eshop.service.order.impl;

import cn.sinjinsong.eshop.common.domain.dto.order.OrderQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.order.EnterpriseDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.dao.order.OrderDOMapper;
import cn.sinjinsong.eshop.properties.OrderProperties;
import cn.sinjinsong.eshop.service.order.EnterpriseService;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.product.ProductService;
import cn.sinjinsong.eshop.service.user.UserService;
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
@Service("orderService")
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDOMapper mapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 将原来Mapper中的association转为现在的populateBean
     *
     * @param order
     */
    private void populateBean(OrderDO order) {
        order.setUser(userService.findById(order.getUser().getId()));
        order.setProduct(productService.findProductById(order.getProduct().getId()));
    }

    @Transactional
    @Override
    public OrderDO placeOrder(OrderDO order) {
        mapper.insert(order);
        return order;
    }

    @Transactional(readOnly = true)
    @Override
    public PageInfo<OrderDO> findAll(Integer pageNum, Integer pageSize) {
        PageInfo<OrderDO> page = mapper.findAll(pageNum, pageSize).toPageInfo();
        page.getList().forEach(
                order -> populateBean(order)
        );
        return page;

    }

    @Transactional(readOnly = true)
    @Override
    public PageInfo<OrderDO> findAllByCondition(OrderQueryConditionDTO queryDTO, Integer pageNum, Integer pageSize) {
        if (queryDTO.getCategoryId() != null) {
            queryDTO.setProductIds(productService.findProductIdsByCategory(queryDTO.getCategoryId()));
        }
        PageInfo<OrderDO> page = mapper.findByCondition(queryDTO, pageNum, pageSize).toPageInfo();
        page.getList().forEach(order -> populateBean(order));
        return page;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDO findById(Long orderId) {
        OrderDO orderDO = mapper.selectByPrimaryKey(orderId);
        populateBean(orderDO);
        return orderDO;
    }

    /**
     * 支付业务的远程事务
     * @param order
     */
    @Transactional
    @Override
    public void finishOrder(OrderDO order) {
        // 更新订单状态
        order.setOrderStatus(OrderStatus.PAID);
        this.updateOrder(order);
        // 向企业用户转账
        EnterpriseDO enterpriseDO = enterpriseService.find();
        enterpriseDO.setBalance(enterpriseDO.getBalance() + order.getTotalPrice());
        enterpriseService.update(enterpriseDO);
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
