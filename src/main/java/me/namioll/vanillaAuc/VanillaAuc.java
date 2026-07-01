package me.namioll.vanillaAuc;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import me.namioll.vanillaAuc.Holders.AuctionHolder;
import me.namioll.vanillaAuc.Listeners.AuctionListener;
import me.namioll.vanillaAuc.Listeners.BuyItemListener;
import me.namioll.vanillaAuc.Listeners.SellerLotsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class VanillaAuc extends JavaPlugin {

    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        databaseManager = new DatabaseManager(this);
        getServer().getPluginManager().registerEvents(new AuctionListener(databaseManager, this), this);
        getServer().getPluginManager().registerEvents(new BuyItemListener(databaseManager, this), this);
        getServer().getPluginManager().registerEvents(new SellerLotsListener(this, databaseManager), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(new AuctionCMD(databaseManager, this).build().build());
        });
        renderAuction();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void renderAuction() {
        new BukkitRunnable() {

            @Override
            public void run() {
                List<AuctionItem> allLots = databaseManager.getAllLots();
                List<AuctionItem> activeLots = new ArrayList<>();

                long remained = 3L * 24 * 60 * 60 * 1000;
                long now = System.currentTimeMillis();

                for (AuctionItem lot : allLots) {
                    if (lot.listedAt() + remained > now) {
                        activeLots.add(lot);
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getOpenInventory().getTopInventory().getHolder() instanceof AuctionHolder holder) {

                                holder.setLots(activeLots);
                                AuctionGUI.updateAuctionGUI(
                                        player.getOpenInventory().getTopInventory(),
                                        activeLots,
                                        holder.getPage()
                                );
                            }
                        }
                    }
                }.runTask(VanillaAuc.this);
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }
}
