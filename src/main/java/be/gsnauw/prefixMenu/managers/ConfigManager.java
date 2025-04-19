package be.gsnauw.prefixMenu.managers;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final String fileName;
    private File file;

    @Getter
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        create();
    }

    private void create() {
        file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false); // Copies default file from resources
        }

        config = YamlConfiguration.loadConfiguration(file);
        updateConfigDefaults();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e + "Couldn't save file! Error.");
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        updateConfigDefaults();
    }

    private void updateConfigDefaults() {
        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream == null) return;
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
        boolean updated = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
                updated = true;
            }
        }
        if (updated) {
            save();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public String getString(String path) {
        return Objects.requireNonNull(config.getString(path));
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<String> getStringList(String path) {
        return Objects.requireNonNull(config.getStringList(path));
    }
}