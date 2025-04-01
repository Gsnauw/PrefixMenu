package be.gsnauw.prefixMenu;

import be.gsnauw.prefixMenu.utils.PrefixUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpension extends PlaceholderExpansion {

    PrefixUtil prefixUtil = PrefixUtil.getInstance();

    @Override
    public @NotNull String getAuthor() {
        return "SnowVille";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sv";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String name) {
        if (name.equalsIgnoreCase("prefix")) {
            return prefixUtil.getPrefix(p);
        }
        return null;
    }
}
