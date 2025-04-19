package be.gsnauw.prefixMenu.commands;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import be.gsnauw.prefixMenu.utils.ChatUtil;
import be.gsnauw.prefixMenu.utils.GuiUtil;
import be.gsnauw.prefixMenu.utils.PrefixUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrefixCommand implements CommandExecutor{

    ChatUtil chat = PrefixMenu.getInstance().getChatUtil();
    GuiUtil gui = GuiUtil.getInstance();
    ConfigManager mainConfig = PrefixMenu.getInstance().getMainConfig();
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

        if (args.length == 0) {
            if (prefixUtil.checkStaff(p)) {
                //Player is staff
                p.sendMessage(chat.prefix(mainConfig.getString("messages.is-staff")));
                return true;
            }

            gui.prefixGui(p).open(p);
        } else {
            p.sendMessage(chat.prefix(mainConfig.getString("messages.command-error")));
        }
        return true;
    }
}
