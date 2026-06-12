package pl.kkrpro.craftPayNotify;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kkrpro.craftPayNotify.commands.InjectCommand;
import pl.kkrpro.craftPayNotify.commands.ItemShopCommand;
import pl.kkrpro.craftPayNotify.config.PluginConfig;
import pl.kkrpro.craftPayNotify.config.repository.CraftPayRepository;
import pl.kkrpro.craftPayNotify.database.Database;

public final class Main extends JavaPlugin {

    private PluginConfig config;
    private Database database;

    private CraftPayRepository craftPayRepository;

    private LiteCommands<?> lite;

    @Override
    public void onEnable() {

        try {
            this.config = ConfigManager.create(PluginConfig.class, it -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(this.getDataFolder().toPath().resolve("config.yml").toFile());
                it.saveDefaults();
                it.load(true);
            });

            this.database = new Database(this.config);

            this.craftPayRepository = new CraftPayRepository(database.getDataSource());

            this.lite = LiteBukkitFactory.builder()
                    .commands(
                            new InjectCommand(craftPayRepository),
                            new ItemShopCommand(craftPayRepository)
                    )
                    .build();

            getLogger().info(getName() + " został poprawnie włączony!");

        } catch (Exception ex){
            getLogger().severe("Błąd startu: " + ex.getMessage());
            ex.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        try {
            if (lite != null) {
                lite.unregister();
            }
        } catch (Exception ignored) {}

        try {
            if (database != null) {
                database.close();
            }
        } catch (Exception ignored) {}
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public CraftPayRepository getCraftPayRepository() {
        return craftPayRepository;
    }
}
