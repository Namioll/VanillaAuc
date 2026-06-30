package me.namioll.vanillaAuc.GUI;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.Holders.SellerLotsGUIHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SellerLotsGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Inventory createSellerGUI(List<AuctionItem> lots){

        Inventory inv = Bukkit.createInventory(new SellerLotsGUIHolder(lots), 9, mm.deserialize("<bold><gradient:#FFD700:#FFA500>Покупка товара</gradient>"));

        for (AuctionItem item : lots) inv.addItem(item.item());

        ItemStack back = new ItemStack(Material.RED_WOOL);
        back.getItemMeta().displayName(mm.deserialize("<red>Назад"));
        inv.setItem(8, back);

        return inv;
    }
}
