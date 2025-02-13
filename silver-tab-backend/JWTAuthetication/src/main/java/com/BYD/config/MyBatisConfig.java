package com.BYD.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.BYD.mapper")
public class MyBatisConfig {

    /**
     * Configures and provides a SqlSessionFactory instance that MyBatis uses to create sessions and interact with the database
     * @param dataSource
     * @return SqlSessionFactory instance
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        // Additional config of MyBatis for XML file configL
        Resource[] resources = new Resource[] {
                new ClassPathResource("mappers/StockMapper.xml"),
                new ClassPathResource("mappers/StockResultMap.xml"),
                new ClassPathResource("mappers/DealerMapper.xml"),
                new ClassPathResource("mappers/DealerResultMap.xml"),
                new ClassPathResource("mappers/DealerImageMapper.xml"),
                new ClassPathResource("mappers/DealerImageResultMap.xml"),
                new ClassPathResource("mappers/UserAccessRegionMapper.xml"),
                new ClassPathResource("mappers/UserMapper.xml"),
                new ClassPathResource("mappers/UserResultMap.xml"),
                new ClassPathResource("mappers/UserSessionMapper.xml"),
                new ClassPathResource("mappers/UserSessionResultMap.xml"),
                new ClassPathResource("mappers/UserProfileMapper.xml"),
        };
        sessionFactoryBean.setMapperLocations(resources);
        SqlSessionFactory sqlSessionFactory = sessionFactoryBean.getObject();
        assert sqlSessionFactory != null;
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);

        return sqlSessionFactory;
    }

    /**
     * Creates a manager that will handle transaction management for database operations
     * @param dataSource
     * @return DataSourceTransactionManager object to handle transactions
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}