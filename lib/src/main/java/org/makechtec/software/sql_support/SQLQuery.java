package org.makechtec.software.sql_support;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLQuery {

    void execute(Connection connection) throws SQLException;

}
