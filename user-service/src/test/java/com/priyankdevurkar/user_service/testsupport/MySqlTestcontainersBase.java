package com.priyankdevurkar.user_service.testsupport;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mysql.MySQLContainer;


public abstract class MySqlTestcontainersBase {

    @ServiceConnection
    @Container
    static MySQLContainer mysql = new MySQLContainer("mysql:8.4")
            .withDatabaseName("smart_power_monitor")
            .withUsername("root")
            .withPassword("password");

}
