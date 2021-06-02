/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils.interfaces.menus;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemCommand;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemCommand.Action;
import me.RockinChaos.itemjoin.item.ItemCommand.CommandSequence;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.interfaces.Button;
import me.RockinChaos.itemjoin.utils.interfaces.Interface;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

	
/**
* Handles the in-game GUI.
* 
*/
public class Menu {
	private static String GUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7           &0&n ItemJoin Menu") : StringUtils.colorFormat("&7           &0&n ItemJoin Menu");
	private static ItemStack fillerPaneBItem = ItemHandler.getItem("STAINED_GLASS_PANE:15", 1, false, "&7", "");
	private static ItemStack fillerPaneGItem = ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, "&7", "");
	private static ItemStack fillerPaneItem = ItemHandler.getItem("GLASS_PANE", 1, false, "&7", "");
	private static ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, "&c&l&nExit", "&7", "&7*Returns you to the game");
	private static List<Player> modifyMenu = new ArrayList<Player>();

//  ============================================== //
//  			   Selection Menus      	       //
//	============================================== //
	
   /**
    * Opens the MAIN CREATOR PANE for the Player.
    * 
    * @param sender - The Sender to have the Pane opened.
    */
	public static void startMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		Interface pagedPane = new Interface(false, 1, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
			pagedPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, "&b&l&nConfig Settings", "&7", "&7*Change the GLOBAL plugin", "&7configuration settings."), event -> configSettings(player)));
			pagedPane.addButton(new Button(fillerPaneBItem));
			pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, "&a&l&nCreate", "&7", "&7*Create a new item from scratch."),
					event -> materialPane(player, new ItemMap("item_" + StringUtils.getPath(1), "ARBITRARY"), 0, 0)));
			pagedPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&e&l&nSave", "&7", "&7*Save an existing item as a custom item."), event -> startHopper(player)));
			pagedPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&c&l&nModify", "&7", "&7*Modify an existing custom item"), event -> startModify(player, null, 0)));
			pagedPane.addButton(new Button(fillerPaneBItem));
			pagedPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&b&l&nItem Settings", "&7", "&7*Change the GLOBAL custom items", "&7configuration settings."), event -> itemSettings(player)));
			pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
		});
		pagedPane.open(player);
	}
	
   /**
    * Opens the ITEM SETTINGS PANE for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void itemSettings(final Player player) {
		Interface itemPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			itemPane.addButton(new Button(fillerPaneBItem), 3);
			itemPane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&bTrigger Delay", "&7", "&7*This is the delay in half-seconds", "&7that ItemJoin will wait", "&7to give you the items.", "&7",
					"&cNOTE: &7It is recommended to", "&7set this to 2 or 3 half-seconds.", "&9&lDELAY: &a" + String.valueOf(ConfigHandler.getConfig().getFile("items.yml").getString("items-Delay")).toUpperCase() + " half-second(s)"), 
					event -> numberPane(player, 1)));
			itemPane.addButton(new Button(ItemHandler.getItem("COOKIE", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items"), 
					"&bItem Permissions", "&7", "&7*If custom items should require", "&7the player to have specific", "&7permissions to receive the item.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Permissions.Obtain-Items", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
			itemPane.addButton(new Button(ItemHandler.getItem("33", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP"), 
					"&bItem Permissions &c&l[OP PLAYERS]", "&7", "&7*If custom items should require", "&7the &c&lOP player(s)&7 to have specific", "&7permissions to receive the item.", "&c&lNOTE: &7This only applies to &c&lOP player(s)&7.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Permissions.Obtain-Items-OP", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));

			itemPane.addButton(new Button(fillerPaneBItem), 3);
			itemPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Overwrite"), 
					"&bOverwrite", "&7", "&7*Setting this to true will allow", "&7all custom items to overwrite", "&7any custom or vanilla items.", "&7", "&cNOTE: &7If set to false, the", "&7overwrite itemflag will still", "&7function normally.",
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("items.yml").getString("items-Overwrite")).toUpperCase()), event -> overwritePane(player)));
			itemPane.addButton(new Button(ItemHandler.getItem("85", 1, ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Spamming"), 
					"&bSpamming", "&7", "&7*Setting this to false will prevent", "&7players from macro spamming", "&7the use of item commands.", "&7", "&cNOTE: &7It is recommended to", "&7leave this set to false.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Spamming")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("items-Spamming", !ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Spamming")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "items.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
			itemPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-RestrictCount"), 
					"&bRestrict Count", "&7", "&7*Settings this to true will", "&7allow players to have their items", "&7refreshed (topped up) if they have", "&7used/consumed some of the given", "&7stack of custom items.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-RestrictCount")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("items-RestrictCount", !ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-RestrictCount")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "items.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
			itemPane.addButton(new Button(ItemHandler.getItem("17", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Log-Commands"), 
					"&bLog Commands", "&7", "&7*If the plugin prevent CONSOLE", "&7from logging any executed", "&7comamnd from the custom items.", "&7This only works for item command(s).", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Log-Commands")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("General.Log-Commands", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Log-Commands")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
			final int heldSlot = ConfigHandler.getConfig().getFile("config.yml").getInt("Settings.HeldItem-Slot");
			itemPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", (heldSlot > 0 ? heldSlot : 1), false, 
					"&bHeld Item Slot", "&7", "&7*This is the hotbar slot that", "&7the player will automatically", "&7have selected upon performing", "&7one of the held item triggers.", 
					"&9&lSLOT: &a" + String.valueOf((heldSlot >= 0 ? heldSlot : "NONE"))), 
					event -> numberPane(player, 2)));
			final String heldTriggers = ConfigHandler.getConfig().getFile("config.yml").getString("Settings.HeldItem-Triggers");
			itemPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, 
					"&bHeld Item Triggers", "&7", "&7*When these trigger(s)", "&7are performed, the held item", "&7slot will be set.", 
					"&9&lENABLED: &a" + String.valueOf((!heldTriggers.isEmpty() && !StringUtils.containsIgnoreCase(heldTriggers, "DISABLE")) ? heldTriggers : "FALSE").toUpperCase()), 
					event -> triggerPane(player)));
			itemPane.addButton(new Button(ItemHandler.getItem("116", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Settings.DataTags"), 
					"&bDataTags", "&7", "&7*If custom items should use", "&7data tags (NBTTags) to distinguish", "&7each custom item, making them unqiue.", 
					"&c&lNOTE: &7This only works on Minecraft 1.8+", "&7It is recommended to keep", "&7this set to TRUE.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Settings.DataTags")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Settings.DataTags", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Settings.DataTags")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().reloadConfigs(true);
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));

			itemPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, 
					"&bClear Items", "&7", "&7*Modify settings for clearing", "&7specific items when a player", "&7performed a specified action."),
					event -> clearPane(player)));
			itemPane.addButton(new Button(ItemHandler.getItem("137", 1, false, 
					"&bPrevent Actions", "&7", "&7*Disable certain actions", "&7with items for players."),
					event -> preventPane(player)));
			itemPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
			itemPane.addButton(new Button(fillerPaneBItem), 7);
			itemPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
		});
		itemPane.open(player);
	}
	
   /**
    * Opens the CONFIG SETTINGS PANE for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void configSettings(final Player player) {
		Interface configPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			configPane.addButton(new Button(fillerPaneBItem), 3);
			configPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, 
					"&6Language", "&7", "&7*The selected lang.yml language.", "&7This is for messages sent", "&7from the plugin to the player.", 
					"&9&lLANG: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getString("Language")).toUpperCase()), 
					event -> languagePane(player)));
			configPane.addButton(new Button(fillerPaneBItem));
			configPane.addButton(new Button(ItemHandler.getItem("137", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Database.MySQL"), 
					"&bMySQL Database", "&7", "&7*If the plugin should use", "&7a MySQL Database instead", "&7of the locale SQLite Database.",
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Database.MySQL")).toUpperCase()), 
					event -> databasePane(player)));
			configPane.addButton(new Button(fillerPaneBItem), 4);
			configPane.addButton(new Button(ItemHandler.getItem("BUCKET", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.CheckforUpdates"), 
					"&bCheck for Updates", "&7", "&7*If the plugin should check", "&7for updates at start-up.", "&7This includes the use of the", "&7/itemjoin updates/upgrade command(s).", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.CheckforUpdates")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("General.CheckforUpdates", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.CheckforUpdates")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(ItemHandler.getItem("COMPASS", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Metrics-Logging"), 
					"&bMetrics Logging", "&7", "&7*If the plugin is allowed", "&7to log plugin data such as", "&7the server(s) Java version.", "&7It is recommended to keep this", "&7set to true as it improves", "&7the quality of plugin updates.",
					"&7", "&7You can view the logged data", "&7Here: https://bstats.org/plugin/bukkit/ItemJoin",
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Metrics-Logging")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("General.Metrics-Logging", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Metrics-Logging")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(ItemHandler.getItem("STICK", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Debugging"), 
					"&bDebugging", "&7", "&7*Allows for more detailed", "&7CONSOLE messages from the plugin.", "&7Typically only used by the", "&7plugin developer to determine", "&7issues or bugs with the plugin.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Debugging")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("General.Debugging", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Debugging")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(fillerPaneBItem));
			configPane.addButton(new Button(ItemHandler.getItem("322", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-OP"), 
					"&bPlugin Commands &c&l[OP PLAYERS]", "&7", "&7*If the plugin should check", "&7if the OP player has the", "&7proper permissions set to", "&7use the plugin commands.", 
					"&7OP Players will no longer get", "&7access to all plugin commands", "&7by default.",
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-OP")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Permissions.Commands-OP", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-OP")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-Get"), 
					"&bGet Commands", "&7", "&7*If the get and getAll", "&7commands should check for item", "&c&lpermissions &7before giving the item.",
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-Get")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Permissions.Commands-Get", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-Get")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Movement-Bypass"), 
					"&bMovement Bypass", "&7", "&7*Enables the use of the", "&aitemjoin.bypass.inventorymodify", "&7permission-node, used to ignore", "&7the global itemMovement prevention", "&7or a custom items itemflag.", 
					"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Movement-Bypass")).toUpperCase()), 
					event -> {
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Permissions.Movement-Bypass", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Movement-Bypass")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> configSettings(player));
					}));
			configPane.addButton(new Button(fillerPaneBItem));
			configPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
			configPane.addButton(new Button(fillerPaneBItem), 7);
			configPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
		});
		configPane.open(player);
	}
	
   /**
    * Opens the MODIFY PANE for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void startModify(final Player player, final ItemMap itemMap, final int k) {
		Interface modifyPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			setPage(player, modifyPane, ItemUtilities.getUtilities().copyItems(), null, itemMap, k);
		});
		modifyPane.open(player);
	}
	
   /**
    * Opens the SAVING PANE for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void startHopper(Player player) {
		Interface dragDrop = new Interface(false, 1, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			dragDrop.allowClick(true);
			dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
			dragDrop.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&a&lAutosave", "&7", "&7*Click me to save your whole", "&7inventory to the items.yml as-is,", "&7including current slot positions!", "&7" , "&c&l[&nALL&c&l ITEMS]"), event -> {
				PlayerInventory playerInv = event.getWhoClicked().getInventory();
				if (playerInv != null) {
					for (int i = 0; i <= 35; i++) {
						if (playerInv.getItem(i) != null && playerInv.getItem(i).getType() != Material.AIR) {
							convertStack(player, playerInv.getItem(i), Integer.toString(i));
						}
					}
					if (playerInv.getHelmet() != null && playerInv.getHelmet().getType() != Material.AIR) { convertStack(player, playerInv.getHelmet(), "HELMET"); }
					if (playerInv.getChestplate() != null && playerInv.getChestplate().getType() != Material.AIR) { convertStack(player, playerInv.getChestplate(), "CHESTPLATE"); }
					if (playerInv.getLeggings() != null && playerInv.getLeggings().getType() != Material.AIR) { convertStack(player, playerInv.getLeggings(),"LEGGINGS"); }
					if (playerInv.getBoots() != null && playerInv.getBoots().getType() != Material.AIR) { convertStack(player, playerInv.getBoots(), "BOOTS"); }
					if (ServerUtils.hasSpecificUpdate("1_9") && PlayerHandler.getOffHandItem(player) != null && PlayerHandler.getOffHandItem(player).getType() != Material.AIR) { convertStack(player, PlayerHandler.getOffHandItem(player), "OFFHAND"); }
					ConfigHandler.getConfig().reloadConfigs(true);
					startMenu(player);
				}
			}));
			dragDrop.addButton(new Button(fillerPaneGItem), 2);
			dragDrop.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&a&lDrop an Item", "&7", "&7*Click an item from your inventory", "&7to save and drop it in this", "&7friendly little hopper!", "&7" , "&a&l[&nSINGLE&a&l ITEM]"), event -> {
				if (event.getCursor().getType() != Material.AIR) {
					ItemStack item = event.getCursor().clone();
					event.getWhoClicked().setItemOnCursor(null);
					event.getWhoClicked().getInventory().addItem(item);
					convertStack(player, item, null);
					dragDrop.allowClick(false);
				}
			}));
			dragDrop.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, "", "")), 2);
			dragDrop.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&a&lAutosave", "&7", "&7*Click me to save your whole", "&7inventory to the items.yml as-is,", "&7including current slot positions!", "&7" , "&c&l[&nALL&c&l ITEMS]"), event -> {
				PlayerInventory playerInv = event.getWhoClicked().getInventory();
				if (playerInv != null) {
					for (int i = 0; i <= 35; i++) {
						if (playerInv.getItem(i) != null && playerInv.getItem(i).getType() != Material.AIR) {
							convertStack(player, playerInv.getItem(i), Integer.toString(i));
						}
					}
					if (playerInv.getHelmet() != null && playerInv.getHelmet().getType() != Material.AIR) { convertStack(player, playerInv.getHelmet(), "HELMET"); }
					if (playerInv.getChestplate() != null && playerInv.getChestplate().getType() != Material.AIR) { convertStack(player, playerInv.getChestplate(), "CHESTPLATE"); }
					if (playerInv.getLeggings() != null && playerInv.getLeggings().getType() != Material.AIR) { convertStack(player, playerInv.getLeggings(),"LEGGINGS"); }
					if (playerInv.getBoots() != null && playerInv.getBoots().getType() != Material.AIR) { convertStack(player, playerInv.getBoots(), "BOOTS"); }
					if (ServerUtils.hasSpecificUpdate("1_9") && PlayerHandler.getOffHandItem(player) != null && PlayerHandler.getOffHandItem(player).getType() != Material.AIR) { convertStack(player, PlayerHandler.getOffHandItem(player), "OFFHAND"); }
					ConfigHandler.getConfig().reloadConfigs(true);
					startMenu(player);
				}
			}));
			dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
		});
		dragDrop.open(player);
	}
	
//  ===================================================================================================================================================================================================================
	
//  ============================================== //
//  			   Menu Utilities        	       //
//  ============================================== //
	
   /**
    * Adds a button to the PagedPane.
    * 
    * @param player - The Player.
    * @param itemMap - The ItemMap currently being modified.
    * @param pagedPane - The PagedPane.
    */
	private static void setButton(final Player player, final ItemMap itemMap, final Interface modifyPane, final ItemMap contents, final ItemMap refMap, final int k) {
		final ItemStack item = itemMap.getTempItem().clone();
		if (item.getType() == Material.AIR) { item.setType(fillerPaneItem.getType()); }
		if (itemMap.isAnimated() || itemMap.isDynamic()) { setModifyMenu(true, player); itemMap.getAnimationHandler().get(player).setMenu(true, 0); }
		String lore = (contents == null && refMap == null ? "&7*Click to modify this custom item." : refMap != null ? "&7*Click to set this custom item as an ingredient." : "&7*Click to add into the contents of " + contents.getConfigName() + ".");
		String space = (contents == null ? "&6---------------------------" : "&6----------------------------------");
		modifyPane.addButton(new Button(ItemHandler.addLore(item, "&7", space, lore, "&9&lNode: &a" + itemMap.getConfigName(), "&7", (contents != null ? "&9&lENABLED: " + (contents.getContents().contains(itemMap.getConfigName()) ? "&aYES" : "&aNO") : "")), event -> 
		{
			if (refMap != null) {
				setIngredients(player, refMap, itemMap.getConfigName(), k);
			} else if (contents == null) {
				choicePane(player, itemMap, item);
			} else {
				List<String> contentList = contents.getContents();
				if (contents.getContents().contains(itemMap.getConfigName())) { contentList.remove(itemMap.getConfigName()); } 
				else { contentList.add(itemMap.getConfigName()); }
				contents.setContents(contentList);
				Interface contentsPane = new Interface(true, 6, GUIName, player);
				SchedulerUtils.runAsync(() -> {
					setPage(player, contentsPane, ItemUtilities.getUtilities().copyItems(), contents, refMap, k);
				});
				contentsPane.open(player);
			}
		}));
	}
	
   /**
    * Adds the ItemMaps to the PagedPane for viewing.
    * 
    * @param player - The Player.
    * @param modifyPane - The PagedPane to have buttons added.
    * @param items - The items to be added to the Pane.
    */
	private static void setPage(final Player player, final Interface modifyPane, final List < ItemMap > items, final ItemMap contents, final ItemMap itemMap, final int k) {
		ItemMap currentItem = null;
		boolean crafting = false;
		boolean arbitrary = false;
		if (contents != null) {
			modifyPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to the item definition menu."), event -> creatingPane(player, contents)));
		} else if (itemMap != null) { 
			modifyPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to the recipe menu."), event -> recipePane(player, itemMap)));
		}
		Interface craftingPane = new Interface(false, 4, GUIName, player);
		craftingPane.addButton(new Button(fillerPaneGItem), 3);
		currentItem = ItemUtilities.getUtilities().getItemMap("CRAFTING[1]", items);
		if (currentItem != null) {
			crafting = true;
			setButton(player, currentItem, craftingPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(fillerPaneGItem));
		}
		craftingPane.addButton(new Button(fillerPaneGItem));
		currentItem = ItemUtilities.getUtilities().getItemMap("CRAFTING[2]", items);
		if (currentItem != null) {
			crafting = true;
			setButton(player, currentItem, craftingPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(fillerPaneGItem));
		}
		craftingPane.addButton(new Button(fillerPaneGItem), 10);
		currentItem = ItemUtilities.getUtilities().getItemMap("CRAFTING[0]", items);
		if (currentItem != null) {
			crafting = true;
			setButton(player, currentItem, craftingPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(fillerPaneGItem));
		}
		craftingPane.addButton(new Button(fillerPaneGItem), 4);
		currentItem = ItemUtilities.getUtilities().getItemMap("CRAFTING[3]", items);
		if (currentItem != null) {
			crafting = true;
			setButton(player, currentItem, craftingPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(fillerPaneGItem));
		}
		craftingPane.addButton(new Button(fillerPaneGItem));
		currentItem = ItemUtilities.getUtilities().getItemMap("CRAFTING[4]", items);
		if (currentItem != null) {
			crafting = true;
			setButton(player, currentItem, craftingPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(fillerPaneGItem));
		}
		craftingPane.addButton(new Button(fillerPaneGItem), 3);
		String lore = (contents == null ? "&7the modifying selection menu" : "&7the contents selection menu.");
		craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
		craftingPane.addButton(new Button(fillerPaneBItem), 7);
		craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
		Interface arbitraryPane = new Interface(true, 6, GUIName, player);
		arbitraryPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
		List < ItemMap > tempList = new ArrayList < ItemMap > ();
		tempList.addAll(items);
		for (final ItemMap item: tempList) {
			if (item.getSlot().equalsIgnoreCase("ARBITRARY")) {
				setButton(player, item, arbitraryPane, contents, itemMap, k);
				items.remove(item);
				arbitrary = true;
			}
		}
		modifyPane.addButton(new Button(fillerPaneGItem));
		if (arbitrary == true) {
			modifyPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&fArbitrary", "&7", "&7*Click to view the existing", "&7Arbitrary slot items."), event -> arbitraryPane.open(player)));
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		if (crafting == true) {
			modifyPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&fCrafting", "&7", "&7*Click to view the existing", "&7crafting slot items."), event -> craftingPane.open(player)));
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		currentItem = ItemUtilities.getUtilities().getItemMap("HELMET", items);
		if (currentItem != null) {
			setButton(player, currentItem, modifyPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		currentItem = ItemUtilities.getUtilities().getItemMap("CHESTPLATE", items);
		if (currentItem != null) {
			setButton(player, currentItem, modifyPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		currentItem = ItemUtilities.getUtilities().getItemMap("LEGGINGS", items);
		if (currentItem != null) {
			setButton(player, currentItem, modifyPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		currentItem = ItemUtilities.getUtilities().getItemMap("BOOTS", items);
		if (currentItem != null) {
			setButton(player, currentItem, modifyPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		currentItem = ItemUtilities.getUtilities().getItemMap("OFFHAND", items);
		if (currentItem != null) {
			setButton(player, currentItem, modifyPane, contents, itemMap, k);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(fillerPaneGItem));
		}
		modifyPane.addButton(new Button(fillerPaneGItem));
		for (int i = 9; i < 36; i++) {
			currentItem = ItemUtilities.getUtilities().getItemMap(i + "", items);
			if (currentItem != null) {
				setButton(player, currentItem, modifyPane, contents, itemMap, k);
				items.remove(currentItem);
			} else {
				modifyPane.addButton(new Button(fillerPaneGItem));
			}
		}
		for (int j = 0; j < 9; j++) {
			currentItem = ItemUtilities.getUtilities().getItemMap(j + "", items);
			if (currentItem != null) {
				setButton(player, currentItem, modifyPane, contents, itemMap, k);
				items.remove(currentItem);
			} else {
				modifyPane.addButton(new Button(fillerPaneGItem));
			}
		}
		if (!items.isEmpty()) {
			setPage(player, modifyPane, items, contents, itemMap, k);
		}
	}
	
   /**
    * Converts the ItemStack that is attempting to be saved to a ItemMap.
    * 
    * @param player - The Player saving the ItemStack.
    * @param item - The ItemStack to be saved.
    */
	private static void convertStack(final Player player, final ItemStack item, final String slot) {
		ItemMap itemMap = new ItemMap("item_" + StringUtils.getPath(1), "ARBITRARY");
		itemMap.setMaterial(item.getType());
		if (!ServerUtils.hasSpecificUpdate("1_13")) { itemMap.setDataValue((short)LegacyAPI.getDataValue(item)); }
		itemMap.setCount(String.valueOf(item.getAmount()));
		if (item.getType().getMaxDurability() > 30 && ItemHandler.getDurability(item) != 0 && ItemHandler.getDurability(item) != (item.getType().getMaxDurability())) {
			itemMap.setDurability(ItemHandler.getDurability(item));
		}
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				itemMap.setCustomName(item.getItemMeta().getDisplayName().replace("§", "&"));
			}
			if (item.getItemMeta().hasLore()) {
				List < String > newLore = new ArrayList < String > ();
				for (String lore: item.getItemMeta().getLore()) {
					newLore.add(lore.replace("§", "&"));
				}
				itemMap.setCustomLore(newLore);
			}
			if (item.getItemMeta().hasEnchants()) {
				Map < String, Integer > enchantList = new HashMap < String, Integer > ();
				for (Enchantment e: item.getItemMeta().getEnchants().keySet()) {
					enchantList.put(ItemHandler.getEnchantName(e).toUpperCase(), item.getItemMeta().getEnchants().get(e));
				}
				itemMap.setEnchantments(enchantList);
			}
		}
		if (StringUtils.containsIgnoreCase(item.getType().toString(), "LEATHER_")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (meta.getColor() != null) {
				itemMap.setLeatherColor(DyeColor.getByColor(meta.getColor()).name());
			}
		} else if (item.getType().toString().equalsIgnoreCase("SKULL_ITEM") || item.getType().toString().equalsIgnoreCase("PLAYER_HEAD")) {
			if (!PlayerHandler.getSkullOwner(item).equalsIgnoreCase("NULL")) {
				itemMap.setSkull(PlayerHandler.getSkullOwner(item));
			} else if (!ItemHandler.getSkullTexture(item.getItemMeta()).isEmpty()) {
				itemMap.setSkullTexture(ItemHandler.getSkullTexture(item.getItemMeta()));
			}
		} else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
			itemMap.setChargeColor(DyeColor.getByFireworkColor(((FireworkEffectMeta) item.getItemMeta()).getEffect().getColors().get(0)));
		} else if (itemMap.getMaterial().toString().contains("BANNER")) {
			itemMap.setBannerPatterns(((BannerMeta) item.getItemMeta()).getPatterns());
		} else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
			List < DyeColor > colors = new ArrayList < DyeColor > ();
			for (Color color: ((FireworkMeta) item.getItemMeta()).getEffects().get(0).getColors()) {
				colors.add(DyeColor.getByFireworkColor(color));
			}
			itemMap.setFirework(((FireworkMeta) item.getItemMeta()).getEffects().get(0));
			itemMap.setFireworkColor(colors);
			itemMap.setFireworkFlicker(((FireworkMeta) item.getItemMeta()).getEffects().get(0).hasFlicker());
			itemMap.setFireworkTrail(((FireworkMeta) item.getItemMeta()).getEffects().get(0).hasTrail());
			itemMap.setFireworkType(((FireworkMeta) item.getItemMeta()).getEffects().get(0).getType());
			itemMap.setFireworkPower(((FireworkMeta) item.getItemMeta()).getPower());
		} else if (itemMap.getMaterial() == Material.WRITTEN_BOOK) {
			itemMap.setAuthor(((BookMeta) item.getItemMeta()).getAuthor().replace("§", "&"));
			itemMap.setTitle(((BookMeta) item.getItemMeta()).getTitle().replace("§", "&"));
			if (ServerUtils.hasSpecificUpdate("1_10")) {
				itemMap.setGeneration(((BookMeta) item.getItemMeta()).getGeneration());
			}
			List < String > newPages = new ArrayList < String > ();
			for (String page: ((BookMeta) item.getItemMeta()).getPages()) {
				newPages.add(page.replace("§", "&"));
			}
			itemMap.setPages(newPages);
			List < List < String >> savePages = new ArrayList < List < String >> ();
			for (String page: ((BookMeta) item.getItemMeta()).getPages()) {
				List < String > pageList = new ArrayList < String > ();
				for (String splitPage: page.split("\n")) {
					pageList.add(splitPage.replace("§", "&"));
				}
				savePages.add(pageList);
			}
			itemMap.setListPages(savePages);
		}
		if (slot == null) { switchPane(player, itemMap, 0); }
		else { 
			itemMap.setSlot(slot);
			itemMap.saveToConfig();
			String[] placeHolders = LanguageAPI.getLang(false).newString();
			placeHolders[3] = (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : itemMap.getConfigName());
			LanguageAPI.getLang(false).sendLangMessage("commands.menu.itemSaved", player, placeHolders);
		}
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting item modification or deletion.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param item - The ItemStack currently being modified.
    */
	private static void choicePane(final Player player, final ItemMap itemMap, final ItemStack item) {
		Interface choicePane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			choicePane.addButton(new Button(fillerPaneBItem), 4);
			choicePane.addButton(new Button(item));
			choicePane.addButton(new Button(fillerPaneBItem), 4);
			choicePane.addButton(new Button(fillerPaneBItem), 3);
			choicePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, true, "&b&lSettings", "&7", "&7*Change the settings for this item.", "&7Make changes to the item name, lore,", "&7permissions, enabled-worlds, and more."), event -> {
				creatingPane(player, itemMap);
			}));
			choicePane.addButton(new Button(fillerPaneBItem));
			choicePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, true, "&c&lDelete", "&7", "&7*Delete this item.", "&7This will remove the item from the", "&7items.yml and will no longer be useable.", "&c&lWARNING: &7This &lCANNOT &7be undone!"), event -> {
				itemMap.removeFromConfig();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[3] = itemMap.getConfigName();
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.itemRemoved", player, placeHolders);
				ConfigHandler.getConfig().reloadConfigs(true);
				SchedulerUtils.runLater(6L, () -> {
					startModify(player, null, 0); 
				});
			}));
			choicePane.addButton(new Button(fillerPaneBItem), 3);
			choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> startModify(player, null, 0)));
			choicePane.addButton(new Button(fillerPaneBItem), 7);
			choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> startModify(player, null, 0)));
		});
		choicePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for Creating a NEW custom item.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void creatingPane(final Player player, final ItemMap itemMap) {
		Interface creatingPane = new Interface(false, 5, GUIName, player);
			SchedulerUtils.runAsync(() -> {
			String slotList = "";
			String slotString = "";
			if (StringUtils.nullCheck(itemMap.getMultipleSlots().toString()) != "NONE") {
				for (String slot: itemMap.getMultipleSlots()) {
					slotString += slot + ", ";
				}
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(slotString.substring(0, slotString.length())))) {
					slotList += "&a" + split + " /n ";
				}
			}
			String itemflagsList = "";
			if (StringUtils.nullCheck(itemMap.getItemFlags()) != "NONE") {
				for (String split: StringUtils.softSplit(itemMap.getItemFlags())) {
					itemflagsList += "&a" + split + " /n ";
				}
			}
			String triggersList = "";
			if (StringUtils.nullCheck(itemMap.getTriggers()) != "NONE") {
				for (String split: StringUtils.softSplit(itemMap.getTriggers())) {
					triggersList += "&a" + split + " /n ";
				}
			}
			String worldList = "";
			if (StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE") {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
					worldList += "&a" + split + " /n ";
				}
			}
			String regionList = "";
			if (StringUtils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE") {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledRegions().toString()))) {
					regionList += "&a" + split + " /n ";
				}
			}
			String enchantList = "";
			if (StringUtils.nullCheck(itemMap.getEnchantments().toString()) != "NONE") {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnchantments().toString()))) {
					enchantList += "&a" + split + " /n ";
				}
			}
			String potionList = "";
			String potionString = "";
			if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
				for (PotionEffect potions: itemMap.getPotionEffect()) {
					potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() + ", ";
				}
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length())))) {
					potionList += "&a" + split + " /n ";
				}
			}
			String attributeList = "";
			String attributeString = "";
			if (StringUtils.nullCheck(itemMap.getAttributes().toString()) != "NONE") {
				for (String attribute: itemMap.getAttributes().keySet()) {
					attributeString += attribute + ":" + itemMap.getAttributes().get(attribute) + ", ";
				}
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(attributeString.substring(0, attributeString.length())))) {
					attributeList += "&a" + split + " /n ";
				}
			}
			String patternList = "";
			String patternString = "";
			if (StringUtils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
				for (Pattern patterns: itemMap.getBannerPatterns()) {
					patternString += patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase() + ", ";
				}
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(patternString.substring(0, patternString.length())))) {
					patternList += "&a" + split + " /n ";
				}
			}
			creatingPane.addButton(new Button(fillerPaneGItem), 4);
			creatingPane.addButton(new Button(headerStack(player, itemMap)));
			creatingPane.addButton(new Button(fillerPaneGItem), 4);
			creatingPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""), 1, false, "&c&lMaterial", "&7", "&7*Set the material of the item.", "&9&lMATERIAL: &a" + 
			itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : "")), event -> { 
				if (itemMap.getDynamicMaterials() != null && !itemMap.getDynamicMaterials().isEmpty()) {
					animateMaterialPane(player, itemMap);
				} else {
					materialPane(player, itemMap, 1, 0);
				}
				}));
			creatingPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, "&c&lSlot", "&7", "&7*Set the slot that the", "&7item will be given in.", (itemMap.getMultipleSlots() != null && 
					!itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSLOT: &a" + itemMap.getSlot().toUpperCase())), event -> switchPane(player, itemMap, 1)));
			creatingPane.addButton(new Button(ItemHandler.getItem("DIAMOND", itemMap.getCount(), false, "&b&lCount", "&7", "&7*Set the amount of the", "&7item to be given.", "&9&lCOUNT: &a" + 
					itemMap.getCount()), event -> countPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&b&lName", "&7", "&7*Set the name of the item.", "&9&lNAME: &f" + StringUtils.nullCheck(itemMap.getCustomName())), event -> {
				if (itemMap.getDynamicNames() != null && !itemMap.getDynamicNames().isEmpty()) {
					animatedNamePane(player, itemMap);
				} else {
					if (StringUtils.nullCheck(itemMap.getCustomName()) != "NONE") {
						itemMap.setCustomName(null);
						creatingPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "NAME";
						placeHolders[15] = "&bUltimate Sword";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
					}
				}
			}, event -> {
				itemMap.setCustomName(ChatColor.stripColor(event.getMessage()));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NAME";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				creatingPane(event.getPlayer(), itemMap);
			}));
			creatingPane.addButton(new Button(ItemHandler.getItem("386", 1, false, "&b&lLore", "&7", "&7*Set the lore of the item.", "&9&lLORE: &f" + StringUtils.nullCheck(itemMap.getCustomLore().toString())), event -> {
				if (itemMap.getDynamicLores() != null && !itemMap.getDynamicLores().isEmpty()) {
					animatedLorePane(player, itemMap);
				} else { 
					lorePane(player, itemMap);
				}
			}, event -> {
				itemMap.setCustomLore(StringUtils.split(ChatColor.stripColor(event.getMessage())));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "LORE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				creatingPane(event.getPlayer(), itemMap);
			}));
			creatingPane.addButton(new Button(ItemHandler.setDurability(ItemHandler.getItem("DIAMOND_BOOTS", 1, false, "&e&lData", "&7", "&7*Set the damage or the", "&7custom texture of the item."), 160), event -> { dataPane(player, itemMap); }));
			creatingPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, "&e&lCommand Settings", "&7", "&7*Define commands for the item", "&7which execute upon being", "&7interacted with."), event -> commandPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("ENCHANTED_BOOK", 1, false, "&b&lEnchantments", "&7", "&7*Add enchants to make the", "&7item sparkle and powerful.", "&9&lENCHANTMENTS: &a" + 
			(StringUtils.nullCheck(itemMap.getEnchantments().toString()) != "NONE" ? "&a" + enchantList : "NONE")), event -> enchantPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&b&lItemflags", "&7", "&7*Special flags that will give", "&7the item abilities and", "&7custom features.", "&9&lITEMFLAGS: &a" + 
			(StringUtils.nullCheck(itemMap.getItemFlags()) != "NONE" ? "&a" + itemflagsList : "NONE")), event -> flagPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&b&lTriggers", "&7", "&7*When the players act upon these", "&7events, the item will be given.", "&9&lTRIGGERS: &a" +
			(StringUtils.nullCheck(itemMap.getTriggers()) != "NONE" ? "&a" + triggersList : "NONE")), event -> triggerPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_15") ? "REDSTONE_TORCH" : "76"), 1, false, "&b&lPermission Node", "&7", "&7*Custom permission node that", "&7will be required by a permission", "&7plugin to receive the item.", "&7&lNote: &7Do NOT include", 
					"&7any spaces or special characters", "&9&lPERMISSION-NODE: &a" + StringUtils.nullCheck(itemMap.getPermissionNode())), event -> {
				if (StringUtils.nullCheck(itemMap.getPermissionNode()) != "NONE") {
					itemMap.setPerm(null);
					creatingPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "CUSTOM PERMISSION";
					placeHolders[15] = "itemjoin.ultra";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setPerm(ChatColor.stripColor(event.getMessage()));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "CUSTOM PERMISSION";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				creatingPane(event.getPlayer(), itemMap);
			}));
			creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, false, "&b&lEnabled Worlds", "&7", "&7*Define the world(s) that the", "&7item will be given in.", 
					"&9&lENABLED-WORLDS: &a" + (StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE" ? "&a" + worldList : "NONE")), event -> worldPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, true, "&b&lEnabled Regions", "&7", "&7*Define the region(s) that the", "&7item will be given in.", (DependAPI.getDepends(false).getGuard().guardEnabled() ? 
					"&9&lENABLED-REGIONS: &a" + (StringUtils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE" ? "&a" + regionList : "NONE") : ""), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7"), 
					(DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), event -> {
				if (DependAPI.getDepends(false).getGuard().guardEnabled()) {
					regionPane(player, itemMap);
				}
			}));
			creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STICKY_PISTON" : "29"), 1, false, "&e&lAnimation Settings", "&7", "&7*Define animations for the item", "&7Example: Custom iterations for the", 
					"&7items name, lore, and material type"), event -> animationPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_FENCE" : "FENCE"), 1, false, "&b&lLimit-Modes", "&7", "&7*Define the gamemode(s) that the", "&7item will be limited to.", "&9&lLIMIT-MODES: &a" + 
					StringUtils.nullCheck(itemMap.getLimitModes())), event -> limitPane(player, itemMap)));
			creatingPane.addButton(new Button(ItemHandler.getItem("NETHER_STAR", 1, false, "&b&lProbability", "&7", "&7*Define the chance that the", "&7item will be given to the player.", "&7", "&c&lNOTICE:&7 Only ONE item defined with", "&7a probability value will be selected.", "&7Probability is the same as a dice roll.", "&7", "&9&lPROBABILITY: &a" +
					StringUtils.nullCheck(itemMap.getProbability() + "&a%")), event -> {
				if (StringUtils.nullCheck(itemMap.getProbability() + "&a%") != "NONE") {
					itemMap.setProbability(-1);
					creatingPane(player, itemMap);
				} else {
					probabilityPane(player, itemMap);
				}
			}));
			creatingPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, "&b&lUsage Cooldown", "&7", "&7*Define the cooldown for", "&7interacting with the item.", "&9&lUSE-COOLDOWN: &a" +
			StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7")), event -> {
				if (StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7") != "NONE") {
					itemMap.setInteractCooldown(0);
					creatingPane(player, itemMap);
				} else {
					usePane(player, itemMap);
				}
			}));
			creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_INGOT", 1, false, "&e&lDrop Chances", "&7", "&7*Define the drop chance for receiving", "&7this item from mobs or breaking blocks."), event -> {
					dropsPane(player, itemMap);
			}));
			creatingPane.addButton(new Button(fillerPaneGItem));
			creatingPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, "&b&lConditions", "&7", "&7*Define conditions for triggers,", "&7commands, and the disposable itemflag.", "&9Enabled: &a" + 
			((itemMap.getTriggerConditions() != null && !itemMap.getTriggerConditions().isEmpty()) || (itemMap.getDisposableConditions() != null && !itemMap.getDisposableConditions().isEmpty()) 
			|| (itemMap.getCommandConditions() != null && !itemMap.getCommandConditions().isEmpty()) ? "YES" : "NONE")), event -> {
					conditionsPane(player, itemMap);
			}));
			creatingPane.addButton(new Button(fillerPaneGItem));
			creatingPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&b&lRecipe", "&7", "&7*Define the recipe to be", "&7able to craft this item.", "&9Enabled: &a" + (itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty() ? "YES" : "NONE")), event -> {
					recipePane(player, itemMap);
			}));
			creatingPane.addButton(new Button(fillerPaneGItem));
			creatingPane.addButton(new Button(ItemHandler.getItem("137", 1, false, "&c&lNBT Properties", "&7", "&7*Define specific NBT Properties", "&7to be set to the item.", "&9Enabled: &a" + (itemMap.getNBTValues() != null && !itemMap.getNBTValues().isEmpty() ? "YES" : "NONE")), event -> {
					nbtPane(player, itemMap);
			}));
			creatingPane.addButton(new Button(fillerPaneGItem));
			if (itemMap.getMaterial().toString().contains("MAP")) {
				creatingPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&e&lMap Image", "&7", "&7*Adds a custom map image that", "&7will be displayed when held.", "&7", "&7Place the custom map image", 
						"&7in the MAIN ItemJoin folder.", "&7", "&7The map CAN be a GIF but", "&7must be a 128x128 pixel image.", "&9&lImage: &a" + StringUtils.nullCheck(itemMap.getMapImage())), event -> {
					if (StringUtils.nullCheck(itemMap.getMapImage()) != "NONE") {
						itemMap.setMapImage(null);
						creatingPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "MAP IMAGE";
						placeHolders[15] = "minecraft.png OR minecraft-dance.gif";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
					}
				}, event -> {
					itemMap.setMapImage(ChatColor.stripColor(event.getMessage()));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "MAP IMAGE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					creatingPane(event.getPlayer(), itemMap);
				}));
			} if (itemMap.getMaterial().toString().contains("ARROW") && !itemMap.getMaterial().toString().contains("TIPPED_ARROW")) {
				creatingPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, false, "&e&lTeleport", "&7", "&7*Set the arrow to teleport", "&7the player upon landing.", "&9&lEnabled: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()),
						event -> teleportPane(player, itemMap, 0)));
			} else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
				Interface colorPane = new Interface(true, 6, GUIName, player);
				colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> creatingPane(player, itemMap)));
				for (DyeColor color: DyeColor.values()) {
					colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_DYE" : "351:8"), 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your firework charge."), event -> {
						itemMap.setChargeColor(color);
						creatingPane(player, itemMap);
					}));
				}
				creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_DYE" : "351:9"), 1, false, "&e&lCharge Color", "&7", "&7*Set the color of", "&7the firework star.", "&9&lColor: &a" +
				StringUtils.nullCheck(itemMap.getChargeColor() + "")), event -> {
					if (StringUtils.nullCheck(itemMap.getChargeColor() + "") != "NONE") {
						itemMap.setChargeColor(null);
						creatingPane(player, itemMap);
					} else {
						colorPane.open(player);
					}
				}));
			} else if (itemMap.getMaterial().isEdible() || itemMap.getMaterial().toString().equalsIgnoreCase("POTION")) {
				creatingPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the item.", "&9&lPotion-Effects: &a" + StringUtils.nullCheck(potionList)),
						event -> potionPane(player, itemMap, 0)));
			} else if (itemMap.getMaterial().toString().contains("BANNER")) {
				creatingPane.addButton(new Button(ItemHandler.getItem("CLAY_BALL", 1, false, "&e&lBanner Patterns", "&7", "&7*Set custom patterns that", "&7will appear on the item.", "&9&lBanner-Meta: &a" + StringUtils.nullCheck(patternList)),
						event -> bannerPane(player, itemMap)));
			} else if (!ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot") && !itemMap.getMaterial().toString().contains("LEATHER_")) {
				creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, "&a&lAttributes", "&7", "&7*Add a custom attribute to", "&7your armor or weapon.", (StringUtils.nullCheck(attributeList) != "NONE" ? "&9&lAttributes: &a" + attributeList : "")), event -> {
					attributePane(player, itemMap, false);
				}));
			} else if (itemMap.getMaterial().toString().contains("SHULKER")) {
				creatingPane.addButton(new Button(ItemHandler.getItem("229", 1, false, "&a&lContents", "&7", "&7*Add existing custom items into", "&7the contents of this box.", "&9Enabled: &a" + (itemMap.getContents() != null && !itemMap.getContents().isEmpty() ? "YES" : "NONE")), event -> {
					Interface contentsPane = new Interface(true, 6, GUIName, player);
					SchedulerUtils.runAsync(() -> {
						setPage(player, contentsPane, ItemUtilities.getUtilities().copyItems(), itemMap, null, 0);
					});
					contentsPane.open(player);
				}));
			} else {
				creatingPane.addButton(new Button(ItemHandler.getItem("MAGMA_CREAM", 1, false, "&e&lOther Settings", "&7", "&7*Settings that are specific", "&7to the item's material type.", (specialItem(itemMap) ? "" : "&7"),
						(specialItem(itemMap) ? "" : "&c&lERROR: &7A " + itemMap.getMaterial().name() + " &7is NOT"), (specialItem(itemMap) ? "" : "&7a special material type.")), event -> {
					if (specialItem(itemMap)) {
						otherPane(player, itemMap);
					}
				}));
			}
			creatingPane.addButton(new Button(fillerPaneGItem));
			creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return to the main menu.", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> returnConfirm(player, itemMap)));
			creatingPane.addButton(new Button(fillerPaneBItem), 3);
			creatingPane.addButton(new Button(ItemHandler.getItem("WOOL:5", 1, false, "&a&l&nSave to Config", "&7", "&7*Saves the custom item", "&7settings to the items.yml file."), event -> {
				itemMap.saveToConfig();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[3] = itemMap.getConfigName();
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.itemSaved", player, placeHolders);
				ConfigHandler.getConfig().reloadConfigs(true);
				player.closeInventory();
			}));
			creatingPane.addButton(new Button(fillerPaneBItem), 3);
			creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return you to the main menu.", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> returnConfirm(player, itemMap)));
			});
		creatingPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for confirming the return to the main menu.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void returnConfirm(final Player player, final ItemMap itemMap) {
		Interface returnPane = new Interface(false, 1, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			returnPane.addButton(new Button(fillerPaneBItem));
			returnPane.addButton(new Button(ItemHandler.getItem("WOOL:14", 1, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return to the", "&7main menu, all modified", "&7settings will be lost.", "&7", "&c&lWARNING: &cThis item has &lNOT&c been saved!"), event -> startMenu(player)));
			returnPane.addButton(new Button(fillerPaneBItem), 2);
			returnPane.addButton(new Button(ItemHandler.getItem("WOOL:5", 1, false, "&a&l&nSave to Config", "&7", "&7*Saves the custom item", "&7settings to the items.yml file."), event -> {
				itemMap.saveToConfig();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[3] = itemMap.getConfigName();
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.itemSaved", player, placeHolders);
				ConfigHandler.getConfig().reloadConfigs(true);
				startMenu(player);
			}));
			returnPane.addButton(new Button(fillerPaneBItem), 2);
			returnPane.addButton(new Button(ItemHandler.getItem("WOOL:4", 1, false, "&e&l&nModify Settings", "&7", "&7*Continue modifying the", "&7custom item settings."), event -> creatingPane(player, itemMap)));
			returnPane.addButton(new Button(fillerPaneBItem));
		});
		returnPane.open(player);
	}
	
	
// =======================================================================================================================================================================================================================================

//  ============================================== //
//                 Settings Menus      	           //
//  ============================================== //
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    * @param stage - The type of selection Pane.
    */
	private static void numberPane(final Player player, final int stage) {
		Interface numberPane = new Interface((stage == 2 ? false : true), (stage == 2 ? 2 : 6), GUIName, player);
		SchedulerUtils.runAsync(() -> {
			numberPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> {
				if (stage == 1) {
					itemSettings(player);
				} else if (stage == 3) {
					clearPane(player);
				}
			}));
			for (int i = 0; i <= (stage == 1 || stage == 3 ? 64 : stage == 2 ? 8 : 0); i++) {
				final int k = i;
				if (stage == 1) {
					numberPane.addButton(new Button(ItemHandler.getItem((i == 0 ? "STAINED_GLASS_PANE:14" : "STAINED_GLASS_PANE:11"), (i == 0 ? 1 : i), false, "&9&lDelay: &a&l" + i, "&7", 
					"&7*Click to set the trigger", "&7delay for giving custom items.", "&aSecond(s): &e" + (i == 0 ? 0 : ((Double.parseDouble(String.valueOf(i))) / 2))), event -> {
						File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("items-Delay", k); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "items.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
				} else if (stage == 2) {
					numberPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", (i == 0 ? 1 : i), false, "&9&lSlot: &a&l" + i + " &9&l[HOTBAR]", "&7", 
					"&7*Click to set the held item slot", "&7that is automatically selected", "&7when performing a held item trigger.", "&aSlot: &e" + k), event -> {
						File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Settings.HeldItem-Slot", k); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> itemSettings(player));
					}));
				} else if (stage == 3) {
					numberPane.addButton(new Button(ItemHandler.getItem((i == 0 ? "STAINED_GLASS_PANE:14" : "STAINED_GLASS_PANE:11"), (i == 0 ? 1 : i), false, "&9&lDelay: &a&l" + i, "&7", 
					"&7*Click to set the trigger", "&7delay for clearing items.", "&aSecond(s): &e" + (i == 0 ? 0 : (Double.parseDouble(String.valueOf(i))))), event -> {
						File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Delay-Tick", k); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> clearPane(player));
					}));
				} 
			}
			if (stage == 2) {
				numberPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
				numberPane.addButton(new Button(fillerPaneBItem), 7);
				numberPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
			}
		});
		numberPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void languagePane(final Player player) {
		Interface languagePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			final String language = ConfigHandler.getConfig().getFile("config.yml").getString("Language").replace(" ", "");
			languagePane.addButton(new Button(fillerPaneBItem));
			languagePane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2", 1, language.equalsIgnoreCase("ENGLISH"), "&6&l&nEnglish", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lEnglish&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("ENGLISH") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("ENGLISH")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "ENGLISH"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(ItemHandler.getItem("SAND", 1, language.equalsIgnoreCase("SPANISH"), "&6&l&nSpanish", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lSpanish&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("SPANISH") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("SPANISH")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "SPANISH"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(ItemHandler.getItem("80", 1, language.equalsIgnoreCase("RUSSIAN"), "&6&l&nRussian", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lRussian&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("RUSSIAN") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("RUSSIAN")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "RUSSIAN"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(fillerPaneBItem));
			languagePane.addButton(new Button(ItemHandler.getItem("98", 1, language.equalsIgnoreCase("FRENCH"), "&6&l&nFrench", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lFrench&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("FRENCH") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("FRENCH")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "FRENCH"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "1:3" : "5", 1, language.equalsIgnoreCase("TRADITIONALCHINESE"), "&6&l&nTraditional Chinese", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lTraditional Chinese&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("TRADITIONALCHINESE") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("TRADITIONALCHINESE")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "TRADITIONAL CHINESE"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "1:4" : "5:3", 1, language.equalsIgnoreCase("SIMPLIFIEDCHINESE"), "&6&l&nSimplified Chinese", "&7", 
			"&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lSimplified Chinese&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.", 
			"&9&lENABLED: &a" + (language.equalsIgnoreCase("SIMPLIFIEDCHINESE") + "").toUpperCase()), event -> {
				if (!language.equalsIgnoreCase("SIMPLIFIEDCHINESE")) {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Language", "SIMPLIFIED CHINESE"); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> languagePane(player));
				}
			}));
			languagePane.addButton(new Button(fillerPaneBItem));
			languagePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
			languagePane.addButton(new Button(fillerPaneBItem), 7);
			languagePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
		});
		languagePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void databasePane(final Player player) {
		Interface databasePane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			databasePane.addButton(new Button(fillerPaneBItem), 4);
			databasePane.addButton(new Button(ItemHandler.getItem("137", 1, ConfigHandler.getConfig().getFile("config.yml").getBoolean("Database.MySQL"), "&b&l&nMySQL", "&7", 
			"&7*If the plugin should use a", "&7MySQL connection instead of the", "&7local SQLite database inside", "&7the plugin folder.", "&7", "&c&lNote: &7Keep this set to false", "&7if you do not know what", "&7you are doing.", 
			"&7", "&c&l&nWARNING: &7Changing this value requires", "&7a server restart for the", "&7changes to take affect.",  
			"&9&lENABLED: &a" + String.valueOf(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Database.MySQL")).toUpperCase()), 
				event -> {
					File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Database.MySQL", !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Database.MySQL")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(fillerPaneBItem), 4);
			databasePane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&a&lHost", "&7", 
			"&7*Set the &c&lHost &7for", "&7the MySQL databse connection.", "&9&lHOST: &a" + ConfigHandler.getConfig().getFile("config.yml").getString("Database.host")), 
				event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "HOST ADDRESS";
					placeHolders[15] = "localhost";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "HOST ADDRESS";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Database.host", ChatColor.stripColor(event.getMessage())); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(fillerPaneBItem));
			databasePane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, "&a&lPort", "&7", 
			"&7*Set the &c&lPort &7for", "&7the MySQL databse connection.", "&9&lPORT: &a" + ConfigHandler.getConfig().getFile("config.yml").getString("Database.port")), 
				event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ADDRESS PORT";
					placeHolders[15] = "3306";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "ADDRESS PORT";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Database.port", Integer.parseInt(ChatColor.stripColor(event.getMessage()))); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
					} else {
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = ChatColor.stripColor(event.getMessage());
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					}
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(fillerPaneBItem));
			databasePane.addButton(new Button(ItemHandler.getItem("17", 1, false, "&a&lTable", "&7", 
			"&7*Set the &c&lTable &7for", "&7the MySQL databse connection.", "&9&lTABLE: &a" + ConfigHandler.getConfig().getFile("config.yml").getString("Database.table")), 
				event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "TABLE NAME";
					placeHolders[15] = "ITEMJOIN_LOCAL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "TABLE NAME";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Database.table", ChatColor.stripColor(event.getMessage())); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(fillerPaneBItem));
			databasePane.addButton(new Button(ItemHandler.getItem("53", 1, false, "&a&lUser", "&7", 
			"&7*Set the &c&lUser &7for", "&7the MySQL databse connection.", "&9&lUSER: &a***********"), 
				event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "USER";
					placeHolders[15] = "rockinchaos";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "USER";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Database.user", ChatColor.stripColor(event.getMessage())); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(fillerPaneBItem));
			databasePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&a&lPassword", "&7", 
			"&7*Set the &c&lPassword &7for", "&7the MySQL databse connection.", "&9&lPORT: &a****"),  
				event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "PASSWORD";
					placeHolders[15] = "cooldude6";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "PASSWORD";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Database.pass", ChatColor.stripColor(event.getMessage())); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> databasePane(player));
				}));
			databasePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
			databasePane.addButton(new Button(fillerPaneBItem), 7);
			databasePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
		});
		databasePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void triggerPane(final Player player) {
		Interface triggerPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			List<String> triggers = new ArrayList<String>();
			for (String trigger: ConfigHandler.getConfig().getFile("config.yml").getString("Settings.HeldItem-Triggers").replace(" ", "").split(",")) {
				if (trigger != null && !trigger.isEmpty()) {
					triggers.add(trigger);
				}
			}
			triggerPane.addButton(new Button(fillerPaneBItem));
			triggerPane.addButton(new Button(ItemHandler.getItem("323", 1, StringUtils.containsValue(triggers, "JOIN"), "&e&l&nJoin", "&7", 
					"&7*Sets the held item slot", "&7upon joinning the server.", 
			"&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "JOIN") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "JOIN")) {
					triggers.remove("JOIN");
				} else {
					triggers.add("JOIN");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(triggers, "RESPAWN"), "&e&l&nRespawn", "&7", "&7*Sets the held item slot", "&7upon player respawning.", "&9&lENABLED: &a" + 
			(StringUtils.containsValue(triggers, "RESPAWN") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "RESPAWN")) {
					triggers.remove("RESPAWN");
				} else {
					triggers.add("RESPAWN");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, StringUtils.containsValue(triggers, "WORLD-SWITCH"), "&e&l&nWorld Switch", "&7", "&7*Sets the held item slot", "&7upon player switching worlds.", 
			"&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "WORLD-SWITCH") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "WORLD-SWITCH")) {
					triggers.remove("WORLD-SWITCH");
				} else {
					triggers.add("WORLD-SWITCH");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(fillerPaneBItem));
			triggerPane.addButton(new Button(ItemHandler.getItem("LEVER", 1, StringUtils.containsValue(triggers, "GAMEMODE-SWITCH"), "&e&l&nGamemode Switch", "&7", "&7*Sets the held item slot", 
				"&7when the player changes", "&7to a different gamemode.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "GAMEMODE-SWITCH") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "GAMEMODE-SWITCH")) {
					triggers.remove("GAMEMODE-SWITCH");
				} else {
					triggers.add("GAMEMODE-SWITCH");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, StringUtils.containsValue(triggers, "REGION-ENTER"), "&e&l&nRegion Enter", "&7", "&7*Sets the held item slot when", 
			"&7the player enters a WorldGuard region.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "REGION-ENTER") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "REGION-ENTER")) {
					triggers.remove("REGION-ENTER");
				} else {
					triggers.add("REGION-ENTER");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("408", 1, StringUtils.containsValue(triggers, "REGION-LEAVE"), "&e&l&nRegion Leave", "&7", "&7*Sets the held item slot when", 
			"&7the player leaves a WorldGuard region.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "REGION-LEAVE") + "").toUpperCase()), event -> {
				if (StringUtils.containsValue(triggers, "REGION-LEAVE")) {
					triggers.remove("REGION-LEAVE");
				} else {
					triggers.add("REGION-LEAVE");
				}
				if (triggers.isEmpty()) { 
					triggers.add("DISABLED"); 
				} else if (StringUtils.containsValue(triggers, "DISABLED")) {
					triggers.remove("DISABLED");
				} else if (StringUtils.containsValue(triggers, "DISABLE")) {
					triggers.remove("DISABLE");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Settings.HeldItem-Triggers", triggers.toString().replace("[", "").replace("]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> triggerPane(player));
			}));
			triggerPane.addButton(new Button(fillerPaneBItem));
			triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
			triggerPane.addButton(new Button(fillerPaneBItem), 7);
			triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
		});
		triggerPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void preventPane(final Player player) {
		Interface preventPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			List<String> bypassList = new ArrayList<String>();
			for (String bypass: ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Bypass").replace(" ", "").split(",")) {
				if (bypass != null && !bypass.isEmpty()) {
					bypassList.add(bypass);
				}
			}
			preventPane.addButton(new Button(fillerPaneBItem), 3);
			preventPane.addButton(new Button(ItemHandler.getItem(StringUtils.containsValue(bypassList, "CREATIVE") ? "322:1" : "322", 1, StringUtils.containsValue(bypassList, "CREATIVE"), 
					"&bCreative Bypass", "&7", "&7*Players in creative mode", "&7will ignore the prevent actions.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(bypassList, "CREATIVE") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(bypassList, "CREATIVE")) {
							bypassList.remove("CREATIVE");
						} else {
							bypassList.add("CREATIVE");
						}
						if (bypassList.isEmpty()) {
							bypassList.add("DISABLED");
						} else if (StringUtils.containsValue(bypassList, "DISABLED")) {
							bypassList.remove("DISABLED");
						} else if (StringUtils.containsValue(bypassList, "DISABLE")) {
							bypassList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Prevent.Bypass", bypassList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> preventPane(player));
					}));
			preventPane.addButton(new Button(fillerPaneBItem));
			preventPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(bypassList, "OP"), 
					"&bOP Bypass", "&7", "&7*Players that are OP", "&7will ignore the prevent actions.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(bypassList, "OP") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(bypassList, "OP")) {
							bypassList.remove("OP");
						} else {
							bypassList.add("OP");
						}
						if (bypassList.isEmpty()) {
							bypassList.add("DISABLED");
						} else if (StringUtils.containsValue(bypassList, "DISABLED")) {
							bypassList.remove("DISABLED");
						} else if (StringUtils.containsValue(bypassList, "DISABLE")) {
							bypassList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Prevent.Bypass", bypassList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> preventPane(player));
					}));
			preventPane.addButton(new Button(fillerPaneBItem), 4);
			preventPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, 
					"&c&l&nPrevent Pickups", "&7", "&7*Prevent players from picking up", "&7ANY items, not just custom items.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Pickups"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Pickups") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Prevent.Pickups")));
			preventPane.addButton(new Button(fillerPaneBItem));
			preventPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, false, 
					"&c&l&nPrevent Movement", "&7", "&7*Prevent players from moving", "&7ANY items around in their", "&7inventory, not just custom items.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.itemMovement"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.itemMovement") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Prevent.itemMovement")));
			preventPane.addButton(new Button(fillerPaneBItem));
			preventPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, 
					"&c&l&nPrevent Drops", "&7", "&7*Prevent players from dropping", "&7ANY items, not just custom items.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Self-Drops"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Self-Drops") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Prevent.Self-Drops")));
			preventPane.addButton(new Button(fillerPaneBItem));
			preventPane.addButton(new Button(ItemHandler.getItem("BONE", 1, false, 
					"&c&l&nPrevent Death Drops", "&7", "&7*Prevent players from dropping", "&7ANY items on death, not just custom items.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Death-Drops"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Prevent.Death-Drops") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Prevent.Death-Drops")));
			preventPane.addButton(new Button(fillerPaneBItem));
			preventPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
			preventPane.addButton(new Button(fillerPaneBItem), 7);
			preventPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
		});
		preventPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void clearPane(final Player player) {
		Interface clearPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			clearPane.addButton(new Button(fillerPaneBItem), 3);
			clearPane.addButton(new Button(ItemHandler.getItem("2", 1, ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ALL"), 
					"&bType: &a&lALL", "&7", "&7*ALL items including other plugin(s)", "&7and vanilla items should be cleared", "&7upon performing a trigger.", 
					"&9&lENABLED: &a" + (ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ALL") + "").toUpperCase()), 
					event -> {
						if (!ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ALL")) {
							File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
							FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
							dataFile.set("Clear-Items.Type", "ALL"); 	
							ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
							ConfigHandler.getConfig().softReload();
							SchedulerUtils.runLater(2L, () -> clearPane(player));
						}
					}));
			clearPane.addButton(new Button(ItemHandler.getItem("347", 1, false, 
					"&b&lClear Delay", "&7", "&7*The number of second(s)", "&7to wait before clearing", "&7items from the player inventory.", 
					"&9&lDelay: &a" + (ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Delay-Tick") + "").toUpperCase()), 
					event -> numberPane(player, 3)));
			clearPane.addButton(new Button(ItemHandler.getItem("33", 1, ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ITEMJOIN"), 
					"&bType: &a&lITEMJOIN", "&7", "&7*Only ItemJoin (custom items)", "&7should be cleared upon", "&7performing a trigger.", 
					"&9&lENABLED: &a" + (ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ITEMJOIN") + "").toUpperCase()), 
					event -> {
						if (!ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ITEMJOIN")) {
							File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
							FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
							dataFile.set("Clear-Items.Type", "ITEMJOIN"); 	
							ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
							ConfigHandler.getConfig().softReload();
							SchedulerUtils.runLater(2L, () -> clearPane(player));
						}
					}));
			clearPane.addButton(new Button(fillerPaneBItem), 3);
			clearPane.addButton(new Button(ItemHandler.getItem("323", 1, false, 
					"&c&l&nJoin", "&7", "&7*Clears the items from the", "&7player upon joining the server.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Join"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Join") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Clear-Items.Join")));
			clearPane.addButton(new Button(fillerPaneBItem));
			clearPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, 
					"&c&l&nWorld-Switch", "&7", "&7*Clears the items from the", "&7player upon changing worlds.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.World-Switch"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.World-Switch") : "FALSE")).toUpperCase()), 
					event -> worldPane(player, "Clear-Items.World-Switch")));
			clearPane.addButton(new Button(fillerPaneBItem));
			clearPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, false, 
					"&c&l&nRegion-Enter", "&7", "&7*Clears the items from the", "&7player upon entering", "&7a WorldGuard region.", 
					(DependAPI.getDepends(false).getGuard().guardEnabled() ? "&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Region-Enter"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Region-Enter") : "FALSE")).toUpperCase() : ""), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7"), 
					(DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), 
					event -> {
						if (DependAPI.getDepends(false).getGuard().guardEnabled()) {
							worldPane(player, "Clear-Items.Region-Enter");
						}
					}));
			clearPane.addButton(new Button(fillerPaneBItem));
			clearPane.addButton(new Button(ItemHandler.getItem("137", 1, false, 
					"&b&lOptions", "&7", "&7*Actions to apply to", "&7the clear items triggers", "&7such as OP bypass.", 
					"&9&lENABLED: &a" + ((!StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Options"), "DISABLE") ? 
					ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Options") : "FALSE")).toUpperCase()), 
					event -> optionPane(player)));
			clearPane.addButton(new Button(fillerPaneBItem));
			clearPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, 
					"&b&lBlackList", "&7", "&7*Materials, Slots, or Item Names", "&7to be blacklisted from being", "&7cleared upon performing a trigger.", 
					"&9&lENABLED: &a" + (!ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Blacklist").isEmpty() + "").toUpperCase()), 
					event -> blacklistPane(player)));
			clearPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
			clearPane.addButton(new Button(fillerPaneBItem), 7);
			clearPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
		});
		clearPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void blacklistPane(final Player player) {
		Interface blacklistPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			String[] blacklist = ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Blacklist").split(",");
			List<String> materials = new ArrayList<String>();
			List<String> slots = new ArrayList<String>();
			List<String> names = new ArrayList<String>();
			try {
				if (blacklist != null) {
					for (String value: blacklist) {
						String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
						String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
						if (valType.equalsIgnoreCase("id") && ItemHandler.getMaterial(inputResult.trim(), null) != null) {
							materials.add(inputResult.trim().toUpperCase());
						} else if (valType.equalsIgnoreCase("slot")) {
							slots.add(inputResult.trim().toUpperCase());
						} else if (valType.equalsIgnoreCase("name")) {
							names.add(inputResult.trim());
						}
					}
				}
			} catch (Exception e) { }
			blacklistPane.addButton(new Button(fillerPaneBItem), 3);
			blacklistPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, 
					"&b&l&nMaterials", "&7", "&7*The material to be blacklisted", "&7from being cleared.", "&7",
					"&9&lMaterials: &a" + ((!materials.isEmpty() ? StringUtils.replaceLast(materials.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))), 
					event -> blacklistMatPane(player)));
			blacklistPane.addButton(new Button(ItemHandler.getItem("20", 1, false, 
					"&b&l&nSlots", "&7", "&7*The inventory slots to be", "&7blacklisted from being cleared.", "&7",
					"&9&lSlots: &a" + ((!slots.isEmpty() ? StringUtils.replaceLast(slots.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))), 
					event -> blacklistSlotPane(player)));
			blacklistPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, 
					"&b&l&nNames", "&7", "&7*The items display names to", "&7be blacklisted from being cleared.", "&7",
					"&9&lNames: &a" + ((!names.isEmpty() ? StringUtils.replaceLast(names.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))), 
					event -> blacklistNamePane(player)));
			blacklistPane.addButton(new Button(fillerPaneBItem), 3);
			blacklistPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
			blacklistPane.addButton(new Button(fillerPaneBItem), 7);
			blacklistPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
		});
		blacklistPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void blacklistMatPane(final Player player) {
		Interface materialPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
			String[] blacklist = ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Blacklist").split(",");
			List<String> materials = new ArrayList<String>();
			List<String> saveList = new ArrayList<String>();
			try {
				if (blacklist != null) {
					for (String value: blacklist) {
						String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
						String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
						if (valType.equalsIgnoreCase("id") && ItemHandler.getMaterial(inputResult.trim(), null) != null) {
							materials.add(inputResult.trim().toUpperCase());
						} else if (!valType.equalsIgnoreCase("id") && !value.isEmpty()) {
							saveList.add(value.trim());
						}
					}
				}
			} catch (Exception e) { }
			materialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "BUKKIT MATERIAL";
				placeHolders[15] = "IRON_SWORD";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != null) {
					if (!StringUtils.containsValue(materials, ChatColor.stripColor(event.getMessage()))) {
						materials.add(ChatColor.stripColor(event.getMessage()).toUpperCase());
					} else {
						materials.remove(ChatColor.stripColor(event.getMessage()).toUpperCase());
					}
					for (String mat : materials) {
						saveList.add("{id:" + mat + "}");
					}
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "BUKKIT MATERIAL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> blacklistMatPane(player));
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noMaterial", player, placeHolders);
					blacklistMatPane(player);
				}
			}));
			Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
					materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, StringUtils.containsValue(materials, material.name()), "", "&7", "&7*Click to set the material.", "&7to be blacklisted from clearing.",
					"&7", (StringUtils.containsValue(materials, material.name()) ? "&a&l&nENABLED: &e&lYES" : "")), event -> {
						if (!StringUtils.containsValue(materials, material.name())) {
							materials.add(material.name());
						} else {
							materials.remove(material.name());
						}
						for (String mat : materials) {
							saveList.add("{id:" + mat + "}");
						}
						File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> blacklistMatPane(player));
					}));
				}
			}
			inventoryCheck.clear();
		});
		materialPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void blacklistSlotPane(final Player player) {
		Interface slotPane = new Interface(true, 6, GUIName, player);
		Interface craftingPane = new Interface(false, 4, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			slotPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
			String[] blacklist = ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Blacklist").split(",");
			List<String> slots = new ArrayList<String>();
			List<String> saveList = new ArrayList<String>();
			try {
				if (blacklist != null) {
					for (String value: blacklist) {
						String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
						String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
						if (valType.equalsIgnoreCase("slot")) {
							slots.add(inputResult.trim().toUpperCase());
						} else if (!value.isEmpty()) {
							saveList.add(value.trim());
						}
					}
				}
			} catch (Exception e) { }
			craftingPane.addButton(new Button(fillerPaneGItem), 3);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, StringUtils.containsValue(slots, "CRAFTING[1]"), "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CRAFTING[1]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CRAFTING[1]")) {
					slots.add("CRAFTING[1]");
				} else {
					slots.remove("CRAFTING[1]");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			craftingPane.addButton(new Button(fillerPaneGItem));
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 2, StringUtils.containsValue(slots, "CRAFTING[2]"), "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CRAFTING[2]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CRAFTING[2]")) {
					slots.add("CRAFTING[2]");
				} else {
					slots.remove("CRAFTING[2]");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 10);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, StringUtils.containsValue(slots, "CRAFTING[0]"), "&9&lSlot: &7&lCRAFTING&a&l[0]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CRAFTING[0]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CRAFTING[0]")) {
					slots.add("CRAFTING[0]");
				} else {
					slots.remove("CRAFTING[0]");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 4);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 3, StringUtils.containsValue(slots, "CRAFTING[3]"), "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CRAFTING[3]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CRAFTING[3]")) {
					slots.add("CRAFTING[3]");
				} else {
					slots.remove("CRAFTING[3]");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			craftingPane.addButton(new Button(fillerPaneGItem));
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 4, StringUtils.containsValue(slots, "CRAFTING[4]"), "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CRAFTING[4]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CRAFTING[4]")) {
					slots.add("CRAFTING[4]");
				} else {
					slots.remove("CRAFTING[4]");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 3);
			craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot blacklist menu"), event -> blacklistSlotPane(player)));
			craftingPane.addButton(new Button(fillerPaneBItem), 7);
			craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot blacklist menu"), event -> blacklistSlotPane(player)));
			slotPane.addButton(new Button(fillerPaneGItem));
			slotPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots"), event -> craftingPane.open(player)));
			slotPane.addButton(new Button(fillerPaneGItem));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, StringUtils.containsValue(slots, "HELMET"), "&9&lSlot: &a&lHELMET", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "HELMET")? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "HELMET")) {
					slots.add("HELMET");
				} else {
					slots.remove("HELMET");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, StringUtils.containsValue(slots, "CHESTPLATE"), "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "CHESTPLATE") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "CHESTPLATE")) {
					slots.add("CHESTPLATE");
				} else {
					slots.remove("CHESTPLATE");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, StringUtils.containsValue(slots, "LEGGINGS"), "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
				(StringUtils.containsValue(slots, "LEGGINGS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "LEGGINGS")) {
					slots.add("LEGGINGS");
				} else {
					slots.remove("LEGGINGS");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, StringUtils.containsValue(slots, "BOOTS"), "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.", 
				(StringUtils.containsValue(slots, "BOOTS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (!StringUtils.containsValue(slots, "BOOTS")) {
					slots.add("BOOTS");
				} else {
					slots.remove("BOOTS");
				}
				for (String slot : slots) {
					saveList.add("{slot:" + slot + "}");
				}
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
			}));
			if (ServerUtils.hasSpecificUpdate("1_9")) {
				slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, StringUtils.containsValue(slots, "OFFHAND"), "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to prevent this slot", "&7from having its item cleared.",
					(StringUtils.containsValue(slots, "OFFHAND") ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (!StringUtils.containsValue(slots, "OFFHAND")) {
						slots.add("OFFHAND");
					} else {
						slots.remove("OFFHAND");
					}
					for (String slot : slots) {
						saveList.add("{slot:" + slot + "}");
					}
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
				}));
			} else {
				slotPane.addButton(new Button(fillerPaneGItem));
			}
			slotPane.addButton(new Button(fillerPaneGItem));
			for (int i = 9; i < 36; i++) {
				final int slot = i;
				slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", i, (StringUtils.containsValue(slots, String.valueOf(slot))), "&9&lSlot: &a&l" + i, "&7", "&7*Click to prevent this slot", 
					"&7from having its item cleared.", (StringUtils.containsValue(slots, String.valueOf(slot)) ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (!StringUtils.containsValue(slots, String.valueOf(slot))) {
						slots.add(String.valueOf(slot));
					} else {
						slots.remove(String.valueOf(slot));
					}
					for (String slotS : slots) {
						saveList.add("{slot:" + slotS + "}");
					}
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
				}));
			}
			for (int j = 0; j < 9; j++) {
				final int slot = j;
				int count = j;
				if (slot == 0) {
					count = 1;
				}
				slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", count, (StringUtils.containsValue(slots, String.valueOf(slot))), "&9&lSlot: &a&l" + j, "&7", "&7*Click to prevent this slot", 
					"&7from having its item cleared.", (StringUtils.containsValue(slots, String.valueOf(slot)) ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (!StringUtils.containsValue(slots, String.valueOf(slot))) {
						slots.add(String.valueOf(slot));
					} else {
						slots.remove(String.valueOf(slot));
					}
					for (String slotS : slots) {
						saveList.add("{slot:" + slotS + "}");
					}
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
				}));
			}
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
			slotPane.addButton(new Button(fillerPaneBItem), 7);
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
		});
		slotPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void blacklistNamePane(final Player player) {
		Interface namePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			namePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
			String[] blacklist = ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Blacklist").split(",");
			List<String> names = new ArrayList<String>();
			List<String> saveList = new ArrayList<String>();
			try {
				if (blacklist != null) {
					for (String value: blacklist) {
						String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
						String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
						if (valType.equalsIgnoreCase("name")) {
							names.add(inputResult.trim().toUpperCase());
						} else if (!value.isEmpty()) {
							saveList.add(value.trim());
						}
					}
				}
			} catch (Exception e) { }
			namePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&b&lAdd Name", "&7", "&7*Add an items display", "&7name to be blacklisted", "&7simply click and type it.", "&7", 
				"&c&l&nNOTE:&7 Do NOT include any", "&7color codes as these are excluded."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ITEM NAME";
				placeHolders[15] = "Ultra Item";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (!StringUtils.containsValue(names, ChatColor.stripColor(event.getMessage()))) {
					names.add(ChatColor.stripColor(event.getMessage()));
				} else {
					names.remove(ChatColor.stripColor(event.getMessage()));
				}
				for (String name : names) {
					saveList.add("{name:" + name + "}");
				}
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ITEM NAME";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
				FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
				dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
				ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
				ConfigHandler.getConfig().softReload();
				SchedulerUtils.runLater(2L, () -> blacklistNamePane(player));
			}));
			for (String itemName : names) {
				namePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&f" + itemName, "&7", "&7*Click to remove this item name", "&7from the clearing blacklist.", "&7", 
					"&c&l&nNOTE:&7 Do NOT include any", "&7color codes as these are excluded."), event -> {
					names.remove(itemName);
					for (String name : names) {
						saveList.add("{name:" + name + "}");
					}
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", "")); 	
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> blacklistNamePane(player));
				}));
			}
		});
		namePane.open(player);
	}

   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void optionPane(final Player player) {
		Interface optionPane = new Interface(false, 2, GUIName, player);
		List<String> optionList = new ArrayList<String>();
		for (String option: ConfigHandler.getConfig().getFile("config.yml").getString("Clear-Items.Options").replace(" ", "").split(",")) {
			if (option != null && !option.isEmpty()) {
				optionList.add(option);
			}
		}
		SchedulerUtils.runAsync(() -> {
			optionPane.addButton(new Button(ItemHandler.getItem("DIAMOND_CHESTPLATE", 1, StringUtils.containsValue(optionList, "PROTECT"), 
					"&e&lProtect", "&7", "&7*Prevents ALL players from", "&7having their first-join and", "&7first-world items cleared.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "PROTECT") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(optionList, "PROTECT")) {
							optionList.remove("PROTECT");
						} else {
							optionList.add("PROTECT");
						}
						if (optionList.isEmpty()) {
							optionList.add("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLED")) {
							optionList.remove("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLE")) {
							optionList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> optionPane(player));
					}));
			optionPane.addButton(new Button(fillerPaneBItem));
			optionPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(optionList, "PROTECT_OP"), 
					"&e&lProtect OP", "&7", "&7*Prevents OP players from", "&7having their first-join and", "&7first-world items cleared.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "PROTECT_OP") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(optionList, "PROTECT_OP")) {
							optionList.remove("PROTECT_OP");
						} else {
							optionList.add("PROTECT_OP");
						}
						if (optionList.isEmpty()) {
							optionList.add("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLED")) {
							optionList.remove("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLE")) {
							optionList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> optionPane(player));
					}));
			optionPane.addButton(new Button(fillerPaneBItem));
			optionPane.addButton(new Button(ItemHandler.getItem("322", 1, StringUtils.containsValue(optionList, "PROTECT_CREATIVE"), 
					"&e&lProtect Creative", "&7", "&7*Prevents players in creative mode", "&7from having their first-join", "&7and first-world items cleared.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "PROTECT_CREATIVE") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(optionList, "PROTECT_CREATIVE")) {
							optionList.remove("PROTECT_CREATIVE");
						} else {
							optionList.add("PROTECT_CREATIVE");
						}
						if (optionList.isEmpty()) {
							optionList.add("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLED")) {
							optionList.remove("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLE")) {
							optionList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> optionPane(player));
					}));
			optionPane.addButton(new Button(fillerPaneBItem));
			optionPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, (StringUtils.containsValue(optionList, "RETURN") && DependAPI.getDepends(false).getGuard().guardEnabled()), 
					"&e&lReturn Regions", "&7*Returns the cleared player inventory", "&7when exiting a cleared region", "&7or entering a region which is", "&7not listed as clearable.", (DependAPI.getDepends(false).getGuard().guardEnabled() ? 
					"&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "RETURN") ? "true" : "false") : ""), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7"), 
					(DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (DependAPI.getDepends(false).getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), 
					event -> {
						if (DependAPI.getDepends(false).getGuard().guardEnabled()) {
							if (StringUtils.containsValue(optionList, "RETURN")) {
								optionList.remove("RETURN");
							} else {
								optionList.add("RETURN");
							}
							if (optionList.isEmpty()) {
								optionList.add("DISABLED");
							} else if (StringUtils.containsValue(optionList, "DISABLED")) {
								optionList.remove("DISABLED");
							} else if (StringUtils.containsValue(optionList, "DISABLE")) {
								optionList.remove("DISABLE");
							}
							File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
							FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
							dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", "")); 	
							ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
							ConfigHandler.getConfig().softReload();
							SchedulerUtils.runLater(2L, () -> optionPane(player));
						}
					}));
			optionPane.addButton(new Button(fillerPaneBItem));
			optionPane.addButton(new Button(ItemHandler.getItem("2", 1, StringUtils.containsValue(optionList, "RETURN_SWITCH"), 
					"&e&lReturn Worlds", "&7", "&7*Returns the prior cleared", "&7player inventory from the", "&7prior world upon returning", "&7to that prior world.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "RETURN_SWITCH") + "").toUpperCase()), 
					event -> {
						if (StringUtils.containsValue(optionList, "RETURN_SWITCH")) {
							optionList.remove("RETURN_SWITCH");
						} else {
							optionList.add("RETURN_SWITCH");
						}
						if (optionList.isEmpty()) {
							optionList.add("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLED")) {
							optionList.remove("DISABLED");
						} else if (StringUtils.containsValue(optionList, "DISABLE")) {
							optionList.remove("DISABLE");
						}
						File fileFolder = new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
						FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
						dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", "")); 	
						ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
						ConfigHandler.getConfig().softReload();
						SchedulerUtils.runLater(2L, () -> optionPane(player));
					}));
			optionPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
			optionPane.addButton(new Button(fillerPaneBItem), 7);
			optionPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
		});
		optionPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    * @param section - The world section type.
    */
	private static void worldPane(final Player player, final String section) {
		Interface preventPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			preventPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the prevent menu."), event -> { 
				if (section.contains("Prevent")) {
					preventPane(player);
				} else {
					clearPane(player);	
				}
			}));
			List < String > enabledWorlds = new ArrayList < String > ();
			String[] enabledParts = ConfigHandler.getConfig().getFile("config.yml").getString(section).replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
			for (String enabledWorld : enabledParts) {
				if (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL")) {
					enabledWorlds.add("ALL");
				} else {
					for (World world: Bukkit.getServer().getWorlds()) {
						if (enabledWorld.equalsIgnoreCase(world.getName())) {
							enabledWorlds.add(world.getName());
						}
					}
				}
			}
		    if (enabledWorlds.isEmpty() && ConfigHandler.getConfig().getFile("config.yml").getBoolean(section)) { enabledWorlds.add("ALL"); }
			preventPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, StringUtils.containsValue(enabledWorlds, "ALL"), "&a&l&nGLOBAL", "&7", "&7*Click to enable &lALL WORLDS.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, "ALL") + "").toUpperCase()), event -> {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					if (StringUtils.containsValue(enabledWorlds, "ALL")) {
						dataFile.set(section, false); 
					} else {
						dataFile.set(section, true); 
					}
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> worldPane(player, section));
			}));
			for (World world: Bukkit.getServer().getWorlds()) {
				String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
				if (world.getEnvironment().equals(Environment.NETHER)) {
					worldMaterial = "NETHERRACK";
				} else if (world.getEnvironment().equals(Environment.THE_END)) {
					worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
				}
				preventPane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, StringUtils.containsValue(enabledWorlds, world.getName()), "&f&l" + world.getName(), "&7", 
						"&7*Click to enable this world.", "&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, world.getName()) + "").toUpperCase()), event -> {
					if (StringUtils.containsValue(enabledWorlds, world.getName())) {
						enabledWorlds.remove(world.getName());
					} else {
						enabledWorlds.add(world.getName());
					}
					if (!enabledWorlds.isEmpty() && enabledWorlds.size() > 1) {
						if (StringUtils.containsValue(enabledWorlds, "ALL")) {
							enabledWorlds.remove("ALL");
						} else if (StringUtils.containsValue(enabledWorlds, "GLOBAL")) {
							enabledWorlds.remove("GLOBAL");
						}
					}
					String worldList = "";
					for (String worldName : enabledWorlds) { worldList += worldName + ", "; }
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "config.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					if (enabledWorlds.isEmpty()) {
						dataFile.set(section, false); 
					} else {
						dataFile.set(section, worldList.substring(0, worldList.length() - 2)); 
					}
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "config.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> worldPane(player, section));
				}));
			}
		});
		preventPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * 
    * @param player - The Player to have the Pane opened.
    */
	private static void overwritePane(final Player player) {
		Interface overwritePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			overwritePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the configuration menu."), event -> itemSettings(player)));
			List < String > enabledWorlds = new ArrayList < String > ();
			String[] enabledParts = ConfigHandler.getConfig().getFile("items.yml").getString("items-Overwrite").replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
			for (String enabledWorld : enabledParts) {
				if (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL")) {
					enabledWorlds.add("ALL");
				} else {
					for (World world: Bukkit.getServer().getWorlds()) {
						if (enabledWorld.equalsIgnoreCase(world.getName())) {
							enabledWorlds.add(world.getName());
						}
					}
				}
			}
		    if (enabledWorlds.isEmpty() && ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Overwrite")) { enabledWorlds.add("ALL"); }
			overwritePane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, StringUtils.containsValue(enabledWorlds, "ALL"), "&a&l&nGLOBAL", "&7", "&7*Click to enable item", "&7overwriting in &lALL WORLDS.", 
					"&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, "ALL") + "").toUpperCase()), event -> {
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					if (StringUtils.containsValue(enabledWorlds, "ALL")) {
						dataFile.set("items-Overwrite", false); 
					} else {
						dataFile.set("items-Overwrite", true); 
					}
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "items.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> overwritePane(player));
			}));
			for (World world: Bukkit.getServer().getWorlds()) {
				String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
				if (world.getEnvironment().equals(Environment.NETHER)) {
					worldMaterial = "NETHERRACK";
				} else if (world.getEnvironment().equals(Environment.THE_END)) {
					worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
				}
				overwritePane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, StringUtils.containsValue(enabledWorlds, world.getName()), "&f&l" + world.getName(), "&7", 
						"&7*Click to enable item", "&7overwriting in this world.", "&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, world.getName()) + "").toUpperCase()), event -> {
					if (StringUtils.containsValue(enabledWorlds, world.getName())) {
						enabledWorlds.remove(world.getName());
					} else {
						enabledWorlds.add(world.getName());
					}
					if (!enabledWorlds.isEmpty() && enabledWorlds.size() > 1) {
						if (StringUtils.containsValue(enabledWorlds, "ALL")) {
							enabledWorlds.remove("ALL");
						} else if (StringUtils.containsValue(enabledWorlds, "GLOBAL")) {
							enabledWorlds.remove("GLOBAL");
						}
					}
					String worldList = "";
					for (String worldName : enabledWorlds) { worldList += worldName + ", "; }
					File fileFolder =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
					FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
					if (enabledWorlds.isEmpty()) {
						dataFile.set("items-Overwrite", false); 
					} else {
						dataFile.set("items-Overwrite", worldList.substring(0, worldList.length() - 2)); 
					}
					ConfigHandler.getConfig().saveFile(dataFile, fileFolder, "items.yml");
					ConfigHandler.getConfig().softReload();
					SchedulerUtils.runLater(2L, () -> overwritePane(player));
				}));
			}
		});
		overwritePane.open(player);
	}
	
