package pl.kkrpro.craftPayNotify.bstats;

import org.bstats.charts.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Metrics {

    private final org.bstats.bukkit.Metrics metrics;

    public Metrics(JavaPlugin plugin, int pluginId) {
        this.metrics = new org.bstats.bukkit.Metrics(plugin, pluginId);

    }

    private void registerCharts() {
        metrics.addCustomChart(new SimplePie("server_version", () -> {
            String version = Bukkit.getVersion();
            if (version.contains("Paper")) return "Paper";
            if (version.contains("Purpur")) return "Purpur";
            if (version.contains("Spigot")) return "Spigot";
            return "inny";
        }));
        metrics.addCustomChart(new MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));

        metrics.addCustomChart(new DrilldownPie("plugin_version", () -> {
            Map<String, Map <String, Integer>> map = new HashMap<>();
            Map<String, Integer> entry = new HashMap<>();
            entry.put("1", 1);
            map.put("1.0.0" , entry);
            return map;
        }));


    }
    public void Log(String message) {
        Bukkit.getLogger().info("[bStats] " + message);
    }
}
