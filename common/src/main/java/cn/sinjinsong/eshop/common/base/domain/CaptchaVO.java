package cn.sinjinsong.eshop.common.base.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by SinjinSong on 2017/10/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    private String image;
    private String captchaCode;
}
