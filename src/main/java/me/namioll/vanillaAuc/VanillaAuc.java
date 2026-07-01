package me.namioll.vanillaAuc;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.namioll.vanillaAuc.Listeners.AuctionListener;
import me.namioll.vanillaAuc.Listeners.BuyItemListener;
import me.namioll.vanillaAuc.Listeners.SellerLotsListener;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }
}
