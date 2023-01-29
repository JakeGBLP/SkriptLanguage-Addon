package me.jake.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static me.jake.SkriptLanguage.pluginName;

public class ConfigManager {
    public static List<String> storedConfig;

    public static List<String> getStoredConfig() {
        return storedConfig;
    }

    public static void setStoredConfig(File newConfig) {
        try {
            storedConfig = Files.readAllLines(newConfig.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static @NotNull Path getPluginsFolder() {
        return Bukkit.getPluginsFolder().toPath();
    }
    @Contract("_ -> new")
    public static @NotNull File getPluginFolder(String name) {
        return new File(getPluginsFolder()+"\\"+name);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFolder(Path path, String name) {
        File folder = new File(path+"\\"+name);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createConfig(File directory) throws IOException {
        File config = new File(directory, "config.yml");
        if (!config.exists()) {
            config.createNewFile();
            ArrayList<String> lines = new ArrayList<>();
            lines.add("randomWordWarning: true");
            Files.write(Paths.get(config.toPath().toUri()), lines);
        }
    }


    public static @NotNull File getConfig() {
        return Path.of(getPluginFolder(pluginName)+"\\"+"config.yml").toFile();
    }
    public static Boolean getBoolean(@NotNull File file, String name) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (!line.startsWith("#")) {
                    if (line.startsWith(name)) {
                        return line.contains("true");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Boolean getBoolean(@NotNull List<String> lines, String name) throws IOException {
        for (String line : lines) {
            if (!line.startsWith("#")) {
                if (line.startsWith(name)) {
                    return line.contains("true");
                }
            }
        }
        return null;
    }

    public void setBoolean(@NotNull File file, String name, Boolean value) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        int index = -1;
        for (String line : lines) {
            index ++;
            if (!line.startsWith("#")) {
                if (line.startsWith(name)) {
                    List<String> split = new ArrayList<>(List.of(line.split(": ")));
                    split.set(1, String.valueOf(value));
                    lines.set(index, split.get(1));
                }
            }
        }
        Files.write(Paths.get(file.toPath().toUri()), lines);
    }
}
