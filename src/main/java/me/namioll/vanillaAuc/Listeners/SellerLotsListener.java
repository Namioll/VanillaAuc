package me.namioll.vanillaAuc.Listeners;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.DatabaseManager;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import me.namioll.vanillaAuc.Holders.SellerLotsGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SellerLotsListener implements Listener {

    private final JavaPlugin plugin;
    private final DatabaseManager db;

    public SellerLotsListener(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    @EventHandler
    public void OnClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!(e.getInventory().getHolder() instanceof SellerLotsGUIHolder holder)) return;
        e.setCancelled(true);

        List<AuctionItem> sellerlots = holder.getSellerLots();
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (slot == 8) {
            p.closeInventory();

            new BukkitRunnable() {

                @Override
                public void run() {
                    List<AuctionItem> allLots = db.getAllLots();

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            p.openInventory(AuctionGUI.createAuctionGUI(allLots, 0));
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
            return;
        }

        if (slot >= 0 && slot < 5) {
            if (slot >= sellerlots.size()) return;

            AuctionItem lot = sellerlots.get(slot);
            p.closeInventory();

            new BukkitRunnable() {

                @Override
                public void run() {
                    boolean success = db.deleteLot(lot.id());

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            if (!success) {
                                p.sendRichMessage("<red>Не удалось забрать предмет. Возможно, его только что купили!");
                                return;
                            }

                            p.getInventory().addItem(lot.item());
                            p.sendRichMessage("<green>Вы успешно сняли свой товар с продажи.");
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);

        }
    }
}
