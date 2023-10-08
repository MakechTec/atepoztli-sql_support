package org.makechtec.software.sql_support.mysql;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.commons.SQLEngineBuilder;
import org.makechtec.software.sql_support.query_call_mechanism.CallExecutor;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

public class MysqlEngine<T> implements SQLEngineBuilder<T> {

    private final StatementInformation.StatementInformationBuilder statementInformationBuilder;

    private final ConnectionInformation connectionInformation;

    public MysqlEngine(ConnectionInformation connectionInformation) {
        this.statementInformationBuilder = StatementInformation.builder();
        this.connectionInformation = connectionInformation;
    }

    @Override
    public MysqlEngine<T> isPrepared() {
        this.statementInformationBuilder.setPrepared(true);
        return this;
    }

    @Override
    public MysqlEngine<T> queryString(String queryString) {
        this.statementInformationBuilder.setQueryString(queryString);
        return this;
    }

    @Override
    public MysqlEngine<T> addParamAtPosition(int position, Object value, ParamType paramType) {
        this.statementInformationBuilder.addParamAtPosition(position, value, paramType);
        return this;
    }

    @Override
    public T run(ProducerByCall<T> producer) {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutor<T>(this.connectionInformation, statement);

        return caller.execute(producer);
    }

    @Override
    public void update() {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutor<T>(this.connectionInformation, statement);

        caller.update();
    }


}
