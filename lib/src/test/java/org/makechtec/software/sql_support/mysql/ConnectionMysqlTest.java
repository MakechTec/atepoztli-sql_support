package org.makechtec.software.sql_support.mysql;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;

public class ConnectionMysqlTest {

    @Test
    public void testConnection() {
        (new MysqlEngine<Void>(new ConnectionInformation(
                "",
                "",
                "",
                "",
                ""
        )))
                .queryString("SELECT * FROM boms;")
                .run(resultSet -> {
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("id"));
                    }
                    return null;
                });
    }

}
