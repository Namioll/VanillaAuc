package me.namioll.vanillaAuc.Listeners;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.Holders.SellerLotsGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class SellerLotsListener implements Listener {

    @EventHandler
    public void OnClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof SellerLotsGUIHolder holder)) return;

        List<AuctionItem> lots = holder.getLots();
        Player p = (Player) e.getWhoClicked();
    }
}
