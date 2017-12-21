package cn.sinjinsong.eshop.common.base.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDTO {
    private String image;
    private String value;
}
