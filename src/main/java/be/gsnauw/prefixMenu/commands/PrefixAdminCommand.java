package be.gsnauw.prefixMenu.commands;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import be.gsnauw.prefixMenu.utils.ChatUtil;
import be.gsnauw.prefixMenu.utils.PrefixUtil;
import org.bukkit.Bukkit;
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
    ConfigManager customPrefixConfig = PrefixMenu.getInstance().getCustomPrefixConfig();
    PrefixUtil prefixUtil = PrefixUtil.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player p)) {
            chat.playerCommand();
            return true;
        }
        if (!p.hasPermission("prefixmenu.admin")) {
            p.sendMessage(chat.noPermission());
            return true;
        }
        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("reload")) {
                sendHelp(p);
                return true;
            }
            mainConfig.reload();
            usersConfig.reload();
            customPrefixConfig.reload();
            p.sendMessage(chat.prefix(mainConfig.getString("messages.reload")));
        }

        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("remove")) {
                sendHelp(p);
                return true;
            }
            String playerName = args[1];
            Player targetPlayer = getPlayer(p, playerName);
            if (targetPlayer == null) return true;
            if (!prefixUtil.checkCustomPrefix(targetPlayer)) {
                p.sendMessage(chat.prefix(mainConfig.getString("messages.player-not-prefix")));
                return true;
            }
            prefixUtil.removeCustomPrefix(targetPlayer);

            String orgMessage = mainConfig.getString("messages.removed");
            String message = orgMessage.replace("<player>", playerName);
            p.sendMessage(chat.prefix(message));
        }


        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    String playerName = args[1];
                    Player targetPlayer = getPlayer(p, playerName);
                    if (targetPlayer == null) return true;
                    if (!targetPlayer.hasPermission("sv-prefix.custom")) {
                        p.sendMessage(chat.prefix(mainConfig.getString("messages.custom-no-permission")));
                        return true;
                    }
                    if (prefixUtil.checkCustomPrefix(targetPlayer)) {
                        p.sendMessage(chat.prefix(mainConfig.getString("messages.player-has-prefix")));
                        return true;
                    }
                    String argsValue = args[2];
                    String prefixValue = argsValue.replace("_", " ");
                    prefixUtil.setCustomPrefix(targetPlayer, prefixValue);

                    String orgMessage = mainConfig.getString("messages.added");
                    String replace = orgMessage.replace("<player>", playerName);
                    String message = replace.replace("<prefix>", prefixValue);
                    p.sendMessage(chat.prefix(message));
                    return true;
                }
                case "edit" -> {
                    String playerName = args[1];
                    Player targetPlayer = getPlayer(p, playerName);
                    if (targetPlayer == null) return true;
                    if (!targetPlayer.hasPermission("sv-prefix.custom")) {
                        p.sendMessage(chat.prefix(mainConfig.getString("messages.player-no-permission")));
                        return true;
                    }
                    String argsValue = args[2];
                    String prefixValue = argsValue.replace("_", " ");
                    prefixUtil.setCustomPrefix(targetPlayer, prefixValue);

                    String orgMessage = mainConfig.getString("messages.edited");
                    String replace = orgMessage.replace("<player>", playerName);
                    String message = replace.replace("<prefix>", prefixValue);
                    p.sendMessage(chat.prefix(message));
                    return true;
                }
                default -> sendHelp(p);
            }
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(chat.format("&f<------<&e&lPrefixMenu&f>------>"));
        p.sendMessage(chat.format("&e/prefixadmin add <player> <prefix>&f - Add a custom prefix to a player."));
        p.sendMessage(chat.format("&e/prefixadmin edit <player> <prefix>&f - Edit the custom prefix of a player."));
        p.sendMessage(chat.format("&e/prefixadmin remove <player>&f - Remove the custom prefix from a player."));
        p.sendMessage(chat.format("&e/prefixadmin reload&f - Reload the plugin."));
        p.sendMessage(chat.format("&e/prefixadmin help&f - Shows this message."));
    }

    public Player getPlayer(Player p, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            p.sendMessage(chat.prefix(mainConfig.getString("messages.player-not-found")));
        }
        return targetPlayer;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("help");
            completions.add("reload");
            completions.add("add");
            completions.add("edit");
            completions.add("remove");
        }
        if (args.length == 2) {
            for (Player player : sender.getServer().getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}