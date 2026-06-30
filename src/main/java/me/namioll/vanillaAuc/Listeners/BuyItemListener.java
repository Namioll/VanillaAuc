package me.namioll.vanillaAuc.Listeners;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.DatabaseManager;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import me.namioll.vanillaAuc.Holders.LotGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BuyItemListener implements Listener {

    private final DatabaseManager db;
    private final JavaPlugin plugin;

    public BuyItemListener(DatabaseManager db, JavaPlugin plugin) {
        this.db = db;
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof LotGUIHolder holder)) return;
        e.setCancelled(true);

        AuctionItem lot = holder.getLot();
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 3) {
            //забрать алмазную обычную и глубинносланцевую руду у игрока от цены товара и дать его, если все ок + удалить предмет из бд
            return;
        }
        if (e.getSlot() == 5) {
            new BukkitRunnable() {
                @Override
                public void run() {

                    List<AuctionItem> lots = db.getAllLots();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.openInventory(AuctionGUI.createAuctionGUI(lots, 0));
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }
    }
}
