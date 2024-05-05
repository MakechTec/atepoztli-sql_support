# postgres_connection

This tutorial will show how to connect to a PostgreSQL database
using sql support library.


* Before you start
* Prepare database connection information
* Connect and handle result

## Before you start

Please get following information:

Make sure that you have:
- Database server IP
- Database port
- Database user
- Database password
- Database name
- java 17 or earlier

## Prepare database connection information

Put the connection information using a ConnectionInformation object:

   ```Java
      var connectionCredentials = new ConnectionInformation(
        "user",
        "pass",
        "host",
        "3306",
        "database"
      );
   ```

## Connect and handle result

   ```Java
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
   ```

In the Dto.java file

   ```Java
      public record Dto(int id, String name) {}
   ```

In the above code you have seen first set the flag if you will
put params at the prepared statement, this is the "isPrepared()".

Then add corresponding queryString and each param with corresponding 
param type.

Finally pass the resulset consumer function to handle the result.

## What you've learned {id="what-learned"}

Simply set connection information at the start and call the query.

<seealso>
<!--Give some related links to how-to articles-->
</seealso>
