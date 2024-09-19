package org.makechtec.software.sql_support.connection_pool;

import java.sql.SQLException;

@FunctionalInterface
public interface PooledConnectionCreator {

    PooledConnection create() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;

}
