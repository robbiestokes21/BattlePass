package org.dstokesncstudio.battlepass;

import org.bukkit.plugin.java.JavaPlugin;
import org.dstokesncstudio.battlepass.commands.commands;
import org.dstokesncstudio.battlepass.db.Database;
import org.dstokesncstudio.battlepass.listeners.Listeners;
import org.dstokesncstudio.battlepass.translate.TranslationConfig;

import java.sql.SQLException;



public final class BattlePass extends JavaPlugin {
    private static BattlePass plugin;

    private static TranslationConfig translationConfig;
    private commands cmd;
    private Listeners listeners;
    private Database database;

    public void onEnable() {
        this.database = new Database();
        this.cmd = new commands(this);
        this.listeners = new Listeners(database, cmd);
        plugin = this;
        translationConfig = new TranslationConfig(plugin); // Initialize the TranslationConfig instance with the plugin

        try {
            this.database.initializeDatabase();
            this.getCommand("battlepass").setExecutor(cmd);
            getServer().getPluginManager().registerEvents(listeners, this);
        } catch (SQLException e) {
            this.getLogger().info(Database.getMessage("Error"));
            System.out.println("Unable to connect to database.");
            e.printStackTrace();
        }

        boolean DBConn = Database.getDBConn();
        getServer().getPluginManager().registerEvents(listeners, this);

        this.getLogger().info("BattlePass has been enabled.");

        if (DBConn) {
            this.getLogger().info(Database.getMessage("Connected"));
        } else {
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

    public TranslationConfig getTranslationConfig() {
        return translationConfig;
    }
}
