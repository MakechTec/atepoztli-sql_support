package org.makechtec.software.sql_support.connection_pool;

import java.sql.SQLException;

@FunctionalInterface
public interface PoolConnectionBorrower {

    void borrowConnection(PooledConnection pooledConnection) throws SQLException;

}
