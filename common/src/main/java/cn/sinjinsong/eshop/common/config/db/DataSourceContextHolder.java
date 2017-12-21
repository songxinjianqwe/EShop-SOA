package cn.sinjinsong.eshop.common.config.db;


import cn.sinjinsong.eshop.common.enumeration.DataSourceType;

/**
 * Created by SinjinSong on 2017/8/20.
 * 当前线程的数据源Holder
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> local = new ThreadLocal<>();

    public static ThreadLocal<String> getLocal() {
        return local;
    }

    /**
     * 读可能是多个库
     */
    public static void read() {
        local.set(DataSourceType.read.getType());
    }

    /**
     * 写只有一个库
     */
    public static void write() {
        local.set(DataSourceType.write.getType());
    }

    public static String getJdbcType() {
        return local.get();
    }
}
