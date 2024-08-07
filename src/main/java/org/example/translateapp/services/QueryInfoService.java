package org.example.translateapp.services;

import jakarta.annotation.PostConstruct;
import org.example.translateapp.db.ConnectionPool;
import org.example.translateapp.models.QueryInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QueryInfoService {

    @Qualifier("connectionPool")
    ConnectionPool connectionPool;

    public QueryInfoService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<QueryInfo> findAllQueryInfos() throws SQLException {
        Connection conn = connectionPool.getConnection();
        List<QueryInfo> queryInfos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(
                     "select id, inet6_ntoa(user_ip_bin) as user_ip, input, output, datetime from query_infos")) {
            while (resultSet.next()) {
                queryInfos.add(QueryInfo.builder()
                                       .id(resultSet.getInt("id"))
                                       .userIp(resultSet.getString("user_ip"))
                                       .input(resultSet.getString("input"))
                                       .output(resultSet.getString("output"))
                                       .datetime(resultSet.getObject("datetime", Date.class))
                                       .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connectionPool.releaseConnection(conn);

        return queryInfos;
    }

    public void saveQueryInfo(QueryInfo queryInfo) throws SQLException {
        Connection conn = connectionPool.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(
                "insert into query_infos(user_ip_bin, input, output, datetime) values(inet6_aton(?), ?, ?, ?)")) {
            ps.setString(1, queryInfo.getUserIp());
            ps.setString(2, queryInfo.getInput());
            ps.setString(3, queryInfo.getOutput());
            ps.setObject(4, queryInfo.getDatetime());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connectionPool.releaseConnection(conn);
    }

    @PostConstruct
    private void init() throws SQLException {
        Connection conn = connectionPool.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("create table if not exists query_infos" +
                                       "(id int primary key auto_increment, user_ip_bin varbinary(16)," +
                                       "input text," +
                                       "output mediumtext, " +
                                       "datetime datetime)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connectionPool.releaseConnection(conn);
    }
}
