package me.RockinChaos.itemjoin.cacheitems;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.listeners.InvClickCreative;
import me.RockinChaos.itemjoin.listeners.giveitems.RegionEnter;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class CreateItems {
	public static Map < String, ItemStack > items = new HashMap < String, ItemStack > ();
	public static Map < String, Integer > probability = new HashMap < String, Integer > ();
	private static boolean hasPreviewed = false;

	public static void run(Player player) {
		items.remove(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items.");
		probability.clear();
		RegionEnter.resetRegions();
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
							short dataValue = ItemHandler.getDataValue(items);
							int count = ItemHandler.getCount(items);
							Material tempmat = ItemHandler.getMaterial(items);
							ItemStack tempitem = new ItemStack(tempmat, count, dataValue);
							
								tempitem = setHeadDatabaseSkull(items, tempmat, tempitem);
								tempitem = setUnbreaking(items, tempitem);
								tempitem = setDurability(items, tempitem);
								tempitem = hideDurability(items, tempitem);
								tempitem = setEnchantments(items, tempitem, player);
								tempitem = setMapImage(tempitem, tempmat, item, player);
								tempitem = setNBTData(items, tempitem, ItemID, item);
								tempitem = setJSONBookPages(items, tempitem, tempmat, player);
								
								ItemMeta tempmeta = getTempMeta(tempitem);							
								tempmeta = setName(items, tempmeta, tempitem, player, ItemID, null);
								tempmeta = setLore(items, tempmeta, player, null);
								tempmeta = setProbability(items, tempmeta, item);
								tempmeta = setSkull(items, player, tempmat, tempmeta);
								tempmeta = setSkullTexture(items, player, tempmat, tempmeta);
								tempmeta = setPotionEffects(items, tempmat, tempmeta);
								tempmeta = setTippedArrows(items, tempmat, tempmeta);
								tempmeta = setBanners(items, tempmat, tempmeta);
								tempmeta = setFireworks(items, tempmeta, tempmat);
								tempmeta = setFireChargeColor(items, tempmeta, tempmat);
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
									CreateItems.items.put(world.getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item, tempitem);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void setRun() {
		Collection < ? extends Player > playersOnlineNew;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				playersOnlineNew = (Collection < ? extends Player > )((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineNew) {
					run(player);
					InvClickCreative.isCreative(player, player.getGameMode());
					AnimationHandler.OpenAnimations(player);
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					run(player);
					InvClickCreative.isCreative(player, player.getGameMode());
					AnimationHandler.OpenAnimations(player);
				}
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		} 
	}
	
	public static void setRegions(ConfigurationSection items) {
		if (items.getString(".enabled-regions") != null) {
			String regionlist = items.getString(".enabled-regions").replace(" ", "");
			String[] regions = regionlist.split(",");
			for (String region: regions) {
				if (RegionEnter.getRegions() != null && !RegionEnter.getRegions().contains(region) || RegionEnter.getRegions() == null) {
					RegionEnter.saveRegion(region);
				}
			}
		}
	}
	
	public static ItemStack setUnbreaking(ConfigurationSection items, ItemStack tempitem) {
		String ItemFlags = items.getString(".itemflags");
		if (ItemHandler.containsIgnoreCase(ItemFlags, "unbreakable")) {
		  try {
			tempitem = Unbreakable.setUnbreakable(tempitem);
		  } catch (Exception e) {
			  if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		  }
		}
		return tempitem;
	}
	
	public static ItemStack setNBTData(ConfigurationSection items, ItemStack tempitem, String ItemID, String item) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && !ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla")) {
		try {
		Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
		Class<?> nmsItemStackClass = Reflection.getNMS("ItemStack");
		Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
		Object nms = getNMSI.invoke(null, tempitem);
		Object tag = Reflection.getNMS("NBTTagCompound").getConstructor().newInstance();
		Object cacheTag = nmsItemStackClass.getMethod("getTag").invoke(nms);
		if (cacheTag != null) {
			cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Name", item);
			cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Slot", ItemID);
			tempitem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
		} else {
		tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, "ItemJoin Name", item);
		tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, "ItemJoin Slot", ItemID);
		nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
		tempitem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
		}
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Error 133 has occured when setting NBTData to an item.");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		}
		return tempitem;
	}

	public static ItemStack setDurability(ConfigurationSection items, ItemStack tempitem) {
		if (items.getString(".skull-owner") != null) {
			tempitem.setDurability((short) 3);
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
		  try {
		 	tempitem = Unbreakable.setUnbreakable(tempitem);
		  } catch (Exception e) {
			  if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		  }
		}
		return tempitem;
	}
	
	public static ItemMeta setProbability(ConfigurationSection items, ItemMeta tempmeta, String item) {
		if (items.getString(".probability") != null) {
			String percentageString = items.getString(".probability").replace("%", "").replace("-", "").replace(" ", "");
			int percentage = Integer.parseInt(percentageString);
			probability.put(item, percentage);
		}
		return tempmeta;
	}

	public static ItemMeta setName(ConfigurationSection items, ItemMeta tempmeta, ItemStack tempitem, Player player, String ItemID, String nameString) {
		if (items.getString(".name") != null) {
			String formatName = items.getString(".name");
			if (nameString != null && items.getString(".name." + nameString) != null) { formatName = items.getString(".name." + nameString); }
			else if (ConfigHandler.getNameSection(items) != null) { formatName = items.getString(".name." + ConfigHandler.getNameSection(items).getKeys(false).iterator().next()); }
			formatName = Utils.format("&r" + formatName.replace("<delay:" + Utils.returnInteger(formatName) + ">", ""), player);
			if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasSpecificUpdate("1_8") 
					|| ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla") && ServerHandler.hasSpecificUpdate("1_8")) {
				tempmeta.setDisplayName(formatName);
			} else if (!ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla")) {
				tempmeta.setDisplayName(formatName + ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID + items.getName()));
			}
		} else {
			String lookup = ItemHandler.getName(tempitem);
			String formatName = "";
			if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasSpecificUpdate("1_8")
					|| ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla") && ServerHandler.hasSpecificUpdate("1_8")) {
			} else if (!ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla")) {
				formatName = Utils.format("&r" + lookup + ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID + items.getName()), player);
				tempmeta.setDisplayName(formatName);
			}
		}
		return tempmeta;
	}

	public static ItemMeta setLore(ConfigurationSection items, ItemMeta tempmeta, Player player, String loreString) {
		if (items.getStringList(".lore") != null) {
			List < String > loreList = items.getStringList(".lore");
			List < String > loreFormatList = new ArrayList < String > ();
			if (loreString != null && items.getStringList(".lore." + loreString) != null) { loreList = items.getStringList(".lore." + loreString); }
			else if (ConfigHandler.getLoreSection(items) != null) { loreList = items.getStringList(".lore." + ConfigHandler.getLoreSection(items).getKeys(false).iterator().next()); }
			for (int k = 0; k < loreList.size(); k++) {
				String formatLore = loreList.get(k).replace("<delay:" + Utils.returnInteger(loreList.get(k)) + ">", "");
				formatLore = Utils.format(formatLore, player);
				loreFormatList.add(formatLore);
			}
			tempmeta.setLore(loreFormatList);
		}
		return tempmeta;
	}

	public static ItemMeta hideAttributes(ConfigurationSection items, ItemMeta tempmeta) {
		String ItemFlags = items.getString(".itemflags");
		if (ServerHandler.hasSpecificUpdate("1_8")) {
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

	public static ItemMeta setTippedArrows(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".potion-effect") != null) {
			if (ServerHandler.hasCombatUpdate() && !ItemJoin.getInstance().getServer().getVersion().contains("(MC: 1.9)") && tempmat == Material.getMaterial("TIPPED_ARROW")) {
				String effectlist = items.getString(".potion-effect").replace(" ", "");
				String[] effects = effectlist.split(",");
				for (String effect: effects) {
					String[] parts = effect.split(":");
					String type = parts[0].toUpperCase();
					int duritation = 1;
					int level = 1;
					if (ItemHandler.containsIgnoreCase(effect, ":")) {
						try {
							if (Integer.parseInt(parts[1]) == 1 || Integer.parseInt(parts[1]) == 2 || Integer.parseInt(parts[1]) == 3) {
								level = Integer.parseInt(parts[1]) - 1;
							} else {
								level = Integer.parseInt(parts[1]);
							}
							duritation = Integer.parseInt(parts[2]);
						} catch (NumberFormatException e) {
							ServerHandler.sendConsoleMessage("&4An error occurred in the config, &c" + parts[1] + "&4 is not a number and a number was expected!");
							ServerHandler.sendConsoleMessage("&4Effect: " + parts[0] + " will now be set to level 1.");
							if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
						}
					}
					if (PotionEffectType.getByName(parts[0].toUpperCase()) != null) {
						((PotionMeta) tempmeta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(type), duritation * 160, level), true);
					} else if (PotionEffectType.getByName(parts[0].toUpperCase()) == null) {
						ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + type + "&4 is an incorrect potion effect!");
						ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct enchantment names!");
					}
				}
			}
		}
		return tempmeta;
	}

	public static ItemMeta setFireworks(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (items.getString(".firework.type") != null) {
			if (tempmat.toString().equalsIgnoreCase("FIREWORK") || tempmat.toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
			String stringType = items.getString(".firework.type").toUpperCase();
			boolean flicker = items.getBoolean(".firework.flicker");
			boolean trail = items.getBoolean(".firework.trail");
			int power = items.getInt(".firework.distance");
			Type buildType = Type.valueOf(stringType);
			List < Color > clist = new ArrayList < Color > ();
			if (items.getString(".firework.colors") != null) {
				String colorlist = items.getString(".firework.colors").replace(" ", "");
				String[] colors = colorlist.split(",");
				for (String color: colors) {
					String coloring = color.toUpperCase();
					try {
					clist.add(DyeColor.valueOf(coloring).getFireworkColor());
					} catch (Exception e) {
						ServerHandler.sendConsoleMessage("&4The item " + items.getName() + " has the incorrect dye color " + coloring + " and does not exist!");
						ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye color names!");
					}
				}
			}
			FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(clist).withFade(clist).with(buildType).build();
			((FireworkMeta) tempmeta).clearEffects();
			((FireworkMeta) tempmeta).addEffect(effect);
			((FireworkMeta) tempmeta).setPower(power);
			}
		}
		return tempmeta;
	}
	
	public static ItemMeta setFireChargeColor(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (items.getString(".charge-color") != null) {
			if (ItemHandler.containsIgnoreCase(tempmat.toString(), "CHARGE") || ItemHandler.containsIgnoreCase(tempmat.toString(), "STAR")) {
				String color = items.getString(".charge-color").toUpperCase();
                ((FireworkEffectMeta) tempmeta).setEffect(FireworkEffect.builder().withColor(DyeColor.valueOf(color).getColor()).build());
			}
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
		if (items.getString(".generation") != null && ServerHandler.hasSpecificUpdate("1_10") && tempmat == Material.WRITTEN_BOOK) {
			((BookMeta) tempmeta).setGeneration(Generation.valueOf(items.getString(".generation")));
		} else if (tempmat == Material.WRITTEN_BOOK && ServerHandler.hasSpecificUpdate("1_10")) {
			((BookMeta) tempmeta).setGeneration(Generation.ORIGINAL);
		}
		return tempmeta;
	}
	
	
	public static ItemStack setJSONBookPages(ConfigurationSection items, ItemStack tempitem, Material tempmat, Player player) {
		if (tempmat == Material.WRITTEN_BOOK && items.getString(".pages") != null && ConfigHandler.getPagesSection(items) != null && ServerHandler.hasSpecificUpdate("1_8")) {

			Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
			Class<?> NBTBASE = Reflection.getNMS("NBTBase");
			Method getNMS = null;
			try { getNMS = craftItemStack.getMethod("asNMSCopy", ItemStack.class); } catch (Exception e) { }
			Object nms = null;
			try { nms = getNMS.invoke(null, tempitem); } catch (Exception e) { e.printStackTrace(); }
			Object pages = null;
			try { pages = Reflection.getNMS("NBTTagList").getConstructor().newInstance(); } catch (Exception e) { e.printStackTrace(); }

			Object tag = null;
			try { tag = Reflection.getNMS("NBTTagCompound").getConstructor().newInstance(); } catch (Exception e) { e.printStackTrace(); }

			for (String pageString: ConfigHandler.getPagesSection(items).getKeys(false)) {
				 List<String> pageList = items.getStringList(".pages." + pageString);
				 List<String> textBuilder = new ArrayList<String>();
				 textBuilder.add("\"\"");
				 for (int k = 0; k < pageList.size(); k++) {
						String formatPage = pageList.get(k);
						if (formatPage.contains("<hover type=\"text\"") || formatPage.contains("<hover type=\"open_url\"") || formatPage.contains("<hover type=\"run_command\"")) {
							String type = "";
							if (formatPage.contains("<hover type=\"text\"")) {
								type = "text";
							} else if (formatPage.contains("<hover type=\"open_url\"")) {
								type = "open_url";
							} else if (formatPage.contains("<hover type=\"run_command\"")) {
								type = "run_command";
							}
							String result = null;
							String result2 = null;
							java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(">\"(.*?)\"</hover>");
							Matcher matcher1 = pattern1.matcher(formatPage);
						    while (matcher1.find()) { result = matcher1.group(1); }
							
							java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("value=\"(.*?)\">");
							Matcher matcher2 = pattern2.matcher(formatPage);
							while (matcher2.find()) { result2 = matcher2.group(1); }
							
							formatPage = formatPage.replace("<hover type=\"" + type + "\" value=\"" + result2 + "\">\"" + result + "\"</hover>", "");
							result2 = Utils.format(result2, player);
							
							String event = "";

							String action = "";
							if (type.equalsIgnoreCase("text")) { event = "hoverEvent"; action = "show_text";}
							else if (type.equalsIgnoreCase("open_url")) { event = "clickEvent"; action = "open_url";}
							else if (type.equalsIgnoreCase("run_command")) { event = "clickEvent"; action = "run_command";}
							String hoverBuilder = new String();
							String hoverString = result2.replace(" <n> ","<n>").replace("<n> ","<n>").replace(" <n>","<n>");
							String[] hovers = hoverString.split("<n>");
							for (String hover: hovers) { if (hoverString.contains("<n>")) {hoverBuilder = hoverBuilder + hover + "\n"; } else { hoverBuilder = hover; } }
							
							formatPage = Utils.format(formatPage, player);
							result = Utils.format(result, player);
							if (type.equalsIgnoreCase("open_url")) {
								if (!ItemHandler.containsIgnoreCase(hoverBuilder, "https") || !ItemHandler.containsIgnoreCase(hoverBuilder, "http")) {
									ServerHandler.sendConsoleMessage("&c[ERROR] The URL Specified for the clickable link in the book " + items.getName() + " is missing http or https and will not be clickable.");
									ServerHandler.sendConsoleMessage("&c[ERROR] A URL designed for a clickable link should look as follows; https://www.google.com/");
								}
							}
							String textComp = "{\"text\":\"" + result + "\",\"" + event + "\":{\"action\":\"" + action + "\",\"value\":\"" + hoverBuilder + "\"}}";
							String newLine = "{\"text\":\"\\n\",\"color\":\"reset\"}";							
							String textCompExtra = "{\"text\":\"" + formatPage + "\"}";
							textBuilder.add(textComp);
							textBuilder.add(textCompExtra);
							textBuilder.add(newLine);

						} else {
							formatPage = Utils.format(formatPage, player);
							String textComp = "{\"text\":\"" + formatPage + "\"}";
							String newLine = "{\"text\":\"\\n\",\"color\":\"reset\"}";
						
							textBuilder.add(textComp);
							textBuilder.add(newLine);
						}
				 }
				 
				 Object tagconvert = null;
				 try { tagconvert = Reflection.getNMS("NBTTagString").getConstructor(String.class).newInstance(textBuilder.toString().replace("}, {", "},{")); } catch (Exception e) { e.printStackTrace(); }
				 try { pages.getClass().getMethod("add", NBTBASE).invoke(pages, tagconvert); } catch (Exception e) { e.printStackTrace(); }
				 
			}
			
			try { tag.getClass().getMethod("set", String.class, NBTBASE).invoke(tag, "pages", pages); } catch (Exception e) { e.printStackTrace(); }
			
	        try { nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag); } catch (Exception e) { e.printStackTrace(); }
	        
	        try { tempitem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms); } catch (Exception e) { e.printStackTrace(); }
	        
		}
		return tempitem;
	}

	public static ItemMeta setBookPages(ConfigurationSection items, ItemMeta tempmeta, Material tempmat, Player player) {
		if (tempmat == Material.WRITTEN_BOOK && items.getString(".pages") != null && ConfigHandler.getPagesSection(items) != null && !ServerHandler.hasSpecificUpdate("1_8")) {
		if (items.getString(".pages") != null && tempmat == Material.WRITTEN_BOOK) {
			for (String pageString: ConfigHandler.getPagesSection(items).getKeys(false)) {
				 List<String> pageList = items.getStringList(".pages." + pageString);
				 String saveList = "";
				 for (int k = 0; k < pageList.size(); k++) {
						String formatPage = pageList.get(k);
						if (formatPage.contains("<hover type=\"text\"") || formatPage.contains("<hover type=\"open_url\"") || formatPage.contains("<hover type=\"run_command\"")) {
							String result = null;
							String result2 = null;
							java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(">\"(.*?)\"</hover>");
							Matcher matcher1 = pattern1.matcher(formatPage);
						    while (matcher1.find()) { result = matcher1.group(1); }
							
							java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("value=\"(.*?)\">");
							Matcher matcher2 = pattern2.matcher(formatPage);
							while (matcher2.find()) { result2 = matcher2.group(1); }
							
							formatPage = formatPage
									.replace("<hover type=\"text\" value=\"" + result2 + "\">\"" + result + "\"</hover>", result)
									.replace("<hover type=\"open_url\" value=\"" + result2 + "\">\"" + result + "\"</hover>", result)
									.replace("<hover type=\"run_command\" value=\"" + result2 + "\">\"" + result + "\"</hover>", result);
						}
						saveList = saveList + Utils.format(formatPage, player) + "\n";
				 }
					((BookMeta) tempmeta).addPage(saveList);
			}
		}
		}
		return tempmeta;
	}

	public static ItemMeta setBanners(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".banner-meta") != null && ServerHandler.hasSpecificUpdate("1_8") && ItemHandler.containsIgnoreCase(tempmat.toString(), "BANNER")) {
			String bannerlist = items.getString(".banner-meta").replace(" ", "");
			String[] banners = bannerlist.split(",");
			List<Pattern> patterns = new ArrayList<Pattern>();
			for (String banner: banners) {
				String[] parts = banner.split(":");
				DyeColor Color = DyeColor.valueOf(parts[0].toUpperCase());
				PatternType Pattern = PatternType.valueOf(parts[1].toUpperCase());
				if (Color != null && Pattern != null) {
					patterns.add(new Pattern(Color, Pattern));
				} else if (Color == null) {
					ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + parts[0] + "&4 is an incorrect dye color!");
					ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye colors!");
				} else if (Pattern == null) {
					ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + parts[1] + "&4 is an incorrect pattern type!");
					ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/block/banner/PatternType.html for a list of correct pattern types!");
				}
			}
			((BannerMeta) tempmeta).setPatterns(patterns);
		}
		return tempmeta;
	}
	
    public static ItemMeta setSkullTexture(ConfigurationSection items, Player player, Material tempmat, ItemMeta tempmeta) {
		if (ServerHandler.hasSpecificUpdate("1_8") && items.getString(".skull-texture") != null && !items.getString(".skull-texture").contains("hdb-") && items.getString(".skull-owner") == null) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
		String texture = items.getString(".skull-texture");
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", new String(texture)));
        try {
            Field declaredField = tempmeta.getClass().getDeclaredField("profile");
            declaredField.setAccessible(true);
            declaredField.set(tempmeta, gameProfile);
        }
        catch (Exception e) {
        	if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
        }
			}
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
			}
		}
        return tempmeta;
    }

	public static ItemMeta setSkull(ConfigurationSection items, Player player, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".skull-owner") != null && items.getString(".skull-texture") == null) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			String owner = items.getString(".skull-owner");
			owner = Utils.format(owner, player);
			return PlayerHandler.setSkullOwner(tempmeta, owner);
			}
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
			}
		}
		return tempmeta;
	}

	public static ItemStack setHeadDatabaseSkull(ConfigurationSection items, Material tempmat, ItemStack tempitem) {
		if (ServerHandler.hasSpecificUpdate("1_8") && Hooks.hasHeadDatabase() && items.getString(".skull-texture") != null && items.getString(".skull-texture").contains("hdb-") 
				&& items.getString(".skull-owner") == null && Utils.isInt(items.getString(".skull-texture").replace("hdb-", ""))) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
	      HeadDatabaseAPI api = new HeadDatabaseAPI();
	      try {
	    	  
	          ItemStack sk = api.getItemHead(items.getString(".skull-texture").replace("hdb-", ""));
	          return sk != null ? sk : tempitem.clone();
	      }
	      catch (NullPointerException e) {
	          ServerHandler.sendConsoleMessage("&4HeadDatabase could not find &c#" + items.getString(".skull-texture").replace("hdb-", "") + "&4, this head does not exist.");
	          if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
	      }
			}
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null) {
			if (tempmat.toString().equalsIgnoreCase("SKULL_ITEM") || tempmat.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
			}
		}
		return tempitem;
	}
	
	public static ItemMeta setPotionEffects(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".potion-effect") != null) {
			if (tempmat == Material.POTION || tempmat.toString().equalsIgnoreCase("SPLASH_POTION") || ServerHandler.hasCombatUpdate() && tempmat == Material.LINGERING_POTION) {
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
							ServerHandler.sendConsoleMessage("&4Potion: " + parts[0] + " will now be set to level 1.");
							if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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

	public static ItemStack setEnchantments(ConfigurationSection items, ItemStack tempitem, Player player) {
		if (items.getString(".enchantment") != null) {
			String enchantlist = items.getString(".enchantment").replace(" ", "");
			String[] enchantments = enchantlist.split(",");
			for (String enchantment: enchantments) {
				String[] parts = enchantment.split(":");
				String name = parts[0].toUpperCase();
				int level = 1;
				@SuppressWarnings("deprecation")
				Enchantment enchantName = Enchantment.getByName(name);
				if (enchantName == null && ServerHandler.hasAquaticUpdate()) {
					try {
					enchantName = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
					} catch (Exception e) { if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); } }
				}
				if (ItemHandler.containsIgnoreCase(enchantment, ":")) {
					try {
						level = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
						ServerHandler.sendConsoleMessage("&4An error occurred in the config, &c" + parts[1] + "&4 is not a number and a number was expected!");
						ServerHandler.sendConsoleMessage("&aEnchantment: " + parts[0] + " will now be enchanted by level 1.");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				}
				if (enchantName != null) {
					tempitem.addUnsafeEnchantment(enchantName, level);
				} else if (enchantName == null && Hooks.hasTokenEnchant() == true && TokenEnchantAPI.getInstance().getEnchant(name) != null) {
					TokenEnchantAPI.getInstance().enchant(player, tempitem, name, level, true, 0, true);
				} else if (enchantName == null && Hooks.hasTokenEnchant() != true) {
					ServerHandler.sendConsoleMessage("&4An error occurred in the config, &a" + name + "&4 is an incorrect enchantment name!");
					ServerHandler.sendConsoleMessage("&4Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
				}
			}
		}
		return tempitem;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack setMapImage(ItemStack tempitem, Material tempmat, String item, Player player) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		ServerHandler.sendConsoleMessage("&aONE " + hasPreviewed);
		if (items.getString(".custom-map-image") != null && ItemHandler.containsIgnoreCase(tempmat.toString(), "MAP")) {
			int mapID = 1;
			String mapIMG = items.getString(".custom-map-image");
			ServerHandler.sendConsoleMessage("&aTWO");
			if (mapIMG.equalsIgnoreCase("default.png") || new File(ItemJoin.getInstance().getDataFolder(), mapIMG).exists()) {
				ServerHandler.sendConsoleMessage("&aTHREE");
				if (SQLData.hasImage(player, item, mapIMG)) {
					ServerHandler.sendConsoleMessage("&aFOUR");
					mapID = SQLData.getMapID(player, mapIMG);
					if (ServerHandler.hasAquaticUpdate()) {
						MapMeta mapmeta = (MapMeta) tempitem.getItemMeta();
						mapmeta.setMapId(mapID);
						tempitem.setItemMeta(mapmeta);
						ServerHandler.sendConsoleMessage("&aFIVE");
					} else {
						tempitem.setDurability((short) mapID);
						ServerHandler.sendConsoleMessage("&aSIX");
					}
					if (RenderImageMaps.hasRendered.get(player) == null || RenderImageMaps.hasRendered.get(player) != null && !RenderImageMaps.hasRendered.get(player).toString().contains(mapID + "")) {
						ServerHandler.sendConsoleMessage("&aSEVEN");
						MapView view = RenderImageMaps.FetchExistingView(player, mapID);
						RenderImageMaps.setImage(mapIMG, mapID);
						if (!hasPreviewed) {
							ServerHandler.sendConsoleMessage("&aEIGHT");
							try {
								view.removeRenderer(view.getRenderers().get(0));
							} catch (NullPointerException e) {
								if (ServerHandler.hasDebuggingMode()) {
									e.printStackTrace();
								}
							}
							try {
								view.addRenderer(new RenderImageMaps());
							} catch (NullPointerException e) {
								if (ServerHandler.hasDebuggingMode()) {
									e.printStackTrace();
								}
							}
							hasPreviewed = true;
							ServerHandler.sendConsoleMessage("&aNINE");
						}
					}
				} else if (!SQLData.hasImage(player, item, mapIMG)) {
					ServerHandler.sendConsoleMessage("&aTEN");
					MapView view = RenderImageMaps.MapView(player, mapID);
					mapID = view.getId();
					if (ServerHandler.hasAquaticUpdate()) {
						MapMeta mapmeta = (MapMeta) tempitem.getItemMeta();
						mapmeta.setMapId(mapID);
						tempitem.setItemMeta(mapmeta);
					} else {
						tempitem.setDurability((short) mapID);
					}
					SQLData.saveMapImage(player, item, "map-id", mapIMG, mapID);
					RenderImageMaps.setImage(mapIMG, mapID);
					try {
						view.removeRenderer(view.getRenderers().get(0));
					} catch (NullPointerException e) {
						if (ServerHandler.hasDebuggingMode()) {
							e.printStackTrace();
						}
					}
					try {
						view.addRenderer(new RenderImageMaps());
					} catch (NullPointerException e) {
						if (ServerHandler.hasDebuggingMode()) {
							e.printStackTrace();
						}
					}
					hasPreviewed = true;
				}
			}
		}
		return tempitem;
	}

	public static Boolean isCompatible(String item, String slot) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		String id = items.getString(".id");
		String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
		String vers = pkgname.substring(pkgname.lastIndexOf('.') + 1);
		if (!ServerHandler.hasCombatUpdate()) {
			if (slot.equalsIgnoreCase("Offhand")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have Offhand support!");
				ServerHandler.sendConsoleMessage("&4Therefore, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("TIPPED_ARROW") || id.equalsIgnoreCase("440") || id.equalsIgnoreCase("0440")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item TIPPED_ARROW!");
				ServerHandler.sendConsoleMessage("&4Therefore, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("LINGERING_POTION") || id.equalsIgnoreCase("441") || id.equalsIgnoreCase("0441")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item LINGERING_POTION!");
				ServerHandler.sendConsoleMessage("&4Therefore, " + item + " will not be set!");
			} else if (ItemHandler.getMaterial(items) == null) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item " + id);
				ServerHandler.sendConsoleMessage("&4Therefore, " + item + " will not be set!");
			} else {
				return true;
			}
			ServerHandler.sendConsoleMessage("&4You are receiving this notice because this item(s) exists in your items.yml, please remove it!");
		} else if (isCreatable(item, slot)) {
			return true;
		}
		return false;
	}

	public static Boolean isComparable(ItemStack tempitem, String Mats) {
		Material getMaterial = Material.getMaterial(Mats);
		if (ItemHandler.isMaterial(Mats) && tempitem.getType() == getMaterial) {
			return true;
		}
		return false;
	}

	public static ItemMeta getTempMeta(ItemStack tempitem) {
		if (isComparable(tempitem, "WRITTEN_BOOK")) {
			return (BookMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "FIREWORK")) {
			return (FireworkMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "BANNER")) {
			return (BannerMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "SKULL_ITEM")) {
			return (SkullMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "LEATHER_HELMET") || isComparable(tempitem, "LEATHER_CHESTPLATE") 
				|| isComparable(tempitem, "LEATHER_LEGGINGS") || isComparable(tempitem, "LEATHER_BOOTS")) {
			return (LeatherArmorMeta) tempitem.getItemMeta();
		} else if (ServerHandler.hasCombatUpdate() && !ItemJoin.getInstance().getServer().getVersion().contains("(MC: 1.9)") 
				&& isComparable(tempitem, "TIPPED_ARROW")) {
			return (PotionMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "POTION") || isComparable(tempitem, "SPLASH_POTION") 
				|| ServerHandler.hasCombatUpdate() && isComparable(tempitem, "LINGERING_POTION")) {
			return (PotionMeta) tempitem.getItemMeta();
		} else if (isComparable(tempitem, "FILLED_MAP")) {
			return (MapMeta) tempitem.getItemMeta();
		}
		return tempitem.getItemMeta();
	}

	public static boolean isCreatable(String item, String slot) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		Material tempmat = ItemHandler.getMaterial(items);
		if (tempmat == null) {
			ServerHandler.sendConsoleMessage("&e" + item + "'s Material 'ID' is invalid or does not exist!");
			ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
			return false;
		}
		if (slot != null) {
			if (!Utils.isInt(slot) && !Utils.isCustomSlot(slot)) {
				ServerHandler.sendConsoleMessage("&e" + item + "'s slot is invalid or does not exist!");
				ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
				return false;
			} else if (Utils.isInt(slot)) {
				int iSlot = Integer.parseInt(slot);
				if (!(iSlot >= 0 && iSlot <= 35)) {
					ServerHandler.sendConsoleMessage("&e" + item + "'s slot must be between 1 and 36!");
					ServerHandler.sendConsoleMessage("&e" + item + " &ewill not be set!");
					return false;
				}
			}
		}
		return true;
	}
}