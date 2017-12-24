package cn.sinjinsong.eshop.common.config.db;

import cn.sinjinsong.eshop.common.condition.DBCondition;
import cn.sinjinsong.eshop.common.enumeration.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/8/20.
 * MyBatis与数据源交接
 */
@Configuration
@Slf4j
public class MyBatisConfig {

    @Value("${datasource.type:}")
    private Class<? extends DataSource> dataSourceType;
    
    @Value("${datasource.readSize:}")
    private String dataSourceSize;
    
    @Autowired(required = false)
    @Qualifier("writeDataSource")
    private DataSource dataSource;
    
    @Autowired(required = false)
    @Qualifier("readDataSources")
    private List<DataSource> readDataSources;
    
    @Value("${mybatis.mapper-locations:}")
    private String mapperLocations;
    
    @Value("${mybatis.config-location:}")
    private String configLocation;

    @Bean
    @Conditional(DBCondition.class)
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(roundRobinDataSourceProxy());
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(configLocation));
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 有多少个数据源就要配置多少个bean
     *
     * @return
     */
    @Bean
    @Conditional(DBCondition.class)
    public AbstractRoutingDataSource roundRobinDataSourceProxy() {
        int size = Integer.parseInt(dataSourceSize);
        MyAbstractRoutingDataSource proxy = new MyAbstractRoutingDataSource(size);
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 写  
        targetDataSources.put(DataSourceType.write.getType(), dataSource);
        //多个读数据库时  
        for (int i = 0; i < size; i++) {
            targetDataSources.put(i, readDataSources.get(i));
        }
        proxy.setDefaultTargetDataSource(dataSource);
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }

}