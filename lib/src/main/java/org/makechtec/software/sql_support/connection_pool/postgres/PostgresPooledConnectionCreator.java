package org.makechtec.software.sql_support.connection_pool.postgres;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.connection_pool.PooledConnection;
import org.makechtec.software.sql_support.connection_pool.PooledConnectionCreator;
import org.makechtec.software.sql_support.postgres.PostgresConnectionFactory;

import java.sql.SQLException;

public class PostgresPooledConnectionCreator implements PooledConnectionCreator {

    private final ConnectionInformation connectionInformation;
    private final PostgresConnectionFactory connectionFactory;

    public PostgresPooledConnectionCreator(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
        this.connectionFactory = new PostgresConnectionFactory();
    }

    @Override
    public PooledConnection create() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new PostgresPooledConnection(
                this.connectionFactory.openNewConnection(connectionInformation)
        );
    }
}
