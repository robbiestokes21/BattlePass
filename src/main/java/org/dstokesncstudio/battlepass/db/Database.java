package org.dstokesncstudio.battlepass.db;

import org.dstokesncstudio.battlepass.model.PlayerBattlePass;

import java.sql.*;

public class Database {
    private  Connection connection;
    private static boolean DBConn;
    private static String msg;


    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        //Try to connect to my MySQL database running locally
        String url = "jdbc:mysql://localhost/battlepass";
        String user = "root";
        String password = "";

        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        System.out.println(Database.getMessage("Connected"));
        DBConn = true;
        return connection;
    }


    public static boolean getDBConn(){
        return DBConn;
    }

    public static String getMessage(String msgType){
        try {
            switch (msgType) {
                case "Connected" -> msg = "Connected to BattlePass Database";
                case "Error" -> msg = "Unable to connect to BattlePass Database";
                case "TableCreated" -> msg = "Table Created in the database.";
            }
        }catch (Exception e){
            msg = e.getMessage();
        }
        return msg;
    }
    public void initializeDatabase() throws SQLException{

        Statement statement = getConnection().createStatement();

        String sql = "CREATE TABLE IF NOT EXISTS battlepass (uuid varchar(36) primary key, isOP int, player_keys int, requiredKeysAmount int, bp_level int)";
        statement.execute(sql);
        statement.close();
        System.out.println(Database.getMessage("TableCreated"));
    }

    public PlayerBattlePass findPlayerStatsByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM battlepass WHERE uuid = ?");
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        PlayerBattlePass PlayerBattlePass;

        if(resultSet.next()){

            PlayerBattlePass = new PlayerBattlePass(resultSet.getString("uuid"), resultSet.getInt("isOP"), resultSet.getInt("player_keys"), resultSet.getInt("requiredKeysAmount"), resultSet.getInt("bp_level"));

            statement.close();

            return PlayerBattlePass;
        }

        statement.close();

        return null;
    }


    public void createPlayerStats(PlayerBattlePass PlayerBattlePass) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO battlepass(uuid, isOP, player_keys, requiredKeysAmount, bp_level) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, PlayerBattlePass.getPlayerUUID());
        statement.setInt(2, PlayerBattlePass.getIsOP());
        statement.setInt(3, PlayerBattlePass.getKeys());
        statement.setLong(4, PlayerBattlePass.getRequiredKeysAmount());
        statement.setDouble(5, PlayerBattlePass.getLevel());

        statement.executeUpdate();

        statement.close();

    }

    public void updatePlayerStats(PlayerBattlePass PlayerBattlePass) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE battlepass SET isOP = ?, player_keys = ?, requiredKeysAmount = ?, bp_level = ? WHERE uuid = ?");
        statement.setInt(1, PlayerBattlePass.getIsOP());
        statement.setInt(2, PlayerBattlePass.getKeys());
        statement.setLong(3, PlayerBattlePass.getRequiredKeysAmount());
        statement.setDouble(4, PlayerBattlePass.getLevel());
        statement.setString(5, PlayerBattlePass.getPlayerUUID());

        statement.executeUpdate();

        statement.close();

    }

    public void deletePlayerStats(PlayerBattlePass PlayerBattlePass) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM battlepass WHERE uuid = ?");
        statement.setString(1, PlayerBattlePass.getPlayerUUID());

        statement.executeUpdate();

        statement.close();

    }

    public void closeConnection(){
        try {
            if(this.connection != null){
                this.connection.close();
            }
        }catch (SQLException e) {
            System.out.println("Error closing Connection!!");
            e.printStackTrace();
        }
    }

}
