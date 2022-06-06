package jdbc.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager implements AutoCloseable{
    private static Connection conn;
    private static ConnectionManager instance;
    private ConnectionManager(){
        try {
            conn = DriverManager.getConnection("jdbc:h2:~/test","sa", "");
            conn.setAutoCommit(true);
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
    public static ConnectionManager getInstance(){
        if(instance == null){
            instance = new ConnectionManager();
        }
        return instance;
    }
    public Connection getConnection(){
        return this.conn;
    }
    @Override
    public void close() throws Exception {
        //conn.close();
    }
}
