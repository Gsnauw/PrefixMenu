package be.gsnauw.prefixMenu.utils;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PrefixUtil {
    @Getter
    private static final PrefixUtil instance = new PrefixUtil();
    ChatUtil chat = PrefixMenu.getInstance().getChatUtil();
    ConfigManager mainConfig = PrefixMenu.getInstance().getMainConfig();
    ConfigManager usersConfig = PrefixMenu.getInstance().getUsersConfig();
    ConfigManager customPrefixes = PrefixMenu.getInstance().getCustomPrefixConfig();

    public void setPrefix(Player player, String prefixName) {
        if (!mainConfig.getConfig().contains("prefixes." + prefixName)) {
            chat.error("An unknown prefix name was given. " + prefixName);
            return;
        }
        usersConfig.getConfig().set("users." + player.getUniqueId(), prefixName);
        usersConfig.save();
    }

    public void setPrefixStaff(Player p) {
        ConfigurationSection prefixesSection = mainConfig.getConfig().getConfigurationSection("prefixes");
        if (prefixesSection == null) return;
        String highestPrefix = null;
        int highestWeight = -1;

        for (String name : prefixesSection.getKeys(false)) {
            String type = mainConfig.getConfig().getString("prefixes." + name + ".type");
            int weight = mainConfig.getConfig().getInt("prefixes." + name + ".weight");

            if ("staff".equalsIgnoreCase(type) && p.hasPermission("sv-prefix." + name)) {
                if (weight > highestWeight) {
                    highestWeight = weight;
                    highestPrefix = name;
                }
            }
        }
        setPrefix(p, highestPrefix);
    }

    public boolean checkStaff(Player p) {
        ConfigurationSection prefixesSection = mainConfig.getConfig().getConfigurationSection("prefixes");
        if (prefixesSection == null) return false;
        for (String name : prefixesSection.getKeys(false)) {
            String type = mainConfig.getConfig().getString("prefixes." + name + ".type");
            if ("staff".equalsIgnoreCase(type) && p.hasPermission("sv-prefix." + name)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCustomPrefix(Player p) {
        ConfigurationSection customPrefixUsers = customPrefixes.getConfig().getConfigurationSection("prefixes");
        UUID uuid = p.getUniqueId();
        if (customPrefixUsers == null) {
            return false;
        }
        return customPrefixUsers.contains(uuid.toString());
    }

    public void setCustomPrefix(Player p, String prefixValue) {
        UUID uuid = p.getUniqueId();
        customPrefixes.set("prefixes." + uuid, prefixValue);
        customPrefixes.save();
    }

    public void removeCustomPrefix(Player p) {
        UUID uuid = p.getUniqueId();
        customPrefixes.set("prefixes." + uuid, null);
        customPrefixes.save();
    }

    public String getCustomPrefix(Player p) {
        UUID uuid = p.getUniqueId();
        if (!checkCustomPrefix(p)) return null;
        return customPrefixes.getString("prefixes." + uuid);
    }

    public String getPrefix(Player p) {
        String uuidPath = "users." + p.getUniqueId();
        String defaultPrefix = mainConfig.getString("prefixes.default.prefix");
        String selectedPrefix;
        if (!usersConfig.getConfig().contains(uuidPath)) {
            //Player not found
            if (checkStaff(p)) {
                //Player is staff but not found
                setPrefixStaff(p);
                selectedPrefix = usersConfig.getString(uuidPath);
                return mainConfig.getString("prefixes." + selectedPrefix + ".prefix");
            }
            return defaultPrefix;
        }
        selectedPrefix = usersConfig.getString(uuidPath);
        if (checkStaff(p)) {
            setPrefixStaff(p);
            selectedPrefix = usersConfig.getString(uuidPath);
            return mainConfig.getString("prefixes." + selectedPrefix + ".prefix");
        }
        if (!mainConfig.getConfig().contains("prefixes." + selectedPrefix)) {
            //Prefix does not exist (anymore)
            return defaultPrefix;
        }
        if (p.hasPermission("sv-prefix." + selectedPrefix)) {
            if (selectedPrefix.equalsIgnoreCase("customprefix")) {
                if (checkCustomPrefix(p)) {
                    return getCustomPrefix(p);
                } else {
                    setPrefix(p, "default");
                }
            } else {
                return mainConfig.getString("prefixes." + selectedPrefix + ".prefix");
            }
        }
        if (!p.hasPermission("sv-prefix." + selectedPrefix)) {
            setPrefix(p, "default");
            return defaultPrefix;
        }
        return defaultPrefix;
    }
}
