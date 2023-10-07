package org.makechtec.software.sql_support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ConnectionTest {

    @Test
    public void testSqlSupportRunSQLQuery() throws SQLException {


        try (var staticMock = Mockito.mockStatic(DriverManager.class)) {

            var fakeConnection = Mockito.mock(Connection.class);

            var url = "jdbc:mysql://" +
                    "testhost" +
                    ":" + "9000" +
                    "/" + "testdatabase";

            staticMock.when(() -> DriverManager.getConnection(url, "makechtestuser", "makechpass"))
                    .thenReturn(fakeConnection);


            Mockito.doNothing()
                    .when(fakeConnection)
                    .close();

            var sqlSupport = new SQLSupport();

            sqlSupport.runSQLQuery(connection -> assertEquals(fakeConnection, connection));

        }


    }


    @Test
    public void testSqlSupportRunSQLTransaction() throws SQLException {


        try (var staticMock = Mockito.mockStatic(DriverManager.class)) {

            var fakeConnection = Mockito.mock(Connection.class);

            var connectionInformation = new ConnectionInformation(
                    "customUser",
                    "customPassword",
                    "customHost",
                    "customPort",
                    "customDatabase"
            );

            var url = "jdbc:mysql://" +
                    "customHost" +
                    ":" + "customPort" +
                    "/" + "customDatabase";


            staticMock.when(() -> DriverManager.getConnection(url, "customUser", "customPassword"))
                    .thenReturn(fakeConnection);

            var sqlSupport = new SQLSupport(connectionInformation);

            Mockito.doNothing()
                    .when(fakeConnection)
                    .close();

            Mockito.doNothing()
                    .when(fakeConnection)
                    .setAutoCommit(false);

            Mockito.doNothing()
                    .when(fakeConnection)
                    .commit();

            sqlSupport.runSQLTransaction(connection -> assertEquals(fakeConnection, connection));

        }


    }

}
