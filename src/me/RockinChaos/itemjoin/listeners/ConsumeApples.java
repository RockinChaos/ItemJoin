package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class ConsumeApples implements Listener {

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		ItemStack inPlayerInventory = event.getItem();
		Player player = event.getPlayer();
		String world = player.getWorld().getName();
		if (inPlayerInventory.getType() == Material.GOLDEN_APPLE) {
			if (Utils.isConfigurable()) {
				for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
					ConfigurationSection items = ConfigHandler.getItemSection(item);
					int Arbitrary = 0;
					String ItemID;
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						for (String slot: slots) {
							if (slot.equalsIgnoreCase("Arbitrary")) {
								Arbitrary = Arbitrary + 1;
								ItemID = slot + Arbitrary;
							} else {
								ItemID = slot;
							}
							ItemStack inStoredItems = CreateItems.items.get(world + "." + player.getName().toString() + ".items." + ItemID + item);
							if (items.getString(".potion-effect") != null && WorldHandler.inWorld(items, world) && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
								event.setCancelled(true);
								player.getInventory().remove(inPlayerInventory);
								String potionlist = items.getString(".potion-effect").replace(" ", "");
								String[] potions = potionlist.split(",");
								for (String potion: potions) {
									String[] parts = potion.split(":");
									PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
									int duritation = 1;
									int amplifier = 1;
									if (ItemHandler.containsIgnoreCase(potion, ":")) {
										try {
											if (Integer.parseInt(parts[1]) == 1 || Integer.parseInt(parts[1]) == 2 || Integer.parseInt(parts[1]) == 3) {
												amplifier = Integer.parseInt(parts[1]) - 1;
											} else {
												amplifier = Integer.parseInt(parts[1]);
											}
											duritation = Integer.parseInt(parts[2]) * 20;
										} catch (NumberFormatException e) {
											ServerHandler.sendConsoleMessage("&4An error occurred in the config, &c" + parts[1] + "&4 is not a number and a number was expected!");
											ServerHandler.sendConsoleMessage("&Potion: " + parts[0] + " will now be enchanted by level 1.");
											if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
										}
									}
									if (PotionEffectType.getByName(parts[0].toUpperCase()) != null) {
										player.addPotionEffect(new PotionEffect(type, duritation, amplifier), true);
									} else if (PotionEffectType.getByName(parts[0].toUpperCase()) == null) {
										ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + type + "&4 is an incorrect potion effect!");
										ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct enchantment names!");
									}
								}
							}
						}
					}
				}
			}
		}
	}
	}