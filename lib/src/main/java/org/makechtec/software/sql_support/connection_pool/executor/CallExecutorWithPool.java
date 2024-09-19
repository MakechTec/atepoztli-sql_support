package org.makechtec.software.sql_support.connection_pool.executor;

import org.makechtec.software.sql_support.connection_pool.ConnectionPool;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class CallExecutorWithPool<P> {
    private static final Logger LOG = Logger.getLogger(CallExecutorWithPool.class.getName());
    private final ConnectionPool pool;
    private final StatementInformation statementInformation;

    public CallExecutorWithPool(ConnectionPool pool, StatementInformation statementInformation) {
        this.pool = pool;
        this.statementInformation = statementInformation;
    }

    public P execute(ProducerByCall<P> producer) throws SQLException {

        CallExecutorWithPool.Wrapper<P> wrapper = new CallExecutorWithPool.Wrapper<>();

        pool.provide(pooledConnection -> {

            var connection = pooledConnection.nativeConnection();

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

    public void update() throws SQLException {

        pool.provide(pooledConnection -> {

            var connection = pooledConnection.nativeConnection();

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

    public long updateWithGeneratedKey(ProducerByCall<Long> producer) throws SQLException {

        var wrapper = new CallExecutorWithPool.Wrapper<Long>();

        pool.provide(pooledConnection -> {

            var connection = pooledConnection.nativeConnection();

            if (!statementInformation.isPrepared()) {
                LOG.severe("Statement is not prepared but required when trying to get generated key");
                wrapper.reservedSpace = 0L;
                return;
            }

            var preparedStatement = this.createPreparedStatement(statementInformation, connection, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.executeUpdate();

            wrapper.reservedSpace = producer.produce(preparedStatement.getGeneratedKeys());

            preparedStatement.close();


        });

        return wrapper.reservedSpace;
    }

    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection) throws SQLException {
        var statement = connection.prepareStatement(statementInformation.getQueryString());

        setUpParams(statementInformation, statement);

        return statement;
    }



    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection, int statementOption) throws SQLException {
        var statement = connection.prepareStatement(statementInformation.getQueryString(), statementOption);

        setUpParams(statementInformation, statement);

        return statement;
    }

    private static void setUpParams(StatementInformation statementInformation, PreparedStatement statement) {
        statementInformation.getParams().forEach(param -> {

            try {
                switch (param.type()) {
                    case TYPE_STRING -> statement.setString(param.position(), (String) param.value());
                    case TYPE_INTEGER -> statement.setInt(param.position(), (int) param.value());
                    case TYPE_FLOAT -> statement.setFloat(param.position(), (float) param.value());
                    case TYPE_LONG -> statement.setLong(param.position(), (long) param.value());
                    case TYPE_BIG_DECIMAL -> statement.setBigDecimal(param.position(), (BigDecimal) param.value());
                    case TYPE_DOUBLE -> statement.setDouble(param.position(), (double) param.value());
                    case TYPE_BINARY_STRING -> statement.setBytes(param.position(), (byte[]) param.value());
                    case TYPE_BINARY_SINGLE -> statement.setByte(param.position(), (byte) param.value());
                }
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }

        });
    }

    private static class Wrapper<P> {
        public P reservedSpace;
    }
}
