package cn.sinjinsong.eshop.common.config.db;

import cn.sinjinsong.eshop.common.condition.DBCondition;
import cn.sinjinsong.eshop.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by SinjinSong on 2017/8/20.
 * 事务管理器
 */
@Configuration
@EnableTransactionManagement(order = 3)
@Slf4j
public class MyDataSourceTransactionManagerAutoConfig extends DataSourceTransactionManagerAutoConfiguration {
    /**
     * 自定义事务
     * MyBatis自动参与到spring事务管理中，无需额外配置，只要org.mybatis.spring.SqlSessionFactoryBean引用的数据源与DataSourceTransactionManager引用的数据源一致即可，否则事务管理会不起作用。
     * @return
     */
    @Bean(name = "transactionManager")
    @Conditional(DBCondition.class)
    public DataSourceTransactionManager transactionManagers() {
        log.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(SpringContextUtil.getBean("roundRobinDataSourceProxy"));
    }
}
