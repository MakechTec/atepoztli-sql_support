package org.makechtec.software.sql_support.query_process;

import lombok.Getter;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.SQLSupport;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryCaller {

    private final ConnectionInformation connectionInformation;
    private final StatementInformation statementInformation;
    @Getter
    private List<String> errorMessages;

    public QueryCaller(ConnectionInformation connectionInformation, StatementInformation statementInformation) {
        this.connectionInformation = connectionInformation;
        this.statementInformation = statementInformation;
        this.errorMessages = new ArrayList<>();
    }

    public void call(QueryConsumer consumer){

        this.errorMessages = new ArrayList<>();

        var support = new SQLSupport(connectionInformation);

        support.runSQLQuery( connection -> {
            if(statementInformation.isPrepared()){
                var preparedStatement = this.createPreparedStatement( statementInformation, connection);

                preparedStatement.execute();

                var resultSet = preparedStatement.getResultSet();

                consumer.consumeResultSet(resultSet);

                resultSet.close();
                preparedStatement.close();

            }
            else{
                var statement = connection.createStatement();

                statement.execute(statementInformation.getQueryString());

                var resultSet = statement.getResultSet();

                consumer.consumeResultSet(resultSet);

                resultSet.close();
                statement.close();
            }
        });

        this.errorMessages = support.getErrorMessages();

    }

    public void callUpdate(){

        this.errorMessages = new ArrayList<>();

        var support = new SQLSupport(connectionInformation);

        support.runSQLQuery( connection -> {
            if(statementInformation.isPrepared()){
                var preparedStatement = this.createPreparedStatement( statementInformation, connection);

                preparedStatement.executeUpdate();

                preparedStatement.close();

            }
            else{
                var statement = connection.createStatement();

                statement.executeUpdate(statementInformation.getQueryString());

                statement.close();
            }
        });

        this.errorMessages = support.getErrorMessages();
    }

    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection) throws SQLException {
        var statement = connection.prepareStatement(statementInformation.getQueryString());

        statementInformation.getParams().forEach( param -> {

            try{
                switch (param.type()) {
                    case TYPE_STRING -> statement.setString(param.position(), (String) param.value());
                    case TYPE_INTEGER -> statement.setInt(param.position(), (int) param.value());
                    case TYPE_FLOAT -> statement.setFloat(param.position(), (float) param.value());
                    case TYPE_LONG -> statement.setLong(param.position(), (long) param.value());
                    case TYPE_BIG_DECIMAL -> statement.setBigDecimal(param.position(), (BigDecimal) param.value());
                    case TYPE_DOUBLE -> statement.setDouble(param.position(), (double) param.value());
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }

        });

        return statement;
    }

}
