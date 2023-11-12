package org.makechtec.software.sql_support.postgres;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.commons.SQLEngineBuilder;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.sql.SQLException;

public class PostgresEngine<T> implements SQLEngineBuilder<T> {


    private final StatementInformation.StatementInformationBuilder statementInformationBuilder;

    private final ConnectionInformation connectionInformation;

    public PostgresEngine(ConnectionInformation connectionInformation) {
        this.statementInformationBuilder = StatementInformation.builder();
        this.connectionInformation = connectionInformation;
    }

    @Override
    public PostgresEngine<T> isPrepared() {
        this.statementInformationBuilder.setPrepared(true);
        return this;
    }

    @Override
    public PostgresEngine<T> queryString(String queryString) {
        this.statementInformationBuilder.setQueryString(queryString);
        return this;
    }

    @Override
    public PostgresEngine<T> addParamAtPosition(int position, Object value, ParamType paramType) {
        this.statementInformationBuilder.addParamAtPosition(position, value, paramType);
        return this;
    }

    @Override
    public T run(ProducerByCall<T> producer) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        var statement = this.statementInformationBuilder.build();
        var caller = new PostgresCaller<T>(this.connectionInformation, statement);
        return caller.execute(producer);
    }

    @Override
    public void update() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        var statement = this.statementInformationBuilder.build();
        var caller = new PostgresCaller<T>(this.connectionInformation, statement);
        caller.update();
    }

}
