package cn.sinjinsong.eshop.controller.order;

import cn.sinjinsong.eshop.common.base.exception.RestValidationException;
import cn.sinjinsong.eshop.common.domain.dto.order.OrderQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.exception.order.OrderNotFoundException;
import cn.sinjinsong.eshop.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.exception.product.ProductNotFoundException;
import cn.sinjinsong.common.web.exception.security.AccessDeniedException;
import cn.sinjinsong.eshop.exception.user.UserNotFoundException;
import cn.sinjinsong.eshop.common.properties.PageProperties;
import cn.sinjinsong.common.web.security.domain.JWTUser;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.product.ProductService;
import cn.sinjinsong.eshop.service.user.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * Created by SinjinSong on 2017/10/6.
 */
@RestController
@RequestMapping("/orders")
@Slf4j
@Api(value = "orders", description = "订单API")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "下单", authorizations = {@Authorization("登录")}, response = OrderDO.class)
    public OrderDO placeOrder(@RequestBody @Valid @ApiParam(value = "订单对象") OrderDO order, BindingResult result) {
        log.info("{}", order);
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        if (userService.findById(order.getUser().getId()) == null) {
            throw new UserNotFoundException(String.valueOf(order.getUser().getId()));
        } else if (productService.findProductById(order.getProduct().getId()) == null) {
            throw new ProductNotFoundException(String.valueOf(order.getProduct().getId()));
        }
        order.setPlaceTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UNPAID);
        return orderService.placeOrder(order);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "分页查询所有订单", response = OrderDO.class, authorizations = {@Authorization("管理员权限")})
    @PreAuthorize("hasRole('ADMIN')")
    public PageInfo<OrderDO> findAll(@RequestParam(value = "pageNum", required = false, defaultValue = PageProperties.DEFAULT_PAGE_NUM)
                                     @ApiParam(value = "页码", required = false, defaultValue = PageProperties.DEFAULT_PAGE_NUM)
                                             Integer pageNum,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = PageProperties.DEFAULT_PAGE_SIZE)
                                     @ApiParam(value = "页的大小", required = false, defaultValue = PageProperties.DEFAULT_PAGE_SIZE)
                                             Integer pageSize) {
        return orderService.findAll(pageNum, pageSize);
    }

    @RequestMapping(value = "/condition", method = RequestMethod.POST)
    @ApiOperation(value = "分页按条件查询订单", response = OrderDO.class, authorizations = {@Authorization("登录")})
    public PageInfo<OrderDO> findAllByCondition(@RequestBody @Valid @ApiParam(value = "查询对象，包含了查询的各种条件", required = true) OrderQueryConditionDTO queryDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(Integer.valueOf(PageProperties.DEFAULT_PAGE_NUM));
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(Integer.valueOf(PageProperties.DEFAULT_PAGE_SIZE));
        }
        return orderService.findAllByCondition(queryDTO, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "按id查询订单", response = OrderDO.class, authorizations = {@Authorization("登录")})
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDO findById(@PathVariable("id") @ApiParam(value = "id", required = true) Long id) {
        return orderService.findById(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "更新订单", authorizations = {@Authorization("登录")})
    @PreAuthorize("hasRole('ADMIN') or principal.username == order.user.username")
    public void updateOrder(@RequestBody @Valid @ApiParam(value = "订单对象", required = true) OrderDO order, BindingResult result) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        orderService.updateOrder(order);
    }

    @RequestMapping(value = "/cancel/{orderId}", method = RequestMethod.PUT)
    @ApiOperation(value = "取消订单", authorizations = {@Authorization("登录")})
    public void cancelOrder(@PathVariable @ApiParam(value = "订单id", required = true) Long orderId, @AuthenticationPrincipal JWTUser user) {
        OrderDO order = orderService.findById(orderId);
        if (order == null) {
            throw new OrderNotFoundException(String.valueOf(orderId));
        }
        if (!user.getUsername().equals(order.getUser().getUsername())) {
            throw new AccessDeniedException(user.getUsername());
        }
        if (order.getOrderStatus() != OrderStatus.UNPAID) {
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        order.setOrderStatus(OrderStatus.CANCELED);
        orderService.updateOrder(order);
    }

}
