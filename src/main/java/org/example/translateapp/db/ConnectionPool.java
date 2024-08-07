package org.example.translateapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {

    private final String url;
    private final String user;
    private final String password;
    private final List<Connection> connections;
    private final List<Connection> usedConnections = new ArrayList<>();
    private static final int INITIAL_POOL_SIZE = 10;
    private static final int MAX_TIMEOUT = 20;

    public ConnectionPool(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;

        connections = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connections.add(DriverManager.getConnection(url, user, password));
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connections.isEmpty()) {
            connections.add(DriverManager.getConnection(url, user, password));
        }

        Connection connection = connections.remove(connections.size() - 1);

        if (!connection.isValid(MAX_TIMEOUT)) {
            connection.close();
            connection = DriverManager.getConnection(url, user, password);
        }

        usedConnections.add(connection);
        return connection;
    }

    public synchronized void releaseConnection(Connection connection) {
        connections.add(connection);
        usedConnections.remove(connection);
    }

}
