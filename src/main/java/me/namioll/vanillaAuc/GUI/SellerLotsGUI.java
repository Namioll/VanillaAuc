package me.namioll.vanillaAuc.GUI;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.Holders.SellerLotsGUIHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SellerLotsGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Inventory createSellerGUI(List<AuctionItem> sellerLots) {

        Inventory inv = Bukkit.createInventory(new SellerLotsGUIHolder(sellerLots), 9, mm.deserialize("<bold><gradient:#FFD700:#FFA500>Покупка товара</gradient>"));

        for (int i = 0; i < sellerLots.size(); i++) {
            AuctionItem item = sellerLots.get(i);
            inv.setItem(i, item.item());
        }

        ItemStack back = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = back.getItemMeta();
        meta.displayName(mm.deserialize("<!italic><red>Назад"));
        back.setItemMeta(meta);
        inv.setItem(8, back);

        return inv;
    }
}
