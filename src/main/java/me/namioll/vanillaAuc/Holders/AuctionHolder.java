package me.namioll.vanillaAuc.Holders;

import me.namioll.vanillaAuc.AuctionItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuctionHolder implements InventoryHolder {
    private final List<AuctionItem> lots;
    private final int page;

    public AuctionHolder(List<AuctionItem> lots, int page) {
        this.lots = lots;
        this.page = page;
    }

    public List<AuctionItem> getLots() { return lots; }
    public int getPage() { return page; }

    @Override
    public @NotNull Inventory getInventory() { return null; }
}
