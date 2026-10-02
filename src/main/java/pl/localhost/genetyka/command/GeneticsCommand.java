package pl.localhost.genetyka.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.localhost.genetyka.Main;
import pl.localhost.genetyka.model.DnaType;
import pl.localhost.genetyka.model.PlayerGenetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneticsCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public GeneticsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui") || args[0].equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            plugin.getGeneticsGUI().openGUI((Player) sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("info") || sub.equals("profil")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            Player player = (Player) sender;
            PlayerGenetics pg = plugin.getGeneticsManager().getGenetics(player);

            player.sendMessage(plugin.getConfigManager().color("&8---------- &b&lProfil Genetyczny &8----------"));
            player.sendMessage(plugin.getConfigManager().color(" &8» &7Punkty Ewolucji: &b" + pg.getEvolutionPoints() + " pkt"));
            player.sendMessage(plugin.getConfigManager().color(" &8» &7Odblokowane sloty: &e" + pg.getUnlockedSlots() + " / 3"));
            player.sendMessage(plugin.getConfigManager().color(" &8» &7Aktywne mutacje:"));
            for (int i = 1; i <= pg.getUnlockedSlots(); i++) {
                DnaType type = pg.getActiveSlot(i);
                if (type != null) {
                    int lvl = pg.getMutationLevel(type);
                    player.sendMessage(plugin.getConfigManager().color("   &8* &fSlot #" + i + ": " + plugin.getConfigManager().getMutationName(type) + " &7(Poz. &b" + lvl + "&7)"));
                } else {
                    player.sendMessage(plugin.getConfigManager().color("   &8* &fSlot #" + i + ": &7[Pusty]"));
                }
            }
            player.sendMessage(plugin.getConfigManager().color("&8-----------------------------------------"));
            return true;
        }

        if (sub.equals("lista") || sub.equals("list")) {
            sender.sendMessage(plugin.getConfigManager().color("&8---------- &b&lDostępne Mutacje DNA &8----------"));
            for (DnaType type : DnaType.values()) {
                String name = plugin.getConfigManager().getMutationName(type);
                sender.sendMessage(plugin.getConfigManager().color(" &8» &e" + type.getKey() + " &7- " + name));
            }
            sender.sendMessage(plugin.getConfigManager().color("&8---------------------------------------------"));
            return true;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission("genetyka.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            plugin.getConfigManager().reload();
            plugin.getItemManager().registerRecipes();
            sender.sendMessage(plugin.getConfigManager().color("&aKonfiguracja pluginu localhost-genetyka zostala przeladowana!"));
            return true;
        }

        if (sub.equals("daj") || sub.equals("give")) {
            if (!sender.hasPermission("genetyka.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(plugin.getConfigManager().color("&7Użycie: &e/dna daj <gracz> <igla|probka> [typ] [ilość]"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(plugin.getConfigManager().color("&cNie znaleziono gracza!"));
                return true;
            }

            String itemType = args[2].toLowerCase();
            int amount = 1;

            if (itemType.equals("igla") || itemType.equals("ekstraktor") || itemType.equals("strzykawka")) {
                if (args.length >= 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ignored) {}
                }
                ItemStack needle = plugin.getItemManager().createExtractor(amount);
                target.getInventory().addItem(needle);
                sender.sendMessage(plugin.getConfigManager().color("&aNadano Strzykawke DNA graczowi &b" + target.getName()));
                return true;
            }

            if (itemType.equals("probka") || itemType.equals("dna")) {
                if (args.length < 4) {
                    sender.sendMessage(plugin.getConfigManager().color("&7Podaj typ DNA! Dostępne: &ewardent, enderman, creeper, pajak, golem, blaze, wither, phantom"));
                    return true;
                }
                DnaType type = DnaType.fromKey(args[3]);
                if (type == null) {
                    sender.sendMessage(plugin.getConfigManager().color("&cNieprawidlowy typ DNA!"));
                    return true;
                }

                if (args.length >= 5) {
                    try {
                        amount = Integer.parseInt(args[4]);
                    } catch (NumberFormatException ignored) {}
                }

                ItemStack sample = plugin.getItemManager().createDnaSample(type, amount);
                target.getInventory().addItem(sample);
                sender.sendMessage(plugin.getConfigManager().color("&aNadano próbkę " + plugin.getConfigManager().getMutationName(type) + " &7graczowi &b" + target.getName()));
                return true;
            }
        }

        if (sub.equals("punkty") || sub.equals("points")) {
            if (!sender.hasPermission("genetyka.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            if (args.length < 4) {
                sender.sendMessage(plugin.getConfigManager().color("&7Użycie: &e/dna punkty <dodaj|ustaw> <gracz> <ilość>"));
                return true;
            }

            String action = args[1].toLowerCase();
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(plugin.getConfigManager().color("&cNie znaleziono gracza!"));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfigManager().color("&cPodana ilość musi być liczbą!"));
                return true;
            }

            PlayerGenetics pg = plugin.getGeneticsManager().getGenetics(target);
            if (action.equals("dodaj") || action.equals("add")) {
                pg.addEvolutionPoints(amount);
                sender.sendMessage(plugin.getConfigManager().color("&aDodano &e" + amount + " &apkt graczowi &b" + target.getName() + " &7(Razem: &b" + pg.getEvolutionPoints() + "&7)"));
                target.sendMessage(plugin.getConfigManager().color("&aOtrzymałeś &e" + amount + " Punktów Ewolucji&a! Twój stan: &b" + pg.getEvolutionPoints() + " pkt"));
            } else if (action.equals("ustaw") || action.equals("set")) {
                pg.setEvolutionPoints(amount);
                sender.sendMessage(plugin.getConfigManager().color("&aUstawiono &e" + amount + " &apkt graczowi &b" + target.getName()));
            }
            return true;
        }

        sender.sendMessage(plugin.getConfigManager().color("&7Nieznana komenda! Wpisz &e/dna &7lub &e/dna info&7."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("gui");
            list.add("info");
            list.add("lista");
            if (sender.hasPermission("genetyka.admin")) {
                list.add("daj");
                list.add("punkty");
                list.add("reload");
            }
            return filter(list, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("daj")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("daj")) {
            return filter(Arrays.asList("igla", "probka"), args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("daj") && args[2].equalsIgnoreCase("probka")) {
            return filter(Arrays.stream(DnaType.values()).map(DnaType::getKey).collect(Collectors.toList()), args[3]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("punkty")) {
            return filter(Arrays.asList("dodaj", "ustaw"), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("punkty")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
        }

        return list;
    }

    private List<String> filter(List<String> list, String query) {
        String lower = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}
