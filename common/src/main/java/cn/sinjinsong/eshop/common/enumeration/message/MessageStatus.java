package cn.sinjinsong.eshop.common.enumeration.message;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
@Getter
public enum MessageStatus {
    UNCONSUMED(0, "未被消费"), CONSUMED(1, "已被消费"), OVER_CONFIRM_RETRY_TIME(2, "超过确认消息重试次数"),OVER_CONSUME_RETRY_TIME(3,"超过消费消息重试次数"),CONSUME_FAILED(4,"消费失败"),ROLLBACK(5,"已被回滚");
    private int code;
    private String desc;
    
    MessageStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    private static Map<Integer, MessageStatus> map = new HashMap<>();
    
    static {
        for (MessageStatus status : values()) {
            map.put(status.code, status);
        }
    }
    
    public static MessageStatus getByCode(int code) {
        return map.get(code);
    }
}
