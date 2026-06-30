package me.namioll.vanillaAuc;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "auction.db");
            plugin.getDataFolder().mkdirs();

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            plugin.getLogger().info("Подключение к БД успешно!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS auction (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    seller_uuid TEXT NOT NULL,
                    seller_name TEXT NOT NULL,
                    item_data BLOB NOT NULL,
                    price INTEGER NOT NULL,
                    listed_at INTEGER NOT NULL
                )
                """;
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка создания таблицы: " + e.getMessage());
        }
    }

    public void insertLot(AuctionItem lot) {
        String sql = "INSERT INTO auction (seller_uuid, seller_name, item_data, price, listed_at) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, lot.sellerUuid().toString());
            stmt.setString(2, lot.sellerName());
            stmt.setBytes(3, serializeItem(lot.item()));
            stmt.setInt(4, lot.price());
            stmt.setLong(5, lot.listedAt());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка добавления лота: " + e.getMessage());
        }
    }

    public int getSellersCount(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM auction WHERE seller_uuid = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка получения лотов: " + e.getMessage());
        }
        return 0;
    }

    public List<AuctionItem> getAllLots() {
        List<AuctionItem> lots = new ArrayList<>();
        String sql = "SELECT * FROM auction ORDER BY listed_at DESC";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                UUID sellerUuid = UUID.fromString(rs.getString("seller_uuid"));
                String sellerName = rs.getString("seller_name");
                ItemStack item = deserializeItem(rs.getBytes("item_data"));
                int price = rs.getInt("price");
                long listedAt = rs.getLong("listed_at");

                lots.add(new AuctionItem(id, sellerUuid, sellerName, item, price, listedAt));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка получения лотов: " + e.getMessage());
        }

        return lots;
    }

    public List<AuctionItem> getSellerSlots(UUID uuid) {
        List<AuctionItem> lots = new ArrayList<>();
        String sql = "SELECT * FROM auction WHERE seller_uuid = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                UUID sellerUuid = UUID.fromString(rs.getString("seller_uuid"));
                String sellerName = rs.getString("seller_name");
                ItemStack item = deserializeItem(rs.getBytes("item_data"));
                int price = rs.getInt("price");
                long listedAt = rs.getLong("listed_at");

                lots.add(new AuctionItem(id, sellerUuid, sellerName, item, price, listedAt));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка получения слотов продавца: " + e.getMessage());
        }

        return lots;
    }

    public void deleteLot(int id) {
        String sql = "DELETE FROM auction WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка удаления лота: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка закрытия БД: " + e.getMessage());
        }
    }

    private byte[] serializeItem(ItemStack item) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeObject(item);
            return baos.toByteArray();
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сериализации предмета: " + e.getMessage());
            return new byte[0];
        }
    }

    private ItemStack deserializeItem(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            return (ItemStack) bois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().severe("Ошибка десериализации предмета: " + e.getMessage());
            return null;
        }
    }
}
