package org.makechtec.software.sql_support.postgres;

import org.makechtec.software.sql_support.ConnectionInformation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PostgresConnectionFactory {

    private static final Logger LOG = Logger.getLogger(PostgresConnectionFactory.class.getName());

    public Connection openNewConnection(ConnectionInformation connectionInformation) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        Connection successConnection;

        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (ClassNotFoundException e) {
            LOG.severe("Couldn't find org.postgresql.Driver when opening new PostgreSQL connection using org.makechtec.software.sql_support.postgres.PostgresConnectionFactory");
            throw e;
        } catch (InstantiationException e) {
            LOG.severe("Couldn't instanciate org.postgresql.Driver when opening new PostgreSQL connection using org.makechtec.software.sql_support.postgres.PostgresConnectionFactory");
            throw e;
        } catch (IllegalAccessException e) {
            LOG.severe("Couldn't access org.postgresql.Driver when opening new PostgreSQL connection using org.makechtec.software.sql_support.postgres.PostgresConnectionFactory");
            throw e;
        }

        var url = "jdbc:postgresql://" +
                connectionInformation.hostname() +
                ":" + connectionInformation.port() +
                "/" + connectionInformation.database();

        try {
            successConnection = DriverManager.getConnection(url, connectionInformation.user(), connectionInformation.password());
        } catch (SQLException e) {
            LOG.severe("There was an SQLException when opening new connection");
            LOG.severe("It has been possibly by wrong user credentials, wrong permissions over database or bad host, port information");
            LOG.severe("please, let's try to check this.");
            throw e;
        }

        return successConnection;
    }

}
