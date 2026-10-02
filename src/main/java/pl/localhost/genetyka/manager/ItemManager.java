package pl.localhost.genetyka.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {

    private final Main plugin;
    private final NamespacedKey itemTagKey;
    private final NamespacedKey dnaTypeKey;

    public ItemManager(Main plugin) {
        this.plugin = plugin;
        this.itemTagKey = new NamespacedKey(plugin, "genetyka_item");
        this.dnaTypeKey = new NamespacedKey(plugin, "genetyka_dna_type");
    }

    public ItemStack createExtractor(int amount) {
        Material mat = Material.PRISMARINE_SHARD;
        ItemStack item = new ItemStack(mat, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(plugin.getConfigManager().color(plugin.getConfigManager().getPrefix() + "&b&lStrzykawka DNA &7(Ekstraktor)"));

        List<String> rawLore = plugin.getConfig().getStringList("extractor.lore");
        List<String> formatted = new ArrayList<>();
        int chance = plugin.getConfigManager().getInt("extractor.base-drop-chance", 25);

        for (String s : rawLore) {
            formatted.add(plugin.getConfigManager().color(s.replace("%chance%", String.valueOf(chance))));
        }
        meta.setLore(formatted);

        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(itemTagKey, PersistentDataType.STRING, "extractor");

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createDnaSample(DnaType type, int amount) {
        Material mat = plugin.getConfigManager().getMutationMaterial(type);
        ItemStack item = new ItemStack(mat, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String name = plugin.getConfigManager().getMutationName(type);
        meta.setDisplayName(plugin.getConfigManager().color("&bPróbka: " + name));

        List<String> lore = new ArrayList<>();
        lore.add(plugin.getConfigManager().color("&7Czysta esencja kodu genetycznego."));
        lore.add(plugin.getConfigManager().color("&7Kliknij w &eLaboratorium Genetycznym &7(/dna),"));
        lore.add(plugin.getConfigManager().color("&7aby wstrzyknąć mutację do swojego organizmu!"));
        lore.add("");
        lore.add(plugin.getConfigManager().color(" &8» &bTyp organizmu: &f" + type.name()));
        lore.add(plugin.getConfigManager().color(" &8» &aGotowa do syntezy!"));
        lore.add("");
        lore.add(plugin.getConfigManager().color("&b&oPróbka DNA Organizmu"));

        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(itemTagKey, PersistentDataType.STRING, "dna_sample");
        pdc.set(dnaTypeKey, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    public boolean isExtractor(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        String val = meta.getPersistentDataContainer().get(itemTagKey, PersistentDataType.STRING);
        return "extractor".equals(val);
    }

    public boolean isDnaSample(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        String val = meta.getPersistentDataContainer().get(itemTagKey, PersistentDataType.STRING);
        return "dna_sample".equals(val);
    }

    public DnaType getDnaType(ItemStack item) {
        if (!isDnaSample(item)) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String typeName = meta.getPersistentDataContainer().get(dnaTypeKey, PersistentDataType.STRING);
        if (typeName == null) return null;
        try {
            return DnaType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void registerRecipes() {
        NamespacedKey rKey = new NamespacedKey(plugin, "recipe_extractor_needle");
        try {
            Bukkit.removeRecipe(rKey);
        } catch (Exception ignored) {}

        ShapedRecipe recipe = new ShapedRecipe(rKey, createExtractor(1));
        recipe.shape(" Q ", " G ", " B ");
        recipe.setIngredient('Q', Material.QUARTZ);
        recipe.setIngredient('G', Material.GLASS_BOTTLE);
        recipe.setIngredient('B', Material.IRON_INGOT);

        try {
            Bukkit.addRecipe(recipe);
        } catch (Exception ignored) {}
    }
}
