package org.makechtec.software.sql_support.connection_pool;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.connection_pool.mysql.MySQLPooledConnection;
import org.makechtec.software.sql_support.connection_pool.mysql.MySQLPooledConnectionCreator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WithPoolEngineTest {

    @Test
    void run() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        var pool = new ConnectionPool(4, () -> new MySQLPooledConnection(connectionMock()));

        pool.boot();

        record Dto(int id, String name) {}

        var result =
            (new WithPoolEngine<Dto>(pool))
                    .queryString("SELECT * FROM test")
                    .run(resultSet -> {
                        resultSet.next();

                        return new Dto(resultSet.getInt("id"), resultSet.getString("name"));
                    });

        assertEquals(1, result.id);
        assertEquals("john", result.name);

    }

    private Connection connectionMock(){
        var connectionMock = mock(Connection.class);

        try {
            var statementMock = mock(Statement.class);

            var resultSetMock = mock(ResultSet.class);

            when(resultSetMock.getInt(anyInt()))
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1);

            when(resultSetMock.getInt(anyString()))
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1)
                    .thenReturn(1);

            when(resultSetMock.getString(anyString()))
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john")
                    .thenReturn("john");

            when(statementMock.executeQuery(anyString()))
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock);

            when(statementMock.getResultSet())
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock)
                    .thenReturn(resultSetMock);

            when(connectionMock.createStatement())
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock)
                    .thenReturn(statementMock);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connectionMock;
    }

}