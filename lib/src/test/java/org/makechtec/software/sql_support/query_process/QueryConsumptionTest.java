package org.makechtec.software.sql_support.query_process;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_process.statement.ParamType;
import org.makechtec.software.sql_support.query_process.statement.StatementInformation;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Log
public class QueryConsumptionTest {

    private ConnectionInformation connectionInformation;

    @BeforeEach
    void setUp() {
        connectionInformation = new ConnectionInformation(
                "lib_tester",
                "3nitrotoluenO@",
                "localhost",
                "3306",
                "lib_tests"
        );
    }

    @Test
    public void testUpdate() {

      var statementInformation =
              StatementInformation.builder()
                      .setQueryString("SELECT * FROM names;")
                      .build();

      var caller = new QueryCaller(connectionInformation, statementInformation);

      caller.callUpdate();


    }

    @Test
    public void testCall() {


        var statementInformation =
                StatementInformation.builder()
                        .setQueryString("SELECT * FROM names;")
                        .build();

        var caller = new QueryCaller(connectionInformation, statementInformation);

        var results = new ArrayList<String>();

        caller.call( resultSet -> {

            while(resultSet.next()){
                results.add(resultSet.getString("name"));
            }

        });

        caller.getErrorMessages().forEach(log::info);

        assertFalse(results.isEmpty());

    }

    @Test
    public void testPreparedUpdate(){
        var statementInformation =
                StatementInformation.builder()
                        .setQueryString("SELECT * FROM names WHERE name = ?;")
                        .setPrepared(true)
                        .addParamAtPosition(1, "test", ParamType.TYPE_STRING)
                        .build();

        var caller = new QueryCaller(connectionInformation, statementInformation);

        caller.callUpdate();
    }

    @Test
    public void testPreparedCall(){
        var statementInformation =
                StatementInformation.builder()
                        .setQueryString("SELECT * FROM names WHERE name = ?;")
                        .setPrepared(true)
                        .addParamAtPosition(1, "test", ParamType.TYPE_STRING)
                        .build();

        var caller = new QueryCaller(connectionInformation, statementInformation);

        var results = new ArrayList<String>();

        caller.call( resultSet -> {
            while(resultSet.next()){
                results.add(resultSet.getString("name"));
            }
        });

        assertFalse(results.isEmpty());
    }

}
