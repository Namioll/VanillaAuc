package me.namioll.vanillaAuc.Holders;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.DatabaseManager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SellerLotsGUIHolder implements InventoryHolder {

    private final List<AuctionItem> sellerLots;

    public SellerLotsGUIHolder(List<AuctionItem> sellerLots) {
        this.sellerLots = sellerLots;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public List<AuctionItem> getSellerLots() { return sellerLots; }
}
