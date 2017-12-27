package cn.sinjinsong.eshop.controller.token;


import cn.sinjinsong.eshop.common.base.exception.RestValidationException;
import cn.sinjinsong.eshop.common.domain.dto.user.LoginDTO;
import cn.sinjinsong.eshop.common.domain.dto.user.LoginSuccessResult;
import cn.sinjinsong.eshop.common.domain.entity.user.UserDO;
import cn.sinjinsong.eshop.common.enumeration.user.UserStatus;
import cn.sinjinsong.eshop.exception.CaptchaValidationException;
import cn.sinjinsong.eshop.exception.LoginInfoInvalidException;
import cn.sinjinsong.eshop.exception.UserStatusInvalidException;
import cn.sinjinsong.eshop.common.util.SpringContextUtil;
import cn.sinjinsong.common.web.security.domain.JWTUser;
import cn.sinjinsong.eshop.security.LoginHandler;
import cn.sinjinsong.common.web.security.token.TokenManager;
import cn.sinjinsong.common.web.security.verification.VerificationManager;
import cn.sinjinsong.eshop.util.CosUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by SinjinSong on 2017/4/27.
 */

@RestController
@RequestMapping("/tokens")
@Api(value = "tokens", description = "各类token")
@Slf4j
public class TokenController {
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private VerificationManager verificationManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CosUtil cosUtil;
    /**
     * 1.用户登录时，先经过自定义的passcard_filter过滤器，
     * 该过滤器继承了AbstractAuthenticationProcessingFilter，
     * 2.执行attemptAuthentication方法，可以通过request获取登录页面传递的参数，
     * 实现自己的逻辑，并且把对应参数set到AbstractAuthenticationToken的实现类中
     * 3.验证逻辑走完后，调用 this.getAuthenticationManager().authenticate(token)方法，
     * 执行AuthenticationProvider的实现类的supports方法
     * 4.如果返回true则继续执行authenticate方法
     * 5.在authenticate方法中，首先可以根据用户名获取到用户信息，
     * 再者可以拿自定义参数和用户信息做逻辑验证，如密码的验证
     * 6.自定义验证通过以后，获取用户权限set到User中，用于springSecurity做权限验证
     * 7.this.getAuthenticationManager().authenticate(token)方法执行完后，
     * 会返回Authentication，如果不为空，则说明验证通过
     *
     * @param loginDTO
     * @param result
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "登录", response = LoginSuccessResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "验证码错误"),
            @ApiResponse(code = 400, message = "登录信息不完整"),
            @ApiResponse(code = 401, message = "用户名或密码错误"),
            @ApiResponse(code = 401, message = "用户未激活")
    })
    public LoginSuccessResult login(@Valid @RequestBody @ApiParam(value = "登录信息，要求用户名或手机号或邮箱有一个非空，且登录模式与其对应，可选值为username或phone；密码非空；验证id和验证码也非空", required = true) LoginDTO loginDTO, BindingResult result) {
        log.info("{}", loginDTO);
        //先验证图片验证码，再验证用户名和密码
        if (!verificationManager.checkVerificationCode(loginDTO.getCaptchaCode(), loginDTO.getCaptchaValue().toUpperCase())) {
            //验证失败，清除验证码
            verificationManager.deleteVerificationCode(loginDTO.getCaptchaCode());
            throw new CaptchaValidationException(loginDTO.getCaptchaValue());
        }
        //登录信息不完整
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }

        LoginHandler loginHandler = SpringContextUtil.getBean("LoginHandler", loginDTO.getUserMode().toString().toLowerCase());
        //下面进行校验
        UserDO user = loginHandler.handle(loginDTO);
        log.info("{}", user);
        String username = null;
        if (user != null) {
            username = user.getUsername();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (LockedException e) {
            throw new UserStatusInvalidException(UserStatus.FORBIDDEN.toString());
        } catch (DisabledException e) {
            throw new UserStatusInvalidException(UserStatus.UNACTIVATED.toString());
        } catch (AuthenticationException e) {
            throw new LoginInfoInvalidException(loginDTO);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //到这里验证成功
        //如果之前已经登录过，那么清除之前登录的token
        tokenManager.deleteToken(username);
        //申请新的token
        String token = tokenManager.createToken(username);
        //验证结束，清除验证码
        verificationManager.deleteVerificationCode(loginDTO.getCaptchaCode());
        return new LoginSuccessResult(user.getId(), username, token);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "登出", authorizations = {@Authorization("登录权限")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "未登录")
    })
    public void logout(@AuthenticationPrincipal JWTUser user) {
        tokenManager.deleteToken(user.getUsername());
    }
    
    @RequestMapping(value = "/cos", method = RequestMethod.GET)
    @ApiOperation(value = "获取腾讯云COS的上传Token", authorizations = {@Authorization("登录权限")})
    public String requestCosToken(@RequestParam("bucket") String bucket,@RequestParam("cosPath") String cosPath) {
        return cosUtil.getSign(bucket,cosPath);
    }
}
