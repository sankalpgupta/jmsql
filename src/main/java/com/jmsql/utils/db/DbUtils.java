package com.jmsql.utils.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DbUtils {
    private static Connection connection;
    private static String     username;
    private static String     password;
    private static String     dbName;
    private static String     dbIp;
    private static String     dbPort;

    private static final Logger LOG = LoggerFactory.getLogger(DbUtils.class);

    public static void init(){
        try {
            //System.out.println("Username:"+username+" password:"+password+" dbName:"+dbName+" dbIp:"+dbIp+" dbPort:"+dbPort);
            long startTime = System.currentTimeMillis();
            connection = DriverManager.getConnection("jdbc:mysql://"+dbIp+":"+dbPort+"/"+dbName+"?autoReconnect=true",username, password);
            long estimatedTime = System.currentTimeMillis() - startTime;
            LOG.debug("Time taken in setting connection:{}ms",estimatedTime);
        } catch (SQLException e) {
            System.out.println("Connection can not be made. Exiting...");
            System.exit(6);
        }
    }

    public static Set<String> getAllTables(Connection connection) throws SQLException {
        Set<String> tables = new HashSet<String>();
        DatabaseMetaData db = connection.getMetaData();
        ResultSet rs = db.getTables(null, null, "%", new String[] { "TABLE" });
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }
    
    public static List<ForiegnKeyRelation> getImportedKeys(Connection connection,String tableName) throws SQLException {
        List<ForiegnKeyRelation> tables = new ArrayList<ForiegnKeyRelation>();
        DatabaseMetaData db = connection.getMetaData();
        ResultSet rs = db.getImportedKeys(null, null, tableName);
        while (rs.next()) {
            tables.add(new ForiegnKeyRelation(rs.getString(7), rs.getString(8), rs.getString(3), rs.getString(4)));
        }
        return tables;
    }
    
    public static Set<String> getAllColumns(Connection connection) throws SQLException {
        Set<String> tables = new HashSet<String>();
        DatabaseMetaData db = connection.getMetaData();
        ResultSet rs = db.getColumns(null, null, "%", "%");
        while (rs.next()) {
            tables.add(rs.getString(3)+":"+rs.getString(4));
        }
        return tables;
    }
    
    public static Set<String> getColumnsByTable(Connection connection,String tableName) throws SQLException {
        Set<String> columns = new HashSet<String>();
        DatabaseMetaData db = connection.getMetaData();
        ResultSet rs = db.getColumns(null, null, tableName, "%");
        while (rs.next()) {
            columns.add(rs.getString(4));
        }
        return columns;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        DbUtils.connection = connection;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DbUtils.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DbUtils.password = password;
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        DbUtils.dbName = dbName;
    }

    public static String getDbIp() {
        return dbIp;
    }

    public static void setDbIp(String dbIp) {
        DbUtils.dbIp = dbIp;
    }

    public static String getDbPort() {
        return dbPort;
    }

    public static void setDbPort(String dbPort) {
        DbUtils.dbPort = dbPort;
    }

}