// =======================================================================================================================================================================================================================================

//  ============================================== //
//             Item Definition Menus      	       //
//  ============================================== //
	
   /**
    * Checks if the ItemStack is a safe Material,
    * that it actually exists and is not AIR or NULL.
    * 
    * @param item - The ItemStack to be checked.
    * @param inventoryCheck - The Inventory used for checking the ItemStack.
    */
	private static boolean safeMaterial(ItemStack item, Inventory inventoryCheck) {
		inventoryCheck.setItem(0, item);
		if (inventoryCheck.getItem(0) != null && inventoryCheck.getItem(0).getType().name() != "AIR") {
			return true;
		}
		return false;
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items Material.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param stage - The stage of the modification.
    */
	private static void materialPane(final Player player, final ItemMap itemMap, final int stage, final int position) {
		Interface materialPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (stage != 0 && stage != 2 && stage != 3) {
				materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
					creatingPane(player, itemMap);
				}));
			} else if (stage == 2) {
				materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the commands menu."), event -> {
					commandPane(player, itemMap);
				}));
			} else if (stage == 3) {
				materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> {
					recipePane(player, itemMap);
				}));
			}
			materialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				if (stage == 2) {
					placeHolders[16] = "ITEM COST";
				} else {
					placeHolders[16] = "BUKKIT MATERIAL";
				}
				placeHolders[15] = "IRON_SWORD";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != null) {
					if (stage == 2) {
						itemMap.setItemCost(ChatColor.stripColor(event.getMessage()).toUpperCase());
					} else if (stage != 3) { 
						itemMap.setMaterial(ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null)); 
						if (!ServerUtils.hasSpecificUpdate("1_13") && ChatColor.stripColor(event.getMessage()).contains(":")) {
							String[] dataValue = ChatColor.stripColor(event.getMessage()).split(":");
							if (StringUtils.isInt(dataValue[1])) {
								itemMap.setDataValue((short)Integer.parseInt(dataValue[1]));
							}	
						}
					}
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					if (stage == 2) {
						placeHolders[16] = "ITEM COST";
					} else {
						placeHolders[16] = "BUKKIT MATERIAL";
					}
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					if (stage == 3) {
						setIngredients(event.getPlayer(), itemMap, ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null).name(), position);
					} else if (stage == 2) {
						commandPane(event.getPlayer(), itemMap);
					} else {
						creatingPane(event.getPlayer(), itemMap);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noMaterial", player, placeHolders);
					materialPane(player, itemMap, stage, position);
				}
			}));
			Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
			for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
					if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
						for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
							if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
								final int dataValue = i;
								materialPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, false, "", "&7", "&7*Click to set the material."), event -> {
									if (stage == 2) {
										itemMap.setItemCost(material.toString());
									} else if (stage != 3) { 
										itemMap.setMaterial(material); 
										if (dataValue != 0) { itemMap.setDataValue((short)dataValue); }
									}
									if (stage == 0) {
										switchPane(player, itemMap, 0);
									} else if (stage == 2) {
										commandPane(player, itemMap);
									} else if (stage == 3) {
										setIngredients(player, itemMap, material.name(), position);
									} else {
										creatingPane(player, itemMap);
									}
								}));
							}
						}
					} else {
					materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, "", "&7", "&7*Click to set the material."), event -> {
						if (stage == 2) {
							itemMap.setItemCost(material.toString());
						} else if (stage != 3) { itemMap.setMaterial(material); }
						if (stage == 0) {
							switchPane(player, itemMap, 0);
						} else if (stage == 2) { 
							commandPane(player, itemMap);
						} else if (stage == 3) {
							setIngredients(player, itemMap, material.name(), position);
						} else {
							creatingPane(player, itemMap);
						}
					}));
					}
				}
			}
			inventoryCheck.clear();
		});
		materialPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting single or multiple slots for an item.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param stage - The stage in the modification.
    */
	private static void switchPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface switchPane = new Interface(false, 1, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (stage == 0) {
				switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> materialPane(player, itemMap, 0, 0)));
			} else { 
				switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			}
			switchPane.addButton(new Button(fillerPaneBItem), 2);
			switchPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, "&a&lSingle Slot", "&7", "&7*Define a single dedicated", "&7 slot for the item."), event -> slotPane(player, itemMap, stage, 0)));
			switchPane.addButton(new Button(fillerPaneBItem), 1);
			switchPane.addButton(new Button(ItemHandler.getItem("23", 1, false, "&b&lMultiple Slots", "&7", "&7*Define multiple slots for the item."), event -> slotPane(player, itemMap, stage, 1)));
			switchPane.addButton(new Button(fillerPaneBItem), 2);
			if (stage == 0) {
				switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> materialPane(player, itemMap, 0, 0)));
			} else { 
				switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			}
		});
		switchPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the an items slot.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param stage - The stage in the modification.
    * @param type - The type of slot being defined, single or multiple.
    */
	private static void slotPane(final Player player, final ItemMap itemMap, final int stage, final int type) {
		Interface slotPane = new Interface(false, 6, GUIName, player);
		Interface craftingPane = new Interface(false, 4, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			craftingPane.addButton(new Button(fillerPaneGItem), 3);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]") : false), "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to set the custom item", 
					"&7to appear in the &lCRAFTING &7slot &a&l[1]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CRAFTING[1]"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CRAFTING[1]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CRAFTING[1]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					}
				}
			}));
			craftingPane.addButton(new Button(fillerPaneGItem));
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 2, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]") : false), "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to set the custom item", 
					"&7to appear in the &lCRAFTING &7slot &a&l[2]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CRAFTING[2]"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CRAFTING[2]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CRAFTING[2]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					}
				}
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 10);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]") : false), "&9&lSlot: &7&lCRAFTING&a&l[0]", "&7", "&7*Click to set the custom item", 
					"&7to appear in the &lCRAFTING &7slot &a&l[0]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CRAFTING[0]"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CRAFTING[0]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CRAFTING[0]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					}
				}
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 4);
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 3, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]") : false), "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to set the custom item", 
					"&7to appear in the &lCRAFTING &7slot &a&l[3]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CRAFTING[3]"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CRAFTING[3]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CRAFTING[3]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					}
				}
			}));
			craftingPane.addButton(new Button(fillerPaneGItem));
			craftingPane.addButton(new Button(ItemHandler.getItem("58", 4, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]") : false), "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to set the custom item", 
					"&7to appear in the &lCRAFTING &7slot &a&l[4]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CRAFTING[4]"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CRAFTING[4]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CRAFTING[4]"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 2);
					}
				}
			}));
			craftingPane.addButton(new Button(fillerPaneGItem), 3);
			craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
			if (type > 0) {
				craftingPane.addButton(new Button(fillerPaneBItem), 3);
				craftingPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> creatingPane(player, itemMap)));
				craftingPane.addButton(new Button(fillerPaneBItem), 3);
			} else {
				craftingPane.addButton(new Button(fillerPaneBItem), 7);
			}
			craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
			slotPane.addButton(new Button(fillerPaneGItem));
			slotPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY") : false), "&9&lSlot: &a&lARBITRARY", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lArbitrary&7", "&7", "&7*Arbitrary is defined as giving the", "&7item in the first available slot.", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY") ? "&9&lENABLED: &aTRUE" : "")), 
					event -> {
				if (type == 0) { 
					itemMap.setSlot("ARBITRARY"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("ARBITRARY"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("ARBITRARY"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					}
				}
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots"), event -> craftingPane.open(player)));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "HELMET") : false), "&9&lSlot: &a&lHELMET", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lHELMET&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "HELMET") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("HELMET"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "HELMET")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("HELMET"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("HELMET"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					}
				}
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE") : false), "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lCHESTPLATE&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("CHESTPLATE"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("CHESTPLATE"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("CHESTPLATE"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					}
				}
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS") : false), "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lLEGGINGS&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("LEGGINGS"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("LEGGINGS"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("LEGGINGS"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					}
				}
			}));
			slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "BOOTS") : false), "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lBOOTS&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "BOOTS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("BOOTS"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					creatingPane(player, itemMap);
				} else { 
					if (StringUtils.containsValue(itemMap.getMultipleSlots(), "BOOTS")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("BOOTS"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("BOOTS"); 
						itemMap.setMultipleSlots(slots);
						slotPane(player, itemMap, stage, 1);
					}
				}
			}));
			if (ServerUtils.hasSpecificUpdate("1_9")) {
				slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), "OFFHAND") : false), "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to set the custom item", 
						"&7to appear in slot &a&lOFFHAND&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "OFFHAND") ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (type == 0) { 
						itemMap.setSlot("OFFHAND"); 
						itemMap.setMultipleSlots(new ArrayList<String>()); 
						creatingPane(player, itemMap);
					} else { 
						if (StringUtils.containsValue(itemMap.getMultipleSlots(), "OFFHAND")) {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.remove("OFFHAND"); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						} else {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.add("OFFHAND"); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						}
					}
				}));
			} else {
				slotPane.addButton(new Button(fillerPaneGItem));
			}
			slotPane.addButton(new Button(fillerPaneGItem));
			for (int i = 9; i < 36; i++) {
				final int slot = i;
				slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", i, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") : false), "&9&lSlot: &a&l" + i, "&7", "&7*Click to set the custom item", 
						"&7to appear in slot &a&l" + i + "&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (type == 0) { 
						itemMap.setSlot(slot + ""); 
						itemMap.setMultipleSlots(new ArrayList<String>()); 
						creatingPane(player, itemMap);
					} else { 
						if (StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "")) {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.remove(slot + ""); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						} else {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.add(slot + ""); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						}
					}
				}));
			}
			for (int j = 0; j < 9; j++) {
				final int slot = j;
				int count = j;
				if (slot == 0) {
					count = 1;
				}
				slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", count, (type > 0 ? StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") : false), "&9&lSlot: &a&l" + j, "&7", "&7*Click to set the custom item", 
						"&7to appear in slot &a&l" + j + "&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
					if (type == 0) { 
						itemMap.setSlot(slot + ""); 
						itemMap.setMultipleSlots(new ArrayList<String>()); 
						creatingPane(player, itemMap);
					} else { 
						if (StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "")) {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.remove(slot + ""); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						} else {
							List<String> slots = itemMap.getMultipleSlots(); 
							slots.add(slot + ""); 
							itemMap.setMultipleSlots(slots);
							slotPane(player, itemMap, stage, 1);
						}
					}
				}));
			}
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> switchPane(player, itemMap, stage)));
			if (type > 0) {
				slotPane.addButton(new Button(fillerPaneBItem), 3);
				slotPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> creatingPane(player, itemMap)));
				slotPane.addButton(new Button(fillerPaneBItem), 3);
			} else {
				slotPane.addButton(new Button(fillerPaneBItem), 7);
			}
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> switchPane(player, itemMap, stage)));
		});
		if (type == 2) {
			craftingPane.open(player);
		} else {
			slotPane.open(player);
		}
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items stack size.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void countPane(final Player player, final ItemMap itemMap) {
		Interface countPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			countPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				countPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lCount: &a&l" + k, "&7", "&7*Click to set the", "&7count of the item."), event -> {
					itemMap.setCount(k + "");creatingPane(player, itemMap);
				}));
			}
		});
		countPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items data.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void dataPane(final Player player, final ItemMap itemMap) {
		Interface dataPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			dataPane.addButton(new Button(fillerPaneBItem));
			dataPane.addButton(new Button(ItemHandler.setDurability(ItemHandler.getItem("BOW", 1, false, "&b&lDamage", "&7", "&7*Set the damage of the item.", (itemMap.getMaterial().getMaxDurability() != 0 ? "&9&lDURABILITY: &a" + 
			StringUtils.nullCheck(itemMap.getDurability() + "&7") : "&c&lERROR: &7This item is NOT damagable.")), 50), event -> {
				if (StringUtils.nullCheck(itemMap.getDurability() + "&7") != "NONE") {
					itemMap.setDurability(null);
					dataPane(player, itemMap);
				} else if (itemMap.getMaterial().getMaxDurability() != 0) {
					damagePane(player, itemMap);
				}
			}));
			dataPane.addButton(new Button(fillerPaneBItem), 2);
			dataPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, "&a&lCustom Texture", "&7", "&7*Set the custom data of the item.", "&7This is the damage value assigned", "&7to the custom resource texture.", "&9&lDURABILITY DATA: &a" + StringUtils.nullCheck(itemMap.getData() + "&7")), event -> {
				if (StringUtils.nullCheck(itemMap.getData() + "&7") != "NONE") {
					itemMap.setData(null);
					dataPane(player, itemMap);
				} else {
					durabilityDataPane(player, itemMap);
				}
			}));
			dataPane.addButton(new Button(fillerPaneBItem), 2);
			dataPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&e&lCustom Model Data", "&7", "&7*Set the custom model data of the item.", 
					!ServerUtils.hasSpecificUpdate("1_14") ? "&c&l[ERROR] &7This version of Minecraft does" : "", !ServerUtils.hasSpecificUpdate("1_14") ? "&7not support custom model data." : "", 
					!ServerUtils.hasSpecificUpdate("1_14") ? "&7This was implemented in 1.14+." : "", "&9&lTEXTURE DATA: &a" + StringUtils.nullCheck(itemMap.getModelData() + "&7")), event -> {
				if (StringUtils.nullCheck(itemMap.getModelData() + "&7") != "NONE" && ServerUtils.hasSpecificUpdate("1_14")) {
					itemMap.setModelData(null);
					dataPane(player, itemMap);
				} else if (ServerUtils.hasSpecificUpdate("1_14")){
					modelDataPane(player, itemMap);
				}
			}));
			dataPane.addButton(new Button(fillerPaneBItem));
			dataPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
				setTriggers(itemMap);creatingPane(player, itemMap);
			}));
			dataPane.addButton(new Button(fillerPaneBItem), 7);
			dataPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
				setTriggers(itemMap);creatingPane(player, itemMap);
			}));
		});
		dataPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items durability.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durabilityDataPane(final Player player, final ItemMap itemMap) {
		Interface texturePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			texturePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				dataPane(player, itemMap);
			}));
			texturePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Texture", "&7", "&7*Click to set a custom texture", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "DURABILITY DATA";
				placeHolders[15] = "1193";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setData(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "DURABILITY DATA";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				dataPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 2000; i++) {
				final int k = i;
				texturePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", 1, false, "&9&lData: &a&l" + k, "&7", "&7*Click to set the", "&7durability data of the item."), event -> {
					itemMap.setData(k); dataPane(player, itemMap);
				}));
			}
		});
		texturePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for an items model data.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modelDataPane(final Player player, final ItemMap itemMap) {
		Interface texturePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			texturePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				dataPane(player, itemMap);
			}));
			texturePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Model Data", "&7", "&7*Click to set the custom mode data", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "MODEL DATA";
				placeHolders[15] = "1193";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setModelData(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "MODEL DATA";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				dataPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 2000; i++) {
				final int k = i;
				texturePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", 1, false, "&9&lModel Data: &a&l" + k, "&7", "&7*Click to set the", "&7custom model data for the item."), event -> {
					itemMap.setModelData(k); dataPane(player, itemMap);
				}));
			}
		});
		texturePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items damage.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void damagePane(final Player player, final ItemMap itemMap) {
		Interface damagePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			damagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				dataPane(player, itemMap);
			}));
			damagePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Damage", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "DAMAGE";
				placeHolders[15] = "1893";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setDurability((short) Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "DAMAGE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				dataPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= itemMap.getMaterial().getMaxDurability(); i++) {
				final int k = i;
				damagePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", 1, false, "&9&lDamage: &a&l" + k, "&7", "&7*Click to set the", "&7damage of the item."), event -> {
					itemMap.setDurability((short) k); dataPane(player, itemMap);
				}));
			}
		});
		damagePane.open(player);
	}
	
