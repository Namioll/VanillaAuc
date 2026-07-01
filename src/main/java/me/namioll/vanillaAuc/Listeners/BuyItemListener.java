package me.namioll.vanillaAuc.Listeners;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.DatabaseManager;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import me.namioll.vanillaAuc.Holders.LotGUIHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
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
        if (e.getClickedInventory() == null) return;
        if (!(e.getInventory().getHolder() instanceof LotGUIHolder holder)) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 3) {
            AuctionItem lot = holder.getLot();
            if (lot == null) {
                p.sendRichMessage("<red>Данный лот больше не доступен.");
                p.closeInventory();
                return;
            }

            if (lot.sellerUuid().equals(p.getUniqueId())) {
                p.closeInventory();
                p.sendRichMessage("<red>Вы не можете купить свой лот! Если хотите его забрать, воспользуйтесь специальной кнопкой на главной странице.");
                return;
            }

            int price = lot.price();
            if (countOre(p) < price) {
                p.closeInventory();
                p.sendRichMessage("<red>Недостаточно АРов.");
                return;
            }

            p.closeInventory();

            new BukkitRunnable() {

                @Override
                public void run() {
                    boolean isOk = db.deleteLot(lot.id());

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (!isOk) {
                                p.sendRichMessage("<red>Этот лот уже кто-то купил!");
                                return;
                            }

                            if (countOre(p) < lot.price()) {
                                p.sendRichMessage("<red>Вот это да! У вас пропали АРы во время покупки. Галя, у нас отмена!");

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        db.insertLotWithId(lot);
                                    }
                                }.runTaskAsynchronously(plugin);
                                return;
                            }

                            int currentPrice = lot.price();
                            for (int i = 0; i < p.getInventory().getSize(); i++) {
                                ItemStack item = p.getInventory().getItem(i);
                                if (item != null &&
                                        (item.getType() == Material.DEEPSLATE_DIAMOND_ORE || item.getType() == Material.DIAMOND_ORE)) {
                                    int amount = item.getAmount();

                                    if (amount <= currentPrice) {
                                        p.getInventory().setItem(i, null);
                                        currentPrice -= amount;
                                    } else {
                                        item.setAmount(amount - currentPrice);
                                        currentPrice = 0;
                                    }
                                    if (currentPrice <= 0) break;
                                }
                            }

                            p.getInventory().addItem(lot.item());
                            p.sendRichMessage("<green>Вы успешно купили предмет за " + lot.price() + " АРов!");
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }

        if (e.getSlot() == 5) {
            p.closeInventory();

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

    private int countOre(Player p) {
        int count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null &&
                    (item.getType() == Material.DEEPSLATE_DIAMOND_ORE || item.getType() == Material.DIAMOND_ORE)) {
                count += item.getAmount();
            }
        }
        return count;
    }
}
