package cn.sinjinsong.eshop.common.enumeration;

import lombok.Getter;

/**
 * Created by SinjinSong on 2017/8/20.
 */
@Getter
public enum DataSourceType {
    read("read", "从库"), write("write", "主库");
    private String type;
    private String name;

    DataSourceType(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
