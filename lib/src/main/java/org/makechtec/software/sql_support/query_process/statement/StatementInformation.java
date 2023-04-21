package org.makechtec.software.sql_support.query_process.statement;

import java.util.HashSet;
import java.util.Set;

public class StatementInformation {

    private boolean isPrepared;
    private String queryString;
    private final Set<QueryParam<?>> params;

    public StatementInformation() {
        this.params = new HashSet<>();
    }

    public String getQueryString() {
        return queryString;
    }


    public boolean isPrepared() {
        return isPrepared;
    }


    public Set<QueryParam<?>> getParams() {
        return params;
    }

    public void addParam(QueryParam<?> param) {
        this.params.add(param);
    }

    public static StatementInformationBuilder builder(){
        return new StatementInformationBuilder();
    }


    public static class StatementInformationBuilder {
        private final StatementInformation statementInformation;


        private StatementInformationBuilder(){
            this.statementInformation = new StatementInformation();
        }


        public StatementInformationBuilder setPrepared(boolean isPrepared) {
            this.statementInformation.isPrepared = isPrepared;
            return this;
        }

        public StatementInformationBuilder setQueryString(String queryString){
            this.statementInformation.queryString = queryString;
            return this;
        }

        public <T> StatementInformationBuilder addParamAtPosition(int position, T value, ParamType type){
            var param = new QueryParam<>(position, value, type);
            this.statementInformation.addParam(param);
            return this;
        }

        public StatementInformation build(){
            return this.statementInformation;
        }

    }

}
