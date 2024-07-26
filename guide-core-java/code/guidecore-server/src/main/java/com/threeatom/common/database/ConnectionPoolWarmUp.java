package com.threeatom.common.database;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectionPoolWarmUp {
    @Autowired private DataSource dataSource;

    @PostConstruct
    public void warmUpConnectionPool() {
        // 在后台线程中创建并放入连接池的代码
        new Thread(
                        () -> {
                            for (int i = 0; i < 5; i++) {
                                try {
                                    dataSource.getConnection(); // 获取连接
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .start();
    }
}
