
# PostgreSQL USAGE #

    var connectionCredentials = new ConnectionInformation(
        "user",
        "pass",
        "host",
        "3306",
        "database"
    );

    var postgresEngine = new PostgresEngine<Dto>(connectionCredentials);

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

    try {
        var result =
                postgresEngine.isPrepared()
                        .queryString("CALL dto_by_id(?)")
                        .addParamAtPosition(1, 1, ParamType.TYPE_INTEGER)
                        .run(producer);
    } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
    }