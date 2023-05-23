package org.dstokesncstudio.battlepass;

import com.google.common.util.concurrent.Service;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public final class BattlePass extends JavaPlugin implements Listener {


    private static BattlePass plugin;
    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        getCommand("battlepass").setExecutor(new openCommand(0));
        getServer().getPluginManager().registerEvents(new openCommand(0), this);
        getLogger().info("BattlePass has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BattlePass has been disabled.");
    }

    public static BattlePass getPlugin() {
        return plugin;
    }
}