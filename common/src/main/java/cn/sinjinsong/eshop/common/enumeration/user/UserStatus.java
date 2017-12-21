package cn.sinjinsong.eshop.common.enumeration.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/4/28.
 */
public enum UserStatus {
    UNACTIVATED(0, "未激活"), ACTIVATED(1, "已激活"), FORBIDDEN(2, "已禁用");
    private int code;
    private String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, UserStatus> map = new HashMap<>();

    static {
        for (UserStatus status : values()) {
            map.put(status.code, status);
        }
    }

    public static UserStatus getByCode(int code) {
        return map.get(code);
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

}
