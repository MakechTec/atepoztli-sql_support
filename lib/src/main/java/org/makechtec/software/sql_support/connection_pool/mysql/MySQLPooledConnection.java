package org.makechtec.software.sql_support.connection_pool.mysql;


import org.makechtec.software.sql_support.connection_pool.PooledConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public record MySQLPooledConnection(Connection nativeConnection) implements PooledConnection {

    private static final String LIGHTWEIGHT_QUERY = "SELECT 1 FROM mysql.users";


    @Override
    public boolean isUsable() {

        try {
            var statement = nativeConnection.createStatement();
            var resultSet = statement.executeQuery(LIGHTWEIGHT_QUERY);

            resultSet.next();

            var result = resultSet.getInt(1);

            return result == 1;
        } catch (SQLException e) {
            return false;
        }

    }

    @Override
    public void close() throws SQLException {
        this.nativeConnection.close();
    }


}
