package cn.sinjinsong.eshop.common.domain.dto.user;


import cn.sinjinsong.eshop.common.enumeration.user.UserMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO implements Serializable{
    private String username;
    private String phone;
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String captchaCode;
    @NotNull
    private String captchaValue;
    @NotNull
    private UserMode userMode;
}
