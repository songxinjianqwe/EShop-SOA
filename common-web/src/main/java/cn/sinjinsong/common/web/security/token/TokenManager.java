package cn.sinjinsong.common.web.security.token;


import cn.sinjinsong.common.web.security.domain.TokenCheckResult;

/**
 * Created by SinjinSong on 2017/4/27.
 */
public interface TokenManager {
    /**
     * 根据查询出来的用户username生成token，然后将username作为键，token作为值，存入缓存
     * @param username
     * @return
     */
    String createToken(String username);

    /**
     * 检查token是否有效，如果token格式正确并且尚未过期，那么返回username，否则返回一个异常
     * @param token
     * @return
     */
    TokenCheckResult checkToken(String token);
    
    /**
     * 当登出后，删除token；通过username删除token
     * @param username
     */
    void deleteToken(String username);
}
