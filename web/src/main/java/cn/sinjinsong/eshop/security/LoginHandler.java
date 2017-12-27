package cn.sinjinsong.eshop.security;


import cn.sinjinsong.eshop.common.domain.dto.user.LoginDTO;
import cn.sinjinsong.eshop.common.domain.entity.user.UserDO;

/**
 * Created by SinjinSong on 2017/5/7.
 */
public interface LoginHandler {
    UserDO handle(LoginDTO loginDTO);
}
