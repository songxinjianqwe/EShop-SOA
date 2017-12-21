package cn.sinjinsong.eshop.common.domain.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Created by SinjinSong on 2017/10/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPasswordDTO {
    private String oldPaymentPassword;
    @NotNull
    private String newPaymentPassword;
}
