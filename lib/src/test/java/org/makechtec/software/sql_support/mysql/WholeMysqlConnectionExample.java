package org.makechtec.software.sql_support.mysql;

import org.junit.jupiter.api.Test;
import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.query_call_mechanism.Dto;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;

public class WholeMysqlConnectionExample {

    @Test
    public void example() {
        var connectionCredentials = new ConnectionInformation(
                "user",
                "pass",
                "host",
                "3306",
                "database"
        );

        var postgresEngine = new MysqlEngine<Dto>(connectionCredentials);

        ProducerByCall<Dto> producer =
                resultSet -> {

                    Dto dto = null;
                    while (resultSet.next()) {
                        dto = new Dto(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        );
                    }

                    return dto;
                };

        var result =
                postgresEngine.isPrepared()
                        .queryString("CALL dto_by_id(?)")
                        .addParamAtPosition(1, 1, ParamType.TYPE_INTEGER)
                        .run(producer);
    }

}
