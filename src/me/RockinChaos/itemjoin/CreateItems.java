package me.RockinChaos.itemjoin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class CreateItems {
	public static List < String > regions = new ArrayList < String > ();
	public static Map < String, ItemStack > items = new HashMap < String, ItemStack > ();

	public static void run(Player player) {
		CreateItems.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
		CreateItems.regions.clear();
		RenderImageMaps.clearMaps(player);
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				if (items.getString(".slot") != null) {
					String slotlist = items.getString(".slot").replace(" ", "");
					String[] slots = slotlist.split(",");
					ItemHandler.clearItemID(player);
					for (String slot: slots) {
						if (isCompatible(item, slot)) {
							String ItemID = ItemHandler.getItemID(player, slot);
							int dataValue = items.getInt(".data-value");
							Material tempmat = getMaterial(items);
							ItemStack tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
							tempitem = setUnbreaking(items, tempitem);
							tempitem = setDurability(items, tempitem);
							tempitem = hideDurability(items, tempitem);
							tempitem = setEnchantments(items, tempitem);
							tempitem = setMapImage(tempitem, tempmat, item, player);
							ItemMeta tempmeta = getTempMeta(items, tempitem);
							tempmeta = setName(items, tempmeta, tempitem, player, ItemID);
							tempmeta = setLore(items, tempmeta, player);
							tempmeta = setSkull(items, player, tempmat, tempmeta);
							tempmeta = setPotionEffects(items, tempmat, tempmeta);
							tempmeta = setTippedArrows(items, tempmeta, tempmat);
							tempmeta = setFireworks(items, tempmeta, tempmat);
							tempmeta = setDye(items, tempmeta, tempmat);
							tempmeta = setBookAuthor(items, tempmeta, tempmat, player);
							tempmeta = setBookTitle(items, tempmeta, tempmat, player);
							tempmeta = setBookGeneration(items, tempmeta, tempmat, player);
							tempmeta = setBookPages(items, tempmeta, tempmat, player);
							tempmeta = hideAttributes(items, tempmeta);
							tempitem.setItemMeta(tempmeta);
							setRegions(items);
							for (World world: Bukkit.getServer().getWorlds()) {
								if (WorldHandler.inWorld(items, world.getName())) {
									CreateItems.items.put(world.getName() + "." + player.getName().toString() + ".items." + ItemID + item, tempitem);
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void setRun() {
		Collection < ? extends Player > playersOnlineNew;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				playersOnlineNew = (Collection < ? extends Player > )((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineNew) {
					run(player);
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					run(player);
				}
			}
		} catch (NoSuchMethodException ex) {} catch (InvocationTargetException ex) {} catch (IllegalAccessException ex) {}
	}
	
	public static void setRegions(ConfigurationSection items) {
		if (items.getString(".enabled-regions") != null) {
			String regionlist = items.getString(".enabled-regions").replace(" ", "");
			String[] regions = regionlist.split(",");
			for (String region: regions) {
				if (CreateItems.regions != null && !CreateItems.regions.contains(region) || CreateItems.regions == null) {
					CreateItems.regions.add(region);
				}
			}
		}
	}

	public static ItemStack setUnbreaking(ConfigurationSection items, ItemStack tempitem) {
		String ItemFlags = items.getString(".itemflags");
		if (ItemHandler.containsIgnoreCase(ItemFlags, "unbreakable")) {
			tempitem = setUnbreakable.Unbreakable(tempitem);
		}
		return tempitem;
	}

	public static ItemStack setDurability(ConfigurationSection items, ItemStack tempitem) {
		if (items.getString(".skull-owner") != null) {
			tempitem.setDurability((short) SkullType.PLAYER.ordinal());
		} else if (items.getString(".durability") != null) {
			int durability = items.getInt(".durability");
			tempitem.setDurability((short) durability);
		}
		return tempitem;
	}

	public static ItemStack hideDurability(ConfigurationSection items, ItemStack tempitem) {
		String ItemFlags = items.getString(".itemflags");
		if (ItemHandler.containsIgnoreCase(ItemFlags, "hide-durability") || ItemHandler.containsIgnoreCase(ItemFlags, "hidedurability") 
				|| ItemHandler.containsIgnoreCase(ItemFlags, "hide durability") || ItemHandler.containsIgnoreCase(ItemFlags, "durability")) {
			tempitem = setUnbreakable.Unbreakable(tempitem);
		}
		return tempitem;
	}

	public static ItemMeta setName(ConfigurationSection items, ItemMeta tempmeta, ItemStack tempitem, Player player, String ItemID) {
		if (items.getString(".name") != null) {
			String name = items.getString(".name");
			name = Utils.format("&r" + name, player);
			tempmeta.setDisplayName(name + ConfigHandler.encodeSecretData(ConfigHandler.secretMsg + ItemID));
		} else {
			String lookup = ItemHandler.getName(tempitem);
			String name = Utils.format("&r" + lookup + ConfigHandler.encodeSecretData(ConfigHandler.secretMsg + ItemID), player);
			tempmeta.setDisplayName(name);
		}
		return tempmeta;
	}

	public static ItemMeta setLore(ConfigurationSection items, ItemMeta tempmeta, Player player) {
		if (items.getStringList(".lore") != null) {
			List < String > templist = items.getStringList(".lore");
			List < String > templist2 = new ArrayList < String > ();
			for (int k = 0; k < templist.size(); k++) {
				String name = (String) templist.get(k);
				name = Utils.format(name, player);
				templist2.add(name);
			}
			tempmeta.setLore(templist2);
		}
		return tempmeta;
	}

	public static ItemMeta hideAttributes(ConfigurationSection items, ItemMeta tempmeta) {
		String ItemFlags = items.getString(".itemflags");
		if (ServerHandler.hasViableUpdate()) {
			if (ItemHandler.containsIgnoreCase(ItemFlags, "hide-attributes") || ItemHandler.containsIgnoreCase(ItemFlags, "hide attributes") 
					|| ItemHandler.containsIgnoreCase(ItemFlags, "attributes") || ItemHandler.containsIgnoreCase(ItemFlags, "hideattributes")) {
				tempmeta = setItemAttributes(tempmeta);
			}
		}
		return tempmeta;
	}

	public static ItemMeta setItemAttributes(ItemMeta tempmeta) {
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON);
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
		tempmeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		return tempmeta;
	}

	public static ItemMeta setTippedArrows(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (ServerHandler.hasCombatUpdate() && !ItemJoin.pl.getServer().getVersion().contains("(MC: 1.9)") 
				&& items.getString(".arrow.potion-effect") != null && tempmat == Material.getMaterial("TIPPED_ARROW")) {
			String effectType = items.getString(".arrow.potion-effect").toUpperCase();
			int powerLevel = items.getInt(".arrow.power");
			int time = items.getInt(".arrow.time");
			if (items.getString(".arrow.time") == null) {
				time = 20;
			}
			if (items.getString(".arrow.power") == null) {
				powerLevel = 1;
			}
			((PotionMeta) tempmeta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(effectType), time * 160, powerLevel), true);
		}
		return tempmeta;
	}

	public static ItemMeta setFireworks(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (items.getString(".firework.type") != null && tempmat == Material.FIREWORK) {
			String stringType = items.getString(".firework.type").toUpperCase();
			boolean flicker = items.getBoolean(".firework.flicker");
			boolean trail = items.getBoolean(".firework.trail");
			int power = items.getInt(".firework.distance");
			Type buildType = Type.valueOf(stringType);
			List < String > tempcolorslist = items.getStringList(".firework.colors");
			List < Color > clist = new ArrayList < Color > ();
			for (int k = 0; k < tempcolorslist.size(); k++) {
				String color = tempcolorslist.get(k).toUpperCase();
				clist.add(DyeColor.valueOf(color).getFireworkColor());
			}
			FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(clist).withFade(clist).with(buildType).build();
			((FireworkMeta) tempmeta).clearEffects();
			((FireworkMeta) tempmeta).addEffect(effect);
			((FireworkMeta) tempmeta).setPower(power);
		}
		return tempmeta;
	}

	public static ItemMeta setDye(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (tempmat == Material.LEATHER_HELMET || tempmat == Material.LEATHER_CHESTPLATE 
				|| tempmat == Material.LEATHER_LEGGINGS || tempmat == Material.LEATHER_BOOTS) {
			if (items.getString(".leather-color") != null) {
				String leathercolor = items.getString(".leather-color").toUpperCase();
				((LeatherArmorMeta) tempmeta).setColor(DyeColor.valueOf(leathercolor).getFireworkColor());
			}
		}
		return tempmeta;
	}

	public static ItemMeta setBookAuthor(ConfigurationSection items, ItemMeta tempmeta, Material tempmat, Player player) {
		if (items.getString(".author") != null && tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setAuthor(Utils.format(items.getString(".author"), player));
		} else if (tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setAuthor(Utils.format("&f", player));
		}
		return tempmeta;
	}

	public static ItemMeta setBookTitle(ConfigurationSection items, ItemMeta tempmeta, Material tempmat, Player player) {
		if (items.getString(".title") != null && tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setTitle(Utils.format(items.getString(".title"), player));
		} else if (tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setTitle(Utils.format("&f", player));
		}
		return tempmeta;
	}

	public static ItemMeta setBookGeneration(ConfigurationSection items, ItemMeta tempmeta, Material tempmat, Player player) {
		if (items.getString(".generation") != null && ServerHandler.hasFrostburnUpdate() && tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setGeneration(Generation.valueOf(items.getString(".generation")));
		} else if (tempmat == Material.WRITTEN_BOOK && ServerHandler.hasFrostburnUpdate()) {
			((BookMeta) tempmeta).setGeneration(Generation.ORIGINAL);
		}
		return tempmeta;
	}

	public static ItemMeta setBookPages(ConfigurationSection items, ItemMeta tempmeta, Material tempmat, Player player) {
		if (items.getString(".pages") != null && tempmat == Material.WRITTEN_BOOK) {
			List < String > templist = items.getStringList(".pages");
			String templist2 = "";
			templist2 = templist2 + "cleanSlate";
			for (int k = 0; k < templist.size(); k++) {
				String pageSetup = (String) templist.get(k);
				if (ItemHandler.containsIgnoreCase(pageSetup, "newpage: ") || ItemHandler.containsIgnoreCase(pageSetup, "newline: ") 
						|| ItemHandler.containsIgnoreCase(pageSetup, "newpage:") 
						|| ItemHandler.containsIgnoreCase(pageSetup, "newline:") || ItemHandler.containsIgnoreCase(pageSetup, ":endthebook:")) {
					if (ItemHandler.containsIgnoreCase(pageSetup, "newpage: ") && !ItemHandler.containsIgnoreCase(templist2.toString(), "cleanSlate") 
							|| ItemHandler.containsIgnoreCase(pageSetup, "newpage:") && !ItemHandler.containsIgnoreCase(templist2.toString(), "cleanSlate")) {
						((BookMeta) tempmeta).addPage(Utils.format(templist2.toString().replace("[", "").replace("]", ""), player));
						templist2 = "";
					} else if (ItemHandler.containsIgnoreCase(pageSetup, ":endthebook:")) {
						((BookMeta) tempmeta).addPage(Utils.format(templist2.toString().replace("[", "").replace("]", ""), player));
						templist2 = "";
					} else if (ItemHandler.containsIgnoreCase(templist2.toString(), "cleanSlate")) {
						templist2 = "";
					}
					templist2 = templist2 + pageSetup.replace("newline: ", "\n").replace("newpage: ", "").replace("newline:", "\n").replace("newpage:", "");
				}
			}
		}
		return tempmeta;
	}

	public static ItemMeta setSkull(ConfigurationSection items, Player player, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".skull-owner") != null && tempmat == Material.SKULL_ITEM) {
			String owner = items.getString(".skull-owner");
			owner = Utils.format(owner, player);
			((SkullMeta) tempmeta).setOwner(owner);
		}
		return tempmeta;
	}

	public static ItemMeta setPotionEffects(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".potioneffects") != null) {
			if (tempmat == Material.POTION || ServerHandler.hasCombatUpdate() 
					&& tempmat == Material.SPLASH_POTION || ServerHandler.hasCombatUpdate() && tempmat == Material.LINGERING_POTION) {
				String potionlist = items.getString(".potioneffects").replace(" ", "");
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
						} catch (NumberFormatException ex) {
							ServerHandler.sendConsoleMessage("&4An error occurred in the config, &c" + parts[1] + "&4 is not a number and a number was expected!");
							ServerHandler.sendConsoleMessage("&Potion: " + parts[0] + " will now be enchanted by level 1.");
						}
					}
					if (PotionEffectType.getByName(parts[0].toUpperCase()) != null) {
						((PotionMeta) tempmeta).addCustomEffect(new PotionEffect(type, duritation, amplifier), true);
					} else if (PotionEffectType.getByName(parts[0].toUpperCase()) == null) {
						ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + type + "&4 is an incorrect potion effect!");
						ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct enchantment names!");
					}
				}
			}
		}
		return tempmeta;
	}

	public static ItemStack setEnchantments(ConfigurationSection items, ItemStack tempitem) {
		if (items.getString(".enchantment") != null) {
			String enchantlist = items.getString(".enchantment").replace(" ", "");
			String[] enchantments = enchantlist.split(",");
			for (String enchantment: enchantments) {
				String[] parts = enchantment.split(":");
				String name = parts[0].toUpperCase();
				int level = 1;
				if (ItemHandler.containsIgnoreCase(enchantment, ":")) {
					try {
						level = Integer.parseInt(parts[1]);
					} catch (NumberFormatException ex) {
						ServerHandler.sendConsoleMessage("&4An error occurred in the config, &c" + parts[1] + "&4 is not a number and a number was expected!");
						ServerHandler.sendConsoleMessage("&aEnchantment: " + parts[0] + " will now be enchanted by level 1.");
					}
				}
				if (Enchantment.getByName(name) != null) {
					tempitem.addUnsafeEnchantment(Enchantment.getByName(name), level);
				} else if (Enchantment.getByName(name) == null) {
					ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + name + "&4 is an incorrect enchantment name!");
					ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
				}
			}
		}
		return tempitem;
	}

	public static ItemStack setMapImage(ItemStack tempitem, Material tempmat, String item, Player player) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		if (items.getString(".custom-map-image") != null && tempmat == Material.MAP) {
			int mapID;
			if (items.getString(".map-id") != null && items.getInt(".map-id") > 0 && items.getInt(".map-id") < 30) {
				mapID = items.getInt(".map-id");
			} else {
				ServerHandler.sendConsoleMessage("&4Your map-id on item " + item + " is incorrect or does not exist!");
				ServerHandler.sendConsoleMessage("&4Please check and makesure your map-id is between 1 and 29.");
				ServerHandler.sendConsoleMessage("&4Your map-id has been set to 1 by default until you fix your config.");
				ServerHandler.sendConsoleMessage("&4If you believe for this to be an error please contact the plugin developer.");
				mapID = 1;
			}
			tempitem.setDurability((short) mapID);
			MapView view = RenderImageMaps.MapView(player, mapID);
			String mapIMG = items.getString(".custom-map-image");
			if (mapIMG.equalsIgnoreCase("default.png") || new File(ItemJoin.pl.getDataFolder(), mapIMG).exists()) {
				RenderImageMaps.setImage(mapIMG, mapID);
				try {
					view.removeRenderer(view.getRenderers().get(0));
				} catch (NullPointerException ex) {}
			}
			try {
			view.addRenderer(new RenderImageMaps());
			} catch (NullPointerException ex) {}
		}
		return tempitem;
	}

	public static Boolean isCompatible(String item, String slot) {
		Boolean isCompatible = false;
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		String id = items.getString(".id");
		String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		String vers = pkgname.substring(pkgname.lastIndexOf('.') + 1);
		if (!ServerHandler.hasCombatUpdate()) {
			if (slot.equalsIgnoreCase("Offhand")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have Offhand support!");
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("TIPPED_ARROW") || id.equalsIgnoreCase("440") || id.equalsIgnoreCase("0440")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item TIPPED_ARROW!");
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("SPLASH_POTION") || id.equalsIgnoreCase("373") || id.equalsIgnoreCase("0373")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item SPLASH_POTION!");
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else {
				isCompatible = true;
			}
		} else if (isCreatable(item, slot)) {
			isCompatible = true;
		}
		return isCompatible;
	}

	@SuppressWarnings("deprecation")
	public static Material getMaterial(ConfigurationSection items) {
		Material material = null;
		if (Utils.isInt(items.getString(".id"))) {
			material = Material.getMaterial(items.getInt(".id"));
		} else {
			material = Material.getMaterial(items.getString(".id").toUpperCase());
		}
		return material;
	}

	public static Boolean isMaterial(String Mats) {
		Boolean isMaterial = false;
		Material getMaterial = Material.getMaterial(Mats);
		if (getMaterial != null) {
			isMaterial = true;
		}
		return isMaterial;
	}

	public static Boolean isComparable(ConfigurationSection items, String Mats) {
		Boolean isMaterial = false;
		Material getMaterial = Material.getMaterial(Mats);
		Material tempmat = getMaterial(items);
		if (isMaterial(Mats) && tempmat == getMaterial) {
			isMaterial = true;
		}
		return isMaterial;
	}

	public static ItemMeta getTempMeta(ConfigurationSection items, ItemStack tempitem) {
		ItemMeta TempMeta = null;
		if (isComparable(items, "WRITTEN_BOOK")) {
			TempMeta = (BookMeta) tempitem.getItemMeta();
		} else if (isComparable(items, "FIREWORK")) {
			TempMeta = (FireworkMeta) tempitem.getItemMeta();
		} else if (isComparable(items, "SKULL_ITEM")) {
			TempMeta = (SkullMeta) tempitem.getItemMeta();
		} else if (isComparable(items, "LEATHER_HELMET") || isComparable(items, "LEATHER_CHESTPLATE") 
				|| isComparable(items, "LEATHER_LEGGINGS") || isComparable(items, "LEATHER_BOOTS")) {
			TempMeta = (LeatherArmorMeta) tempitem.getItemMeta();
		} else if (ServerHandler.hasCombatUpdate() && !ItemJoin.pl.getServer().getVersion().contains("(MC: 1.9)") 
				&& isComparable(items, "TIPPED_ARROW")) {
			TempMeta = (PotionMeta) tempitem.getItemMeta();
		} else if (isComparable(items, "POTION") || ServerHandler.hasCombatUpdate() && isComparable(items, "SPLASH_POTION") 
				|| ServerHandler.hasCombatUpdate() && isComparable(items, "LINGERING_POTION")) {
			TempMeta = (PotionMeta) tempitem.getItemMeta();
		} else {
			TempMeta = tempitem.getItemMeta();
		}
		return TempMeta;
	}

	public static boolean isCreatable(String item, String slot) {
		boolean isCreatable = true;
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		Material tempmat = getMaterial(items);
		if (tempmat == null) {
			ServerHandler.sendConsoleMessage("&e" + item + "'s Material 'ID' is invalid or does not exist!");
			ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
			isCreatable = false;
		}
		if (slot != null) {
			if (!Utils.isInt(slot) && !Utils.isCustomSlot(slot)) {
				ServerHandler.sendConsoleMessage("&e" + item + "'s slot is invalid or does not exist!");
				ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
				isCreatable = false;
			} else if (Utils.isInt(slot)) {
				int iSlot = Integer.parseInt(slot);
				if (!(iSlot >= 0 && iSlot <= 35)) {
					ServerHandler.sendConsoleMessage("&e" + item + "'s slot must be between 1 and 36!");
					ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
					isCreatable = false;
				}
			}
		}
		return isCreatable;
	}
}