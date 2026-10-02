package pl.localhost.genetyka.model;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum DnaType {
    WARDEN("warden", "&3&lMutacja Wardena", Material.ECHO_SHARD),
    ENDERMAN("enderman", "&5&lMutacja Endermana", Material.ENDER_EYE),
    CREEPER("creeper", "&a&lMutacja Creepera", Material.GUNPOWDER),
    PAJAK("pajak", "&c&lMutacja Pająka", Material.SPIDER_EYE),
    GOLEM("golem", "&f&lMutacja Żelaznego Golema", Material.IRON_BLOCK),
    BLAZE("blaze", "&6&lMutacja Blaze'a", Material.BLAZE_ROD),
    WITHER("wither", "&8&lMutacja Withera", Material.WITHER_SKELETON_SKULL),
    PHANTOM("phantom", "&9&lMutacja Phantoma", Material.PHANTOM_MEMBRANE);

    private final String key;
    private final String defaultName;
    private final Material defaultMaterial;

    DnaType(String key, String defaultName, Material defaultMaterial) {
        this.key = key;
        this.defaultName = defaultName;
        this.defaultMaterial = defaultMaterial;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public static DnaType fromKey(String key) {
        if (key == null) return null;
        for (DnaType type : values()) {
            if (type.key.equalsIgnoreCase(key) || type.name().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    public static DnaType fromEntity(EntityType entityType) {
        if (entityType == null) return null;
        switch (entityType) {
            case WARDEN:
                return WARDEN;
            case ENDERMAN:
                return ENDERMAN;
            case CREEPER:
                return CREEPER;
            case SPIDER:
            case CAVE_SPIDER:
                return PAJAK;
            case IRON_GOLEM:
                return GOLEM;
            case BLAZE:
                return BLAZE;
            case WITHER_SKELETON:
            case WITHER:
                return WITHER;
            case PHANTOM:
                return PHANTOM;
            default:
                return null;
        }
    }
}
