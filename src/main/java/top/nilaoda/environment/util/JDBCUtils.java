package top.nilaoda.environment.util;

import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    //四要素
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    //对四要素赋值并注册驱动
    static {
        Properties prop = new ConfigurationReader().readConfigAsProperties();
        try {
            driver = prop.getProperty("dbstore_driver");
            url = prop.getProperty("dbstore_url");
            user = prop.getProperty("dbstore_user");
            password = prop.getProperty("dbstore_password");

            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //私有构造器
    private JDBCUtils() {
    }

    //获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    //释放资源
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }
}
