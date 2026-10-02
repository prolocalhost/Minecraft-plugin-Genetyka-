package pl.localhost.genetyka.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerGenetics {

    private final UUID uuid;
    private int evolutionPoints;
    private int unlockedSlots;
    private final Map<Integer, DnaType> activeSlots = new HashMap<>();
    private final Map<DnaType, Integer> mutationLevels = new HashMap<>();

    public PlayerGenetics(UUID uuid) {
        this.uuid = uuid;
        this.evolutionPoints = 0;
        this.unlockedSlots = 1;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getEvolutionPoints() {
        return evolutionPoints;
    }

    public void setEvolutionPoints(int evolutionPoints) {
        this.evolutionPoints = Math.max(0, evolutionPoints);
    }

    public void addEvolutionPoints(int amount) {
        this.evolutionPoints += amount;
    }

    public boolean removeEvolutionPoints(int amount) {
        if (this.evolutionPoints >= amount) {
            this.evolutionPoints -= amount;
            return true;
        }
        return false;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public void setUnlockedSlots(int unlockedSlots) {
        this.unlockedSlots = Math.min(3, Math.max(1, unlockedSlots));
    }

    public boolean unlockNextSlot() {
        if (this.unlockedSlots < 3) {
            this.unlockedSlots++;
            return true;
        }
        return false;
    }

    public Map<Integer, DnaType> getActiveSlots() {
        return activeSlots;
    }

    public DnaType getActiveSlot(int slot) {
        return activeSlots.get(slot);
    }

    public boolean isMutationActive(DnaType type) {
        return activeSlots.containsValue(type);
    }

    public boolean equipMutation(int slot, DnaType type) {
        if (slot < 1 || slot > unlockedSlots) return false;
        activeSlots.entrySet().removeIf(entry -> entry.getValue() == type);
        activeSlots.put(slot, type);
        mutationLevels.putIfAbsent(type, 1);
        return true;
    }

    public void unequipMutation(int slot) {
        activeSlots.remove(slot);
    }

    public int getMutationLevel(DnaType type) {
        if (!isMutationActive(type)) return 0;
        return mutationLevels.getOrDefault(type, 1);
    }

    public int getKnownLevel(DnaType type) {
        return mutationLevels.getOrDefault(type, 1);
    }

    public void setMutationLevel(DnaType type, int level) {
        mutationLevels.put(type, Math.min(3, Math.max(1, level)));
    }

    public boolean upgradeMutation(DnaType type) {
        int current = getKnownLevel(type);
        if (current < 3) {
            mutationLevels.put(type, current + 1);
            return true;
        }
        return false;
    }

    public Map<DnaType, Integer> getMutationLevels() {
        return mutationLevels;
    }
}
