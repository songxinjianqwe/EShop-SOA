package cn.sinjinsong.eshop.controller.user.handler.impl;


import cn.sinjinsong.eshop.common.domain.entity.user.UserDO;
import cn.sinjinsong.eshop.controller.user.handler.QueryUserHandler;
import cn.sinjinsong.eshop.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by SinjinSong on 2017/5/6.
 */
@Component("QueryUserHandler.phone")
public class QueryUserByPhoneHandler implements QueryUserHandler {
    @Autowired
    private UserService userService;
    @Override
    public UserDO handle(String key) {
        return userService.findByEmail(key);
    }
}
