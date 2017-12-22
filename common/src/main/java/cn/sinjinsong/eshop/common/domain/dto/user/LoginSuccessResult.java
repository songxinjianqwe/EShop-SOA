package cn.sinjinsong.eshop.common.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/10/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResult implements Serializable{
    private Long id;
    private String username;
    private String token;
}
