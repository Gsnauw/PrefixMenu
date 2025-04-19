package be.gsnauw.prefixMenu;

import be.gsnauw.prefixMenu.commands.PrefixAdminCommand;
import be.gsnauw.prefixMenu.commands.PrefixCommand;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import be.gsnauw.prefixMenu.utils.ChatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PrefixMenu extends JavaPlugin {
    @Getter
    private ConfigManager mainConfig;
    @Getter
    private ConfigManager usersConfig;
    @Getter
    private ConfigManager customPrefixConfig;
    @Getter
    private ChatUtil chatUtil;
    @Getter
    private static PrefixMenu instance;

    @Override
    public void onEnable() {
        instance = this;
        mainConfig = new ConfigManager(this, "config.yml");
        usersConfig = new ConfigManager(this, "users.yml");
        customPrefixConfig = new ConfigManager(this, "customprefixes.yml");
        chatUtil = new ChatUtil(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpension().register();
            chatUtil.info("PapiExpension registered.");
        } else {
            chatUtil.error("Could not find PlaceholderAPI! ");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Objects.requireNonNull(this.getCommand("prefix")).setExecutor(new PrefixCommand());
        Objects.requireNonNull(this.getCommand("prefixmenuadmin")).setExecutor(new PrefixAdminCommand());

        getLogger().info("The plugin has started, Hello World!");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has been disabled, goodbye!");
    }
}
