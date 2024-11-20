# Sql support

## Requirements ##

- java 17+
- MySQL as database
- PostgreSQL as database [link to docs](postgres-connection.md)

### Dependency ###

There are the dependecy tags

<tabs>
    <tab title="Maven">
        <code-block lang=xml>
            <![CDATA[
                <dependency>
                    <groupId>org.makechtec.software</groupId>
                    <artifactId>sql_support</artifactId>
                    <version>2.1.0</version>
                </dependency>
            ]]>
        </code-block>
    </tab>
    <tab title="Groovy">
        <code-block lang=groovy>
            implementation 'org.makechtec.software:sql_support:2.1.0'
        </code-block>
    </tab>
    <tab title="Kotlin">
        <code-block lang=kotlin>
            implementation ("org.makechtec.software:sql_support:2.1.0")
        </code-block>
    </tab>
</tabs>

### Usage

#### Example producing a Dto record

<tabs>
    <tab title="Call to database">
        <code-block lang="java">
                var connectionCredentials = new ConnectionInformation(
                    "user",
                    "pass",
                    "host",
                    "3306",
                    "database"
                );
                var postgresEngine = new PostgresEngine&lt;Dto&gt;(connectionCredentials);
                ProducerByCall&lt;Dto&gt; producer =
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
                try {
                    var result =
                            postgresEngine.isPrepared()
                                    .queryString("CALL dto_by_id(?)")
                                    .addParamAtPosition(1, 1, ParamType.TYPE_INTEGER)
                                    .run(producer);
                } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                assertFalse(dto.name().isEmpty());
        </code-block>
    </tab>
    <tab title="Dto.java">
        <code-block lang="java">
            public record Dto(int id, String name) {
            }
        </code-block>
    </tab>
</tabs>

### Changes History ###

#### 2.1.0 Added PostgreSQL support ####

#### 1.4.2 Fixing bug related to com.mysql.cj.jdbc.Driver class load ####

#### 1.4.1 Fixing bug in types ####

#### 1.4.0 Added more supported prepared statement types {collapsible="true"}

They are

- TYPE_STRING
- TYPE_INTEGER
- TYPE_FLOAT
- TYPE_LONG
- TYPE_BIG_DECIMAL
- TYPE_DOUBLE

#### 1.3.2 Added ProducerByCall functionality ####

<seealso>
    <category ref="wrs">
        <a href="postgres-connection.md">Postgres Connection</a>
    </category>
</seealso>