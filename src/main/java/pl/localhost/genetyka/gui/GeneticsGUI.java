package pl.localhost.genetyka.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;
import pl.localhost.genetyka.model.PlayerGenetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticsGUI implements Listener {

    private final Main plugin;
    private static final String GUI_IDENTIFIER = "§0§9";

    public GeneticsGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);
        String title = plugin.getConfigManager().color("&8» &b&lLaboratorium Genetyczne") + GUI_IDENTIFIER;
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack cyanAccent = createItem(Material.CYAN_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, border);
        }

        int[] accents = {0, 8, 9, 17, 36, 44, 45, 53};
        for (int slot : accents) {
            inv.setItem(slot, cyanAccent);
        }

        ItemStack info = new ItemStack(Material.BEACON);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(plugin.getConfigManager().color("&b&lTwoje Dane Genetyczne"));
            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().color("&7Witaj w centrum inzynierii genetycznej!"));
            lore.add("");
            lore.add(plugin.getConfigManager().color(" &8» &ePunkty Ewolucji: &b" + genetics.getEvolutionPoints() + " pkt"));
            lore.add(plugin.getConfigManager().color(" &8» &eOdblokowane sloty: &a" + genetics.getUnlockedSlots() + " / 3"));
            lore.add("");
            lore.add(plugin.getConfigManager().color("&7Zabijaj moby i graczy, aby zdobywac &bPunkty Ewolucji&7."));
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(4, info);

        int[] chromosomeSlots = {20, 22, 24};
        int unlocked = genetics.getUnlockedSlots();

        for (int i = 1; i <= 3; i++) {
            int guiSlot = chromosomeSlots[i - 1];

            if (i > unlocked) {
                int unlockCost = (i == 2 ? plugin.getConfigManager().getInt("evolution.unlock-slot-2-cost", 150)
                                         : plugin.getConfigManager().getInt("evolution.unlock-slot-3-cost", 350));
                ItemStack locked = new ItemStack(Material.BARRIER);
                ItemMeta meta = locked.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(plugin.getConfigManager().color("&c&lSlot Chromosomu #" + i + " &7(Zablokowany)"));
                    List<String> lore = new ArrayList<>();
                    lore.add(plugin.getConfigManager().color("&7Ten slot wymaga odblokowania."));
                    lore.add("");
                    lore.add(plugin.getConfigManager().color(" &8» &7Koszt odblokowania: &e" + unlockCost + " Punktów Ewolucji"));
                    lore.add(plugin.getConfigManager().color(" &8» &7Twój stan konta: &b" + genetics.getEvolutionPoints() + " pkt"));
                    lore.add("");
                    lore.add(plugin.getConfigManager().color("&eKliknij, aby odblokować slot!"));
                    meta.setLore(lore);
                    locked.setItemMeta(meta);
                }
                inv.setItem(guiSlot, locked);
            } else {
                DnaType active = genetics.getActiveSlot(i);
                if (active == null) {
                    ItemStack empty = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                    ItemMeta meta = empty.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(plugin.getConfigManager().color("&a&lSlot Chromosomu #" + i + " &7(Pusty)"));
                        List<String> lore = new ArrayList<>();
                        lore.add(plugin.getConfigManager().color("&7Brak aktywnej mutacji w tym slocie."));
                        lore.add("");
                        lore.add(plugin.getConfigManager().color("&eWybierz gen z dolnej listy, aby go wstrzyknąć!"));
                        meta.setLore(lore);
                        empty.setItemMeta(meta);
                    }
                    inv.setItem(guiSlot, empty);
                } else {
                    int lvl = genetics.getMutationLevel(active);
                    Material mat = plugin.getConfigManager().getMutationMaterial(active);
                    ItemStack equipped = new ItemStack(mat);
                    ItemMeta meta = equipped.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfigManager().getMutationName(active) + " &7(Slot #" + i + ")"));

                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("level", String.valueOf(lvl));
                        placeholders.put("sonar_radius", String.valueOf(18 + (lvl * 5)));
                        placeholders.put("dodge", String.valueOf(lvl == 1 ? 15 : (lvl == 2 ? 25 : 35)));
                        placeholders.put("kb_res", String.valueOf(lvl == 1 ? 30 : (lvl == 2 ? 50 : 75)));
                        placeholders.put("cooldown", "45");

                        List<String> lore = plugin.getConfigManager().getMutationLore(active, placeholders);
                        lore.add("");
                        lore.add(plugin.getConfigManager().color("&aAktualny poziom: &b" + lvl + " / 3"));
                        if (lvl < 3) {
                            int upCost = (lvl == 1 ? plugin.getConfigManager().getInt("evolution.upgrade-cost-level-2", 100)
                                                   : plugin.getConfigManager().getInt("evolution.upgrade-cost-level-3", 250));
                            lore.add(plugin.getConfigManager().color("&ePrawy klik: &aUlepsz do Poz. " + (lvl + 1) + " &7(Koszt: &e" + upCost + " pkt&7)"));
                        } else {
                            lore.add(plugin.getConfigManager().color("&6&lMAKSYMALNY POZIOM MUTACJI!"));
                        }
                        lore.add(plugin.getConfigManager().color("&cLewy klik: &7Wyjmij / dezaktywuj gen"));
                        meta.setLore(lore);
                        equipped.setItemMeta(meta);
                    }
                    inv.setItem(guiSlot, equipped);
                }
            }
        }

        DnaType[] allTypes = DnaType.values();
        int startSlot = 38;
        for (int i = 0; i < allTypes.length && i < 8; i++) {
            DnaType type = allTypes[i];
            boolean isActive = genetics.isMutationActive(type);
            int lvl = genetics.getKnownLevel(type);

            Material mat = plugin.getConfigManager().getMutationMaterial(type);
            ItemStack selector = new ItemStack(mat);
            ItemMeta meta = selector.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfigManager().getMutationName(type)));
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getConfigManager().color("&7Kod genetyczny: &f" + type.name()));
                lore.add(plugin.getConfigManager().color("&7Osiągnięty poziom: &b" + lvl + " / 3"));
                lore.add("");
                if (isActive) {
                    lore.add(plugin.getConfigManager().color("&a✔ AKTUALNIE AKTYWNY"));
                } else {
                    lore.add(plugin.getConfigManager().color("&eKliknij, aby aktywować w wolnym slocie!"));
                }
                meta.setLore(lore);
                selector.setItemMeta(meta);
            }
            inv.setItem(startSlot + i, selector);
        }

        ItemStack close = createItem(Material.BARRIER, "&c&lZamknij Laboratorium");
        inv.setItem(49, close);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 1.4f);
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().color(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith(GUI_IDENTIFIER)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        PlayerGenetics genetics = plugin.getGeneticsManager().getGenetics(player);

        int slot = event.getRawSlot();
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        if (slot == 20 || slot == 22 || slot == 24) {
            int chromoSlot = (slot == 20 ? 1 : (slot == 22 ? 2 : 3));
            int unlocked = genetics.getUnlockedSlots();

            if (chromoSlot > unlocked) {
                int cost = (chromoSlot == 2 ? plugin.getConfigManager().getInt("evolution.unlock-slot-2-cost", 150)
                                            : plugin.getConfigManager().getInt("evolution.unlock-slot-3-cost", 350));
                if (genetics.removeEvolutionPoints(cost)) {
                    genetics.unlockNextSlot();
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
                    player.sendMessage(plugin.getConfigManager().getMessage("slot-unlocked"));
                    openGUI(player);
                } else {
                    player.sendMessage(plugin.getConfigManager().getMessage("not-enough-points")
                            .replace("%cost%", String.valueOf(cost))
                            .replace("%points%", String.valueOf(genetics.getEvolutionPoints())));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
                }
                return;
            }

            DnaType active = genetics.getActiveSlot(chromoSlot);
            if (active != null) {
                if (event.isRightClick()) {
                    int currentLvl = genetics.getKnownLevel(active);
                    if (currentLvl >= 3) {
                        player.sendMessage(plugin.getConfigManager().getMessage("mutation-max-level"));
                        return;
                    }

                    int cost = (currentLvl == 1 ? plugin.getConfigManager().getInt("evolution.upgrade-cost-level-2", 100)
                                                : plugin.getConfigManager().getInt("evolution.upgrade-cost-level-3", 250));

                    if (genetics.removeEvolutionPoints(cost)) {
                        genetics.upgradeMutation(active);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.4f);
                        player.sendMessage(plugin.getConfigManager().getMessage("mutation-upgraded")
                                .replace("%mutation%", plugin.getConfigManager().getMutationName(active))
                                .replace("%level%", String.valueOf(currentLvl + 1)));
                        openGUI(player);
                    } else {
                        player.sendMessage(plugin.getConfigManager().getMessage("not-enough-points")
                                .replace("%cost%", String.valueOf(cost))
                                .replace("%points%", String.valueOf(genetics.getEvolutionPoints())));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
                    }
                } else {
                    genetics.unequipMutation(chromoSlot);
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.8f, 0.9f);
                    player.sendMessage(plugin.getConfigManager().getMessage("mutation-unequipped")
                            .replace("%mutation%", plugin.getConfigManager().getMutationName(active)));
                    openGUI(player);
                }
            }
            return;
        }

        if (slot >= 38 && slot <= 45) {
            int index = slot - 38;
            DnaType[] allTypes = DnaType.values();
            if (index < allTypes.length) {
                DnaType selected = allTypes[index];
                if (genetics.isMutationActive(selected)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
                    return;
                }

                int freeSlot = -1;
                for (int i = 1; i <= genetics.getUnlockedSlots(); i++) {
                    if (genetics.getActiveSlot(i) == null) {
                        freeSlot = i;
                        break;
                    }
                }

                if (freeSlot != -1) {
                    genetics.equipMutation(freeSlot, selected);
                    player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.2f);
                    player.sendMessage(plugin.getConfigManager().getMessage("mutation-equipped")
                            .replace("%mutation%", plugin.getConfigManager().getMutationName(selected))
                            .replace("%level%", String.valueOf(genetics.getMutationLevel(selected))));
                    openGUI(player);
                } else {
                    player.sendMessage(plugin.getConfigManager().color("&cWszystkie odblokowane sloty chromosomow sa zajete! Odblokuj nowy slot lub zdejmij mutacje."));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
                }
            }
        }
    }
}
