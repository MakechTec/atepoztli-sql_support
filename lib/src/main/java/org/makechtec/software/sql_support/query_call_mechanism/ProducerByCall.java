package org.makechtec.software.sql_support.query_call_mechanism;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ProducerByCall<P> {

    P produce(ResultSet resultSet) throws SQLException;
}
