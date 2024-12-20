package org.makechtec.software.sql_support;

import lombok.Getter;
import lombok.extern.java.Log;
import org.makechtec.software.sql_support.commons.properties.PropertyLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Log
public class SQLSupport {

    private static final Logger LOG = Logger.getLogger(SQLSupport.class.getName());
    private final ConnectionInformation connectionInformation;
    private String connectionURL;
    @Getter
    private List<String> errorMessages;

    public SQLSupport() {
        errorMessages = new ArrayList<>();
        this.connectionInformation = this.connectionFromFile("sqlconnection.properties");
    }

    public SQLSupport(String connectionPropertiesFile) {
        errorMessages = new ArrayList<>();
        this.connectionInformation = this.connectionFromFile(connectionPropertiesFile);
    }

    public SQLSupport(ConnectionInformation connectionInformation) {
        errorMessages = new ArrayList<>();
        this.connectionInformation = connectionInformation;
    }

    public void runSQLQuery(SQLQuery query) {

        errorMessages = new ArrayList<>();

        var connection = this.createConnection();

        if (connection.isEmpty()) {
            errorMessages.add("No connected to the SQL Server for URL: " + connectionURL);
            showErrorMessages();
            return;
        }

        try {
            Connection verfiedConnection = connection.get();
            query.execute(verfiedConnection);
            verfiedConnection.close();
        } catch (SQLException e) {
            var queryError = "Error in SQL Query: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(queryError);
            showErrorMessages();
        }

    }

    public void runSQLTransaction(SQLQuery query) {

        errorMessages = new ArrayList<>();
        var connection = this.createConnection();

        if (connection.isEmpty()) {
            errorMessages.add("No connected to the SQL Server for URL: " + connectionURL);
            showErrorMessages();
            return;
        }

        LOG.info("Connected by url: " + connectionURL);

        try {

            var verfiedConnection = connection.get();
            verfiedConnection.setAutoCommit(false);
            query.execute(verfiedConnection);
            verfiedConnection.commit();
            verfiedConnection.close();
        } catch (SQLException e) {
            var queryError = "Error in SQL Query: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(queryError);
            showErrorMessages();
        }
    }

    private Optional<Connection> createConnection() {

        Optional<Connection> connection = Optional.empty();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            var url = "jdbc:mysql://" +
                    connectionInformation.hostname() +
                    ":" + connectionInformation.port() +
                    "/" + connectionInformation.database();

            this.connectionURL = url;
            var successConnection = DriverManager.getConnection(url, connectionInformation.user(), connectionInformation.password());

            connection = Optional.of(successConnection);
        } catch (SQLException e) {

            var connectionError = "Error in SQL connection: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(connectionError);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.warning("There was an error creating the connection to MySQL server: " + e.getMessage());
        }

        return connection;

    }

    private void showErrorMessages() {
        errorMessages.forEach(LOG::severe);
    }

    private ConnectionInformation connectionFromFile(String propertiesFile) {
        var propertyLoader = new PropertyLoader(propertiesFile);

        var host = propertyLoader.getProperty("db_hostname");
        var port = propertyLoader.getProperty("db_port");
        var dbname = propertyLoader.getProperty("db_name");
        var user = propertyLoader.getProperty("db_user");
        var password = propertyLoader.getProperty("db_password");

        return new ConnectionInformation(
                user.orElse(""),
                password.orElse(""),
                host.orElse(""),
                port.orElse(""),
                dbname.orElse("")
        );

    }

}
