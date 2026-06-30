package me.namioll.vanillaAuc.Listeners;

import me.namioll.vanillaAuc.*;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import me.namioll.vanillaAuc.GUI.LotGUI;
import me.namioll.vanillaAuc.GUI.SellerLotsGUI;
import me.namioll.vanillaAuc.Holders.AuctionHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static com.google.common.primitives.Ints.indexOf;

public class AuctionListener implements Listener {

    private static final int[] ITEM_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34,
            37,38,39,40,41,42,43
    };

    private final DatabaseManager db;
    private final JavaPlugin plugin;

    public AuctionListener(DatabaseManager db, JavaPlugin plugin) {
        this.db = db;
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getClickedInventory().getHolder() instanceof AuctionHolder holder)) return;
        e.setCancelled(true);

        int slot = e.getSlot();
        int page = holder.getPage();
        Player p = (Player) e.getWhoClicked();
        List<AuctionItem> lots = holder.getLots();

        int slotIndex = indexOf(ITEM_SLOTS, slot);

        if (slotIndex != -1) {
            int lotIndex = page * 26 + slotIndex;

            if (lotIndex < lots.size()) {
                AuctionItem lot = lots.get(lotIndex);
                LotGUI.createLotGUI(lot);
            }
            return;
        }

        if (slot == 48 && e.getCurrentItem().getType() != Material.GRAY_STAINED_GLASS_PANE) {
            p.openInventory(AuctionGUI.createAuctionGUI(lots, page-1));
            return;
        }

        if (slot == 50 && e.getCurrentItem().getType() != Material.GRAY_STAINED_GLASS_PANE) {
            p.openInventory(AuctionGUI.createAuctionGUI(lots, page+1));
            return;
        }

        if (slot == 49) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    List<AuctionItem> sellerLots = db.getSellerSlots(p.getUniqueId());

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            p.openInventory(SellerLotsGUI.createSellerGUI(sellerLots));
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
            return;
        }
    }
}
