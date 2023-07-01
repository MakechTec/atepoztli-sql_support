
## Requirements ##

- java 17+
- MySQL as database

## Dependency ##

__Maven__

    <dependency>
        <groupId>org.makechtec.software</groupId>
        <artifactId>sql_support</artifactId>
        <version>1.2.0</version>
    </dependency>

__Gradle__

    implementation 'org.makechtec.software:sql_support:1.2.0'

## Usage ##

__Example of update__

    var statementInformation =
    StatementInformation.builder()
                        .setQueryString("SELECT * FROM names;")
                        .build();
    
    var caller = new QueryCaller(connectionInformation, statementInformation);

    caller.callUpdate();

__Example of call__

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