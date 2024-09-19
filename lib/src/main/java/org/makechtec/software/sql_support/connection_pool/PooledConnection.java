package org.makechtec.software.sql_support.connection_pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface PooledConnection {

    boolean isUsable();

    Connection nativeConnection();

    void close() throws SQLException;

}
