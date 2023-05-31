package org.dstokesncstudio.battlepass;


import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public final class BattlePass extends JavaPlugin implements Listener {
    private static BattlePass plugin;

    public void onEnable() {
        databaseConn conn = databaseConn.getConn();
        boolean DBConn = databaseConn.getDBConn();

        plugin = this;

        this.getCommand("battlepass").setExecutor(new openCommand(0, Material.TRIPWIRE_HOOK, 5));
        this.getServer().getPluginManager().registerEvents(new openCommand(0, Material.TRIPWIRE_HOOK, 5), this);
        this.getLogger().info("BattlePass has been enabled.");
        if(DBConn){
            this.getLogger().info("Connected to BattlePass Database");
        }else {
            this.getLogger().info("Unable to connect to BattlePass Database");
        }
    }

    public void onDisable() {
        this.getLogger().info("BattlePass has been disabled.");
    }

    public static BattlePass getPlugin() {
        return plugin;
    }
}
