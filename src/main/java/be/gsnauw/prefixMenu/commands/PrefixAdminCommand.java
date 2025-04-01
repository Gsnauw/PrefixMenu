package be.gsnauw.prefixMenu.commands;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import be.gsnauw.prefixMenu.utils.ChatUtil;
import be.gsnauw.prefixMenu.utils.PrefixUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrefixAdminCommand implements CommandExecutor, TabCompleter {

    ChatUtil chat = PrefixMenu.getInstance().getChatUtil();
    ConfigManager mainConfig = PrefixMenu.getInstance().getMainConfig();
    ConfigManager usersConfig = PrefixMenu.getInstance().getUsersConfig();
    PrefixUtil prefixUtil = PrefixUtil.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player p)) {
            chat.playerCommand();
            return true;
        }

        if (!p.hasPermission("prefixmenu.use")) {
            p.sendMessage(chat.noPermission());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!p.hasPermission("prefixmenu.reload")) {
                    p.sendMessage(chat.noPermission());
                    return true;
                }

                mainConfig.reload();
                usersConfig.reload();
                chat.info("Plugin reloaded.");
                p.sendMessage(chat.prefix(mainConfig.getString("prefixmenu.reload")));
            } else {
                p.sendMessage(chat.prefix(mainConfig.getString("prefixmenu.help")));
            }
        } else {
            p.sendMessage(chat.prefix(mainConfig.getString("prefixmenu.help")));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("help");
            completions.add("reload");
        }
        return completions;
    }
}