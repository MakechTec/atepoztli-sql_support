package org.makechtec.software.sql_support.query_call_mechanism;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

public class ProducerCallEngine<P> {


    private final StatementInformation.StatementInformationBuilder statementInformationBuilder;

    private final ConnectionInformation connectionInformation;

    private ProducerCallEngine(ConnectionInformation connectionInformation) {
        this.connectionInformation = connectionInformation;
        this.statementInformationBuilder = StatementInformation.builder();
        this.statementInformationBuilder.setPrepared(false);
    }

    public static <P> ProducerCallEngine<P> builder(ConnectionInformation connectionInformation) {
        return new ProducerCallEngine<>(connectionInformation);
    }

    public ProducerCallEngine<P> setQueryString(String queryString) {
        this.statementInformationBuilder.setQueryString(queryString);
        return this;
    }

    public ProducerCallEngine<P> isPrepared() {
        this.statementInformationBuilder.setPrepared(true);
        return this;
    }

    public ProducerCallEngine<P> addParamAtPosition(int position, Object value, ParamType type) {
        this.statementInformationBuilder.addParamAtPosition(position, value, type);
        return this;
    }

    public P produce(ProducerByCall<P> producer) {
        var statement = this.statementInformationBuilder.build();
        var caller = new CallExecutor<P>(this.connectionInformation, statement);

        return caller.execute(producer);
    }


}
