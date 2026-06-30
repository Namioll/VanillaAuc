package me.namioll.vanillaAuc.GUI;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.Holders.LotGUIHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LotGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Inventory createLotGUI(AuctionItem lot) {
        Inventory inv = Bukkit.createInventory(new LotGUIHolder(lot), InventoryType.DISPENSER, mm.deserialize("<bold><gradient:#FFD700:#FFA500>Покупка товара</gradient>"));

        ItemStack yes = new ItemStack(Material.GREEN_WOOL);
        yes.getItemMeta().displayName(mm.deserialize("<green>Купить товар за " + lot.price() + "АР"));

        ItemStack no = new ItemStack(Material.RED_WOOL);
        no.getItemMeta().displayName(mm.deserialize("<red>Отмена"));

        inv.setItem(3, yes);
        inv.setItem(4, lot.item());
        inv.setItem(5, no);

        return inv;
    }
}
