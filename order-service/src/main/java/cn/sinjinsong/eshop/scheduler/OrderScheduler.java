package cn.sinjinsong.eshop.scheduler;


import cn.sinjinsong.eshop.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 业务相关的作业调度
 * 1)cron
 * 字段               允许值                           允许的特殊字符
 * 秒	 	0-59	 	, - * /
 * 分	 	0-59	 	, - * /
 * 小时	 	0-23	 	, - * /
 * 日期	 	1-31	 	, - * ? / L W C
 * 月份	 	1-12 或者 JAN-DEC	 	, - * /
 * 星期	 	1-7 或者 SUN-SAT	 	, - * ? / L C #
 * 年（可选）	 	留空, 1970-2099	 	, - * /
 * <p>
 * "*"字符代表所有可能的值
 * "?"字符仅被用于天（月）和天（星期）两个子表达式，表示不指定值
 * "/"  字符用来指定数值的增量
 * "L"  字符仅被用于天（月）和天（星期）两个子表达式，表示一个月的最后一天或者一个星期的最后一天
 * 6L 可以表示倒数第６天
 * <p>
 * <p>
 * 示例：
 * "0 0 12 * * ?"           每天中午十二点触发
 * "0 15 10 ? * *"          每天早上10:15触发
 * "0 15 10 * * ?"          每天早上10:15触发
 * "0 15 10 * * ? *"        每天早上10:15触发
 * "0 15 10 * * ? 2005"     2005年的每天早上10:15触发
 * "0 * 14 * * ?"           每天从下午2点开始到2点59分每分钟一次触发
 * "0 0/5 14 * * ?"         每天从下午2点开始到2:55分结束每5分钟一次触发
 * "0 0/5 14,18 * * ?"      每天下午2点至2:55和6点至6点55分两个时间段内每5分钟一次触发
 * "0 0-5 14 * * ?"         每天14:00至14:05每分钟一次触发
 * "0 10,44 14 ? 3 WED"     三月的每周三的14:10和14:44触发
 * "0 15 10 ? * MON-FRI"    每个周一,周二,周三,周四,周五的10:15触发
 * "0 15 10 15 * ?"         每月15号的10:15触发
 * "0 15 10 L * ?"          每月的最后一天的10:15触发
 * "0 15 10 ? * 6L"         每月最后一个周五的10:15触发
 * "0 15 10 ? * 6#3"        每月的第三个周五的10:15触发
 * "0 0/5 * * * ?"          每五钟执行一次
 * <p>
 * <p>
 * 2)fixedRate：每隔多少毫秒执行一次该方法。如：
 * <p>
 * //@Scheduled(fixedRate = 2000)  // 每隔2秒执行一次
 * <p>
 * 3)fixedDelay：当一次方法执行完毕之后，延迟多少毫秒再执行该方法。
 * <p>
 * 4)@Scheduled(initialDelay=1000, fixedRate=5000)
 *
 * @Author SinjinSong
 */

@Component
@Slf4j
public class OrderScheduler {
    @Autowired
    private OrderService orderService;

    /**
     * 每隔1分钟将超时订单状态设置为超时
     */
//    @Scheduled(fixedRate = 60 * 1000)
    public void clearTimeOutOrders() {
        orderService.updateTimeOutOrders();
    }
}
