package me.jake;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import me.jake.utils.AddonUtils;
import me.jake.utils.ConfigManager;
public class Commands implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager.setStoredConfig(ConfigManager.getConfig());
        String message = AddonUtils.Message("&7[&9SkriptLanguage&7] &aSuccessfully reloaded &9config file&7.");
        sender.sendMessage(message);
        return true;
    }
}
