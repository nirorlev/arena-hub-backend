package com.threeatom.common.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @author PC
 * @title: ConnectionPoolWarmUp
 * @projectName code-generator
 * @description: TODO
 * @date 2024/1/2516:34
 */
@Component
public class ConnectionPoolWarmUp {
    @Autowired
    private DataSource dataSource;  // 注入数据源

    @PostConstruct
    public void warmUpConnectionPool() {
        // 在后台线程中创建并放入连接池的代码
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    dataSource.getConnection();  // 获取连接
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
