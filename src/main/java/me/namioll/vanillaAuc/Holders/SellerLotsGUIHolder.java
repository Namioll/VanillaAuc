package me.namioll.vanillaAuc.Holders;

import me.namioll.vanillaAuc.AuctionItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SellerLotsGUIHolder implements InventoryHolder {

    private final List<AuctionItem> lots;

    public SellerLotsGUIHolder(List<AuctionItem> lots) {
        this.lots = lots;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public List<AuctionItem> getLots() { return lots; }
}
