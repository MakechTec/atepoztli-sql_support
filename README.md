## Requirements ##

- java 17+
- MySQL as database

## Dependency ##

__Maven__

    <dependency>
        <groupId>org.makechtec.software</groupId>
        <artifactId>sql_support</artifactId>
        <version>1.4.2</version>
    </dependency>

__Gradle__

    implementation 'org.makechtec.software:sql_support:1.4.2'

## Usage ##

__Example producing a Dto record__

    var connectionCredentials = new ConnectionInformation(
                "user",
                "pass",
                "host",
                "3306",
                "database"
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

Dto.java

    public record Dto(int id, String name) {}

### Releases history ###

1.4.2 Fixing bug related to com.mysql.cj.jdbc.Driver class load

1.4.1 Fixing bug in types

1.4.0 Added more supported prepared statement types, in total they are:

- TYPE_STRING
- TYPE_INTEGER
- TYPE_FLOAT
- TYPE_LONG
- TYPE_BIG_DECIMAL
- TYPE_DOUBLE

1.3.2 Added ProducerByCall functionality