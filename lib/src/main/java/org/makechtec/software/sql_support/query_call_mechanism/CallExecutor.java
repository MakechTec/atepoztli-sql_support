package org.makechtec.software.sql_support.query_call_mechanism;

import lombok.Getter;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.SQLSupport;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CallExecutor<P> {

    private final ConnectionInformation connectionInformation;
    private final StatementInformation statementInformation;
    @Getter
    private List<String> errorMessages;

    public CallExecutor(ConnectionInformation connectionInformation, StatementInformation statementInformation) {
        this.connectionInformation = connectionInformation;
        this.statementInformation = statementInformation;
    }

    public Optional<P> execute( ProducerByCall<P> producer ){
        this.errorMessages = new ArrayList<>();

        var support = new SQLSupport(connectionInformation);

        Optional<P> response = Optional.empty();

        Wrapper<P> wrapper = new Wrapper<>();

        support.runSQLQuery( connection -> {
            if(statementInformation.isPrepared()){
                var preparedStatement = this.createPreparedStatement( statementInformation, connection);

                preparedStatement.execute();

                var resultSet = preparedStatement.getResultSet();

                wrapper.reservedSpace = producer.produce(resultSet);

                resultSet.close();
                preparedStatement.close();

            }
            else{
                var statement = connection.createStatement();

                statement.execute(statementInformation.getQueryString());

                var resultSet = statement.getResultSet();

                wrapper.reservedSpace = producer.produce(resultSet);

                resultSet.close();
                statement.close();
            }
        });

        this.errorMessages = support.getErrorMessages();

        response = Optional.of(wrapper.reservedSpace);
        return response;
    }

    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection) throws SQLException {
        var statement = connection.prepareStatement(statementInformation.getQueryString());

        statementInformation.getParams().forEach( param -> {

            try{
                switch (param.type()) {
                    case TYPE_STRING -> statement.setString(param.position(), (String) param.value());
                    case TYPE_INTEGER -> statement.setInt(param.position(), (int) param.value());
                    case TYPE_FLOAT -> statement.setFloat(param.position(), (float) param.value());
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }

        });

        return statement;
    }

    private static class Wrapper<P>{
        public P reservedSpace;
    }

}