//  ============================================== //
//               ItemCommand Menus        	       //
//  ============================================== //
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items commands.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void commandPane(final Player player, final ItemMap itemMap) {
		Interface commandPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			commandPane.addButton(new Button(fillerPaneGItem), 4);
			commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, "&e&lCommands", "&7", "&7*Click to define the custom command lines", "&7for the item and click type.", 
					"&7", "&9&lCommands: &a" + (itemMap.getCommands().length != 0 ? "YES" : "NONE")), event -> actionPane(player, itemMap)));
			commandPane.addButton(new Button(fillerPaneGItem), 4);
			commandPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&a&lParticle", "&7", "&7*Custom particle(s) that will be", "&7displayed when the commands", "&7are successfully executed.", "&9&lCOMMANDS-PARTICLE: &a" +
					StringUtils.nullCheck(itemMap.getCommandParticle() + "")), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandParticle() + "") != "NONE") {
					itemMap.setCommandParticle(null);
					commandPane(player, itemMap);
				} else {
					particlePane(player, itemMap, 3);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lItem Cost", "&7", "&7*Material that will", "&7be charged upon successfully", "&7executing the commands.", "&9&lCOMMANDS-ITEM: &a" + 
			(StringUtils.nullCheck(itemMap.getItemCost()))), event -> {
				if (StringUtils.nullCheck(itemMap.getItemCost()) != "NONE") {
					itemMap.setItemCost(null);
					commandPane(player, itemMap);
				} else {
					materialPane(player, itemMap, 2, 0);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, "&a&lCost", "&7", "&7*Amount that the player will", "&7be charged upon successfully", "&7executing the commands.", "&9&lCOMMANDS-COST: &a" + 
			(StringUtils.nullCheck(itemMap.getCommandCost() + "&7"))), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandCost() + "&7") != "NONE") {
					itemMap.setCommandCost(0);
					commandPane(player, itemMap);
				} else {
					costPane(player, itemMap);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, "&a&lReceive", "&7", "&7*The number of times the", "&7commands will execute when", "&7receiving the custom item.", 
					"&cNOTE: &7Only functions with", "&7the on-receive command action.", "&9&lCOMMANDS-RECEIVE: &a" + (StringUtils.nullCheck(itemMap.getCommandReceive() + "&7"))), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandReceive() + "&7") != "NONE") {
					itemMap.setCommandReceive(0);
					commandPane(player, itemMap);
				} else {
					receivePane(player, itemMap);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, "&a&lCooldown", "&7", "&7*The time that the commands will", "&7be on cooldown for.", "&7", "&9&lCOMMANDS-COOLDOWN: &a" + StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7") + 
					(StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE" ? "&a second(s)" : "")), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE") {
					itemMap.setCommandCooldown(0);
					commandPane(player, itemMap);
				} else {
					cooldownPane(player, itemMap);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("323", 1, false, "&a&lCooldown Message", "&7", "&7*Optional cooldown message", "&7to be displayed when", "&7the items commands are", 
					"&7on cooldown.", "&9&lCOOLDOWN-MESSAGE: &a" + StringUtils.nullCheck(itemMap.getCooldownMessage())), event -> {
				if (StringUtils.nullCheck(itemMap.getCooldownMessage()) != "NONE") {
					itemMap.setCooldownMessage(null);
					commandPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COOLDOWN MESSAGE";
					placeHolders[15] = "&cThis item is on cooldown for &a%timeleft% &cseconds..";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setCooldownMessage(ChatColor.stripColor(event.getMessage()));String[] placeHolders = LanguageAPI.getLang(false).newString();placeHolders[16] = "COOLDOWN MESSAGE";LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);commandPane(event.getPlayer(), itemMap);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("JUKEBOX", 1, false, "&a&lSound", "&7", "&7*The sound that will be", "&7played after a successful", "&7command execution.", "&9&lCOMMANDS-SOUND: &a" + 
			StringUtils.nullCheck(itemMap.getCommandSound() + "")), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandSound() + "") != "NONE") {
					itemMap.setCommandSound(null);
					commandPane(player, itemMap);
				} else {
					soundPane(player, itemMap, 3);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, "&a&lSequence", "&7", "&7*The order that the command lines", "&7will be executed in.", "&9&lCOMMANDS-SEQUENCE: &a" + 
			StringUtils.nullCheck(itemMap.getCommandSequence() + "")), event -> {
				if (StringUtils.nullCheck(itemMap.getCommandSequence() + "") != "NONE") {
					itemMap.setCommandSequence(null);
					commandPane(player, itemMap);
				} else {
					sequencePane(player, itemMap);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("327", 1, false, "&a&lWarmup", "&7", "&7*The time it will take before", "&7the commands are executed.", "&7Player movement will cancel the", "&7pending commands execution.", "&7", 
					"&9&lCOMMANDS-WARMUP: &a" + StringUtils.nullCheck(itemMap.getWarmDelay() + "&7") + (StringUtils.nullCheck(itemMap.getWarmDelay() + "&7") != "NONE" ? "&a second(s)" : "")), event -> {
				if (StringUtils.nullCheck(itemMap.getWarmDelay() + "&7") != "NONE") {
					itemMap.setWarmDelay(0);
					commandPane(player, itemMap);
				} else {
					warmPane(player, itemMap);
				}
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			commandPane.addButton(new Button(fillerPaneBItem), 7);
			commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		commandPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an action to a command.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void actionPane(final Player player, final ItemMap itemMap) {
		Interface clickPane = new Interface(false, 5, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			clickPane.addButton(new Button(fillerPaneGItem), 2);
			clickPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "324" : "64", 1, false, "&e&lInteract", "&7", "&7*Commands that will execute only", "&7when left and right clicking.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_ALL)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_ALL);
			}));
			clickPane.addButton(new Button(fillerPaneGItem));
			clickPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&e&lInventory", "&7", "&7*Commands that will execute only", "&7when cursor clicking the item", "&7with the players inventory open.", 
					"&7", "&9&lCommands: &a" + listCommands(itemMap, Action.INVENTORY_ALL)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_ALL);
			}));
			clickPane.addButton(new Button(fillerPaneGItem));
			clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, "&e&lPhysical", "&7", "&7*Commands that will execute", "&7when held in the player hand", "&7and they interact with a object", "&7such as a pressure plate.", "&7", 
					"&9&lCommands: &a" + listCommands(itemMap, Action.PHYSICAL)), event -> {
				commandListPane(player, itemMap, Action.PHYSICAL);
			}));
			clickPane.addButton(new Button(fillerPaneGItem), 2);
			clickPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, "&e&lOn-Equip", "&7", "&7*Commands that will execute only", "&7when the item is placed", "&7in an armor slot.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_EQUIP)), event -> {
				commandListPane(player, itemMap, Action.ON_EQUIP);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, "&e&lUn-Equip", "&7", "&7*Commands that will execute only", "&7when the item is removed", "&7from an armor slot.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.UN_EQUIP)), event -> {
				commandListPane(player, itemMap, Action.UN_EQUIP);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, "&e&lOn-Hold", "&7", "&7*Commands that will execute only", "&7when holding the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_HOLD)), event -> {
				commandListPane(player, itemMap, Action.ON_HOLD);
			}));
			clickPane.addButton(new Button(fillerPaneGItem));
			clickPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&e&lOn-Fire", "&7", "&7*Commands that will execute only", "&7when an arrow or bow is fired.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_FIRE)), event -> {
				commandListPane(player, itemMap, Action.ON_FIRE);
			}));
			clickPane.addButton(new Button(fillerPaneGItem));
			clickPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lOn-Consume", "&7", "&7*Commands that will execute only", "&7when you consume an the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_CONSUME)), event -> {
				commandListPane(player, itemMap, Action.ON_CONSUME);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&e&lOn-Receive", "&7", "&7*Commands that will execute only", "&7when you are given the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_RECEIVE)), event -> {
				commandListPane(player, itemMap, Action.ON_RECEIVE);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("397", 1, false, "&e&lOn-Death", "&7", "&7*Commands that will execute only", "&7when die with the", "&7item in your inventory.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_DEATH)), event -> {
				commandListPane(player, itemMap, Action.ON_DEATH);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("20", 1, false, "&e&lInteract-Air", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking the air.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_AIR)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_AIR);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("95:3", 1, false, "&e&lInteract-Air-Left", "&7", "&7*Commands that will execute only", "&7when left clicking the air.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_LEFT_AIR)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_LEFT_AIR);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("95:6", 1, false, "&e&lInteract-Air-Right", "&7", "&7*Commands that will execute only", "&7when right clicking the air.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_RIGHT_AIR)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_RIGHT_AIR);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("2", 1, false, "&e&lInteract-Block", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking a block.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_BLOCK)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_BLOCK);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("1", 1, false, "&e&lInteract-Block-Left", "&7", "&7*Commands that will execute only", "&7when left clicking a block.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_LEFT_BLOCK)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_LEFT_BLOCK);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("4", 1, false, "&e&lInteract-Block-Right", "&7", "&7*Commands that will execute only", "&7when right clicking a block.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_RIGHT_BLOCK)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_RIGHT_BLOCK);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("330", 1, false, "&e&lInteract-Left", "&7", "&7*Commands that will execute only", "&7when left clicking.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_LEFT_ALL)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_LEFT_ALL);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "324" : "64", 1, false, "&e&lInteract-Right", "&7", "&7*Commands that will execute only", "&7when right clicking.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INTERACT_RIGHT_ALL)), event -> {
				commandListPane(player, itemMap, Action.INTERACT_RIGHT_ALL);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Commands that will execute only", "&7when cursor swapping with another item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_SWAP_CURSOR)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_SWAP_CURSOR);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, "&e&lInventory-Middle", "&7", "&7*Commands that will execute only", "&7when cursor middle clicking the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_MIDDLE)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_MIDDLE);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, "&e&lInventory-Creative", "&7", "&7*Commands that will execute only", "&7when cursor clicking the item in creative mode.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_CREATIVE)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_CREATIVE);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, "&e&lInventory-Left", "&7", "&7*Commands that will execute only", "&7when cursor left clicking the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_LEFT)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_LEFT);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&e&lInventory-Right", "&7", "&7*Commands that will execute only", "&7when cursor right clicking the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_RIGHT)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_RIGHT);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("44", 2, false, "&e&lInventory-Shift-Left", "&7", "&7*Commands that will execute only", "&7when cursor shift left clicking the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_SHIFT_LEFT)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_SHIFT_LEFT);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("44:3", 2, false, "&e&lInventory-Shift-Right", "&7", "&7*Commands that will execute only", "&7when cursor shift right clicking the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.INVENTORY_SHIFT_RIGHT)), event -> {
				commandListPane(player, itemMap, Action.INVENTORY_SHIFT_RIGHT);
			}));
			clickPane.addButton(new Button(fillerPaneGItem));
			clickPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, "&e&lOn-Damage", "&7", "&7*Commands that will execute only", "&7when the player damages an", "&7entity or is damaged by an", "&7entity with the item in", "&7their inventory.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_DAMAGE)), event -> {
				commandListPane(player, itemMap, Action.ON_DAMAGE);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, "&e&lOn-Hit", "&7", "&7*Commands that will execute only", "&7when the player damages an", "&7entity while holding the item.", "&7", "&9&lCommands: &a" + 
			listCommands(itemMap, Action.ON_HIT)), event -> {
				commandListPane(player, itemMap, Action.ON_HIT);
			}));
			clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
			clickPane.addButton(new Button(fillerPaneBItem), 7);
			clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
		});
		clickPane.open(player);
	}
	
   /**
    * Gets the command for the specified action.
    * 
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action that the command should contain.
    * @return The raw String command.
    */
	private static String listCommands(final ItemMap itemMap, final Action action) {
		String commands = "";
		String commandReturn = "NONE";
		for (ItemCommand command: itemMap.getCommands()) {
			if (command.matchAction(action)) {
				commands += command.getRawCommand() + " /n ";
			}
		}
		if (StringUtils.nullCheck(commands) != "NONE") {
			commandReturn = commands;
		}
		return commandReturn;
	}
	
   /**
    * Modifies an existing ItemCommand or adds the new ItemCommand to the ItemMap.
    * 
    * @param itemMap - The ItemMap currently being modified.
    * @param itemCommand - The itemCommand to be modified or added.
    * @param newCommand - If this is a new command being added.
    */
	private static void modifyCommands(final ItemMap itemMap, final ItemCommand itemCommand, final boolean newCommand) {
		List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
		for (ItemCommand command: itemMap.getCommands()) { arrayCommands.add(command); }
		if (newCommand) { arrayCommands.add(itemCommand); } 
		else { arrayCommands.remove(itemCommand); }
		final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
		for (int i = 0; i < arrayCommands.size(); ++i) { commands[i] = arrayCommands.get(i); }
		itemMap.setCommands(commands);
	}
	
   /**
    * Gets the number of commands for the action type.
    * 
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    */
	private static int getCommandSize(final ItemMap itemMap, final Action action) {
		int l = 0;
		for (ItemCommand command: itemMap.getCommands()) {
			if (command.matchAction(action)) {
				l++;
			}
		}
		return l;
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items list of commands.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    */
	private static void commandListPane(final Player player, final ItemMap itemMap, final Action action) {
		Interface commandListPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			commandListPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the click type menu."), event -> {
				actionPane(player, itemMap);
			}));
			commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&lNew Line", "&7", "&7*Add a new command to be executed", "&7by &9&l" + action.name()), event -> {
				executorPane(player, itemMap, action);
			}));
			ItemCommand[] commandList = itemMap.getCommands();
			int l = 1;
			for (ItemCommand command: commandList) {
				if (command.matchAction(action)) {
					final int k = l;
					commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&f" + command.getRawCommand(), "&7", "&7*Click to &lmodify &7this command.", "&9&lOrder Number: &a" + k), event -> {
						modifyCommandsPane(player, itemMap, action, command, k);
					}));
					l++;
				}
			}
		});
		commandListPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items list of commands.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    * @param command - The ItemCommand instance being modified.
    * @param orderNumber - The current number that dictates the ItemCommands "place in line".
    */
	private static void orderPane(final Player player, final ItemMap itemMap, final Action action, final ItemCommand command, final int orderNumber) {
		Interface orderPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			orderPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command modify menu."), event -> {
				modifyCommandsPane(player, itemMap, action, command, orderNumber);
			}));
			for (int i = 1; i <= getCommandSize(itemMap, action); i++) {
				final int k = i;
				orderPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", k, false, "&9&lOrder Number: &a&l" + k, "&7", "&7*Click to set the order", "&7number of the command."), event -> {
					List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
					int l = 0;
					for (ItemCommand Command: itemMap.getCommands()) {
						if (Command.matchAction(action)) {
							if ((l + 1) == k) {
								arrayCommands.add(command);
							}
							l++;
						}
						if (!Command.equals(command)) {
							arrayCommands.add(Command);
						}
					}
					final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
					for (int j = 0; j < arrayCommands.size(); ++j) {
						commands[j] = arrayCommands.get(j);
					}
					itemMap.setCommands(commands);
					commandListPane(player, itemMap, action);
				}));
			}
		});
		orderPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for creating a new command for an item.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    * @param command - The ItemCommand instance being modified.
    * @param orderNumber - The current number that dictates the ItemCommands "place in line".
    */
	private static void modifyCommandsPane(final Player player, final ItemMap itemMap, final Action action, final ItemCommand command, final int orderNumber) {
		Interface modPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modPane.addButton(new Button(fillerPaneGItem), 4);
			modPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&f" + command.getRawCommand(), "&7", "&7*You are modifying this command.", "&9&lOrder Number: &a" + orderNumber)));
			modPane.addButton(new Button(fillerPaneGItem), 4);
			modPane.addButton(new Button(fillerPaneGItem));
			modPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, "&fIdentifier", "&7", "&7*Set a custom identifier", "&7for this command line.", "&7", "&cNOTE: &7This is in order to set", "&7a random command list sequence.", 
					"&7Only use this if", "&7the commands sequence is", "&7set to &aRANDOM_LIST&7.", "&7", "&9&lIDENTIFIER: &a" + StringUtils.nullCheck(command.getSection())), event -> {
						if (StringUtils.nullCheck(command.getSection()) != "NONE") {
							ItemCommand[] commands = itemMap.getCommands();
							for (ItemCommand Command: commands) {
								if (Command.equals(command)) {
									Command.setSection(null);
								}
							}
							itemMap.setCommands(commands);
							command.setSection(null);
							modifyCommandsPane(player, itemMap, action, command, orderNumber);
						} else {
							player.closeInventory();
							String[] placeHolders = LanguageAPI.getLang(false).newString();
							placeHolders[16] = "COMMAND IDENTIFIER";
							placeHolders[15] = "winner";
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
						}
					}, event -> {
						ItemCommand[] commands = itemMap.getCommands();
						for (ItemCommand Command: commands) {
							if (Command.equals(command)) {
								Command.setSection(ChatColor.stripColor(event.getMessage()));
							}
						}
						itemMap.setCommands(commands);
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "COMMAND IDENTIFIER";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
						commandListPane(event.getPlayer(), itemMap, action);
					}));
			modPane.addButton(new Button(fillerPaneGItem));
			modPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&fModify", "&7", "&7*Sets the command to", "&7another text entry."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "MODIFIED COMMAND";
				placeHolders[15] = "gamemode creative";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				ItemCommand[] commands = itemMap.getCommands();
				for (ItemCommand Command: commands) {
					if (Command.equals(command)) {
						Command.setCommand(ChatColor.stripColor(event.getMessage()));
					}
				}
				itemMap.setCommands(commands);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "MODIFIED COMMAND";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			modPane.addButton(new Button(fillerPaneGItem));
			modPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, "&fOrder", "&7", "&7*Changes the order of execution", "&7for this command line.", "&7", "&7This will simply set the order", "&7number and push the", 
					"&7other commands down by one."), event -> {
				orderPane(player, itemMap, action, command, orderNumber);
			}));
			modPane.addButton(new Button(fillerPaneGItem));
			modPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&fDelete", "&7", "&7*Click to &cdelete &7this command."), event -> {
				modifyCommands(itemMap, command, false);
				commandListPane(player, itemMap, action);
			}));
			modPane.addButton(new Button(fillerPaneGItem));
			modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
			modPane.addButton(new Button(fillerPaneBItem), 7);
			modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
		});
		modPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command executor.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - THe action to be matched,
    */
	private static void executorPane(final Player player, final ItemMap itemMap, final Action action) {
		Interface executorPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			executorPane.addButton(new Button(ItemHandler.getItem("289", 1, false, "&f")));
			executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, "&e&lPlayer", "&7", "&7*Executes the command", "&7as the player."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "PLAYER COMMAND";placeHolders[15] = "spawn";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("player: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "PLAYER COMMAND";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, true, "&e&lOp", "&7", "&7*Executes the command as if the", "&7player has /op (admin permissions)."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "OP COMMAND";
				placeHolders[15] = "broadcast I am &cADMIN!";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("op: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND LINE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&e&lConsole", "&7", "&7*Executes the command", "&7in the console window."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "CONSOLE COMMAND";
				placeHolders[15] = "gamemode creative %player%";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("console: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "OP COMMAND";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&e&lServer", "&7", "&7*Switches the player to", "&7the defined server name.", "&7", "&7&lNote: &7This is the name", 
					"&7defined in the BungeeCord config."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SERVER SWITCH";
				placeHolders[15] = "survival";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("server: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();placeHolders[16] = "SERVER SWITCH";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, false, "&e&lBungee", "&7", "&7*Executes a BungeeCord specific command."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "BUNGEE COMMAND";
				placeHolders[15] = "survival";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("bungee: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "BUNGEE COMMAND";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&e&lMessage", "&7", "&7*Sends the player a custom message."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "MESSAGE";
				placeHolders[15] = "&eWelcome to the Server!";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				modifyCommands(itemMap, ItemCommand.fromString("message: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "MESSAGE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			executorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, "&e&lSwap-Item", "&7", "&7*Swaps the item to another defined item."), event -> swapPane(player, itemMap, action)));
			executorPane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&e&lDelay", "&7", "&7*Adds a delay between command lines."), event -> delayPane(player, itemMap, action)));
			executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
			executorPane.addButton(new Button(fillerPaneBItem), 7);
			executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
		});
		executorPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the commands swap-item.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    */
	private static void swapPane(final Player player, final ItemMap itemMap, final Action action) {
		Interface swapPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			swapPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> {
				executorPane(player, itemMap, action);
			}));
			for (ItemMap item : ItemUtilities.getUtilities().copyItems()) {
				if (item.getNodeLocation() != itemMap.getNodeLocation()) {
					if (itemMap.isAnimated() || itemMap.isDynamic()) { setModifyMenu(true, player); itemMap.getAnimationHandler().get(player).setMenu(true, 1); }
					swapPane.addButton(new Button(ItemHandler.addLore(item.getTempItem(), "&7", "&6---------------------------", "&7*Click to set as a swap-item.", "&9&lNode: &a" + item.getConfigName(), "&7"), event -> { 
					modifyCommands(itemMap, ItemCommand.fromString("swap-item: " + item.getConfigName(), action, itemMap, 0L, null), true);
					commandListPane(player, itemMap, action); }));
				}
			}
		});
		swapPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command delay duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param action - The action to be matched.
    */
	private static void delayPane(final Player player, final ItemMap itemMap, final Action action) {
		Interface delayPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			delayPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> {
				executorPane(player, itemMap, action);
			}));
			delayPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom", "&7delay for the next command."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "DELAY";
				placeHolders[15] = "180";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					modifyCommands(itemMap, ItemCommand.fromString("delay: " + Integer.parseInt(ChatColor.stripColor(event.getMessage())), action, itemMap, Integer.parseInt(ChatColor.stripColor(event.getMessage())), null), true);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "DELAY";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				commandListPane(event.getPlayer(), itemMap, action);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				delayPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:8", k, false, "&9&lDelay: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the", "&7delay of the next command."), event -> {
					modifyCommands(itemMap, ItemCommand.fromString("delay: " + k, action, itemMap, k, null), true);
					commandListPane(player, itemMap, action);
				}));
			}
		});
		delayPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command cooldown duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void cooldownPane(final Player player, final ItemMap itemMap) {
		Interface cooldownPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			cooldownPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
				commandPane(player, itemMap);
			}));
			cooldownPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom commands-cooldown", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND COOLDOWN";
				placeHolders[15] = "180";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setCommandCooldown(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COMMAND COOLDOWN";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				commandPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				cooldownPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-cooldown of the item."), event -> {
					itemMap.setCommandCooldown(k);
					commandPane(player, itemMap);
				}));
			}
		});
		cooldownPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command warmup duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void warmPane(final Player player, final ItemMap itemMap) {
		Interface warmPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			warmPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
				commandPane(player, itemMap);
			}));
			warmPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Warmup", "&7", "&7*Click to set a custom commands-warmup", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND WARMUP";
				placeHolders[15] = "12";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setWarmDelay(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COMMAND WARMUP";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				commandPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				warmPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lWarming: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-warmup of the item."), event -> {
					itemMap.setWarmDelay(k);
					commandPane(player, itemMap);
				}));
			}
		});
		warmPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command cost.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void costPane(final Player player, final ItemMap itemMap) {
		Interface costPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			costPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
				commandPane(player, itemMap);
			}));
			costPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cost", "&7", "&7*Click to set a custom commands-cost", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND COST";
				placeHolders[15] = "340";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setCommandCost(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COMMAND COST";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				commandPane(event.getPlayer(), itemMap);
			}));
			for (int i = 0; i <= 64; i++) {
				final int k = i;
				costPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", (k == 0 ? 1 : k), false, "&9&lCost: &a$&l" + k, "&7", "&7*Click to set the", "&7commands-cost of the item."), event -> {
					itemMap.setCommandCost(k);
					commandPane(player, itemMap);
				}));
			}
		});
		costPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for an items command item currency.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void receivePane(final Player player, final ItemMap itemMap) {
		Interface receivePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			receivePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
				commandPane(player, itemMap);
			}));
			receivePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Receive", "&7", "&7*Click to set a custom commands-receive", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND RECEIVE";
				placeHolders[15] = "10";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setCommandReceive(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COMMAND RECEIVE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				commandPane(event.getPlayer(), itemMap);
			}));
			for (int i = 0; i <= 64; i++) {
				final int k = i;
				receivePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", (k == 0 ? 1 : k), false, "&9&lReceive: &a#&l" + k, "&7", "&7*Click to set the", "&7commands-receive of the item."), event -> {
					itemMap.setCommandReceive(k);
					commandPane(player, itemMap);
				}));
			}
		});
		receivePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command sequence.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void sequencePane(final Player player, final ItemMap itemMap) {
		Interface sequencePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			sequencePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, "&a&lSequential", "&7", "&7*Executes the command lines", "&7in order from top to bottom."), event -> {
				itemMap.setCommandSequence(CommandSequence.SEQUENTIAL);
				commandPane(player, itemMap);
			}));
			sequencePane.addButton(new Button(fillerPaneGItem));
			sequencePane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, "&a&lRandom Single", "&7", "&7*Executes one of the command lines", "&7randomly with equal values."), event -> {
				itemMap.setCommandSequence(CommandSequence.RANDOM_SINGLE);
				commandPane(player, itemMap);
			}));
			sequencePane.addButton(new Button(fillerPaneGItem));
			sequencePane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&a&lRandom List", "&7", "&7*Randomly selects from a list", "&7of commands to execute."), event -> {
				itemMap.setCommandSequence(CommandSequence.RANDOM_LIST);
				commandPane(player, itemMap);
			}));
			sequencePane.addButton(new Button(fillerPaneGItem));
			sequencePane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lRandom", "&7", "&7*Executes each command line in a", "&7random order with equal values."), event -> {
				itemMap.setCommandSequence(CommandSequence.RANDOM);
				commandPane(player, itemMap);
			}));
			sequencePane.addButton(new Button(fillerPaneGItem));
			sequencePane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&a&lRemain", "&7", "&7*Executes each command only if", "&7the item exists in the player", "&7inventory at the time of executing", "&7a delayed command line.", "&7The commands will be sent in", "&7order from top to bottom."), event -> {
				itemMap.setCommandSequence(CommandSequence.REMAIN);
				commandPane(player, itemMap);
			}));
			sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
			sequencePane.addButton(new Button(fillerPaneBItem), 7);
			sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
		});
		sequencePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command sound.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void soundPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface soundPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
				if (stage != 3) {
					soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> commandPane(player, itemMap)));
				} else { 
					soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap))); 
				}
			
			for (Sound sound: Sound.values()) {
				if (stage != 3) {
					soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, "&f" + sound.name(), "&7", "&7*Click to set the", "&7teleport-sound of the item."), event -> {
						itemMap.setTeleportSound(sound.name());
						teleportPane(player, itemMap, stage);
					}));
				} else {
					soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, "&f" + sound.name(), "&7", "&7*Click to set the", "&7commands-sound of the item."), event -> {
						itemMap.setCommandSound(sound);
						commandPane(player, itemMap);
					}));
				}
			}
		});
		soundPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items command particle.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void particlePane(final Player player, final ItemMap itemMap, final int stage) {
		Interface particlePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (stage != 3) {
				particlePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> {
					teleportPane(player, itemMap, stage);
				}));
			} else {
				particlePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
					commandPane(player, itemMap);
				}));
			}
			if (stage != 3) { } 
			else {
				particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&fFIREWORK_FAKE", "&7", "&7*Click to set the lifetime", "&7commands-particle of the item."), event -> lifePane(player, itemMap, "FIREWORK", 1)));
			}
			if (ServerUtils.hasSpecificUpdate("1_9")) {
				for (org.bukkit.Particle particle: org.bukkit.Particle.values()) {
					if (stage != 3) {
						particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + particle.name(), "&7", "&7*Click to set the", "&7teleport-effect of the item."), event -> { 
							itemMap.setTeleportEffect(particle.name());
							teleportPane(player, itemMap, stage); 
						}));
					} else {
						particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + particle.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> lifePane(player, itemMap, particle.name(), 0)));
					}
				}
			} else {
				for (org.bukkit.Effect effect: org.bukkit.Effect.values()) {
					if (stage != 3) {
						particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + effect.name(), "&7", "&7*Click to set the", "&7teleport-effect of the item."), event -> { 
							itemMap.setTeleportEffect(effect.name());
							teleportPane(player, itemMap, stage); 
						}));
					} else {
						particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + effect.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> lifePane(player, itemMap, effect.name(), 0)));
					}
				}
			}
		});
		particlePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the life time of the commands firework particle.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void lifePane(final Player player, final ItemMap itemMap, final String particle, final int stage) {
		Interface lifePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			lifePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
				particlePane(player, itemMap, 3);
			}));
			lifePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom LifeTime", "&7", "&7*Click to set a lifetime (duration)", "&7value for particle effect."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "PARTICLE LIFETIME";
				placeHolders[15] = "170";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					if (stage == 0) {
						itemMap.setCommandParticle(particle + ":" + ChatColor.stripColor(event.getMessage()));
					} else {
						explosionPane(player, itemMap, particle, Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					}
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "PARTICLE LIFETIME";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					particlePane(event.getPlayer(), itemMap, 3);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				lifePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:9", k, false, "&9&lLifeTime: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the lifetime", "&7(duration) of the particle effect."), event -> {
					if (stage == 0) {
						itemMap.setCommandParticle(particle + ":" + k);
						commandPane(player, itemMap);
					} else {
						explosionPane(player, itemMap, particle, k);
					}
				}));
			}
		});
		lifePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the commands firework explosion particle.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void explosionPane(final Player player, final ItemMap itemMap, final String particle, final int lifetime) {
		Interface patternPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			patternPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
				particlePane(player, itemMap, 3);
			}));
			for (Type explosion: Type.values()) {
				patternPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&f" + explosion.name(), "&7", "&7*Click to set the pattern", "&7of the firework explosion effect."), 
						event -> colorParticlePane(player, itemMap, particle, lifetime, explosion, null)));
			}
		});
		patternPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the commands color particle.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void colorParticlePane(final Player player, final ItemMap itemMap, final String particle, final int lifetime, final Type explosion, final DyeColor color1) {
		Interface colorPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
				particlePane(player, itemMap, 3);
			}));
			for (DyeColor color: DyeColor.values()) {
				colorPane.addButton(new Button(ItemHandler.getItem("GRAY_DYE", 1, false, "&f" + color.name(), "&7", "&7*Click to set the " + (color1 != null ? "&c&lend color" : "&9&lstart color"), "&7of the firework explosion effect."), event -> {
					if (color1 != null) {
						itemMap.setCommandParticle(particle + ":" + color1.name() + ":" + color.name() + ":" + explosion.name() + ":" + lifetime);
						commandPane(player, itemMap);
					} else {
						colorParticlePane(player, itemMap, particle, lifetime, explosion, color);
					}
				}));
			}
		});
		colorPane.open(player);
	}
	
