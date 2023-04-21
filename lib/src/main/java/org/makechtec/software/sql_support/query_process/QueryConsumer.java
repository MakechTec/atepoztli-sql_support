package org.makechtec.software.sql_support.query_process;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryConsumer {
    void consumeResultSet(ResultSet resultSet) throws SQLException;

}
