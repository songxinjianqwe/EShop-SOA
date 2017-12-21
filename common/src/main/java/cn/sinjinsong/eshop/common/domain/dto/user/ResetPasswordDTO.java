package cn.sinjinsong.eshop.common.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Created by SinjinSong on 2017/10/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    @NotNull
    private String validationCode;
    @NotNull
    private String password;
}
