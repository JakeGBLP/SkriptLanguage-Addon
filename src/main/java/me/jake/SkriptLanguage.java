package me.jake;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkriptLanguage extends JavaPlugin {

    public static SkriptAddon addon;
    public static SkriptLanguage instance;
    @Override
    public void onEnable() {
        instance = this;
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