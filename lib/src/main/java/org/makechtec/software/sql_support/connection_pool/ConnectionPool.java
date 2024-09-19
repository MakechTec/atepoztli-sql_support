package org.makechtec.software.sql_support.connection_pool;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public class ConnectionPool {

    private final int quantityOfConnections;
    private final BlockingDeque<PooledConnection> deque = new LinkedBlockingDeque<>();
    private final PooledConnectionCreator creator;

    public ConnectionPool(int quantityOfConnections, PooledConnectionCreator creator) {
        this.quantityOfConnections = quantityOfConnections;
        this.creator = creator;
    }

    public void boot() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        for(int i = 0; i < quantityOfConnections; i++) {


            PooledConnection connection = creator.create();

            if(!connection.isUsable()){
                throw new RuntimeException("Connection is not usable");
            }

            deque.add(connection);
        }

    }

    public void provide(PoolConnectionBorrower borrower) throws SQLException {

        PooledConnection connection;

        try {
            if ((deque.size() % 2) != 0) {
                connection = deque.takeFirst();
            } else {
                connection = deque.takeLast();
            }
        }catch (InterruptedException e){
            return;
        }

        try {
            borrower.borrowConnection(connection);
            giveBack(connection);
        } catch (SQLException e) {
            connection.close();
            regenerateConnection();
            throw e;
        }

    }

    public void giveBack(PooledConnection pooledConnection){
        deque.add(pooledConnection);
    }

    public void regenerateConnection() throws SQLException {

        try {
            deque.add(creator.create());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException(ex);
        }

    }

}
