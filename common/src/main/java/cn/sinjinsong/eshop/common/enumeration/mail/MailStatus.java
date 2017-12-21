package cn.sinjinsong.eshop.common.enumeration.mail;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/5/4.
 */
public enum MailStatus {
    NOT_VIEWED(0, "未查看"), VIEWED(1, "已查看"), ALL(2, "全部");
    private int code;
    private String desc;
    
    MailStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, MailStatus> map = new HashMap<>();

    static {
        for (MailStatus status : values()) {
            map.put(status.code, status);
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MailStatus getByCode(int code) {
        return map.get(code);
    }


}
