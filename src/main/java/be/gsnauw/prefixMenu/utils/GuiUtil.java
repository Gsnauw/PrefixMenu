package be.gsnauw.prefixMenu.utils;

import be.gsnauw.prefixMenu.PrefixMenu;
import be.gsnauw.prefixMenu.managers.ConfigManager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class GuiUtil {
    @Getter
    private static final GuiUtil instance = new GuiUtil();
    ChatUtil chat = PrefixMenu.getInstance().getChatUtil();
    ConfigManager mainConfig = PrefixMenu.getInstance().getMainConfig();
    ConfigManager usersConfig = PrefixMenu.getInstance().getUsersConfig();
    ConfigManager customPrefixes = PrefixMenu.getInstance().getCustomPrefixConfig();
    PrefixUtil prefixUtil = PrefixUtil.getInstance();

    public Gui prefixGui(Player p) {
        Gui gui = Gui.gui()
                .title(chat.format("&bPrefix&fMenu"))
                .rows(5)
                .create();

        String selectedPrefix = "default";
        if (usersConfig.getConfig().contains("users." + p.getUniqueId())) {
            //Player found
            selectedPrefix = usersConfig.getString("users." + p.getUniqueId());
        }

        if (!p.hasPermission("sv-prefix." + selectedPrefix)) {
            prefixUtil.setPrefix(p, "default");
            selectedPrefix = usersConfig.getString("users." + p.getUniqueId());
        }

        int slot = 0;
        for (String name : Objects.requireNonNull(mainConfig.getConfig().getConfigurationSection("prefixes")).getKeys(false)) {
            String prefix;
            if (name.equalsIgnoreCase("customprefix")) {
                if (prefixUtil.checkCustomPrefix(p)) {
                    prefix = customPrefixes.getString("prefixes." + p.getUniqueId());
                } else {
                    continue;
                }
            } else {
                prefix = mainConfig.getString("prefixes." + name + ".prefix");
            }
            String materialString = mainConfig.getString("prefixes." + name + ".material");

            if (p.hasPermission("sv-prefix." + name)) {
                Material material = Material.matchMaterial(materialString);
                if (material == null) continue;
                List<String> lore = List.of("Klik om te selecteren");
                ItemStack itemStack;
                GuiItem item;
                if (name.equalsIgnoreCase(selectedPrefix)) {
                    lore = List.of("Actieve prefix");
                    itemStack = addItem(material, prefix, lore, true);
                    item = ItemBuilder.from(itemStack).asGuiItem();
                } else {
                    itemStack = addItem(material, prefix, lore, false);
                    item = ItemBuilder.from(itemStack).asGuiItem(event -> {
                        usersConfig.set("users." + p.getUniqueId(), name);
                        p.sendMessage(chat.prefix("Je hebt de prefix " + prefix + "&f geselecteerd!"));
                        p.closeInventory();
                    });
                }
                gui.setItem(slot, item);
                slot++;
            }
        }

        GuiItem barrier = ItemBuilder.from(addItem(Material.BARRIER, "&cSluit", null, false))
                .asGuiItem(event -> p.closeInventory());
        gui.setItem(5, 5, barrier);
        gui.getFiller().fill(ItemBuilder.from(filler()).asGuiItem());
        gui.setDefaultClickAction(e -> e.setCancelled(true));
        return gui;
    }

    private ItemStack addItem(Material material, String name, List<String> lore, boolean glow) {
        ItemStack Item = new ItemStack(material);
        if (glow) {
            Item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
        }
        ItemMeta ItemMeta = Item.getItemMeta();
        if (lore != null) {
            List<Component> loreComponents = lore.stream()
                    .map(chat::format)
                    .toList();
            ItemMeta.lore(loreComponents);
        }
        ItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ItemMeta.displayName(chat.format(name));
        Item.setItemMeta(ItemMeta);
        return Item;
    }

    private ItemStack filler() {
        ItemStack Item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta ItemMeta = Item.getItemMeta();
        ItemMeta.displayName(chat.format(""));
        Item.setItemMeta(ItemMeta);
        return Item;
    }
}