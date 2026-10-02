package pl.localhost.genetyka;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.localhost.genetyka.command.GeneticsCommand;
import pl.localhost.genetyka.gui.GeneticsGUI;
import pl.localhost.genetyka.listener.DnaExtractionListener;
import pl.localhost.genetyka.listener.MutationAbilitiesListener;
import pl.localhost.genetyka.manager.ConfigManager;
import pl.localhost.genetyka.manager.GeneticsManager;
import pl.localhost.genetyka.manager.ItemManager;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private GeneticsManager geneticsManager;
    private ItemManager itemManager;
    private GeneticsGUI geneticsGUI;
    private BukkitTask autoSaveTask;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.geneticsManager = new GeneticsManager(this);
        this.itemManager = new ItemManager(this);
        this.geneticsGUI = new GeneticsGUI(this);

        GeneticsCommand cmd = new GeneticsCommand(this);
        if (getCommand("genetyka") != null) {
            getCommand("genetyka").setExecutor(cmd);
            getCommand("genetyka").setTabCompleter(cmd);
        }

        getServer().getPluginManager().registerEvents(new DnaExtractionListener(this), this);
        getServer().getPluginManager().registerEvents(new MutationAbilitiesListener(this), this);
        getServer().getPluginManager().registerEvents(geneticsGUI, this);

        this.itemManager.registerRecipes();

        this.autoSaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (geneticsManager != null) {
                geneticsManager.saveAll();
            }
        }, 6000L, 6000L);

        getLogger().info("localhost-genetyka zostal pomyslnie wlaczony!");
    }

    @Override
    public void onDisable() {
        if (this.autoSaveTask != null) {
            this.autoSaveTask.cancel();
        }
        if (this.geneticsManager != null) {
            this.geneticsManager.saveAll();
        }
        getLogger().info("localhost-genetyka zostal wylaczony, dane zapisane!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GeneticsManager getGeneticsManager() {
        return geneticsManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public GeneticsGUI getGeneticsGUI() {
        return geneticsGUI;
    }
}
