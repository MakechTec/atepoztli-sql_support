package org.makechtec.software.sql_support.connection_pool.mysql;


import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.connection_pool.PooledConnection;
import org.makechtec.software.sql_support.connection_pool.PooledConnectionCreator;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLPooledConnectionCreator implements PooledConnectionCreator {

    private final ConnectionInformation connectionInformation;

    public MySQLPooledConnectionCreator(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
    }

    @Override
    public PooledConnection create() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

        var url = "jdbc:mysql://" +
                connectionInformation.hostname() +
                ":" + connectionInformation.port() +
                "/" + connectionInformation.database();

        var successConnection = DriverManager.getConnection(url, connectionInformation.user(), connectionInformation.password());

        return new MySQLPooledConnection(successConnection);
    }
}
