package cn.sinjinsong.eshop.common.enumeration.product;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/10/6.
 */
public enum ProductType {
    PRIMARY(0, "初级"), MIDDLE(1, "中级"), HIGH(2, "高级");
    private int code;
    private String desc;

    ProductType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, ProductType> map = new HashMap<>();

    static {
        for (ProductType status : values()) {
            map.put(status.code, status);
        }
    }

    public static ProductType getByCode(int code) {
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
