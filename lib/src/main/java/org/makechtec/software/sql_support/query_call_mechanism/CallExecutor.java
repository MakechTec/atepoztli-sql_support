package org.makechtec.software.sql_support.query_call_mechanism;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.SQLSupport;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class CallExecutor<P> {

    private final ConnectionInformation connectionInformation;
    private final StatementInformation statementInformation;

    public CallExecutor(ConnectionInformation connectionInformation, StatementInformation statementInformation) {
        this.connectionInformation = connectionInformation;
        this.statementInformation = statementInformation;
    }

    public P execute(ProducerByCall<P> producer) {

        var support = new SQLSupport(connectionInformation);

        Wrapper<P> wrapper = new Wrapper<>();

        support.runSQLQuery(connection -> {
            if (statementInformation.isPrepared()) {
                var preparedStatement = this.createPreparedStatement(statementInformation, connection);

                preparedStatement.execute();

                var resultSet = preparedStatement.getResultSet();

                wrapper.reservedSpace = producer.produce(resultSet);

                resultSet.close();
                preparedStatement.close();

            } else {
                var statement = connection.createStatement();

                statement.execute(statementInformation.getQueryString());

                var resultSet = statement.getResultSet();

                wrapper.reservedSpace = producer.produce(resultSet);

                resultSet.close();
                statement.close();
            }
        });


        return wrapper.reservedSpace;
    }

    public void update() {

        var support = new SQLSupport(connectionInformation);

        support.runSQLQuery(connection -> {
            if (statementInformation.isPrepared()) {
                var preparedStatement = this.createPreparedStatement(statementInformation, connection);

                preparedStatement.executeUpdate();

                preparedStatement.close();

            } else {
                var statement = connection.createStatement();

                statement.executeUpdate(statementInformation.getQueryString());

                statement.close();
            }
        });

    }

    public long updateWithGeneratedKey(ProducerByCall<Long> producer) {
        var support = new SQLSupport(connectionInformation);

        var wrapper = new Wrapper<Long>();

        support.runSQLQuery(connection -> {
            if (statementInformation.isPrepared()) {
                var preparedStatement = this.createPreparedStatement(statementInformation, connection);

                preparedStatement.executeUpdate();

                wrapper.reservedSpace = producer.produce(preparedStatement.getGeneratedKeys());

                preparedStatement.close();

            } else {
                var statement = connection.createStatement();

                statement.executeUpdate(statementInformation.getQueryString());

                wrapper.reservedSpace = producer.produce(statement.getGeneratedKeys());

                statement.close();
            }
        });

        return wrapper.reservedSpace;
    }

    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection) throws SQLException {
        var statement = connection.prepareStatement(statementInformation.getQueryString());

        statementInformation.getParams().forEach(param -> {

            try {
                switch (param.type()) {
                    case TYPE_STRING -> statement.setString(param.position(), (String) param.value());
                    case TYPE_INTEGER -> statement.setInt(param.position(), (int) param.value());
                    case TYPE_FLOAT -> statement.setFloat(param.position(), (float) param.value());
                    case TYPE_LONG -> statement.setLong(param.position(), (long) param.value());
                    case TYPE_BIG_DECIMAL -> statement.setBigDecimal(param.position(), (BigDecimal) param.value());
                    case TYPE_DOUBLE -> statement.setDouble(param.position(), (double) param.value());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        return statement;
    }

    private static class Wrapper<P> {
        public P reservedSpace;
    }

}
