package pl.localhost.genetyka.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;
import pl.localhost.genetyka.model.PlayerGenetics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeneticsManager {

    private final Main plugin;
    private final Map<UUID, PlayerGenetics> players = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public GeneticsManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/genetics.yml");
        loadAll();
    }

    public PlayerGenetics getGenetics(UUID uuid) {
        return players.computeIfAbsent(uuid, PlayerGenetics::new);
    }

    public PlayerGenetics getGenetics(Player player) {
        return getGenetics(player.getUniqueId());
    }

    public void loadAll() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Nie udalo sie utworzyc pliku data/genetics.yml: " + e.getMessage());
            }
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection sec = dataConfig.getConfigurationSection("players");
        if (sec == null) return;

        for (String uuidStr : sec.getKeys(false)) {
            try {
                UUID u = UUID.fromString(uuidStr);
                PlayerGenetics pg = new PlayerGenetics(u);
                pg.setEvolutionPoints(sec.getInt(uuidStr + ".points", 0));
                pg.setUnlockedSlots(sec.getInt(uuidStr + ".unlocked-slots", 1));

                ConfigurationSection slotsSec = sec.getConfigurationSection(uuidStr + ".active-slots");
                if (slotsSec != null) {
                    for (String slotKey : slotsSec.getKeys(false)) {
                        int slot = Integer.parseInt(slotKey);
                        DnaType type = DnaType.fromKey(slotsSec.getString(slotKey));
                        if (type != null) {
                            pg.equipMutation(slot, type);
                        }
                    }
                }

                ConfigurationSection levelsSec = sec.getConfigurationSection(uuidStr + ".mutation-levels");
                if (levelsSec != null) {
                    for (String mKey : levelsSec.getKeys(false)) {
                        DnaType type = DnaType.fromKey(mKey);
                        int lvl = levelsSec.getInt(mKey, 1);
                        if (type != null) {
                            pg.setMutationLevel(type, lvl);
                        }
                    }
                }

                players.put(u, pg);
            } catch (Exception e) {
                plugin.getLogger().warning("Blad wczytywania genetyki gracza: " + uuidStr);
            }
        }
    }

    public void saveAll() {
        if (dataConfig == null) {
            dataConfig = new YamlConfiguration();
        }

        dataConfig.set("players", null);
        for (Map.Entry<UUID, PlayerGenetics> entry : players.entrySet()) {
            String path = "players." + entry.getKey().toString();
            PlayerGenetics pg = entry.getValue();

            dataConfig.set(path + ".points", pg.getEvolutionPoints());
            dataConfig.set(path + ".unlocked-slots", pg.getUnlockedSlots());

            for (Map.Entry<Integer, DnaType> slotEntry : pg.getActiveSlots().entrySet()) {
                dataConfig.set(path + ".active-slots." + slotEntry.getKey(), slotEntry.getValue().getKey());
            }

            for (Map.Entry<DnaType, Integer> lvlEntry : pg.getMutationLevels().entrySet()) {
                dataConfig.set(path + ".mutation-levels." + lvlEntry.getKey().getKey(), lvlEntry.getValue());
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udalo sie zapisac data/genetics.yml: " + e.getMessage());
        }
    }
}
