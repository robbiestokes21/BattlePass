package org.dstokesncstudio.battlepass.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.dstokesncstudio.battlepass.commands.commands;
import org.dstokesncstudio.battlepass.db.Database;
import org.dstokesncstudio.battlepass.model.PlayerBattlePass;

import java.sql.SQLException;

public class Listeners implements Listener {
    private  final Database database;
    private  final commands cmd;

    public Listeners(Database database, commands cmd) {
        this.database = database;
        this.cmd = cmd;
    }

    public PlayerBattlePass getPlayerStatsFromDatabase(Player player) throws SQLException {

        PlayerBattlePass playerStats = database.findPlayerStatsByUUID(player.getUniqueId().toString());

        if (playerStats == null) {
            playerStats = new PlayerBattlePass(player.getUniqueId().toString(), 0, 0, 5, 0);
            database.createPlayerStats(playerStats);
        }

        return playerStats;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            Material itemMaterial = event.getItem().getType();
            if (itemMaterial == Material.TRIPWIRE_HOOK) {
                event.setCancelled(true); // Cancel the event to prevent placing the battle pass key
                event.getPlayer().sendMessage("You are not allowed to place the battle pass key.");
            }
        }
    }
}
