package DbOperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {
    private final static String url = "jdbc:mysql://localhost:3306/imsdb?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private final static String username = "root";
    private final static String password = "";
    
    protected static Connection connection;
    protected static Statement statement;
    protected static PreparedStatement prepare;   
    protected static ResultSet result;
    protected static ResultSetMetaData metaData;

    public DbConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            //this.connection = DriverManager.getConnection(urlOnline, usernameOnline, passwordOnline);
            this.statement = connection.createStatement();
        }catch(Exception e){
          
        }              
    }
    
    public boolean isDatabaseConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
