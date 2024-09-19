package org.makechtec.software.sql_support.connection_pool;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.connection_pool.mysql.MySQLPooledConnection;
import org.makechtec.software.sql_support.connection_pool.mysql.MySQLPooledConnectionCreator;
import org.makechtec.software.sql_support.mysql.MysqlEngine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WithPoolEngineTest {

    @Test
    void run() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        var pool = new ConnectionPool(4, () -> new MySQLPooledConnection(connectionMock()));

        pool.boot();

        record Dto(int id, String name) {
        }

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

    private Connection connectionMock() {
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

    @Test
    void realConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        if(Objects.isNull(System.getenv("isRealConnection")) || !System.getenv("isRealConnection").equals("true")) {
            System.out.print("Disabled realConnection() test because is not enabled the isRealConnection env property to true");
            return;
        }

        var connectionInformation = new ConnectionInformation(
                "test",
                "test",
                "localhost",
                "3306",
                "test"
        );

        pooledConnection(connectionInformation);
        normalConnection(connectionInformation);
    }

    private static void pooledConnection(ConnectionInformation connectionInformation) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        var pool = new ConnectionPool(4, new MySQLPooledConnectionCreator(connectionInformation));

        pool.boot();

        var initialTime = System.currentTimeMillis();

        var result =
                (new WithPoolEngine<Dto>(pool))
                        .queryString("SELECT id, name FROM test")
                        .run(resultSet -> {
                            resultSet.next();

                            return new Dto(resultSet.getInt("id"), resultSet.getString("name"));
                        });

        repeatPooled(6, pool);

        var endTime = System.currentTimeMillis();

        System.out.println("taked time: "+ (endTime - initialTime));

        assertEquals(1, result.id);
        assertEquals("john", result.name);
    }

    private static void repeatPooled(int times, ConnectionPool pool) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (int i = 0; i < times; i++) {
            (new WithPoolEngine<Dto>(pool))
                    .queryString("SELECT id, name FROM test")
                    .run(resultSet -> {
                        resultSet.next();

                        return new Dto(resultSet.getInt("id"), resultSet.getString("name"));
                    });
        }

    }

    private static void normalConnection(ConnectionInformation connectionInformation){


        var initialTime = System.currentTimeMillis();

        var result =
            (new MysqlEngine<Dto>(connectionInformation))
                    .queryString("SELECT id, name FROM test")
                    .run(resultSet -> {
                        resultSet.next();

                        return new Dto(resultSet.getInt("id"), resultSet.getString("name"));
                    });

        repeatNormal(6, connectionInformation);

        var endTime = System.currentTimeMillis();

        System.out.println("taked time: "+ (endTime - initialTime));

        assertEquals(1, result.id);
        assertEquals("john", result.name);

    }

    private static void repeatNormal(int times, ConnectionInformation connectionInformation){
        for (int i = 0; i < times; i++) {
            (new MysqlEngine<Dto>(connectionInformation))
                    .queryString("SELECT id, name FROM test")
                    .run(resultSet -> {
                        resultSet.next();

                        return new Dto(resultSet.getInt("id"), resultSet.getString("name"));
                    });
        }
    }

    record Dto(int id, String name) {}


}