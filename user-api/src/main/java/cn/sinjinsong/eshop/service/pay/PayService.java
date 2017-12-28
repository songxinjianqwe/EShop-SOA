package cn.sinjinsong.eshop.service.pay;


/**
 * 包含支付、充值和重置支付密码
 * Created by SinjinSong on 2017/10/7.
 */
public interface PayService {
    void deposit(Long userId, Integer amount);
    void setPaymentPassword(Long userId, String oldPaymentPassword, String newPaymentPassword);
    void decreaseAccount(Long userId, Double totalPrice, String paymentPassword);
    void increaseAccount(Long userId,Double totalPrice);
}
