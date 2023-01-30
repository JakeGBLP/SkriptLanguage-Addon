package me.jake;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import me.jake.utils.ConfigManager;
import java.util.Objects;

public final class SkriptLanguage extends JavaPlugin implements Listener {
    public static SkriptAddon addon;
    public static SkriptLanguage instance;
    public static String pluginName = "SkriptLanguage";
    @Override
    public void onEnable() {
        ConfigManager.setStoredConfig(ConfigManager.getConfig());
        ConfigManager.createFolder(ConfigManager.getPluginsFolder(), pluginName);
        try {
            ConfigManager.createConfig(ConfigManager.getPluginFolder(pluginName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        instance = this;
        Objects.requireNonNull(this.getCommand("skriptlanguage")).setExecutor(new Commands());
        addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("me.jake", "elements");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[SkriptLanguage] has been enabled!");
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
    public static SkriptLanguage getInstance() {
        return instance;
    }
}