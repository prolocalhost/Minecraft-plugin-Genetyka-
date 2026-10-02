package pl.localhost.genetyka.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;
import pl.localhost.genetyka.model.PlayerGenetics;

import java.util.*;

public class MutationAbilitiesListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    private final Map<UUID, Long> sonarCooldown = new HashMap<>();
    private final Map<UUID, Long> sonicBoomCooldown = new HashMap<>();
    private final Map<UUID, Long> creeperBlastCooldown = new HashMap<>();

    public MutationAbilitiesListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;

        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);
        if (!genetics.isMutationActive(DnaType.WARDEN)) return;

        long now = System.currentTimeMillis();
        long last = sonarCooldown.getOrDefault(player.getUniqueId(), 0L);
        int cdSec = plugin.getConfigManager().getInt("mutations.warden.sonar-cooldown", 8);

        if (now - last < (cdSec * 1000L)) return;
        sonarCooldown.put(player.getUniqueId(), now);

        int lvl = genetics.getMutationLevel(DnaType.WARDEN);
        int radius = plugin.getConfigManager().getInt("mutations.warden.sonar-radius", 18) + (lvl * 5);
        int durSec = plugin.getConfigManager().getInt("mutations.warden.sonar-glowing-duration", 4);

        int detected = 0;
        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof LivingEntity && !e.equals(player)) {
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durSec * 20, 0, false, false));
                detected++;
            }
        }

        Location loc = player.getLocation().add(0, 0.2, 0);
        player.getWorld().playSound(loc, Sound.ENTITY_WARDEN_HEARTBEAT, 1.2f, 1.0f);
        player.getWorld().spawnParticle(Particle.SCULK_SOUL, loc, 25, radius / 4.0, 0.2, radius / 4.0, 0.05);

        player.sendMessage(plugin.getConfigManager().getMessage("sonar-activated")
                .replace("%count%", String.valueOf(detected)));
    }

    @EventHandler
    public void onSonicBoom(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);
        if (!genetics.isMutationActive(DnaType.WARDEN)) return;

        long now = System.currentTimeMillis();
        long last = sonicBoomCooldown.getOrDefault(player.getUniqueId(), 0L);
        int cdSec = plugin.getConfigManager().getInt("mutations.warden.sonic-boom-cooldown", 20);

        if (now - last < (cdSec * 1000L)) {
            long rem = ((cdSec * 1000L) - (now - last)) / 1000L;
            player.sendMessage(plugin.getConfigManager().getMessage("sonic-boom-cooldown")
                    .replace("%time%", String.valueOf(rem)));
            return;
        }

        sonicBoomCooldown.put(player.getUniqueId(), now);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().normalize();

        player.getWorld().playSound(start, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.5f, 1.0f);
        player.sendMessage(plugin.getConfigManager().getMessage("sonic-boom-cast"));

        double dmg = plugin.getConfigManager().getDouble("mutations.warden.sonic-boom-damage", 8.0);
        for (double d = 1.0; d <= 16.0; d += 1.0) {
            Location point = start.clone().add(dir.clone().multiply(d));
            point.getWorld().spawnParticle(Particle.SONIC_BOOM, point, 1);

            for (Entity e : point.getWorld().getNearbyEntities(point, 1.5, 1.5, 1.5)) {
                if (e instanceof LivingEntity && !e.equals(player)) {
                    LivingEntity target = (LivingEntity) e;
                    target.damage(dmg, player);
                    target.setVelocity(dir.clone().multiply(1.5).setY(0.5));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();

        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(victim);
        if (!genetics.isMutationActive(DnaType.ENDERMAN)) return;

        if (event.getDamager() instanceof Projectile) {
            int lvl = genetics.getMutationLevel(DnaType.ENDERMAN);
            int chance = (lvl == 1 ? 15 : (lvl == 2 ? 25 : 35));

            if (random.nextInt(100) < chance) {
                event.setCancelled(true);
                Location loc = victim.getLocation();
                double angle = random.nextDouble() * 2 * Math.PI;
                Location target = loc.clone().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);

                victim.teleport(target);
                victim.getWorld().playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
                victim.getWorld().spawnParticle(Particle.PORTAL, target.add(0, 1, 0), 20, 0.4, 0.4, 0.4);
                victim.sendMessage(plugin.getConfigManager().color("&5&l[UNIK] &7Zagiąłeś przestrzeń i uniknąłeś pocisku!"));
            }
        }
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        Player player = event.getPlayer();
        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);

        if (genetics.isMutationActive(DnaType.ENDERMAN)) {
            player.setFallDistance(0f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreeperDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);
        if (!genetics.isMutationActive(DnaType.CREEPER)) return;

        double finalHp = player.getHealth() - event.getFinalDamage();
        if (finalHp <= 4.0) {
            long now = System.currentTimeMillis();
            long last = creeperBlastCooldown.getOrDefault(player.getUniqueId(), 0L);
            int cdSec = plugin.getConfigManager().getInt("mutations.creeper.blast-cooldown", 45);

            if (now - last >= (cdSec * 1000L)) {
                creeperBlastCooldown.put(player.getUniqueId(), now);
                Location loc = player.getLocation();

                loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 0.5, 0.5, 0.5, 0.1);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);

                double power = plugin.getConfigManager().getDouble("mutations.creeper.blast-power", 3.5);
                for (Entity e : player.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof LivingEntity && !e.equals(player)) {
                        LivingEntity target = (LivingEntity) e;
                        Vector v = target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.8).setY(0.7);
                        target.setVelocity(v);
                        target.damage(power * 2.0);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                    }
                }
                player.sendMessage(plugin.getConfigManager().getMessage("creeper-blast"));
            }
        }
    }

    @EventHandler
    public void onSpiderMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);

        if (genetics.isMutationActive(DnaType.PAJAK)) {
            if (player.isSneaking()) {
                Block front = player.getTargetBlockExact(1);
                if (front != null && front.getType().isSolid()) {
                    player.setVelocity(new Vector(0, 0.28, 0));
                    if (random.nextInt(10) == 0) {
                        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 4, 0.2, 0.1, 0.2, front.getBlockData());
                    }
                }
            }

            if (player.getLocation().getBlock().getType() == Material.COBWEB) {
                Vector v = player.getLocation().getDirection().multiply(0.3).setY(0.05);
                player.setVelocity(v);
            }
        }

        if (genetics.isMutationActive(DnaType.PHANTOM)) {
            if (player.isSneaking() && player.getFallDistance() > 2.5 && player.getVelocity().getY() < -0.3) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0, false, false));
                if (random.nextInt(4) == 0) {
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.1, 0), 2, 0.2, 0.0, 0.2, 0.01);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMelee(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player attacker = (Player) event.getDamager();
            LivingEntity victim = (LivingEntity) event.getEntity();
            PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(attacker);

            if (genetics.isMutationActive(DnaType.PAJAK)) {
                int chance = plugin.getConfigManager().getInt("mutations.pajak.poison-chance", 20);
                if (random.nextInt(100) < chance) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
                    victim.getWorld().spawnParticle(Particle.SPELL_MOB, victim.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2);
                }
            }

            if (genetics.isMutationActive(DnaType.WITHER)) {
                int chance = plugin.getConfigManager().getInt("mutations.wither.wither-chance", 25);
                if (random.nextInt(100) < chance) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 1));
                    victim.getWorld().spawnParticle(Particle.SMOKE_NORMAL, victim.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2);
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.7f, 1.5f);
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(victim);

            if (genetics.isMutationActive(DnaType.BLAZE)) {
                if (event.getDamager() instanceof LivingEntity) {
                    LivingEntity attacker = (LivingEntity) event.getDamager();
                    int dur = plugin.getConfigManager().getInt("mutations.blaze.thorns-burn-seconds", 4);
                    attacker.setFireTicks(dur * 20);
                    attacker.getWorld().spawnParticle(Particle.FLAME, attacker.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.05);
                }
            }

            if (genetics.isMutationActive(DnaType.GOLEM)) {
                int lvl = genetics.getMutationLevel(DnaType.GOLEM);
                double kbFactor = (lvl == 1 ? 0.7 : (lvl == 2 ? 0.5 : 0.25));
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    victim.setVelocity(victim.getVelocity().multiply(kbFactor));
                }, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageImmunities(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (genetics.isMutationActive(DnaType.BLAZE)) {
            if (cause == EntityDamageEvent.DamageCause.FIRE ||
                cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                cause == EntityDamageEvent.DamageCause.LAVA ||
                cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                event.setCancelled(true);
                player.setFireTicks(0);
                return;
            }
        }

        if (genetics.isMutationActive(DnaType.WITHER)) {
            if (cause == EntityDamageEvent.DamageCause.WITHER) {
                event.setCancelled(true);
                player.removePotionEffect(PotionEffectType.WITHER);
                return;
            }
        }

        if (cause == EntityDamageEvent.DamageCause.FALL && genetics.isMutationActive(DnaType.GOLEM)) {
            if (player.getFallDistance() >= 4.0) {
                Location loc = player.getLocation();
                loc.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.2f, 0.6f);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 2);

                for (Entity e : player.getNearbyEntities(5, 3, 5)) {
                    if (e instanceof LivingEntity && !e.equals(player)) {
                        LivingEntity target = (LivingEntity) e;
                        Vector v = target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.4).setY(0.6);
                        target.setVelocity(v);
                        target.damage(4.0, player);
                    }
                }
            }
        }
    }
}
