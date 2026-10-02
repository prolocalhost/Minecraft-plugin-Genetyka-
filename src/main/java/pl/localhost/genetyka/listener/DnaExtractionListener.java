package pl.localhost.genetyka.listener;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;
import pl.localhost.genetyka.model.PlayerGenetics;

import java.util.Random;

public class DnaExtractionListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public DnaExtractionListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(killer);

        if (victim instanceof Player && !victim.equals(killer)) {
            int pts = plugin.getConfigManager().getInt("evolution.points-per-player-kill", 25);
            genetics.addEvolutionPoints(pts);
        } else if (!(victim instanceof Player)) {
            int pts = plugin.getConfigManager().getInt("evolution.points-per-mob-kill", 2);
            genetics.addEvolutionPoints(pts);
        }

        ItemStack mainHand = killer.getInventory().getItemInMainHand();
        ItemStack offHand = killer.getInventory().getItemInOffHand();
        boolean hasExtractor = plugin.getItemManager().isExtractor(mainHand) || plugin.getItemManager().isExtractor(offHand);

        if (!hasExtractor) return;

        DnaType type = DnaType.fromEntity(victim.getType());
        if (type == null) return;

        int chance = plugin.getConfigManager().getInt("extractor.base-drop-chance", 25);
        if (random.nextInt(100) < chance) {
            ItemStack sample = plugin.getItemManager().createDnaSample(type, 1);
            Location loc = victim.getLocation();

            if (killer.getInventory().firstEmpty() != -1) {
                killer.getInventory().addItem(sample);
            } else {
                loc.getWorld().dropItemNaturally(loc, sample);
            }

            killer.playSound(killer.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.2f);
            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 1.8f);
            loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.add(0, 1, 0), 15, 0.4, 0.4, 0.4, 0.1);

            String dnaName = plugin.getConfigManager().getMutationName(type);
            killer.sendMessage(plugin.getConfigManager().getMessage("dna-extracted").replace("%dna%", dnaName));
        }
    }
}
