package cn.sinjinsong.common.web.security.token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/4/27.
 */
public enum TokenState {
    VALID,
    EXPIRED,
    INVALID,
    NOT_FOUND;
    private static final Map<String, TokenState> stringToEnum = new HashMap<>();

    static {
        for (TokenState type : values()) {
            stringToEnum.put(type.toString(), type);
        }
    }

    public static TokenState fromString(String type) {
        if (!stringToEnum.containsKey(type)) {
            return null;
        }
        return stringToEnum.get(type);
    }
}
