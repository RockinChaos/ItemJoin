package me.RockinChaos.itemjoin.cacheitems;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.SkullType;
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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class CreateItems {
	public static Map < String, ItemStack > items = new HashMap < String, ItemStack > ();

	public static void run(Player player) {
		CreateItems.items.remove(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items.");
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
						    tempitem = setNBTData(tempitem, ItemID); // New method to setting data to an item to identify that its an ItemJoin item.

							ItemMeta tempmeta = getTempMeta(tempitem);
							tempmeta = setName(items, tempmeta, tempitem, player, ItemID, null);
							tempmeta = setLore(items, tempmeta, player, null);
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
	
	@SuppressWarnings("unchecked")
	public static void setRun() {
		Collection < ? extends Player > playersOnlineNew;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				playersOnlineNew = (Collection < ? extends Player > )((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineNew) {
					run(player);
					InvClickCreative.isCreative(player, player.getGameMode());
					AnimationHandler.setAnimations(player);
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					run(player);
					InvClickCreative.isCreative(player, player.getGameMode());
					AnimationHandler.setAnimations(player);
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
			tempitem = setUnbreakable.Unbreakable(tempitem);
		  } catch (Exception e) {
			  if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		  }
		}
		return tempitem;
	}
	
	public static ItemStack setNBTData(ItemStack tempitem, String ItemID) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true) {
		try {
		Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
		Class<?> nmsItemStackClass = Reflection.getNMS("ItemStack");
		Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
		Object nms = getNMSI.invoke(null, tempitem);
		Object tag = Reflection.getNMS("NBTTagCompound").getConstructor().newInstance();
		Object cacheTag = nmsItemStackClass.getMethod("getTag").invoke(nms);
		if (cacheTag != null) {
			cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin", "Slot: " + ItemID);
			tempitem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
		} else {
		tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, "ItemJoin", "Slot: " + ItemID);
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
		  try {
		 	tempitem = setUnbreakable.Unbreakable(tempitem);
		  } catch (Exception e) {
			  if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		  }
		}
		return tempitem;
	}

	public static ItemMeta setName(ConfigurationSection items, ItemMeta tempmeta, ItemStack tempitem, Player player, String ItemID, String NameString) {
		if (items.getString(".name") != null) {
			String name = items.getString(".name");
			ConfigurationSection namePath = ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".name");
			if (namePath != null) {
			Set<String> nameKeys = namePath.getKeys(false);
			Iterator < String > it = nameKeys.iterator();
			while (it.hasNext()) { // maybe remove?
				String nameString = it.next();
				if (NameString != null) {
					nameString = NameString;
				}
				if (items.getString(".name." + nameString) != null) {
					name = items.getString(".name." + nameString);
					break;
				}
			  }
			}
			name = Utils.format("&r" + name.replace("<delay:" + Utils.returnInteger(name) + ">", ""), player);
			if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasBountifulUpdate()) {
				tempmeta.setDisplayName(name);
			} else {
				tempmeta.setDisplayName(name + ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID));
			}
		} else {
			String lookup = ItemHandler.getName(tempitem);
			String name = "";
			if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasBountifulUpdate()) {
				name = Utils.format("&r" + lookup, player);
			} else {
				name = Utils.format("&r" + lookup + ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), player);
			}
			tempmeta.setDisplayName(name);
		}
		return tempmeta;
	}

	public static ItemMeta setLore(ConfigurationSection items, ItemMeta tempmeta, Player player, String NameString) {
		if (items.getStringList(".lore") != null) {
			List < String > templist = items.getStringList(".lore");
			List < String > templist2 = new ArrayList < String > ();
			
			ConfigurationSection lorePath = ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".lore");
			if (lorePath != null) {
			Set<String> loreKeys = lorePath.getKeys(false);
			Iterator < String > it = loreKeys.iterator();
			while (it.hasNext()) { // maybe remove?
				String nameString = it.next();
				if (NameString != null) {
					nameString = NameString;
				}
				if (items.getStringList(".lore." + nameString) != null) {
					templist = items.getStringList(".lore." + nameString);
					break;
				}
			  }
			}
			
			for (int k = 0; k < templist.size(); k++) {
				String name = templist.get(k).replace("<delay:" + Utils.returnInteger(templist.get(k)) + ">", "");
				name = Utils.format(name, player);
				templist2.add(name);
			}
			tempmeta.setLore(templist2);
		}
		return tempmeta;
	}

	public static ItemMeta hideAttributes(ConfigurationSection items, ItemMeta tempmeta) {
		String ItemFlags = items.getString(".itemflags");
		if (ServerHandler.hasChangedTheWorldUpdate()) {
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
		if (items.getString(".firework.type") != null && tempmat == Material.FIREWORK) {
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
					clist.add(DyeColor.valueOf(coloring).getFireworkColor());
				}
			}
			FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(clist).withFade(clist).with(buildType).build();
			((FireworkMeta) tempmeta).clearEffects();
			((FireworkMeta) tempmeta).addEffect(effect);
			((FireworkMeta) tempmeta).setPower(power);
		}
		return tempmeta;
	}
	
	public static ItemMeta setFireChargeColor(ConfigurationSection items, ItemMeta tempmeta, Material tempmat) {
		if (tempmat == Material.FIREWORK_CHARGE && items.getString(".charge-color") != null) {
				String color = items.getString(".charge-color").toUpperCase();
                ((FireworkEffectMeta) tempmeta).setEffect(FireworkEffect.builder().withColor(DyeColor.valueOf(color).getColor()).build());
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
	
	public static ItemMeta setBanners(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".banner-meta") != null && ServerHandler.hasBountifulUpdate() && tempmat == Material.BANNER) {
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
		if (ServerHandler.hasBountifulUpdate() && items.getString(".skull-texture") != null && !items.getString(".skull-texture").contains("hdb-") && items.getString(".skull-owner") == null && tempmat == Material.SKULL_ITEM) {
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
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null && tempmat == Material.SKULL_ITEM) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
		}
        return tempmeta;
    }

	public static ItemMeta setSkull(ConfigurationSection items, Player player, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".skull-owner") != null && items.getString(".skull-texture") == null && tempmat == Material.SKULL_ITEM) {
			String owner = items.getString(".skull-owner");
			owner = Utils.format(owner, player);
			return PlayerHandler.setSkullOwner(tempmeta, owner);
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null && tempmat == Material.SKULL_ITEM) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
		}
		return tempmeta;
	}

	public static ItemStack setHeadDatabaseSkull(ConfigurationSection items, Material tempmat, ItemStack tempitem) {
		if (ServerHandler.hasBountifulUpdate() && Hooks.hasHeadDatabase() && items.getString(".skull-texture") != null && items.getString(".skull-texture").contains("hdb-") 
				&& items.getString(".skull-owner") == null && Utils.isInt(items.getString(".skull-texture").replace("hdb-", "")) && tempmat == Material.SKULL_ITEM) {
	      HeadDatabaseAPI api = new HeadDatabaseAPI();
	      try {
	    	  
	          ItemStack sk = api.getItemHead(items.getString(".skull-texture").replace("hdb-", ""));
	          return sk != null ? sk : tempitem.clone();
	      }
	      catch (NullPointerException e) {
	          ServerHandler.sendConsoleMessage("&4HeadDatabase could not find &c#" + items.getString(".skull-texture").replace("hdb-", "") + "&4, this head does not exist.");
	          if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
	      }
		} else if (items.getString(".skull-owner") != null && items.getString(".skull-texture") != null && tempmat == Material.SKULL_ITEM) {
			ServerHandler.sendConsoleMessage("&4You cannot define a skull owner and a skull texture at the same time, please remove one from the item.");
		}
		return tempitem;
	}
	
	public static ItemMeta setPotionEffects(ConfigurationSection items, Material tempmat, ItemMeta tempmeta) {
		if (items.getString(".potion-effect") != null) {
			if (tempmat == Material.POTION || ServerHandler.hasCombatUpdate() 
					&& tempmat == Material.SPLASH_POTION || ServerHandler.hasCombatUpdate() && tempmat == Material.LINGERING_POTION) {
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
				Enchantment enchantName = Enchantment.getByName(name);
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
			if (mapIMG.equalsIgnoreCase("default.png") || new File(ItemJoin.getInstance().getDataFolder(), mapIMG).exists()) {
				RenderImageMaps.setImage(mapIMG, mapID);
				try {
					view.removeRenderer(view.getRenderers().get(0));
				} catch (NullPointerException e) {
					if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
				}
			}
			try {
			view.addRenderer(new RenderImageMaps());
			} catch (NullPointerException e) {
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("TIPPED_ARROW") || id.equalsIgnoreCase("440") || id.equalsIgnoreCase("0440")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item TIPPED_ARROW!");
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else if (id.equalsIgnoreCase("SPLASH_POTION") || id.equalsIgnoreCase("373") || id.equalsIgnoreCase("0373")) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item SPLASH_POTION!");
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else if (ItemHandler.getMaterial(items) == null) {
				ServerHandler.sendConsoleMessage("&4Your server is running &eMC " + vers + " and this does not have the item " + id);
				ServerHandler.sendConsoleMessage("&4Because of this, " + item + " will not be set!");
			} else {
				return true;
			}
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
		} else if (isComparable(tempitem, "POTION") || ServerHandler.hasCombatUpdate() && isComparable(tempitem, "SPLASH_POTION") 
				|| ServerHandler.hasCombatUpdate() && isComparable(tempitem, "LINGERING_POTION")) {
			return (PotionMeta) tempitem.getItemMeta();
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