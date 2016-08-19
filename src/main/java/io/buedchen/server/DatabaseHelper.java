package io.buedchen.server;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
    private Connection conn;
    private PreparedStatement preparedStatement;

    public DatabaseHelper() {

        BasicDataSource bds = DataSource.getInstance().getBds();

        try {
            conn = bds.getConnection();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, String param2) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, int param2) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setInt(2, param2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, URL param2) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setURL(2, param2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, String param2, Integer param3) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setInt(3, param3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, String param2, String param3) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setString(3, param3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, Integer param1, String param2, String param3) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setString(3, param3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, String param2, Integer param3, String param4) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setInt(3, param3);
            preparedStatement.setString(4, param4);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, String param2, String param3, int param4) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setString(3, param3);
            preparedStatement.setInt(4, param4);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecuteUpdate(String query, String param1, Integer param2, String param3, String param4) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setInt(2, param2);
            preparedStatement.setString(3, param3);
            preparedStatement.setString(4, param4);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }


    public void statementExecuteUpdate(String query, String param1, String param2, String param3, String param4) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.setString(2, param2);
            preparedStatement.setString(3, param3);
            preparedStatement.setString(4, param4);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public void statementExecute(String query) {

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
    }

    public ResultSet statementExecuteQuery(String query, String param1) {

        ResultSet rs = null;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, param1);
            preparedStatement.executeQuery();
            return preparedStatement.getResultSet();
        } catch (SQLException e) {
            logger.error("SQL Error : ", e);
        }
        return rs;
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            logger.error("SQL error : ", e);
        }

    }
}