//  ============================================================================================================================================================================================================================================================
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items enchantment.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void enchantPane(final Player player, final ItemMap itemMap) {
		Interface enchantPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			enchantPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			for (Enchantment enchant: Enchantment.values()) {
				if (ItemHandler.getEnchantName(enchant) != null) { 
					boolean containsKey = (itemMap.getEnchantments() != null) ? itemMap.getEnchantments().containsKey(ItemHandler.getEnchantName(enchant).toUpperCase()) : false;
					ItemStack enchantItem = ItemHandler.getItem((containsKey ? "ENCHANTED_BOOK" : "BOOK"), 1, false, "&f" + ItemHandler.getEnchantName(enchant).toUpperCase(), "&7", 
							"&7*Click to add this enchantment", "&7to the custom item.", "&7", "&9&lENABLED: &a" + (containsKey + "").toUpperCase(), (containsKey ? "&7" : ""), 
							(containsKey ? "&9&lLEVEL: &a" + itemMap.getEnchantments().get(ItemHandler.getEnchantName(enchant).toUpperCase()) : ""));
					enchantPane.addButton(new Button(enchantItem, event -> {
						if (containsKey) {
							Map < String, Integer > enchantments = itemMap.getEnchantments();
							enchantments.remove(ItemHandler.getEnchantName(enchant).toUpperCase());
							itemMap.setEnchantments(enchantments);
							enchantPane(player, itemMap);
						} else {
							enchantLevelPane(player, itemMap, enchant);
						}
					}));
				}
			}
		});
		enchantPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an echantment level.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void enchantLevelPane(final Player player, final ItemMap itemMap, final Enchantment enchant) {
		Interface enchantLevelPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			enchantLevelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the enchant selection menu."), event -> {
				enchantPane(player, itemMap);
			}));
			enchantLevelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Count", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ENCHANT LEVEL";
				placeHolders[15] = "86";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					Map < String, Integer > enchantments = (itemMap.getEnchantments() != null) ? itemMap.getEnchantments() : new HashMap < String, Integer >();
					enchantments.put(ItemHandler.getEnchantName(enchant).toUpperCase(), Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					itemMap.setEnchantments(enchantments);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ENCHANT LEVEL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				enchantPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				enchantLevelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lLevel: &a&l" + k, "&7", "&7*Click to set the", "&7level of the item enchantment.", "&7", "&7This will be &l" + 
				ItemHandler.getEnchantName(enchant).toUpperCase() + ":" + k), event -> {
					Map < String, Integer > enchantments = (itemMap.getEnchantments() != null) ? itemMap.getEnchantments() : new HashMap < String, Integer >();
					enchantments.put(ItemHandler.getEnchantName(enchant).toUpperCase(), k);
					itemMap.setEnchantments(enchantments);
					enchantPane(player, itemMap);
				}));
			}
		});
		enchantLevelPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items itemflags.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void flagPane(final Player player, final ItemMap itemMap) {
		Interface flagPane = new Interface(true, 5, GUIName, player);
		flagPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			setItemFlags(itemMap);
			creatingPane(player, itemMap);
		}));
		SchedulerUtils.runAsync(() -> {
			flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isOpBypass(), "&a&l&nAllowOpBypass", "&7", 
					"&a&lTrue&f:&7 Allows players who are OP to", "&7bypass any itemflags that add", "&7restrictions for this item.", "&7",
					"&c&lFalse&f:&7 Players who are OP will be", "&7restricted by itemflags that add", "&7restrictions for this item.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isOpBypass() + "").toUpperCase()), event -> {
				if (itemMap.isOpBypass()) {
					itemMap.setOpBypass(false);
				} else {
					itemMap.setOpBypass(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isCreativeBypass() ? (ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1") : "GOLDEN_APPLE", 1, itemMap.isCreativeBypass(), "&a&l&nCreativeBypass", "&7", 
					"&a&lTrue&f:&7 Allows players who are in Creative", "&7to bypass any itemflags that add", "&7restrictions for this item.", "&7",
					"&c&lFalse&f:&7 Players who are in Creative will", "&7be restricted by itemflags that add", "&7restrictions for this item.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isCreativeBypass() + "").toUpperCase()), event -> {
				if (itemMap.isCreativeBypass()) {
					itemMap.setCreativeBypass(false);
				} else {
					itemMap.setCreativeBypass(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("351:4", 1, itemMap.isGlowing(), "&a&l&nGlowing", "&7", 
					"&a&lTrue&f:&7 The item will glow as if it was enchanted!", "&7",
					"&c&lFalse&f:&7 The item will not glow.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isGlowing() + "").toUpperCase()), event -> {
				if (itemMap.isGlowing()) {
					itemMap.setGlowing(false);
				} else {
					itemMap.setGlowing(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("277", 7, itemMap.isStackable(), "&a&l&nStackable", "&7", 
					"&a&lTrue&f:&7 The item will be stackable with itself!", "&7",
					"&c&lFalse&f:&7 The item stack only if it did in vanilla.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isStackable() + "").toUpperCase()), event -> {
				if (itemMap.isStackable()) {
					itemMap.setStackable(false);
				} else {
					itemMap.setStackable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("38", 1, itemMap.isInventoryClose(), "&a&l&nInventory Close", "&7", 
					"&a&lTrue&f:&7 Closes the players current", "&7inventory when clicking the item.", "&7",
					"&c&lFalse&f:&7 The current inventory will not", "&7be closed when clicking the item.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isInventoryClose() + "").toUpperCase()), event -> {
				if (itemMap.isInventoryClose()) {
					itemMap.setCloseInventory(false);
				} else {
					itemMap.setCloseInventory(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, itemMap.isAutoRemove(), "&a&l&nAuto Remove", "&7", 
					"&a&lTrue&f:&7 Automatically removes the", "&7item from the players inventory", "&7when entering or joining a", "&7world that is not defined", "&7under the enabled-worlds.", "&7",
					"&c&lFalse&f:&7 The player will keep the", "&7item when entering or joining", "&7an undefined world.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isAutoRemove() + "").toUpperCase()), event -> {
				if (itemMap.isAutoRemove()) {
					itemMap.setAutoRemove(false);
				} else {
					itemMap.setAutoRemove(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, itemMap.isOverwritable(), "&a&l&nOverwrite", "&7", 
					"&a&lTrue&f: &7Allows the item to overwrite", "&7any existing items in the defined slot.", "&7", 
					"&c&lFalse&f:&7 The item will not overwrite other items.", "&7When the slot is full it", "&7will fail to give the item, unless", "&7the give-next or move-next flag is set to &a&lTrue&7.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isOverwritable() + "").toUpperCase()), event -> {
				if (itemMap.isOverwritable()) {
					itemMap.setOverwritable(false);
				} else {
					itemMap.setOverwritable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("CACTUS", 1, itemMap.isDisposable(), "&a&l&nDisposable", "&7", 
					"&a&lTrue&f:&7 If the item has a command", "&7defined, running the command", "&7will remove x1 of the item.", "&7",
					"&c&lFalse&f:&7 Running item commands will", "&7not lower the items count.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isDisposable() + "").toUpperCase()), event -> {
				if (itemMap.isDisposable()) {
					itemMap.setDisposable(false);
				} else {
					itemMap.setDisposable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WOODEN_SWORD" : "268"), 1, itemMap.isVanilla(), "&a&l&nVanilla", "&7", 
					"&a&lTrue&f:&7 The item will be given as a default no-name item.", 
					"&cNOTE: &7Itemflags and commands will NOT work", "&7unless the vanilla-status or vanilla-control", "&7itemflags are defined.", "&7",
					"&c&lFalse&f:&7 The item will be given", "&7as an custom item, allowing all", "&7ItemJoin properties to continue working.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isVanilla() + "").toUpperCase()), event -> {
				if (itemMap.isVanilla()) {
					itemMap.setVanilla(false);
				} else {
					itemMap.setVanilla(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, itemMap.isVanillaStatus(), "&a&l&nVanilla Status", "&7", 
					"&a&lTrue&f: &7Allows the Vanilla itemflag to retain", "&7ItemJoin properties without", "&7making it only a ItemJoin specific item.", 
					"&cNOTE: &7Useful for trying to implement", "&7ItemJoin items into other plugins.", "&7", 
					"&c&lFalse&f:&7 The item will be given", "&7as a custom item, allowing all", "&7ItemJoin properties to continue working.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isVanillaStatus() + "").toUpperCase()), event -> {
				if (itemMap.isVanillaStatus()) {
					itemMap.setVanillaStatus(false);
				} else {
					itemMap.setVanillaStatus(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WOODEN_PICKAXE" : "270"), 1, itemMap.isVanillaControl(), "&a&l&nVanilla Control", "&7", 
					"&a&lTrue&f: &7Allows the Vanilla itemflag to retain", "&7the use of commands and itemflags.", "&7", 
					"&c&lFalse&f:&7 The item will be given", "&7as an custom item, allowing all", "&7ItemJoin properties to continue working.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isVanillaControl() + "").toUpperCase()), event -> {
				if (itemMap.isVanillaControl()) {
					itemMap.setVanillaControl(false);
				} else {
					itemMap.setVanillaControl(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, itemMap.isIpLimted(), "&a&l&nIP Limit", "&7", 
					"&a&lTrue&f:&7 The item will be tied to the players IP.", "&7No other players using the same IP will receive the item.", "&7Useful to prevent item duplication.", "&7", 
					"&c&lFalse&f:&7 The item will not be tied to a players IP.", "&7Other player accounts using the same IP will receive the item.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isIpLimted() + "").toUpperCase()), event -> {
				if (itemMap.isIpLimted()) {
					itemMap.setIpLimited(false);
				} else {
					itemMap.setIpLimited(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isUnbreakable() ? "ENCHANTED_BOOK" : "BOOK", 1, itemMap.isUnbreakable(), "&a&l&nUnbreakable", "&7", 
					"&a&lTrue&f:&7 Allows the item to be unbreakable or INDESTRUCTIBLE!", "&7",
					"&c&lFalse&f:&7 The item will be damageable when being used.", "&7", "&7This flag only takes effect on items which have durability.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isUnbreakable() + "").toUpperCase()), event -> {
				if (itemMap.isUnbreakable()) {
					itemMap.setUnbreakable(false);
				} else {
					itemMap.setUnbreakable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENDER_EYE" : "381"), 1, itemMap.isAttributesInfo(), "&a&l&nHide Attributes", "&7", 
					"&a&lTrue&f: &7Hides all information tags from the item", "&7such as firework colors, damage values, enchants, etc.", "&7", 
					"&c&lFalse&f:&7 The item will have information tags visible.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isAttributesInfo() + "").toUpperCase()), event -> {
				if (itemMap.isAttributesInfo()) {
					itemMap.setAttributesInfo(false);
				} else {
					itemMap.setAttributesInfo(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("IRON_SWORD", 1, itemMap.isDurabilityBar(), "&a&l&nHide Durability", "&7", 
					"&a&lTrue&f:&7 The durability bar of the damageable ", "&7item will be hidden.", "&cNOTE: &7Items with this flag will still break,", "&7unless the unbreakable flag is set to &a&lTrue&7.", "&7",
					"&c&lFalse&f:&7 The durability bar of the", "&7damageable item will be shown as normal.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isDurabilityBar() + "").toUpperCase()), event -> {
				if (itemMap.isDurabilityBar()) {
					itemMap.setDurabilityBar(false);
				} else {
					itemMap.setDurabilityBar(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("COBBLESTONE", 1, itemMap.isPlaceable(), "&a&l&nPlacement", "&7", 
					"&a&lTrue&f: &7Prevents the item from being", "&7placed on the ground,", "&7in any item-frames, and entities.", "&7",
					"&c&lFalse&f: &7Item will be able to be placed.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isPlaceable() + "").toUpperCase()), event -> {
				if (itemMap.isPlaceable()) {
					itemMap.setPlaceable(false);
				} else {
					itemMap.setPlaceable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, itemMap.isMovement(), "&a&l&nInventory Modify", "&7", 
					"&a&lTrue&f: &7Prevents the item from being", "&7moved or switched to other slots", "&7and blocks placement in item-frames.", "&7",	
					"&c&lFalse&f:&7 Allows the item to be moved", "&7freely inside the players inventory.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isMovement() + "").toUpperCase()), event -> {
				if (itemMap.isMovement()) {
					itemMap.setMovement(false);
				} else {
					itemMap.setMovement(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.isDynamic(), "&a&l&nDynamic", "&7", 
					"&a&lTrue&f: &7Allows the item to dynamically", "&7update every 100 ticks", "&7Useful for updating placeholders.", "&7",
					"&c&lFalse&f: &7Item will not update its name, lore, etc.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isDynamic() + "").toUpperCase()), event -> {
				if (itemMap.isDynamic()) {
					itemMap.setDynamic(false);
				} else {
					itemMap.setDynamic(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("EGG", 1, itemMap.isAnimated(), "&a&l&nAnimate", "&7", 
					"&a&lTrue&f: &7Allows the item to animate between", "&7its different iterations defined", "&7under the animations tab.", "&7",
					"&c&lFalse&f: &7Item will not animate.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isAnimated() + "").toUpperCase()), event -> {
				if (itemMap.isAnimated()) {
					itemMap.setAnimate(false);
				} else {
					itemMap.setAnimate(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CHEST_MINECART" : "342"), 1, itemMap.isItemStore(), "&a&l&nItem Store", "&7",
					"&a&lTrue&f:&7 Prevents the storage of the item in any containers.", "&7Such as chests, armor stands, anvils, etc.", "&7",
					"&c&lFalse&f:&7 The item can be stored in containers.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isItemStore() + "").toUpperCase()), event -> {
				if (itemMap.isItemStore()) {
					itemMap.setItemStore(false);
				} else {
					itemMap.setItemStore(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "324" : "64", 1, itemMap.isCountLock(), "&a&l&nCount Lock", "&7", 
					"&a&lTrue&f:&7 The item can be used indefinitely.", "&7Useful to give a player infinite apples.", "&cNOTE: &7This will overwrite the disposable flag.", "&7",
					"&c&lFalse&f:&7 The item will be removed from the inventory on use.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isCountLock() + "").toUpperCase()), event -> {
				if (itemMap.isCountLock()) {
					itemMap.setCountLock(false);
				} else {
					itemMap.setCountLock(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, itemMap.isDeathKeepable(), "&a&l&nDeath Keep", "&7", 
					"&a&lTrue&f:&7 On death, the item will remain", "&7in players inventory on respawn.", "&7",
					"&c&lFalse&f:&7 On death, the item will be dropped", "&7at the death location as normal.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isDeathKeepable() + "").toUpperCase()), event -> {
				if (itemMap.isDeathKeepable()) {
					itemMap.setDeathKeepable(false);
				} else {
					itemMap.setDeathKeepable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("BONE", 1, itemMap.isDeathDroppable(), "&a&l&nDeath Drops", "&7", 
					"&a&lTrue&f:&7 On death, the item will be removed", "&7from the players inventory.", "&7",
					"&c&lFalse&f:&7 On death, the item will be dropped", "&7at the death location as normal.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isDeathDroppable() + "").toUpperCase()), event -> {
				if (itemMap.isDeathDroppable()) {
					itemMap.setDeathDroppable(false);
				} else {
					itemMap.setDeathDroppable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, itemMap.isSelfDroppable(), "&a&l&nSelf Drops", "&7", 
					"&a&lTrue&f: &7Prevents the item from being dropped", "&7by the player, returns it back to their inventory.", "&7",
					"&c&lFalse&f: &7Allows the item to be dropped.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isSelfDroppable() + "").toUpperCase()), event -> {
				if (itemMap.isSelfDroppable()) {
					itemMap.setSelfDroppable(false);
				} else {
					itemMap.setSelfDroppable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("FURNACE", 1, itemMap.isItemModify(), "&a&l&nItem Modifiable", "&7", 
					"&a&lTrue&f: &7Blocks the item from being", "&7repaired or enchanted in-game.", "&7",
					"&c&lFalse&f: &7Allows items to", "&7be repaired and enchanted.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isItemModify() + "").toUpperCase()), event -> {
				if (itemMap.isItemModify()) {
					itemMap.setItemModify(false);
				} else {
					itemMap.setItemModify(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("ANVIL", 1, itemMap.isItemRepairable(), "&a&l&nItem Repairable", "&7", 
					"&a&lTrue&f: &7Blocks the item from being", "&7used in an anvil or repaired.", "&7",
					"&c&lFalse&f: &7Allows the item to be repaired.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isItemRepairable() + "").toUpperCase()), event -> {
				if (itemMap.isItemRepairable()) {
					itemMap.setItemRepairable(false);
				} else {
					itemMap.setItemRepairable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, itemMap.isItemCraftable(), "&a&l&nItem Craftable", "&7", 
					"&a&lTrue&f: &7Blocks the item from being", "&7used in a crafting recipe.", "&7",
					"&c&lFalse&f: &7Item will be usable in", "&7a crafting recipe.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isItemCraftable() + "").toUpperCase()), event -> {
				if (itemMap.isItemCraftable()) {
					itemMap.setItemCraftable(false);
				} else {
					itemMap.setItemCraftable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("BOW", 1, itemMap.isCancelEvents(), "&a&l&nCancel Events", "&7", 
					"&a&lTrue&f: &7Prevents almost any event from executing", "&7when right-clicking the item.", "&7",
					"&c&lFalse&f: &7Allows item events to be executed freely.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isCancelEvents() + "").toUpperCase()), event -> {
				if (itemMap.isCancelEvents()) {
					itemMap.setCancelEvents(false);
				} else {
					itemMap.setCancelEvents(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WHEAT_SEEDS" : "295"), 1, itemMap.isAlwaysGive(), "&a&l&nAlways Give", "&7", 
					"&a&lTrue&f: &7Gives the item every time the player", "&7performs one of the triggers actions.", "&7regardless of already having the item.", "&7",
					"&cNOTE: &7Don't use this if you want only ONE instance of the item.", "&7",
					"&c&lFalse&f: &7Normal item restrictions will apply.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isAlwaysGive() + "").toUpperCase()), event -> {
				if (itemMap.isAlwaysGive()) {
					itemMap.setAlwaysGive(false);
				} else {
					itemMap.setAlwaysGive(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, itemMap.isItemChangable(), "&a&l&nAllow Modifications", "&7", 
					"&a&lTrue&f: &7Allows the players to modify the item", "&7while retaining all properties.", "&7",
					"&c&lFalse&f: &7Item will not be modifiable.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isItemChangable() + "").toUpperCase()), event -> {
				if (itemMap.isItemChangable()) {
					itemMap.setItemChangable(false);
				} else {
					itemMap.setItemChangable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("ITEM_FRAME", 1, itemMap.isGiveNext(), "&a&l&nGive Next", "&7", 
					"&a&lTrue&f: &7Gives the item to the next available slot", "&7only if the defined slot already has an existing item.", 
					"&cNOTE: &7The overwrite flag will not work.", "&7",
					"&c&lFalse&f:&7 The item will be only given in the defined slot.", "&7If an item is already in the slot the", "&7item wont be given, unless the overwrite", "&7flag is set to &l&aTrue&7.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isGiveNext() + "").toUpperCase()), event -> {
				if (itemMap.isGiveNext()) {
					itemMap.setGiveNext(false);
				} else {
					itemMap.setGiveNext(true);
				}
				flagPane(player, itemMap);
			}));
			if (itemMap.getMaterial().toString().contains("TIPPED_ARROW") || itemMap.getMaterial().toString().contains("ARROW")) {
				flagPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, itemMap.isTeleport(), "&a&l&nTeleport", "&7", 
						"&a&lTrue&f: &7Teleports the Player to the location", "&7that the arrow landed.", 
						"&cNOTE: &7This only works if the arrow is fired by a Bow.",
						"&9&lENABLED: &a" + (itemMap.isTeleport() + "").toUpperCase()), event -> {
					if (itemMap.isTeleport()) {
						itemMap.setTeleport(false);
					} else {
						itemMap.setTeleport(true);
					}
					flagPane(player, itemMap);
				}));
			} else { flagPane.addButton(new Button(fillerPaneBItem)); }
			flagPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, itemMap.isMoveNext(), "&a&l&nMove Next", "&7", 
					"&a&lTrue&f: &7Moves the existing item to the next available slot", "&7only if the defined slot already has an existing item.", 
					"&cNOTE: &7The overwrite flag will not work.", "&7",
					"&c&lFalse&f: &7The item will be only given in the defined slot.", "&7If an item is already in the slot the", "&7item wont be given, unless the overwrite", "&7flag is set to &l&aTrue&7.", "&7", 
					"&9&lENABLED: &a" + (itemMap.isMoveNext() + "").toUpperCase()), event -> {
				if (itemMap.isMoveNext()) {
					itemMap.setMoveNext(false);
				} else {
					itemMap.setMoveNext(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, itemMap.isDropFull(), "&a&l&nDrop Full", "&7", 
					"&a&lTrue&f: &7Drops the item on the ground if", "&7the players inventory is full.", "&7",
					"&c&lFalse&f: &7Fails to give the item", "&7if the players inventory is full.", "&7", 
					"&9&lENABLED: &a" +  (itemMap.isDropFull() + "").toUpperCase()), event -> {
				if (itemMap.isDropFull()) {
					itemMap.setDropFull(false);
				} else {
					itemMap.setDropFull(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, itemMap.isSelectable(), "&a&l&nSelectable", "&7", 
					"&a&lTrue&f: &7Prevents the item from being", "&7held in the players hand.", "&7",
					"&c&lFalse&f: &7Allows the item to be selected.", "&7", 
					"&9&lENABLED: &a" +  (itemMap.isSelectable() + "").toUpperCase()), event -> {
				if (itemMap.isSelectable()) {
					itemMap.setSelectable(false);
				} else {
					itemMap.setSelectable(true);
				}
				flagPane(player, itemMap);
			}));
			flagPane.addButton(new Button(fillerPaneBItem));
		});
		flagPane.open(player);
	}
	
   /**
    * Sets the ItemFlags to the ItemMap.
    * 
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void setItemFlags(final ItemMap itemMap) {
		String itemflags = "";
		if (itemMap.isVanilla()) { itemflags += "VANILLA, "; }
		if (itemMap.isVanillaStatus()) { itemflags += "VANILLA-STATUS, "; }
		if (itemMap.isVanillaControl()) { itemflags += "VANILLA-CONTROL, "; }
		if (itemMap.isIpLimted()) { itemflags += "IP-LIMIT, "; }
		if (itemMap.isUnbreakable()) { itemflags += "UNBREAKABLE, "; }
		if (itemMap.isAttributesInfo()) { itemflags += "HIDE-ATTRIBUTES, "; }
		if (itemMap.isDurabilityBar()) { itemflags += "HIDE-DURABILITY, "; }
		if (itemMap.isPlaceable()) { itemflags += "PLACEMENT, "; }
		if (itemMap.isMovement()) { itemflags += "INVENTORY-MODIFY, "; }
		if (itemMap.isInventoryClose()) { itemflags += "INVENTORY-CLOSE, "; }
		if (itemMap.isDynamic()) { itemflags += "DYNAMIC, "; }
		if (itemMap.isAnimated()) { itemflags += "ANIMATE, "; }
		if (itemMap.isGlowing()) { itemflags += "GLOWING, "; }
		if (itemMap.isStackable()) { itemflags += "STACKABLE, "; }
		if (itemMap.isSelectable()) { itemflags += "SELECTABLE, "; }
		if (itemMap.isItemStore()) { itemflags += "ITEM-STORE, "; }
		if (itemMap.isCancelEvents()) { itemflags += "CANCEL-EVENTS, "; }
		if (itemMap.isCountLock()) { itemflags += "COUNT-LOCK, "; }
		if (itemMap.isDeathKeepable()) { itemflags += "DEATH-KEEP, "; }
		if (itemMap.isDeathDroppable()) { itemflags += "DEATH-DROPS, "; }
		if (itemMap.isSelfDroppable()) { itemflags += "SELF-DROPS, "; }
		if (itemMap.isDisposable()) { itemflags += "DISPOSABLE, "; }
		if (itemMap.isItemModify()) { itemflags += "ITEM-MODIFIABLE, "; }
		if (itemMap.isItemRepairable()) { itemflags += "ITEM-REPAIRABLE, "; }
		if (itemMap.isItemCraftable()) { itemflags += "ITEM-CRAFTABLE, "; }
		if (itemMap.isAlwaysGive()) { itemflags += "ALWAYS-GIVE, "; }
		if (itemMap.isItemChangable()) { itemflags += "ITEM-CHANGABLE, "; }
		if (itemMap.isTeleport()) { itemflags += "TELEPORT, "; }
		if (itemMap.isGiveNext()) { itemflags += "GIVE-NEXT, "; }
		if (itemMap.isMoveNext()) { itemflags += "MOVE-NEXT, "; }
		if (itemMap.isDropFull()) { itemflags += "DROP-FULL, "; }
		if (itemMap.isOverwritable()) { itemflags += "OVERWRITE, "; }
		if (itemMap.isOpBypass()) { itemflags += "ALLOWOPBYPASS, "; }
		if (itemMap.isAutoRemove()) { itemflags += "AUTO-REMOVE, "; }
		if (itemMap.isCreativeBypass()) { itemflags += "CREATIVEBYPASS, "; }
		if (itemflags.endsWith(", ")) { itemflags = itemflags.substring(0, itemflags.length() - 2); }
		itemMap.setItemFlags(itemflags);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items triggers.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void triggerPane(final Player player, final ItemMap itemMap) {
		Interface triggerPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			triggerPane.addButton(new Button(fillerPaneBItem), 3);
			triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "MAP"), 1, itemMap.isOnlyFirstJoin(), "&e&l&nFirst Join", "&7", "&7*Gives the item when the", "&7player logs into the server", 
					"&7for the first time only.", "&7This will overwrite any triggers", "&7such as respawn, and world-switch.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstJoin() + "").toUpperCase()), event -> {
				if (itemMap.isOnlyFirstJoin()) {
					itemMap.setOnlyFirstJoin(false);
				} else {
					itemMap.setOnlyFirstJoin(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, itemMap.isOnlyFirstWorld(), "&e&l&nFirst World", "&7", "&7*Gives the item when the", "&7player enters each of the defined", "&7worlds for the first time.", "&7", 
					"&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
				if (itemMap.isOnlyFirstWorld()) {
					itemMap.setOnlyFirstWorld(false);
				} else {
					itemMap.setOnlyFirstWorld(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "TOTEM_OF_UNDYING" : "322:1", 1, itemMap.isOnlyFirstLife(), "&e&l&nFirst Life", "&7", "&7*Gives the item when the", "&7player logs into the server", 
					"&7for the first time only,", "&7but will give the item", "&7EVERY TIME on player RESPAWN.", "&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
				if (itemMap.isOnlyFirstWorld()) {
					itemMap.setOnlyFirstWorld(false);
				} else {
					itemMap.setOnlyFirstWorld(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(fillerPaneBItem), 3);
			triggerPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, itemMap.isGiveOnDisabled(), "&c&l&nDISABLED", "&7", "&7*Prevents the item from given", "&7through the use of triggers.", "&7", "&7Useful to only get the item", 
					"&7using &l/itemjoin get <item>", "&9&lENABLED: &a" + (itemMap.isGiveOnDisabled() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnDisabled()) {
					itemMap.setGiveOnDisabled(false);
				} else {
					itemMap.setGiveOnJoin(false);
					itemMap.setOnlyFirstJoin(false);
					itemMap.setOnlyFirstWorld(false);
					itemMap.setOnlyFirstLife(false);
					itemMap.setGiveOnRespawn(false);
					itemMap.setGiveOnWorldSwitch(false);
					itemMap.setUseOnLimitSwitch(false);
					itemMap.setGiveOnRegionEnter(false);
					itemMap.setGiveOnRegionLeave(false);
					itemMap.setGiveOnRegionAccess(false);
					itemMap.setGiveOnRegionEgress(false);
					itemMap.setGiveOnDisabled(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("323", 1, itemMap.isGiveOnJoin(), "&e&l&nJoin", "&7", "&7*Gives the item when the", "&7player logs into the server.", 
					"&9&lENABLED: &a" + (itemMap.isGiveOnJoin() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnJoin()) {
					itemMap.setGiveOnJoin(false);
				} else {
					itemMap.setGiveOnJoin(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isGiveOnRespawn(), "&e&l&nRespawn", "&7", "&7*Gives the item when the", "&7player respawns from a death event.", "&9&lENABLED: &a" + 
			(itemMap.isGiveOnRespawn() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnRespawn()) {
					itemMap.setGiveOnRespawn(false);
				} else {
					itemMap.setGiveOnRespawn(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, itemMap.isGiveOnWorldSwitch(), "&e&l&nWorld Switch", "&7", "&7*Gives the item when the", "&7player teleports to one", "&7of the specified worlds.", 
					"&9&lENABLED: &a" + (itemMap.isGiveOnWorldSwitch() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnWorldSwitch()) {
					itemMap.setGiveOnWorldSwitch(false);
				} else {
					itemMap.setGiveOnWorldSwitch(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("LEVER", 1, itemMap.isUseOnLimitSwitch(), "&e&l&nGamemode Switch", "&7", "&7*Gives the item when the", "&7player changes gamemodes to any", "&7of the defined limit-modes.", 
					"&9&lENABLED: &a" + (itemMap.isUseOnLimitSwitch() + "").toUpperCase()), event -> {
				if (itemMap.isUseOnLimitSwitch()) {
					itemMap.setUseOnLimitSwitch(false);
				} else {
					itemMap.setUseOnLimitSwitch(true);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, itemMap.isGiveOnRegionEnter(), "&e&l&nRegion Enter", "&7", "&7*Gives the item when the", "&7player enters any of the enabled-regions.", "&9&lENABLED: &a" +
			(itemMap.isGiveOnRegionEnter() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnRegionEnter()) {
					itemMap.setGiveOnRegionEnter(false);
				} else {
					itemMap.setGiveOnRegionEnter(true);
					itemMap.setGiveOnRegionAccess(false);
					itemMap.setGiveOnRegionEgress(false);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("408", 1, itemMap.isGiveOnRegionLeave(), "&e&l&nRegion Leave", "&7", "&7*Removes the item when the", "&7player leaves any of the enabled-regions.", "&9&lENABLED: &a" +
			(itemMap.isGiveOnRegionLeave() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnRegionLeave()) {
					itemMap.setGiveOnRegionLeave(false);
				} else {
					itemMap.setGiveOnRegionLeave(true);
					itemMap.setGiveOnRegionAccess(false);
					itemMap.setGiveOnRegionEgress(false);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("407", 1, itemMap.isGiveOnRegionAccess(), "&e&l&nRegion Access", "&7", "&7*Gives the item when the", "&7player enters any of the enabled-regions", "&7and removes the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
			(itemMap.isGiveOnRegionAccess() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnRegionAccess()) {
					itemMap.setGiveOnRegionAccess(false);
				} else {
					itemMap.setGiveOnRegionAccess(true);
					itemMap.setGiveOnRegionEnter(false);
					itemMap.setGiveOnRegionLeave(false);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("342", 1, itemMap.isGiveOnRegionEgress(), "&e&l&nRegion Engress", "&7", "&7*Removes the item when the", "&7player enters any of the enabled-regions", "&7and gives the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
			(itemMap.isGiveOnRegionEgress() + "").toUpperCase()), event -> {
				if (itemMap.isGiveOnRegionEgress()) {
					itemMap.setGiveOnRegionEgress(false);
				} else {
					itemMap.setGiveOnRegionEgress(true);
					itemMap.setGiveOnRegionEnter(false);
					itemMap.setGiveOnRegionLeave(false);
				}
				triggerPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
				setTriggers(itemMap);creatingPane(player, itemMap);
			}));
			triggerPane.addButton(new Button(fillerPaneBItem), 7);
			triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
				setTriggers(itemMap);creatingPane(player, itemMap);
			}));
		});
		triggerPane.open(player);
	}
	
   /**
    * Sets the Triggers to the ItemMap.
    * 
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void setTriggers(final ItemMap itemMap) {
		String triggers = "";
		if (itemMap.isGiveOnDisabled()) { triggers += "DISABLED, "; }
		if (itemMap.isGiveOnJoin() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) { triggers += "JOIN, "; }
		if (itemMap.isOnlyFirstJoin()) { triggers += "FIRST-JOIN, "; }
		if (itemMap.isOnlyFirstWorld()) { triggers += "FIRST-WORLD, "; }
		if (itemMap.isOnlyFirstLife()) { triggers += "FIRST-LIFE, "; }
		if (itemMap.isGiveOnRespawn() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) { triggers += "RESPAWN, "; }
		if (itemMap.isGiveOnWorldSwitch() && !itemMap.isOnlyFirstWorld()) { triggers += "WORLD-SWITCH, "; }
		if (itemMap.isUseOnLimitSwitch()) { triggers += "GAMEMODE-SWITCH, "; }
		if (itemMap.isGiveOnRegionEnter()) { triggers += "REGION-ENTER, "; }
		if (itemMap.isGiveOnRegionLeave()) { triggers += "REGION-LEAVE, "; }
		if (itemMap.isGiveOnRegionAccess()) { triggers += "REGION-ACCESS, "; }
		if (itemMap.isGiveOnRegionEgress()) { triggers += "REGION-EGRESS, "; }
		if (triggers.endsWith(", ")) { triggers = triggers.substring(0, triggers.length() - 2); }
		itemMap.setTriggers(triggers);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items worlds.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void worldPane(final Player player, final ItemMap itemMap) {
		Interface worldPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			worldPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			List < String > enabledWorlds = itemMap.getEnabledWorlds();
			worldPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.containsWorld("ALL"), "&a&l&nGLOBAL", "&7", "&7*Click to enable the", "&7custom item in &lALL WORLDS.", "&9&lENABLED: &a" + 
			(itemMap.containsWorld("ALL") + "").toUpperCase()), event -> {
				if (itemMap.containsWorld("ALL")) {
					enabledWorlds.remove("GLOBAL");
					enabledWorlds.remove("ALL");
				} else {
					enabledWorlds.add("GLOBAL");
				}
				itemMap.setEnabledWorlds(enabledWorlds);worldPane(player, itemMap);
			}));
			for (World world: Bukkit.getServer().getWorlds()) {
				String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
				if (world.getEnvironment().equals(Environment.NETHER)) {
					worldMaterial = "NETHERRACK";
				} else if (world.getEnvironment().equals(Environment.THE_END)) {
					worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
				}
				worldPane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, itemMap.containsWorld(world.getName()), "&f&l" + world.getName(), "&7", "&7*Click to enable the", "&7custom item in this world.", 
						"&9&lENABLED: &a" + (itemMap.containsWorld(world.getName()) + "").toUpperCase()), event -> {
					if (itemMap.containsWorld(world.getName())) {
						enabledWorlds.remove(world.getName());
					} else {
						enabledWorlds.add(world.getName());
					}
					itemMap.setEnabledWorlds(enabledWorlds);worldPane(player, itemMap);
				}));
			}
		});
		worldPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items regions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void regionPane(final Player player, final ItemMap itemMap) {
		Interface regionPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			regionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			List < String > enabledRegions = itemMap.getEnabledRegions();
			regionPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.containsRegion("UNDEFINED"), "&c&l&nUNDEFINED", "&7", "&7*Click to enable the", "&7custom item in &lALL REGIONS.", "&9&lENABLED: &a" + 
			(itemMap.containsRegion("UNDEFINED") + "").toUpperCase()), event -> {
				if (itemMap.containsRegion("UNDEFINED")) {
					enabledRegions.remove("UNDEFINED");
				} else {
					enabledRegions.add("UNDEFINED");
				}
				itemMap.setEnabledRegions(enabledRegions);regionPane(player, itemMap);
			}));
			for (World world: Bukkit.getServer().getWorlds()) {
				for (String region: DependAPI.getDepends(false).getGuard().getRegions(world).keySet()) {
					String regionMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
					if (world.getEnvironment().equals(Environment.NETHER)) {
						regionMaterial = "NETHERRACK";
					} else if (world.getEnvironment().equals(Environment.THE_END)) {
						regionMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
					}
					regionPane.addButton(new Button(ItemHandler.getItem(regionMaterial, 1, itemMap.containsRegion(region), "&f&l" + region, "&7", "&a&lWORLD: &f" + world.getName(), "&7", "&7*Click to enable the", 
							"&7custom item in this region.", "&9&lENABLED: &a" + (itemMap.containsRegion(region) + "").toUpperCase()), event -> {
						if (itemMap.containsRegion(region)) {
							enabledRegions.remove(region);
						} else {
							enabledRegions.add(region);
						}
						itemMap.setEnabledRegions(enabledRegions);regionPane(player, itemMap);
					}));
				}
			}
		});
		regionPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying an items lore.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void lorePane(final Player player, final ItemMap itemMap) {
		Interface lorePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			lorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			lorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to the item lore."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "LORE LINE";
				placeHolders[15] = "&bThis is a new lore line.";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "LORE LINE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				List<String> lore = new ArrayList<String>(); 
				if (itemMap.getCustomLore() != null) {
					lore = itemMap.getCustomLore(); 
					lore.add(ChatColor.stripColor(event.getMessage())); 
					itemMap.setCustomLore(lore); 
				} else { 
					lore.add(ChatColor.stripColor(event.getMessage())); 
					itemMap.setCustomLore(lore);
				}
				lorePane(player, itemMap);
			}));
			if (itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
				for (int i = 1; i <= itemMap.getCustomLore().size(); i++) {
					final int k = i;
					lorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&fLore " + k, "&7", "&7*Click modify this lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(k - 1)), event -> modifyLoreLinePane(player, itemMap, k - 1)));
				}
			}
		});
		lorePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying a specific lore line.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyLoreLinePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyLorePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&e&l&nModify", "&7", "&7*Change the lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(position)), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "LORE LINE";
				placeHolders[15] = "&bThis is a new lore line.";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				List <  String> lore = itemMap.getCustomLore();
				lore.set(position, ChatColor.stripColor(event.getMessage()));
				itemMap.setCustomLore(lore);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "LORE LINE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				modifyLoreLinePane(event.getPlayer(), itemMap, position);
			}));
			modifyLorePane.addButton(new Button(fillerPaneGItem));
			modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this lore line."), event -> {
				List < String > lore = itemMap.getCustomLore();
				lore.remove(position);
				itemMap.setCustomLore(lore);
				lorePane(player, itemMap);
			}));
			modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> lorePane(player, itemMap)));
			modifyLorePane.addButton(new Button(fillerPaneBItem), 7);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> lorePane(player, itemMap)));
		});
		modifyLorePane.open(player);
	}
	
//  ============================================== //
//           Animation Definition Menus            //
//  ============================================== //
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated material.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void animateMaterialPane(final Player player, final ItemMap itemMap) {
		Interface animateMaterialPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			animateMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
				if (!itemMap.getDynamicMaterials().isEmpty()) {
					itemMap.setAnimate(true);
					if (ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)).contains(":")) {
						String[] material = ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)).split(":");
						itemMap.setMaterial(ItemHandler.getMaterial(material[0], null));
						itemMap.setDataValue((short)Integer.parseInt(material[1]));
					} else {
						itemMap.setMaterial(ItemHandler.getMaterial(ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)), null));
					}
				}
				animationPane(player, itemMap);
			}));
			animateMaterialPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Material", "&7", "&7*Add a new material", "&7to be animated between."), event -> selectMaterialPane(player, itemMap, 0, true)));
			for (int i = 1; i <= itemMap.getDynamicMaterials().size(); i++) {
				final int k = i;
				animateMaterialPane.addButton(new Button(ItemHandler.getItem(ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(k - 1)), 1, false, "&fMaterial " + k, "&7", "&7*Click modify this animated material.", 
						"&9&lAnimation Ticks: &a" + StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicMaterials().get(k - 1)))), event -> modifyMaterialPane(player, itemMap, k - 1)));
			}
		});
		animateMaterialPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying aniamted material.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void selectMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew) {
		Interface selectMaterialPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			selectMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> {
				animateMaterialPane(player, itemMap);
			}));
			selectMaterialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "BUKKIT MATERIAL";
				placeHolders[15] = "IRON_SWORD";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != null) {
					if (isNew) {
						durationMaterialPane(player, itemMap, position, isNew, ChatColor.stripColor(event.getMessage()).toUpperCase());
					} else {
						List < String > mats = itemMap.getDynamicMaterials();
						mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + ChatColor.stripColor(event.getMessage()).toUpperCase());
						itemMap.setDynamicMaterials(mats);
						modifyMaterialPane(player, itemMap, position);
					}
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "BUKKIT MATERIAL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noMaterial", player, placeHolders);
					selectMaterialPane(player, itemMap, position, isNew);
				}
			}));
				Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
				for (Material material: Material.values()) {
				if (!material.name().contains("LEGACY") && material.name() != "AIR" && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
					if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
						for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
							if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
								final int dataValue = i;
								selectMaterialPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
									if (isNew) {
										if (dataValue != 0) { durationMaterialPane(player, itemMap, position, isNew, material.name() + ":" + dataValue); }
										else { durationMaterialPane(player, itemMap, position, isNew, material.name()); }
									} else {
										List < String > mats = itemMap.getDynamicMaterials();
										if (dataValue != 0) { mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + material.name() + ":" + dataValue); }
										else { mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + material.name()); }
										itemMap.setDynamicMaterials(mats);
										modifyMaterialPane(player, itemMap, position);
									}
								}));
							}
						}
					} else {
						selectMaterialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
							if (isNew) {
								durationMaterialPane(player, itemMap, position, isNew, material.name());
							} else {
								List < String > mats = itemMap.getDynamicMaterials();
								mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + material.name());
								itemMap.setDynamicMaterials(mats);
								modifyMaterialPane(player, itemMap, position);
							}
						}));
					}
				}
			}
			inventoryCheck.clear();
		});
		selectMaterialPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated material duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durationMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animateMaterialPane(player, itemMap)));
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATION DURATION";
				placeHolders[15] = "110";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ANIMATION DURATION";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					List < String > mats = itemMap.getDynamicMaterials();
					if (isNew) {
						if (itemMap.getDynamicMaterials().isEmpty()) {
							mats.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + itemMap.getMaterial());
						}
						mats.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + value);
					} else {
						mats.set(position, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + ItemHandler.cutDelay(mats.get(position)));
					}
					itemMap.setDynamicMaterials(mats);
					if (isNew) {
						animateMaterialPane(player, itemMap);
					} else {
						modifyMaterialPane(player, itemMap, position);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					durationMaterialPane(player, itemMap, position, isNew, value);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
					List < String > mats = itemMap.getDynamicMaterials();
					if (isNew) {
						if (itemMap.getDynamicMaterials().isEmpty()) {
							mats.add("<delay:" + k + ">" + itemMap.getMaterial());
						}
						mats.add("<delay:" + k + ">" + value);
					} else {
						mats.set(position, "<delay:" + k + ">" + ItemHandler.cutDelay(mats.get(position)));
					}
					itemMap.setDynamicMaterials(mats);
					if (isNew) {
						animateMaterialPane(player, itemMap);
					} else {
						modifyMaterialPane(player, itemMap, position);
					}
				}));
			}
		});
		durationPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated material.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyMaterialPane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyMaterialPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modifyMaterialPane.addButton(new Button(fillerPaneGItem), 3);
			modifyMaterialPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nMaterial", "&7", "&7*Change the animated material type.", "&9&lMaterial: &a" + ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(position))),
					event -> selectMaterialPane(player, itemMap, position, false)));
			modifyMaterialPane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
			StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicMaterials().get(position)))), event -> durationMaterialPane(player, itemMap, position, false, ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(position)))));
			modifyMaterialPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated material."), event -> {
				List < String > mats = itemMap.getDynamicMaterials();mats.remove(position);
				itemMap.setDynamicMaterials(mats);
				animateMaterialPane(player, itemMap);
			}));
			modifyMaterialPane.addButton(new Button(fillerPaneGItem), 3);
			modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> animateMaterialPane(player, itemMap)));
			modifyMaterialPane.addButton(new Button(fillerPaneBItem), 7);
			modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> animateMaterialPane(player, itemMap)));
		});
		modifyMaterialPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated names.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void animatedNamePane(final Player player, final ItemMap itemMap) {
		Interface animatedNamePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			animatedNamePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
				if (!itemMap.getDynamicNames().isEmpty()) {
					itemMap.setAnimate(true);
					itemMap.setCustomName(ItemHandler.cutDelay(itemMap.getDynamicNames().get(0)));
				}
				animationPane(player, itemMap);
			}));
			animatedNamePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Name Line", "&7", "&7*Add a new name line", "&7to be animated between."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NAME";
				placeHolders[15] = "&bUltimate Sword";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				durationNamePane(player, itemMap, 0, true, ChatColor.stripColor(event.getMessage()));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NAME";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
			}));
			for (int i = 1; i <= itemMap.getDynamicNames().size(); i++) {
				final int k = i;
				animatedNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&fName " + k, "&7", "&7*Click modify this animated name.", "&9&lName: &a" + ItemHandler.cutDelay(itemMap.getDynamicNames().get(k - 1)), 
						"&9&lAnimation Ticks: &a" + StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicNames().get(k - 1)))), event -> modifyNamePane(player, itemMap, k - 1)));
			}
		});	
		animatedNamePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated name duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durationNamePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedNamePane(player, itemMap)));
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATION DURATION";
				placeHolders[15] = "110";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ANIMATION DURATION";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					List < String > names = itemMap.getDynamicNames();
					if (isNew) {
						if (itemMap.getDynamicNames().isEmpty() && itemMap.getCustomName() != null && !itemMap.getCustomName().isEmpty()) {
							names.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + itemMap.getCustomName());
						}
						names.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + value);
					} else {
						names.set(position, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + ItemHandler.cutDelay(names.get(position)));
					}
					itemMap.setDynamicNames(names);
					if (isNew) {
						animatedNamePane(player, itemMap);
					} else {
						modifyNamePane(player, itemMap, position);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					durationNamePane(player, itemMap, position, isNew, value);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
					List < String > names = itemMap.getDynamicNames();
					if (isNew) {
						if (itemMap.getDynamicNames().isEmpty() && itemMap.getCustomName() != null && !itemMap.getCustomName().isEmpty()) {
							names.add("<delay:" + k + ">" + itemMap.getCustomName());
						}
						names.add("<delay:" + k + ">" + value);
					} else {
						names.set(position, "<delay:" + k + ">" + ItemHandler.cutDelay(names.get(position)));
					}
					itemMap.setDynamicNames(names);
					if (isNew) {
						animatedNamePane(player, itemMap);
					} else {
						modifyNamePane(player, itemMap, position);
					}
				}));
			}
		});
		durationPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying aniamted names.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyNamePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyNamePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modifyNamePane.addButton(new Button(fillerPaneGItem), 3);
			modifyNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nName", "&7", "&7*Change the animated name line.", "&9&lName: &a" + ItemHandler.cutDelay(itemMap.getDynamicNames().get(position))), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NAME";
				placeHolders[15] = "&bUltimate Sword";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				List < String > names = itemMap.getDynamicNames();
				names.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(names.get(position))) + ">" + ChatColor.stripColor(event.getMessage()));
				itemMap.setDynamicNames(names)
				;String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NAME";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				modifyNamePane(event.getPlayer(), itemMap, position);
			}));
			modifyNamePane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
			StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicNames().get(position)))), event -> durationNamePane(player, itemMap, position, false, ItemHandler.cutDelay(itemMap.getDynamicNames().get(position)))));
			modifyNamePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated name."), event -> {
				List < String > names = itemMap.getDynamicNames();
				names.remove(position);
				itemMap.setDynamicNames(names);
				animatedNamePane(player, itemMap);
			}));
			modifyNamePane.addButton(new Button(fillerPaneGItem), 3);
			modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> animatedNamePane(player, itemMap)));
			modifyNamePane.addButton(new Button(fillerPaneBItem), 7);
			modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> animatedNamePane(player, itemMap)));
		});
		modifyNamePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated lore.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void animatedLorePane(final Player player, final ItemMap itemMap) {
		Interface animatedLorePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			animatedLorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
				if (!itemMap.getDynamicLores().isEmpty()) {
					itemMap.setAnimate(true);
					itemMap.setCustomLore(ItemHandler.cutDelay(itemMap.getDynamicLores().get(0)));
				}
				animationPane(player, itemMap);
			}));
			animatedLorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to be animated between."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATED LORE";
				placeHolders[15] = "&bThis is line 1, &cThis is line 2, &6This is line 3";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATED LORE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				durationLorePane(event.getPlayer(), itemMap, 0, true, ChatColor.stripColor(event.getMessage()));
			}));
			for (int i = 1; i <= itemMap.getDynamicLores().size(); i++) {
				final int k = i;
				animatedLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&fLore " + k, "&7", "&7*Click modify this animated lore.", "&9&lLore: &a" + ItemHandler.cutDelay(itemMap.getDynamicLores().get(k - 1)), 
						"&9&lAnimation Ticks: &a" + StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicLores().get(k - 1).get(0)))), event -> modifyLorePane(player, itemMap, k - 1)));
			}
		});
		animatedLorePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated lore duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durationLorePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedLorePane(player, itemMap)));
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATION DURATION";
				placeHolders[15] = "110";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ANIMATION DURATION";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					List < List < String >> lores = itemMap.getDynamicLores();
					if (isNew) {
						if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
							List < String > loreCut = itemMap.getCustomLore();
							loreCut.set(0, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + loreCut.get(0));
							lores.add(loreCut);
						}
						lores.add(StringUtils.split("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + value));
					} else {
						List < String > loreCut = ItemHandler.cutDelay(lores.get(position));
						loreCut.set(0, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + loreCut.get(0));
						lores.set(position, loreCut);
					}
					itemMap.setDynamicLores(lores);
					if (isNew) {
						animatedLorePane(player, itemMap);
					} else {
						modifyLorePane(player, itemMap, position);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					durationLorePane(player, itemMap, position, isNew, value);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
					List < List < String >> lores = itemMap.getDynamicLores();
					if (isNew) {
						if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
							List < String > loreCut = itemMap.getCustomLore();
							loreCut.set(0, "<delay:" + k + ">" + loreCut.get(0));
							lores.add(loreCut);
						}
						lores.add(StringUtils.split("<delay:" + k + ">" + value));
					} else {
						List < String > loreCut = ItemHandler.cutDelay(lores.get(position));
						loreCut.set(0, "<delay:" + k + ">" + loreCut.get(0));
						lores.set(position, loreCut);
					}
					itemMap.setDynamicLores(lores);
					if (isNew) {
						animatedLorePane(player, itemMap);
					} else {
						modifyLorePane(player, itemMap, position);
					}
				}));
			}
		});
		durationPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated lore.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyLorePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyLorePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&a&l&nLore", "&7", "&7*Change the animated lore line.", "&9&lLore: &a" + ItemHandler.cutDelay(itemMap.getDynamicLores().get(position))), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATED LORE";
				placeHolders[15] = "&bThis is line 1, &cThis is line 2, &6This is line 3";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				List < List < String >> lores = itemMap.getDynamicLores();
				lores.set(position, StringUtils.split("<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(lores.get(position).get(0))) + ">" + ChatColor.stripColor(event.getMessage())));
				itemMap.setDynamicLores(lores);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATED LORE";LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				modifyLorePane(event.getPlayer(), itemMap, position);
			}));
			modifyLorePane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
			StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicLores().get(position).get(0)))), event -> durationLorePane(player, itemMap, position, false, "")));
			modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated lore."), event -> {
				List < List < String >> lores = itemMap.getDynamicLores();
				lores.remove(position);
				itemMap.setDynamicLores(lores);
				animatedLorePane(player, itemMap);
			}));
			modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> animatedLorePane(player, itemMap)));
			modifyLorePane.addButton(new Button(fillerPaneBItem), 7);
			modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> animatedLorePane(player, itemMap)));
		});
		modifyLorePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying animated skull items.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void animatedSkullPane(final Player player, final ItemMap itemMap, boolean owner) {
		Interface animatedSkullPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (owner) {
				animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
					if (!itemMap.getDynamicOwners().isEmpty()) {
						itemMap.setAnimate(true);
						itemMap.setSkullTexture("");
						itemMap.setDynamicTextures(new ArrayList < String > ());
						itemMap.setSkull(ItemHandler.cutDelay(itemMap.getDynamicOwners().get(0)));
					}
					animationPane(player, itemMap);
				}));
				animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Skull Owner", "&7", "&7*Add a new skull owner", "&7to be animated between."), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL OWNER";
					placeHolders[15] = "RockinChaos";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					durationSkullPane(player, itemMap, 0, true, ChatColor.stripColor(event.getMessage()), owner);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL OWNER";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				}));
				for (int i = 1; i <= itemMap.getDynamicOwners().size(); i++) {
					final int k = i;
					animatedSkullPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, "&fSkull Owner " + k, "&7", "&7*Click modify this animated skull owner.", "&9&lSkull Owner: &a" + 
					ItemHandler.cutDelay(itemMap.getDynamicOwners().get(k - 1)), "&9&lAnimation Ticks: &a" + StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicOwners().get(k - 1)))), event -> modifySkullPane(player, itemMap, k - 1, owner)));
				}
			} else {
				animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
					if (!itemMap.getDynamicTextures().isEmpty()) {
						itemMap.setAnimate(true);
						itemMap.setSkull("");
						itemMap.setDynamicOwners(new ArrayList < String > ());
						itemMap.setSkullTexture(ItemHandler.cutDelay(itemMap.getDynamicTextures().get(0)));
					}
					animationPane(player, itemMap);
				}));
				animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Skull Texture", "&7", "&7*Add a new skull texture", "&7to be animated between."), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL TEXTURE";
					placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					durationSkullPane(player, itemMap, 0, true, ChatColor.stripColor(event.getMessage()), owner);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL TEXTURE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				}));
				for (int i = 1; i <= itemMap.getDynamicTextures().size(); i++) {
					final int k = i;
					animatedSkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&fSkull Texture " + k, "&7", "&7*Click modify this animated skull texture.", "&9&lSkull Texture: &a" + 
					(ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1)).length() > 40 ? ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1)).substring(0, 40) : ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1))),
					"&9&lAnimation Ticks: &a" + StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicTextures().get(k - 1)))), event -> modifySkullPane(player, itemMap, k - 1, owner)));
				}
			}
		});
		animatedSkullPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the skull animation duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durationSkullPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value, boolean owner) {
		Interface durationPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedSkullPane(player, itemMap, owner)));
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "ANIMATION DURATION";
				placeHolders[15] = "110";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "ANIMATION DURATION";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					List < String > skulls = itemMap.getDynamicOwners();
					if (!owner) {
						skulls = itemMap.getDynamicTextures();
					}
					if (isNew) {
						if (itemMap.getDynamicOwners().isEmpty() && owner && itemMap.getSkull() != null && !itemMap.getSkull().isEmpty()) {
							skulls.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + itemMap.getSkull());
						} else if (itemMap.getDynamicTextures().isEmpty() && !owner && itemMap.getSkullTexture() != null && !itemMap.getSkullTexture().isEmpty()) {
							skulls.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + itemMap.getSkullTexture());
						}
						skulls.add("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + value);
					} else {
						skulls.set(position, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + ItemHandler.cutDelay(skulls.get(position)));
					}
					if (owner) {
						itemMap.setDynamicOwners(skulls);
					} else {
						itemMap.setDynamicTextures(skulls);
					}
					if (isNew) {
						animatedSkullPane(player, itemMap, owner);
					} else {
						modifySkullPane(player, itemMap, position, owner);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					durationSkullPane(player, itemMap, position, isNew, value, owner);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
					List < String > skulls = itemMap.getDynamicOwners();
					if (!owner) {
						skulls = itemMap.getDynamicTextures();
					}
					if (isNew) {
						if (itemMap.getDynamicOwners().isEmpty() && owner && itemMap.getSkull() != null && !itemMap.getSkull().isEmpty()) {
							skulls.add("<delay:" + k + ">" + itemMap.getSkull());
						} else if (itemMap.getDynamicTextures().isEmpty() && !owner && itemMap.getSkullTexture() != null && !itemMap.getSkullTexture().isEmpty()) {
							skulls.add("<delay:" + k + ">" + itemMap.getSkullTexture());
						}
						skulls.add("<delay:" + k + ">" + value);
					} else {
						skulls.set(position, "<delay:" + k + ">" + ItemHandler.cutDelay(skulls.get(position)));
					}
					if (owner) {
						itemMap.setDynamicOwners(skulls);
					} else {
						itemMap.setDynamicTextures(skulls);
					}
					if (isNew) {
						animatedSkullPane(player, itemMap, owner);
					} else {
						modifySkullPane(player, itemMap, position, owner);
					}
				}));
			}
		});
		durationPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the skull owner or skull texture.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifySkullPane(final Player player, final ItemMap itemMap, final int position, boolean owner) {
		Interface modifySkullPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			modifySkullPane.addButton(new Button(fillerPaneGItem), 3);
			if (owner) {
				modifySkullPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, "&a&l&nSkull Owner", "&7", "&7*Change the animated skull owner.", "&9&lSkull Owner: &a" + 
			ItemHandler.cutDelay(itemMap.getDynamicOwners().get(position))), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL OWNER";
					placeHolders[15] = "RockinChaos";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					List < String > skulls = itemMap.getDynamicOwners();
					skulls.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(skulls.get(position))) + ">" + ChatColor.stripColor(event.getMessage()));
					itemMap.setDynamicOwners(skulls);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL OWNER";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					modifySkullPane(event.getPlayer(), itemMap, position, owner);
				}));
			} else {
				modifySkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&a&l&nSkull Texture", "&7", "&7*Change the animated skull texture.", "&9&lSkull Texture: &a" + 
			ItemHandler.cutDelay(itemMap.getDynamicTextures().get(position))), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL TEXTURE";
					placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					List < String > skulls = itemMap.getDynamicTextures();
					skulls.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(skulls.get(position))) + ">" + ChatColor.stripColor(event.getMessage()));
					itemMap.setDynamicTextures(skulls);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL TEXTURE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					modifySkullPane(event.getPlayer(), itemMap, position, owner);
				}));
			}
			modifySkullPane.addButton(new Button(ItemHandler.getItem("347", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
			StringUtils.returnInteger(ItemHandler.getDelayFormat((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))))), 
					event -> durationSkullPane(player, itemMap, position, false, ItemHandler.cutDelay((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))), owner)));
			modifySkullPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated skull " + (owner ? "owner." : "texture.")), event -> {
				List < String > skulls = itemMap.getDynamicOwners();
				if (!owner) {
					skulls = itemMap.getDynamicTextures();
				}
				skulls.remove(position);
				if (owner) {
					itemMap.setDynamicOwners(skulls);
				} else {
					itemMap.setDynamicTextures(skulls);
				}
				animatedSkullPane(player, itemMap, owner);
			}));
			modifySkullPane.addButton(new Button(fillerPaneGItem), 3);
			modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> animatedSkullPane(player, itemMap, owner)));
			modifySkullPane.addButton(new Button(fillerPaneBItem), 7);
			modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> animatedSkullPane(player, itemMap, owner)));
		});
		modifySkullPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying item animations.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void animationPane(final Player player, final ItemMap itemMap) {
		Interface animationPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
				animationPane.addButton(new Button(fillerPaneGItem), 2);
			} else {
				animationPane.addButton(new Button(fillerPaneGItem), 3);
			}
			animationPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString(), 1, false, "&c&l&nMaterial", "&7", "&7*Add additional material types", "&7to have the item change between.", "&9&lAnimated Materials: &a" + 
			(StringUtils.nullCheck(itemMap.getDynamicMaterials() + "") != "NONE" ? "YES" : "NONE")), event -> animateMaterialPane(player, itemMap)));
			animationPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nName", "&7", "&7*Add additional custom names", "&7to have the item change between.", "&9&lAnimated Names: &a" + 
			(StringUtils.nullCheck(itemMap.getDynamicNames() + "") != "NONE" ? "YES" : "NONE")), event -> animatedNamePane(player, itemMap)));
			animationPane.addButton(new Button(ItemHandler.getItem("386", 1, false, "&b&l&nLore", "&7", "&7*Add additional custom lores", "&7to have the item change between.", "&9&lAnimated Lores: &a" + 
			(StringUtils.nullCheck(itemMap.getDynamicLores() + "") != "NONE" ? "YES" : "NONE")), event -> animatedLorePane(player, itemMap)));
			if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
				animationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, "&a&lSkull Owner", "&7", "&7*Add additional skull owners", "&7to have the item change between.", "&7", "&7You can only define skull owner", 
						"&7or skull texture, this will", "&7remove any skull textures.", "&9&lAnimated Owners: &a" + (StringUtils.nullCheck(itemMap.getDynamicOwners() + "") != "NONE" ? "YES" : "NONE")), event -> animatedSkullPane(player, itemMap, true)));
				animationPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&b&lSkull Texture", "&7", "&7*Add additional skull textures", "&7to have the item change between.", "&7", "&7You can only define skull texture", 
						"&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like &aminecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.", 
						"&9&lAnimated Textures: &a" + (StringUtils.nullCheck(itemMap.getDynamicTextures() + "") != "NONE" ? "YES" : "NONE")), event -> animatedSkullPane(player, itemMap, false)));
				animationPane.addButton(new Button(fillerPaneGItem), 2);
			} else {
				animationPane.addButton(new Button(fillerPaneGItem), 3);
			}
			animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			animationPane.addButton(new Button(fillerPaneBItem), 7);
			animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		animationPane.open(player);
	}

//  ===========================================================================================================================================================================================================================================================
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the Limit Gamemodes.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void limitPane(final Player player, final ItemMap itemMap) {
		Interface limitPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			List < String > limitModes = new ArrayList < String > ();
			if (StringUtils.containsIgnoreCase(itemMap.getLimitModes(), "ADVENTURE")) {
				limitModes.add("ADVENTURE");
			}
			if (StringUtils.containsIgnoreCase(itemMap.getLimitModes(), "SURVIVAL")) {
				limitModes.add("SURVIVAL");
			}
			if (StringUtils.containsIgnoreCase(itemMap.getLimitModes(), "CREATIVE")) {
				limitModes.add("CREATIVE");
			}
			limitPane.addButton(new Button(fillerPaneGItem), 3);
			limitPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "MAP"), 1, limitModes.contains("ADVENTURE"), "&a&l&nADVENTURE", "&7", "&7*Limits the item to ADVENTURE mode.", "&9&lENABLED: &a" + 
			(limitModes.contains("ADVENTURE") + "").toUpperCase()), event -> {
				if (limitModes.contains("ADVENTURE")) {
					limitModes.remove("ADVENTURE");
				} else {
					limitModes.add("ADVENTURE");
				}
				itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));limitPane(player, itemMap);
			}));
			limitPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, limitModes.contains("SURVIVAL"), "&b&l&nSURVIVAL", "&7", "&7*Limits the item to SURVIVAL mode.", "&9&lENABLED: &a" + (limitModes.contains("SURVIVAL") + "").toUpperCase()), 
					event -> {
				if (limitModes.contains("SURVIVAL")) {
					limitModes.remove("SURVIVAL");
				} else {
					limitModes.add("SURVIVAL");
				}
				itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));limitPane(player, itemMap);
			}));
			limitPane.addButton(new Button(ItemHandler.getItem(limitModes.contains("CREATIVE") ? (ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1") : "GOLDEN_APPLE", 1, false, "&6&l&nCREATIVE", "&7", "&7*Limits the item to CREATIVE mode.", "&9&lENABLED: &a" + 
			(limitModes.contains("CREATIVE") + "").toUpperCase()), event -> {
				if (limitModes.contains("CREATIVE")) {
					limitModes.remove("CREATIVE");
				} else {
					limitModes.add("CREATIVE");
				}
				itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));limitPane(player, itemMap);
			}));
			limitPane.addButton(new Button(fillerPaneGItem), 3);
			limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			limitPane.addButton(new Button(fillerPaneBItem), 7);
			limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		limitPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the item probability.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void probabilityPane(final Player player, final ItemMap itemMap) {
		Interface probabilityPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			probabilityPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			for (int i = 1; i < 100; i++) {
				final int k = i;
				probabilityPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", 1, false, "&9&lChance: &a&l" + k + "%", "&7", "&7*Click to set the", "&7probability of the item."), event -> {
					itemMap.setProbability(k);creatingPane(player, itemMap);
				}));
			}
		});
		probabilityPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the use-cooldown.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void usePane(final Player player, final ItemMap itemMap) {
		Interface usePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			usePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				creatingPane(player, itemMap);
			}));
			usePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Usage", "&7", "&7*Click to set a custom usage cooldown", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "USAGE COOLDOWN";
				placeHolders[15] = "120";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setInteractCooldown(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "USAGE COOLDOWN";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				creatingPane(event.getPlayer(), itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				usePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7use-cooldown of the item."), event -> {
					itemMap.setInteractCooldown(k);
					creatingPane(player, itemMap);
				}));
			}
		});
		usePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the drop chances.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void dropsPane(final Player player, final ItemMap itemMap) {
		Interface dropsPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			String mobs = "";
			String blocks = "";
			for (EntityType entity: itemMap.getMobsDrop().keySet()) { mobs += entity.name() + ", "; }
			for (Material material: itemMap.getBlocksDrop().keySet()) { blocks += material.name() + ", "; }
			dropsPane.addButton(new Button(fillerPaneBItem), 12);
			dropsPane.addButton(new Button(ItemHandler.getItem("383:54", 1, false, "&b&lMobs Drop", "&7", "&7*Define mobs that are", "&7allowed to drop the item.", (!mobs.isEmpty() ? "&9&lMobs: &a" + mobs.substring(0, mobs.length() - 2) : "")), event -> {
				mobsPane(player, itemMap);
			}));
			dropsPane.addButton(new Button(fillerPaneBItem));
			dropsPane.addButton(new Button(ItemHandler.getItem("DIAMOND_ORE", 1, false, "&b&lBlocks Drop", "&7", "&7*Define blocks that are", "&7allowed to drop the item.", (!blocks.isEmpty() ? "&9&lBlocks: &a" + blocks.substring(0, blocks.length() - 2) : "")), event -> {
				blocksPane(player, itemMap);
			}));
			dropsPane.addButton(new Button(fillerPaneBItem), 3);
			dropsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			dropsPane.addButton(new Button(fillerPaneBItem), 7);
			dropsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		dropsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the mobs drop chances.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void mobsPane(final Player player, final ItemMap itemMap) {
		Interface dropsPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			dropsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the drop chances menu."), event -> dropsPane(player, itemMap)));
			for (EntityType entity: EntityType.values()) {
				if (itemMap.getMobsDrop().containsKey(entity) && entity.isAlive()) {
					dropsPane.addButton(new Button(ItemHandler.getItem("EGG", 1, (itemMap.getMobsDrop().containsKey(entity)), "&f" + entity.name(), "&7", "&7*Click to add this as", "&7a banner pattern.", 
							(itemMap.getMobsDrop().containsKey(entity) ? "&9&lChance: &a" + itemMap.getMobsDrop().get(entity) : "")), event -> {
						if (itemMap.getMobsDrop().containsKey(entity)) {
							Map<EntityType, Double> mobsDrop = itemMap.getMobsDrop();
							mobsDrop.remove(entity);
							itemMap.setMobsDrop(mobsDrop);
							mobsPane(player, itemMap);
						} else {
							chancePane(player, itemMap, entity, null);
						}
					}));
				}
			}
			for (EntityType entity: EntityType.values()) {
				if (!itemMap.getMobsDrop().containsKey(entity) && entity.isAlive()) {
					dropsPane.addButton(new Button(ItemHandler.getItem("EGG", 1, (itemMap.getMobsDrop().containsKey(entity)), "&f" + entity.name(), "&7", "&7*Click to add this as", "&7a banner pattern.", 
							(itemMap.getMobsDrop().containsKey(entity) ? "&9&lChance: &a" + itemMap.getMobsDrop().get(entity) : "")), event -> {
						if (itemMap.getMobsDrop().containsKey(entity)) {
							Map<EntityType, Double> mobsDrop = itemMap.getMobsDrop();
							mobsDrop.remove(entity);
							itemMap.setMobsDrop(mobsDrop);
							mobsPane(player, itemMap);
						} else {
							chancePane(player, itemMap, entity, null);
						}
					}));
				}
			}
		});
		dropsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the blocks drop chances.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void blocksPane(final Player player, final ItemMap itemMap) {
		Interface blockPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			blockPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the drop chances menu."), event -> {
				dropsPane(player, itemMap);
			}));
			blockPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "BUKKIT MATERIAL";
				placeHolders[15] = "IRON_SWORD";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != null) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "BUKKIT MATERIAL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					chancePane(player, itemMap, null, ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null));
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noMaterial", player, placeHolders);
					blocksPane(player, itemMap);
				}
			}));
			Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
			for (Material material: Material.values()) {
				if (material.isBlock() && itemMap.getBlocksDrop().containsKey(material)) {
					if (!material.name().contains("LEGACY") && material.name() != "AIR" && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
						if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
							for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
								if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
									final int dataValue = i;
									blockPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, (itemMap.getBlocksDrop().containsKey(material)), "", "&7", "&7*Click to set the material.",
											(itemMap.getBlocksDrop().containsKey(material) ? "&9&lChance: &a" + itemMap.getBlocksDrop().get(material) : "")), event -> {
										if (itemMap.getBlocksDrop().containsKey(material)) {
											Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
											blocksDrop.remove(material);
											itemMap.setBlocksDrop(blocksDrop);
											blocksPane(player, itemMap);
										} else {
											chancePane(player, itemMap, null, material);
										}
									}));
								}
							}
						} else {
						blockPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, (itemMap.getBlocksDrop().containsKey(material)), "", "&7", "&7*Click to set the material.",
								(itemMap.getBlocksDrop().containsKey(material) ? "&9&lChance: &a" + itemMap.getBlocksDrop().get(material) : "")), event -> {
							if (itemMap.getBlocksDrop().containsKey(material)) {
								Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
								blocksDrop.remove(material);
								itemMap.setBlocksDrop(blocksDrop);
								blocksPane(player, itemMap);
							} else {
								chancePane(player, itemMap, null, material);
							}
						}));
						}
					}
				}
			}
			for (Material material: Material.values()) {
				if (material.isBlock() && !itemMap.getBlocksDrop().containsKey(material)) {
					if (!material.name().contains("LEGACY") && material.name() != "AIR" && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
						if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
							for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
								if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
									final int dataValue = i;
									blockPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, (itemMap.getBlocksDrop().containsKey(material)), "", "&7", "&7*Click to set the material.",
											(itemMap.getBlocksDrop().containsKey(material) ? "&9&lChance: &a" + itemMap.getBlocksDrop().get(material) : "")), event -> {
										if (itemMap.getBlocksDrop().containsKey(material)) {
											Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
											blocksDrop.remove(material);
											itemMap.setBlocksDrop(blocksDrop);
											blocksPane(player, itemMap);
										} else {
											chancePane(player, itemMap, null, material);
										}
									}));
								}
							}
						} else {
						blockPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, (itemMap.getBlocksDrop().containsKey(material)), "", "&7", "&7*Click to set the material.",
								(itemMap.getBlocksDrop().containsKey(material) ? "&9&lChance: &a" + itemMap.getBlocksDrop().get(material) : "")), event -> {
							if (itemMap.getBlocksDrop().containsKey(material)) {
								Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
								blocksDrop.remove(material);
								itemMap.setBlocksDrop(blocksDrop);
								blocksPane(player, itemMap);
							} else {
								chancePane(player, itemMap, null, material);
							}
						}));
						}
					}
				}
			}
			inventoryCheck.clear();
		});
		blockPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting an items drop chances.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param entity - The Entity selected.
    */
	private static void chancePane(final Player player, final ItemMap itemMap, final EntityType entity, final Material material) {
		Interface chancePane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (entity != null) {
				chancePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the mobs drop menu."), event -> {
					mobsPane(player, itemMap);
				}));
			} else {
				chancePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the blocks drop menu."), event -> {
					blocksPane(player, itemMap);
				}));
			}
			chancePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, "&e&lCustom Drop Chance", "&7", "&7*Click to set a custom drop chance", "&7value for the item."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "DROP CHANCE";
				placeHolders[15] = "0.001";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isDouble(ChatColor.stripColor(event.getMessage()))) {
					if (entity != null) { 
						Map<EntityType, Double> mobsDrop = itemMap.getMobsDrop();
						mobsDrop.put(entity, Double.parseDouble(ChatColor.stripColor(event.getMessage())));
						itemMap.setMobsDrop(mobsDrop);
					} else {
						Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
						blocksDrop.put(material, Double.parseDouble(ChatColor.stripColor(event.getMessage())));
						itemMap.setBlocksDrop(blocksDrop);
					}
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "DROP CHANCE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				if (entity != null) { mobsPane(event.getPlayer(), itemMap); }
				else { blocksPane(event.getPlayer(), itemMap); }
			}));
			for (double i = 0.01; i < 1; i += 0.01) {
				final double k = Double.parseDouble(new DecimalFormat("#.##")
						.format(
								Double.parseDouble(Double.toString(i).replace(",", ".")))
						.replace(",", "."));
				chancePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:10", 1, false, "&9&lCost: &a$&l" + k, "&7", "&7*Click to set the", "&7drop chance of the item."), event -> {
					if (entity != null) { 
						Map<EntityType, Double> mobsDrop = itemMap.getMobsDrop();
						mobsDrop.put(entity, k);
						itemMap.setMobsDrop(mobsDrop);
						mobsPane(player, itemMap);
					} else {
						Map<Material, Double> blocksDrop = itemMap.getBlocksDrop();
						blocksDrop.put(material, k);
						itemMap.setBlocksDrop(blocksDrop);
						blocksPane(player, itemMap);
					}
				}));
			}
		});
		chancePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the item conditions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void conditionsPane(final Player player, final ItemMap itemMap) {
		Interface conditionsPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			conditionsPane.addButton(new Button(fillerPaneBItem), 3);
			conditionsPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, "&b&l&nCommand&b&l Conditions", "&7", "&7*Condition(s) that must be met", 
					"&7in order to execute item commands.", "&7", "&9&lENABLED: " + String.valueOf((StringUtils.nullCheck(itemMap.getCommandConditions() + "") != "NONE")).toUpperCase()), event -> commandActionPane(player, itemMap)));
			conditionsPane.addButton(new Button(ItemHandler.getItem("CACTUS", 1, false, "&b&l&nDisposable&b&l Conditions", "&7", "&7*Condition(s) that must be met", "&7in order for the disposable", "&7itemflag to function.",
					"&7", "&c&l&nNOTE:&7 The disposable itemflag", "&7must be defined for this", "&7condition to function.",
					"&7", "&9&lENABLED: " + String.valueOf((StringUtils.nullCheck(itemMap.getDisposableConditions() + "") != "NONE")).toUpperCase()), event -> disposableCPane(player, itemMap)));
			conditionsPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&b&l&nTrigger&b&l Conditions", "&7", "&7*Condition(s) that must be met", "&7in order to to receive the", 
					"&7item when performing a trigger.", "&7", "&9&lENABLED: " + String.valueOf((StringUtils.nullCheck(itemMap.getTriggerConditions() + "") != "NONE")).toUpperCase()), event -> triggerCPane(player, itemMap)));
			conditionsPane.addButton(new Button(fillerPaneBItem), 3);
			conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			conditionsPane.addButton(new Button(fillerPaneBItem), 7);
			conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		conditionsPane.open(player);
	}

   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the command action conditions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void commandActionPane(final Player player, final ItemMap itemMap) {
		Interface commandPane = new Interface(false, 5, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			commandPane.addButton(new Button(fillerPaneGItem), 2);
			commandPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "324" : "64", 1, false, "&e&lInteract", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_ALL.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_ALL);
			}));
			commandPane.addButton(new Button(fillerPaneGItem));
			commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&e&lInventory", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_ALL.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_ALL);
			}));
			commandPane.addButton(new Button(fillerPaneGItem));
			commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, "&e&lPhysical", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.PHYSICAL.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.PHYSICAL);
			}));
			commandPane.addButton(new Button(fillerPaneGItem), 2);
			commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, "&e&lOn-Equip", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_EQUIP.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_EQUIP);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, "&e&lUn-Equip", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.UN_EQUIP.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.UN_EQUIP);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, "&e&lOn-Hold", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_HOLD.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_HOLD);
			}));
			commandPane.addButton(new Button(fillerPaneGItem));
			commandPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&e&lOn-Fire", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_FIRE.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_FIRE);
			}));
			commandPane.addButton(new Button(fillerPaneGItem));
			commandPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lOn-Consume", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_CONSUME.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_CONSUME);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&e&lOn-Receive", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_RECEIVE.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_RECEIVE);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("397", 1, false, "&e&lOn-Death", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_DEATH.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_DEATH);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("20", 1, false, "&e&lInteract-Air", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_AIR.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_AIR);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("95:3", 1, false, "&e&lInteract-Air-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_AIR.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_LEFT_AIR);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("95:6", 1, false, "&e&lInteract-Air-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_AIR.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_RIGHT_AIR);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("2", 1, false, "&e&lInteract-Block", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_BLOCK.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_BLOCK);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("1", 1, false, "&e&lInteract-Block-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_BLOCK.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_LEFT_BLOCK);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("4", 1, false, "&e&lInteract-Block-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_BLOCK.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_RIGHT_BLOCK);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("330", 1, false, "&e&lInteract-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_ALL.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_LEFT_ALL);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_8") ? "324" : "64", 1, false, "&e&lInteract-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_ALL.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INTERACT_RIGHT_ALL);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SWAP_CURSOR.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_SWAP_CURSOR);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, "&e&lInventory-Middle", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_MIDDLE.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_MIDDLE);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, "&e&lInventory-Creative", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_CREATIVE.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_CREATIVE);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, "&e&lInventory-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_LEFT.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_LEFT);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&e&lInventory-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_RIGHT.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_RIGHT);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("44", 2, false, "&e&lInventory-Shift-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SHIFT_LEFT.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_SHIFT_LEFT);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("44:3", 2, false, "&e&lInventory-Shift-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SHIFT_RIGHT.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.INVENTORY_SHIFT_RIGHT);
			}));
			commandPane.addButton(new Button(fillerPaneGItem));
			commandPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, "&e&lOn-Damage", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_DAMAGE.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_DAMAGE);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, "&e&lOn-Hit", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
					"&7", "&9&lENABLED: &a" + (StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_HIT.config()) + "") != "NONE" ? "YES" : "NO")), event -> {
				commandCPane(player, itemMap, Action.ON_HIT);
			}));
			commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
			commandPane.addButton(new Button(fillerPaneBItem), 7);
			commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
		});
		commandPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the commands conditions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param commandAction - The command action being referenced.
    */
	private static void commandCPane(final Player player, final ItemMap itemMap, final Action commandAction) {
		Interface conditionsPane = new Interface(true, 2, GUIName, player);
		conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command actions menu."), event -> commandActionPane(player, itemMap)));
		SchedulerUtils.runAsync(() -> {
			final String commandIdent = commandAction.config().replace("-", " ").replace(".", "");
			conditionsPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (StringUtils.nullCheck(itemMap.getCommandMessages().get(commandAction.config())) != "NONE"), "&c&l" + String.valueOf(commandIdent.charAt(0)).toUpperCase() + commandIdent.substring(1) + " Fail Message", "&7", "&7*An optional message to be", "&7sent when the player does not", "&7meet the commands conditions.",
			"&7", "&9&lMESSAGE: &a" + (StringUtils.nullCheck(itemMap.getCommandMessages().get(commandAction.config())) != "NONE" ? itemMap.getCommandMessages().get(commandAction.config()) : "NONE")),
			event -> {
				if (StringUtils.nullCheck(itemMap.getCommandMessages().get(commandAction.config())) != "NONE") {
					Map<String, String> messages = itemMap.getCommandMessages();
					messages.put(commandAction.config(), null);
					itemMap.setCommandMessages(messages);
					commandCPane(player, itemMap, commandAction);
				} else {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "COMMAND FAIL MESSAGE";
					placeHolders[15] = "&cYou do not meet the conditions to execute this item command.";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "COMMAND FAIL MESSAGE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				Map<String, String> messages = itemMap.getCommandMessages();
				messages.put(commandAction.config(), ChatColor.stripColor(event.getMessage()));
				itemMap.setCommandMessages(messages);
				commandCPane(player, itemMap, commandAction);
			}));
			conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met", 
					"&7in order to execute the", "&7" + commandAction.config().replace("-", " ").replace(".", "") + " item commands."), 			
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				placeHolders[15] = "100";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				addConditionPane(event.getPlayer(), itemMap, commandAction, commandAction.config(), ChatColor.stripColor(event.getMessage()));
			}));
			if (itemMap.getCommandConditions().get(commandAction.config()) != null) {
				for (String condition: itemMap.getCommandConditions().get(commandAction.config())) {
					conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&f" + condition, "&7", "&7*Click to remove this condition."), 
					event -> {
						Map<String, List<String>> commands = itemMap.getCommandConditions();
						List < String > conditions = commands.get(commandAction.config());
						conditions.remove(condition);
						commands.put(commandAction.config(), conditions);
						itemMap.setCommandConditions(commands);
						commandCPane(player, itemMap, commandAction);
					}));
				}
			}
		});
		conditionsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the disposable conditions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void disposableCPane(final Player player, final ItemMap itemMap) {
		Interface conditionsPane = new Interface(true, 2, GUIName, player);
		conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
		SchedulerUtils.runAsync(() -> {
			conditionsPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (StringUtils.nullCheck(itemMap.getDisposableMessage()) != "NONE"), "&c&lDisposable Fail Message", "&7", "&7*An optional message to be", "&7sent when the player does not", "&7meet the disposable conditions.",
			"&7", "&9&lMESSAGE: &a" + (StringUtils.nullCheck(itemMap.getDisposableMessage()) != "NONE" ? itemMap.getDisposableMessage() : "NONE")),
			event -> {
				if (StringUtils.nullCheck(itemMap.getDisposableMessage()) != "NONE") {
					itemMap.setDisposableMessage(null);
					disposableCPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "DISPOSABLE FAIL MESSAGE";
					placeHolders[15] = "&cYou do not meet the conditions to dispose of this item.";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setDisposableMessage(ChatColor.stripColor(event.getMessage()));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "DISPOSABLE FAIL MESSAGE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				disposableCPane(event.getPlayer(), itemMap);
			}));
			conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met", "&7in order to dispose of the item."), 			
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				placeHolders[15] = "100";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				addConditionPane(event.getPlayer(), itemMap, null, "disposable-conditions", ChatColor.stripColor(event.getMessage()));
			}));
			for (String condition: itemMap.getDisposableConditions()) {
				conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&f" + condition, "&7", "&7*Click to remove this condition."), 
				event -> {
					List < String > conditions = itemMap.getDisposableConditions();
					conditions.remove(condition);
					itemMap.setDisposableConditions(conditions);
					disposableCPane(player, itemMap);
				}));
			}
		});
		conditionsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the trigger conditions.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void triggerCPane(final Player player, final ItemMap itemMap) {
		Interface conditionsPane = new Interface(true, 2, GUIName, player);
		conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
		SchedulerUtils.runAsync(() -> {
			conditionsPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (StringUtils.nullCheck(itemMap.getTriggerMessage()) != "NONE"), "&c&lTrigger Fail Message", "&7", "&7*An optional message to be", "&7sent when the player does not", "&7meet the trigger conditions.",
			"&7", "&9&lMESSAGE: &a" + (StringUtils.nullCheck(itemMap.getTriggerMessage()) != "NONE" ? itemMap.getTriggerMessage() : "NONE")),
			event -> {
				if (StringUtils.nullCheck(itemMap.getTriggerMessage()) != "NONE") {
					itemMap.setTriggerMessage(null);
					triggerCPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "TRIGGER FAIL MESSAGE";
					placeHolders[15] = "&cYou do not meet the conditions to receive this item.";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setTriggerMessage(ChatColor.stripColor(event.getMessage()));
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "TRIGGER FAIL MESSAGE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				triggerCPane(event.getPlayer(), itemMap);
			}));
			conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met", "&7in order to receive the item."), 			
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				placeHolders[15] = "100";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIRST VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				addConditionPane(event.getPlayer(), itemMap, null, "trigger-conditions", ChatColor.stripColor(event.getMessage()));
			}));
			for (String condition: itemMap.getTriggerConditions()) {
				conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&f" + condition, "&7", "&7*Click to remove this condition."), 
				event -> {
					List < String > conditions = itemMap.getTriggerConditions();
					conditions.remove(condition);
					itemMap.setTriggerConditions(conditions);
					triggerCPane(player, itemMap);
				}));
			}
		});
		conditionsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for adding the item condition.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param condition - The condition currently being modified.
    */
	private static void addConditionPane(final Player player, final ItemMap itemMap, final Action commandAction, final String condition1, final String value1) {
		Interface conditionsPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			conditionsPane.addButton(new Button(fillerPaneBItem));
			conditionsPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, false, "&b&lEQUAL", "&7", "&7*The first value must be", "&7EQUAL to the second value", "&7for the condition to be met."),			
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				placeHolders[15] = "400";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				List < String > conditions = (condition1.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition1.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition1) != null ? itemMap.getCommandConditions().get(condition1) : new ArrayList < String > ());
				conditions.add(value1 + ":" + "EQUAL" + ":" + ChatColor.stripColor(event.getMessage()));
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					itemMap.setDisposableConditions(conditions);
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					itemMap.setTriggerConditions(conditions);
					triggerCPane(player, itemMap);
				} else {
					Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
					conditions2.put(condition1.replace("-conditions", ""), conditions);
					itemMap.setCommandConditions(conditions2);
					commandCPane(player, itemMap, commandAction);
				}
			}));
			conditionsPane.addButton(new Button(fillerPaneBItem));
			conditionsPane.addButton(new Button(ItemHandler.getItem("85", 1, false, "&b&lNOTEQUAL", "&7", "&7*The first value must be", "&7NOTEQUAL to the second value", "&7for the condition to be met."), 
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				placeHolders[15] = "400";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				List < String > conditions = (condition1.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition1.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition1) != null ? itemMap.getCommandConditions().get(condition1) : new ArrayList < String > ());
				conditions.add(value1 + ":" + "NOTEQUAL" + ":" + ChatColor.stripColor(event.getMessage()));
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					itemMap.setDisposableConditions(conditions);
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					itemMap.setTriggerConditions(conditions);
					triggerCPane(player, itemMap);
				} else {
					Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
					conditions2.put(condition1.replace("-conditions", ""), conditions);
					itemMap.setCommandConditions(conditions2);
					commandCPane(player, itemMap, commandAction);
				}
			}));
			conditionsPane.addButton(new Button(fillerPaneBItem));
			conditionsPane.addButton(new Button(ItemHandler.getItem("53", 1, false, "&b&lOVER", "&7", "&7*The first value must be", "&7OVER the second value", "&7for the condition to be met.", "&7", "&c&l&nNOTE:&7 This only works if both", "&7values referenced are integers."), 
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				placeHolders[15] = "400";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				List < String > conditions = (condition1.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition1.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition1) != null ? itemMap.getCommandConditions().get(condition1) : new ArrayList < String > ());
				conditions.add(value1 + ":" + "OVER" + ":" + ChatColor.stripColor(event.getMessage()));
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					itemMap.setDisposableConditions(conditions);
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					itemMap.setTriggerConditions(conditions);
					triggerCPane(player, itemMap);
				} else {
					Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
					conditions2.put(condition1.replace("-conditions", ""), conditions);
					itemMap.setCommandConditions(conditions2);
					commandCPane(player, itemMap, commandAction);
				}
			}));
			conditionsPane.addButton(new Button(fillerPaneBItem));
			conditionsPane.addButton(new Button(ItemHandler.getItem("333", 1, false, "&b&lUNDER", "&7", "&7*The first value must be", "&7UNDER to the second value", "&7for the condition to be met.", "&7", "&c&l&nNOTE:&7 This only works if both", "&7values referenced are integers."), 
			event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				placeHolders[15] = "400";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "SECOND VALUE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				List < String > conditions = (condition1.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition1.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition1) != null ? itemMap.getCommandConditions().get(condition1) : new ArrayList < String > ());
				conditions.add(value1 + ":" + "UNDER" + ":" + ChatColor.stripColor(event.getMessage()));
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					itemMap.setDisposableConditions(conditions);
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					itemMap.setTriggerConditions(conditions);
					triggerCPane(player, itemMap);
				} else {
					Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
					conditions2.put(condition1.replace("-conditions", ""), conditions);
					itemMap.setCommandConditions(conditions2);
					commandCPane(player, itemMap, commandAction);
				}
			}));
			conditionsPane.addButton(new Button(fillerPaneBItem));
			conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the " + condition1.replace("-", " ").replace(".", "") + " condition menu."), 
			event -> { 
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					triggerCPane(player, itemMap);
				} else {
					commandCPane(player, itemMap, commandAction);
				}
			}));
			conditionsPane.addButton(new Button(fillerPaneBItem), 7);
			conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the " + condition1.replace("-", " ").replace(".", "") + " condition menu."), 
			event -> { 
				if (condition1.equalsIgnoreCase("disposable-conditions")) {
					disposableCPane(player, itemMap);
				} else if (condition1.equalsIgnoreCase("trigger-conditions")) {
					triggerCPane(player, itemMap);
				} else {
					commandCPane(player, itemMap, commandAction);
				}
			}));
		});
		conditionsPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the custom recipe.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void recipePane(final Player player, final ItemMap itemMap) {
		Interface recipePane = new Interface(false, 4, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			recipePane.addButton(new Button(fillerPaneBItem), 3);
			for (int i = 0; i < 9; i++) {
				final int k = i;
				String stack = "CHEST";
				ItemStack stack1 = null;
				if (itemMap.getRecipe().size() > i && itemMap.getRecipe().get(i) != 'X') {
					final ItemMap copyMap = ItemUtilities.getUtilities().getItemMap(null, itemMap.getIngredients().get(itemMap.getRecipe().get(i)).toString(), null);
					if (copyMap != null) { stack1 = copyMap.getItemStack(player); }
					else { stack = itemMap.getIngredients().get(itemMap.getRecipe().get(i)).toString(); }
				}
				if (stack1 != null) {
					stack1 = ItemHandler.addLore(stack1, "&9&lDISPLAY: &f" + stack1.getItemMeta().getDisplayName(), "&7", "&7*Create a recipe that can be used.");
					ItemMeta meta = stack1.getItemMeta();
					meta.setDisplayName(StringUtils.translateLayout((itemMap.getRecipe().size() > i ? "&e&l" + itemMap.getRecipe().get(i): "&e&lX"), player));
					stack1.setItemMeta(meta);
					
					recipePane.addButton(new Button(stack1, event -> {
						if ((itemMap.getRecipe().size() > k && itemMap.getRecipe().get(k) != 'X')) { setIngredients(player, itemMap, "AIR", k); } 
						else { ingredientPane(player, itemMap, k); }
					}));
					if (i == 2) {
						recipePane.addButton(new Button(fillerPaneBItem), 6);
					} else if (i == 5) {
						recipePane.addButton(new Button(fillerPaneBItem));
						recipePane.addButton(new Button(headerStack(player, itemMap)));
						recipePane.addButton(new Button(fillerPaneBItem), 4);
					} else if (i == 8) {
						recipePane.addButton(new Button(fillerPaneBItem), 3);
					}
				} else {
					recipePane.addButton(new Button(ItemHandler.getItem(stack, 1, false, 
							(itemMap.getRecipe().size() > i ? "&e&l" + itemMap.getRecipe().get(i): "&e&lX"), "&7", "&7*Create a recipe that can be used."), event -> {
						if ((itemMap.getRecipe().size() > k && itemMap.getRecipe().get(k) != 'X')) { setIngredients(player, itemMap, "AIR", k); } 
						else { ingredientPane(player, itemMap, k); }
					}));
					if (i == 2) {
						recipePane.addButton(new Button(fillerPaneBItem), 6);
					} else if (i == 5) {
						recipePane.addButton(new Button(fillerPaneBItem));
						recipePane.addButton(new Button(headerStack(player, itemMap)));
						recipePane.addButton(new Button(fillerPaneBItem), 4);
					} else if (i == 8) {
						recipePane.addButton(new Button(fillerPaneBItem), 3);
					}
				}
			}
			recipePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			recipePane.addButton(new Button(fillerPaneBItem), 7);
			recipePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		recipePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the custom recipe.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void ingredientPane(final Player player, final ItemMap itemMap, final int k) {
		Interface ingredientPane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			ingredientPane.addButton(new Button(fillerPaneBItem), 3);
			ingredientPane.addButton(new Button(ItemHandler.getItem("2", 1, false, "&b&lMaterial", "&7", "&7*Select a material type", "&7to be defined in the recipe."), event -> materialPane(player, itemMap, 3, k)));
			ingredientPane.addButton(new Button(fillerPaneBItem));
			ingredientPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&b&lCustom Item", "&7", "&7*Select a custom item", "&7to be defined in the recipe."), event -> startModify(player, itemMap, k)));
			ingredientPane.addButton(new Button(fillerPaneBItem), 3);
			ingredientPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> recipePane(player, itemMap)));
			ingredientPane.addButton(new Button(fillerPaneBItem), 7);
			ingredientPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> recipePane(player, itemMap)));
		});
		ingredientPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for creating NBT Properties.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void nbtPane(final Player player, final ItemMap itemMap) {
		Interface nbtPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			Map<String, String> properties = itemMap.getNBTValues();
			nbtPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
				nbtPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, true, "&e&l&nNew Property", "&7", "&7*Add a new NBT Property to the custom item."), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "NBT PROPERTY";
					placeHolders[15] = "TranslatableDisplayName:&aUltra &cItem";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					if (ChatColor.stripColor(event.getMessage()).contains(":")) {
						String[] propertyParts = ChatColor.stripColor(event.getMessage()).split(":");
						properties.put(propertyParts[0], propertyParts[1]);
					}
					itemMap.setNBTValues(properties);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "NBT PROPERTY";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					nbtPane(event.getPlayer(), itemMap);
				}));
				for (String key : properties.keySet()) {
					nbtPane.addButton(new Button(ItemHandler.getItem("137", 1, false, "&f" + key + ":" + properties.get(key), "&7", "&7*Click to modify or delete", "&7this custom NBT Property."), event -> modifyProperty(player, itemMap, key)));
				}
		});
		nbtPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying NBT Propterties.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyProperty(final Player player, final ItemMap itemMap, final String key) {
		Interface modifyProperty = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			Map<String, String> properties = itemMap.getNBTValues();
			modifyProperty.addButton(new Button(fillerPaneGItem), 3);
			modifyProperty.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&c&l&nModify", "&7", "&7*Modify this NBT Property.", "&7", "&9&lProperty: &a" + "&f" + key + ":" + properties.get(key)), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NBT PROPERTY";
				placeHolders[15] = "TranslatableDisplayName:&aUltra &cItem";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (ChatColor.stripColor(event.getMessage()).contains(":")) {
					properties.remove(key);
					String[] propertyParts = ChatColor.stripColor(event.getMessage()).split(":");
					properties.put(propertyParts[0], propertyParts[1]);
				}
				itemMap.setNBTValues(properties);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "NBT PROPERTY";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				nbtPane(event.getPlayer(), itemMap);
			}));
			modifyProperty.addButton(new Button(fillerPaneGItem));
			modifyProperty.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this custom NBT Property.", "&7", "&9&lProperty: &a" + "&f" + key + ":" + properties.get(key)), event -> {
				properties.remove(key);
				itemMap.setNBTValues(properties);
				nbtPane(player, itemMap);
			}));
			modifyProperty.addButton(new Button(fillerPaneGItem), 3);
			modifyProperty.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the NBT Properties menu."), event -> nbtPane(player, itemMap)));
			modifyProperty.addButton(new Button(fillerPaneBItem), 7);
			modifyProperty.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the NBT Properties menu."), event -> nbtPane(player, itemMap)));
		});
		modifyProperty.open(player);
	}
	
   /**
    * Sets the recipe pattern and ingredients.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    * @param material - The material to be set.
    * @param position - The position in the crafting table being set.
    */
	private static void setIngredients(final Player player, final ItemMap itemMap, final String material, final int position) {
		Map<Character, String> ingredients = itemMap.getIngredients();
		List < Character > recipe = itemMap.getRecipe();
		char character = 'A';
		for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
			if (alphabet != 'X' && !ingredients.containsKey(alphabet)) {
				character = alphabet;
				break;
			}
		}
		for (Character characters: ingredients.keySet()) {
			if (ingredients.get(characters).equals(material)) {
				character = characters;
				break;
			}
		}
		if (!StringUtils.containsIgnoreCase(material, "AIR") && !ingredients.containsValue(material)) {
			ingredients.put(character, material);
		} else if (StringUtils.containsIgnoreCase(material, "AIR")) {
			int count = 0;
			for (Character recipes: recipe) {
				if (recipes.equals(recipe.get(position))) {
					count++;
				}
			}
			if (count == 1) {
				ingredients.remove(recipe.get(position));
			}
		}
		while (position >= recipe.size()) {
			recipe.add('X');
		}
		recipe.set(position, (!StringUtils.containsIgnoreCase(material, "AIR") ? character : 'X'));
		itemMap.setRecipe(recipe);
		itemMap.setIngredients(ingredients);
		recipePane(player, itemMap);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying banner items.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void bannerPane(final Player player, final ItemMap itemMap) {
		Interface bannerPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			bannerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			for (PatternType pattern: PatternType.values()) {
				String patternString = "NONE";
				if (StringUtils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
					for (Pattern patterns: itemMap.getBannerPatterns()) {
						if (patterns.getPattern() == pattern) {
							patternString = patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase();
							break;
						}
					}
				}
				final String checkPattern = patternString;
				bannerPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, (checkPattern != "NONE" ? true : false), "&f" + pattern.name(), "&7", "&7*Click to add this as", "&7a banner pattern.", (checkPattern != "NONE" ? 
						"&9&lInformation: &a" + checkPattern : "")), event -> {
					if (checkPattern != "NONE") {
						List < Pattern > patternList = itemMap.getBannerPatterns();
						if (StringUtils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
							for (Pattern patterns: patternList) {
							if (patterns.getPattern() == pattern) {
									patternList.remove(patterns);
									itemMap.setBannerPatterns(patternList);
									break;
								}
							}
						}
						bannerPane(player, itemMap);
					} else {
						patternPane(player, itemMap, pattern);
					}
				}));
			}
		});
		bannerPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting the Banner Pattern.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void patternPane(final Player player, final ItemMap itemMap, final PatternType pattern) {
		Interface colorPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the banner patterns menu."), event -> bannerPane(player, itemMap)));
			for (DyeColor color: DyeColor.values()) {
				colorPane.addButton(new Button(ItemHandler.getItem("GRAY_DYE", 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your banner pattern."), event -> {
					List < Pattern > patterns = itemMap.getBannerPatterns();patterns.add(new Pattern(color, pattern));itemMap.setBannerPatterns(patterns);bannerPane(player, itemMap);
				}));
			}
		});
		colorPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting the Potion Type.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void potionPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface potionPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (stage != 1) {
				potionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			} else {
				potionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> otherPane(player, itemMap)));
			}
			for (PotionEffectType potion: PotionEffectType.values()) {
				if (potion != null) {
					String potionString = "NONE";
					if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
						for (PotionEffect potions: itemMap.getPotionEffect()) {
							if (potions.getType() == potion) {
								potionString = potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + (potions.getDuration());
								break;
							}
						}
					}
					final String checkPotion = potionString;
					potionPane.addButton(new Button(ItemHandler.getItem("GLASS_BOTTLE", 1, (checkPotion != "NONE" ? true : false), "&f" + potion.getName(), "&7", "&7*Add this potion effect", "&7to the item.", 
							(checkPotion != "NONE" ? "&9&lInformation: &a" + checkPotion : "")), event -> {
						if (checkPotion != "NONE") {
							List < PotionEffect > potionEffects = itemMap.getPotionEffect();
							if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
								for (PotionEffect potions: potionEffects) {
									if (potions.getType() == potion) {
										potionEffects.remove(potions);
										itemMap.setPotionEffect(potionEffects);
										break;
									}
								}
							}
							potionPane(player, itemMap, stage);
						} else {
							levelPane(player, itemMap, potion, stage);
						}
					}));
				}
			}
		});
		potionPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting the Teleport Arrow.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void teleportPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface teleportPane = new Interface(false, 1, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (stage == 1) {
				teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> { 
					setItemFlags(itemMap);
					otherPane(player, itemMap); 
				}));
			} else {
				teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> { 
					setItemFlags(itemMap);
					creatingPane(player, itemMap); 
				}));
			}
			teleportPane.addButton(new Button(fillerPaneGItem), 2);
			teleportPane.addButton(new Button(ItemHandler.getItem("BLAZE_POWDER", 1, false, "&b&lTeleport Effect", "&7", "&7*The effect to play at the", "&7arrow landed location when", "&7the player is teleported.", 
					"&9&lTeleport-Effect: &a" + StringUtils.nullCheck(itemMap.getTeleportEffect())), event -> {
				particlePane(player, itemMap, stage);
			}));
			teleportPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, itemMap.isTeleport(), "&a&l&nTeleport", "&7", 
					"&a&lTrue&f: &7Teleports the Player to the location", "&7that the arrow landed.", 
					"&cNOTE: &7This only works if the arrow is fired by a Bow.",
					"&9&lENABLED: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()), event -> {
				if (itemMap.isTeleport()) {
					itemMap.setTeleport(false);
				} else {
					itemMap.setTeleport(true);
				}
				teleportPane(player, itemMap, stage);
			}));
			teleportPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, "&a&lTeleport Sound", "&7", "&7*The sound to play at the", "&7arrow landed location when", "&7the player is teleported.", 
			"&9&lTeleport-Sound: &a" + StringUtils.nullCheck(itemMap.getTeleportSound())), event -> {
				soundPane(player, itemMap, stage);
			}));
			teleportPane.addButton(new Button(fillerPaneGItem), 2);
			if (stage == 1) {
				teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> { 
					setItemFlags(itemMap);
					otherPane(player, itemMap); 
				}));
			} else {
				teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> { 
					setItemFlags(itemMap);
					creatingPane(player, itemMap); 
				}));
			}
		});
		teleportPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the Potion Level.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void levelPane(final Player player, final ItemMap itemMap, final PotionEffectType potion, final int stage) {
		Interface levelPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			levelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> potionPane(player, itemMap, stage)));
			levelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Level", "&7", "&7*Click to set a custom level (strength)", "&7value for the potion effect."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "EFFECT LEVEL";
				placeHolders[15] = "16";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "EFFECT LEVEL";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					durationPane(event.getPlayer(), itemMap, potion, Integer.parseInt(ChatColor.stripColor(event.getMessage())), stage);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					levelPane(event.getPlayer(), itemMap, potion, stage);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				levelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", k, false, "&d&lLevel: &a&l" + k + "", "&7", "&7*Click to set the", "&7level (strength) of the potion effect."), event -> {
					itemMap.setInteractCooldown(k);durationPane(player, itemMap, potion, k, stage);
				}));
			}
		});
		levelPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for setting the Potion Duration.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void durationPane(final Player player, final ItemMap itemMap, final PotionEffectType potion, int level, final int stage) {
		Interface durationPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> potionPane(player, itemMap, stage)));
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the potion effect."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "EFFECT DURATION";
				placeHolders[15] = "110";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "EFFECT DURATION";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					potionPane(event.getPlayer(), itemMap, stage);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
					durationPane(event.getPlayer(), itemMap, potion, level, stage);
				}
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7duration of the potion effect."), event -> {
					List < PotionEffect > effects = itemMap.getPotionEffect();
					effects.add(new PotionEffect(potion, k, level));
					itemMap.setPotionEffect(effects);potionPane(player, itemMap, stage);
				}));
			}
		});
		durationPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting the Firework Power.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void powerPane(final Player player, final ItemMap itemMap) {
		Interface powerPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			powerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
			powerPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Power", "&7", "&7*Click to set a custom power", "&7value for the firework."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "FIREWORK POWER";
				placeHolders[15] = "96";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage()))) {
					itemMap.setFireworkPower(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "FIREWORK POWER";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				otherPane(player, itemMap);
			}));
			for (int i = 1; i <= 64; i++) {
				final int k = i;
				powerPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lPower Level: &a&l" + k, "&7", "&7*Click to set the", "&7power level of the firework."), event -> {
					itemMap.setFireworkPower(k);
					otherPane(player, itemMap);
				}));
			}
		});
		powerPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting a Firework Color.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void colorPane(final Player player, final ItemMap itemMap) {
		Interface colorPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
			for (DyeColor color: DyeColor.values()) {
				colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_DYE" : "351:8"), 1, StringUtils.containsValue(itemMap.getFireworkColor(), color.name()), "&f" + color.name(), 
						"&7", "&7*This will be the color", "&7of your firework charge.", "&9&lENABLED: &a" + (StringUtils.containsValue(itemMap.getFireworkColor(), color.name()) + "").toUpperCase()), event -> {
					List < DyeColor > colors = itemMap.getFireworkColor();
					if (StringUtils.containsIgnoreCase(itemMap.getFireworkColor().toString(), color.name())) {
						colors.remove(color);
					} else {
						colors.add(color);
						itemMap.setFireworkColor(colors);
					}
					colorPane(player, itemMap);
				}));
			}
		});
		colorPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for selecting a Firework Type.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void designPane(final Player player, final ItemMap itemMap) {
		Interface designPane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			designPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
			for (Type type: Type.values()) {
				designPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, "&f" + type.name(), "&7", "&7*This will be the type (pattern)", "&7of your firework."), event -> {
					itemMap.setFireworkType(type);
					otherPane(player, itemMap);
				}));
			}
		});
		designPane.open(player);
	}
	
