package me.namioll.vanillaAuc;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public record AuctionItem(
        int id,
        UUID sellerUuid,
        String sellerName,
        ItemStack item,
        int price,
        long listedAt
) {}
