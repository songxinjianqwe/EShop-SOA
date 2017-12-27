package cn.sinjinsong.eshop.controller.user;

import cn.sinjinsong.eshop.common.base.exception.RestValidationException;
import cn.sinjinsong.eshop.common.domain.dto.user.ResetPasswordDTO;
import cn.sinjinsong.eshop.common.domain.entity.user.UserDO;
import cn.sinjinsong.eshop.common.enumeration.user.UserStatus;
import cn.sinjinsong.eshop.exception.ActivationCodeValidationException;
import cn.sinjinsong.eshop.exception.UserStatusInvalidException;
import cn.sinjinsong.eshop.exception.user.PasswordNotFoundException;
import cn.sinjinsong.eshop.exception.user.QueryUserModeNotFoundException;
import cn.sinjinsong.eshop.exception.user.UserNotFoundException;
import cn.sinjinsong.eshop.exception.user.UsernameExistedException;
import cn.sinjinsong.eshop.common.properties.PageProperties;
import cn.sinjinsong.eshop.common.util.SpringContextUtil;
import cn.sinjinsong.eshop.common.util.UUIDUtil;
import cn.sinjinsong.eshop.controller.user.handler.QueryUserHandler;
import cn.sinjinsong.common.web.properties.AuthenticationProperties;
import cn.sinjinsong.common.web.security.domain.JWTUser;
import cn.sinjinsong.common.web.security.token.TokenManager;
import cn.sinjinsong.common.web.security.verification.VerificationManager;
import cn.sinjinsong.eshop.service.email.EmailService;
import cn.sinjinsong.eshop.service.user.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@RestController
@RequestMapping("/users")
@Api(value = "users", description = "用户API")
@Slf4j
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private VerificationManager verificationManager;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthenticationProperties authenticationProperties;
    @Autowired
    private TokenManager tokenManager;
    
    /**
     * mode 支持id、username、email、手机号
     * 只有管理员或自己才可以查询某用户的完整信息
     * 如果是用户，那么只能访问自己的信息
     * 如果是管理员，那么只能访问自己的信息和所有用户的信息，不能访问其他管理员的信息
     *
     * @param key
     * @param mode id、username、email、手机号
     * @return
     */
    @RequestMapping(value = "/query/{key}", method = RequestMethod.GET)
    @PostAuthorize("(hasRole('ADMIN') and not returnObject.roles.contains(new cn.sinjinsong.eshop.common.domain.entity.user.RoleDO(1,'ROLE_ADMIN'))) or (returnObject.username ==  principal.username)")
    @ApiOperation(value = "按某属性查询用户", notes = "属性可以是id或username或email或手机号", response = UserDO.class, authorizations = {@Authorization("登录权限")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "未登录"),
            @ApiResponse(code = 404, message = "查询模式未找到"),
            @ApiResponse(code = 403, message = "只有管理员或用户自己能查询自己的用户信息"),
    })
    public UserDO findByKey(@PathVariable("key") @ApiParam(value = "查询关键字", required = true) String key, @RequestParam("mode") @ApiParam(value = "查询模式，可以是id或username或phone或email", required = true) String mode) {
        QueryUserHandler handler = SpringContextUtil.getBean("QueryUserHandler", StringUtils.lowerCase(mode));
        if (handler == null) {
            throw new QueryUserModeNotFoundException(mode);
        }
        UserDO userDO = handler.handle(key);
        if (userDO == null) {
            throw new UserNotFoundException(key);
        }
        userDO.setPassword(null);
        return userDO;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", response = UserDO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "用户名已存在"),
            @ApiResponse(code = 400, message = "用户属性校验失败")
    })
    public UserDO createUser(@RequestBody @Valid @ApiParam(value = "用户信息，用户的用户名、密码、昵称、邮箱不可为空", required = true) UserDO user, BindingResult result) {
        log.info("{}", user);
        if (isUsernameDuplicated(user.getUsername())) {
            throw new UsernameExistedException(user.getUsername());
        } else if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        } else if (user.getPassword() == null) {
            throw new PasswordNotFoundException();
        }
        service.save(user);
        return user;
    }

    @RequestMapping(value = "/{id}/mail_validation", method = RequestMethod.POST)
    @ApiOperation(value = "为用户发送验证邮件，等待用户激活，若24小时内未激活需要重新注册")
    public void sendActivationMail(@PathVariable("id") Long id) {
        UserDO user = service.findById(id);
        if (user == null) {
            throw new UserNotFoundException(String.valueOf(id));
        }
        if (user.getUserStatus() != UserStatus.UNACTIVATED) {
            throw new UserStatusInvalidException(user.getUserStatus().toString());
        }
        //生成邮箱的激活码
        String activationCode = UUIDUtil.uuid();
        //发送验证邮件
        verificationManager.createVerificationCode(activationCode, String.valueOf(id), authenticationProperties.getActivationCodeExpireTime());
        log.info("{}     {}", user.getEmail(), user.getId());
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());
        params.put("activationCode", activationCode);
        emailService.sendHTML(user.getEmail(), "activation", params, null);
    }

    @RequestMapping(value = "/{id}/activation", method = RequestMethod.POST)
    @ApiOperation(value = "用户激活，前置条件是用户已注册且在24小时内", response = UserDO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "未注册或超时或激活码错误")
    })
    public UserDO activate(@PathVariable("id") @ApiParam(value = "用户id", required = true) Long id, @RequestParam("activationCode") @ApiParam(value = "激活码", required = true) String activationCode) {
        UserDO user = service.findById(id);
        if (user == null) {
            throw new UserNotFoundException(String.valueOf(id));
        }
        //获取Redis中的验证码
        if (!verificationManager.checkVerificationCode(activationCode, String.valueOf(id))) {
            verificationManager.deleteVerificationCode(activationCode);
            throw new ActivationCodeValidationException(activationCode);
        }
        user.setUserStatus(UserStatus.ACTIVATED);
        verificationManager.deleteVerificationCode(activationCode);
        service.update(user);
        return user;
    }

    // 更新
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("#user.id == principal.id or hasRole('ADMIN')")
    @ApiOperation(value = "更新用户信息", authorizations = {@Authorization("登录权限")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "未登录"),
            @ApiResponse(code = 404, message = "用户属性校验失败"),
            @ApiResponse(code = 403, message = "只有管理员或用户自己能更新用户信息"),

    })
    public void updateUser(@RequestBody @Valid @ApiParam(value = "用户信息，用户的用户名、密码、昵称、邮箱不可为空", required = true) UserDO user, BindingResult result) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        service.update(user);
    }

    @RequestMapping(value = "/{key}/password/reset_validation", method = RequestMethod.POST)
    @ApiOperation(value = "发送忘记密码的邮箱验证", notes = "属性可以是id,sername或email或手机号", response = UserDO.class)
    public void forgetPassword(@PathVariable("key") @ApiParam(value = "关键字", required = true) String key, @RequestParam("mode") @ApiParam(value = "验证模式，可以是username或phone或email", required = true) String mode) {
        UserDO user = findByKey(key, mode);
        if (user == null) {
            throw new UserNotFoundException(key);
        }
        //user 一定不为空
        String forgetPasswordCode = UUIDUtil.uuid();
        verificationManager.createVerificationCode(forgetPasswordCode, String.valueOf(user.getId()), authenticationProperties.getForgetNameCodeExpireTime());
        log.info("{}   {}", user.getEmail(), user.getId());
        //发送邮件
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());
        params.put("validationCode", forgetPasswordCode);
        emailService.sendHTML(user.getEmail(), "forgetPassword", params, null);
    }


    @RequestMapping(value = "/{id}/password", method = RequestMethod.PUT)
    @ApiOperation(value = "忘记密码后可以修改密码", response = UserDO.class)
    public UserDO resetPassword(@PathVariable("id") Long id, @RequestBody @Valid ResetPasswordDTO dto, BindingResult result, @AuthenticationPrincipal JWTUser user) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        String validationCode = dto.getValidationCode();
        //获取Redis中的验证码
        if (!verificationManager.checkVerificationCode(validationCode, String.valueOf(id))) {
            verificationManager.deleteVerificationCode(validationCode);
            throw new ActivationCodeValidationException(validationCode);
        }
        tokenManager.deleteToken(user.getUsername());
        verificationManager.deleteVerificationCode(validationCode);
        service.resetPassword(id,user.getUsername(), dto.getPassword());
        return service.findById(id);
    }

    @RequestMapping(value = "/{username}/duplication", method = RequestMethod.GET)
    @ApiOperation(value = "查询用户名是否重复", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code = 401, message = "未登录")})
    public boolean isUsernameDuplicated(@PathVariable("username") String username) {
        if (service.findByUsername(username) == null) {
            return false;
        }
        return true;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "分页查询用户信息", response = PageInfo.class, authorizations = {@Authorization("登录权限")})
    @ApiResponses(value = {@ApiResponse(code = 401, message = "未登录")})
    public PageInfo<UserDO> findAllUsers(@RequestParam(value = "pageNum", required = false, defaultValue = PageProperties.DEFAULT_PAGE_NUM) @ApiParam(value = "页码，从1开始", defaultValue = PageProperties.DEFAULT_PAGE_NUM) Integer pageNum, @RequestParam(value = "pageSize", required = false, defaultValue = PageProperties.DEFAULT_PAGE_SIZE) @ApiParam(value = "每页记录数", defaultValue = PageProperties.DEFAULT_PAGE_SIZE) Integer pageSize) {
        return service.findAll(pageNum, pageSize);
    }

    @RequestMapping(value = "/query/fuzzy/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "按用户名模糊查询用户信息", response = UserDO.class, authorizations = {@Authorization("登录权限")})
    public List<UserDO> findUserIdAndNamesContaining(@PathVariable("username") String username) {
        return service.findIdAndNameByUsernameContaining(username);
    }
}