//  ============================================== //
//                Book Pages Menus      	       //
//  ============================================== //
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying book items.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void pagePane(final Player player, final ItemMap itemMap) {
		Interface pagePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			pagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> {
				otherPane(player, itemMap);
			}));
			pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Page", "&7", "&7*Add a new page to the book."), event -> linePane(player, itemMap, true, itemMap.getListPages().size())));
			for (int i = 1; i <= itemMap.getListPages().size(); i++) {
				final int k = i;
				pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&b&lPage " + i, "&7", "&7*Click to modify the contents", "&7of this book page."), event -> linePane(player, itemMap, false, k - 1)));
			}
		});
		pagePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying book page lines.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void linePane(final Player player, final ItemMap itemMap, final boolean isNew, final int page) {
		Interface linePane = new Interface(true, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			List < List < String > > pages = itemMap.getListPages();
			linePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book pages menu."), event -> pagePane(player, itemMap)));
			if (isNew) {
				linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
					player.closeInventory();
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "PAGE LINE";
					placeHolders[15] = "&eWelcome to the Server!";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
				}, event -> {
					List < String > newPage = new ArrayList < String > ();
					newPage.add(ChatColor.stripColor(event.getMessage()));pages.add(newPage);
					itemMap.setListPages(pages);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "PAGE LINE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					linePane(event.getPlayer(), itemMap, false, page);
				}));
			} else {
				List < String > selectPage = pages.get(page);
				linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this page from the book.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
					pages.remove(page);
					itemMap.setListPages(pages);
					pagePane(player, itemMap);
				}));
				if (selectPage.size() < 14) {
					linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lLine: &a" + (selectPage.size() + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
						player.closeInventory();
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "PAGE LINE";
						placeHolders[15] = "&eWelcome to the Server!";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
					}, event -> {
						selectPage.add(ChatColor.stripColor(event.getMessage()));
						pages.set(page, selectPage);
						itemMap.setListPages(pages);
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "PAGE LINE";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
						linePane(event.getPlayer(), itemMap, false, page);
					}));
				}
				for (int i = 1; i <= selectPage.size(); i++) {
					final int k = i;
					linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&f" + selectPage.get(k - 1), "&7", "&7*Click to modify or delete", "&7this line in the book page.", "&7", "&9&lLine: &a" + k + 
							"    &9&lPage: &a" + (page + 1)), event -> modifyPagesPane(player, itemMap, k - 1, page)));
				}
			}
		});
		linePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying book pages.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void modifyPagesPane(final Player player, final ItemMap itemMap, final int line, final int page) {
		Interface linePane = new Interface(false, 2, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			List < List < String > > pages = itemMap.getListPages();
			List < String > selectPage = pages.get(page);
			linePane.addButton(new Button(fillerPaneGItem), 3);
			linePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&c&l&nModify", "&7", "&7*Modify this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "PAGE LINE";
				placeHolders[15] = "&eWelcome to the Server!";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				selectPage.set(line, ChatColor.stripColor(event.getMessage()));
				pages.set(page, selectPage);
				itemMap.setListPages(pages);
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "PAGE LINE";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				linePane(event.getPlayer(), itemMap, false, page);
			}));
			linePane.addButton(new Button(fillerPaneGItem));
			linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
				selectPage.remove(selectPage.get(line));
				pages.set(page, selectPage);
				itemMap.setListPages(pages);
				linePane(player, itemMap, false, page);
			}));
			linePane.addButton(new Button(fillerPaneGItem), 3);
			linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> linePane(player, itemMap, false, page)));
			linePane.addButton(new Button(fillerPaneBItem), 7);
			linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> linePane(player, itemMap, false, page)));
		});
		linePane.open(player);
	}
	
