package org.dstokesncstudio.battlepass.commands;



import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dstokesncstudio.battlepass.BattlePass;
import org.dstokesncstudio.battlepass.model.PlayerBattlePass;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dstokesncstudio.battlepass.translate.TranslationConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class commands implements CommandExecutor, Listener {
    private final BattlePass plugin;
    private commands cmd;
    private List<ItemStack> unlockItems;
    private int currentLevel;
    private int keyCount;
    private String lore;
    private FileConfiguration config;


    private Material requiredKeyMaterial;  // Added variable to store the required key material
    private int requiredKeyAmount;  // Added variable to store the required key amount
    private final String Key = "key";
    private PlayerBattlePass PlayerBattlePass;

    public commands(BattlePass plugin){
        this.plugin = plugin;

    }
    private void initializeConfig(Player player) {

        this.currentLevel = '0';
        unlockItems = new ArrayList<>();
        // Add the custom unlock items here
        unlockItems.add(createUnlockItem(Material.DIAMOND, "DIAMOND"));
        unlockItems.add(createUnlockItem(Material.GOLD_INGOT, "GOLD_INGOT"));
        unlockItems.add(createUnlockItem(Material.TRIPWIRE_HOOK, "test"));
        // Add more unlock items if needed
        config = YamlConfiguration.loadConfiguration(new File("plugins/battlepass/config.yml"));
        //initializeConfig();

        this.requiredKeyMaterial = Material.TRIPWIRE_HOOK;
        this.requiredKeyAmount = 5;
        player.getServer().getLogger().info(String.valueOf(requiredKeyAmount));
        File configFile = new File("plugins/battlepass/config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        String playerId = player.getUniqueId().toString();
        this.keyCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == requiredKeyMaterial) {
                keyCount += item.getAmount();
            }
        }
        if (!configFile.exists()) {
            config.options().copyDefaults(true);
            config.set("players." + playerId + ".keys", keyCount); // Set the initial key count to 0
            config.set("players." + playerId +".requiredKeysAmount", requiredKeyAmount);  // Save the required key amount to the config
            player.sendMessage("players." + playerId +".requiredKeysAmount", String.valueOf(requiredKeyAmount));
            saveConfig();
            requiredKeyAmount = config.getInt("requiredKeysAmount", requiredKeyAmount);
        }else{
            setPlayerKeys(player, keyCount);
            setRequiredKeyAmount(player, requiredKeyAmount);
        }
        keyCount = config.getInt("keys", keyCount);
        requiredKeyAmount = config.getInt("requiredKeysAmount", requiredKeyAmount);  // Retrieve the required key amount from the config
        player.getServer().getLogger().info(String.valueOf(requiredKeyAmount));
    }
    private void saveConfig() {
        try {
            config.save(new File("plugins/battlepass/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
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

    private void setPlayerKeys(Player player, int keys) {
        String playerId = player.getUniqueId().toString();
        config.set("players." + playerId + ".keys", keys);  // Save the player's key count to the config
        saveConfigFile();
    }
    private void setRequiredKeyAmount(Player p, int RequiredKeyAmount){
        String playerId = p.getUniqueId().toString();
        config.set("players." + playerId + ".requiredKeysAmount", RequiredKeyAmount);
        saveConfigFile();
    }
    private int getRequiredKeyAmount(Player p){
        String playerId = p.getUniqueId().toString();
        return config.getInt("players." + playerId + ".requiredKeysAmount", 0);  // Retrieve the player's key count from the config
    }
    private int getPlayerKeys(Player player) {
        String playerId = player.getUniqueId().toString();
        return config.getInt("players." + playerId + ".keys", 0);  // Retrieve the player's key count from the config
    }

    private int getCurrentLevel(Player player) {
        player.getPlayer();
        // Implement the logic to get the current level of the player's battle pass
        // You can use a data storage system (e.g., configuration file, database) to store and retrieve the player's progress
        String playerId = player.getUniqueId().toString();
        return config.getInt("players." + playerId + ".level", 0);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        initializeConfig(player);
        if (cmd.getName().equalsIgnoreCase("battlepass")) {
            if (args.length == 0) {
                // If no arguments provided, open the battle pass menu for the player
                openMenu(player);
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("claim")) {
                // If 'claim' argument provided, try to claim the reward for the next level
                int nextLevel = getCurrentLevel(player) + 1;
                this.requiredKeyAmount += getRequiredKeyAmount(player) + (nextLevel * 2);
                setRequiredKeyAmount(player, requiredKeyAmount);

                claimReward(player, nextLevel);
                return true;
            }else if (args.length == 1 && args[0].equalsIgnoreCase("level")) {
                player.sendMessage("Your level in the battlepass is: " + getCurrentLevel(player));
                return true;
            }else if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
                if (player.isOp()) {
                    Player targetPlayer = Bukkit.getPlayer(args[1]);
                    if (targetPlayer != null) {
                        Material itemMaterial = Material.matchMaterial(String.valueOf(Material.TRIPWIRE_HOOK));
                        if(Key.equals(args[2])){
                            if (itemMaterial != null) {
                                ItemStack itemStack = createBattlepasskey(player,itemMaterial, "Battle Pass Key");
                                int amount = 1;
                                if (args.length >= 4) {
                                    try {
                                        amount = Integer.parseInt(args[3]);
                                    } catch (NumberFormatException e) {
                                        player.sendMessage("Invalid amount specified.");
                                        return true;
                                    }
                                }
                                itemStack.setAmount(amount);
                                targetPlayer.getInventory().addItem(itemStack);
                                targetPlayer.sendMessage("You received " + amount + " custom item(s) from an OP.");
                                player.sendMessage("You gave " + amount + " custom item(s) to " + targetPlayer.getName());
                                //get player keys
                                int currentKeys = getPlayerKeys(player);
                                System.out.println(currentKeys);
                                int newKeys = currentKeys + amount;
                                System.out.println(newKeys);
                                setPlayerKeys(player, newKeys);
                                return true;
                            } else {
                                player.sendMessage("Invalid item material.");
                                return true;
                            }
                        }else{
                            player.sendMessage("Invalid item.");
                            return true;
                        }
                    } else {
                        player.sendMessage("Player not found.");
                        return true;
                    }
                } else {
                    player.sendMessage("You don't have permission to use this command.");
                    return true;
                }
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

            // Create a new lore
            List<String> lore = new ArrayList<>();
            lore.add("This is the item you will unlock at Level " + (i + 1));

            // Add information about the items the player will get if they claim the rewards
            List<String> rewardItems = getRewardItemsForLevel(i + 1); // Assuming a method that retrieves the reward items based on the level
            lore.addAll(rewardItems);

            itemMeta.setLore(lore);
            unlockItem.setItemMeta(itemMeta);
            menu.setItem(i, unlockItem);
        }

        player.openInventory(menu);

    }

    private List<String> getRewardItemsForLevel(int level) {
        List<String> rewardItems = new ArrayList<>();

        // Add reward items based on the level
        if (level == 1) {
            rewardItems.add("Item 1");
            rewardItems.add("Item 2");
        } else if (level == 2) {
            rewardItems.add("Item 3");
            rewardItems.add("Item 4");
            rewardItems.add("Item 5");
        } else if (level == 3) {
            rewardItems.add("Item 6");
        }
        // Add more conditions for different levels and reward items if needed

        return rewardItems;
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

        if (level <= unlockItems.size()) {
            ItemStack requiredKey = createUnlockItem(requiredKeyMaterial, "Battle Pass Key");
            int playerKeyAmount = getPlayerKeyAmount(player);
            System.out.println(playerKeyAmount);
            return playerKeyAmount >= getRequiredKeyAmount(player);
        }

        return false;



    }

    private int getPlayerKeyAmount(Player player) {
        int keyAmount = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == requiredKeyMaterial) {

                keyAmount += item.getAmount();
            }


        }

        return keyAmount;
    }
    private void removeRequiredItems(Player player, int level) {
        // Implement the logic to remove the required items from the player's inventory
        // You can use player.getInventory().remove(...) or other methods to remove the items

        if (level <= unlockItems.size()) {
            int remainingAmount = getRequiredKeyAmount(player);
            ItemStack[] playerInventory = player.getInventory().getContents();

            for (int i = 0; i < playerInventory.length; i++) {
                ItemStack item = playerInventory[i];
                ItemMeta itemMeta = item.getItemMeta();
        


                if (item.getType() == requiredKeyMaterial) {
                    NBTItem nbtItem = new NBTItem(item);

                    if (nbtItem.hasKey("battlepass_key") && nbtItem.getString("battlepass_key").equals("true")) {
                        int itemAmount = item.getAmount();
                        if (itemAmount <= remainingAmount) {
                            remainingAmount -= itemAmount;
                            playerInventory[i] = null;
                        } else {
                            item.setAmount(itemAmount - remainingAmount);
                            remainingAmount = 0;
                            break;
                        }
                    }
                }
            }

            player.getInventory().setContents(playerInventory);
        }

        player.updateInventory();
        int currentKeys = getPlayerKeys(player);
        int newKeys = currentKeys - getRequiredKeyAmount(player);
        setPlayerKeys(player, newKeys);
    }

    private void giveReward(Player player, int level) {
        // Implement the logic to give the player the reward for the specified level
        // This could include giving items, experience, currency, etc.
        // Example: player.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));



        if (level == 1) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 2));
            System.out.println("kitadmin key guest_kit Regular "+player.getName()+ "1");
            //battlePass.getServer().dispatchCommand(player, "kitadmin key guest_kit Regular "+ player + " 1");
            player.getServer().dispatchCommand(player, "kitadmin key guest_kit Regular "+player.getName()+ " 1");
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
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("key", displayName);
        return nbtItem.getItem();
        //return itemStack;
    }

    private ItemStack createBattlepasskey(Player p, Material material, String displayName){
        BattlePass plugin = BattlePass.getPlugin();
        TranslationConfig translationConfig = plugin.getTranslationConfig();
        String lore = translationConfig.getTranslation(p, "BattlePassKey");

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);

        List<String> itemLore = itemMeta.getLore();

        if (itemLore == null) {
            itemLore = new ArrayList<>();
        }

        itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));

        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);

        // Set custom NBT tag
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("battlepass_key", "true"); // Set the custom NBT tag "battlepass_key" to indicate it's a Battle Pass key
        itemStack = nbtItem.getItem();

        return itemStack;
    }

}
