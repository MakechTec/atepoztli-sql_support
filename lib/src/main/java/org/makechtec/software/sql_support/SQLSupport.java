package org.makechtec.software.sql_support;

import lombok.Getter;
import lombok.extern.java.Log;
import org.makechtec.software.properties_loader.PropertyLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
public class SQLSupport {

    private final String connectionPropertiesFile;
    private String connectionURL;
    @Getter
    private List<String> errorMessages;

    public SQLSupport(){
        errorMessages = new ArrayList<>();
        connectionPropertiesFile = "sqlconnection.properties";
    }

    public SQLSupport(String connectionPropertiesFile){
        errorMessages = new ArrayList<>();
        this.connectionPropertiesFile = connectionPropertiesFile;
    }

    public void runSQLQuery(SQLQuery query){

        errorMessages = new ArrayList<>();

        var connection = this.createConnection();

        if(connection.isEmpty()){
            errorMessages.add("No connected to the SQL Server for URL: " + connectionURL);
            showErrorMessages();
            return;
        }

        try{
            Connection verfiedConnection = connection.get();
            query.execute(verfiedConnection);
            verfiedConnection.close();
        }
        catch (SQLException e) {
            var queryError = "Error in SQL Query: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(queryError);
            showErrorMessages();
        }

    }

    public void runSQLTransaction(SQLQuery query){

        errorMessages = new ArrayList<>();
        var connection = this.createConnection();

        if(connection.isEmpty()){
            errorMessages.add("No connected to the SQL Server for URL: " + connectionURL);
            showErrorMessages();
            return;
        }

        log.info("Connected by url: " + connectionURL);

        try{

            var verfiedConnection = connection.get();
            verfiedConnection.setAutoCommit(false);
            query.execute(verfiedConnection);
            verfiedConnection.commit();
            verfiedConnection.close();
        }
        catch (SQLException e) {
            var queryError = "Error in SQL Query: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(queryError);
            showErrorMessages();
        }
    }

    private Optional<Connection> createConnection(){

        Optional<Connection> connection = Optional.empty();

        try{
            var propertyLoader = new PropertyLoader(connectionPropertiesFile);

            var driver = propertyLoader.getProperty("driver");
            var host = propertyLoader.getProperty("host");
            var port = propertyLoader.getProperty("port");
            var dbname = propertyLoader.getProperty("dbname");
            var user = propertyLoader.getProperty("user");
            var password = propertyLoader.getProperty("password");

            var url = "jdbc:" + driver.orElse("") + "://" + host.orElse("") + ":" + port.orElse("") + "/" + dbname.orElse("");

            this.connectionURL = url;
            var successConnection = DriverManager.getConnection(url, user.orElse(""), password.orElse(""));

            connection = Optional.of(successConnection);
        }
        catch (SQLException e) {

            var connectionError = "Error in SQL connection: \n" +
                    "Code: " + " \n" +
                    e.getErrorCode() + " \n" +
                    "Message: " + " \n" +
                    e.getMessage();

            errorMessages.add(connectionError);
        }

        return connection;

    }

    private void showErrorMessages(){
        errorMessages.forEach(log::severe);
    }


}
