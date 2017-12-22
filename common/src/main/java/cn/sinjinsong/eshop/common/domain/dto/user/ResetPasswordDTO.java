package cn.sinjinsong.eshop.common.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/10/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO implements Serializable{
    @NotNull
    private String validationCode;
    @NotNull
    private String password;
}
