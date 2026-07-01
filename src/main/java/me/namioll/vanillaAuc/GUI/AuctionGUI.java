package me.namioll.vanillaAuc.GUI;

import me.namioll.vanillaAuc.AuctionItem;
import me.namioll.vanillaAuc.Holders.AuctionHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final int[] SIDES = {9,18,27,36,17,26,35,44};
    private static final int[] CORNERS = {0, 8, 45, 53};
    private static final int[] ITEM_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34,
            37,38,39,40,41,42,43
    };

    public static Inventory createAuctionGUI(List<AuctionItem> lots, int page){

        int lastpage = lots.size()/ITEM_SLOTS.length + (lots.size()%ITEM_SLOTS.length==0?0:1) - 1;

        Inventory inv = Bukkit.createInventory(new AuctionHolder(lots, page), 54, mm.deserialize("<bold><gradient:#FFD700:#FFA500>Аукцион</gradient>"));

        ItemStack yellow_glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = yellow_glass.getItemMeta();
        meta.displayName(Component.empty());
        yellow_glass.setItemMeta(meta);

        ItemStack gray_glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        gray_glass.setItemMeta(meta);

        ItemStack vpered = new ItemStack(Material.LIME_DYE);
        ItemMeta metavpered = vpered.getItemMeta();
        metavpered.displayName(mm.deserialize("<!italic><bold><green>Следующая страница »"));
        List<Component> loreVpered = new ArrayList<>();
        loreVpered.add(mm.deserialize("<!italic><gray>Страница <white>" + (page + 2) + "</white> из <white>" + lastpage));
        metavpered.lore(loreVpered);
        vpered.setItemMeta(metavpered);

        ItemStack nazad = new ItemStack(Material.RED_DYE);
        ItemMeta metarnazad = nazad.getItemMeta();
        metarnazad.displayName(mm.deserialize("<!italic><bold><red>« Предыдущая страница"));
        List<Component> loreNazad = new ArrayList<>();
        loreNazad.add(mm.deserialize("<!italic><gray>Страница <white>" + page + "</white> из <white>" + lastpage));
        metarnazad.lore(loreNazad);
        nazad.setItemMeta(metarnazad);

        for (int i : CORNERS) inv.setItem(i, yellow_glass);
        for (int i : SIDES) inv.setItem(i, gray_glass);

        for (int i = 1; i < 8; i++) inv.setItem(i, gray_glass);
        for (int i = 46; i < 53; i++) inv.setItem(i, gray_glass);

        ItemStack syndyk = new ItemStack(Material.CHEST);

        ItemMeta metasyndyk = syndyk.getItemMeta();
        metasyndyk.displayName(mm.deserialize("<!italic><gradient:#FFD700:#FF8C00><bold>Мои товары</bold></gradient>"));
        List<Component> loreSyndyk = new ArrayList<>();
        loreSyndyk.add(Component.empty());
        loreSyndyk.add(mm.deserialize("<!italic><gray>Список Ваших товаров (в том числе истёкших)."));
        metasyndyk.lore(loreSyndyk);
        syndyk.setItemMeta(metasyndyk);

        inv.setItem(49, syndyk);

        int startIndex = page*28;
        long remained = 3 * 24 * 60 * 60 * 1000;

        for (int i = 0; i < ITEM_SLOTS.length; i++) {
            int lotIndex = startIndex + i;
            if (lotIndex >= lots.size()) break;

            AuctionItem item = lots.get(lotIndex);
            ItemStack itemStack = item.item();
            ItemMeta metaitem = itemStack.getItemMeta();

            long expiresAt = item.listedAt() + remained;
            long timeLeft = expiresAt - System.currentTimeMillis();

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize("<!italic><gray>――――――――――――――――――"));
            lore.add(mm.deserialize("<!italic><yellow>Продавец: <green>" + item.sellerName()));
            lore.add(mm.deserialize("<!italic><yellow>Цена: <gold>" + item.price() + " АР"));
            lore.add(mm.deserialize("<!italic><yellow>Осталось: <red>" + formatTime(timeLeft)));
            lore.add(mm.deserialize("<!italic><gray>――――――――――――――――――"));
            lore.add(mm.deserialize("<!italic><gray>ЛКМ <white>— купить"));
            metaitem.lore(lore);

            itemStack.setItemMeta(metaitem);
            inv.setItem(ITEM_SLOTS[i], itemStack);
        }

        if (page > 0) inv.setItem(48, nazad);
        if (page < lastpage) inv.setItem(50, vpered);

        return inv;
    }

    private static String formatTime(long millis) {
        long totalSeconds = millis / 1000;                // переводим в секунды
        long hours = totalSeconds / 3600;                 // сколько целых часов
        long minutes = (totalSeconds % 3600) / 60;        // остаток после часов, переведённый в минуты
        long seconds = totalSeconds % 60;                 // что осталось после минут
        String timeLeft = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return timeLeft;
    }
}
