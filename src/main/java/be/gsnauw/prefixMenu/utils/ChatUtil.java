package be.gsnauw.prefixMenu.utils;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatUtil {

    private final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private final JavaPlugin plugin;

    ConfigManager mainConfig = PrefixMenu.getInstance().getMainConfig();

    public ChatUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Component format(String text) {
        return LEGACY_SERIALIZER.deserialize(text)
                .colorIfAbsent(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.UNDERLINED, false);
    }

    public Component prefix(String text) {
        return format(mainConfig.getString("messages.prefix") + text);
    }

    public Component noPermission() {
        return prefix(mainConfig.getString("messages.no-permission"));
    }

    public void playerCommand() {
        info(mainConfig.getString("messages.player-command"));
    }

    public void info(String text) {
        plugin.getLogger().info(text);
    }

    public void warn(String text) {
        plugin.getLogger().warning(text);
    }

    public void error(String text) {
        plugin.getLogger().severe(text);
    }
}
