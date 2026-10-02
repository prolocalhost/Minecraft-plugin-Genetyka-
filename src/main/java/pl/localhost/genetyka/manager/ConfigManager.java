package pl.localhost.genetyka.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPrefix() {
        return color(config.getString("prefix", "&8[&b&lGenetyka&8] &r"));
    }

    public String getMessage(String key) {
        String msg = config.getString("messages." + key);
        if (msg == null) return color("&cBrak wiadomosci: " + key);
        return color(getPrefix() + msg);
    }

    public String getMutationName(DnaType type) {
        String path = "mutations." + type.getKey() + ".name";
        return color(config.getString(path, type.getDefaultName()));
    }

    public Material getMutationMaterial(DnaType type) {
        String path = "mutations." + type.getKey() + ".material";
        String mat = config.getString(path);
        if (mat != null) {
            try {
                return Material.valueOf(mat.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return type.getDefaultMaterial();
    }

    public List<String> getMutationLore(DnaType type, Map<String, String> placeholders) {
        String path = "mutations." + type.getKey() + ".lore";
        List<String> raw = config.getStringList(path);
        List<String> res = new ArrayList<>();

        for (String line : raw) {
            String processed = line;
            if (placeholders != null) {
                for (Map.Entry<String, String> e : placeholders.entrySet()) {
                    processed = processed.replace("%" + e.getKey() + "%", e.getValue());
                }
            }
            res.add(color(processed));
        }
        return res;
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
}
