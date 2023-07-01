package org.makechtec.software.sql_support.query_call_mechanism;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_process.statement.ParamType;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CallExecutorTest {

    @Test
    public void testCall(){

        var connectionCredentials = new ConnectionInformation(
                "root",
                "",
                "localhost",
                "3306",
                "lib_testing"
        );

        ProducerByCall<Dto> producer =
                resultSet -> {

                    Dto dto = null;
                    while(resultSet.next()){
                        dto = new Dto(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        );
                    }

                    return dto;
                };

        var dto =
                ProducerCallEngine.<Dto>builder(connectionCredentials)
                                    .isPrepared()
                                    .setQueryString("CALL dto_by_id(?)")
                                    .addParamAtPosition(1, 1, ParamType.TYPE_INTEGER)
                                    .produce(producer);

        assertFalse(dto.name().isEmpty());

    }

}
