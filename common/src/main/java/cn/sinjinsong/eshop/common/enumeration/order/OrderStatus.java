package cn.sinjinsong.eshop.common.enumeration.order;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/10/6.
 */
@Getter
public enum OrderStatus {
    UNPAID(0, "未付款"), PAID(1, "已付款"), TIME_OUT(2, "超时"), CANCELED(3, "取消"), PAYING(4, "正在付款"), PAY_FAILED(5, "付款失败");
    private int code;
    private String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, OrderStatus> map = new HashMap<>();

    static {
        for (OrderStatus status : values()) {
            map.put(status.code, status);
        }
    }

    public static OrderStatus getByCode(int code) {
        return map.get(code);
    }
}