//  ==========================================================================================================================================================================================================================================================
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying item attributes.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void attributePane(final Player player, final ItemMap itemMap, final boolean isLeather) {
		Interface attributePane = new Interface(true, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			if (isLeather) {
				attributePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> otherPane(player, itemMap)));
			} else {
				attributePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			}
			if (ServerUtils.hasSpecificUpdate("1_9")) {
				for (Attribute attribute: Attribute.values()) {
					String checkAttribute = (itemMap.getAttributes().containsKey(attribute.name()) ? (attribute.name() + ":" + itemMap.getAttributes().get(attribute.name())) : "NONE");
					attributePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.getAttributes().containsKey(attribute.name()), "&f" + attribute.name(), "&7", "&7*Add this custom attribute to the item.", 
									(checkAttribute != "NONE" ? "&9&lInformation: &a" + checkAttribute : "")), event -> { 
						if (itemMap.getAttributes().containsKey(attribute.name())) {
							Map<String, Double> attributeList = itemMap.getAttributes();
							attributeList.remove(attribute.name());
							attributePane(player, itemMap, isLeather); 
						} else {
							strengthPane(player, itemMap, attribute.name(), isLeather); 
						}
					}));
				}
			} else {
				String[] attributes = new String[] { "GENERIC_ATTACK_DAMAGE", "GENERIC_FOLLOW_RANGE", "GENERIC_MAX_HEALTH", "GENERIC_MOVEMENT_SPEED" };
				for (String attribute: attributes) {
					String checkAttribute = (itemMap.getAttributes().containsKey(attribute) ? (attribute + ":" + itemMap.getAttributes().get(attribute)) : "NONE");
					attributePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.getAttributes().containsKey(attribute), "&f" + attribute, "&7", "&7*Add this custom attribute to the item.", 
									(checkAttribute != "NONE" ? "&9&lInformation: &a" + checkAttribute : "")), event -> { 
						if (itemMap.getAttributes().containsKey(attribute)) {
							Map<String, Double> attributeList = itemMap.getAttributes();
							attributeList.remove(attribute);
							attributePane(player, itemMap, isLeather); 
						} else {
							strengthPane(player, itemMap, attribute, isLeather); 
						}
					}));
				}
			}
		});
		attributePane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying item attributes strength.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void strengthPane(final Player player, final ItemMap itemMap, final String attribute, final boolean isLeather) {
		Interface strengthPane = new Interface(true, 6, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			strengthPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the custom attributes menu."), event -> attributePane(player, itemMap, isLeather)));
			strengthPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Strength", "&7", "&7*Click to set a custom strength", "&7value for the custom attribute."), event -> {
				player.closeInventory();
				String[] placeHolders = LanguageAPI.getLang(false).newString();
				placeHolders[16] = "STRENGTH";
				placeHolders[15] = "14.0";
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
				LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
			}, event -> {
				if (StringUtils.isInt(ChatColor.stripColor(event.getMessage())) || StringUtils.isDouble(ChatColor.stripColor(event.getMessage()))) {
					Map<String, Double> attributeList = itemMap.getAttributes();
					attributeList.put(attribute, Double.parseDouble(ChatColor.stripColor(event.getMessage())));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "STRENGTH";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = ChatColor.stripColor(event.getMessage());
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.noInteger", player, placeHolders);
				}
				attributePane(event.getPlayer(), itemMap, isLeather);
			}));
			for (double i = 1; i < 90; i++) {
				final double k = i;
				strengthPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", 1, false, "&9&lStrength: &a&l" + k, "&7", "&7*Click to set the strength", "&7 of the custom attribute."), event -> { 
					Map<String, Double> attributeList = itemMap.getAttributes();
					attributeList.put(attribute, k);
					attributePane(player, itemMap, isLeather); 
				}));
			}
		});
		strengthPane.open(player);
	}
	
   /**
    * Opens the Pane for the Player.
    * This Pane is for modifying special items.
    * 
    * @param player - The Player to have the Pane opened.
    * @param itemMap - The ItemMap currently being modified.
    */
	private static void otherPane(final Player player, final ItemMap itemMap) {
		Interface otherPane = new Interface(false, 3, GUIName, player);
		SchedulerUtils.runAsync(() -> {
			otherPane.addButton(new Button(fillerPaneGItem), 4);
			otherPane.addButton(new Button(headerStack(player, itemMap)));
			otherPane.addButton(new Button(fillerPaneGItem), 4);
			if (itemMap.getMaterial().toString().contains("WRITTEN_BOOK")) {
				otherPane.addButton(new Button(fillerPaneGItem), 3);
				otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, "&e&lPages", "&7", "&7*Define custom pages for the book.", 
						"&9&lPages: &a" + (StringUtils.nullCheck(itemMap.getPages() + "") != "NONE" ? "YES" : "NONE")), event -> pagePane(player, itemMap)));
				otherPane.addButton(new Button(fillerPaneGItem));
				otherPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&a&lAuthor", "&7", "&7*Define the author of the book.", "&9&lAuthor: &a" + StringUtils.nullCheck(itemMap.getAuthor())), event -> {
					if (StringUtils.nullCheck(itemMap.getAuthor()) != "NONE") {
						itemMap.setAuthor(null);
						otherPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "AUTHOR";
						placeHolders[15] = "RockinChaos";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
					}
				}, event -> {
					itemMap.setAuthor(ChatColor.stripColor(event.getMessage()));
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "AUTHOR";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					otherPane(event.getPlayer(), itemMap);
				}));
				otherPane.addButton(new Button(fillerPaneGItem), 3);
			} else if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
				String potionList = "";
				String potionString = "";
				if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
					for (PotionEffect potions: itemMap.getPotionEffect()) {
						potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() + ", ";
					}
					for (String split: StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length())))) {
						potionList += "&a" + split + " /n ";
					}
				}
				otherPane.addButton(new Button(fillerPaneGItem), 2);
				otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, "&b&lSkull Owner", "&7", "&7*Define a skull owner for the", "&7head adding that persons skin.", "&7", "&7You can only define skull owner", 
						"&7or skull texture, this will", "&7remove any skull textures.", "&9&lSkull-Owner: &a" + StringUtils.nullCheck(itemMap.getSkull())), event -> {
					if (itemMap.getDynamicOwners() != null && !itemMap.getDynamicOwners().isEmpty()) {
						animatedSkullPane(player, itemMap, true);
					} else {
						if (StringUtils.nullCheck(itemMap.getSkull()) != "NONE") {
							itemMap.setSkull(null);
							otherPane(player, itemMap);
						} else {
							player.closeInventory();
							String[] placeHolders = LanguageAPI.getLang(false).newString();
							placeHolders[16] = "SKULL OWNER";
							placeHolders[15] = "RockinChaos";
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
						}
					}
				}, event -> {
					itemMap.setSkull(ChatColor.stripColor(event.getMessage()));
					itemMap.setSkullTexture(null);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL OWNER";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					otherPane(event.getPlayer(), itemMap);
				}));
				otherPane.addButton(new Button(fillerPaneGItem));
				otherPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&a&lSkull Texture", "&7", "&7*Add a skull texture for the", "&7head as a custom skin.", "&7", "&7You can only define skull texture", 
						"&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like &aminecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.", "&9&lSkull-Texture: &a" + 
				(StringUtils.nullCheck(itemMap.getSkullTexture()) != "NONE" ? (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : "")), event -> {
					if (itemMap.getDynamicTextures() != null && !itemMap.getDynamicTextures().isEmpty()) {
						animatedSkullPane(player, itemMap, false);
						} else {
						if (StringUtils.nullCheck(itemMap.getSkullTexture()) != "NONE") {
							itemMap.setSkullTexture(null);
							otherPane(player, itemMap);
						} else {
							player.closeInventory();
							String[] placeHolders = LanguageAPI.getLang(false).newString();
							placeHolders[16] = "SKULL TEXTURE";
							placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
							LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
						}
					}
				}, event -> {
					itemMap.setSkullTexture(ChatColor.stripColor(event.getMessage()));
					itemMap.setSkull(null);
					String[] placeHolders = LanguageAPI.getLang(false).newString();
					placeHolders[16] = "SKULL TEXTURE";
					LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
					otherPane(event.getPlayer(), itemMap);
				}));
				otherPane.addButton(new Button(fillerPaneGItem));
				otherPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the item.", "&9&lPotion-Effects: &a" + StringUtils.nullCheck(potionList)),
						event -> potionPane(player, itemMap, 1)));
				otherPane.addButton(new Button(fillerPaneGItem), 2);
			} else if (itemMap.getMaterial().toString().contains("TIPPED_ARROW")) {
				String potionList = "";
				String potionString = "";
				if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
					for (PotionEffect potions: itemMap.getPotionEffect()) {
						potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() + ", ";
					}
					for (String split: StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length())))) {
						potionList += "&a" + split + " /n ";
					}
				}
				otherPane.addButton(new Button(fillerPaneGItem), 3);
				otherPane.addButton(new Button(ItemHandler.getItem("BLAZE_POWDER", 1, false, "&e&lEffects", "&7", "&7*Add custom effects", "&7to the arrow tip.", "&9&lTipped-Effect: &a" + StringUtils.nullCheck(potionList)),
						event -> potionPane(player, itemMap, 1)));
				otherPane.addButton(new Button(fillerPaneGItem));
				otherPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, false, "&e&lTeleport", "&7", "&7*Set the arrow to teleport", "&7the player upon landing.", "&9&lEnabled: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()),
						event -> teleportPane(player, itemMap, 1)));
				otherPane.addButton(new Button(fillerPaneGItem), 3);
			} else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
				String colorList = "";
				if (StringUtils.nullCheck(itemMap.getFireworkColor().toString()) != "NONE") {
					for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getFireworkColor().toString()))) {
						colorList += "&a" + split + " /n ";
					}
				}
				otherPane.addButton(new Button(fillerPaneGItem), 2);
				otherPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, "&a&lType", "&7", "&7*Set the style of the explosion.", "&9&lType: &a" + StringUtils.nullCheck(itemMap.getFireworkType() + "")), event -> {
					if (StringUtils.nullCheck(itemMap.getFireworkType() + "") != "NONE") {
						itemMap.setFireworkType(null);
						otherPane(player, itemMap);
					} else {
						designPane(player, itemMap);
					}
				}));
				otherPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.getFireworkFlicker(), "&a&lFlicker", "&7", "&7*Show the flicker effect as", "&7the firework particles dissipate", "&7after the explosion.", 
						"&9&lENABLED: &a" + itemMap.getFireworkFlicker()), event -> {
					if (itemMap.getFireworkFlicker()) {
						itemMap.setFireworkFlicker(false);
					} else {
						itemMap.setFireworkFlicker(true);
					}
					otherPane(player, itemMap);
				}));
				otherPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, itemMap.getFireworkTrail(), "&a&lTrail", "&7", "&7*Show the trail (smoke) of", "&7the firework when launched.", "&9&lENABLED: &a" + itemMap.getFireworkTrail()), event -> {
					if (itemMap.getFireworkTrail()) {
						itemMap.setFireworkTrail(false);
					} else {
						itemMap.setFireworkTrail(true);
					}
					otherPane(player, itemMap);
				}));
				otherPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&a&lPower", "&7", "&7*Set the power (distance)", "&7that the firework travels.", "&9&lPower: &a" + StringUtils.nullCheck(itemMap.getFireworkPower() + "&7")), event -> {
					if (StringUtils.nullCheck(itemMap.getFireworkPower() + "&7") != "NONE") {
						itemMap.setFireworkPower(0);
						otherPane(player, itemMap);
					} else {
						powerPane(player, itemMap);
					}
				}));
				otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIME_DYE" : "351:10"), 1, false, "&a&lColor(s)", "&7", "&7*Define the individual colors of the", "&7firework effect type.", 
						"&9&lColor(s): &a" + (StringUtils.nullCheck(colorList) != "NONE" ? colorList : "NONE")), event -> colorPane(player, itemMap)));
				otherPane.addButton(new Button(fillerPaneGItem), 2);
			} else if (itemMap.getMaterial().toString().contains("LEATHER_")) {
				Interface colorPane = new Interface(true, 6, GUIName, player);
				colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
				for (DyeColor color: DyeColor.values()) {
					colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_DYE" : "351:8"), 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your leather armor."), event -> {
						itemMap.setLeatherColor(color.name());itemMap.setLeatherHex(null);otherPane(player, itemMap);
					}));
				}
				otherPane.addButton(new Button(fillerPaneGItem), 3);
				otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_DYE" : "351:11"), 1, false, "&a&lDye", "&7", "&7*Add a custom color to", "&7your leather armor.", "&9&lLeather-Color: &a" +
				(StringUtils.nullCheck(itemMap.getLeatherColor()) != "NONE" ? StringUtils.nullCheck(itemMap.getLeatherColor()) : StringUtils.nullCheck(itemMap.getLeatherHex()))), event -> {
					if (itemMap.getLeatherColor() != null) {
						itemMap.setLeatherColor(null);
						otherPane(player, itemMap);
					} else {
						colorPane.open(player);
					}
				}));
				if (!ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot")) {
					String attributeList = "";
					String attributeString = "";
					if (StringUtils.nullCheck(itemMap.getAttributes().toString()) != "NONE") {
						for (String attribute: itemMap.getAttributes().keySet()) {
							attributeString += attribute + ":" + itemMap.getAttributes().get(attribute) + ", ";
						}
						for (String split: StringUtils.softSplit(StringUtils.nullCheck(attributeString.substring(0, attributeString.length())))) {
							attributeList += "&a" + split + " /n ";
						}
					}
					otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, "&a&lAttributes", "&7", "&7*Add a custom attribute to", "&7your armor or weapon.", (StringUtils.nullCheck(attributeList) != "NONE" ? "&9&lAttributes: &a" + attributeList : "")), event -> {
						attributePane(player, itemMap, true);
					}));
				} else { otherPane.addButton(new Button(fillerPaneGItem)); }
				otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, "&a&lHex Color", "&7", "&7*Add a custom hex color", "&7to your leather armor.", "&9&lLeather-Color: &a" + 
				(StringUtils.nullCheck(itemMap.getLeatherHex()) != "NONE" ? StringUtils.nullCheck(itemMap.getLeatherHex()) : StringUtils.nullCheck(itemMap.getLeatherColor()))), event -> {
					if (itemMap.getLeatherHex() != null) {
						itemMap.setLeatherHex(null);
						otherPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "HEX COLOR";
						placeHolders[15] = "#033dfc";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputType", player, placeHolders);
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputExample", player, placeHolders);
					}
				}, event -> {
					if (itemMap.getLeatherHex() == null) {
						itemMap.setLeatherHex(ChatColor.stripColor(event.getMessage()));
						itemMap.setLeatherColor(null);
						String[] placeHolders = LanguageAPI.getLang(false).newString();
						placeHolders[16] = "HEX COLOR";
						LanguageAPI.getLang(false).sendLangMessage("commands.menu.inputSet", player, placeHolders);
						otherPane(event.getPlayer(), itemMap);
					}
				}));
				otherPane.addButton(new Button(fillerPaneGItem), 3);
			}	
			otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
			otherPane.addButton(new Button(fillerPaneBItem), 7);
			otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
		});
		otherPane.open(player);
	}
	
   /**
    * Gets the Header ItemStack.
    * 
    * @param player - The player getting the Header Stack.
    * @param itemMap - The ItemMap to be formatted.
    * @return The formatted Header ItemStack.
    */
	private static ItemStack headerStack(final Player player, final ItemMap itemMap) {
		String slotList = "";
		String slotString = "";
		ItemStack item = new ItemStack(Material.STONE);
		if (StringUtils.nullCheck(itemMap.getMultipleSlots().toString()) != "NONE") {
			for (String slot: itemMap.getMultipleSlots()) {
				slotString += slot + ", ";
			}
			if (slotString.length() >= 2) {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(slotString.substring(0, slotString.length() - 2)))) {
					slotList += "&a" + split + " /n ";
				}
			}
		}
		String itemflagsList = "";
		if (StringUtils.nullCheck(itemMap.getItemFlags()) != "NONE") {
			for (String split: StringUtils.softSplit(itemMap.getItemFlags())) {
				itemflagsList += "&a" + split + " /n ";
			}
		}
		String triggersList = "";
		if (StringUtils.nullCheck(itemMap.getTriggers()) != "NONE") {
			for (String split: StringUtils.softSplit(itemMap.getTriggers())) {
				triggersList += "&a" + split + " /n ";
			}
		}
		String worldList = "";
		if (StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE") {
			for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
				worldList += "&a" + split + " /n ";
			}
		}
		String regionList = "";
		if (StringUtils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE") {
			for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledRegions().toString()))) {
				regionList += "&a" + split + " /n ";
			}
		}
		String enchantList = "";
		if (StringUtils.nullCheck(itemMap.getEnchantments().toString()) != "NONE") {
			for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnchantments().toString()))) {
				enchantList += "&a" + split + " /n ";
			}
		}
		String potionList = "";
		String potionString = "";
		if (StringUtils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
			for (PotionEffect potions: itemMap.getPotionEffect()) {
				potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() + ", ";
			}
			if (potionString.length() >= 2) {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length() - 2)))) {
					potionList += "&a" + split + " /n ";
				}
			}
		}
		String attributeList = "";
		String attributeString = "";
		if (StringUtils.nullCheck(itemMap.getAttributes().toString()) != "NONE") {
			for (String attribute: itemMap.getAttributes().keySet()) {
				attributeString += attribute + ":" + itemMap.getAttributes().get(attribute) + ", ";
			}
			for (String split: StringUtils.softSplit(StringUtils.nullCheck(attributeString.substring(0, attributeString.length())))) {
				attributeList += "&a" + split + " /n ";
			}
		}
		String patternList = "";
		String patternString = "";
		if (ServerUtils.hasSpecificUpdate("1_8") && StringUtils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
			for (Pattern patterns: itemMap.getBannerPatterns()) {
				patternString += patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase() + ", ";
			}
			if (patternString.length() >= 2) {
				for (String split: StringUtils.softSplit(StringUtils.nullCheck(patternString.substring(0, patternString.length() - 2)))) {
					patternList += "&a" + split + " /n ";
				}
			}
		}
		String colorList = "";
		if (StringUtils.nullCheck(itemMap.getFireworkColor().toString()) != "NONE") {
			for (String split: StringUtils.softSplit(StringUtils.nullCheck(itemMap.getFireworkColor().toString()))) {
				colorList += "&a" + split + " /n ";
			}
		}
		boolean useCommands = true;
		if (itemMap.getCommands().length == 1) {
			for (ItemCommand command : itemMap.getCommands()) {
				if (command.getRawCommand().equalsIgnoreCase("default: ")) {
					useCommands = false;
				}
			}
		} else if (itemMap.getCommands().length == 0) { useCommands = false; }
		String mobs = "";
		for (EntityType entity: itemMap.getMobsDrop().keySet()) { mobs += entity.name() + ", "; }
		String blocks = "";
		for (Material material: itemMap.getBlocksDrop().keySet()) { blocks += material.name() + ", "; }
		try {
			item = ItemHandler.getItem(itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""), 1, false, "&7*&6&l&nItem Information", "&7", "&9&lNode: &a" + itemMap.getConfigName(), "&9&lMaterial: &a" 
			+ itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""), 
					(itemMap.getMultipleSlots() != null && !itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSlot: &a" + itemMap.getSlot().toUpperCase()), (itemMap.getCount() != 1 && itemMap.getCount() != 0) ? "&9&lCount: &a" + itemMap.getCount() : "", 
					((StringUtils.nullCheck(itemMap.getCustomName()) != "NONE" && !ItemHandler.getMaterialName(itemMap.getTempItem()).equalsIgnoreCase(itemMap.getCustomName())) ? "&9&lName: &a" + itemMap.getCustomName() : ""), (StringUtils.nullCheck(itemMap.getCustomLore().toString()) != "NONE" ? "&9&lLore: &a" + (StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").length() > 40 ? StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").substring(0, 40) : StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",")) : ""), 
					(StringUtils.nullCheck(itemMap.getDurability() + "&7") != "NONE" ? "&9&lDurability: &a" + itemMap.getDurability() : ""), (StringUtils.nullCheck(itemMap.getData() + "&7") != "NONE" ? "&9&lTexture Data: &a" + itemMap.getData() : ""), (useCommands ? "&9&lCommands: &aYES" : ""), 
					(StringUtils.nullCheck(itemMap.getItemCost() + "") != "NONE" ? "&9&lCommands-Item: &a" + itemMap.getItemCost() : ""), (StringUtils.nullCheck(itemMap.getCommandCost() + "&7") != "NONE" ? "&9&lCommands-Cost: &a" + itemMap.getCommandCost() : ""), 
					(StringUtils.nullCheck(itemMap.getCommandReceive() + "&7") != "NONE" ? "&9&lCommands-Receive: &a" + itemMap.getCommandReceive() : ""),
					(StringUtils.nullCheck(itemMap.getCommandSequence() + "") != "NONE" ? "&9&lCommands-Sequence: &a" + itemMap.getCommandSequence() : ""), (StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE" ? "&9&lCommands-Cooldown: &a" + itemMap.getCommandCooldown() + " second(s)" : ""), 
					(StringUtils.nullCheck(itemMap.getCooldownMessage()) != "NONE" ? "&9&lCooldown-Message: &a" + itemMap.getCooldownMessage() : ""), (StringUtils.nullCheck(itemMap.getCommandSound() + "") != "NONE" ? "&9&lCommands-Sound: &a" + itemMap.getCommandSound() : ""), 
					(StringUtils.nullCheck(itemMap.getCommandParticle() + "") != "NONE" ? "&9&lCommands-Particle: &a" + itemMap.getCommandParticle() : ""), (StringUtils.nullCheck(itemMap.getEnchantments().toString()) != "NONE" ? "&9&lEnchantments: &a" + enchantList : ""), 
					(StringUtils.nullCheck(itemMap.getItemFlags()) != "NONE" ? "&9&lItemflags: &a" + itemflagsList : ""), (StringUtils.nullCheck(itemMap.getTriggers()) != "NONE" ? "&9&lTriggers: &a" + triggersList : ""), 
					(StringUtils.nullCheck(itemMap.getPermissionNode()) != "NONE" ? "&9&lPermission Node: &a" + itemMap.getPermissionNode() : ""), (StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE" ? "&9&lEnabled Worlds: &a" + worldList : ""), 
					(StringUtils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE" ? "&9&lEnabled Regions: &a" + regionList : ""), (!itemMap.getDynamicMaterials().isEmpty() ? "&9&lMaterial Animations: &aYES" : ""), 
					(!itemMap.getDynamicNames().isEmpty() ? "&9&lName Animations: &aYES" : ""), (!itemMap.getDynamicLores().isEmpty() ? "&9&lLore Animations: &aYES" : ""), 
					(!itemMap.getDynamicOwners().isEmpty() || !itemMap.getDynamicTextures().isEmpty() ? "&9&lSkull Animations: &aYES" : ""), (StringUtils.nullCheck(itemMap.getLimitModes()) != "NONE" ? "&9&lLimit-Modes: &a" + itemMap.getLimitModes() : ""), 
					(StringUtils.nullCheck(itemMap.getProbability() + "&a%") != "NONE" ? "&9&lProbability: &a" + itemMap.getProbability() + "%" : ""), 
					(StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7") != "NONE" ? "&9&lUse-Cooldown: &a" + itemMap.getInteractCooldown() : ""), 
					(StringUtils.nullCheck(itemMap.getLeatherColor()) != "NONE" ? "&9&lLeather Color: &a" + itemMap.getLeatherColor() : ""), (StringUtils.nullCheck(itemMap.getLeatherHex()) != "NONE" ? "&9&lLeather Color: &a" + itemMap.getLeatherHex() : ""),
					(StringUtils.nullCheck(itemMap.getMapImage()) != "NONE" ? "&9&lMap-Image: &a" + itemMap.getMapImage() : ""), (StringUtils.nullCheck(itemMap.getChargeColor() + "") != "NONE" ? "&9&lCharge Color: &a" + itemMap.getChargeColor() : ""),
					(StringUtils.nullCheck(patternList) != "NONE" ? "&9&lBanner Meta: &a" + patternList : ""), (StringUtils.nullCheck(potionList) != "NONE" ? "&9&lPotion-Effects: &a" + potionList : ""), (itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty() ? "&9&lRecipe: &aYES" : ""),
					(!mobs.isEmpty() ? "&9&lMobs Drop: &a" + mobs.substring(0, mobs.length() - 2) : ""), (!blocks.isEmpty() ? "&9&lBlocks Drop: &a" + blocks.substring(0, blocks.length() - 2) : ""), 
					(StringUtils.nullCheck(itemMap.getCommandConditions() + "") != "NONE" ? "&9&lCommand Conditions: &aYES" : ""), (StringUtils.nullCheck(itemMap.getDisposableConditions() + "") != "NONE" ? "&9&lDisposable Conditions: &aYES" : ""), 
					(StringUtils.nullCheck(itemMap.getTriggerConditions() + "") != "NONE" ? "&9&lTrigger Conditions: &aYES" : ""),
					(StringUtils.nullCheck(itemMap.getNBTValues() + "") != "NONE" ? "&9&lNBT Properties: &aYES" : ""), (StringUtils.nullCheck(itemMap.getContents() + "") != "NONE" ? "&9&lContents: &aYES" : ""),
					(StringUtils.nullCheck(attributeList) != "NONE" ? "&9&lAttributes: &a" + attributeList : ""), (StringUtils.nullCheck(itemMap.getPages() + "") != "NONE" ? "&9&lBook Pages: &aYES" : ""),
					(StringUtils.nullCheck(itemMap.getAuthor()) != "NONE" ? "&9&lBook Author: &a" + itemMap.getAuthor() : ""), (StringUtils.nullCheck(itemMap.getSkull()) != "NONE" ? "&9&lSkull-Owner: &a" + itemMap.getSkull() : ""), 
					(StringUtils.nullCheck(itemMap.getSkullTexture()) != "NONE" ? "&9&lSkull-Texture: &a" + (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : ""), 
					(StringUtils.nullCheck(itemMap.getFireworkType() + "") != "NONE" ? "&9&lFirework Type: &a" + itemMap.getFireworkType().name() : ""), 
					(StringUtils.nullCheck(itemMap.getFireworkPower() + "&7") != "NONE" ? "&9&lFirework Power: &a" + itemMap.getFireworkPower() : ""), (StringUtils.nullCheck(colorList) != "NONE" ? "&9&lFirework Color(s): &a" + colorList : ""), 
					(itemMap.getFireworkTrail() ? "&9&lFirework Trail: &aENABLED" : ""), (itemMap.getFireworkFlicker() ? "&9&lFirework Flicker: &aENABLED" : ""));
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		if (ItemHandler.isSkull(itemMap.getMaterial())) {
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMap.getSkull() != null) {
				itemMeta = ItemHandler.setSkullOwner(itemMeta, StringUtils.translateLayout(itemMap.getSkull(), player));
			} else if (itemMap.getSkullTexture() != null && !itemMap.isHeadDatabase()) {
				try {
					if (ServerUtils.hasSpecificUpdate("1_8")) {
						GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
						gameProfile.getProperties().put("textures", new Property("textures", new String(itemMap.getSkullTexture())));
						Field declaredField = itemMeta.getClass().getDeclaredField("profile");
						declaredField.setAccessible(true);
						declaredField.set(itemMeta, gameProfile);
					}
				} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			} else if (itemMap.isHeadDatabase() && itemMap.getSkullTexture() != null) {
				HeadDatabaseAPI api = new HeadDatabaseAPI();
				ItemStack sk = api.getItemHead(itemMap.getSkullTexture());
				item = (sk != null ? sk : item.clone());
			}
			item.setItemMeta(itemMeta);
		}
		return item;
	}
	
   /**
    * Checks if the ItemMap is a special item.
    * 
    * @param itemMap - The ItemMap to be checked.
    * @return If the ItemMap is a "special" item.
    */
	private static boolean specialItem(final ItemMap itemMap) {
		if (itemMap.getMaterial().toString().contains("WRITTEN_BOOK") || itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM") 
				|| itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET") 
				|| itemMap.getMaterial().toString().contains("TIPPED_ARROW") || itemMap.getMaterial().toString().contains("LEATHER_") || !ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot")) {
			return true;
		}
		return false;
	}
	
//  ==============================================================================================================================================================================================================================================================
	
   /**
    * Attemps to close all online players inventories, 
    * if they are found to have the plugin UI open.
    * 
    */
	public static void closeMenu() {
		PlayerHandler.forOnlinePlayers(player -> { 
			if (isOpen(player) || modifyMenu(player)) {
				player.closeInventory();
			}
		});
	}
	
   /**
    * Sets the Player to the Modify Menu.
    * 
    * @param bool - If the Player is in the Menu.
    * @param player - The Player to be set to the Modify Menu.
    */
	public static void setModifyMenu(final boolean bool, final Player player) {
		if (bool) { modifyMenu.add(player); } 
		else { modifyMenu.remove(player); }
	}
	
   /**
    * Checks if the Player is in the Modify Menu.
    * 
    * @param player - The Player to be checked.
    * @return If the Player is in the Modify Menu.
    */
	public static boolean modifyMenu(final Player player) {
		return modifyMenu.contains(player);
	}
	
   /**
    * Checks if the Player has the GUI Menu open.
    * 
    * @param player - The Player to be checked.
    * @return If the GUI Menu is open.
    */
	public static boolean isOpen(final Player player) {
		if (GUIName == null) { GUIName = ServerUtils.hasSpecificUpdate("1_9") ? StringUtils.colorFormat("&7           &0&n ItemJoin Menu") : StringUtils.colorFormat("&7           &0&n ItemJoin Menu"); }
		if (GUIName != null && player != null && player.getOpenInventory().getTitle().toString().equalsIgnoreCase(StringUtils.colorFormat(GUIName))) {
			return true;
		}
		return false;
	}
}