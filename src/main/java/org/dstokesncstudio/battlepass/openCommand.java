package org.dstokesncstudio.battlepass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class openCommand implements CommandExecutor, Listener {
    private final List<ItemStack> unlockItems;
    private int currentLevel;
    private FileConfiguration config;
    public openCommand(int currentLevel) {
        this.currentLevel = currentLevel;
        unlockItems = new ArrayList<>();
        // Add the custom unlock items here
        unlockItems.add(createUnlockItem(Material.DIAMOND, "Diamond Key"));
        unlockItems.add(createUnlockItem(Material.GOLD_INGOT, "Golden Key"));
        // Add more unlock items if needed
        config = YamlConfiguration.loadConfiguration(new File("plugins/battlepass/config.yml"));
        initializeConfig();
    }
    private void initializeConfig() {
        File configFile = new File("plugins/battlepass/config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists()) {
            config.options().copyDefaults(true);
            saveConfig();
        }
    }
    private void saveConfig() {
        try {
            config.save(new File("plugins/battlepass/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("battlepass")) {
            if (args.length == 0) {
                // If no arguments provided, open the battle pass menu for the player
                openMenu(player);
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("claim")) {
                // If 'claim' argument provided, try to claim the reward for the next level
                int nextLevel = getCurrentLevel(player) + 1;
                claimReward(player, nextLevel);
                return true;
            }else if (args.length == 1 && args[0].equalsIgnoreCase("level")) {
                player.sendMessage("Your level in the battlepass is: " + getCurrentLevel(player));
                return true;
            }
        }

        return false;
    }

    private void openMenu(Player player) {
        // Implement the logic to open the battle pass menu
        // You can use the Bukkit API to create an inventory menu and populate it with items
        int inventorySize = (int) Math.ceil((double) unlockItems.size() / 9) * 9;
        Inventory menu = Bukkit.createInventory(player, inventorySize, "Battle Pass Menu");

        for (int i = 0; i < unlockItems.size(); i++) {
            ItemStack unlockItem = unlockItems.get(i);
            ItemMeta itemMeta = unlockItem.getItemMeta();
            itemMeta.setDisplayName("Unlock Level " + (i + 1));
            unlockItem.setItemMeta(itemMeta);
            menu.setItem(i, unlockItem);
        }

        player.openInventory(menu);

    }
    // Custom inventory click event handler
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(player.getOpenInventory().getTopInventory())) {
            event.setCancelled(true); // Cancel the event to prevent item movement
        }
    }
    private void saveConfigFile() {
        // Save the config file to disk
        try {
            config.save(new File("plugins/battlepass/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setPlayerLevel(Player player, int level) {
        // Set the player's level in the config file
        String playerId = player.getUniqueId().toString();
        config.set("players." + playerId + ".level", level);
        saveConfigFile();
    }
    private int getCurrentLevel(Player player) {
        // Implement the logic to get the current level of the player's battle pass
        // You can use a data storage system (e.g., configuration file, database) to store and retrieve the player's progress
        String playerId = player.getUniqueId().toString();
        return config.getInt("players." + playerId + ".level", 0);
    }


    public void claimReward(Player player, int level) {
        if (level <= getCurrentLevel(player)) {
            player.sendMessage("You have already claimed the reward for level " + level);
            return;
        }

        if (!hasRequiredItems(player, level)) {
            player.sendMessage("You don't have enough items to claim the reward for level " + level);
            return;
        }

        // Remove the required items from the player's inventory
        removeRequiredItems(player, level);

        // Set the player's current level to the claimed level
        setPlayerLevel(player, level);


        // Give the player the reward for the claimed level
        giveReward(player, level);
        // ...



        // Optionally, you can send a message to the player indicating they have claimed the reward
        player.sendMessage("Congratulations! You have claimed the reward for level " + level);
    }

    private boolean hasRequiredItems(Player player, int level) {
        // Implement the logic to check if the player has the required items for the specified level
        // You can use player.getInventory().contains(...) or other methods to check for the items
        // Return true if the player has the required items, false otherwise
        if (level == 1) {
            return player.getInventory().contains(Material.DIAMOND);
        }
        // If level 2 is reached, check if the player has a gold ingot:
        else if (level == 2) {
            return player.getInventory().contains(Material.GOLD_INGOT);
        }
        // Add more item checking logic as needed

        return false;

    }

    private void removeRequiredItems(Player player, int level) {
        // Implement the logic to remove the required items from the player's inventory
        // You can use player.getInventory().remove(...) or other methods to remove the items
        if (level == 1) {
            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
        }
        // If level 2 is reached, remove a gold ingot from the player's inventory:
        else if (level == 2) {
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT));
        }
        // Add more item removal logic as needed

        // Update the player's inventory
        player.updateInventory();
    }

    private void giveReward(Player player, int level) {
        // Implement the logic to give the player the reward for the specified level
        // This could include giving items, experience, currency, etc.
        // Example: player.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));
        if (level == 1) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 2));
        }
        // If level 2 is reached, give a golden key:
        else if (level == 2) {
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 5));
        }
        // Add more reward logic as needed

        // Update the player's inventory
        player.updateInventory();
    }
    private ItemStack createUnlockItem(Material material, String displayName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
