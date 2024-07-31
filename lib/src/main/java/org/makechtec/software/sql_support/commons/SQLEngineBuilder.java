package org.makechtec.software.sql_support.commons;

import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;

import java.sql.SQLException;

public interface SQLEngineBuilder<T> {

    SQLEngineBuilder<T> isPrepared();

    SQLEngineBuilder<T> queryString(String queryString);

    SQLEngineBuilder<T> addParamAtPosition(int position, Object value, ParamType paramType);

    T run(ProducerByCall<T> producer) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    void update() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    long updateWithGeneratedKey(ProducerByCall<Long> producer) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;
}
