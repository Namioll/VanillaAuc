package me.namioll.vanillaAuc.Holders;

import me.namioll.vanillaAuc.AuctionItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class LotGUIHolder implements InventoryHolder {

    private final AuctionItem lot;

    public LotGUIHolder(AuctionItem lot) {
        this.lot = lot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public AuctionItem getLot() {
        return lot;
    }
}
