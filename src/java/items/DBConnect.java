package items;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stanchev
 */
public class DBConnect {
    
    private static DBConnect instance;
    private Connection connection;
    private String url = "jdbc:postgresql://ambari-head.csc.calpoly.edu/brian";
    private String username = "brian";
    private String password = "seniorProjectST";

    public Connection getConnection() {
//        connection = null;
//        try {
//            connection = DriverManager.getConnection(
//                    url, username,
//                    password); //not actual password
//        } catch (SQLException e) {
//            System.out.println("Connection Failed! Check output console");
//            e.printStackTrace();
//            return null;
//        }
        return connection;
    }
    
    public static DBConnect getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnect();
        } else if (instance.getConnection().isClosed()){
            instance = new DBConnect();
        }    
        return instance;
    }

    public DBConnect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }
    }
}