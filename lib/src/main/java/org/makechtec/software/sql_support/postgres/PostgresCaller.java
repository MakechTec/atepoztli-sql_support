package org.makechtec.software.sql_support.postgres;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.QueryParam;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PostgresCaller<T> {

    private static final Logger LOG = Logger.getLogger(PostgresCaller.class.getName());
    private final ConnectionInformation connectionInformation;
    private final StatementInformation statementInformation;
    private final PostgresConnectionFactory connectionFactory;

    public PostgresCaller(ConnectionInformation connectionInformation, StatementInformation statementInformation) {
        this.connectionInformation = connectionInformation;
        this.statementInformation = statementInformation;
        connectionFactory = new PostgresConnectionFactory();
    }

    public T execute(ProducerByCall<T> producer) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        T result;

        try {

            var connection = connectionFactory.openNewConnection(connectionInformation);

            if (statementInformation.isPrepared()) {
                var statement = this.createPreparedStatement(statementInformation, connection);
                statement.execute();

                var resultSet = statement.getResultSet();

                result = producer.produce(resultSet);

                resultSet.close();
                statement.close();
            } else {
                var statement = connection.createStatement();

                statement.execute(statementInformation.getQueryString());

                var resultSet = statement.getResultSet();

                result = producer.produce(resultSet);

                resultSet.close();
                statement.close();
            }

            connection.close();

            return result;
        } catch (SQLException e) {
            LOG.severe("There was an error executing the SQL query with the specified statement information");
            LOG.severe("or running the producer action");
            LOG.severe("by calling org.makechtec.software.sql_support.postgres.PostgresCaller");
            throw e;
        }

    }

    public void update() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        try {

            var connection = connectionFactory.openNewConnection(connectionInformation);

            if (statementInformation.isPrepared()) {
                var statement = this.createPreparedStatement(statementInformation, connection);
                statement.executeUpdate();

                statement.close();
            } else {
                var statement = connection.createStatement();

                statement.executeUpdate(statementInformation.getQueryString());

                statement.close();
            }

            connection.close();

        } catch (SQLException e) {
            LOG.severe("There was an error executing the SQL query with the specified statement information");
            LOG.severe("or running the producer action");
            LOG.severe("by calling org.makechtec.software.sql_support.postgres.PostgresCaller");
            throw e;
        }
    }

    private PreparedStatement createPreparedStatement(StatementInformation statementInformation, Connection connection) throws SQLException {

        try {

            var statement = connection.prepareStatement(statementInformation.getQueryString());

            for (QueryParam<?> param : statementInformation.getParams()) {
                switch (param.type()) {
                    case TYPE_STRING -> statement.setString(param.position(), (String) param.value());
                    case TYPE_INTEGER -> statement.setInt(param.position(), (int) param.value());
                    case TYPE_FLOAT -> statement.setFloat(param.position(), (float) param.value());
                    case TYPE_LONG -> statement.setLong(param.position(), (long) param.value());
                    case TYPE_BIG_DECIMAL -> statement.setBigDecimal(param.position(), (BigDecimal) param.value());
                    case TYPE_DOUBLE -> statement.setDouble(param.position(), (double) param.value());
                }
            }

            return statement;
        } catch (SQLException e) {
            LOG.severe("There was an error creating prepared statement, it could be a bad sql query or bad param number or type");
            LOG.severe("at org.makechtec.software.sql_support.postgres.PostgresCaller");
            throw e;
        }
    }


}
