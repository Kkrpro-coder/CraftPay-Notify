package pl.kkrpro.craftPayNotify.config.repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class CraftPayRepository {

    public CraftPayRepository(DataSource ds) {
        this.ds = ds;
    }

    public record Item(int id, String name , String command, Timestamp createAt) {}

    private final DataSource ds;

    public void createItem(String name, String command) throws SQLException {
        try (var c = ds.getConnection();
             var ps = c.prepareStatement(
                     "INSERT INTO itemshop_items(name, command) VALUES (?, ?)"
             )) {
            ps.setString(1, name);
            ps.setString(2, command);
            ps.executeUpdate();
        }
    }
    public Optional<Item> findByName(String name) throws SQLException {

        try (var c = ds.getConnection();
             var ps = c.prepareStatement(
                     "SELECT * FROM itemshop_items WHERE name=? LIMIT 1"
             )) {

            ps.setString(1, name);

            try (var rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("command"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
    }
    public void deleteItem(String name) throws SQLException {
        try (var c = ds.getConnection();
            var ps = c.prepareStatement(
                    "DELETE FROM itemshop_items WHERE name=?"
            )) {
            ps.setString(1, name);

            int deleted = ps.executeUpdate();

            if (deleted == 0) {
                throw new IllegalStateException("Nie znaleziono itemu");
            }
        }
    }

    public void updateItem(String name, String newCommand) throws SQLException {

        try (var c = ds.getConnection();
             var ps = c.prepareStatement(
                     "UPDATE itemshop_items SET command=? WHERE name=?"
        )) {
            ps.setString(1, newCommand);
            ps.setString(2, name);

            int updated = ps.executeUpdate();

            if (updated == 0) {
                throw new IllegalStateException("Nie znaleziono itemu.");
            }
        }
    }

    public void logPurchase(String uuid, String itemName) throws SQLException {
        try (var c = ds.getConnection();
             var ps = c.prepareStatement(
                     "INSERT INTO itemshop_purchases(player_uuid, item_name) VALUES (? , ?)"
             )) {
            ps.setString(1, uuid);
            ps.setString(2, itemName);
            ps.executeUpdate();
        }
    }

    public boolean exists(String name) throws SQLException {

        try (var c = ds.getConnection();
            var ps = c.prepareStatement(
                "SELECT 1 FROM itemshop_items WHERE name=? LIMIT 1"
            )) {

            ps.setString(1, name);

            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void inject() throws SQLException {
        try (var c = ds.getConnection()) {
            c.setAutoCommit(false);

            try (var st = c.createStatement()) {
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS itemshop_items (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(32) UNIQUE NOT NULL,
                        command TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)""");

                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS itemshop_purchases (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        player_uuid VARCHAR(36) NOT NULL,
                        item_name VARCHAR(32) NOT NULL,
                        purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )""");

                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
    public record item(
            int id,
            String name,
            String command,
            Timestamp createdAt
    ) {}
}
