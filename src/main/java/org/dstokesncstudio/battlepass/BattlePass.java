package org.dstokesncstudio.battlepass;



import org.bukkit.plugin.java.JavaPlugin;
import org.dstokesncstudio.battlepass.db.Database;
import org.dstokesncstudio.battlepass.commands.commands;
import org.dstokesncstudio.battlepass.listeners.Listeners;
import java.sql.SQLException;



public final class BattlePass extends JavaPlugin {
    private static BattlePass plugin;
    private commands cmd;
    private Listeners listeners;
    private Database database;


    public void onEnable() {
        this.database  = new Database();
        this.listeners = new Listeners(database, cmd);
        plugin = this;
        try {

            this.database.initializeDatabase();
            this.getCommand(("battlepass")).setExecutor(new commands(cmd));
            this.getServer().getPluginManager().registerEvents(new Listeners(database, cmd), this);
            this.getServer().getPluginManager().registerEvents(new commands(cmd), this);
        } catch (SQLException e) {
            this.getLogger().info(Database.getMessage("Error"));
            System.out.println("Unable to connect to database.");
            e.printStackTrace();
        }
        boolean DBConn = Database.getDBConn();
        //openCommand cmd = new openCommand(0, Material.TRIPWIRE_HOOK, 5);


        getServer().getPluginManager().registerEvents(listeners, this);



        /*
        this.getCommand("battlepass").setExecutor(new openCommand(0, Material.TRIPWIRE_HOOK, 5));
        this.getServer().getPluginManager().registerEvents(new openCommand(0, Material.TRIPWIRE_HOOK, 5), this);
        */
        this.getLogger().info("BattlePass has been enabled.");

        if(DBConn){
            this.getLogger().info(Database.getMessage("Connected"));
        }else {
            this.getLogger().info(Database.getMessage("Error"));

        }
    }

    public void onDisable() {
        this.getLogger().info("BattlePass has been disabled.");
        this.database.closeConnection();
    }

    public static BattlePass getPlugin() {
        return plugin;
    }
}
