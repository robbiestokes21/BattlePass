package org.dstokesncstudio.battlepass;



import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
public class databaseConn {

    private static databaseConn conn;
    private static boolean DBConn;

    public static databaseConn connectDB(){
        String url = "jdbc:mysql://localhost/battlepass";
        String user = "root";
        String password = "";
        try{
            Connection conn = DriverManager.getConnection(url, user, password);
            DBConn = true;
            System.out.println("Connected to BattlePass Database");
        }catch (SQLException e){
            conn = null;
            DBConn = false;
            System.out.println("Unable to connect to BattlePass Database");
            e.printStackTrace();

        }
        return conn;
    }

    public static databaseConn getConn() {
        return connectDB();
    }

    public static boolean getDBConn(){
        return DBConn;
    }

}
