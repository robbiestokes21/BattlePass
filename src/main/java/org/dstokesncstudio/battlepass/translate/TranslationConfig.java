package org.dstokesncstudio.battlepass.translate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dstokesncstudio.battlepass.BattlePass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.function.Supplier;

public class TranslationConfig {
    private final Plugin plugin;
    private FileConfiguration config;
    private File configFile;

    public TranslationConfig(Plugin plugin) {
        this.plugin = plugin;
        // Specify the folder where the translate.yml file should be saved
        File translateFolder = new File(plugin.getDataFolder(), "translate");
        if (!translateFolder.exists()) {
            translateFolder.mkdirs();
        }

        this.configFile = new File(translateFolder, "translate.yml");

        // Copy the translate.yml file from resources if it doesn't exist
        if (!configFile.exists()) {
            try (InputStream inputStream = plugin.getResource("translate/translate.yml")) {
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reloadConfig();

    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getTranslation(Player player, String key) {

        String playerLocale = player.getLocale();
        plugin.getServer().getLogger().info(playerLocale);
        String translation = config.getString(playerLocale + "." + key);
        if (translation != null) {
            plugin.getServer().getLogger().info(translation);
            return translation;
        }
        plugin.getServer().getLogger().info(key);
        return key; // Return the key itself if translation is not found

    }

    public boolean isEnglishLocale(Player player) {
        String playerLocale = player.getLocale();
        return playerLocale.startsWith("en");
    }

    public boolean isSpanishLocale(Player player) {
        String playerLocale = player.getLocale();
        return playerLocale.startsWith("es");
    }
}
