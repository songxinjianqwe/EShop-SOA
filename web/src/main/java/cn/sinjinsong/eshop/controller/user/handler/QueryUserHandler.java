package cn.sinjinsong.eshop.controller.user.handler;


import cn.sinjinsong.eshop.common.domain.entity.user.UserDO;

/**
 * Created by SinjinSong on 2017/4/27.
 */
public interface QueryUserHandler {
    UserDO handle(String key);
}
