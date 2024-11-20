package org.makechtec.software.sql_support.connection_pool;

import org.makechtec.software.sql_support.commons.SQLEngineBuilder;
import org.makechtec.software.sql_support.connection_pool.executor.CallExecutorWithPool;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.sql.SQLException;

public class WithPoolEngine<T> implements SQLEngineBuilder<T> {

    private final StatementInformation.StatementInformationBuilder statementInformationBuilder;
    private final ConnectionPool connectionPool;

    public WithPoolEngine(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.statementInformationBuilder = StatementInformation.builder();
    }

    @Override
    public SQLEngineBuilder<T> isPrepared() {
        this.statementInformationBuilder.setPrepared(true);
        return this;
    }

    @Override
    public SQLEngineBuilder<T> queryString(String queryString) {
        this.statementInformationBuilder.setQueryString(queryString);
        return this;
    }

    @Override
    public SQLEngineBuilder<T> addParamAtPosition(int position, Object value, ParamType paramType) {
        this.statementInformationBuilder.addParamAtPosition(position, value, paramType);
        return this;
    }

    @Override
    public T run(ProducerByCall<T> producer) throws SQLException {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutorWithPool<T>(this.connectionPool, statement);

        return caller.execute(producer);
    }

    @Override
    public void update() throws SQLException {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutorWithPool<T>(this.connectionPool, statement);

        caller.update();
    }

    @Override
    public long updateWithGeneratedKey(ProducerByCall<Long> producer) throws SQLException {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutorWithPool<T>(this.connectionPool, statement);

        return caller.updateWithGeneratedKey(producer);
    }
}
