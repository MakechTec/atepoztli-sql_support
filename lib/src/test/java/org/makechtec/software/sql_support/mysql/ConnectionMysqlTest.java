package org.makechtec.software.sql_support.mysql;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;

import java.util.Objects;

public class ConnectionMysqlTest {

    @Test
    public void testConnection() {

        if(Objects.isNull(System.getenv("isRealConnection")) || !System.getenv("isRealConnection").equals("true")) {
            System.out.print("Disabled realConnection() test because is not enabled the isRealConnection env property to true");
            return;
        }

        (new MysqlEngine<Void>(new ConnectionInformation(
                "test",
                "test",
                "localhost",
                "3306",
                "test"
        )))
                .queryString("SELECT id FROM test;")
                .run(resultSet -> {
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("id"));
                    }
                    return null;
                });
    }

}
