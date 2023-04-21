package org.makechtec.software.sql_support.query_process.statement;

public record QueryParam<T>(int position, T value, ParamType type) {

}
