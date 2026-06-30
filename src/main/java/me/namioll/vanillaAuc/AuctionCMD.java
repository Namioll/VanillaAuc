package me.namioll.vanillaAuc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.namioll.vanillaAuc.GUI.AuctionGUI;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AuctionCMD {

    private DatabaseManager db;
    private JavaPlugin plugin;

    public AuctionCMD(DatabaseManager db,  JavaPlugin plugin) {
        this.db = db;
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("ah")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player p)) return 0;

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

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("sell")
                        .then(Commands.argument("price", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    if (!(sender instanceof Player p)) return 0;

                                    int  price = IntegerArgumentType.getInteger(ctx, "price");

                                    //у игрока 36 слотов по 64 ара в каждом => макс. 2304 предметов в инвентаре без шалкеров и 2 руки.
                                    //это макс. цена за 1 предмет.
                                    if (price > 2304) {
                                        p.sendRichMessage("<red>У покупателя не поместится столько АРов в инвентаре!");
                                        return 0;
                                    }

                                    ItemStack item = p.getInventory().getItemInMainHand();
                                    if (item == null || item.getType() == Material.AIR) {
                                        p.sendRichMessage("<red>Возьмите предмет в руку!");
                                        return 0;
                                    }

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            int count = db.getSellersCount(p.getUniqueId());

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (count >= 5) {
                                                        p.sendRichMessage("<red>Максимум 5 товаров!");
                                                        return;
                                                    }

                                                    p.getInventory().setItemInMainHand(null);
                                                    p.sendRichMessage("<green>Выставлено за <gold>" + price + " <green>АР!");

                                                    new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            db.insertLot(new AuctionItem(
                                                                    -1, p.getUniqueId(), p.getName(),
                                                                    item.clone(), price, System.currentTimeMillis()
                                                            ));
                                                        }
                                                    }.runTaskAsynchronously(plugin);
                                                }
                                            }.runTask(plugin);
                                        }
                                    }.runTaskAsynchronously(plugin);

                                    return Command.SINGLE_SUCCESS;
                                }))
                );
    }
}
