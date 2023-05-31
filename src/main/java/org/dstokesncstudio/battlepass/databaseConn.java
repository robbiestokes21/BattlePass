package org.dstokesncstudio.battlepass;



import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
public class databaseConn {

    private static databaseConn conn;
    private static boolean DBConn;

    private static String msg;

    public static databaseConn connectDB(){
        String url = "jdbc:mysql://localhost/battlepass";
        String user = "root";
        String password = "";
        try{
            Connection conn = DriverManager.getConnection(url, user, password);
            DBConn = true;
        }catch (SQLException e){
            conn = null;
            DBConn = false;
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

    public static String getMessage(String msgType){
        try {
            switch (msgType) {
                case "Connected" -> msg = "Connected to BattlePass Database";
                case "Error" -> msg = "Unable to connect to BattlePass Database";
            }
        }catch (Exception e){
            msg = e.getMessage();
        }
        return msg;
    }

}
