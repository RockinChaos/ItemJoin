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
package me.RockinChaos.itemjoin.utils.menus;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.interfaces.Interface;
import me.RockinChaos.core.utils.interfaces.Query;
import me.RockinChaos.core.utils.interfaces.types.Button;
import me.RockinChaos.core.utils.types.PlaceHolder;
import me.RockinChaos.core.utils.types.PlaceHolder.Holder;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemCommand;
import me.RockinChaos.itemjoin.item.ItemCommand.Action;
import me.RockinChaos.itemjoin.item.ItemCommand.CommandSequence;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemRecipe;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Handles the in-game GUI.
 */
public class Menu {
    private static final ItemStack fillerPaneBItem = ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15"), 1, false, false, "&7", "");
    private static final ItemStack fillerPaneGItem = ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:7"), 1, false, false, "&7", "");
    private static final ItemStack fillerPaneItem = ItemHandler.getItem("GLASS_PANE", 1, false, false, "&7", "");
    private static final ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nExit", "&7", "&7*Returns you to the game");
    private static final List<String> modifyMenu = new ArrayList<>();    private static final Button exitButton = new Button(exitItem, event -> Menu.startMenu(event.getWhoClicked()));
    private static final Map<String, Interface> typingMenu = new HashMap<>();
    private static String GUIName = StringUtils.colorFormat("&7           &0&n ItemJoin Menu");
    private static int failCycle = 0;

    /**
     * Opens the MAIN CREATOR PANE for the Player.
     *
     * @param sender - The Sender to have the Pane opened.
     */
    public static void startMenu(final CommandSender sender) {
        final Player player = (Player) sender;
        Interface pagedPane = new Interface(false, 1, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
            pagedPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, false, "&b&l&nConfig Settings", "&7", "&7*Change the GLOBAL plugin", "&7configuration settings."), event -> configSettings(player)));
            pagedPane.addButton(new Button(fillerPaneBItem));
            pagedPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&a&l&nCreate", "&7", "&7*Create a new item from scratch."),
                    event -> materialPane(player, new ItemMap("item_" + PluginData.getInfo().getPath(1), "ARBITRARY"), 0, 0)));
            pagedPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&l&nSave", "&7", "&7*Save an existing item as a custom item."), event -> startHopper(player)));
            pagedPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&c&l&nModify", "&7", "&7*Modify an existing custom item"), event -> startModify(player, null, 0)));
            pagedPane.addButton(new Button(fillerPaneBItem));
            pagedPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&b&l&nItem Settings", "&7", "&7*Change the GLOBAL custom items", "&7configuration settings."), event -> itemSettings(player)));
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
        Interface itemPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&bTrigger Delay", "&7", "&7*This is the delay in half-seconds", "&7that ItemJoin will wait", "&7to give you the items.", "&7",
                    "&cNOTE: &7It is recommended to", "&7set this to 2 or 3 half-seconds.", "&9&lDELAY: &a" + String.valueOf(ItemJoin.getCore().getConfig("items.yml").getString("items-Delay")).toUpperCase() + " half-second(s)"),
                    event -> numberPane(player, 1)));
            itemPane.addButton(new Button(fillerPaneBItem));
            final String defaultTriggers = ItemJoin.getCore().getConfig("config.yml").getString("Settings.Default-Triggers");
            itemPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false,
                    "&bDefault Triggers", "&7", "&7*This will be the default", "&7triggers used if a custom", "&7item is defined without", "&7specifying any triggers.",
                    "&9&lENABLED: &a" + ((defaultTriggers != null && !defaultTriggers.isEmpty() && !StringUtils.containsIgnoreCase(defaultTriggers, "DISABLE")) ? defaultTriggers : "FALSE").toUpperCase()),
                    event -> triggerPane(player, 0)));
            itemPane.addButton(new Button(fillerPaneBItem));
            itemPane.addButton(new Button(ItemHandler.getItem("COOKIE", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items"), false,
                    "&bItem Permissions", "&7", "&7*If custom items should require", "&7the player to have specific", "&7permissions to receive the item.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Permissions.Obtain-Items", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(fillerPaneBItem));
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "33"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP"), false,
                    "&bItem Permissions &c&l[OP PLAYERS]", "&7", "&7*If custom items should require", "&7the &c&lOP player(s)&7 to have specific", "&7permissions to receive the item.", "&c&lNOTE: &7This only applies to &c&lOP player(s)&7.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Permissions.Obtain-Items-OP", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(fillerPaneBItem));
            itemPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Overwrite"), false,
                    "&bOverwrite", "&7", "&7*Setting this to true will allow", "&7all custom items to overwrite", "&7any custom or vanilla items.", "&7", "&cNOTE: &7If set to false, false, the", "&7overwrite itemflag will still", "&7function normally.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("items.yml").getString("items-Overwrite")).toUpperCase()), event -> overwritePane(player)));
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_FENCE" : "85"), 1, ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Spamming"), false,
                    "&bSpamming", "&7", "&7*Setting this to false will prevent", "&7players from macro spamming", "&7the use of item commands.", "&7", "&cNOTE: &7It is recommended to", "&7leave this set to false.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Spamming")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("items-Spamming", !ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Spamming"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "items.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, ItemJoin.getCore().getConfig("items.yml").getBoolean("items-RestrictCount"), false,
                    "&bRestrict Count", "&7", "&7*Settings this to true will", "&7allow players to have their items", "&7refreshed (topped up) if they have", "&7used/consumed some of the given", "&7stack of custom items.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("items.yml").getBoolean("items-RestrictCount")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("items-RestrictCount", !ItemJoin.getCore().getConfig("items.yml").getBoolean("items-RestrictCount"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "items.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_WOOD" : "17"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Log-Commands"), false,
                    "&bLog Commands", "&7", "&7*If the plugin prevent CONSOLE", "&7from logging any executed", "&7command from the custom items.", "&7This only works for item command(s).",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Log-Commands")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("General.Log-Commands", !ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Log-Commands"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.HeldItem-Animations"), false,
                    "&bHeld Item Animations", "&7", "&7*If the animate or dynamic", "&7itemflags should update the item", "&7while the player is holding the item.", "&7",
                    "&7This essentially prevents the", "&7glitch held item animation", "&7from the item constantly updating.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.HeldItem-Animations")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Settings.HeldItem-Animations", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.HeldItem-Animations"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().hardReload(true);
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            final int heldSlot = ItemJoin.getCore().getConfig("config.yml").getInt("Settings.HeldItem-Slot");
            itemPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", (heldSlot > 0 ? heldSlot : 1), false, false,
                    "&bHeld Item Slot", "&7", "&7*This is the hotbar slot that", "&7the player will automatically", "&7have selected upon performing", "&7one of the held item triggers.",
                    "&9&lSLOT: &a" + (heldSlot >= 0 ? heldSlot : "NONE")),
                    event -> numberPane(player, 2)));
            final String heldTriggers = ItemJoin.getCore().getConfig("config.yml").getString("Settings.HeldItem-Triggers");
            itemPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false,
                    "&bHeld Item Triggers", "&7", "&7*When these trigger(s)", "&7are performed, the held item", "&7slot will be set.",
                    "&9&lENABLED: &a" + ((heldTriggers != null && !heldTriggers.isEmpty() && !StringUtils.containsIgnoreCase(heldTriggers, "DISABLE")) ? heldTriggers : "FALSE").toUpperCase()),
                    event -> triggerPane(player, 1)));
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTING_TABLE" : "116"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.DataTags"), false,
                    "&bDataTags", "&7", "&7*If custom items should use", "&7data tags (NBTTags) to distinguish", "&7each custom item, making them unique.",
                    "&c&lNOTE: &7It is recommended to keep", "&7this set to TRUE.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.DataTags")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Settings.DataTags", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.DataTags"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().hardReload(true);
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
            itemPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false,
                    "&bClear Items", "&7", "&7*Modify settings for clearing", "&7specific items when a player", "&7performed a specified action."),
                    event -> clearPane(player)));
            itemPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, false, false,
                    "&bPrevent Actions", "&7", "&7*Disable certain actions", "&7with items for players."),
                    event -> preventPane(player)));
            itemPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
            itemPane.addButton(new Button(fillerPaneBItem), 7);
            itemPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
        });
        itemPane.open(player);
    }

    /**
     * Opens the CONFIG SETTINGS PANE for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void configSettings(final Player player) {
        Interface configPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            configPane.addButton(new Button(fillerPaneBItem), 3);
            configPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false,
                    "&6Language", "&7", "&7*The selected lang.yml language.", "&7This is for messages sent", "&7from the plugin to the player.",
                    "&9&lLANG: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getString("Language")).toUpperCase()),
                    event -> languagePane(player)));
            configPane.addButton(new Button(fillerPaneBItem));
            configPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL"), false,
                    "&bMySQL Database", "&7", "&7*If the plugin should use", "&7a MySQL Database instead", "&7of the locale SQLite Database.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL")).toUpperCase()),
                    event -> databasePane(player)));
            configPane.addButton(new Button(fillerPaneBItem), 3);
            configPane.addButton(new Button(ItemHandler.getItem("BUCKET", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("General.CheckforUpdates"), false,
                    "&bCheck for Updates", "&7", "&7*If the plugin should check", "&7for updates at start-up.", "&7This includes the use of the", "&7/itemjoin updates/upgrade command(s).",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.CheckforUpdates")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("General.CheckforUpdates", !ItemJoin.getCore().getConfig("config.yml").getBoolean("General.CheckforUpdates"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("COMPASS", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Metrics-Logging"), false,
                    "&bMetrics Logging", "&7", "&7*If the plugin is allowed", "&7to log plugin data such as", "&7the server(s) Java version.", "&7It is recommended to keep this", "&7set to true as it improves", "&7the quality of plugin updates.",
                    "&7", "&7You can view the logged data", "&7Here: https://bstats.org/plugin/bukkit/ItemJoin",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Metrics-Logging")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("General.Metrics-Logging", !ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Metrics-Logging"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("STICK", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Debugging"), false,
                    "&bDebugging", "&7", "&7*Allows for more detailed", "&7CONSOLE messages from the plugin.", "&7Typically only used by the", "&7plugin developer to determine", "&7issues or bugs with the plugin.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Debugging")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("General.Debugging", !ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Debugging"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("General.ignoreErrors"), false,
                    "&bIgnore Errors", "&7", "&7*Disables the sending of errors", "&7to all admins upon joining the server.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.ignoreErrors")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("General.ignoreErrors", !ItemJoin.getCore().getConfig("config.yml").getBoolean("General.ignoreErrors"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(fillerPaneBItem));
            configPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, ((!ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands").isEmpty()
                            && !StringUtils.containsIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds"), "DISABLED"))), false,
                    "&bActive Commands", "&7", "&7*Specify a list of commands", "&7to be executed upon performing a trigger.", "&7These commands are not related to", "&7custom items, rather the server itself.",
                    "&9&lENABLED: &a" + ((!ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands").isEmpty()
                            && !StringUtils.containsIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds"), "DISABLED")) ? "YES" : "NO")),
                    event -> activeCommands(player)));
            configPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_APPLE" : "322"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-OP"), false,
                    "&bPlugin Commands &c&l[OP PLAYERS]", "&7", "&7*If the plugin should check", "&7if the OP player has the", "&7proper permissions set to", "&7use the plugin commands.",
                    "&7OP Players will no longer get", "&7access to all plugin commands", "&7by default.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-OP")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Permissions.Commands-OP", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-OP"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-Get"), false,
                    "&bGet Commands", "&7", "&7*If the get and getAll", "&7commands should check for item", "&c&lpermissions &7before giving the item.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-Get")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Permissions.Commands-Get", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Commands-Get"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Movement-Bypass"), false,
                    "&bMovement Bypass", "&7", "&7*Enables the use of the", "&aitemjoin.bypass.inventorymodify", "&7permission-node, used to ignore", "&7the global itemMovement prevention", "&7or a custom items itemflag.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Movement-Bypass")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Permissions.Movement-Bypass", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Movement-Bypass"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> configSettings(player));
                    }));
            configPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
            configPane.addButton(new Button(fillerPaneBItem), 7);
            configPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
        });
        configPane.open(player);
    }

//  ============================================== //
//  			   Selection Menus      	       //
//	============================================== //

    /**
     * Opens the MODIFY PANE for the Player.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap reference.
     * @param k       - The directory.
     */
    private static void startModify(final Player player, final ItemMap itemMap, final int k) {
        Interface modifyPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            try {
                setPage(player, modifyPane, ItemUtilities.getUtilities().copyItems(), null, itemMap, k);
            } catch (Exception e) {
                if (failCycle != 6) {
                    failCycle++;
                    SchedulerUtils.runLater(2L, () -> startModify(player, itemMap, k));
                } else {
                    failCycle = 0;
                    ServerUtils.sendSevereTrace(e);
                }
            }
        });
        modifyPane.open(player);
    }

    /**
     * Opens the SAVING PANE for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void startHopper(Player player) {
        Interface dragDrop = new Interface(false, 1, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            dragDrop.allowClick(true);
            dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
            dragDrop.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&a&lAutosave", "&7", "&7*Click me to save your whole", "&7inventory to the items.yml as-is,", "&7including current slot positions!", "&7", "&c&l[&nALL&c&l ITEMS]"), event -> {
                final PlayerInventory playerInv = event.getWhoClicked().getInventory();
                final ItemStack offItem = PlayerHandler.getOffHandItem(player);
                for (int i = 0; i <= 35; i++) {
                    final ItemStack item = playerInv.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        convertStack(player, item, Integer.toString(i));
                    }
                }
                if (playerInv.getHelmet() != null && playerInv.getHelmet().getType() != Material.AIR) {
                    convertStack(player, playerInv.getHelmet(), "HELMET");
                }
                if (playerInv.getChestplate() != null && playerInv.getChestplate().getType() != Material.AIR) {
                    convertStack(player, playerInv.getChestplate(), "CHESTPLATE");
                }
                if (playerInv.getLeggings() != null && playerInv.getLeggings().getType() != Material.AIR) {
                    convertStack(player, playerInv.getLeggings(), "LEGGINGS");
                }
                if (playerInv.getBoots() != null && playerInv.getBoots().getType() != Material.AIR) {
                    convertStack(player, playerInv.getBoots(), "BOOTS");
                }
                if (ServerUtils.hasSpecificUpdate("1_9") && offItem.getType() != Material.AIR) {
                    convertStack(player, offItem, "OFFHAND");
                }
                PluginData.getInfo().hardReload(true);
                startMenu(player);
            }));
            dragDrop.addButton(new Button(fillerPaneGItem), 2);
            dragDrop.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&a&lDrop an Item", "&7", "&7*Click an item from your inventory", "&7to save and drop it in this", "&7friendly little hopper!", "&7", "&a&l[&nSINGLE&a&l ITEM]"), event -> {
                final ItemStack cursorItem = event.getCursor();
                if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    final ItemStack item = cursorItem.clone();
                    event.getWhoClicked().setItemOnCursor(null);
                    event.getWhoClicked().getInventory().addItem(item);
                    convertStack(player, item, null);
                    dragDrop.allowClick(false);
                }
            }));
            dragDrop.addButton(new Button(fillerPaneGItem), 2);
            dragDrop.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&a&lAutosave", "&7", "&7*Click me to save your whole", "&7inventory to the items.yml as-is,", "&7including current slot positions!", "&7", "&c&l[&nALL&c&l ITEMS]"), event -> {
                final PlayerInventory playerInv = event.getWhoClicked().getInventory();
                final ItemStack offItem = PlayerHandler.getOffHandItem(player);
                for (int i = 0; i <= 35; i++) {
                    final ItemStack item = playerInv.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        convertStack(player, item, Integer.toString(i));
                    }
                }
                if (playerInv.getHelmet() != null && playerInv.getHelmet().getType() != Material.AIR) {
                    convertStack(player, playerInv.getHelmet(), "HELMET");
                }
                if (playerInv.getChestplate() != null && playerInv.getChestplate().getType() != Material.AIR) {
                    convertStack(player, playerInv.getChestplate(), "CHESTPLATE");
                }
                if (playerInv.getLeggings() != null && playerInv.getLeggings().getType() != Material.AIR) {
                    convertStack(player, playerInv.getLeggings(), "LEGGINGS");
                }
                if (playerInv.getBoots() != null && playerInv.getBoots().getType() != Material.AIR) {
                    convertStack(player, playerInv.getBoots(), "BOOTS");
                }
                if (ServerUtils.hasSpecificUpdate("1_9") && offItem.getType() != Material.AIR) {
                    convertStack(player, offItem, "OFFHAND");
                }
                PluginData.getInfo().hardReload(true);
                startMenu(player);
            }));
            dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> startMenu(player)));
        });
        dragDrop.open(player);
    }

    /**
     * Adds a button to the PagedPane.
     *
     * @param player     - The Player.
     * @param itemMap    - The ItemMap currently being modified.
     * @param modifyPane - The PagedPane.
     * @param contents   - The ItemMap.
     * @param k          - The Page Directory.
     */
    private static void setButton(final Player player, final ItemMap itemMap, final Interface modifyPane, final ItemMap contents, final ItemMap refMap, final int k) {
        final ItemStack item = itemMap.getTempItem().clone();
        if (item.getType() == Material.AIR) {
            item.setType(fillerPaneItem.getType());
        }
        if (itemMap.isAnimated() || itemMap.isDynamic()) {
            setModifyMenu(true, player);
            if (itemMap.getAnimationHandler().get(player) != null) {
                itemMap.getAnimationHandler().get(player).setMenu(true, 0);
            }
        }
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
                if (contents.getContents().contains(itemMap.getConfigName())) {
                    contentList.remove(itemMap.getConfigName());
                } else {
                    contentList.add(itemMap.getConfigName());
                }
                contents.setContents(contentList);
                Interface contentsPane = new Interface(true, 6, exitButton, GUIName, player);
                SchedulerUtils.runAsync(() -> setPage(player, contentsPane, ItemUtilities.getUtilities().copyItems(), contents, null, k));
                contentsPane.open(player);
            }
        }));
    }

    /**
     * Adds the ItemMaps to the PagedPane for viewing.
     *
     * @param player     - The Player.
     * @param modifyPane - The PagedPane to have buttons added.
     * @param items      - The items to be added to the Pane.
     */
    private static void setPage(final Player player, final Interface modifyPane, final List<ItemMap> items, final ItemMap contents, final ItemMap itemMap, final int k) {
        ItemMap currentItem;
        boolean crafting = false;
        boolean arbitrary = false;
        if (contents != null) {
            modifyPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to the item definition menu."), event -> creatingPane(player, contents)));
        } else if (itemMap != null) {
            modifyPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to the recipe menu."), event -> recipePane(player, itemMap)));
        }
        Interface craftingPane = new Interface(false, 4, exitButton, GUIName, player);
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
        craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
        craftingPane.addButton(new Button(fillerPaneBItem), 7);
        craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
        Interface arbitraryPane = new Interface(true, 6, exitButton, GUIName, player);
        arbitraryPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", lore), event -> modifyPane.open(player)));
        List<ItemMap> tempList = new ArrayList<>(items);
        for (final ItemMap item : tempList) {
            if (item.getSlot().equalsIgnoreCase("ARBITRARY")) {
                setButton(player, item, arbitraryPane, contents, itemMap, k);
                items.remove(item);
                arbitrary = true;
            }
        }
        modifyPane.addButton(new Button(fillerPaneGItem));
        if (arbitrary) {
            modifyPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&fArbitrary", "&7", "&7*Click to view the existing", "&7Arbitrary slot items."), event -> arbitraryPane.open(player)));
        } else {
            modifyPane.addButton(new Button(fillerPaneGItem));
        }
        if (crafting) {
            modifyPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, false, false, "&fCrafting", "&7", "&7*Click to view the existing", "&7crafting slot items."), event -> craftingPane.open(player)));
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
     * @param item   - The ItemStack to be saved.
     */
    private static void convertStack(final Player player, final ItemStack item, final String slot) {
        ItemMap itemMap = new ItemMap("item_" + PluginData.getInfo().getPath(1), "ARBITRARY");
        itemMap.setMaterial(item.getType());
        if (!ServerUtils.hasSpecificUpdate("1_13")) {
            itemMap.setDataValue((short) LegacyAPI.getDataValue(item));
        }
        itemMap.setCount(String.valueOf(item.getAmount()));
        if (item.getType().getMaxDurability() > 30 && ItemHandler.getDurability(item) != 0 && ItemHandler.getDurability(item) != (item.getType().getMaxDurability())) {
            itemMap.setDurability(ItemHandler.getDurability(item));
        }
        if (item.hasItemMeta()) {
            final ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                itemMap.setCustomName(itemMeta.getDisplayName().replace(ChatColor.COLOR_CHAR, '&'));
            }
            if (itemMeta != null && itemMeta.getLore() != null) {
                List<String> newLore = new ArrayList<>();
                for (String lore : itemMeta.getLore()) {
                    newLore.add(lore.replace(ChatColor.COLOR_CHAR, '&'));
                }
                itemMap.setCustomLore(newLore);
            }
            if (ServerUtils.hasSpecificUpdate("1_14") && itemMeta != null && itemMeta.hasCustomModelData()) {
                itemMap.setModelData(String.valueOf(itemMeta.getCustomModelData()));
            }
            if (item.getItemMeta().hasEnchants()) {
                Map<String, Integer> enchantList = new HashMap<>();
                for (Enchantment e : item.getItemMeta().getEnchants().keySet()) {
                    enchantList.put(ItemHandler.getEnchantName(e).toUpperCase(), item.getItemMeta().getEnchants().get(e));
                }
                itemMap.setEnchantments(enchantList);
            }
        }
        if (StringUtils.containsIgnoreCase(item.getType().toString(), "LEATHER_")) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (meta != null) {
                final DyeColor dyeColor = DyeColor.getByColor(meta.getColor());
                if (dyeColor != null) {
                    itemMap.setLeatherColor(dyeColor.name());
                }
            }
        } else if (item.getType().toString().equalsIgnoreCase("SKULL_ITEM") || item.getType().toString().equalsIgnoreCase("PLAYER_HEAD")) {
            if (!PlayerHandler.getSkullOwner(item).equalsIgnoreCase("NULL")) {
                itemMap.setSkull(PlayerHandler.getSkullOwner(item));
            } else if (item.getItemMeta() != null && !ItemHandler.getSkullTexture(item.getItemMeta()).isEmpty()) {
                itemMap.setSkullTexture(ItemHandler.getSkullTexture(item.getItemMeta()));
            }
        } else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
            itemMap.setChargeColor(DyeColor.getByFireworkColor(Objects.requireNonNull(((FireworkEffectMeta) Objects.requireNonNull(item.getItemMeta())).getEffect()).getColors().get(0)));
        } else if (itemMap.getMaterial().toString().contains("BANNER")) {
            itemMap.setBannerPatterns(((BannerMeta) Objects.requireNonNull(item.getItemMeta())).getPatterns());
        } else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
            final ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                List<DyeColor> colors = new ArrayList<>();
                for (Color color : ((FireworkMeta) itemMeta).getEffects().get(0).getColors()) {
                    colors.add(DyeColor.getByFireworkColor(color));
                }
                itemMap.setFirework(((FireworkMeta) itemMeta).getEffects().get(0));
                itemMap.setFireworkColor(colors);
                itemMap.setFireworkFlicker(((FireworkMeta) itemMeta).getEffects().get(0).hasFlicker());
                itemMap.setFireworkTrail(((FireworkMeta) itemMeta).getEffects().get(0).hasTrail());
                itemMap.setFireworkType(((FireworkMeta) itemMeta).getEffects().get(0).getType());
                itemMap.setFireworkPower(((FireworkMeta) itemMeta).getPower());
            }
        } else if (itemMap.getMaterial() == Material.WRITTEN_BOOK) {
            final ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMap.setAuthor(Objects.requireNonNull(((BookMeta) itemMeta).getAuthor()).replace(ChatColor.COLOR_CHAR, '&'));
                itemMap.setTitle(Objects.requireNonNull(((BookMeta) itemMeta).getTitle()).replace(ChatColor.COLOR_CHAR, '&'));
                if (ServerUtils.hasSpecificUpdate("1_10")) {
                    itemMap.setGeneration(((BookMeta) itemMeta).getGeneration());
                }
                List<String> newPages = new ArrayList<>();
                for (String page : ((BookMeta) itemMeta).getPages()) {
                    newPages.add(page.replace(ChatColor.COLOR_CHAR, '&'));
                }
                itemMap.setPages(newPages);
                List<List<String>> savePages = new ArrayList<>();
                for (String page : ((BookMeta) itemMeta).getPages()) {
                    List<String> pageList = new ArrayList<>();
                    for (String splitPage : page.split("\n")) {
                        pageList.add(splitPage.replace(ChatColor.COLOR_CHAR, '&'));
                    }
                    savePages.add(pageList);
                }
                itemMap.setListPages(savePages);
            }
        }
        if (slot == null) {
            switchPane(player, itemMap, 0);
        } else {
            itemMap.setSlot(slot);
            itemMap.saveToConfig();
            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.ITEM, (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() ? item.getItemMeta().getDisplayName() : itemMap.getConfigName()));
            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.itemSaved", player, placeHolders);
        }
        itemMap.renderItemStack();
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for selecting item modification or deletion.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param item    - The ItemStack currently being modified.
     */
    private static void choicePane(final Player player, final ItemMap itemMap, final ItemStack item) {
        Interface choicePane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            choicePane.addButton(new Button(fillerPaneBItem), 4);
            choicePane.addButton(new Button(item));
            choicePane.addButton(new Button(fillerPaneBItem), 4);
            choicePane.addButton(new Button(fillerPaneBItem), 3);
            choicePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, true, false, "&b&lSettings", "&7", "&7*Change the settings for this item.", "&7Make changes to the item name, lore,", "&7permissions, enabled-worlds, and more."), event -> creatingPane(player, itemMap)));
            choicePane.addButton(new Button(fillerPaneBItem));
            choicePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, true, false, "&c&lDelete", "&7", "&7*Delete this item.", "&7This will remove the item from the", "&7items.yml and will no longer be usable.", "&c&lWARNING: &7This &lCANNOT &7be undone!"), event -> {
                itemMap.removeFromConfig();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.ITEM, itemMap.getConfigName());
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.itemRemoved", player, placeHolders);
                PluginData.getInfo().hardReload(true);
                {
                    SchedulerUtils.runLater(4L, () -> startModify(player, null, 0));
                }
            }));
            choicePane.addButton(new Button(fillerPaneBItem), 3);
            choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> startModify(player, null, 0)));
            choicePane.addButton(new Button(fillerPaneBItem), 7);
            choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> startModify(player, null, 0)));
        });
        choicePane.open(player);
    }

//  ===================================================================================================================================================================================================================

//  ============================================== //
//  			   Menu Utilities        	       //
//  ============================================== //

    /**
     * Opens the Pane for the Player.
     * This Pane is for Creating a NEW custom item.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void creatingPane(final Player player, final ItemMap itemMap) {
        Interface creatingPane = new Interface(false, 5, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            StringBuilder slotList = new StringBuilder();
            StringBuilder slotString = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getMultipleSlots().toString()).equals("NONE")) {
                for (String slot : itemMap.getMultipleSlots()) {
                    slotString.append(slot).append(", ");
                }
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(slotString.toString()))) {
                    slotList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder itemflagsList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getItemFlags()).equals("NONE")) {
                for (String split : StringUtils.softSplit(itemMap.getItemFlags())) {
                    itemflagsList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder triggersList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getTriggers()).equals("NONE")) {
                for (String split : StringUtils.softSplit(itemMap.getTriggers())) {
                    triggersList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder disabledList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()).equals("NONE")) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()))) {
                    disabledList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder enabledList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()).equals("NONE")) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
                    enabledList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder enabledRegionList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getEnabledRegions().toString()).equals("NONE")) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledRegions().toString()))) {
                    enabledRegionList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder disabledRegionList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getDisabledRegions().toString()).equals("NONE")) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getDisabledRegions().toString()))) {
                    disabledRegionList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder enchantList = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getEnchantments().toString()).equals("NONE")) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnchantments().toString()))) {
                    enchantList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder potionList = new StringBuilder();
            StringBuilder potionString = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
                for (PotionEffect potions : itemMap.getPotionEffect()) {
                    potionString.append(CompatUtils.getName(potions)).append(":").append(potions.getAmplifier()).append(":").append(potions.getDuration()).append(", ");
                }
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length())))) {
                    potionList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder attributeList = new StringBuilder();
            StringBuilder attributeString = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getAttributes().toString()).equals("NONE")) {
                for (String attribute : itemMap.getAttributes().keySet()) {
                    attributeString.append(attribute).append(":").append(itemMap.getAttributes().get(attribute)).append(", ");
                }
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(attributeString.substring(0, attributeString.length())))) {
                    attributeList.append("&a").append(split).append(" /n ");
                }
            }
            StringBuilder patternList = new StringBuilder();
            StringBuilder patternString = new StringBuilder();
            if (!StringUtils.nullCheck(itemMap.getBannerPatterns().toString()).equals("NONE")) {
                for (Pattern patterns : itemMap.getBannerPatterns()) {
                    patternString.append(patterns.getColor()).append(":").append(ItemHandler.getPatternName(patterns)).append(", ");
                }
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(patternString.substring(0, patternString.length())))) {
                    patternList.append("&a").append(split).append(" /n ");
                }
            }
            creatingPane.addButton(new Button(fillerPaneGItem), 4);
            creatingPane.addButton(new Button(headerStack(player, itemMap)));
            creatingPane.addButton(new Button(fillerPaneGItem), 4);
            creatingPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""), 1, false, false, "&c&lMaterial", "&7", "&7*Set the material of the item.", "&9&lMATERIAL: &a" +
                    itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : "")), event -> {
                if (itemMap.getDynamicMaterials() != null && !itemMap.getDynamicMaterials().isEmpty()) {
                    animateMaterialPane(player, itemMap);
                } else {
                    materialPane(player, itemMap, 1, 0);
                }
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, false, "&c&lSlot", "&7", "&7*Set the slot that the", "&7item will be given in.", (itemMap.getMultipleSlots() != null &&
                    !itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSLOT: &a" + itemMap.getSlot().toUpperCase())), event -> switchPane(player, itemMap, 1)));
            creatingPane.addButton(new Button(ItemHandler.getItem("DIAMOND", itemMap.getCount(player), false, false, "&b&lCount", "&7", "&7*Set the amount of the", "&7item to be given.", "&9&lCOUNT: &a" +
                    (itemMap.getCount(player) != 1 ? itemMap.getRawCount() : "1")), event -> countPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&b&lName", "&7", "&7*Set the name of the item.", "&9&lNAME: &f" + StringUtils.nullCheck(itemMap.getCustomName())), event -> { // false - Temp identifier
                final InventoryHolder inventoryHolder = event.getInventory().getHolder();
                if (itemMap.getDynamicNames() != null && itemMap.isAnimated()) {
                    animatedNamePane(player, itemMap);
                } else {
                    if (!StringUtils.nullCheck(itemMap.getCustomName()).equals("NONE")) {
                        itemMap.setCustomName(null);
                        creatingPane(player, itemMap);
                    } else if (inventoryHolder != null) {
                        ((Interface) inventoryHolder).onTyping(CompatUtils.getPlayer(event.getView()));
                        Menu.setTypingMenu(true, player, ((Interface) inventoryHolder));
                    }
                }
            }, query -> query.onClose(stateSnapshot -> creatingPane(stateSnapshot.getPlayer(), itemMap))
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != Query.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        itemMap.setCustomName(StringUtils.restoreColor(stateSnapshot.getText()));
                        Menu.setTypingMenu(false, player, null);
                        creatingPane(stateSnapshot.getPlayer(), itemMap);
                        return Collections.singletonList(Query.ResponseAction.close());
                    })
                    .itemLeft(ItemHandler.getItem("NAME_TAG", 1, false, true, " ", "&bThis is what the raw data looks like."))
                    .itemRight(ItemHandler.getItem("GOLD_NUGGET", 1, true, true, "&c&n&lTips", "&aType your answer into the query box!", "&7", ItemJoin.getCore().getLang().getLangMessage("commands.menu.inputType").replace("%input%", "NAME").replace("%prefix% ", "").replace("%prefix%", ""), ItemJoin.getCore().getLang().getLangMessage("commands.menu.inputExample").replace("%input_example%", "&bUltimate Sword").replace("%prefix% ", "").replace("%prefix%", "")))
                    .itemOutput(ItemHandler.getItem("IRON_INGOT", 1, false, true, "&bStart typing...", "&aThis is what the text will look like."))
                    .title("Type the item name:"), 0));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&b&lLore", "&7", "&7*Set the lore of the item.", "&9&lLORE: &f" + StringUtils.nullCheck(itemMap.getCustomLore().toString())), event -> {
                if (itemMap.getDynamicLores() != null && !itemMap.getDynamicLores().isEmpty()) {
                    animatedLorePane(player, itemMap);
                } else {
                    lorePane(player, itemMap);
                }
            }, event -> {
                itemMap.setCustomLore(StringUtils.split(StringUtils.restoreColor(event.getMessage())));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "LORE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                creatingPane(event.getPlayer(), itemMap);
            }));
            creatingPane.addButton(new Button(ItemHandler.setDurability(ItemHandler.getItem("DIAMOND_BOOTS", 1, false, false, "&e&lData", "&7", "&7*Set the damage or the", "&7custom texture of the item."), 160), event -> dataPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, false, "&e&lCommand Settings", "&7", "&7*Define commands for the item", "&7which execute upon being", "&7interacted with."), event -> commandPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("ENCHANTED_BOOK", 1, false, false, "&b&lEnchantments", "&7", "&7*Add enchants to make the", "&7item sparkle and powerful.", "&9&lENCHANTMENTS: &a" +
                    (!StringUtils.nullCheck(itemMap.getEnchantments().toString()).equals("NONE") ? "&a" + enchantList : "NONE")), event -> enchantPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&b&lItemflags", "&7", "&7*Special flags that will give", "&7the item abilities and", "&7custom features.", "&9&lITEMFLAGS: &a" +
                    (!StringUtils.nullCheck(itemMap.getItemFlags()).equals("NONE") ? "&a" + itemflagsList : "NONE")), event -> flagPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&b&lTriggers", "&7", "&7*When the players act upon these", "&7events, the item will be given.", "&9&lTRIGGERS: &a" +
                    (!StringUtils.nullCheck(itemMap.getTriggers()).equals("NONE") ? "&a" + triggersList : "NONE")), event -> triggerPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REDSTONE_TORCH" : "76"), 1, false, false, "&b&lPermission Node", "&7", "&7*Custom permission node that", "&7will be required by a permission", "&7plugin to receive the item.", "&7", "&c&lNote: &7You can use a &c&l! &7symbol", "&7to invert the requirement such", "&7as !fish.cakes, do NOT include any",
                    "&7other special characters or spaces.", "&7", "&7Setting this bypasses the", "&7config.yml Permissions Settings.", "&7", "&9&lPERMISSION-NODE: &a" + StringUtils.nullCheck(itemMap.getPermissionNode())), event -> {
                if (!StringUtils.nullCheck(itemMap.getPermissionNode()).equals("NONE")) {
                    itemMap.setPerm(null);
                    creatingPane(player, itemMap);
                } else {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "CUSTOM PERMISSION").with(Holder.INPUT_EXAMPLE, "itemjoin.ultra");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }
            }, event -> {
                itemMap.setPerm(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "CUSTOM PERMISSION");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                creatingPane(event.getPlayer(), itemMap);
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BEDROCK" : "7"), 1, false, false, "&b&lDisabled Worlds", "&7", "&7*Define the world(s) that the", "&7item will &l&nNOT&7 be given in.",
                    "&9&lDISABLED-WORLDS: &a" + (!StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()).equals("NONE") ? "&a" + disabledList : "NONE")), event -> worldPane(player, itemMap, 0)));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, false, false, "&b&lEnabled Worlds", "&7", "&7*Define the world(s) that the", "&7item will be given in.",
                    "&9&lENABLED-WORLDS: &a" + (!StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()).equals("NONE") ? "&a" + enabledList : "NONE")), event -> worldPane(player, itemMap, 1)));
            creatingPane.addButton(new Button(ItemHandler.getItem("REDSTONE_BLOCK", 1, true, false, "&b&lDisabled Regions", "&7", "&7*Define the region(s) that the", "&7item will &l&nNOT&7 be given in.", (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ?
                            "&9&lDISABLED-REGIONS: &a" + (!StringUtils.nullCheck(itemMap.getDisabledRegions().toString()).equals("NONE") ? "&a" + disabledRegionList : "NONE") : ""), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7"),
                    (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), event -> {
                if (ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
                    regionPane(player, itemMap, 0);
                }
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, true, false, "&b&lEnabled Regions", "&7", "&7*Define the region(s) that the", "&7item will be given in.", (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ?
                            "&9&lENABLED-REGIONS: &a" + (!StringUtils.nullCheck(itemMap.getEnabledRegions().toString()).equals("NONE") ? "&a" + enabledRegionList : "NONE") : ""), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7"),
                    (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), event -> {
                if (ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
                    regionPane(player, itemMap, 1);
                }
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STICKY_PISTON" : "29"), 1, false, false, "&e&lAnimation Settings", "&7", "&7*Define animations for the item", "&7Example: Custom iterations for the",
                    "&7items name, lore, and material type"), event -> animationPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_FENCE" : "FENCE"), 1, false, false, "&b&lLimit-Modes", "&7", "&7*Define the gamemode(s) that the", "&7item will be limited to.", "&9&lLIMIT-MODES: &a" +
                    StringUtils.nullCheck(itemMap.getLimitModes())), event -> limitPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem("NETHER_STAR", 1, false, false, "&b&lProbability", "&7", "&7*Define the chance that the", "&7item will be given to the player.", "&7", "&c&lNOTICE:&7 Only ONE item defined with", "&7a probability value will be selected.", "&7Probability is the same as a dice roll.", "&7", "&9&lPROBABILITY: &a" +
                    StringUtils.nullCheck(itemMap.getProbability() + "&a%")), event -> {
                if (!StringUtils.nullCheck(itemMap.getProbability() + "&a%").equals("NONE")) {
                    itemMap.setProbability(-1);
                    creatingPane(player, itemMap);
                } else {
                    probabilityPane(player, itemMap);
                }
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, false, "&b&lUsage Cooldown", "&7", "&7*Define the cooldown for", "&7interacting with the item.", "&9&lUSE-COOLDOWN: &a" +
                    StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7")), event -> {
                if (!StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7").equals("NONE")) {
                    itemMap.setInteractCooldown(0);
                    creatingPane(player, itemMap);
                } else {
                    usePane(player, itemMap);
                }
            }));
            creatingPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false, "&b&lConditions", "&7", "&7*Define conditions for triggers", "&7and the disposable itemflag.", "&9Enabled: &a" +
                    ((itemMap.getTriggerConditions() != null && !itemMap.getTriggerConditions().isEmpty()) || (itemMap.getDisposableConditions() != null && !itemMap.getDisposableConditions().isEmpty())
                            || (itemMap.getCommandConditions() != null && !itemMap.getCommandConditions().isEmpty()) ? "YES" : "NONE")), event -> conditionsPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, false, "&b&lToggle", "&7", "&7*Specify command(s) players can", "&7execute to enable or disable the", "&7custom item for themselves.", "&9Enabled: &a" + (itemMap.getToggleCommands() != null && !itemMap.getToggleCommands().isEmpty() ? "YES" : "NONE")), event -> togglePane(player, itemMap)));
            creatingPane.addButton(new Button(fillerPaneGItem));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, false, false, "&b&lRecipe", "&7", "&7*Define the recipe to be", "&7able to craft this item.", "&9Enabled: &a" + (itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty() ? "YES" : "NONE")), event -> recipePane(player, itemMap)));
            if (ServerUtils.hasSpecificUpdate("1_20") && ItemHandler.isArmor(itemMap.getMaterial().toString())) {
                String trimPattern = "NONE";
                if (itemMap.getTrimPattern() != null && !itemMap.getTrimPattern().isEmpty()) {
                    final Map.Entry<String, String> entry = itemMap.getTrimPattern().entrySet().iterator().next();
                    trimPattern = entry.getKey() + ":" + entry.getValue();
                }
                final String trimEnabled = trimPattern;
                creatingPane.addButton(new Button(ItemHandler.getItem("DUNE_ARMOR_TRIM_SMITHING_TEMPLATE", 1, false, true, "&b&lTrim Pattern", "&7", "&7*Define the pattern to be", "&7set to the armor.", "&9&lArmor Meta: &a" + trimPattern), event ->
                {
                    if (trimEnabled.equalsIgnoreCase("NONE")) {
                        trimPane(player, itemMap);
                    } else {
                        final Map<String, String> trimMap = new HashMap<>();
                        itemMap.setTrimPattern(trimMap);
                        creatingPane(player, itemMap);
                    }
                }));
            } else {
                creatingPane.addButton(new Button(fillerPaneGItem));
            }
            creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_INGOT", 1, false, false, "&e&lDrop Chances", "&7", "&7*Define the drop chance for receiving", "&7this item from mobs or breaking blocks."), event -> dropsPane(player, itemMap)));
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, false, false, "&c&lNBT Properties", "&7", "&7*Define specific NBT Properties", "&7to be set to the item.", "&9Enabled: &a" + (itemMap.getNBTValues() != null && !itemMap.getNBTValues().isEmpty() ? "YES" : "NONE")), event -> nbtPane(player, itemMap)));
            if (itemMap.getMaterial().toString().contains("MAP")) {
                creatingPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&e&lMap Image", "&7", "&7*Adds a custom map image that", "&7will be displayed when held.", "&7", "&7Place the custom map image",
                        "&7in the MAIN ItemJoin folder.", "&7", "&7The map CAN be a GIF but", "&7must be a 128x128 pixel image.", "&9&lImage: &a" + StringUtils.nullCheck(itemMap.getMapImage())), event -> {
                    if (!StringUtils.nullCheck(itemMap.getMapImage()).equals("NONE")) {
                        itemMap.setMapImage(null);
                        creatingPane(player, itemMap);
                    } else {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MAP IMAGE").with(Holder.INPUT_EXAMPLE, "minecraft.png OR minecraft-dance.gif");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }
                }, event -> {
                    itemMap.setMapImage(ChatColor.stripColor(event.getMessage()));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MAP IMAGE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    creatingPane(event.getPlayer(), itemMap);
                }));
            } else if (itemMap.getMaterial().toString().contains("ARROW") && !itemMap.getMaterial().toString().contains("TIPPED_ARROW")) {
                creatingPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, false, false, "&e&lTeleport", "&7", "&7*Set the arrow to teleport", "&7the player upon landing.", "&9&lEnabled: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()),
                        event -> teleportPane(player, itemMap, 0)));
            } else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
                Interface colorPane = new Interface(true, 6, exitButton, GUIName, player);
                colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> creatingPane(player, itemMap)));
                for (DyeColor color : DyeColor.values()) {
                    colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? color.name() +  "_DYE" : "351:8"), 1, false, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your firework charge."), event -> {
                        itemMap.setChargeColor(color);
                        creatingPane(player, itemMap);
                    }));
                }
                creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_DYE" : "351:9"), 1, false, false, "&e&lCharge Color", "&7", "&7*Set the color of", "&7the firework star.", "&9&lColor: &a" +
                        StringUtils.nullCheck(itemMap.getChargeColor() + "")), event -> {
                    if (!StringUtils.nullCheck(itemMap.getChargeColor() + "").equals("NONE")) {
                        itemMap.setChargeColor(null);
                        creatingPane(player, itemMap);
                    } else {
                        colorPane.open(player);
                    }
                }));
            } else if (itemMap.getMaterial().isEdible() || itemMap.getMaterial().toString().equalsIgnoreCase("POTION")) {
                creatingPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the item.", "&9&lPotion-Effects: &a" + StringUtils.nullCheck(potionList.toString())),
                        event -> potionPane(player, itemMap, 0)));
            } else if (itemMap.getMaterial().toString().contains("BANNER")) {
                creatingPane.addButton(new Button(ItemHandler.getItem("CLAY_BALL", 1, false, false, "&e&lBanner Patterns", "&7", "&7*Set custom patterns that", "&7will appear on the item.", "&9&lBanner-Meta: &a" + StringUtils.nullCheck(patternList.toString())),
                        event -> bannerPane(player, itemMap)));
            } else if (!ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot") && !itemMap.getMaterial().toString().contains("LEATHER_")) {
                creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&a&lAttributes", "&7", "&7*Add a custom attribute to", "&7your armor or weapon.", (!StringUtils.nullCheck(attributeList.toString()).equals("NONE") ? "&9&lAttributes: &a" + attributeList : "")), event -> attributePane(player, itemMap, false)));
            } else if (itemMap.getMaterial().toString().contains("SHULKER")) {
                creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PURPLE_SHULKER_BOX" : "229"), 1, false, false, "&a&lContents", "&7", "&7*Add existing custom items into", "&7the contents of this box.", "&9Enabled: &a" + (itemMap.getContents() != null && !itemMap.getContents().isEmpty() ? "YES" : "NONE")), event -> {
                    Interface contentsPane = new Interface(true, 6, exitButton, GUIName, player);
                    SchedulerUtils.runAsync(() -> setPage(player, contentsPane, ItemUtilities.getUtilities().copyItems(), itemMap, null, 0));
                    contentsPane.open(player);
                }));
            } else {
                creatingPane.addButton(new Button(ItemHandler.getItem("MAGMA_CREAM", 1, false, false, "&e&lOther Settings", "&7", "&7*Settings that are specific", "&7to the item's material type.", (specialItem(itemMap) ? "" : "&7"),
                        (specialItem(itemMap) ? "" : "&c&lERROR: &7A " + itemMap.getMaterial().name() + " &7is NOT"), (specialItem(itemMap) ? "" : "&7a special material type.")), event -> {
                    if (specialItem(itemMap)) {
                        otherPane(player, itemMap);
                    }
                }));
            }
            creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return to the main menu.", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> returnConfirm(player, itemMap)));
            creatingPane.addButton(new Button(fillerPaneBItem), 3);
            creatingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIME_WOOL" : "WOOL:5"), 1, false, false, "&a&l&nSave to Config", "&7", "&7*Saves the custom item", "&7settings to the items.yml file."), event -> {
                itemMap.saveToConfig();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.ITEM, itemMap.getConfigName());
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.itemSaved", player, placeHolders);
                PluginData.getInfo().hardReload(true);
                player.closeInventory();
            }));
            creatingPane.addButton(new Button(fillerPaneBItem), 3);
            creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return you to the main menu.", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> returnConfirm(player, itemMap)));
        });
        creatingPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for confirming the return to the main menu.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void returnConfirm(final Player player, final ItemMap itemMap) {
        Interface returnPane = new Interface(false, 1, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            returnPane.addButton(new Button(fillerPaneBItem));
            returnPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "RED_WOOL" : "WOOL:14"), 1, false, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return to the", "&7main menu, all modified", "&7settings will be lost.", "&7", "&c&lWARNING: &cThis item has &lNOT&c been saved!"), event -> startMenu(player)));
            returnPane.addButton(new Button(fillerPaneBItem), 2);
            returnPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIME_WOOL" : "WOOL:5"), 1, false, false, "&a&l&nSave to Config", "&7", "&7*Saves the custom item", "&7settings to the items.yml file."), event -> {
                itemMap.saveToConfig();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.ITEM, itemMap.getConfigName());
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.itemSaved", player, placeHolders);
                PluginData.getInfo().hardReload(true);
                startMenu(player);
            }));
            returnPane.addButton(new Button(fillerPaneBItem), 2);
            returnPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_WOOL" : "WOOL:4"), 1, false, false, "&e&l&nModify Settings", "&7", "&7*Continue modifying the", "&7custom item settings."), event -> creatingPane(player, itemMap)));
            returnPane.addButton(new Button(fillerPaneBItem));
        });
        returnPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     * @param stage  - The type of selection Pane.
     */
    private static void numberPane(final Player player, final int stage) {
        Interface numberPane = new Interface((stage != 2), (stage == 2 ? 2 : 6), exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            numberPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> {
                if (stage == 1) {
                    itemSettings(player);
                } else if (stage == 3) {
                    clearPane(player);
                }
            }));
            for (int i = 0; i <= (stage == 1 || stage == 3 ? 64 : stage == 2 ? 8 : 0); i++) {
                final int k = i;
                if (stage == 1) {
                    numberPane.addButton(new Button(ItemHandler.getItem((i == 0 ? (ServerUtils.hasSpecificUpdate("1_13") ? "RED_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:14") : (ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11")), (i == 0 ? 1 : i), false, false, "&9&lDelay: &a&l" + i, "&7",
                            "&7*Click to set the trigger", "&7delay for giving custom items.", "&aSecond(s): &e" + (i == 0 ? 0 : ((Double.parseDouble(String.valueOf(i))) / 2))), event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("items-Delay", k);
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "items.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
                } else if (stage == 2) {
                    numberPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), (i == 0 ? 1 : i), false, false, "&9&lSlot: &a&l" + i + " &9&l[HOTBAR]", "&7",
                            "&7*Click to set the held item slot", "&7that is automatically selected", "&7when performing a held item trigger.", "&aSlot: &e" + k), event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Settings.HeldItem-Slot", k);
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> itemSettings(player));
                    }));
                } else if (stage == 3) {
                    numberPane.addButton(new Button(ItemHandler.getItem((i == 0 ? (ServerUtils.hasSpecificUpdate("1_13") ? "RED_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:14") : (ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11")), (i == 0 ? 1 : i), false, false, "&9&lDelay: &a&l" + i, "&7",
                            "&7*Click to set the trigger", "&7delay for clearing items.", "&aSecond(s): &e" + (i == 0 ? 0 : (Double.parseDouble(String.valueOf(i))))), event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Delay-Tick", k);
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> clearPane(player));
                    }));
                }
            }
            if (stage == 2) {
                numberPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
                numberPane.addButton(new Button(fillerPaneBItem), 7);
                numberPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
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
        Interface languagePane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            final String lang = ItemJoin.getCore().getConfig("config.yml").getString("Language");
            final String language = (lang != null && !lang.isEmpty()) ? lang.replace(" ", "") : "ENGLISH";
            languagePane.addButton(new Button(fillerPaneBItem), 4);
            languagePane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2", 1, language.equalsIgnoreCase("ENGLISH"), false, "&6&l&nEnglish", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lEnglish&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("ENGLISH") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("ENGLISH")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "ENGLISH");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(fillerPaneBItem), 4);
            languagePane.addButton(new Button(ItemHandler.getItem("SAND", 1, language.equalsIgnoreCase("SPANISH"), false, "&6&l&nSpanish", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lSpanish&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("SPANISH") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("SPANISH")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "SPANISH");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOW_BLOCK" : "80"), 1, language.equalsIgnoreCase("RUSSIAN"), false, "&6&l&nRussian", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lRussian&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("RUSSIAN") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("RUSSIAN")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "RUSSIAN");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE_BRICKS" : "98"), 1, language.equalsIgnoreCase("FRENCH"), false, "&6&l&nFrench", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lFrench&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("FRENCH") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("FRENCH")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "FRENCH");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "DIORITE" : "1:3"), 1, language.equalsIgnoreCase("TRADITIONALCHINESE"), false, "&6&l&nTraditional Chinese", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lTraditional Chinese&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("TRADITIONALCHINESE") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("TRADITIONALCHINESE")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "TRADITIONAL CHINESE");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "POLISHED_DIORITE" : "1:4"), 1, language.equalsIgnoreCase("SIMPLIFIEDCHINESE"), false, "&6&l&nSimplified Chinese", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lSimplified Chinese&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("SIMPLIFIEDCHINESE") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("SIMPLIFIEDCHINESE")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "SIMPLIFIED CHINESE");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAVEL" : "13"), 1, language.equalsIgnoreCase("PORTUGUESE"), false, "&6&l&nPortuguese", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lPortuguese&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("PORTUGUESE") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("PORTUGUESE")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "PORTUGUESE");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PRISMARINE" : "168"), 1, language.equalsIgnoreCase("POLISH"), false, "&6&l&nPolish", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lPolish&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("POLISH") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("POLISH")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "POLISH");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SANDSTONE" : "24"), 1, language.equalsIgnoreCase("DUTCH"), false, "&6&l&nDutch", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lDutch&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("DUTCH") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("DUTCH")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "DUTCH");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SMOOTH_STONE" : "43:8"), 1, language.equalsIgnoreCase("ITALIAN"), false, "&6&l&nItalian", "&7",
                    "&7*Sets the messages sent by", "&7the plugin to the player", "&7to be written in &c&lItalian&7.", "&7This is the type of lang.yml file", "&7generated in the plugin folder.",
                    "&9&lENABLED: &a" + (language.equalsIgnoreCase("ITALIAN") + "").toUpperCase()), event -> {
                if (!language.equalsIgnoreCase("ITALIAN")) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Language", "ITALIAN");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> languagePane(player));
                }
            }));
            languagePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
            languagePane.addButton(new Button(fillerPaneBItem), 7);
            languagePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
        });
        languagePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void activeCommands(final Player player) {
        Interface activePane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            final String triggers = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.triggers");
            final String enabledList = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds");
            activePane.addButton(new Button(fillerPaneBItem), 4);
            activePane.addButton(new Button(ItemHandler.getItem("BOOK", 1, !ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands").isEmpty(), false, "&b&l&nCommands", "&7",
                    "&7*Specify a list of commands to be", "&7executed upon performing a trigger.",
                    "&9&lENABLED: &a" + (!ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands").isEmpty() ? "YES" : "NO")), event -> altCommandPane(player, null, 4)));
            activePane.addButton(new Button(fillerPaneBItem), 6);
            activePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, false, "&a&lSequence", "&7", "&7*The order that the command lines",
                    "&7will be executed in.", "&9&lCOMMANDS-SEQUENCE: &a" + StringUtils.nullCheck(ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.commands-sequence"))), event -> sequencePane(player, null, 4)));
            activePane.addButton(new Button(fillerPaneBItem));
            activePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false,
                    "&bTriggers", "&7", "&7*This will be the triggers", "&7that will cause the command lines", "&7to execute.",
                    "&9&lENABLED: &a" + ((triggers != null && !triggers.isEmpty() && !StringUtils.containsIgnoreCase(triggers, "DISABLE")) ? triggers : "FALSE").toUpperCase()),
                    event -> triggerPane(player, 4)));
            activePane.addButton(new Button(fillerPaneBItem));
            activePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, false, false, "&b&lEnabled Worlds", "&7", "&7*Define the world(s) that the", "&7commands will be executed in.",
                    "&9&lENABLED-WORLDS: &a" + (!StringUtils.nullCheck(enabledList).equals("NONE") ? "&a" + enabledList : "NONE")), event -> worldPane(player, null, 4)));
            activePane.addButton(new Button(fillerPaneBItem), 2);
            activePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
            activePane.addButton(new Button(fillerPaneBItem), 7);
            activePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
        });
        activePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void databasePane(final Player player) {
        Interface databasePane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            databasePane.addButton(new Button(fillerPaneBItem), 4);
            databasePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL"), false, "&b&l&nMySQL", "&7",
                    "&7*If the plugin should use a", "&7MySQL connection instead of the", "&7local SQLite database inside", "&7the plugin folder.", "&7", "&c&lNote: &7Keep this set to false", "&7if you do not know what", "&7you are doing.",
                    "&7", "&c&l&nWARNING: &7Changing this value requires", "&7a server restart for the", "&7changes to take affect.",
                    "&9&lENABLED: &a" + String.valueOf(ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL")).toUpperCase()),
                    event -> {
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Database.MySQL", !ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL"));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> databasePane(player));
                    }));
            databasePane.addButton(new Button(fillerPaneBItem), 4);
            databasePane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&a&lHost", "&7",
                    "&7*Set the &c&lHost &7for", "&7the MySQL database connection.", "&9&lHOST: &a" + ItemJoin.getCore().getConfig("config.yml").getString("Database.host")),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "HOST ADDRESS").with(Holder.INPUT_EXAMPLE, "localhost");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "HOST ADDRESS");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Database.host", ChatColor.stripColor(event.getMessage()));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, false, "&a&lPort", "&7",
                    "&7*Set the &c&lPort &7for", "&7the MySQL database connection.", "&9&lPORT: &a" + ItemJoin.getCore().getConfig("config.yml").getString("Database.port")),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ADDRESS PORT").with(Holder.INPUT_EXAMPLE, "3306");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ADDRESS PORT");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Database.port", Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(fillerPaneBItem));
            final String databaseString = (ItemJoin.getCore().getConfig("config.yml").getString("Database.table") != null ? "Database.table" : "Database.database");
            databasePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_WOOD" : "17"), 1, false, false, "&a&lTable", "&7",
                    "&7*Set the &c&lTable &7for", "&7the MySQL database connection.", "&9&lTABLE: &a" + ItemJoin.getCore().getConfig("config.yml").getString(databaseString)),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TABLE NAME").with(Holder.INPUT_EXAMPLE, "ITEMJOIN_LOCAL");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TABLE NAME");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(databaseString, ChatColor.stripColor(event.getMessage()));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(fillerPaneBItem));
            databasePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&a&lPrefix", "&7",
                    "&7*Set the &c&lTable &7for", "&7the MySQL database connection.", "&9&lTABLE: &a" + ItemJoin.getCore().getConfig("config.yml").getString("Database.prefix")),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TABLE PREFIX").with(Holder.INPUT_EXAMPLE, "IJ_");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TABLE PREFIX");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Database.prefix", ChatColor.stripColor(event.getMessage()));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(fillerPaneBItem));
            databasePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_STAIRS" : "53"), 1, false, false, "&a&lUser", "&7",
                    "&7*Set the &c&lUser &7for", "&7the MySQL database connection.", "&9&lUSER: &a***********"),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "USER").with(Holder.INPUT_EXAMPLE, "rockinchaos");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "USER");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Database.user", ChatColor.stripColor(event.getMessage()));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&a&lPassword", "&7",
                    "&7*Set the &c&lPassword &7for", "&7the MySQL database connection.", "&9&lPORT: &a****"),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PASSWORD").with(Holder.INPUT_EXAMPLE, "cooldude6");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PASSWORD");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Database.pass", ChatColor.stripColor(event.getMessage()));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> databasePane(player));
            }));
            databasePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
            databasePane.addButton(new Button(fillerPaneBItem), 7);
            databasePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the config settings menu."), event -> configSettings(player)));
        });
        databasePane.open(player);
    }


// =======================================================================================================================================================================================================================================

//  ============================================== //
//                 Settings Menus      	           //
//  ============================================== //

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void triggerPane(final Player player, final int stage) {
        final Interface triggerPane = new Interface(false, 3, exitButton, GUIName, player);
        final String triggerOption = (stage == 4 ? "&7*Executes the commands when the" : (stage == 0 ? "&7*Gives the custom item when the" : "&7*Sets the held item slot when the"));
        final String triggerString = (stage == 4 ? "Active-Commands.triggers" : (stage == 0 ? "Settings.Default-Triggers" : "Settings.HeldItem-Triggers"));
        SchedulerUtils.runAsync(() -> {
            List<String> triggers = new ArrayList<>();
            try {
                final String triggerSet = ItemJoin.getCore().getConfig("config.yml").getString(triggerString);
                if (triggerSet != null && !triggerSet.isEmpty()) {
                    for (String trigger : triggerSet.replace(" ", "").split(",")) {
                        if (trigger != null && !trigger.isEmpty()) {
                            triggers.add(trigger);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            triggerPane.addButton(new Button(fillerPaneBItem), 3);
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "MAP"), 1, StringUtils.containsValue(triggers, "FIRST-JOIN"), false, "&e&l&nFirst Join", "&7", triggerOption, "&7player logs into the server",
                    "&7for the first time only.", "&7This will overwrite any triggers", "&7such as respawn, and world-switch.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "FIRST-JOIN") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "FIRST-JOIN")) {
                    triggers.remove("FIRST-JOIN");
                } else {
                    triggers.add("FIRST-JOIN");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, StringUtils.containsValue(triggers, "FIRST-WORLD"), false, "&e&l&nFirst World", "&7", triggerOption, "&7player enters each of the defined", "&7worlds for the first time.", "&7",
                    "&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "FIRST-WORLD") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "FIRST-WORLD")) {
                    triggers.remove("FIRST-WORLD");
                } else {
                    triggers.add("FIRST-WORLD");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "TOTEM_OF_UNDYING" : "322:1", 1, StringUtils.containsValue(triggers, "FIRST-LIFE"), false, "&e&l&nFirst Life", "&7", triggerOption, "&7player logs into the server",
                    "&7for the first time only,", "&7but will give the item", "&7EVERY TIME on player RESPAWN.", "&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "FIRST-LIFE") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "FIRST-LIFE")) {
                    triggers.remove("FIRST-LIFE");
                } else {
                    triggers.add("FIRST-LIFE");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(fillerPaneBItem), 3);
            triggerPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, (StringUtils.containsValue(triggers, "DISABLE") || StringUtils.containsValue(triggers, "DISABLED")), false, "&c&l&nDISABLED", "&7", "&7*Disables the use of triggers", "&7for this section.", "&7", "&9&lENABLED: &a" + ((StringUtils.containsValue(triggers, "DISABLE") || StringUtils.containsValue(triggers, "DISABLED")) + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "DISABLE") || StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                    triggers.remove("DISABLE");
                } else if (!(StringUtils.containsValue(triggers, "DISABLE") || StringUtils.containsValue(triggers, "DISABLED"))) {
                    triggers.clear();
                    triggers.add("DISABLED");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, StringUtils.containsValue(triggers, "JOIN"), false, "&e&l&nJoin", "&7", triggerOption, "&7player logs into the server.",
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
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(triggers, "RESPAWN"), false, "&e&l&nRespawn", "&7", triggerOption, "&7player respawns from a death event.", "&9&lENABLED: &a" +
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
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, StringUtils.containsValue(triggers, "WORLD-SWITCH"), false, "&e&l&nWorld Switch", "&7", triggerOption, "&7player teleports to one", "&7of the specified worlds.",
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
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("LEVER", 1, StringUtils.containsValue(triggers, "GAMEMODE-SWITCH"), false, "&e&l&nGamemode Switch", "&7", triggerOption, "&7player changes gamemodes to any", "&7of the defined limit-modes.",
                    "&9&lENABLED: &a" + (StringUtils.containsValue(triggers, "GAMEMODE-SWITCH") + "").toUpperCase()), event -> {
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
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, StringUtils.containsValue(triggers, "REGION-ENTER"), false, "&e&l&nRegion Enter", "&7", triggerOption, "&7player enters any of the enabled-regions.", "&9&lENABLED: &a" +
                    (StringUtils.containsValue(triggers, "REGION-ENTER") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "REGION-ENTER")) {
                    triggers.remove("REGION-ENTER");
                } else {
                    triggers.add("REGION-ENTER");
                    triggers.add("REGION-ACCESS");
                    triggers.add("REGION-EGRESS");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "HOPPER_MINECRAFT" : "408"), 1, StringUtils.containsValue(triggers, "REGION-LEAVE"), false, "&e&l&nRegion Leave", "&7", triggerOption.replace("Gives", "Removes"), "&7player leaves any of the enabled-regions.", "&9&lENABLED: &a" +
                    (StringUtils.containsValue(triggers, "REGION-LEAVE") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "REGION-LEAVE")) {
                    triggers.remove("REGION-LEAVE");
                } else {
                    triggers.add("REGION-LEAVE");
                    triggers.remove("REGION-ACCESS");
                    triggers.remove("REGION-EGRESS");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "TNT_MINECART" : "407"), 1, StringUtils.containsValue(triggers, "REGION-ACCESS"), false, "&e&l&nRegion Access", "&7", triggerOption, "&7player enters any of the enabled-regions", "&7and removes the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
                    (StringUtils.containsValue(triggers, "REGION-ACCESS") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "REGION-ACCESS")) {
                    triggers.remove("REGION-ACCESS");
                } else {
                    triggers.add("REGION-ACCESS");
                    triggers.remove("REGION-ENTER");
                    triggers.remove("REGION-LEAVE");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CHEST_MINECART" : "342"), 1, StringUtils.containsValue(triggers, "REGION-EGRESS"), false, "&e&l&nRegion Egress", "&7", triggerOption.replace("Gives", "Removes"), "&7player enters any of the enabled-regions", "&7and gives the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
                    (StringUtils.containsValue(triggers, "REGION-EGRESS") + "").toUpperCase()), event -> {
                if (StringUtils.containsValue(triggers, "REGION-EGRESS")) {
                    triggers.remove("REGION-EGRESS");
                } else {
                    triggers.add("REGION-EGRESS");
                    triggers.remove("REGION-ENTER");
                    triggers.remove("REGION-LEAVE");
                }
                if (triggers.isEmpty()) {
                    triggers.add("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLED")) {
                    triggers.remove("DISABLED");
                } else if (StringUtils.containsValue(triggers, "DISABLE")) {
                    triggers.remove("DISABLE");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set(triggerString, triggers.toString().replace("[", "").replace("]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> triggerPane(player, stage));
            }));
            if (stage == 4) {
                triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
            }
            triggerPane.addButton(new Button(fillerPaneBItem), 7);
            if (stage == 4) {
                triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
            }
        });
        triggerPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void preventPane(final Player player) {
        Interface preventPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            final List<String> bypassList = new ArrayList<>();
            final String bypassSet = ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Bypass");
            if (bypassSet != null) {
                for (String bypass : bypassSet.replace(" ", "").split(",")) {
                    if (bypass != null && !bypass.isEmpty()) {
                        bypassList.add(bypass);
                    }
                }
            }
            preventPane.addButton(new Button(fillerPaneBItem), 3);
            preventPane.addButton(new Button(ItemHandler.getItem(StringUtils.containsValue(bypassList, "CREATIVE") ? (ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1") : (ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_APPLE" : "322"), 1, StringUtils.containsValue(bypassList, "CREATIVE"),
                    false, "&bCreative Bypass", "&7", "&7*Players in creative mode", "&7will ignore the prevent actions.",
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Prevent.Bypass", bypassList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> preventPane(player));
                    }));
            preventPane.addButton(new Button(fillerPaneBItem));
            preventPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(bypassList, "OP"), false,
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Prevent.Bypass", bypassList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> preventPane(player));
                    }));
            preventPane.addButton(new Button(fillerPaneBItem), 3);
            final String chat = (ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Chat") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Chat") : "DISABLE");
            final String pickups = (ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Pickups") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Pickups") : "DISABLE");
            final String itemMovement = (ItemJoin.getCore().getConfig("config.yml").getString("Prevent.itemMovement") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Prevent.itemMovement") : "DISABLE");
            final String selfDrops = (ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Self-Drops") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Self-Drops") : "DISABLE");
            final String deathDrops = (ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Death-Drops") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Death-Drops") : "DISABLE");
            preventPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false,
                    "&c&l&nPrevent Chat", "&7", "&7*Prevent players from being able", "&7to send chat messages.", "&7", "&7Useful if you are using BungeeChat.",
                    "&9&lENABLED: &a" + (((chat != null && !StringUtils.containsIgnoreCase(chat, "DISABLE")) ? chat : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Prevent.Chat")));
            preventPane.addButton(new Button(fillerPaneBItem));
            preventPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false,
                    "&c&l&nPrevent Pickups", "&7", "&7*Prevent players from picking up", "&7ANY items, not just custom items.",
                    "&9&lENABLED: &a" + (((pickups != null && !StringUtils.containsIgnoreCase(pickups, "DISABLE")) ? pickups : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Prevent.Pickups")));
            preventPane.addButton(new Button(fillerPaneBItem));
            preventPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, false, false,
                    "&c&l&nPrevent Movement", "&7", "&7*Prevent players from moving", "&7ANY items around in their", "&7inventory, not just custom items.",
                    "&9&lENABLED: &a" + (((itemMovement != null && !StringUtils.containsIgnoreCase(itemMovement, "DISABLE")) ? itemMovement : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Prevent.itemMovement")));
            preventPane.addButton(new Button(fillerPaneBItem));
            preventPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false,
                    "&c&l&nPrevent Drops", "&7", "&7*Prevent players from dropping", "&7ANY items, not just custom items.",
                    "&9&lENABLED: &a" + (((selfDrops != null && !StringUtils.containsIgnoreCase(selfDrops, "DISABLE")) ? selfDrops : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Prevent.Self-Drops")));
            preventPane.addButton(new Button(fillerPaneBItem));
            preventPane.addButton(new Button(ItemHandler.getItem("BONE", 1, false, false,
                    "&c&l&nPrevent Death Drops", "&7", "&7*Prevent players from dropping", "&7ANY items on death, not just custom items.",
                    "&9&lENABLED: &a" + (((deathDrops != null && !StringUtils.containsIgnoreCase(deathDrops, "DISABLE")) ? deathDrops : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Prevent.Death-Drops")));
            preventPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
            preventPane.addButton(new Button(fillerPaneBItem), 7);
            preventPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
        });
        preventPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void clearPane(final Player player) {
        Interface clearPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            final String clearType = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Type");
            if (clearType != null) {
                clearPane.addButton(new Button(fillerPaneBItem), 3);
                clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, clearType.equalsIgnoreCase("ALL"), false,
                        "&bType: &a&lALL", "&7", "&7*ALL items including other plugin(s)", "&7and vanilla items should be cleared", "&7upon performing a trigger.",
                        "&9&lENABLED: &a" + (clearType.equalsIgnoreCase("ALL") + "").toUpperCase()),
                        event -> {
                            if (!clearType.equalsIgnoreCase("ALL")) {
                                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                                dataFile.set("Clear-Items.Type", "ALL");
                                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                                PluginData.getInfo().softReload();
                                SchedulerUtils.runLater(2L, () -> clearPane(player));
                            }
                        }));
                clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "DANDELION" : "37"), 1, clearType.equalsIgnoreCase("VANILLA"), false,
                        "&bType: &a&lVANILLA", "&7", "&7*Only Vanilla items", "&7NOT (Custom ItemJoin item)", "&7should be cleared upon", "&7performing a trigger.",
                        "&9&lENABLED: &a" + (clearType.equalsIgnoreCase("VANILLA") + "").toUpperCase()),
                        event -> {
                            if (!clearType.equalsIgnoreCase("VANILLA")) {
                                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                                dataFile.set("Clear-Items.Type", "VANILLA");
                                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                                PluginData.getInfo().softReload();
                                SchedulerUtils.runLater(2L, () -> clearPane(player));
                            }
                        }));
                clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "33"), 1, clearType.equalsIgnoreCase("ITEMJOIN"), false,
                        "&bType: &a&lITEMJOIN", "&7", "&7*Only ItemJoin (custom items)", "&7should be cleared upon", "&7performing a trigger.",
                        "&9&lENABLED: &a" + (clearType.equalsIgnoreCase("ITEMJOIN") + "").toUpperCase()),
                        event -> {
                            if (!clearType.equalsIgnoreCase("ITEMJOIN")) {
                                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                                dataFile.set("Clear-Items.Type", "ITEMJOIN");
                                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                                PluginData.getInfo().softReload();
                                SchedulerUtils.runLater(2L, () -> clearPane(player));
                            }
                        }));
                clearPane.addButton(new Button(fillerPaneBItem), 3);
            }
            final String join = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Join") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Join") : "DISABLE");
            final String quit = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Quit") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Quit") : "DISABLE");
            final String worldSwitch = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.World-Switch") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.World-Switch") : "DISABLE");
            final String regionEnter = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Region-Enter") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Region-Enter") : "DISABLE");
            final String delayTick = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Delay-Tick") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Delay-Tick") : "DISABLE");
            final String options = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options") : "DISABLE");
            final String blackList = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist") : "DISABLE");
            final boolean guardEnabled = ItemJoin.getCore().getDependencies().getGuard().guardEnabled();
            clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false,
                    "&c&l&nJoin", "&7", "&7*Clears the items from the", "&7player upon joining the server.",
                    "&9&lENABLED: &a" + (((join != null && !StringUtils.containsIgnoreCase(join, "DISABLE")) ? join : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Clear-Items.Join")));
            clearPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false,
                    "&c&l&nQuit", "&7", "&7*Clears the items from the", "&7player upon quiting the server.",
                    "&9&lENABLED: &a" + (((quit != null && !StringUtils.containsIgnoreCase(quit, "DISABLE")) ? quit : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Clear-Items.Quit")));
            clearPane.addButton(new Button(fillerPaneBItem));
            clearPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, false,
                    "&c&l&nWorld-Switch", "&7", "&7*Clears the items from the", "&7player upon changing worlds.",
                    "&9&lENABLED: &a" + (((worldSwitch != null && !StringUtils.containsIgnoreCase(worldSwitch, "DISABLE")) ? worldSwitch : "FALSE")).toUpperCase()),
                    event -> worldPane(player, "Clear-Items.World-Switch")));
            clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "342"), 1, false, false,
                    "&b&lClear Delay", "&7", "&7*The number of second(s)", "&7to wait before clearing", "&7items from the player inventory.",
                    "&9&lDelay: &a" + (delayTick != null ? delayTick.toUpperCase() : "0")),
                    event -> numberPane(player, 3)));
            clearPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, false, false,
                    "&c&l&nRegion-Enter", "&7", "&7*Clears the items from the", "&7player upon entering", "&7a WorldGuard region.",
                    (guardEnabled ? "&9&lENABLED: &a" + ((regionEnter != null && !StringUtils.containsIgnoreCase(regionEnter, "DISABLE") ? regionEnter : "FALSE")).toUpperCase() : ""), (guardEnabled ? "" : "&7"), (guardEnabled ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (guardEnabled ? "" : "&7This button will do nothing...")),
                    event -> {
                        if (ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
                            worldPane(player, "Clear-Items.Region-Enter");
                        }
                    }));
            clearPane.addButton(new Button(fillerPaneBItem));
            clearPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, false, false,
                    "&b&lOptions", "&7", "&7*Actions to apply to", "&7the clear items triggers", "&7such as OP bypass.",
                    "&9&lENABLED: &a" + (((options != null && !StringUtils.containsIgnoreCase(options, "DISABLE")) ? options : "FALSE")).toUpperCase()),
                    event -> optionPane(player)));
            clearPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false,
                    "&b&lBlackList", "&7", "&7*Materials, Slots, or Item Names", "&7to be blacklisted from being", "&7cleared upon performing a trigger.",
                    "&9&lENABLED: &a" + ((blackList != null && !blackList.isEmpty()) + "").toUpperCase()),
                    event -> blacklistPane(player)));
            clearPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
            clearPane.addButton(new Button(fillerPaneBItem), 7);
            clearPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item settings menu."), event -> itemSettings(player)));
        });
        clearPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void blacklistPane(final Player player) {
        Interface blacklistPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            List<String> materials = new ArrayList<>();
            List<String> slots = new ArrayList<>();
            List<String> names = new ArrayList<>();
            try {
                final String blacklist = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist");
                if (blacklist != null) {
                    for (String value : blacklist.split(",")) {
                        String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
                        String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
                        if (valType.equalsIgnoreCase("id") && ItemHandler.getMaterial(inputResult.trim(), null) != Material.AIR) {
                            materials.add(inputResult.trim().toUpperCase());
                        } else if (valType.equalsIgnoreCase("slot")) {
                            slots.add(inputResult.trim().toUpperCase());
                        } else if (valType.equalsIgnoreCase("name")) {
                            names.add(inputResult.trim());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            blacklistPane.addButton(new Button(fillerPaneBItem), 3);
            blacklistPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, false,
                    "&b&l&nMaterials", "&7", "&7*The material to be blacklisted", "&7from being cleared.", "&7",
                    "&9&lMaterials: &a" + ((!materials.isEmpty() ? StringUtils.replaceLast(materials.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))),
                    event -> blacklistMatPane(player)));
            blacklistPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLASS" : "20"), 1, false, false,
                    "&b&l&nSlots", "&7", "&7*The inventory slots to be", "&7blacklisted from being cleared.", "&7",
                    "&9&lSlots: &a" + ((!slots.isEmpty() ? StringUtils.replaceLast(slots.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))),
                    event -> blacklistSlotPane(player)));
            blacklistPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false,
                    "&b&l&nNames", "&7", "&7*The items display names to", "&7be blacklisted from being cleared.", "&7",
                    "&9&lNames: &a" + ((!names.isEmpty() ? StringUtils.replaceLast(names.toString().replaceFirst("\\[", ""), "]", "") : "NONE"))),
                    event -> blacklistNamePane(player)));
            blacklistPane.addButton(new Button(fillerPaneBItem), 3);
            blacklistPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
            blacklistPane.addButton(new Button(fillerPaneBItem), 7);
            blacklistPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
        });
        blacklistPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void blacklistMatPane(final Player player) {
        Interface materialPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
            List<String> materials = new ArrayList<>();
            List<String> saveList = new ArrayList<>();
            try {
                final String blacklist = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist");
                if (blacklist != null) {
                    for (String value : blacklist.split(",")) {
                        String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
                        String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
                        if (valType.equalsIgnoreCase("id") && ItemHandler.getMaterial(inputResult.trim(), null) != Material.AIR) {
                            materials.add(inputResult.trim().toUpperCase());
                        } else if (!valType.equalsIgnoreCase("id") && !value.isEmpty()) {
                            saveList.add(value.trim());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            materialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, false, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL").with(Holder.INPUT_EXAMPLE, "IRON_SWORD");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != Material.AIR) {
                    if (!StringUtils.containsValue(materials, ChatColor.stripColor(event.getMessage()))) {
                        materials.add(ChatColor.stripColor(event.getMessage()).toUpperCase());
                    } else {
                        materials.remove(ChatColor.stripColor(event.getMessage()).toUpperCase());
                    }
                    for (String mat : materials) {
                        saveList.add("{id:" + mat + "}");
                    }
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> blacklistMatPane(player));
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noMaterial", player, placeHolders);
                    blacklistMatPane(player);
                }
            }));
            Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equals("AIR") && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, StringUtils.containsValue(materials, material.name()), false, "", "&7", "&7*Click to set the material.", "&7to be blacklisted from clearing.",
                            "&7", (StringUtils.containsValue(materials, material.name()) ? "&a&l&nENABLED: &e&lYES" : "")), event -> {
                        if (!StringUtils.containsValue(materials, material.name())) {
                            materials.add(material.name());
                        } else {
                            materials.remove(material.name());
                        }
                        for (String mat : materials) {
                            saveList.add("{id:" + mat + "}");
                        }
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
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
        Interface slotPane = new Interface(true, 6, exitButton, GUIName, player);
        Interface craftingPane = new Interface(false, 4, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            slotPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
            List<String> slots = new ArrayList<>();
            List<String> saveList = new ArrayList<>();
            try {
                final String blacklist = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist");
                if (blacklist != null) {
                    for (String value : blacklist.split(",")) {
                        String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
                        String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
                        if (valType.equalsIgnoreCase("slot")) {
                            slots.add(inputResult.trim().toUpperCase());
                        } else if (!value.isEmpty()) {
                            saveList.add(value.trim());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            craftingPane.addButton(new Button(fillerPaneGItem), 3);
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, StringUtils.containsValue(slots, "CRAFTING[1]"), false, "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CRAFTING[1]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CRAFTING[1]")) {
                    slots.add("CRAFTING[1]");
                } else {
                    slots.remove("CRAFTING[1]");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            craftingPane.addButton(new Button(fillerPaneGItem));
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 2, StringUtils.containsValue(slots, "CRAFTING[2]"), false, "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CRAFTING[2]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CRAFTING[2]")) {
                    slots.add("CRAFTING[2]");
                } else {
                    slots.remove("CRAFTING[2]");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            craftingPane.addButton(new Button(fillerPaneGItem), 10);
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, StringUtils.containsValue(slots, "CRAFTING[0]"), false, "&9&lSlot: &7&lCRAFTING&a&l[0]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CRAFTING[0]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CRAFTING[0]")) {
                    slots.add("CRAFTING[0]");
                } else {
                    slots.remove("CRAFTING[0]");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            craftingPane.addButton(new Button(fillerPaneGItem), 4);
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 3, StringUtils.containsValue(slots, "CRAFTING[3]"), false, "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CRAFTING[3]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CRAFTING[3]")) {
                    slots.add("CRAFTING[3]");
                } else {
                    slots.remove("CRAFTING[3]");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            craftingPane.addButton(new Button(fillerPaneGItem));
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 4, StringUtils.containsValue(slots, "CRAFTING[4]"), false, "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CRAFTING[4]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CRAFTING[4]")) {
                    slots.add("CRAFTING[4]");
                } else {
                    slots.remove("CRAFTING[4]");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            craftingPane.addButton(new Button(fillerPaneGItem), 3);
            craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot blacklist menu"), event -> blacklistSlotPane(player)));
            craftingPane.addButton(new Button(fillerPaneBItem), 7);
            craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot blacklist menu"), event -> blacklistSlotPane(player)));
            slotPane.addButton(new Button(fillerPaneGItem));
            slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, false, false, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots"), event -> craftingPane.open(player)));
            slotPane.addButton(new Button(fillerPaneGItem));
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, StringUtils.containsValue(slots, "HELMET"), false, "&9&lSlot: &a&lHELMET", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "HELMET") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "HELMET")) {
                    slots.add("HELMET");
                } else {
                    slots.remove("HELMET");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, StringUtils.containsValue(slots, "CHESTPLATE"), false, "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "CHESTPLATE") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "CHESTPLATE")) {
                    slots.add("CHESTPLATE");
                } else {
                    slots.remove("CHESTPLATE");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, StringUtils.containsValue(slots, "LEGGINGS"), false, "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "LEGGINGS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "LEGGINGS")) {
                    slots.add("LEGGINGS");
                } else {
                    slots.remove("LEGGINGS");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, StringUtils.containsValue(slots, "BOOTS"), false, "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to prevent this slot", "&7from having its items cleared.",
                    (StringUtils.containsValue(slots, "BOOTS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (!StringUtils.containsValue(slots, "BOOTS")) {
                    slots.add("BOOTS");
                } else {
                    slots.remove("BOOTS");
                }
                for (String slot : slots) {
                    saveList.add("{slot:" + slot + "}");
                }
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
            }));
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, StringUtils.containsValue(slots, "OFFHAND"), false, "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to prevent this slot", "&7from having its item cleared.",
                        (StringUtils.containsValue(slots, "OFFHAND") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (!StringUtils.containsValue(slots, "OFFHAND")) {
                        slots.add("OFFHAND");
                    } else {
                        slots.remove("OFFHAND");
                    }
                    for (String slot : slots) {
                        saveList.add("{slot:" + slot + "}");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
                }));
            } else {
                slotPane.addButton(new Button(fillerPaneGItem));
            }
            slotPane.addButton(new Button(fillerPaneGItem));
            for (int i = 9; i < 36; i++) {
                final int slot = i;
                slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:3"), i, (StringUtils.containsValue(slots, String.valueOf(slot))), false, "&9&lSlot: &a&l" + i, "&7", "&7*Click to prevent this slot",
                        "&7from having its item cleared.", (StringUtils.containsValue(slots, String.valueOf(slot)) ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (!StringUtils.containsValue(slots, String.valueOf(slot))) {
                        slots.add(String.valueOf(slot));
                    } else {
                        slots.remove(String.valueOf(slot));
                    }
                    for (String slotS : slots) {
                        saveList.add("{slot:" + slotS + "}");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
                }));
            }
            for (int j = 0; j < 9; j++) {
                final int slot = j;
                int count = j;
                if (slot == 0) {
                    count = 1;
                }
                slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), count, (StringUtils.containsValue(slots, String.valueOf(slot))), false, "&9&lSlot: &a&l" + j, "&7", "&7*Click to prevent this slot",
                        "&7from having its item cleared.", (StringUtils.containsValue(slots, String.valueOf(slot)) ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (!StringUtils.containsValue(slots, String.valueOf(slot))) {
                        slots.add(String.valueOf(slot));
                    } else {
                        slots.remove(String.valueOf(slot));
                    }
                    for (String slotS : slots) {
                        saveList.add("{slot:" + slotS + "}");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> blacklistSlotPane(player));
                }));
            }
            slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
            slotPane.addButton(new Button(fillerPaneBItem), 7);
            slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
        });
        slotPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player - The Player to have the Pane opened.
     */
    private static void blacklistNamePane(final Player player) {
        Interface namePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            namePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blacklist menu."), event -> blacklistPane(player)));
            List<String> names = new ArrayList<>();
            List<String> saveList = new ArrayList<>();
            try {
                final String blacklist = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Blacklist");
                if (blacklist != null) {
                    for (String value : blacklist.split(",")) {
                        String valType = (StringUtils.containsIgnoreCase(value, "{id") ? "id" : (StringUtils.containsIgnoreCase(value, "{slot") ? "slot" : (StringUtils.containsIgnoreCase(value, "{name") ? "name" : "")));
                        String inputResult = org.apache.commons.lang.StringUtils.substringBetween(value, "{" + valType + ":", "}");
                        if (valType.equalsIgnoreCase("name")) {
                            names.add(inputResult.trim().toUpperCase());
                        } else if (!value.isEmpty()) {
                            saveList.add(value.trim());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            namePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&b&lAdd Name", "&7", "&7*Add an items display", "&7name to be blacklisted", "&7simply click and type it.", "&7",
                    "&c&l&nNOTE:&7 Do NOT include any", "&7color codes as these are excluded."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ITEM NAME").with(Holder.INPUT_EXAMPLE, "ULTRA ITEM");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (!StringUtils.containsValue(names, ChatColor.stripColor(event.getMessage()))) {
                    names.add(ChatColor.stripColor(event.getMessage()));
                } else {
                    names.remove(ChatColor.stripColor(event.getMessage()));
                }
                for (String name : names) {
                    saveList.add("{name:" + name + "}");
                }
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ITEM NAME");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> blacklistNamePane(player));
            }));
            for (String itemName : names) {
                namePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&f" + itemName, "&7", "&7*Click to remove this item name", "&7from the clearing blacklist.", "&7",
                        "&c&l&nNOTE:&7 Do NOT include any", "&7color codes as these are excluded."), event -> {
                    names.remove(itemName);
                    for (String name : names) {
                        saveList.add("{name:" + name + "}");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Clear-Items.Blacklist", StringUtils.replaceLast(saveList.toString().replaceFirst("\\[", ""), "]", ""));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
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
        Interface optionPane = new Interface(false, 2, exitButton, GUIName, player);
        List<String> optionList = new ArrayList<>();
        final String options = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options");
        if (options != null) {
            for (String option : options.replace(" ", "").split(",")) {
                if (option != null && !option.isEmpty()) {
                    optionList.add(option);
                }
            }
        }
        SchedulerUtils.runAsync(() -> {
            optionPane.addButton(new Button(ItemHandler.getItem("DIAMOND_CHESTPLATE", 1, StringUtils.containsValue(optionList, "PROTECT"), false,
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> optionPane(player));
                    }));
            optionPane.addButton(new Button(fillerPaneBItem));
            optionPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, StringUtils.containsValue(optionList, "PROTECT_OP"), false,
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> optionPane(player));
                    }));
            optionPane.addButton(new Button(fillerPaneBItem));
            optionPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_APPLE" : "322"), 1, StringUtils.containsValue(optionList, "PROTECT_CREATIVE"), false,
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> optionPane(player));
                    }));
            optionPane.addButton(new Button(fillerPaneBItem));
            optionPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, (StringUtils.containsValue(optionList, "RETURN") && ItemJoin.getCore().getDependencies().getGuard().guardEnabled()), false,
                    "&e&lReturn Regions", "&7*Returns the cleared player inventory", "&7when exiting a cleared region", "&7or entering a region which is", "&7not listed as clearable.", (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ?
                            "&9&lENABLED: &a" + (StringUtils.containsValue(optionList, "RETURN") ? "true" : "false") : ""), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7"),
                    (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (ItemJoin.getCore().getDependencies().getGuard().guardEnabled() ? "" : "&7This button will do nothing...")),
                    event -> {
                        if (ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
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
                            File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                            FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                            dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", ""));
                            ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                            PluginData.getInfo().softReload();
                            SchedulerUtils.runLater(2L, () -> optionPane(player));
                        }
                    }));
            optionPane.addButton(new Button(fillerPaneBItem));
            optionPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, StringUtils.containsValue(optionList, "RETURN_SWITCH"), false,
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
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Clear-Items.Options", optionList.toString().replace("[", "").replace("]", ""));
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> optionPane(player));
                    }));
            optionPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
            optionPane.addButton(new Button(fillerPaneBItem), 7);
            optionPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the clear settings."), event -> clearPane(player)));
        });
        optionPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     *
     * @param player  - The Player to have the Pane opened.
     * @param section - The world section type.
     */
    private static void worldPane(final Player player, final String section) {
        Interface preventPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            preventPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the prevent menu."), event -> {
                if (section.contains("Prevent")) {
                    preventPane(player);
                } else {
                    clearPane(player);
                }
            }));
            List<String> enabledWorlds = new ArrayList<>();
            final String configSection = ItemJoin.getCore().getConfig("config.yml").getString(section);
            String[] enabledParts = (configSection != null ? configSection.replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",") : new String[1]);
            for (String enabledWorld : enabledParts) {
                if (enabledWorld != null && (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL"))) {
                    enabledWorlds.add("ALL");
                } else if (enabledWorld != null) {
                    for (World world : Bukkit.getServer().getWorlds()) {
                        if (enabledWorld.equalsIgnoreCase(world.getName())) {
                            enabledWorlds.add(world.getName());
                        }
                    }
                }
            }
            if (enabledWorlds.isEmpty() && ItemJoin.getCore().getConfig("config.yml").getBoolean(section)) {
                enabledWorlds.add("ALL");
            }
            preventPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, StringUtils.containsValue(enabledWorlds, "ALL"), false, "&a&l&nGLOBAL", "&7", "&7*Click to enable &lALL WORLDS.",
                    "&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, "ALL") + "").toUpperCase()), event -> {
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                if (StringUtils.containsValue(enabledWorlds, "ALL")) {
                    dataFile.set(section, false);
                } else {
                    dataFile.set(section, true);
                }
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> worldPane(player, section));
            }));
            for (World world : Bukkit.getServer().getWorlds()) {
                String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
                if (world.getEnvironment().equals(Environment.NETHER)) {
                    worldMaterial = "NETHERRACK";
                } else if (world.getEnvironment().equals(Environment.THE_END)) {
                    worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
                }
                preventPane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, StringUtils.containsValue(enabledWorlds, world.getName()), false, "&f&l" + world.getName(), "&7",
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
                    StringBuilder worldList = new StringBuilder();
                    for (String worldName : enabledWorlds) {
                        worldList.append(worldName).append(", ");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    if (enabledWorlds.isEmpty()) {
                        dataFile.set(section, false);
                    } else {
                        dataFile.set(section, worldList.substring(0, worldList.length() - 2));
                    }
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
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
        Interface overwritePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            overwritePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the configuration menu."), event -> itemSettings(player)));
            List<String> enabledWorlds = new ArrayList<>();
            final String overwrite = ItemJoin.getCore().getConfig("items.yml").getString("items-Overwrite");
            if (overwrite != null) {
                String[] enabledParts = overwrite.replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
                for (String enabledWorld : enabledParts) {
                    if (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL")) {
                        enabledWorlds.add("ALL");
                    } else {
                        for (World world : Bukkit.getServer().getWorlds()) {
                            if (enabledWorld.equalsIgnoreCase(world.getName())) {
                                enabledWorlds.add(world.getName());
                            }
                        }
                    }
                }
            }
            if (enabledWorlds.isEmpty() && ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Overwrite")) {
                enabledWorlds.add("ALL");
            }
            overwritePane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, StringUtils.containsValue(enabledWorlds, "ALL"), false, "&a&l&nGLOBAL", "&7", "&7*Click to enable item", "&7overwriting in &lALL WORLDS.",
                    "&9&lENABLED: &a" + (StringUtils.containsValue(enabledWorlds, "ALL") + "").toUpperCase()), event -> {
                File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
                FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                if (StringUtils.containsValue(enabledWorlds, "ALL")) {
                    dataFile.set("items-Overwrite", false);
                } else {
                    dataFile.set("items-Overwrite", true);
                }
                ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "items.yml");
                PluginData.getInfo().softReload();
                SchedulerUtils.runLater(2L, () -> overwritePane(player));
            }));
            for (World world : Bukkit.getServer().getWorlds()) {
                String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
                if (world.getEnvironment().equals(Environment.NETHER)) {
                    worldMaterial = "NETHERRACK";
                } else if (world.getEnvironment().equals(Environment.THE_END)) {
                    worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
                }
                overwritePane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, StringUtils.containsValue(enabledWorlds, world.getName()), false, "&f&l" + world.getName(), "&7",
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
                    StringBuilder worldList = new StringBuilder();
                    for (String worldName : enabledWorlds) {
                        worldList.append(worldName).append(", ");
                    }
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    if (enabledWorlds.isEmpty()) {
                        dataFile.set("items-Overwrite", false);
                    } else {
                        dataFile.set("items-Overwrite", worldList.substring(0, worldList.length() - 2));
                    }
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "items.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> overwritePane(player));
                }));
            }
        });
        overwritePane.open(player);
    }

    /**
     * Checks if the ItemStack is a safe Material,
     * that it actually exists and is not AIR or NULL.
     *
     * @param item           - The ItemStack to be checked.
     * @param inventoryCheck - The Inventory used for checking the ItemStack.
     */
    private static boolean safeMaterial(final ItemStack item, final Inventory inventoryCheck) {
        inventoryCheck.setItem(0, item);
        final ItemStack getItem = inventoryCheck.getItem(0);
        return getItem != null && !getItem.getType().name().equals("AIR");
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items Material.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The stage of the modification.
     */
    private static void materialPane(final Player player, final ItemMap itemMap, final int stage, final int position) {
        Interface materialPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 0 && stage != 2 && stage != 3) {
                materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            } else if (stage == 2) {
                materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the commands menu."), event -> commandPane(player, itemMap)));
            } else if (stage == 3) {
                materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> recipePane(player, itemMap)));
            }
            materialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, false, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 2 ? "ITEM COST" : "BUKKIT MATERIAL")).with(Holder.INPUT_EXAMPLE, "IRON_SWORD");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ItemHandler.getMaterial(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player), null) != Material.AIR) {
                    if (stage == 2) {
                        itemMap.setItemCost(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player).toUpperCase());
                    } else if (stage != 3) {
                        itemMap.setMaterial(ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null));
                        if (!ServerUtils.hasSpecificUpdate("1_13") && ChatColor.stripColor(event.getMessage()).contains(":")) {
                            String[] dataValue = ChatColor.stripColor(event.getMessage()).split(":");
                            if (StringUtils.isInt(StringUtils.translateLayout(dataValue[1], player))) {
                                itemMap.setDataValue((short) Integer.parseInt(dataValue[1]));
                            }
                        }
                    }
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 2 ? "ITEM COST" : "BUKKIT MATERIAL"));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    if (stage == 3) {
                        setIngredients(event.getPlayer(), itemMap, Objects.requireNonNull(ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null)).name(), position);
                    } else if (stage == 2) {
                        commandPane(event.getPlayer(), itemMap);
                    } else {
                        creatingPane(event.getPlayer(), itemMap);
                    }
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noMaterial", player, placeHolders);
                    materialPane(player, itemMap, stage, position);
                }
            }));
            Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equals("AIR") && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
                        for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
                            if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
                                final int dataValue = i;
                                materialPane.addButton(new Button(ItemHandler.getItem(material + ":" + dataValue, 1, false, false, "", "&7", "&7*Click to set the material."), event -> {
                                    if (stage == 2) {
                                        itemMap.setItemCost(material.toString());
                                    } else if (stage != 3) {
                                        itemMap.setMaterial(material);
                                        if (dataValue != 0) {
                                            itemMap.setDataValue((short) dataValue);
                                        }
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
                        materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, false, "", "&7", "&7*Click to set the material."), event -> {
                            if (stage == 2) {
                                itemMap.setItemCost(material.toString());
                            } else if (stage != 3) {
                                itemMap.setMaterial(material);
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
            }
            inventoryCheck.clear();
        });
        materialPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting single or multiple slots for an item.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The stage in the modification.
     */
    private static void switchPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface switchPane = new Interface(false, 1, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage == 0) {
                switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> materialPane(player, itemMap, 0, 0)));
            } else {
                switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            }
            switchPane.addButton(new Button(fillerPaneBItem), 2);
            switchPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, false, "&a&lSingle Slot", "&7", "&7*Define a single dedicated", "&7 slot for the item."), event -> slotPane(player, itemMap, stage, 0)));
            switchPane.addButton(new Button(fillerPaneBItem), 1);
            switchPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "DISPENSER" : "23"), 1, false, false, "&b&lMultiple Slots", "&7", "&7*Define multiple slots for the item."), event -> slotPane(player, itemMap, stage, 1)));
            switchPane.addButton(new Button(fillerPaneBItem), 2);
            if (stage == 0) {
                switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> materialPane(player, itemMap, 0, 0)));
            } else {
                switchPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            }
        });
        switchPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the items slot.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The stage in the modification.
     * @param type    - The type of slot being defined, single or multiple.
     */
    private static void slotPane(final Player player, final ItemMap itemMap, final int stage, final int type) {
        Interface slotPane = new Interface(false, 6, exitButton, GUIName, player);
        Interface craftingPane = new Interface(false, 4, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            craftingPane.addButton(new Button(fillerPaneGItem), 3);
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]")), false, "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to set the custom item",
                    "&7to appear in the &lCRAFTING &7slot &a&l[1]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CRAFTING[1]");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 2, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]")), false, "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to set the custom item",
                    "&7to appear in the &lCRAFTING &7slot &a&l[2]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CRAFTING[2]");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]")), false, "&9&lSlot: &7&lCRAFTING&a&l[0]", "&7", "&7*Click to set the custom item",
                    "&7to appear in the &lCRAFTING &7slot &a&l[0]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CRAFTING[0]");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 3, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]")), false, "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to set the custom item",
                    "&7to appear in the &lCRAFTING &7slot &a&l[3]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CRAFTING[3]");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            craftingPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 4, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]")), false, "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to set the custom item",
                    "&7to appear in the &lCRAFTING &7slot &a&l[4]&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CRAFTING[4]");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
            if (type > 0) {
                craftingPane.addButton(new Button(fillerPaneBItem), 3);
                craftingPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> creatingPane(player, itemMap)));
                craftingPane.addButton(new Button(fillerPaneBItem), 3);
            } else {
                craftingPane.addButton(new Button(fillerPaneBItem), 7);
            }
            craftingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
            slotPane.addButton(new Button(fillerPaneGItem));
            slotPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY")), false, "&9&lSlot: &a&lARBITRARY", "&7", "&7*Click to set the custom item",
                    "&7to appear in slot &a&lArbitrary&7", "&7", "&7*Arbitrary is defined as giving the", "&7item in the first available slot.", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY") ? "&9&lENABLED: &aTRUE" : "")),
                    event -> {
                        if (type == 0) {
                            itemMap.setSlot("ARBITRARY");
                            itemMap.setMultipleSlots(new ArrayList<>());
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
            slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, false, false, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots"), event -> craftingPane.open(player)));
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "HELMET")), false, "&9&lSlot: &a&lHELMET", "&7", "&7*Click to set the custom item",
                    "&7to appear in slot &a&lHELMET&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "HELMET") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("HELMET");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE")), false, "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to set the custom item",
                    "&7to appear in slot &a&lCHESTPLATE&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("CHESTPLATE");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS")), false, "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to set the custom item",
                    "&7to appear in slot &a&lLEGGINGS&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("LEGGINGS");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
            slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "BOOTS")), false, "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to set the custom item",
                    "&7to appear in slot &a&lBOOTS&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "BOOTS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                if (type == 0) {
                    itemMap.setSlot("BOOTS");
                    itemMap.setMultipleSlots(new ArrayList<>());
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
                slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "OFFHAND")), false, "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to set the custom item",
                        "&7to appear in slot &a&lOFFHAND&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), "OFFHAND") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (type == 0) {
                        itemMap.setSlot("OFFHAND");
                        itemMap.setMultipleSlots(new ArrayList<>());
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
                slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:3"), i, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "")), false, "&9&lSlot: &a&l" + i, "&7", "&7*Click to set the custom item",
                        "&7to appear in slot &a&l" + i + "&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (type == 0) {
                        itemMap.setSlot(slot + "");
                        itemMap.setMultipleSlots(new ArrayList<>());
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
                slotPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), count, (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "")), false, "&9&lSlot: &a&l" + j, "&7", "&7*Click to set the custom item",
                        "&7to appear in slot &a&l" + j + "&7", (type > 0 && StringUtils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
                    if (type == 0) {
                        itemMap.setSlot(slot + "");
                        itemMap.setMultipleSlots(new ArrayList<>());
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
            slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> switchPane(player, itemMap, stage)));
            if (type > 0) {
                slotPane.addButton(new Button(fillerPaneBItem), 3);
                slotPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> creatingPane(player, itemMap)));
                slotPane.addButton(new Button(fillerPaneBItem), 3);
            } else {
                slotPane.addButton(new Button(fillerPaneBItem), 7);
            }
            slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> switchPane(player, itemMap, stage)));
        });
        if (type == 2) {
            craftingPane.open(player);
        } else {
            slotPane.open(player);
        }
    }

// =======================================================================================================================================================================================================================================

//  ============================================== //
//             Item Definition Menus      	       //
//  ============================================== //

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items stack size.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void countPane(final Player player, final ItemMap itemMap) {
        Interface countPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            countPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            countPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Count", "&7", "&7*Click to set a custom count", "&7value for the item.", "&7", "&c&lNote: &7You can use placeholders", "&7as long as they parse to a number."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ITEM COUNT").with(Holder.INPUT_EXAMPLE, "48");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                final String count = ChatColor.stripColor(event.getMessage());
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, count);
                if (StringUtils.isInt(count)) {
                    itemMap.setCount(count);
                    final PlaceHolder placeHolder = new PlaceHolder().with(Holder.INPUT, "ITEM COUNT");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolder);
                } else if (count.contains("%")) {
                    final String translateCount = StringUtils.translateLayout(count, player).replaceAll("[^\\d.]", "").replace("-", "").replace(".", "").replace(" ", "");
                    if (StringUtils.isInt(translateCount)) {
                        itemMap.setCount(count);
                        final PlaceHolder placeHolder = new PlaceHolder().with(Holder.INPUT, "ITEM COUNT");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolder);
                    } else {
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    }
                } else {
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                creatingPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                countPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lCount: &a&l" + k, "&7", "&7*Click to set the", "&7count of the item."), event -> {
                    itemMap.setCount(k + "");
                    creatingPane(player, itemMap);
                }));
            }
        });
        countPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items' data.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void dataPane(final Player player, final ItemMap itemMap) {
        Interface dataPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            dataPane.addButton(new Button(fillerPaneBItem));
            dataPane.addButton(new Button(ItemHandler.setDurability(ItemHandler.getItem("BOW", 1, false, false, "&b&lDamage", "&7", "&7*Set the damage of the item.", (itemMap.getMaterial().getMaxDurability() != 0 ? "&9&lDURABILITY: &a" +
                    StringUtils.nullCheck(itemMap.getDurability() + "&7") : "&c&lERROR: &7This item is NOT damageable.")), 50), event -> {
                if (!StringUtils.nullCheck(itemMap.getDurability() + "&7").equals("NONE")) {
                    itemMap.setDurability(null);
                    dataPane(player, itemMap);
                } else if (itemMap.getMaterial().getMaxDurability() != 0) {
                    damagePane(player, itemMap);
                }
            }));
            dataPane.addButton(new Button(fillerPaneBItem), 2);
            dataPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, false, "&a&lDamage Data", "&7", "&7*Set the custom data of the item.", "&7This is the damage value assigned", "&7to the custom resource texture.", (ServerUtils.hasSpecificUpdate("1_14") ? "&7" : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&c&l&nWARNING: &eThis setting is only for" : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&eMinecraft versions below 1.14." : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&eYou are using a newer version of Minecraft" : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&eso things may not function as expected." : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&7" : ""), (ServerUtils.hasSpecificUpdate("1_14") ? "&eIt is highly recommended to use &l&nModel Data." : ""), "&9&lDAMAGE DATA: &a" + StringUtils.nullCheck(itemMap.getData() + "&7")), event -> {
                if (!StringUtils.nullCheck(itemMap.getData() + "&7").equals("NONE")) {
                    itemMap.setData(null);
                    dataPane(player, itemMap);
                } else {
                    durabilityDataPane(player, itemMap);
                }
            }));
            dataPane.addButton(new Button(fillerPaneBItem), 2);
            dataPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&e&lModel Data", "&7", "&7*Set the custom model data of the item.", "&7This is the custom texture.",
                    !ServerUtils.hasSpecificUpdate("1_14") ? "&c&l[ERROR] &7This version of Minecraft does" : "", !ServerUtils.hasSpecificUpdate("1_14") ? "&7not support custom model data." : "",
                    !ServerUtils.hasSpecificUpdate("1_14") ? "&7This was implemented in 1.14+." : "", "&9&lMODEL DATA: &a" + StringUtils.nullCheck(itemMap.getModelData() + "&7")), event -> {
                if (!StringUtils.nullCheck(itemMap.getModelData() + "&7").equals("NONE") && ServerUtils.hasSpecificUpdate("1_14")) {
                    itemMap.setModelData(null);
                    dataPane(player, itemMap);
                } else if (ServerUtils.hasSpecificUpdate("1_14")) {
                    modelDataPane(player, itemMap);
                }
            }));
            dataPane.addButton(new Button(fillerPaneBItem));
            dataPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
                setTriggers(itemMap);
                creatingPane(player, itemMap);
            }));
            dataPane.addButton(new Button(fillerPaneBItem), 7);
            dataPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
                setTriggers(itemMap);
                creatingPane(player, itemMap);
            }));
        });
        dataPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items' durability.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durabilityDataPane(final Player player, final ItemMap itemMap) {
        Interface texturePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            texturePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> dataPane(player, itemMap)));
            texturePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&lCustom Texture", "&7", "&7*Click to set a custom texture", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DURABILITY DATA").with(Holder.INPUT_EXAMPLE, "1193");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setData(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DURABILITY DATA");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                dataPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 2000; i++) {
                final int k = i;
                final ItemStack item = ItemHandler.getItem(itemMap.getMaterial().name(), 1, false, false, "&9&lData: &a&l" + k, "&7", "&7*Click to set the", "&7durability data of the item.");
                final ItemMeta itemMeta = item.getItemMeta();
                if (ServerUtils.hasSpecificUpdate("1_13") && itemMeta != null) {
                    ((Damageable) itemMeta).setDamage(k);
                    item.setItemMeta(itemMeta);
                } else {
                    LegacyAPI.setDurability(item, (short)k);
                }
                texturePane.addButton(new Button(item, event -> {
                    itemMap.setData(k);
                    dataPane(player, itemMap);
                }));
            }
        });
        texturePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for an items model data.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modelDataPane(final Player player, final ItemMap itemMap) {
        Interface texturePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            texturePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> dataPane(player, itemMap)));
            texturePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&lCustom Model Data", "&7", "&7*Click to set the custom mode data", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MODEL DATA").with(Holder.INPUT_EXAMPLE, "1193");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setModelData(ChatColor.stripColor(event.getMessage()));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MODEL DATA");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                dataPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 2000; i++) {
                final int k = i;
                final ItemStack item = ItemHandler.getItem(itemMap.getMaterial().name(), 1, false, false, "&9&lModel Data: &a&l" + k, "&7", "&7*Click to set the", "&7custom model data for the item.");
                final ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setCustomModelData(k);
                    item.setItemMeta(itemMeta);
                }
                texturePane.addButton(new Button(item, event -> {
                    itemMap.setModelData(String.valueOf(k));
                    dataPane(player, itemMap);
                }));
            }
        });
        texturePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items damage.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void damagePane(final Player player, final ItemMap itemMap) {
        Interface damagePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            damagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> dataPane(player, itemMap)));
            damagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Damage", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DAMAGE").with(Holder.INPUT_EXAMPLE, "1893");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setDurability((short) Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DAMAGE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                dataPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= itemMap.getMaterial().getMaxDurability(); i++) {
                final int k = i;
                damagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:6"), 1, false, false, "&9&lDamage: &a&l" + k, "&7", "&7*Click to set the", "&7damage of the item."), event -> {
                    itemMap.setDurability((short) k);
                    dataPane(player, itemMap);
                }));
            }
        });
        damagePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items commands.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void commandPane(final Player player, final ItemMap itemMap) {
        Interface commandPane = new Interface(false, 4, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            commandPane.addButton(new Button(fillerPaneGItem), 4);
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&e&lCommands", "&7", "&7*Click to define the custom command lines", "&7for the item and click type.",
                    "&7", "&9&lCOMMANDS: &a" + (itemMap.getCommands().length > 0 ? "YES" : "NONE")), event -> actionPane(player, itemMap)));
            commandPane.addButton(new Button(fillerPaneGItem), 7);
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LAVA_BUCKET" : "327"), 1, false, false, "&a&lWarmup", "&7", "&7*The time it will take before", "&7the commands are executed.", "&7Player movement will cancel the", "&7pending commands execution.", "&7",
                    "&9&lCOMMANDS-WARMUP: &a" + StringUtils.nullCheck(itemMap.getWarmDelay() + "&7") + (!StringUtils.nullCheck(itemMap.getWarmDelay() + "&7").equals("NONE") ? "&a second(s)" : "")), event -> {
                if (!StringUtils.nullCheck(itemMap.getWarmDelay() + "&7").equals("NONE")) {
                    itemMap.setWarmDelay(0);
                    commandPane(player, itemMap);
                } else {
                    warmPane(player, itemMap);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, false, "&a&lCooldown", "&7", "&7*The time that the commands will", "&7be on cooldown for.", "&7", "&9&lCOMMANDS-COOLDOWN: &a" + StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7") +
                    (!StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7").equals("NONE") ? "&a second(s)" : "")), event -> {
                if (!StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7").equals("NONE")) {
                    itemMap.setCommandCooldown(0);
                    commandPane(player, itemMap);
                } else {
                    cooldownPane(player, itemMap);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false, "&a&lCooldown Message", "&7", "&7*Optional cooldown message", "&7to be displayed when", "&7the items commands are",
                    "&7on cooldown.", "&7", "&9&lCOOLDOWN-MESSAGE: &a" + StringUtils.nullCheck(itemMap.getCooldownMessage())), event -> {
                if (!StringUtils.nullCheck(itemMap.getCooldownMessage()).equals("NONE")) {
                    itemMap.setCooldownMessage(null);
                    commandPane(player, itemMap);
                } else {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COOLDOWN MESSAGE").with(Holder.INPUT_EXAMPLE, "&cThis item is on cooldown for &a%timeleft%&c seconds..");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }
            }, event -> {
                itemMap.setCooldownMessage(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COOLDOWN MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandPane(event.getPlayer(), itemMap);
            }));
            commandPane.addButton(new Button(fillerPaneGItem), 3);
            commandPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&a&lParticle", "&7", "&7*Custom particle(s) that will be", "&7displayed when the commands", "&7are successfully executed.", "&7", "&9&lCOMMANDS-PARTICLE: &a" +
                    StringUtils.nullCheck(itemMap.getCommandParticle())), event -> {
                if (!StringUtils.nullCheck(itemMap.getCommandParticle()).equals("NONE")) {
                    itemMap.setCommandParticle(null);
                    commandPane(player, itemMap);
                } else {
                    particlePane(player, itemMap, 3);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&a&lItem Cost", "&7", "&7*Material that will", "&7be charged upon successfully", "&7executing the commands.", "&7", "&9&lCOMMANDS-ITEM: &a" +
                    (StringUtils.nullCheck(itemMap.getItemCost()))), event -> {
                if (!StringUtils.nullCheck(itemMap.getItemCost()).equals("NONE")) {
                    itemMap.setItemCost(null);
                    commandPane(player, itemMap);
                } else {
                    materialPane(player, itemMap, 2, 0);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, false, "&a&lCost", "&7", "&7*Amount that the player will", "&7be charged upon successfully", "&7executing the commands.", "&7", "&9&lCOMMANDS-COST: &a" +
                    (StringUtils.nullCheck(itemMap.getRawCost() + "&7"))), event -> {
                if (!StringUtils.nullCheck(itemMap.getRawCost() + "&7").equals("NONE")) {
                    itemMap.setCommandCost("0");
                    commandPane(player, itemMap);
                } else {
                    costPane(player, itemMap);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, false, false, "&a&lReceive", "&7", "&7*The number of times the", "&7commands will execute when", "&7receiving the custom item.",
                    "&cNOTE: &7Only functions with", "&7the on-receive command action.", "&7", "&9&lCOMMANDS-RECEIVE: &a" + (StringUtils.nullCheck(itemMap.getCommandReceive() + "&7"))), event -> {
                if (!StringUtils.nullCheck(itemMap.getCommandReceive() + "&7").equals("NONE")) {
                    itemMap.setCommandReceive(0);
                    commandPane(player, itemMap);
                } else {
                    receivePane(player, itemMap);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("JUKEBOX", 1, false, false, "&a&lSound", "&7", "&7*The sound that will be", "&7played after a successful", "&7command execution.", "&7", "&9&lCOMMANDS-SOUND: &a" +
                            StringUtils.nullCheck(itemMap.getCommandSound() + ""),
                    "&9&lVOLUME: &a" + ((!StringUtils.nullCheck(itemMap.getCommandSound() + "").equals("NONE")) ? itemMap.getCommandVolume() : "0"),
                    "&9&lPITCH: &a" + ((!StringUtils.nullCheck(itemMap.getCommandSound() + "").equals("NONE")) ? itemMap.getCommandPitch() : "0")), event -> {
                if (!StringUtils.nullCheck(itemMap.getCommandSound() + "").equals("NONE")) {
                    itemMap.setCommandSound(null);
                    commandPane(player, itemMap);
                } else {
                    soundPane(player, itemMap, 3);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, false, "&a&lSequence", "&7", "&7*The order that the command lines", "&7will be executed in.", "&7", "&9&lCOMMANDS-SEQUENCE: &a" +
                    StringUtils.nullCheck(itemMap.getCommandSequence() + "")), event -> {
                if (!StringUtils.nullCheck(itemMap.getCommandSequence() + "").equals("NONE")) {
                    itemMap.setCommandSequence(null);
                    commandPane(player, itemMap);
                } else {
                    sequencePane(player, itemMap, 0);
                }
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REDSTONE_TORCH" : "76"), 1, false, false, "&b&lPermissions", "&7", "&7*Permissions(s) that must be met",
                    "&7in order to execute item commands.", "&7", "&9&lENABLED: &a" + String.valueOf((!StringUtils.nullCheck(itemMap.getCommandPermissions() + "").equals("NONE"))).toUpperCase()), event -> commandPermissionPane(player, itemMap)));
            commandPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, false, "&b&lConditions", "&7", "&7*Condition(s) that must be met",
                    "&7in order to execute item commands.", "&7", "&9&lENABLED: &a" + String.valueOf((!StringUtils.nullCheck(itemMap.getCommandConditions() + "").equals("NONE"))).toUpperCase()), event -> commandActionPane(player, itemMap)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLOWSTONE_DUST" : "348"), 1, false, false, "&b&lFail Messages", "&7", "&7*Define messages for each command action", "&7that will be sent when either", "&7permissions or conditions requirements", "&7are not met.", "&7", "&9&lENABLED: &a" +
                    String.valueOf((!StringUtils.nullCheck(itemMap.getCommandMessages() + "").equals("NONE"))).toUpperCase()), event -> commandFailPane(player, itemMap)));
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            commandPane.addButton(new Button(fillerPaneBItem), 7);
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        commandPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an action to a command.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void actionPane(final Player player, final ItemMap itemMap) {
        Interface clickPane = new Interface(false, 5, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            clickPane.addButton(new Button(fillerPaneGItem), 2);
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract", "&7", "&7*Commands that will execute only", "&7when left and right clicking.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_ALL)), event -> commandListPane(player, itemMap, Action.INTERACT_ALL)));
            clickPane.addButton(new Button(fillerPaneGItem));
            clickPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory", "&7", "&7*Commands that will execute only", "&7when cursor clicking the item", "&7with the players inventory open.",
                    "&7", "&9&lCommands: &a" + listCommands(itemMap, Action.INVENTORY_ALL)), event -> commandListPane(player, itemMap, Action.INVENTORY_ALL)));
            clickPane.addButton(new Button(fillerPaneGItem));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, false, "&e&lPhysical", "&7", "&7*Commands that will execute", "&7when held in the player hand", "&7and they interact with a object", "&7such as a pressure plate.", "&7",
                    "&9&lCommands: &a" + listCommands(itemMap, Action.PHYSICAL)), event -> commandListPane(player, itemMap, Action.PHYSICAL)));
            clickPane.addButton(new Button(fillerPaneGItem), 2);
            clickPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, true, "&e&lOn-Equip", "&7", "&7*Commands that will execute only", "&7when the item is placed", "&7in an armor slot.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_EQUIP)), event -> commandListPane(player, itemMap, Action.ON_EQUIP)));
            clickPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, true, "&e&lUn-Equip", "&7", "&7*Commands that will execute only", "&7when the item is removed", "&7from an armor slot.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.UN_EQUIP)), event -> commandListPane(player, itemMap, Action.UN_EQUIP)));
            clickPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, false, "&e&lOn-Hold", "&7", "&7*Commands that will execute only", "&7when holding the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_HOLD)), event -> commandListPane(player, itemMap, Action.ON_HOLD)));
            clickPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, false, "&e&lOn-Fire", "&7", "&7*Commands that will execute only", "&7when an arrow or bow is fired.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_FIRE)), event -> commandListPane(player, itemMap, Action.ON_FIRE)));
            clickPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&lOn-Drop", "&7", "&7*Commands that will execute only", "&7when you drop the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_DROP)), event -> commandListPane(player, itemMap, Action.ON_DROP)));
            clickPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, true, "&e&lOn-Consume", "&7", "&7*Commands that will execute only", "&7when you consume the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_CONSUME)), event -> commandListPane(player, itemMap, Action.ON_CONSUME)));
            clickPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&e&lOn-Receive", "&7", "&7*Commands that will execute only", "&7when you are given the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_RECEIVE)), event -> commandListPane(player, itemMap, Action.ON_RECEIVE)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SKELETON_SKULL" : "397"), 1, false, false, "&e&lOn-Death", "&7", "&7*Commands that will execute only", "&7when dying with the", "&7item in your inventory.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_DEATH)), event -> commandListPane(player, itemMap, Action.ON_DEATH)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PLAYER_HEAD" : "397:3"), 1, false, false, "&e&lOn-Kill", "&7", "&7*Commands that will execute only", "&7when killing a player with the", "&7item in your inventory.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_KILL)), event -> commandListPane(player, itemMap, Action.ON_KILL)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLASS" : "20"), 1, false, false, "&e&lInteract-Air", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking the air.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_AIR)), event -> commandListPane(player, itemMap, Action.INTERACT_AIR)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS" : "95:3"), 1, false, false, "&e&lInteract-Air-Left", "&7", "&7*Commands that will execute only", "&7when left clicking the air.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_LEFT_AIR)), event -> commandListPane(player, itemMap, Action.INTERACT_LEFT_AIR)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS" : "95:6"), 1, false, false, "&e&lInteract-Air-Right", "&7", "&7*Commands that will execute only", "&7when right clicking the air.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_RIGHT_AIR)), event -> commandListPane(player, itemMap, Action.INTERACT_RIGHT_AIR)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, false, false, "&e&lInteract-Block", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking a block.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_BLOCK)), event -> commandListPane(player, itemMap, Action.INTERACT_BLOCK)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE" : "1"), 1, false, false, "&e&lInteract-Block-Left", "&7", "&7*Commands that will execute only", "&7when left clicking a block.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_LEFT_BLOCK)), event -> commandListPane(player, itemMap, Action.INTERACT_LEFT_BLOCK)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE" : "4"), 1, false, false, "&e&lInteract-Block-Right", "&7", "&7*Commands that will execute only", "&7when right clicking a block.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_RIGHT_BLOCK)), event -> commandListPane(player, itemMap, Action.INTERACT_RIGHT_BLOCK)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_DOOR" : "330"), 1, false, false, "&e&lInteract-Left", "&7", "&7*Commands that will execute only", "&7when left clicking.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_LEFT_ALL)), event -> commandListPane(player, itemMap, Action.INTERACT_LEFT_ALL)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract-Right", "&7", "&7*Commands that will execute only", "&7when right clicking.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INTERACT_RIGHT_ALL)), event -> commandListPane(player, itemMap, Action.INTERACT_RIGHT_ALL)));
            clickPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Commands that will execute only", "&7when cursor swapping with another item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_SWAP_CURSOR)), event -> commandListPane(player, itemMap, Action.INVENTORY_SWAP_CURSOR)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, false, "&e&lInventory-Middle", "&7", "&7*Commands that will execute only", "&7when cursor middle clicking the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_MIDDLE)), event -> commandListPane(player, itemMap, Action.INVENTORY_MIDDLE)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&e&lInventory-Creative", "&7", "&7*Commands that will execute only", "&7when cursor clicking the item in creative mode.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_CREATIVE)), event -> commandListPane(player, itemMap, Action.INVENTORY_CREATIVE)));
            clickPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, false, "&e&lInventory-Left", "&7", "&7*Commands that will execute only", "&7when cursor left clicking the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_LEFT)), event -> commandListPane(player, itemMap, Action.INVENTORY_LEFT)));
            clickPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory-Right", "&7", "&7*Commands that will execute only", "&7when cursor right clicking the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_RIGHT)), event -> commandListPane(player, itemMap, Action.INVENTORY_RIGHT)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE_SLAB" : "44"), 2, false, false, "&e&lInventory-Shift-Left", "&7", "&7*Commands that will execute only", "&7when cursor shift left clicking the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_SHIFT_LEFT)), event -> commandListPane(player, itemMap, Action.INVENTORY_SHIFT_LEFT)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE_SLAB" : "44:3"), 2, false, false, "&e&lInventory-Shift-Right", "&7", "&7*Commands that will execute only", "&7when cursor shift right clicking the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.INVENTORY_SHIFT_RIGHT)), event -> commandListPane(player, itemMap, Action.INVENTORY_SHIFT_RIGHT)));
            clickPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false, "&e&lOn-Join", "&7", "&7*Commands that will execute only", "&7when the player joins the server", "&7and they already have the item.", "&7", "&7Note: This will not be triggered", "&7after the user receives the item", "&7they must already have the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_JOIN)), event -> commandListPane(player, itemMap, Action.ON_JOIN)));
            clickPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false, "&e&lOn-Damage", "&7", "&7*Commands that will execute only", "&7when the player damages an", "&7entity or is damaged by an", "&7entity with the item in", "&7their inventory.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_DAMAGE)), event -> commandListPane(player, itemMap, Action.ON_DAMAGE)));
            clickPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, true, "&e&lOn-Hit", "&7", "&7*Commands that will execute only", "&7when the player damages an", "&7entity while holding the item.", "&7", "&9&lCommands: &a" +
                    listCommands(itemMap, Action.ON_HIT)), event -> commandListPane(player, itemMap, Action.ON_HIT)));
            clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            clickPane.addButton(new Button(fillerPaneBItem), 7);
            clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
        });
        clickPane.open(player);
    }

    /**
     * Gets the command for the specified action.
     *
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action that the command should contain.
     * @return The raw String command.
     */
    private static String listCommands(final ItemMap itemMap, final Action action) {
        StringBuilder commands = new StringBuilder();
        String commandReturn = "NONE";
        for (ItemCommand command : itemMap.getCommands()) {
            if (command.matchAction(action)) {
                commands.append(command.getRawCommand()).append(" /n ");
            }
        }
        if (!StringUtils.nullCheck(commands.toString()).equals("NONE")) {
            commandReturn = commands.toString();
        }
        return commandReturn;
    }

    /**
     * Modifies an existing ItemCommand or adds the new ItemCommand to the ItemMap.
     *
     * @param itemMap     - The ItemMap currently being modified.
     * @param itemCommand - The itemCommand to be modified or added.
     * @param newCommand  - If this is a new command being added.
     */
    private static void modifyCommands(final ItemMap itemMap, final ItemCommand itemCommand, final boolean newCommand) {
        List<ItemCommand> arrayCommands = new ArrayList<>(Arrays.asList(itemMap.getCommands()));
        if (newCommand) {
            arrayCommands.add(itemCommand);
        } else {
            arrayCommands.remove(itemCommand);
        }
        final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
        for (int i = 0; i < arrayCommands.size(); ++i) {
            commands[i] = arrayCommands.get(i);
        }
        itemMap.setCommands(commands);
    }

//  ============================================== //
//               ItemCommand Menus        	       //
//  ============================================== //

    /**
     * Gets the number of commands for the action type.
     *
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action to be matched.
     */
    private static int getCommandSize(final ItemMap itemMap, final Action action) {
        int l = 0;
        for (ItemCommand command : itemMap.getCommands()) {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action to be matched.
     */
    private static void commandListPane(final Player player, final ItemMap itemMap, final Action action) {
        Interface commandListPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            commandListPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the click type menu."), event -> actionPane(player, itemMap)));
            commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&lNew Line", "&7", "&7*Add a new command to be executed", "&7by &9&l" + action.name()), event -> executorPane(player, itemMap, action)));
            ItemCommand[] commandList = itemMap.getCommands();
            int l = 1;
            for (ItemCommand command : commandList) {
                if (command.matchAction(action)) {
                    final int k = l;
                    commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&f" + command.getRawCommand(), "&7", "&7*Click to&l modify &7this command.", "&9&lOrder Number: &a" + k), event -> modifyCommandsPane(player, itemMap, action, command, k)));
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
     * @param player      - The Player to have the Pane opened.
     * @param itemMap     - The ItemMap currently being modified.
     * @param action      - The action to be matched.
     * @param command     - The ItemCommand instance being modified.
     * @param orderNumber - The current number that dictates the ItemCommands "place in line".
     */
    private static void orderPane(final Player player, final ItemMap itemMap, final Action action, final ItemCommand command, final int orderNumber) {
        Interface orderPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            orderPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command modify menu."), event -> modifyCommandsPane(player, itemMap, action, command, orderNumber)));
            for (int i = 1; i <= getCommandSize(itemMap, action); i++) {
                final int k = i;
                orderPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:3"), k, false, false, "&9&lOrder Number: &a&l" + k, "&7", "&7*Click to set the order", "&7number of the command."), event -> {
                    List<ItemCommand> arrayCommands = new ArrayList<>();
                    int l = 0;
                    for (ItemCommand Command : itemMap.getCommands()) {
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
     * @param player      - The Player to have the Pane opened.
     * @param itemMap     - The ItemMap currently being modified.
     * @param action      - The action to be matched.
     * @param command     - The ItemCommand instance being modified.
     * @param orderNumber - The current number that dictates the ItemCommands "place in line".
     */
    private static void modifyCommandsPane(final Player player, final ItemMap itemMap, final Action action, final ItemCommand command, final int orderNumber) {
        Interface modPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modPane.addButton(new Button(fillerPaneGItem), 4);
            modPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&f" + command.getRawCommand(), "&7", "&7*You are modifying this command.", "&9&lOrder Number: &a" + orderNumber)));
            modPane.addButton(new Button(fillerPaneGItem), 4);
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, false, "&fIdentifier", "&7", "&7*Set a custom identifier", "&7for this command line.", "&7", "&cNOTE: &7This is in order to set", "&7a random command list sequence.",
                    "&7Only use this if", "&7the commands sequence is", "&7set to &aRANDOM_LIST&7.", "&7", "&9&lIDENTIFIER: &a" + StringUtils.nullCheck(command.getSection())), event -> {
                if (!StringUtils.nullCheck(command.getSection()).equals("NONE")) {
                    ItemCommand[] commands = itemMap.getCommands();
                    for (ItemCommand Command : commands) {
                        if (Command.equals(command)) {
                            Command.setSection(null);
                        }
                    }
                    itemMap.setCommands(commands);
                    command.setSection(null);
                    modifyCommandsPane(player, itemMap, action, command, orderNumber);
                } else {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND IDENTIFIER").with(Holder.INPUT_EXAMPLE, "winner");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }
            }, event -> {
                ItemCommand[] commands = itemMap.getCommands();
                for (ItemCommand Command : commands) {
                    if (Command.equals(command)) {
                        Command.setSection(ChatColor.stripColor(event.getMessage()));
                    }
                }
                itemMap.setCommands(commands);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND IDENTIFIER");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&fModify", "&7", "&7*Sets the command to", "&7another text entry."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MODIFIED COMMAND").with(Holder.INPUT_EXAMPLE, "gamemode creative");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                ItemCommand[] commands = itemMap.getCommands();
                for (ItemCommand Command : commands) {
                    if (Command.equals(command)) {
                        Command.setCommand(ChatColor.stripColor(event.getMessage()));
                    }
                }
                itemMap.setCommands(commands);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MODIFIED COMMAND");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, false, "&fOrder", "&7", "&7*Changes the order of execution", "&7for this command line.", "&7", "&7This will simply set the order", "&7number and push the",
                    "&7other commands down by one."), event -> orderPane(player, itemMap, action, command, orderNumber)));
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&fDelete", "&7", "&7*Click to&c delete &7this command."), event -> {
                modifyCommands(itemMap, command, false);
                commandListPane(player, itemMap, action);
            }));
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
            modPane.addButton(new Button(fillerPaneBItem), 7);
            modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
        });
        modPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command executor.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - THe action to be matched,
     */
    private static void executorPane(final Player player, final ItemMap itemMap, final Action action) {
        Interface executorPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, false, "&e&lPlayer", "&7", "&7*Executes the command", "&7as the player."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PLAYER COMMAND").with(Holder.INPUT_EXAMPLE, "spawn");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("player: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PLAYER COMMAND");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, true, false, "&e&lOp", "&7", "&7*Executes the command as if the", "&7player has /op (admin permissions)."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ADMIN COMMAND").with(Holder.INPUT_EXAMPLE, "broadcast I am &cADMIN!");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("op: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ADMIN COMMAND");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&e&lConsole", "&7", "&7*Executes the command", "&7in the console window."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "CONSOLE COMMAND").with(Holder.INPUT_EXAMPLE, "gamemode creative %player%");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("console: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "CONSOLE COMMAND");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&lServer", "&7", "&7*Switches the player to", "&7the defined server name.", "&7", "&7&lNote: &7This is the name",
                    "&7defined in the BungeeCord config."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SERVER SWITCH").with(Holder.INPUT_EXAMPLE, "survival");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("server: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SERVER SWITCH");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, false, false, "&e&lBungee", "&7", "&7*Executes a BungeeCord specific command."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUNGEE COMMAND").with(Holder.INPUT_EXAMPLE, "survival");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("bungee: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUNGEE COMMAND");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&e&lMessage", "&7", "&7*Sends the player a custom message."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MESSAGE").with(Holder.INPUT_EXAMPLE, "&eWelcome to the Server!");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                modifyCommands(itemMap, ItemCommand.fromString("message: " + ChatColor.stripColor(event.getMessage()), action, itemMap, 0L, null), true);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            executorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ANVIL" : "145"), 1, false, false, "&e&lDamage", "&7", "&7*Damages the item (x) amount."), event -> damageExecutorPane(player, itemMap, action)));
            executorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "REPEATER" : "356"), 1, false, false, "&e&lSwap-Item", "&7", "&7*Swaps the item to another defined item."), event -> swapPane(player, itemMap, action)));
            executorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&e&lDelay", "&7", "&7*Adds a delay between command lines."), event -> delayPane(player, itemMap, action)));
            executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
            executorPane.addButton(new Button(fillerPaneBItem), 7);
            executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> commandListPane(player, itemMap, action)));
        });
        executorPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the commands swap-item.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action to be matched.
     */
    private static void swapPane(final Player player, final ItemMap itemMap, final Action action) {
        Interface swapPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            swapPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> executorPane(player, itemMap, action)));
            for (ItemMap item : ItemUtilities.getUtilities().copyItems()) {
                if (item.getNodeLocation() != itemMap.getNodeLocation()) {
                    if (itemMap.isAnimated() || itemMap.isDynamic()) {
                        setModifyMenu(true, player);
                        if (itemMap.getAnimationHandler().get(player) != null) {
                            itemMap.getAnimationHandler().get(player).setMenu(true, 1);
                        }
                    }
                    swapPane.addButton(new Button(ItemHandler.addLore(item.getTempItem(), "&7", "&6---------------------------", "&7*Click to set as a swap-item.", "&9&lNode: &a" + item.getConfigName(), "&7"), event -> {
                        modifyCommands(itemMap, ItemCommand.fromString("swap-item: " + item.getConfigName(), action, itemMap, 0L, null), true);
                        commandListPane(player, itemMap, action);
                    }));
                }
            }
        });
        swapPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command delay duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action to be matched.
     */
    private static void delayPane(final Player player, final ItemMap itemMap, final Action action) {
        Interface delayPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            delayPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> executorPane(player, itemMap, action)));
            delayPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom", "&7delay for the next command."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DELAY").with(Holder.INPUT_EXAMPLE, "180");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    modifyCommands(itemMap, ItemCommand.fromString("delay: " + Integer.parseInt(ChatColor.stripColor(event.getMessage())), action, itemMap, Integer.parseInt(ChatColor.stripColor(event.getMessage())), null), true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DELAY");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                delayPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:8"), k, false, false, "&9&lDelay: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the", "&7delay of the next command."), event -> {
                    modifyCommands(itemMap, ItemCommand.fromString("delay: " + k, action, itemMap, k, null), true);
                    commandListPane(player, itemMap, action);
                }));
            }
        });
        delayPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command damage amount.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param action  - The action to be matched.
     */
    private static void damageExecutorPane(final Player player, final ItemMap itemMap, final Action action) {
        Interface damagePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            damagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> executorPane(player, itemMap, action)));
            damagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom", "&7delay for the next command."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DAMAGE").with(Holder.INPUT_EXAMPLE, "180");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    modifyCommands(itemMap, ItemCommand.fromString("damage: " + Integer.parseInt(ChatColor.stripColor(event.getMessage())), action, itemMap, Integer.parseInt(ChatColor.stripColor(event.getMessage())), null), true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DAMAGE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandListPane(event.getPlayer(), itemMap, action);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                damagePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:8"), k, false, false, "&9&lDamage: &a&l" + k, "&7", "&7*Click to set the", "&7damage the item should take."), event -> {
                    modifyCommands(itemMap, ItemCommand.fromString("damage: " + k, action, itemMap, k, null), true);
                    commandListPane(player, itemMap, action);
                }));
            }
        });
        damagePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command cooldown duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void cooldownPane(final Player player, final ItemMap itemMap) {
        Interface cooldownPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            cooldownPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            cooldownPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom commands-cooldown", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND COOLDOWN").with(Holder.INPUT_EXAMPLE, "180");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setCommandCooldown(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND COOLDOWN");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                cooldownPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-cooldown of the item."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void warmPane(final Player player, final ItemMap itemMap) {
        Interface warmPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            warmPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            warmPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Warmup", "&7", "&7*Click to set a custom commands-warmup", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND WARMUP").with(Holder.INPUT_EXAMPLE, "12");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setWarmDelay(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND WARMUP");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                warmPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lWarming: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-warmup of the item."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void costPane(final Player player, final ItemMap itemMap) {
        Interface costPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            costPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            costPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Cost", "&7", "&7*Click to set a custom commands-cost", "&7value for the item.", "&7", "&c&lNote: &7You can use placeholders", "&7as long as they parse to a number."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND COST").with(Holder.INPUT_EXAMPLE, "340");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                final String cost = ChatColor.stripColor(event.getMessage());
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, cost);
                if (StringUtils.isInt(cost) || StringUtils.isDouble(cost)) {
                    itemMap.setCommandCost(cost);
                    final PlaceHolder placeHolder = new PlaceHolder().with(Holder.INPUT, "COMMAND COST");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolder);
                } else if (cost.contains("%")) {
                    final String translateCost = StringUtils.translateLayout(cost, player).replaceAll("[^\\d.]", "").replace("-", "").replace(".", "").replace(" ", "");
                    if (StringUtils.isInt(translateCost) || StringUtils.isDouble(translateCost)) {
                        itemMap.setCommandCost(cost);
                        final PlaceHolder placeHolder = new PlaceHolder().with(Holder.INPUT, "COMMAND COST");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolder);
                    } else {
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    }
                } else {
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandPane(event.getPlayer(), itemMap);
            }));
            for (int i = 0; i <= 384; i++) {
                final int k = i;
                costPane.addButton(new Button(ItemHandler.getItem(k == 0 ? (ServerUtils.hasSpecificUpdate("1_13") ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5") : (ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), (k == 0 ? 1 : k), false, false, "&9&lCost: &a$&l" + k, "&7", "&7*Click to set the", "&7commands-cost of the item."), event -> {
                    itemMap.setCommandCost(String.valueOf(k));
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void receivePane(final Player player, final ItemMap itemMap) {
        Interface receivePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            receivePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            receivePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Receive", "&7", "&7*Click to set a custom commands-receive", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND RECEIVE").with(Holder.INPUT_EXAMPLE, "10");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setCommandReceive(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "COMMAND RECEIVE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                commandPane(event.getPlayer(), itemMap);
            }));
            for (int i = 0; i <= 64; i++) {
                final int k = i;
                receivePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), (k == 0 ? 1 : k), false, false, "&9&lReceive: &a#&l" + k, "&7", "&7*Click to set the", "&7commands-receive of the item."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void sequencePane(final Player player, final ItemMap itemMap, final int stage) {
        Interface sequencePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage == 4) {
                sequencePane.addButton(new Button(fillerPaneGItem));
            }
            sequencePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&a&lSequential", "&7", "&7*Executes the command lines", "&7in order from top to bottom."), event -> {
                if (stage == 4) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands-sequence", "SEQUENTIAL");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> activeCommands(player));
                } else {
                    itemMap.setCommandSequence(CommandSequence.SEQUENTIAL);
                    commandPane(player, itemMap);
                }
            }));
            sequencePane.addButton(new Button(fillerPaneGItem));
            sequencePane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, false, "&a&lRandom Single", "&7", "&7*Executes one of the command lines", "&7randomly with equal values."), event -> {
                if (stage == 4) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands-sequence", "RANDOM_SINGLE");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> activeCommands(player));
                } else {
                    itemMap.setCommandSequence(CommandSequence.RANDOM_SINGLE);
                    commandPane(player, itemMap);
                }
            }));
            sequencePane.addButton(new Button(fillerPaneGItem));
            sequencePane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&a&lRandom List", "&7", "&7*Randomly selects from a list", "&7of commands to execute."), event -> {
                if (stage == 4) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands-sequence", "RANDOM_LIST");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> activeCommands(player));
                } else {
                    itemMap.setCommandSequence(CommandSequence.RANDOM_LIST);
                    commandPane(player, itemMap);
                }
            }));
            sequencePane.addButton(new Button(fillerPaneGItem));
            sequencePane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&a&lRandom", "&7", "&7*Executes each command line in a", "&7random order with equal values."), event -> {
                if (stage == 4) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands-sequence", "RANDOM");
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> activeCommands(player));
                } else {
                    itemMap.setCommandSequence(CommandSequence.RANDOM);
                    commandPane(player, itemMap);
                }
            }));
            sequencePane.addButton(new Button(fillerPaneGItem));
            if (stage != 4) {
                sequencePane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&a&lRemain", "&7", "&7*Executes each command only if", "&7the item exists in the player", "&7inventory at the time of executing", "&7a delayed command line.", "&7The commands will be sent in", "&7order from top to bottom."), event -> {
                    itemMap.setCommandSequence(CommandSequence.REMAIN);
                    commandPane(player, itemMap);
                }));
            }
            if (stage == 4) {
                sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }
            sequencePane.addButton(new Button(fillerPaneBItem), 7);
            if (stage == 4) {
                sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }
        });
        sequencePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command sound.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void soundPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface soundPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 3) {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> teleportPane(player, itemMap, stage)));
            } else {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }

            for (Sound sound : CompatUtils.values(Sound.class)) {
                if (stage != 3) {
                    soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, false, "&f" + CompatUtils.getName(sound), "&7", "&7*Click to set the", "&7teleport-sound of the item."), event -> {
                        itemMap.setTeleportSound(CompatUtils.getName(sound));
                        soundVolumePane(player, itemMap, stage);
                    }));
                } else {
                    soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, false, "&f" + CompatUtils.getName(sound), "&7", "&7*Click to set the", "&7commands-sound of the item."), event -> {
                        itemMap.setCommandSound(sound);
                        soundVolumePane(player, itemMap, stage);
                    }));
                }
            }
        });
        soundPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an item commands sound volume.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void soundVolumePane(final Player player, final ItemMap itemMap, final int stage) {
        Interface soundPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 3) {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> teleportPane(player, itemMap, stage)));
            } else {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }
            soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Volume", "&7", "&7*Click to set a custom sound volume value."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SOUND VOLUME").with(Holder.INPUT_EXAMPLE, "1.4");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isDouble(ChatColor.stripColor(event.getMessage()))) {
                    if (stage != 3) {
                        itemMap.setTeleportVolume(Double.parseDouble(ChatColor.stripColor(event.getMessage())));
                    } else {
                        itemMap.setCommandVolume(Double.parseDouble(ChatColor.stripColor(event.getMessage())));
                    }
                    soundPitchPane(player, itemMap, stage);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SOUND VOLUME");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    soundVolumePane(event.getPlayer(), itemMap, stage);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GREEN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:13"), k, false, false, "&9&lVolume: &a&l" + k, "&7", "&7*Click to set the sound volume."), event -> {
                    if (stage != 3) {
                        itemMap.setTeleportVolume((double) k);
                    } else {
                        itemMap.setCommandVolume((double) k);
                    }
                    soundPitchPane(player, itemMap, stage);
                }));
            }
        });
        soundPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an item commands sound pitch.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void soundPitchPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface soundPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 3) {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> teleportPane(player, itemMap, stage)));
            } else {
                soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }
            soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Pitch", "&7", "&7*Click to set a custom sound pitch", "&7value for the command sound."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SOUND PITCH").with(Holder.INPUT_EXAMPLE, "0.8");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isDouble(ChatColor.stripColor(event.getMessage()))) {
                    if (stage != 3) {
                        itemMap.setTeleportPitch(Double.parseDouble(ChatColor.stripColor(event.getMessage())));
                        teleportPane(player, itemMap, stage);
                    } else {
                        itemMap.setCommandPitch(Double.parseDouble(ChatColor.stripColor(event.getMessage())));
                        commandPane(player, itemMap);
                    }
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SOUND PITCH");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    soundPitchPane(event.getPlayer(), itemMap, stage);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                soundPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lPitch: &a&l" + k, "&7", "&7*Click to set the sound pitch", "&7for the command sound."), event -> {
                    if (stage != 3) {
                        itemMap.setTeleportPitch((double) k);
                        teleportPane(player, itemMap, stage);
                    } else {
                        itemMap.setCommandPitch((double) k);
                        commandPane(player, itemMap);
                    }
                }));
            }
        });
        soundPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting an items command particle.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void particlePane(final Player player, final ItemMap itemMap, final int stage) {
        Interface particlePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 3) {
                particlePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the teleport menu."), event -> teleportPane(player, itemMap, stage)));
            } else {
                particlePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            }
            if (stage == 3) {
                particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&fFIREWORK_FAKE", "&7", "&7*Click to set the lifetime", "&7commands-particle of the item."), event -> lifePane(player, itemMap, "FIREWORK", 1)));
            }
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                for (Particle particle : Particle.values()) {
                    if (stage != 3) {
                        particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&f" + particle.name(), "&7", "&7*Click to set the", "&7teleport-effect of the item."), event -> {
                            itemMap.setTeleportEffect(particle.name());
                            teleportPane(player, itemMap, stage);
                        }));
                    } else {
                        particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&f" + particle.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> lifePane(player, itemMap, particle.name(), 0)));
                    }
                }
            } else {
                for (Effect effect : Effect.values()) {
                    if (stage != 3) {
                        particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&f" + effect.name(), "&7", "&7*Click to set the", "&7teleport-effect of the item."), event -> {
                            itemMap.setTeleportEffect(effect.name());
                            teleportPane(player, itemMap, stage);
                        }));
                    } else {
                        particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&f" + effect.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> lifePane(player, itemMap, effect.name(), 0)));
                    }
                }
            }
        });
        particlePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the lifetime of the commands firework particle.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void lifePane(final Player player, final ItemMap itemMap, final String particle, final int stage) {
        Interface lifePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            lifePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> particlePane(player, itemMap, 3)));
            lifePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom LifeTime", "&7", "&7*Click to set a lifetime (duration)", "&7value for particle effect."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PARTICLE LIFETIME").with(Holder.INPUT_EXAMPLE, "170");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    if (stage == 0) {
                        itemMap.setCommandParticle(particle + ":" + ChatColor.stripColor(event.getMessage()));
                    } else {
                        explosionPane(player, itemMap, particle, Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    }
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PARTICLE LIFETIME");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    particlePane(event.getPlayer(), itemMap, 3);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                lifePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CYAN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:9"), k, false, false, "&9&lLifeTime: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the lifetime", "&7(duration) of the particle effect."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void explosionPane(final Player player, final ItemMap itemMap, final String particle, final int lifetime) {
        Interface patternPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            patternPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> particlePane(player, itemMap, 3)));
            for (Type explosion : Type.values()) {
                patternPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&f" + explosion.name(), "&7", "&7*Click to set the pattern", "&7of the firework explosion effect."),
                        event -> colorParticlePane(player, itemMap, particle, lifetime, explosion, null)));
            }
        });
        patternPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the commands color particle.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void colorParticlePane(final Player player, final ItemMap itemMap, final String particle, final int lifetime, final Type explosion, final DyeColor color1) {
        Interface colorPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> particlePane(player, itemMap, 3)));
            for (DyeColor color : DyeColor.values()) {
                colorPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? color.name() +  "_DYE" : "351:8", 1, false, false, "&f" + color.name(), "&7", "&7*Click to set the " + (color1 != null ? "&c&lend color" : "&9&lstart color"), "&7of the firework explosion effect."), event -> {
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

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items' enchantment.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void enchantPane(final Player player, final ItemMap itemMap) {
        Interface enchantPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            enchantPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            for (Enchantment enchant : ItemHandler.getEnchants()) {
                boolean containsKey = itemMap.getEnchantments() != null && itemMap.getEnchantments().containsKey(ItemHandler.getEnchantName(enchant).toUpperCase());
                ItemStack enchantItem = ItemHandler.getItem((containsKey ? "ENCHANTED_BOOK" : "BOOK"), 1, false, false, "&f" + ItemHandler.getEnchantName(enchant).toUpperCase(), "&7",
                        "&7*Click to add this enchantment", "&7to the custom item.", "&7", "&9&lENABLED: &a" + (containsKey + "").toUpperCase(), (containsKey ? "&7" : ""),
                        (containsKey ? "&9&lLEVEL: &a" + itemMap.getEnchantments().get(ItemHandler.getEnchantName(enchant).toUpperCase()) : ""));
                enchantPane.addButton(new Button(enchantItem, event -> {
                    if (containsKey) {
                        Map<String, Integer> enchantments = itemMap.getEnchantments();
                        enchantments.remove(ItemHandler.getEnchantName(enchant).toUpperCase());
                        itemMap.setEnchantments(enchantments);
                        enchantPane(player, itemMap);
                    } else {
                        enchantLevelPane(player, itemMap, enchant);
                    }
                }));
            }
        });
        enchantPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an enactment level.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void enchantLevelPane(final Player player, final ItemMap itemMap, final Enchantment enchant) {
        Interface enchantLevelPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            enchantLevelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the enchant selection menu."), event -> enchantPane(player, itemMap)));
            enchantLevelPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Count", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ENCHANT LEVEL").with(Holder.INPUT_EXAMPLE, "86");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    Map<String, Integer> enchantments = (itemMap.getEnchantments() != null) ? itemMap.getEnchantments() : new HashMap<>();
                    enchantments.put(ItemHandler.getEnchantName(enchant).toUpperCase(), Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    itemMap.setEnchantments(enchantments);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ENCHANT LEVEL");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                enchantPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                enchantLevelPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lLevel: &a&l" + k, "&7", "&7*Click to set the", "&7level of the item enchantment.", "&7", "&7This will be &l" +
                        ItemHandler.getEnchantName(enchant).toUpperCase() + ":" + k), event -> {
                    Map<String, Integer> enchantments = (itemMap.getEnchantments() != null) ? itemMap.getEnchantments() : new HashMap<>();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void flagPane(final Player player, final ItemMap itemMap) {
        Interface flagPane = new Interface(true, 5, exitButton, GUIName, player);
        flagPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
            setItemFlags(itemMap);
            creatingPane(player, itemMap);
        }));
        SchedulerUtils.runAsync(() -> {
            flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isOpBypass(), false, "&a&l&nAllowOpBypass", "&7",
                    "&a&lTrue&f:&7 Allows players who are OP to", "&7bypass any itemflags that add", "&7restrictions for this item.", "&7",
                    "&c&lFalse&f:&7 Players who are OP will be", "&7restricted by itemflags that add", "&7restrictions for this item.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isOpBypass() + "").toUpperCase()), event -> {
                itemMap.setOpBypass(!itemMap.isOpBypass());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isCreativeBypass() ? (ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1") : "GOLDEN_APPLE", 1, itemMap.isCreativeBypass(), false, "&a&l&nCreativeBypass", "&7",
                    "&a&lTrue&f:&7 Allows players who are in Creative", "&7to bypass any itemflags that add", "&7restrictions for this item.", "&7",
                    "&c&lFalse&f:&7 Players who are in Creative will", "&7be restricted by itemflags that add", "&7restrictions for this item.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isCreativeBypass() + "").toUpperCase()), event -> {
                itemMap.setCreativeBypass(!itemMap.isCreativeBypass());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LAPIS_LAZULI" : "351:4"), 1, itemMap.isGlowing(), false, "&a&l&nGlowing", "&7",
                    "&a&lTrue&f:&7 The item will glow as if it was enchanted!", "&7",
                    "&c&lFalse&f:&7 The item will not glow.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isGlowing() + "").toUpperCase()), event -> {
                itemMap.setGlowing(!itemMap.isGlowing());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "DIAMOND_SHOVEL" : "277"), 7, itemMap.isStackable(), false, "&a&l&nStackable", "&7",
                    "&a&lTrue&f:&7 The item will be stackable with itself!", "&7",
                    "&c&lFalse&f:&7 The item stack only if it did in vanilla.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isStackable() + "").toUpperCase()), event -> {
                itemMap.setStackable(!itemMap.isStackable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_HELMET" : "306"), 1, itemMap.isNotHat(), false, "&a&l&nNot Hat", "&7",
                    "&a&lTrue&f:&7 Prevents the item from being worn as a hat", "&7using the command /hat from plugins", "&7such as Essentials or CMI.", "&7",
                    "&c&lFalse&f:&7 Allows the item to be worn as a /hat.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isNotHat() + "").toUpperCase()), event -> {
                itemMap.setNotHat(!itemMap.isNotHat());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LAVA_BUCKET" : "327"), 1, itemMap.isNoClear(), false, "&a&l&nNo Clear", "&7",
                    "&a&lTrue&f:&7 Prevents the item from being cleared", "&7using the command /clear from plugins", "&7such as Essentials or CMI.", "&7",
                    "&c&lFalse&f:&7 Allows the item to be cleared.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isNoClear() + "").toUpperCase()), event -> {
                itemMap.setNoClear(!itemMap.isNoClear());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "POPPY" : "38"), 1, itemMap.isInventoryClose(), false, "&a&l&nInventory Close", "&7",
                    "&a&lTrue&f:&7 Closes the players current", "&7inventory when clicking the item.", "&7",
                    "&c&lFalse&f:&7 The current inventory will not", "&7be closed when clicking the item.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isInventoryClose() + "").toUpperCase()), event -> {
                itemMap.setCloseInventory(!itemMap.isInventoryClose());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, itemMap.isAutoRemove(), false, "&a&l&nAuto Remove", "&7",
                    "&a&lTrue&f:&7 Automatically removes the", "&7item from the players inventory", "&7when entering or joining a", "&7world that is not defined", "&7under the enabled-worlds.", "&7",
                    "&c&lFalse&f:&7 The player will keep the", "&7item when entering or joining", "&7an undefined world.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isAutoRemove() + "").toUpperCase()), event -> {
                itemMap.setAutoRemove(!itemMap.isAutoRemove());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, itemMap.isOverwritable(), false, "&a&l&nOverwrite", "&7",
                    "&a&lTrue&f: &7Allows the item to overwrite", "&7any existing items in the defined slot.", "&7",
                    "&c&lFalse&f:&7 The item will not overwrite other items.", "&7When the slot is full it", "&7will fail to give the item, unless", "&7the give-next or move-next flag is set to &a&lTrue&7.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isOverwritable() + "").toUpperCase()), event -> {
                itemMap.setOverwritable(!itemMap.isOverwritable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("CACTUS", 1, itemMap.isDisposable(), false, "&a&l&nDisposable", "&7",
                    "&a&lTrue&f:&7 If the item has a command", "&7defined, running the command", "&7will remove x1 of the item.", "&7",
                    "&c&lFalse&f:&7 Running item commands will", "&7not lower the items count.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDisposable() + "").toUpperCase()), event -> {
                itemMap.setDisposable(!itemMap.isDisposable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WOODEN_SWORD" : "268"), 1, itemMap.isVanilla(), false, "&a&l&nVanilla", "&7",
                    "&a&lTrue&f:&7 The item will be given as a default no-name item.",
                    "&cNOTE: &7Itemflags and commands will NOT work", "&7unless the vanilla-status or vanilla-control", "&7itemflags are defined.", "&7",
                    "&c&lFalse&f:&7 The item will be given", "&7as an custom item, allowing all", "&7ItemJoin properties to continue working.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isVanilla() + "").toUpperCase()), event -> {
                itemMap.setVanilla(!itemMap.isVanilla());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, itemMap.isVanillaStatus(), false, "&a&l&nVanilla Status", "&7",
                    "&a&lTrue&f: &7Allows the Vanilla itemflag to retain", "&7ItemJoin properties without", "&7making it only a ItemJoin specific item.",
                    "&cNOTE: &7Useful for trying to implement", "&7ItemJoin items into other plugins.", "&7",
                    "&c&lFalse&f:&7 The item will be given", "&7as a custom item, allowing all", "&7ItemJoin properties to continue working.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isVanillaStatus() + "").toUpperCase()), event -> {
                itemMap.setVanillaStatus(!itemMap.isVanillaStatus());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WOODEN_PICKAXE" : "270"), 1, itemMap.isVanillaControl(), false, "&a&l&nVanilla Control", "&7",
                    "&a&lTrue&f: &7Allows the Vanilla itemflag to retain", "&7the use of commands and itemflags.", "&7",
                    "&c&lFalse&f:&7 The item will be given", "&7as an custom item, allowing all", "&7ItemJoin properties to continue working.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isVanillaControl() + "").toUpperCase()), event -> {
                itemMap.setVanillaControl(!itemMap.isVanillaControl());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, itemMap.isIpLimited(), false, "&a&l&nIP Limit", "&7",
                    "&a&lTrue&f:&7 The item will be tied to the players IP.", "&7No other players using the same IP will receive the item.", "&7Useful to prevent item duplication.", "&7",
                    "&c&lFalse&f:&7 The item will not be tied to a players IP.", "&7Other player accounts using the same IP will receive the item.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isIpLimited() + "").toUpperCase()), event -> {
                itemMap.setIpLimited(!itemMap.isIpLimited());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isUnbreakable() ? "ENCHANTED_BOOK" : "BOOK", 1, itemMap.isUnbreakable(), false, "&a&l&nUnbreakable", "&7",
                    "&a&lTrue&f:&7 Allows the item to be unbreakable or INDESTRUCTIBLE!", "&7",
                    "&c&lFalse&f:&7 The item will be damageable when being used.", "&7", "&7This flag only takes effect on items which have durability.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isUnbreakable() + "").toUpperCase()), event -> {
                itemMap.setUnbreakable(!itemMap.isUnbreakable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "358"), 1, itemMap.isFlagsInfo(), false, "&a&l&nHide Flags", "&7",
                    "&a&lTrue&f: &7Hides all information tags from the item", "&7such as firework colors, damage values, enchants, etc.", "&7",
                    "&c&lFalse&f:&7 The item will have information tags visible.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isFlagsInfo() + "").toUpperCase()), event -> {
                itemMap.setFlagsInfo(!itemMap.isFlagsInfo());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? (itemMap.isEnchantmentsInfo() ? "EXPERIENCE_BOTTLE" : "GLASS_BOTTLE") : (itemMap.isEnchantmentsInfo() ? "384" : "374")), 1, itemMap.isEnchantmentsInfo(), false, "&a&l&nHide Enchantments", "&7",
                    "&a&lTrue&f: &7Hides all enchantments on an item.", "&7",
                    "&c&lFalse&f:&7 The item will have enchantments visible.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isEnchantmentsInfo() + "").toUpperCase()), event -> {
                itemMap.setEnchantmentsInfo(!itemMap.isEnchantmentsInfo());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENDER_EYE" : "381"), 1, itemMap.isAttributesInfo(), false, "&a&l&nHide Attributes", "&7",
                    "&a&lTrue&f: &7Hides all attribute tags from the item", "&7such damage values, attack speed, hit points, etc.", "&7Typically this is the information that", "&7starts with (When in Main Hand:).", "&7",
                    "&c&lFalse&f:&7 The item will have information tags visible.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isAttributesInfo() + "").toUpperCase()), event -> {
                itemMap.setAttributesInfo(!itemMap.isAttributesInfo());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("IRON_SWORD", 1, itemMap.isDurabilityBar(), false, "&a&l&nHide Durability", "&7",
                    "&a&lTrue&f:&7 The durability bar of the damageable ", "&7item will be hidden.", "&cNOTE: &7Items with this flag will still break,", "&7unless the unbreakable flag is set to &a&lTrue&7.", "&7",
                    "&c&lFalse&f:&7 The durability bar of the", "&7damageable item will be shown as normal.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDurabilityBar() + "").toUpperCase()), event -> {
                itemMap.setDurabilityBar(!itemMap.isDurabilityBar());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("COBBLESTONE", 1, itemMap.isPlaceable(), false, "&a&l&nPlacement", "&7",
                    "&a&lTrue&f: &7Prevents the item from being", "&7placed on the ground,", "&7in any item-frames, and entities.", "&7",
                    "&c&lFalse&f: &7Item will be able to be placed.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isPlaceable() + "").toUpperCase()), event -> {
                itemMap.setPlaceable(!itemMap.isPlaceable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("BEDROCK", 1, itemMap.isMovement(), false, "&a&l&nInventory Modify", "&7",
                    "&a&lTrue&f: &7Prevents the item from being", "&7moved or switched to other slots", "&7and blocks placement in item-frames.", "&7",
                    "&c&lFalse&f:&7 Allows the item to be moved", "&7freely inside the players inventory.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isMovement() + "").toUpperCase()), event -> {
                itemMap.setMovement(!itemMap.isMovement());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_BOOTS", 1, itemMap.isEquip(), false, "&a&l&nCancel Equip", "&7",
                    "&a&lTrue&f: &7Prevents the item from being", "&7moved or switched to armor slots.", "&7",
                    "&c&lFalse&f:&7 Allows the item to be moved", "&7freely inside the players inventory.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isEquip() + "").toUpperCase()), event -> {
                itemMap.setEquip(!itemMap.isEquip());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("EGG", 1, itemMap.isAnimated(), false, "&a&l&nAnimate", "&7",
                    "&a&lTrue&f: &7Allows the item to animate between", "&7its different iterations defined", "&7under the animations tab.", "&7",
                    "&c&lFalse&f: &7Item will not animate.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isAnimated() + "").toUpperCase()), event -> {
                if (itemMap.isAnimated()) {
                    itemMap.setAnimate(false);
                    if (itemMap.getDynamicNames() != null && !itemMap.getDynamicNames().isEmpty()) {
                        itemMap.setCustomName(ItemHandler.cutDelay(itemMap.getDynamicNames().get(0)));
                        itemMap.setDynamicNames(new ArrayList<>());
                    }
                } else {
                    itemMap.setAnimate(true);
                    if (itemMap.getCustomName() != null && !itemMap.getCustomName().isEmpty()) {
                        List<String> names = new ArrayList<>();
                        names.add(itemMap.getCustomName());
                        itemMap.setDynamicNames(names);
                    }
                }
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.isDynamic(), false, "&a&l&nDynamic", "&7",
                    "&a&lTrue&f: &7Allows the item to dynamically", "&7update every 100 ticks", "&7Useful for updating placeholders.", "&7",
                    "&c&lFalse&f: &7Item will not update its name, lore, etc.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDynamic() + "").toUpperCase()), event -> {
                itemMap.setDynamic(!itemMap.isDynamic());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 4, itemMap.isDynamicCount(), false, "&a&l&nDynamic Count", "&7",
                    "&a&lTrue&f: &7When an item dynamically updates", "&7the items count will be reset to its default.", "&7Only functions with the dynamic or animate itemflag.", "&7",
                    "&c&lFalse&f: &7The item will keep its existing count.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDynamicCount() + "").toUpperCase()), event -> {
                itemMap.setDynamicCount(!itemMap.isDynamicCount());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CHEST_MINECART" : "342"), 1, itemMap.isItemStore(), false, "&a&l&nItem Store", "&7",
                    "&a&lTrue&f:&7 Prevents the storage of the item in any containers.", "&7Such as chests, armor stands, anvils, etc.", "&7",
                    "&c&lFalse&f:&7 The item can be stored in containers.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isItemStore() + "").toUpperCase()), event -> {
                itemMap.setItemStore(!itemMap.isItemStore());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, itemMap.isCountLock(), false, "&a&l&nCount Lock", "&7",
                    "&a&lTrue&f:&7 The item can be used indefinitely.", "&7Useful to give a player infinite apples.", "&cNOTE: &7This will overwrite the disposable flag.", "&7",
                    "&c&lFalse&f:&7 The item will be removed from the inventory on use.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isCountLock() + "").toUpperCase()), event -> {
                itemMap.setCountLock(!itemMap.isCountLock());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, itemMap.isDeathKeepable(), false, "&a&l&nDeath Keep", "&7",
                    "&a&lTrue&f:&7 On death, the item will remain", "&7in players inventory on respawn.", "&7",
                    "&c&lFalse&f:&7 On death, the item will be dropped", "&7at the death location as normal.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDeathKeepable() + "").toUpperCase()), event -> {
                itemMap.setDeathKeepable(!itemMap.isDeathKeepable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("BONE", 1, itemMap.isDeathDroppable(), false, "&a&l&nDeath Drops", "&7",
                    "&a&lTrue&f:&7 On death, the item will be removed", "&7from the players inventory.", "&7",
                    "&c&lFalse&f:&7 On death, the item will be dropped", "&7at the death location as normal.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDeathDroppable() + "").toUpperCase()), event -> {
                itemMap.setDeathDroppable(!itemMap.isDeathDroppable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, itemMap.isSelfDroppable(), false, "&a&l&nSelf Drops", "&7",
                    "&a&lTrue&f: &7Prevents the item from being dropped", "&7by the player, returns it back to their inventory.", "&7",
                    "&c&lFalse&f: &7Allows the item to be dropped.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isSelfDroppable() + "").toUpperCase()), event -> {
                itemMap.setSelfDroppable(!itemMap.isSelfDroppable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "FLINT_AND_STEEL" : "259", 1, itemMap.isEraseDroppable(), false, "&a&l&nErase Drops", "&7",
                    "&a&lTrue&f: &7Deletes the item if it is", "&7dropped by the player.", "&7",
                    "&c&lFalse&f: &7Allows the item to be dropped.", "&7",
                    "&e&nNote: &eThis is similar to self-drops", "&eand death-drops, but instead the", "&eitem is entirely deleted.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isEraseDroppable() + "").toUpperCase()), event -> {
                itemMap.setEraseDroppable(!itemMap.isEraseDroppable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("FURNACE", 1, itemMap.isItemModify(), false, "&a&l&nItem Modifiable", "&7",
                    "&a&lTrue&f: &7Blocks the item from being", "&7repaired or enchanted in-game.", "&7",
                    "&c&lFalse&f: &7Allows items to", "&7be repaired and enchanted.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isItemModify() + "").toUpperCase()), event -> {
                itemMap.setItemModify(!itemMap.isItemModify());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("ANVIL", 1, itemMap.isItemRepairable(), false, "&a&l&nItem Repairable", "&7",
                    "&a&lTrue&f: &7Blocks the item from being", "&7used in an anvil or repaired.", "&7",
                    "&c&lFalse&f: &7Allows the item to be repaired.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isItemRepairable() + "").toUpperCase()), event -> {
                itemMap.setItemRepairable(!itemMap.isItemRepairable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CRAFTING_TABLE" : "58"), 1, itemMap.isItemCraftable(), false, "&a&l&nItem Craftable", "&7",
                    "&a&lTrue&f: &7Blocks the item from being", "&7used in a crafting recipe.", "&7",
                    "&c&lFalse&f: &7Item will be usable in", "&7a crafting recipe.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isItemCraftable() + "").toUpperCase()), event -> {
                itemMap.setItemCraftable(!itemMap.isItemCraftable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("BOW", 1, itemMap.isCancelEvents(), false, "&a&l&nCancel Events", "&7",
                    "&a&lTrue&f: &7Prevents almost any event from executing", "&7when right-clicking the item.", "&7",
                    "&c&lFalse&f: &7Allows item events to be executed freely.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isCancelEvents() + "").toUpperCase()), event -> {
                itemMap.setCancelEvents(!itemMap.isCancelEvents());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WHEAT_SEEDS" : "295"), 1, itemMap.isAlwaysGive(), false, "&a&l&nAlways Give", "&7",
                    "&a&lTrue&f: &7Gives the item every time the player", "&7performs one of the triggers actions.", "&7regardless of already having the item.", "&7",
                    "&cNOTE: &7Don't use this if you want only ONE instance of the item.", "&7",
                    "&c&lFalse&f: &7Normal item restrictions will apply.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isAlwaysGive() + "").toUpperCase()), event -> {
                itemMap.setAlwaysGive(!itemMap.isAlwaysGive());
                flagPane(player, itemMap);
            }));
            if (itemMap.getMaterial().toString().contains("TIPPED_ARROW") || itemMap.getMaterial().toString().contains("ARROW")) {
                flagPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, itemMap.isTeleport(), false, "&a&l&nTeleport", "&7",
                        "&a&lTrue&f: &7Teleports the Player to the location", "&7that the arrow landed.",
                        "&cNOTE: &7This only works if the arrow is fired by a Bow.",
                        "&9&lENABLED: &a" + (itemMap.isTeleport() + "").toUpperCase()), event -> {
                    itemMap.setTeleport(!itemMap.isTeleport());
                    flagPane(player, itemMap);
                }));
            } else {
                flagPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, false, false, "&c&l&nX", "&7", "&c*Not Available")));
            }
            flagPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTING_TABLE" : "116"), 1, itemMap.isItemChangeable(), false, "&a&l&nAllow Modifications", "&7",
                    "&a&lTrue&f: &7Allows the players to modify the item", "&7while retaining all properties.", "&7",
                    "&c&lFalse&f: &7Item will not be modifiable.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isItemChangeable() + "").toUpperCase()), event -> {
                itemMap.setItemChangeable(!itemMap.isItemChangeable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("ITEM_FRAME", 1, itemMap.isGiveNext(), false, "&a&l&nGive Next", "&7",
                    "&a&lTrue&f: &7Gives the item to the next available slot", "&7only if the defined slot already has an existing item.",
                    "&cNOTE: &7The overwrite flag will not work.", "&7",
                    "&c&lFalse&f:&7 The item will be only given in the defined slot.", "&7If an item is already in the slot the", "&7item wont be given, unless the overwrite", "&7flag is set to &l&aTrue&7.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isGiveNext() + "").toUpperCase()), event -> {
                itemMap.setGiveNext(!itemMap.isGiveNext());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, itemMap.isMoveNext(), false, "&a&l&nMove Next", "&7",
                    "&a&lTrue&f: &7Moves the existing item to the next available slot", "&7only if the defined slot already has an existing item.",
                    "&cNOTE: &7The overwrite flag will not work.", "&7",
                    "&c&lFalse&f: &7The item will be only given in the defined slot.", "&7If an item is already in the slot the", "&7item wont be given, unless the overwrite", "&7flag is set to &l&aTrue&7.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isMoveNext() + "").toUpperCase()), event -> {
                itemMap.setMoveNext(!itemMap.isMoveNext());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, itemMap.isDropFull(), false, "&a&l&nDrop Full", "&7",
                    "&a&lTrue&f: &7Drops the item on the ground if", "&7the players inventory is full.", "&7",
                    "&c&lFalse&f: &7Fails to give the item", "&7if the players inventory is full.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isDropFull() + "").toUpperCase()), event -> {
                itemMap.setDropFull(!itemMap.isDropFull());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, itemMap.isSelectable(), false, "&a&l&nSelectable", "&7",
                    "&a&lTrue&f: &7Prevents the item from being", "&7held in the players hand.", "&7",
                    "&c&lFalse&f: &7Allows the item to be selected.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isSelectable() + "").toUpperCase()), event -> {
                itemMap.setSelectable(!itemMap.isSelectable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(ItemHandler.getItem("DIRT", 64, itemMap.isSplittable(), false, "&a&l&nSplittable", "&7",
                    "&a&lTrue&f: &7Prevents the item from being", "&7split into multiple stacks via", "&7right-click or click dragging.", "&7",
                    "&c&lFalse&f: &7Allows the item to be split", "&7into multiple stacks.", "&7",
                    "&9&lENABLED: &a" + (itemMap.isSplittable() + "").toUpperCase()), event -> {
                itemMap.setSplittable(!itemMap.isSplittable());
                flagPane(player, itemMap);
            }));
            flagPane.addButton(new Button(fillerPaneBItem), 29);
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
        if (itemMap.isVanilla()) {
            itemflags += "VANILLA, ";
        }
        if (itemMap.isVanillaStatus()) {
            itemflags += "VANILLA-STATUS, ";
        }
        if (itemMap.isVanillaControl()) {
            itemflags += "VANILLA-CONTROL, ";
        }
        if (itemMap.isIpLimited()) {
            itemflags += "IP-LIMIT, ";
        }
        if (itemMap.isUnbreakable()) {
            itemflags += "UNBREAKABLE, ";
        }
        if (itemMap.isAttributesInfo()) {
            itemflags += "HIDE-ATTRIBUTES, ";
        }
        if (itemMap.isEnchantmentsInfo()) {
            itemflags += "HIDE-ENCHANTS, ";
        }
        if (itemMap.isNotHat()) {
            itemflags += "NOT-HAT, ";
        }
        if (itemMap.isNoClear()) {
            itemflags += "NO-CLEAR, ";
        }
        if (itemMap.isFlagsInfo()) {
            itemflags += "HIDE-FLAGS, ";
        }
        if (itemMap.isDurabilityBar()) {
            itemflags += "HIDE-DURABILITY, ";
        }
        if (itemMap.isPlaceable()) {
            itemflags += "PLACEMENT, ";
        }
        if (itemMap.isEquip()) {
            itemflags += "CANCEL-EQUIP, ";
        }
        if (itemMap.isMovement()) {
            itemflags += "INVENTORY-MODIFY, ";
        }
        if (itemMap.isInventoryClose()) {
            itemflags += "INVENTORY-CLOSE, ";
        }
        if (itemMap.isDynamic()) {
            itemflags += "DYNAMIC, ";
        }
        if (itemMap.isDynamicCount()) {
            itemflags += "DYNAMIC-COUNT, ";
        }
        if (itemMap.isAnimated()) {
            itemflags += "ANIMATE, ";
        }
        if (itemMap.isGlowing()) {
            itemflags += "GLOWING, ";
        }
        if (itemMap.isStackable()) {
            itemflags += "STACKABLE, ";
        }
        if (itemMap.isSelectable()) {
            itemflags += "SELECTABLE, ";
        }
        if (itemMap.isSplittable()) {
            itemflags += "SPLITTABLE, ";
        }
        if (itemMap.isItemStore()) {
            itemflags += "ITEM-STORE, ";
        }
        if (itemMap.isCancelEvents()) {
            itemflags += "CANCEL-EVENTS, ";
        }
        if (itemMap.isCountLock()) {
            itemflags += "COUNT-LOCK, ";
        }
        if (itemMap.isDeathKeepable()) {
            itemflags += "DEATH-KEEP, ";
        }
        if (itemMap.isDeathDroppable()) {
            itemflags += "DEATH-DROPS, ";
        }
        if (itemMap.isSelfDroppable()) {
            itemflags += "SELF-DROPS, ";
        }
        if (itemMap.isEraseDroppable()) {
            itemflags += "ERASE-DROPS, ";
        }
        if (itemMap.isDisposable()) {
            itemflags += "DISPOSABLE, ";
        }
        if (itemMap.isItemModify()) {
            itemflags += "ITEM-MODIFIABLE, ";
        }
        if (itemMap.isItemRepairable()) {
            itemflags += "ITEM-REPAIRABLE, ";
        }
        if (itemMap.isItemCraftable()) {
            itemflags += "ITEM-CRAFTABLE, ";
        }
        if (itemMap.isAlwaysGive()) {
            itemflags += "ALWAYS-GIVE, ";
        }
        if (itemMap.isItemChangeable()) {
            itemflags += "ITEM-CHANGEABLE, ";
        }
        if (itemMap.isTeleport()) {
            itemflags += "TELEPORT, ";
        }
        if (itemMap.isGiveNext()) {
            itemflags += "GIVE-NEXT, ";
        }
        if (itemMap.isMoveNext()) {
            itemflags += "MOVE-NEXT, ";
        }
        if (itemMap.isDropFull()) {
            itemflags += "DROP-FULL, ";
        }
        if (itemMap.isOverwritable()) {
            itemflags += "OVERWRITE, ";
        }
        if (itemMap.isOpBypass()) {
            itemflags += "ALLOWOPBYPASS, ";
        }
        if (itemMap.isAutoRemove()) {
            itemflags += "AUTO-REMOVE, ";
        }
        if (itemMap.isCreativeBypass()) {
            itemflags += "CREATIVEBYPASS, ";
        }
        if (itemflags.endsWith(", ")) {
            itemflags = itemflags.substring(0, itemflags.length() - 2);
        }
        itemMap.setItemFlags(itemflags);
    }

//  ============================================================================================================================================================================================================================================================

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items triggers.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void triggerPane(final Player player, final ItemMap itemMap) {
        Interface triggerPane = new Interface(false, 4, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, itemMap.isGiveOnDisabled(), false, "&c&l&nDISABLED", "&7", "&7*Prevents the item from given", "&7through the use of triggers.", "&7", "&7Useful to only get the item",
                    "&7using &l/itemjoin get <item>", "&9&lENABLED: &a" + (itemMap.isGiveOnDisabled() + "").toUpperCase()), event -> {
                if (itemMap.isGiveOnDisabled()) {
                    itemMap.setGiveOnDisabled(false);
                } else {
                    itemMap.setGiveOnJoin(false);
                    itemMap.setOnlyFirstJoin(false);
                    itemMap.setOnlyFirstWorld(false);
                    itemMap.setOnlyFirstLife(false);
                    itemMap.setGiveOnRespawn(false);
                    itemMap.setGiveOnRespawnPoint(false);
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
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, itemMap.isGiveOnJoin(), false, "&a&l&nJoin", "&7", "&7*Gives the item when the", "&7player logs into the server.",
                    "&9&lENABLED: &a" + (itemMap.isGiveOnJoin() + "").toUpperCase()), event -> {
                itemMap.setGiveOnJoin(!itemMap.isGiveOnJoin());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, itemMap.isGiveOnWorldSwitch(), false, "&a&l&nWorld Switch", "&7", "&7*Gives the item when the", "&7player teleports to one", "&7of the specified worlds.",
                    "&9&lENABLED: &a" + (itemMap.isGiveOnWorldSwitch() + "").toUpperCase()), event -> {
                itemMap.setGiveOnWorldSwitch(!itemMap.isGiveOnWorldSwitch());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isGiveOnRespawn(), false, "&a&l&nRespawn", "&7", "&7*Gives the item when the", "&7player respawns from a death event.", "&9&lENABLED: &a" +
                    (itemMap.isGiveOnRespawn() + "").toUpperCase()), event -> {
                itemMap.setGiveOnRespawn(!itemMap.isGiveOnRespawn());
                itemMap.setGiveOnRespawnPoint(false);
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "MAP"), 1, itemMap.isOnlyFirstJoin(), true, "&e&l&nFirst Join", "&7", "&7*Gives the item when the", "&7player logs into the server",
                    "&7for the first time only.", "&7This will overwrite any triggers", "&7such as respawn, and world-switch.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstJoin() + "").toUpperCase()), event -> {
                itemMap.setOnlyFirstJoin(!itemMap.isOnlyFirstJoin());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, itemMap.isOnlyFirstWorld(), false, "&e&l&nFirst World", "&7", "&7*Gives the item when the", "&7player enters each of the defined", "&7worlds for the first time.", "&7",
                    "&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
                itemMap.setOnlyFirstWorld(!itemMap.isOnlyFirstWorld());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? "TOTEM_OF_UNDYING" : "322:1", 1, itemMap.isOnlyFirstLife(), false, "&e&l&nFirst Life", "&7", "&7*Gives the item when the", "&7player logs into the server",
                    "&7for the first time only,", "&7but will give the item", "&7EVERY TIME on player RESPAWN.", "&7This flag overwrites any triggers", "&7such as respawn, and join.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
                itemMap.setOnlyFirstWorld(!itemMap.isOnlyFirstWorld());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_DOOR" : "330"), 1, itemMap.isGiveOnPermissionSwitch(), false, "&e&l&nPermission Switch", "&7", "&7*Gives the item when the", "&7player is granted permission", "&7to receive the item.", "&7", "&7Removes the item when the", "&7player has the permission revoked.", "&9&lENABLED: &a" +
                    (itemMap.isGiveOnPermissionSwitch() + "").toUpperCase()), event -> {
                itemMap.setGiveOnPermissionSwitch(!itemMap.isGiveOnPermissionSwitch());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem("COAL", 1, itemMap.isGiveOnRespawnPoint(), false, "&e&l&nRespawn Point", "&7", "&7*Gives the item when the", "&7player respawns from a death event.", "&7", "&c&nException:&7 The item will not be given if", "&7spawning in a &lBED&7, &lANCHOR&7, or &lSPAWN-POINT&7.", "&9&lENABLED: &a" +
                    (itemMap.isGiveOnRespawnPoint() + "").toUpperCase()), event -> {
                itemMap.setGiveOnRespawnPoint(!itemMap.isGiveOnRespawnPoint());
                itemMap.setGiveOnRespawn(false);
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENDER_PEARL" : "368"), 1, itemMap.isGiveOnTeleport(), false, "&e&l&nTeleport", "&7", "&7*Gives the item when the", "&7player teleports to a new location.",
                    "&9&lENABLED: &a" + (itemMap.isGiveOnTeleport() + "").toUpperCase()), event -> {
                itemMap.setGiveOnTeleport(!itemMap.isGiveOnTeleport());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(ItemHandler.getItem("LEVER", 1, itemMap.isUseOnLimitSwitch(), false, "&e&l&nGamemode Switch", "&7", "&7*Gives the item when the", "&7player changes gamemodes to any", "&7of the defined limit-modes.",
                    "&9&lENABLED: &a" + (itemMap.isUseOnLimitSwitch() + "").toUpperCase()), event -> {
                itemMap.setUseOnLimitSwitch(!itemMap.isUseOnLimitSwitch());
                triggerPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, itemMap.isGiveOnRegionEnter(), false, "&e&l&nRegion Enter", "&7", "&7*Gives the item when the", "&7player enters any of the enabled-regions.", "&9&lENABLED: &a" +
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
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "HOPPER_MINECART" : "408"), 1, itemMap.isGiveOnRegionLeave(), false, "&e&l&nRegion Leave", "&7", "&7*Gives the item when the", "&7player leaves any of the enabled-regions.", "&9&lENABLED: &a" +
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
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "TNT_MINECART" : "407"), 1, itemMap.isGiveOnRegionAccess(), false, "&e&l&nRegion Access", "&7", "&7*Gives the item when the", "&7player enters any of the enabled-regions", "&7and removes the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
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
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CHEST_MINECART" : "342"), 1, itemMap.isGiveOnRegionEgress(), false, "&e&l&nRegion Egress", "&7", "&7*Removes the item when the", "&7player enters any of the enabled-regions", "&7and gives the item when leaving", "&7any of the enabled-regions.", "&9&lENABLED: &a" +
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
            triggerPane.addButton(new Button(fillerPaneBItem));
            triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
                setTriggers(itemMap);
                creatingPane(player, itemMap);
            }));
            triggerPane.addButton(new Button(fillerPaneBItem), 7);
            triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
                setTriggers(itemMap);
                creatingPane(player, itemMap);
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
        if (itemMap.isGiveOnDisabled()) {
            triggers += "DISABLED, ";
        }
        if (itemMap.isGiveOnJoin() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) {
            triggers += "JOIN, ";
        }
        if (itemMap.isOnlyFirstJoin()) {
            triggers += "FIRST-JOIN, ";
        }
        if (itemMap.isOnlyFirstWorld()) {
            triggers += "FIRST-WORLD, ";
        }
        if (itemMap.isOnlyFirstLife()) {
            triggers += "FIRST-LIFE, ";
        }
        if (itemMap.isGiveOnTeleport() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) {
            triggers += "TELEPORT, ";
        }
        if (itemMap.isGiveOnRespawn() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) {
            triggers += "RESPAWN, ";
        }
        if (itemMap.isGiveOnRespawnPoint() && !itemMap.isOnlyFirstJoin() && !itemMap.isOnlyFirstWorld()) {
            triggers += "RESPAWN-POINT, ";
        }
        if (itemMap.isGiveOnWorldSwitch() && !itemMap.isOnlyFirstWorld()) {
            triggers += "WORLD-SWITCH, ";
        }
        if (itemMap.isGiveOnPermissionSwitch()) {
            triggers += "PERMISSION-SWITCH, ";
        }
        if (itemMap.isUseOnLimitSwitch()) {
            triggers += "GAMEMODE-SWITCH, ";
        }
        if (itemMap.isGiveOnRegionEnter()) {
            triggers += "REGION-ENTER, ";
        }
        if (itemMap.isGiveOnRegionLeave()) {
            triggers += "REGION-LEAVE, ";
        }
        if (itemMap.isGiveOnRegionAccess()) {
            triggers += "REGION-ACCESS, ";
        }
        if (itemMap.isGiveOnRegionEgress()) {
            triggers += "REGION-EGRESS, ";
        }
        if (triggers.endsWith(", ")) {
            triggers = triggers.substring(0, triggers.length() - 2);
        }
        itemMap.setTriggers(triggers);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items worlds.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The type of selection Pane.
     */
    private static void worldPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface worldPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage == 4) {
                worldPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                worldPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            }
            List<String> listWorlds = (stage == 4 ? new ArrayList<>() : (stage == 0 ? itemMap.getDisabledWorlds() : itemMap.getEnabledWorlds()));
            final String worlds = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds");
            if (stage == 4 && worlds != null) {
                listWorlds.addAll(Arrays.asList(worlds.replace(" ", "").split(",")));
            }
            worldPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, (stage == 4 ? (StringUtils.containsValue(listWorlds, "ALL") || StringUtils.containsValue(listWorlds, "GLOBAL")) : itemMap.containsWorld("ALL", (stage == 0))), false, "&a&l&nGLOBAL", "&7", "&7*Click to " +
                    (stage == 0 ? "disable" : "enable") + " the", "&7custom item in &lALL WORLDS.", (stage == 0 ? "&9&lDISABLED:" : "&9&lENABLED:") + " &a" +
                    (stage == 4 ? (StringUtils.containsValue(listWorlds, "ALL") || StringUtils.containsValue(listWorlds, "GLOBAL")) : (itemMap.containsWorld("ALL", (stage == 0))) + "")), event -> {
                if ((stage == 4 && (StringUtils.containsValue(listWorlds, "ALL") || StringUtils.containsValue(listWorlds, "GLOBAL"))) || (stage != 4 && itemMap.containsWorld("ALL", (stage == 0)))) {
                    listWorlds.clear();
                    listWorlds.add("DISABLED");
                } else {
                    listWorlds.clear();
                    listWorlds.add("GLOBAL");
                }
                if (stage == 4) {
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.enabled-worlds", (listWorlds.isEmpty() ? "DISABLED" : "GLOBAL"));
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> worldPane(player, itemMap, stage));
                } else if (stage == 0) {
                    itemMap.setDisabledWorlds(listWorlds);
                    worldPane(player, itemMap, stage);
                } else {
                    itemMap.setEnabledWorlds(listWorlds);
                    worldPane(player, itemMap, stage);
                }
            }));
            for (World world : Bukkit.getServer().getWorlds()) {
                String worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
                if (world.getEnvironment().equals(Environment.NETHER)) {
                    worldMaterial = "NETHERRACK";
                } else if (world.getEnvironment().equals(Environment.THE_END)) {
                    worldMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
                }
                worldPane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, (stage == 4 ? StringUtils.containsValue(listWorlds, world.getName()) : itemMap.containsWorld(world.getName(), (stage == 0))),
                        false, "&f&l" + world.getName(), "&7", "&7*Click to " + (stage == 0 ? "disable" : "enable") + " the", "&7custom item in this world.",
                        (stage == 0 ? "&9&lDISABLED:" : "&9&lENABLED:") + " &a" + (stage == 4 ? StringUtils.containsValue(listWorlds, world.getName()) : (itemMap.containsWorld(world.getName(), (stage == 0))) + "")), event -> {
                    if ((stage == 4 && StringUtils.containsValue(listWorlds, world.getName())) || (stage != 4 && itemMap.containsWorld(world.getName(), (stage == 0)))) {
                        listWorlds.remove(world.getName());
                    } else {
                        listWorlds.add(world.getName());
                    }
                    if (listWorlds.isEmpty()) {
                        listWorlds.add("DISABLED");
                    } else if (StringUtils.containsValue(listWorlds, "DISABLED") || StringUtils.containsValue(listWorlds, "DISABLE")) {
                        listWorlds.remove("DISABLED");
                    }
                    if (stage == 4) {
                        StringBuilder worldList = new StringBuilder();
                        for (String worldString : listWorlds) {
                            worldList.append(worldString).append(", ");
                        }
                        if (!StringUtils.isEmpty(worldList)) {
                            worldList = new StringBuilder(worldList.substring(0, worldList.length() - 2));
                        }
                        File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                        dataFile.set("Active-Commands.enabled-worlds", worldList.toString());
                        ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                        PluginData.getInfo().softReload();
                        SchedulerUtils.runLater(2L, () -> worldPane(player, itemMap, stage));
                    } else if (stage == 0) {
                        itemMap.setDisabledWorlds(listWorlds);
                        worldPane(player, itemMap, stage);
                    } else {
                        itemMap.setEnabledWorlds(listWorlds);
                        worldPane(player, itemMap, stage);
                    }
                }));
            }
        });
        worldPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items regions.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The type of selection Pane.
     */
    private static void regionPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface regionPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            regionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            List<String> regions = (stage == 0 ? itemMap.getDisabledRegions() : itemMap.getEnabledRegions());
            regionPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.containsRegion("UNDEFINED", (stage == 0)), false, "&c&l&nUNDEFINED", "&7", "&7*Click to " + (stage == 0 ? "disable" : "enable") + " the", "&7custom item in &lALL REGIONS.", (stage == 0 ? "&9&lDISABLED: &a" : "&9&lENABLED: &a") +
                    (itemMap.containsRegion("UNDEFINED", (stage == 0)) + "").toUpperCase()), event -> {
                if (itemMap.containsRegion("UNDEFINED", (stage == 0))) {
                    regions.remove("UNDEFINED");
                } else {
                    regions.add("UNDEFINED");
                }
                if (stage == 0) {
                    itemMap.setDisabledRegions(regions);
                } else {
                    itemMap.setEnabledRegions(regions);
                }
                regionPane(player, itemMap, stage);
            }));
            for (final World world : Bukkit.getServer().getWorlds()) {
                for (final String region : ItemJoin.getCore().getDependencies().getGuard().getRegions(world).keySet()) {
                    String regionMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2");
                    if (world.getEnvironment().equals(Environment.NETHER)) {
                        regionMaterial = "NETHERRACK";
                    } else if (world.getEnvironment().equals(Environment.THE_END)) {
                        regionMaterial = (ServerUtils.hasSpecificUpdate("1_13") ? "END_STONE" : "121");
                    }
                    regionPane.addButton(new Button(ItemHandler.getItem(regionMaterial, 1, itemMap.containsRegion(region, (stage == 0)) && itemMap.containsWorld(world.getName(), false), false, "&f&l" + region, "&7", "&a&lWORLD: &f" + world.getName(), "&7", "&7*Click to " + (stage == 0 ? "disable" : "enable") + " the",
                            "&7custom item in this region.", (stage == 0 ? "&9&lDISABLED: &a" : "&9&lENABLED: &a") + ((itemMap.containsRegion(region, (stage == 0)) && itemMap.containsWorld(world.getName(), false)) + "").toUpperCase()), event -> {
                        if (itemMap.containsRegion(region, (stage == 0)) && itemMap.containsWorld(world.getName(), false)) {
                            regions.remove(region);
                            clearWorlds(itemMap, stage);
                        } else {
                            clearWorlds(itemMap, stage);
                            if (!itemMap.containsRegion(region, (stage == 0))) {
                                regions.add(region);
                            }
                            if (!itemMap.containsWorld(world.getName(), false)) {
                                final List<String> worldList = itemMap.getEnabledWorlds();
                                worldList.add(world.getName().toUpperCase());
                                itemMap.setEnabledWorlds(worldList);
                            }
                        }
                        if (stage == 0) {
                            itemMap.setDisabledRegions(regions);
                        } else {
                            itemMap.setEnabledRegions(regions);
                        }
                        regionPane(player, itemMap, stage);
                    }));
                }
            }
        });
        regionPane.open(player);
    }

    /**
     * Clears the worlds that are currently not used for any specific region list.
     *
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The type of selection Pane.
     */
    private static void clearWorlds(final ItemMap itemMap, final int stage) {
        for (final World worldRemoval : Bukkit.getServer().getWorlds()) {
            if (itemMap.containsWorld(worldRemoval.getName(), false)) {
                boolean hasRegion = false;
                for (final String regionExists : ItemJoin.getCore().getDependencies().getGuard().getRegions(worldRemoval).keySet()) {
                    if (itemMap.containsRegion(regionExists, (stage == 0))) {
                        hasRegion = true;
                    }
                }
                if (!hasRegion) {
                    final List<String> worldList = itemMap.getEnabledWorlds();
                    worldList.removeIf(s -> s.equalsIgnoreCase(worldRemoval.getName()));
                    itemMap.setEnabledWorlds(worldList);
                }
            }
        }
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying an items' lore.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void lorePane(final Player player, final ItemMap itemMap) {
        Interface lorePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            lorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            lorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to the item lore."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "LORE LINE").with(Holder.INPUT_EXAMPLE, "&bThis is a new lore line.");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "LORE LINE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                List<String> lore = new ArrayList<>();
                if (itemMap.getCustomLore() != null) {
                    lore = itemMap.getCustomLore();
                }
                lore.add(StringUtils.restoreColor(event.getMessage()));
                itemMap.setCustomLore(lore);
                lorePane(player, itemMap);
            }));
            if (itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
                for (int i = 1; i <= itemMap.getCustomLore().size(); i++) {
                    final int k = i;
                    lorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, false, "&fLore " + k, "&7", "&7*Click modify this lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(k - 1)), event -> modifyLoreLinePane(player, itemMap, k - 1)));
                }
            }
        });
        lorePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying a specific lore line.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyLoreLinePane(final Player player, final ItemMap itemMap, final int position) {
        Interface modifyLorePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, false, "&e&l&nModify", "&7", "&7*Change the lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(position)), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "LORE LINE").with(Holder.INPUT_EXAMPLE, "&bThis is a new lore line.");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                List<String> lore = itemMap.getCustomLore();
                lore.set(position, StringUtils.restoreColor(event.getMessage()));
                itemMap.setCustomLore(lore);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "LORE LINE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                modifyLoreLinePane(event.getPlayer(), itemMap, position);
            }));
            modifyLorePane.addButton(new Button(fillerPaneGItem));
            modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this lore line."), event -> {
                List<String> lore = itemMap.getCustomLore();
                lore.remove(position);
                itemMap.setCustomLore(lore);
                lorePane(player, itemMap);
            }));
            modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> lorePane(player, itemMap)));
            modifyLorePane.addButton(new Button(fillerPaneBItem), 7);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> lorePane(player, itemMap)));
        });
        modifyLorePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated material.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void animateMaterialPane(final Player player, final ItemMap itemMap) {
        Interface animateMaterialPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            animateMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
                if (!itemMap.getDynamicMaterials().isEmpty()) {
                    itemMap.setAnimate(true);
                    if (ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)).contains(":")) {
                        String[] material = ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)).split(":");
                        itemMap.setMaterial(ItemHandler.getMaterial(material[0], null));
                        itemMap.setDataValue((short) Integer.parseInt(material[1]));
                    } else {
                        itemMap.setMaterial(ItemHandler.getMaterial(ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(0)), null));
                    }
                }
                animationPane(player, itemMap);
            }));
            animateMaterialPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Material", "&7", "&7*Add a new material", "&7to be animated between."), event -> selectMaterialPane(player, itemMap, 0, true)));
            for (int i = 1; i <= itemMap.getDynamicMaterials().size(); i++) {
                final int k = i;
                final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicMaterials().get(k - 1)));
                animateMaterialPane.addButton(new Button(ItemHandler.getItem(ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(k - 1)), 1, false, false, "&fMaterial " + k, "&7", "&7*Click modify this animated material.",
                        "&9&lAnimation Ticks: &a" + (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> modifyMaterialPane(player, itemMap, k - 1)));
            }
        });
        animateMaterialPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated material.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void selectMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew) {
        Interface selectMaterialPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            selectMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> animateMaterialPane(player, itemMap)));
            selectMaterialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, false, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL").with(Holder.INPUT_EXAMPLE, "IRON_SWORD");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != Material.AIR) {
                    if (isNew) {
                        durationMaterialPane(player, itemMap, position, true, ChatColor.stripColor(event.getMessage()).toUpperCase());
                    } else {
                        List<String> mats = itemMap.getDynamicMaterials();
                        mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + ChatColor.stripColor(event.getMessage()).toUpperCase());
                        itemMap.setDynamicMaterials(mats);
                        modifyMaterialPane(player, itemMap, position);
                    }
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noMaterial", player, placeHolders);
                    selectMaterialPane(player, itemMap, position, isNew);
                }
            }));
            Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (!material.name().contains("LEGACY") && !material.name().equals("AIR") && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                    if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
                        for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
                            if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
                                final int dataValue = i;
                                selectMaterialPane.addButton(new Button(ItemHandler.getItem(material + ":" + dataValue, 1, false, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
                                    if (isNew) {
                                        if (dataValue != 0) {
                                            durationMaterialPane(player, itemMap, position, true, material.name() + ":" + dataValue);
                                        } else {
                                            durationMaterialPane(player, itemMap, position, true, material.name());
                                        }
                                    } else {
                                        List<String> mats = itemMap.getDynamicMaterials();
                                        if (dataValue != 0) {
                                            mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + material.name() + ":" + dataValue);
                                        } else {
                                            mats.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(mats.get(position))) + ">" + material.name());
                                        }
                                        itemMap.setDynamicMaterials(mats);
                                        modifyMaterialPane(player, itemMap, position);
                                    }
                                }));
                            }
                        }
                    } else {
                        selectMaterialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
                            if (isNew) {
                                durationMaterialPane(player, itemMap, position, true, material.name());
                            } else {
                                List<String> mats = itemMap.getDynamicMaterials();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durationMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
        Interface durationPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animateMaterialPane(player, itemMap)));
            durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION").with(Holder.INPUT_EXAMPLE, "110");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    List<String> mats = itemMap.getDynamicMaterials();
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
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    durationMaterialPane(player, itemMap, position, isNew, value);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
                    List<String> mats = itemMap.getDynamicMaterials();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyMaterialPane(final Player player, final ItemMap itemMap, final int position) {
        Interface modifyMaterialPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modifyMaterialPane.addButton(new Button(fillerPaneGItem), 3);
            modifyMaterialPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&a&l&nMaterial", "&7", "&7*Change the animated material type.", "&9&lMaterial: &a" + ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(position))),
                    event -> selectMaterialPane(player, itemMap, position, false)));
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicMaterials().get(position)));
            modifyMaterialPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" +
                    (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> durationMaterialPane(player, itemMap, position, false, ItemHandler.cutDelay(itemMap.getDynamicMaterials().get(position)))));
            modifyMaterialPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this animated material."), event -> {
                List<String> mats = itemMap.getDynamicMaterials();
                mats.remove(position);
                itemMap.setDynamicMaterials(mats);
                animateMaterialPane(player, itemMap);
            }));
            modifyMaterialPane.addButton(new Button(fillerPaneGItem), 3);
            modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> animateMaterialPane(player, itemMap)));
            modifyMaterialPane.addButton(new Button(fillerPaneBItem), 7);
            modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> animateMaterialPane(player, itemMap)));
        });
        modifyMaterialPane.open(player);
    }

//  ============================================== //
//           Animation Definition Menus            //
//  ============================================== //

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated names.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void animatedNamePane(final Player player, final ItemMap itemMap) {
        Interface animatedNamePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            animatedNamePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
                if (!itemMap.getDynamicNames().isEmpty()) {
                    itemMap.setAnimate(true);
                    itemMap.setCustomName(ItemHandler.cutDelay(itemMap.getDynamicNames().get(0)));
                }
                animationPane(player, itemMap);
            }));
            animatedNamePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Name Line", "&7", "&7*Add a new name line", "&7to be animated between."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NAME").with(Holder.INPUT_EXAMPLE, "&bUltimate Sword");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                durationNamePane(player, itemMap, 0, true, StringUtils.restoreColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NAME");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
            }));
            for (int i = 1; i <= itemMap.getDynamicNames().size(); i++) {
                final int k = i;
                final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicNames().get(k - 1)));
                animatedNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&fName " + k, "&7", "&7*Click modify this animated name.", "&9&lName: &a" + ItemHandler.cutDelay(itemMap.getDynamicNames().get(k - 1)),
                        "&9&lAnimation Ticks: &a" + (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> modifyNamePane(player, itemMap, k - 1)));
            }
        });
        animatedNamePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated name duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durationNamePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
        Interface durationPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedNamePane(player, itemMap)));
            durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION").with(Holder.INPUT_EXAMPLE, "110");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    List<String> names = itemMap.getDynamicNames();
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
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    durationNamePane(player, itemMap, position, isNew, value);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
                    List<String> names = itemMap.getDynamicNames();
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
     * This Pane is for modifying animated names.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyNamePane(final Player player, final ItemMap itemMap, final int position) {
        Interface modifyNamePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modifyNamePane.addButton(new Button(fillerPaneGItem), 3);
            modifyNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&a&l&nName", "&7", "&7*Change the animated name line.", "&9&lName: &a" + ItemHandler.cutDelay(itemMap.getDynamicNames().get(position))), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NAME").with(Holder.INPUT_EXAMPLE, "&bUltimate Sword");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                List<String> names = itemMap.getDynamicNames();
                names.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(names.get(position))) + ">" + StringUtils.restoreColor(event.getMessage()));
                itemMap.setDynamicNames(names)
                ;
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NAME");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                modifyNamePane(event.getPlayer(), itemMap, position);
            }));
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicNames().get(position)));
            modifyNamePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" +
                    (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> durationNamePane(player, itemMap, position, false, ItemHandler.cutDelay(itemMap.getDynamicNames().get(position)))));
            modifyNamePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this animated name."), event -> {
                List<String> names = itemMap.getDynamicNames();
                names.remove(position);
                itemMap.setDynamicNames(names);
                animatedNamePane(player, itemMap);
            }));
            modifyNamePane.addButton(new Button(fillerPaneGItem), 3);
            modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> animatedNamePane(player, itemMap)));
            modifyNamePane.addButton(new Button(fillerPaneBItem), 7);
            modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> animatedNamePane(player, itemMap)));
        });
        modifyNamePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated lore.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void animatedLorePane(final Player player, final ItemMap itemMap) {
        Interface animatedLorePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            animatedLorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
                if (!itemMap.getDynamicLores().isEmpty()) {
                    itemMap.setAnimate(true);
                    itemMap.setCustomLore(ItemHandler.cutDelay(itemMap.getDynamicLores().get(0)));
                }
                animationPane(player, itemMap);
            }));
            animatedLorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to be animated between."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATED LORE").with(Holder.INPUT_EXAMPLE, "&bThis is line 1, &cThis is line 2, &6This is line 3");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATED LORE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                durationLorePane(event.getPlayer(), itemMap, 0, true, StringUtils.restoreColor(event.getMessage()));
            }));
            for (int i = 1; i <= itemMap.getDynamicLores().size(); i++) {
                final int k = i;
                final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicLores().get(k - 1).get(0)));
                animatedLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, false, "&fLore " + k, "&7", "&7*Click modify this animated lore.", "&9&lLore: &a" + ItemHandler.cutDelay(itemMap.getDynamicLores().get(k - 1)),
                        "&9&lAnimation Ticks: &a" + (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> modifyLorePane(player, itemMap, k - 1)));
            }
        });
        animatedLorePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated lore duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durationLorePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
        Interface durationPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedLorePane(player, itemMap)));
            durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION").with(Holder.INPUT_EXAMPLE, "110");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    List<List<String>> lores = itemMap.getDynamicLores();
                    if (isNew) {
                        if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
                            List<String> loreCut = itemMap.getCustomLore();
                            loreCut.set(0, "<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + loreCut.get(0));
                            lores.add(loreCut);
                        }
                        lores.add(StringUtils.split("<delay:" + Integer.parseInt(ChatColor.stripColor(event.getMessage())) + ">" + value));
                    } else {
                        List<String> loreCut = ItemHandler.cutDelay(lores.get(position));
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
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    durationLorePane(player, itemMap, position, isNew, value);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
                    List<List<String>> lores = itemMap.getDynamicLores();
                    if (isNew) {
                        if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
                            List<String> loreCut = itemMap.getCustomLore();
                            loreCut.set(0, "<delay:" + k + ">" + loreCut.get(0));
                            lores.add(loreCut);
                        }
                        lores.add(StringUtils.split("<delay:" + k + ">" + value));
                    } else {
                        List<String> loreCut = ItemHandler.cutDelay(lores.get(position));
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyLorePane(final Player player, final ItemMap itemMap, final int position) {
        Interface modifyLorePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, false, "&a&l&nLore", "&7", "&7*Change the animated lore line.", "&9&lLore: &a" + ItemHandler.cutDelay(itemMap.getDynamicLores().get(position))), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATED LORE").with(Holder.INPUT_EXAMPLE, "&bThis is line 1, &cThis is line 2, &6This is line 3");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                List<List<String>> lores = itemMap.getDynamicLores();
                lores.set(position, StringUtils.split("<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(lores.get(position).get(0))) + ">" + StringUtils.restoreColor(event.getMessage())));
                itemMap.setDynamicLores(lores);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATED LORE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                modifyLorePane(event.getPlayer(), itemMap, position);
            }));
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicLores().get(position).get(0)));
            modifyLorePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" +
                    (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> durationLorePane(player, itemMap, position, false, "")));
            modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this animated lore."), event -> {
                List<List<String>> lores = itemMap.getDynamicLores();
                lores.remove(position);
                itemMap.setDynamicLores(lores);
                animatedLorePane(player, itemMap);
            }));
            modifyLorePane.addButton(new Button(fillerPaneGItem), 3);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> animatedLorePane(player, itemMap)));
            modifyLorePane.addButton(new Button(fillerPaneBItem), 7);
            modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> animatedLorePane(player, itemMap)));
        });
        modifyLorePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying animated skull items.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void animatedSkullPane(final Player player, final ItemMap itemMap, boolean owner) {
        Interface animatedSkullPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (owner) {
                animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
                    if (!itemMap.getDynamicOwners().isEmpty()) {
                        itemMap.setAnimate(true);
                        itemMap.setSkullTexture("");
                        itemMap.setDynamicTextures(new ArrayList<>());
                        itemMap.setSkull(ItemHandler.cutDelay(itemMap.getDynamicOwners().get(0)));
                    }
                    animationPane(player, itemMap);
                }));
                animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Skull Owner", "&7", "&7*Add a new skull owner", "&7to be animated between."), event -> {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER").with(Holder.INPUT_EXAMPLE, "RockinChaos");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }, event -> {
                    durationSkullPane(player, itemMap, 0, true, ChatColor.stripColor(event.getMessage()), true);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                }));
                for (int i = 1; i <= itemMap.getDynamicOwners().size(); i++) {
                    final int k = i;
                    final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicOwners().get(k - 1)));
                    animatedSkullPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, false, "&fSkull Owner " + k, "&7", "&7*Click modify this animated skull owner.", "&9&lSkull Owner: &a" +
                            ItemHandler.cutDelay(itemMap.getDynamicOwners().get(k - 1)), "&9&lAnimation Ticks: &a" + (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> modifySkullPane(player, itemMap, k - 1, true)));
                }
            } else {
                animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
                    if (!itemMap.getDynamicTextures().isEmpty()) {
                        itemMap.setAnimate(true);
                        itemMap.setSkull("");
                        itemMap.setDynamicOwners(new ArrayList<>());
                        itemMap.setSkullTexture(ItemHandler.cutDelay(itemMap.getDynamicTextures().get(0)));
                    }
                    animationPane(player, itemMap);
                }));
                animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&eNew Skull Texture", "&7", "&7*Add a new skull texture", "&7to be animated between."), event -> {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE").with(Holder.INPUT_EXAMPLE, "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }, event -> {
                    durationSkullPane(player, itemMap, 0, true, ChatColor.stripColor(event.getMessage()), false);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                }));
                for (int i = 1; i <= itemMap.getDynamicTextures().size(); i++) {
                    final int k = i;
                    final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(itemMap.getDynamicTextures().get(k - 1)));
                    animatedSkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, false, "&fSkull Texture " + k, "&7", "&7*Click modify this animated skull texture.", "&9&lSkull Texture: &a" +
                                    (ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1)).length() > 40 ? ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1)).substring(0, 40) : ItemHandler.cutDelay(itemMap.getDynamicTextures().get(k - 1))),
                            "&9&lAnimation Ticks: &a" + (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")), event -> modifySkullPane(player, itemMap, k - 1, false)));
                }
            }
        });
        animatedSkullPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the skull animation duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durationSkullPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value, boolean owner) {
        Interface durationPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> animatedSkullPane(player, itemMap, owner)));
            durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION").with(Holder.INPUT_EXAMPLE, "110");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "ANIMATION DURATION");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    List<String> skulls = itemMap.getDynamicOwners();
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
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    durationSkullPane(player, itemMap, position, isNew, value, owner);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
                    List<String> skulls = itemMap.getDynamicOwners();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifySkullPane(final Player player, final ItemMap itemMap, final int position, boolean owner) {
        Interface modifySkullPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modifySkullPane.addButton(new Button(fillerPaneGItem), 3);
            if (owner) {
                modifySkullPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, false, "&a&l&nSkull Owner", "&7", "&7*Change the animated skull owner.", "&9&lSkull Owner: &a" +
                        ItemHandler.cutDelay(itemMap.getDynamicOwners().get(position))), event -> {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER").with(Holder.INPUT_EXAMPLE, "RockinChaos");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }, event -> {
                    List<String> skulls = itemMap.getDynamicOwners();
                    skulls.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(skulls.get(position))) + ">" + ChatColor.stripColor(event.getMessage()));
                    itemMap.setDynamicOwners(skulls);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    modifySkullPane(event.getPlayer(), itemMap, position, true);
                }));
            } else {
                modifySkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, false, "&a&l&nSkull Texture", "&7", "&7*Change the animated skull texture.", "&9&lSkull Texture: &a" +
                        ItemHandler.cutDelay(itemMap.getDynamicTextures().get(position))), event -> {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE").with(Holder.INPUT_EXAMPLE, "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }, event -> {
                    List<String> skulls = itemMap.getDynamicTextures();
                    skulls.set(position, "<delay:" + StringUtils.returnInteger(ItemHandler.getDelayFormat(skulls.get(position))) + ">" + ChatColor.stripColor(event.getMessage()));
                    itemMap.setDynamicTextures(skulls);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    modifySkullPane(event.getPlayer(), itemMap, position, false);
                }));
            }
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))));
            modifySkullPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "CLOCK" : "347"), 1, false, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" +
                    (!StringUtils.nullCheck(String.valueOf(delay)).equals("NONE") ? String.valueOf(delay) : "20")),
                    event -> durationSkullPane(player, itemMap, position, false, ItemHandler.cutDelay((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))), owner)));
            modifySkullPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this animated skull " + (owner ? "owner." : "texture.")), event -> {
                List<String> skulls = itemMap.getDynamicOwners();
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
            modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> animatedSkullPane(player, itemMap, owner)));
            modifySkullPane.addButton(new Button(fillerPaneBItem), 7);
            modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> animatedSkullPane(player, itemMap, owner)));
        });
        modifySkullPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying item animations.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void animationPane(final Player player, final ItemMap itemMap) {
        Interface animationPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
                animationPane.addButton(new Button(fillerPaneGItem), 2);
            } else {
                animationPane.addButton(new Button(fillerPaneGItem), 3);
            }
            animationPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString(), 1, false, false, "&c&l&nMaterial", "&7", "&7*Add additional material types", "&7to have the item change between.", "&9&lAnimated Materials: &a" +
                    (!StringUtils.nullCheck(itemMap.getDynamicMaterials() + "").equals("NONE") ? "YES" : "NONE")), event -> animateMaterialPane(player, itemMap)));
            animationPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&a&l&nName", "&7", "&7*Add additional custom names", "&7to have the item change between.", "&9&lAnimated Names: &a" +
                    (!StringUtils.nullCheck(itemMap.getDynamicNames() + "").equals("NONE") ? "YES" : "NONE")), event -> animatedNamePane(player, itemMap)));
            animationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&b&l&nLore", "&7", "&7*Add additional custom lores", "&7to have the item change between.", "&9&lAnimated Lores: &a" +
                    (!StringUtils.nullCheck(itemMap.getDynamicLores() + "").equals("NONE") ? "YES" : "NONE")), event -> animatedLorePane(player, itemMap)));
            if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
                animationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, false, "&a&lSkull Owner", "&7", "&7*Add additional skull owners", "&7to have the item change between.", "&7", "&7You can only define skull owner",
                        "&7or skull texture, this will", "&7remove any skull textures.", "&9&lAnimated Owners: &a" + (!StringUtils.nullCheck(itemMap.getDynamicOwners() + "").equals("NONE") ? "YES" : "NONE")), event -> animatedSkullPane(player, itemMap, true)));
                animationPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, false, "&b&lSkull Texture", "&7", "&7*Add additional skull textures", "&7to have the item change between.", "&7", "&7You can only define skull texture",
                        "&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like&a minecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.",
                        "&9&lAnimated Textures: &a" + (!StringUtils.nullCheck(itemMap.getDynamicTextures() + "").equals("NONE") ? "YES" : "NONE")), event -> animatedSkullPane(player, itemMap, false)));
                animationPane.addButton(new Button(fillerPaneGItem), 2);
            } else {
                animationPane.addButton(new Button(fillerPaneGItem), 3);
            }
            animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            animationPane.addButton(new Button(fillerPaneBItem), 7);
            animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        animationPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the Limit Gamemodes.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void limitPane(final Player player, final ItemMap itemMap) {
        Interface limitPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            List<String> limitModes = new ArrayList<>();
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
            limitPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "FILLED_MAP" : "MAP"), 1, limitModes.contains("ADVENTURE"), false, "&a&l&nADVENTURE", "&7", "&7*Limits the item to ADVENTURE mode.", "&9&lENABLED: &a" +
                    (limitModes.contains("ADVENTURE") + "").toUpperCase()), event -> {
                if (limitModes.contains("ADVENTURE")) {
                    limitModes.remove("ADVENTURE");
                } else {
                    limitModes.add("ADVENTURE");
                }
                itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));
                limitPane(player, itemMap);
            }));
            limitPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, limitModes.contains("SURVIVAL"), false, "&b&l&nSURVIVAL", "&7", "&7*Limits the item to SURVIVAL mode.", "&9&lENABLED: &a" + (limitModes.contains("SURVIVAL") + "").toUpperCase()),
                    event -> {
                        if (limitModes.contains("SURVIVAL")) {
                            limitModes.remove("SURVIVAL");
                        } else {
                            limitModes.add("SURVIVAL");
                        }
                        itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));
                        limitPane(player, itemMap);
                    }));
            limitPane.addButton(new Button(ItemHandler.getItem(limitModes.contains("CREATIVE") ? (ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1") : "GOLDEN_APPLE", 1, false, false, "&6&l&nCREATIVE", "&7", "&7*Limits the item to CREATIVE mode.", "&9&lENABLED: &a" +
                    (limitModes.contains("CREATIVE") + "").toUpperCase()), event -> {
                if (limitModes.contains("CREATIVE")) {
                    limitModes.remove("CREATIVE");
                } else {
                    limitModes.add("CREATIVE");
                }
                itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));
                limitPane(player, itemMap);
            }));
            limitPane.addButton(new Button(fillerPaneGItem), 3);
            limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            limitPane.addButton(new Button(fillerPaneBItem), 7);
            limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        limitPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the item probability.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void probabilityPane(final Player player, final ItemMap itemMap) {
        Interface probabilityPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            probabilityPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            for (int i = 1; i < 100; i++) {
                final int k = i;
                probabilityPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), 1, false, false, "&9&lChance: &a&l" + k + "%", "&7", "&7*Click to set the", "&7probability of the item."), event -> {
                    itemMap.setProbability(k);
                    creatingPane(player, itemMap);
                }));
            }
        });
        probabilityPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the use-cooldown.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void usePane(final Player player, final ItemMap itemMap) {
        Interface usePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            usePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            usePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Usage", "&7", "&7*Click to set a custom usage cooldown", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "USAGE COOLDOWN").with(Holder.INPUT_EXAMPLE, "120");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setInteractCooldown(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "USAGE COOLDOWN");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                creatingPane(event.getPlayer(), itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                usePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7use-cooldown of the item."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void dropsPane(final Player player, final ItemMap itemMap) {
        Interface dropsPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            StringBuilder mobs = new StringBuilder();
            StringBuilder blocks = new StringBuilder();
            for (EntityType entity : itemMap.getMobsDrop().keySet()) {
                mobs.append(entity.name()).append(", ");
            }
            for (Material material : itemMap.getBlocksDrop().keySet()) {
                blocks.append(material.name()).append(", ");
            }
            dropsPane.addButton(new Button(fillerPaneBItem), 12);
            dropsPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ZOMBIE_SPAWN_EGG" : "383:54"), 1, false, false, "&b&lMobs Drop", "&7", "&7*Define mobs that are", "&7allowed to drop the item.", ((!StringUtils.isEmpty(mobs)) ? "&9&lMobs: &a" + mobs.substring(0, mobs.length() - 2) : "")), event -> mobsPane(player, itemMap)));
            dropsPane.addButton(new Button(fillerPaneBItem));
            dropsPane.addButton(new Button(ItemHandler.getItem("DIAMOND_ORE", 1, false, false, "&b&lBlocks Drop", "&7", "&7*Define blocks that are", "&7allowed to drop the item.", ((!StringUtils.isEmpty(blocks)) ? "&9&lBlocks: &a" + blocks.substring(0, blocks.length() - 2) : "")), event -> blocksPane(player, itemMap)));
            dropsPane.addButton(new Button(fillerPaneBItem), 3);
            dropsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            dropsPane.addButton(new Button(fillerPaneBItem), 7);
            dropsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        dropsPane.open(player);
    }

//  ===========================================================================================================================================================================================================================================================

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the mobs drop chances.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void mobsPane(final Player player, final ItemMap itemMap) {
        Interface dropsPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            dropsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the drop chances menu."), event -> dropsPane(player, itemMap)));
            for (EntityType entity : EntityType.values()) {
                if (itemMap.getMobsDrop().containsKey(entity) && entity.isAlive()) {
                    dropsPane.addButton(new Button(ItemHandler.getItem("EGG", 1, (itemMap.getMobsDrop().containsKey(entity)), false, "&f" + entity.name(), "&7", "&7*Click to add this as", "&7a banner pattern.",
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
            for (EntityType entity : EntityType.values()) {
                if (!itemMap.getMobsDrop().containsKey(entity) && entity.isAlive()) {
                    dropsPane.addButton(new Button(ItemHandler.getItem("EGG", 1, (itemMap.getMobsDrop().containsKey(entity)), false, "&f" + entity.name(), "&7", "&7*Click to add this as", "&7a banner pattern.",
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void blocksPane(final Player player, final ItemMap itemMap) {
        Interface blockPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            blockPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the drop chances menu."), event -> dropsPane(player, itemMap)));
            blockPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, false, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL").with(Holder.INPUT_EXAMPLE, "IRON_SWORD");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null) != Material.AIR) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "BUKKIT MATERIAL");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    chancePane(player, itemMap, null, ItemHandler.getMaterial(ChatColor.stripColor(event.getMessage()), null));
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noMaterial", player, placeHolders);
                    blocksPane(player, itemMap);
                }
            }));
            Inventory inventoryCheck = Bukkit.getServer().createInventory(null, 9, GUIName);
            for (Material material : Material.values()) {
                if (material.isBlock() && itemMap.getBlocksDrop().containsKey(material)) {
                    if (!material.name().contains("LEGACY") && !material.name().equals("AIR") && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                        if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
                            for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
                                if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
                                    blockPane.addButton(new Button(ItemHandler.getItem(material + ":" + i, 1, (itemMap.getBlocksDrop().containsKey(material)), false, "", "&7", "&7*Click to set the material.",
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
                            blockPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, (itemMap.getBlocksDrop().containsKey(material)), false, "", "&7", "&7*Click to set the material.",
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
            for (Material material : Material.values()) {
                if (material.isBlock() && !itemMap.getBlocksDrop().containsKey(material)) {
                    if (!material.name().contains("LEGACY") && !material.name().equals("AIR") && safeMaterial(ItemHandler.getItem(material.toString(), 1, false, false, "", ""), inventoryCheck)) {
                        if (!ServerUtils.hasSpecificUpdate("1_13") && LegacyAPI.getDataValue(material) != 0) {
                            for (int i = 0; i <= LegacyAPI.getDataValue(material); i++) {
                                if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
                                    blockPane.addButton(new Button(ItemHandler.getItem(material + ":" + i, 1, (itemMap.getBlocksDrop().containsKey(material)), false, "", "&7", "&7*Click to set the material.",
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
                            blockPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, (itemMap.getBlocksDrop().containsKey(material)), false, "", "&7", "&7*Click to set the material.",
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param entity  - The Entity selected.
     */
    private static void chancePane(final Player player, final ItemMap itemMap, final EntityType entity, final Material material) {
        Interface chancePane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (entity != null) {
                chancePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the mobs drop menu."), event -> mobsPane(player, itemMap)));
            } else {
                chancePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the blocks drop menu."), event -> blocksPane(player, itemMap)));
            }
            chancePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:7"), 1, false, false, "&e&lCustom Drop Chance", "&7", "&7*Click to set a custom drop chance", "&7value for the item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DROP CHANCE").with(Holder.INPUT_EXAMPLE, "0.001");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
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
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DROP CHANCE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                if (entity != null) {
                    mobsPane(event.getPlayer(), itemMap);
                } else {
                    blocksPane(event.getPlayer(), itemMap);
                }
            }));
            for (double i = 0.01; i < 1; i += 0.01) {
                final double k = Double.parseDouble(new DecimalFormat("#.##")
                        .format(
                                Double.parseDouble(Double.toString(i).replace(",", ".")))
                        .replace(",", "."));
                chancePane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PURPLE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:10"), 1, false, false, "&9&lCost: &a$&l" + k, "&7", "&7*Click to set the", "&7drop chance of the item."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void conditionsPane(final Player player, final ItemMap itemMap) {
        Interface conditionsPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            conditionsPane.addButton(new Button(fillerPaneBItem), 3);
            conditionsPane.addButton(new Button(ItemHandler.getItem("CACTUS", 1, false, false, "&b&l&nDisposable&b&l Conditions", "&7", "&7*Condition(s) that must be met", "&7in order for the disposable", "&7itemflag to function.",
                    "&7", "&c&l&nNOTE:&7 The disposable itemflag", "&7must be defined for this", "&7condition to function.",
                    "&7", "&9&lENABLED: &a" + String.valueOf((!StringUtils.nullCheck(itemMap.getDisposableConditions() + "").equals("NONE"))).toUpperCase()), event -> disposableCPane(player, itemMap)));
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&b&l&nTrigger&b&l Conditions", "&7", "&7*Condition(s) that must be met", "&7in order to to receive the",
                    "&7item when performing a trigger.", "&7", "&9&lENABLED: &a" + String.valueOf((!StringUtils.nullCheck(itemMap.getTriggerConditions() + "").equals("NONE"))).toUpperCase()), event -> triggerCPane(player, itemMap)));
            conditionsPane.addButton(new Button(fillerPaneBItem), 3);
            conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            conditionsPane.addButton(new Button(fillerPaneBItem), 7);
            conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        conditionsPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the command action conditions.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void commandActionPane(final Player player, final ItemMap itemMap) {
        Interface commandPane = new Interface(false, 5, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_ALL)));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_ALL)));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, false, "&e&lPhysical", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.PHYSICAL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.PHYSICAL)));
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, true, "&e&lOn-Equip", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_EQUIP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_EQUIP)));
            commandPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, true, "&e&lUn-Equip", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.UN_EQUIP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.UN_EQUIP)));
            commandPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, false, "&e&lOn-Hold", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_HOLD.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_HOLD)));
            commandPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, false, "&e&lOn-Fire", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_FIRE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_FIRE)));
            commandPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&lOn-Drop", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_DROP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_DROP)));
            commandPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, true, "&e&lOn-Consume", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_CONSUME.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_CONSUME)));
            commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&e&lOn-Receive", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_RECEIVE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_RECEIVE)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SKELETON_SKULL" : "397"), 1, false, false, "&e&lOn-Death", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_DEATH.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_DEATH)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PLAYER_HEAD" : "397:3"), 1, false, false, "&e&lOn-Kill", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_KILL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_KILL)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLASS" : "20"), 1, false, false, "&e&lInteract-Air", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS" : "95:3"), 1, false, false, "&e&lInteract-Air-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_LEFT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS" : "95:6"), 1, false, false, "&e&lInteract-Air-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_RIGHT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2:4"), 1, false, false, "&e&lInteract-Block", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE" : "1"), 1, false, false, "&e&lInteract-Block-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_LEFT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE" : "4"), 1, false, false, "&e&lInteract-Block-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_RIGHT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_DOOR" : "330"), 1, false, false, "&e&lInteract-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_LEFT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_LEFT_ALL)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INTERACT_RIGHT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INTERACT_RIGHT_ALL)));
            commandPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SWAP_CURSOR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_SWAP_CURSOR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, false, "&e&lInventory-Middle", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_MIDDLE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_MIDDLE)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&e&lInventory-Creative", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_CREATIVE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_CREATIVE)));
            commandPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, false, "&e&lInventory-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_LEFT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_LEFT)));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_RIGHT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_RIGHT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE_SLAB" : "44"), 2, false, false, "&e&lInventory-Shift-Left", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SHIFT_LEFT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_SHIFT_LEFT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE_SLAB" : "44:3"), 2, false, false, "&e&lInventory-Shift-Right", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.INVENTORY_SHIFT_RIGHT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.INVENTORY_SHIFT_RIGHT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false, "&e&lOn-Join", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_JOIN.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_JOIN)));
            commandPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false, "&e&lOn-Damage", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_DAMAGE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_DAMAGE)));
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, true, "&e&lOn-Hit", "&7", "&7*Condition(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandConditions().get(Action.ON_HIT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandCPane(player, itemMap, Action.ON_HIT)));
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> commandPane(player, itemMap)));
            commandPane.addButton(new Button(fillerPaneBItem), 7);
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> commandPane(player, itemMap)));
        });
        commandPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the command action permissions.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void commandPermissionPane(final Player player, final ItemMap itemMap) {
        Interface commandPane = new Interface(false, 5, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_ALL)));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_ALL)));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, false, "&e&lPhysical", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.PHYSICAL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.PHYSICAL)));
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, true, "&e&lOn-Equip", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_EQUIP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_EQUIP)));
            commandPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, true, "&e&lUn-Equip", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.UN_EQUIP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.UN_EQUIP)));
            commandPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, false, "&e&lOn-Hold", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_HOLD.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_HOLD)));
            commandPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, false, "&e&lOn-Fire", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_FIRE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_FIRE)));
            commandPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&lOn-Drop", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_DROP.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_DROP)));
            commandPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, true, "&e&lOn-Consume", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_CONSUME.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_CONSUME)));
            commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&e&lOn-Receive", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_RECEIVE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_RECEIVE)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SKELETON_SKULL" : "397"), 1, false, false, "&e&lOn-Death", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_DEATH.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_DEATH)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PLAYER_HEAD" : "397:3"), 1, false, false, "&e&lOn-Kill", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_KILL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_KILL)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLASS" : "20"), 1, false, false, "&e&lInteract-Air", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS" : "95:3"), 1, false, false, "&e&lInteract-Air-Left", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_LEFT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_LEFT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS" : "95:6"), 1, false, false, "&e&lInteract-Air-Right", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_RIGHT_AIR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_RIGHT_AIR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2:4"), 1, false, false, "&e&lInteract-Block", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE" : "1"), 1, false, false, "&e&lInteract-Block-Left", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_LEFT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_LEFT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE" : "4"), 1, false, false, "&e&lInteract-Block-Right", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_RIGHT_BLOCK.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_RIGHT_BLOCK)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_DOOR" : "330"), 1, false, false, "&e&lInteract-Left", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_LEFT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_LEFT_ALL)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract-Right", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INTERACT_RIGHT_ALL.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INTERACT_RIGHT_ALL)));
            commandPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_SWAP_CURSOR.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_SWAP_CURSOR)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, false, "&e&lInventory-Middle", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_MIDDLE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_MIDDLE)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&e&lInventory-Creative", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_CREATIVE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_CREATIVE)));
            commandPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, false, "&e&lInventory-Left", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_LEFT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_LEFT)));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory-Right", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_RIGHT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_RIGHT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE_SLAB" : "44"), 2, false, false, "&e&lInventory-Shift-Left", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_SHIFT_LEFT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_SHIFT_LEFT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE_SLAB" : "44:3"), 2, false, false, "&e&lInventory-Shift-Right", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.INVENTORY_SHIFT_RIGHT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.INVENTORY_SHIFT_RIGHT)));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false, "&e&lOn-Join", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_JOIN.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_JOIN)));
            commandPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false, "&e&lOn-Damage", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_DAMAGE.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_DAMAGE)));
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, true, "&e&lOn-Hit", "&7", "&7*Permissions(s) that must be met", "&7in order to execute item commands.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandPermissions().get(Action.ON_HIT.config()) + "").equals("NONE") ? "YES" : "NO")), event -> commandPPane(player, itemMap, Action.ON_HIT)));
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            commandPane.addButton(new Button(fillerPaneBItem), 7);
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
        });
        commandPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the command action fail messages.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void commandFailPane(final Player player, final ItemMap itemMap) {
        Interface commandPane = new Interface(false, 5, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_ALL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_ALL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_ALL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_ALL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagesMap = itemMap.getCommandMessages();
                messagesMap.put(Action.INTERACT_ALL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagesMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_ALL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_ALL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_ALL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_ALL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_ALL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(fillerPaneGItem));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PISTON" : "PISTON_BASE"), 1, false, false, "&e&lPhysical", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.PHYSICAL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.PHYSICAL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.PHYSICAL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.PHYSICAL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.PHYSICAL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(fillerPaneGItem), 2);
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, false, true, "&e&lOn-Equip", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_EQUIP.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_EQUIP.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_EQUIP.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                        messagesMap.remove(Action.ON_EQUIP.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_EQUIP.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("IRON_HELMET", 1, false, true, "&e&lUn-Equip", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.UN_EQUIP.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.UN_EQUIP.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.UN_EQUIP.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.UN_EQUIP.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.UN_EQUIP.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("TORCH", 1, false, false, "&e&lOn-Hold", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_HOLD.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_HOLD.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_HOLD.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_HOLD.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_HOLD.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, false, "&e&lOn-Fire", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_FIRE.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_FIRE.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_FIRE.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_FIRE.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_FIRE.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, false, "&e&lOn-Drop", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_DROP.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_DROP.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_DROP.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_DROP.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_DROP.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, true, "&e&lOn-Consume", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_CONSUME.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_CONSUME.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_CONSUME.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_CONSUME.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_CONSUME.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, false, "&e&lOn-Receive", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_RECEIVE.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_RECEIVE.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_RECEIVE.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_RECEIVE.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_RECEIVE.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SKELETON_SKULL" : "397"), 1, false, false, "&e&lOn-Death", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_DEATH.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_DEATH.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_DEATH.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_DEATH.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_DEATH.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PLAYER_HEAD" : "397:3"), 1, false, false, "&e&lOn-Kill", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_KILL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_KILL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_KILL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_KILL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_KILL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GLASS" : "20"), 1, false, false, "&e&lInteract-Air", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_AIR.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_AIR.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_AIR.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_AIR.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_AIR.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIGHT_BLUE_STAINED_GLASS" : "95:3"), 1, false, false, "&e&lInteract-Air-Left", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_LEFT_AIR.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_LEFT_AIR.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_LEFT_AIR.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_LEFT_AIR.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_LEFT_AIR.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS" : "95:6"), 1, false, false, "&e&lInteract-Air-Right", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_AIR.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_AIR.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_AIR.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_RIGHT_AIR.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_RIGHT_AIR.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2:4"), 1, false, false, "&e&lInteract-Block", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_BLOCK.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_BLOCK.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_BLOCK.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_BLOCK.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_BLOCK.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE" : "1"), 1, false, false, "&e&lInteract-Block-Left", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_LEFT_BLOCK.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_LEFT_BLOCK.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_LEFT_BLOCK.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_LEFT_BLOCK.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_LEFT_BLOCK.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE" : "4"), 1, false, false, "&e&lInteract-Block-Right", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_BLOCK.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_BLOCK.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_BLOCK.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_RIGHT_BLOCK.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_RIGHT_BLOCK.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "IRON_DOOR" : "330"), 1, false, false, "&e&lInteract-Left", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_LEFT_ALL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_LEFT_ALL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_LEFT_ALL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_LEFT_ALL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_LEFT_ALL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_DOOR" : "324"), 1, false, false, "&e&lInteract-Right", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_ALL.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_ALL.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INTERACT_RIGHT_ALL.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INTERACT_RIGHT_ALL.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INTERACT_RIGHT_ALL.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&e&lInventory-Swap-Cursor", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_SWAP_CURSOR.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_SWAP_CURSOR.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_SWAP_CURSOR.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_SWAP_CURSOR.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_SWAP_CURSOR.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "SNOWBALL" : "SNOW_BALL"), 8, false, false, "&e&lInventory-Middle", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_MIDDLE.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_MIDDLE.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_MIDDLE.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_MIDDLE.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_MIDDLE.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&e&lInventory-Creative", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_CREATIVE.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_CREATIVE.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_CREATIVE.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_CREATIVE.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_CREATIVE.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("ENDER_CHEST", 1, false, false, "&e&lInventory-Left", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_LEFT.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_LEFT.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_LEFT.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_LEFT.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_LEFT.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&e&lInventory-Right", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_RIGHT.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_RIGHT.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_RIGHT.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_RIGHT.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_RIGHT.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "STONE_SLAB" : "44"), 2, false, false, "&e&lInventory-Shift-Left", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_LEFT.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_LEFT.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_LEFT.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_SHIFT_LEFT.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_SHIFT_LEFT.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COBBLESTONE_SLAB" : "44:3"), 2, false, false, "&e&lInventory-Shift-Right", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_RIGHT.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_RIGHT.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.INVENTORY_SHIFT_RIGHT.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.INVENTORY_SHIFT_RIGHT.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.INVENTORY_SHIFT_RIGHT.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_SIGN" : "323"), 1, false, false, "&e&lOn-Join", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_JOIN.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_JOIN.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_JOIN.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_JOIN.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_JOIN.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("LAVA_BUCKET", 1, false, false, "&e&lOn-Damage", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_DAMAGE.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_DAMAGE.config()) : "NONE")),
                    event -> {
                        if (itemMap.getCommandMessages().get(Action.ON_DAMAGE.config()) == null) {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        } else {
                            final Map<String, String> messagesMap = itemMap.getCommandMessages();
                            messagesMap.remove(Action.ON_DAMAGE.config());
                            itemMap.setCommandMessages(messagesMap);
                            commandFailPane(player, itemMap);
                        }
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_DAMAGE.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, true, "&e&lOn-Hit", "&7", "&7*Message that will be sent when", "&7either the permission or conditions", "&7requirements are not met.",
                    "&7", "&9&lENABLED: &a" + (!StringUtils.nullCheck(itemMap.getCommandMessages().get(Action.ON_HIT.config())).equals("NONE") ? itemMap.getCommandMessages().get(Action.ON_HIT.config()) : "NONE")),
            event -> {
                if (itemMap.getCommandMessages().get(Action.ON_HIT.config()) == null) {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "You do not meet the requirements to execute this command.");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                } else {
                    final Map<String, String> messagesMap = itemMap.getCommandMessages();
                    messagesMap.remove(Action.ON_HIT.config());
                    itemMap.setCommandMessages(messagesMap);
                    commandFailPane(player, itemMap);
                }
            }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final Map<String, String> messagessMap = itemMap.getCommandMessages();
                messagessMap.put(Action.ON_HIT.config(), ChatColor.stripColor(event.getMessage()));
                itemMap.setCommandMessages(messagessMap);
                commandFailPane(player, itemMap);
            }));
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
            commandPane.addButton(new Button(fillerPaneBItem), 7);
            commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> commandPane(player, itemMap)));
        });
        commandPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the commands permissions.
     *
     * @param player        - The Player to have the Pane opened.
     * @param itemMap       - The ItemMap currently being modified.
     * @param commandAction - The command action being referenced.
     */
    private static void commandPPane(final Player player, final ItemMap itemMap, final Action commandAction) {
        Interface permissionsPane = new Interface(true, 2, exitButton, GUIName, player);
        permissionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command actions menu."), event -> commandPermissionPane(player, itemMap)));
        SchedulerUtils.runAsync(() -> {
            permissionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&b&lAdd Permission", "&7", "&7*Permissions(s) that must be met",
                    "&7in order to execute the", "&7" + commandAction.config().replace("-", " ").replace(".", "") + " item commands.", "&7", "&c&lNote: &7You can use a &c&l! &7symbol", "&7to invert the requirement", "&7such as !fish.cakes"),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PERMISSION").with(Holder.INPUT_EXAMPLE, "fish.cakes");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PERMISSION");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                final List<String> permissions = itemMap.getCommandPermissions().get(commandAction.config()) != null ? itemMap.getCommandPermissions().get(commandAction.config()) : new ArrayList<>();
                permissions.add(ChatColor.stripColor(event.getMessage()));
                final Map<String, List<String>> permissionsMap = itemMap.getCommandPermissions();
                permissionsMap.put(commandAction.config(), permissions);
                itemMap.setCommandPermissions(permissionsMap);
                commandPPane(player, itemMap, commandAction);
            }));
            if (itemMap.getCommandPermissions().get(commandAction.config()) != null) {
                for (String permission : itemMap.getCommandPermissions().get(commandAction.config())) {
                    permissionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&f" + permission, "&7", "&7*Click to remove this permission."),
                            event -> {
                                final Map<String, List<String>> commands = itemMap.getCommandPermissions();
                                final List<String> permissions = commands.get(commandAction.config());
                                permissions.remove(permission);
                                if (!permissions.isEmpty()) {
                                    commands.put(commandAction.config(), permissions);
                                } else {
                                    commands.remove(commandAction.config());
                                }
                                itemMap.setCommandPermissions(commands);
                                commandPPane(player, itemMap, commandAction);
                            }));
                }
            }
        });
        permissionsPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the commands conditions.
     *
     * @param player        - The Player to have the Pane opened.
     * @param itemMap       - The ItemMap currently being modified.
     * @param commandAction - The command action being referenced.
     */
    private static void commandCPane(final Player player, final ItemMap itemMap, final Action commandAction) {
        Interface conditionsPane = new Interface(true, 2, exitButton, GUIName, player);
        conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the command actions menu."), event -> commandActionPane(player, itemMap)));
        SchedulerUtils.runAsync(() -> {
            conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met",
                    "&7in order to execute the", "&7" + commandAction.config().replace("-", " ").replace(".", "") + " item commands."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE").with(Holder.INPUT_EXAMPLE, "100");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                addConditionPane(event.getPlayer(), itemMap, commandAction, commandAction.config(), ChatColor.stripColor(event.getMessage()));
            }));
            if (itemMap.getCommandConditions().get(commandAction.config()) != null) {
                for (String condition : itemMap.getCommandConditions().get(commandAction.config())) {
                    conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&f" + condition, "&7", "&7*Click to remove this condition."),
                            event -> {
                                Map<String, List<String>> commands = itemMap.getCommandConditions();
                                List<String> conditions = commands.get(commandAction.config());
                                conditions.remove(condition);
                                if (!conditions.isEmpty()) {
                                    commands.put(commandAction.config(), conditions);
                                } else {
                                    commands.remove(commandAction.config());
                                }
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void disposableCPane(final Player player, final ItemMap itemMap) {
        Interface conditionsPane = new Interface(true, 2, exitButton, GUIName, player);
        conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
        SchedulerUtils.runAsync(() -> {
            conditionsPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (!StringUtils.nullCheck(itemMap.getDisposableMessage()).equals("NONE")), false, "&c&lDisposable Fail Message", "&7", "&7*An optional message to be", "&7sent when the player does not", "&7meet the disposable conditions.",
                    "&7", "&9&lMESSAGE: &a" + (!StringUtils.nullCheck(itemMap.getDisposableMessage()).equals("NONE") ? itemMap.getDisposableMessage() : "NONE")),
                    event -> {
                        if (!StringUtils.nullCheck(itemMap.getDisposableMessage()).equals("NONE")) {
                            itemMap.setDisposableMessage(null);
                            disposableCPane(player, itemMap);
                        } else {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DISPOSABLE FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "&cYou do not meet the conditions to dispose of this item.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        }
                    }, event -> {
                itemMap.setDisposableMessage(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "DISPOSABLE FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                disposableCPane(event.getPlayer(), itemMap);
            }));
            conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met", "&7in order to dispose of the item."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE").with(Holder.INPUT_EXAMPLE, "100");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                addConditionPane(event.getPlayer(), itemMap, null, "disposable-conditions", ChatColor.stripColor(event.getMessage()));
            }));
            for (String condition : itemMap.getDisposableConditions()) {
                conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&f" + condition, "&7", "&7*Click to remove this condition."),
                        event -> {
                            List<String> conditions = itemMap.getDisposableConditions();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void triggerCPane(final Player player, final ItemMap itemMap) {
        Interface conditionsPane = new Interface(true, 2, exitButton, GUIName, player);
        conditionsPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item conditions menu."), event -> conditionsPane(player, itemMap)));
        SchedulerUtils.runAsync(() -> {
            conditionsPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (!StringUtils.nullCheck(itemMap.getTriggerMessage()).equals("NONE")), false, "&c&lTrigger Fail Message", "&7", "&7*An optional message to be", "&7sent when the player does not", "&7meet the trigger conditions.",
                    "&7", "&9&lMESSAGE: &a" + (!StringUtils.nullCheck(itemMap.getTriggerMessage()).equals("NONE") ? itemMap.getTriggerMessage() : "NONE")),
                    event -> {
                        if (!StringUtils.nullCheck(itemMap.getTriggerMessage()).equals("NONE")) {
                            itemMap.setTriggerMessage(null);
                            triggerCPane(player, itemMap);
                        } else {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TRIGGER FAIL MESSAGE").with(Holder.INPUT_EXAMPLE, "&cYou do not meet the conditions to receive this item.");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        }
                    }, event -> {
                itemMap.setTriggerMessage(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TRIGGER FAIL MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                triggerCPane(event.getPlayer(), itemMap);
            }));
            conditionsPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&b&lAdd Condition", "&7", "&7*Condition(s) that must be met", "&7in order to receive the item."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE").with(Holder.INPUT_EXAMPLE, "100");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIRST VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                addConditionPane(event.getPlayer(), itemMap, null, "trigger-conditions", ChatColor.stripColor(event.getMessage()));
            }));
            for (String condition : itemMap.getTriggerConditions()) {
                conditionsPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&f" + condition, "&7", "&7*Click to remove this condition."),
                        event -> {
                            List<String> conditions = itemMap.getTriggerConditions();
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
     * @param player        - The Player to have the Pane opened.
     * @param itemMap       - The ItemMap currently being modified.
     * @param commandAction - The command action performed.
     * @param condition     - The condition currently being modified.
     * @param value         - The condition value.
     */
    private static void addConditionPane(final Player player, final ItemMap itemMap, final Action commandAction, final String condition, final String value) {
        Interface conditionsPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, false, false, "&b&lEQUAL", "&7", "&7*The first value must be", "&7EQUAL to the second value", "&7for the condition to be met."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE").with(Holder.INPUT_EXAMPLE, "400");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                List<String> conditions = (condition.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition) != null ? itemMap.getCommandConditions().get(condition) : new ArrayList<>());
                conditions.add(value + ":" + "EQUAL" + ":" + ChatColor.stripColor(event.getMessage()));
                if (condition.equalsIgnoreCase("disposable-conditions")) {
                    itemMap.setDisposableConditions(conditions);
                    disposableCPane(player, itemMap);
                } else if (condition.equalsIgnoreCase("trigger-conditions")) {
                    itemMap.setTriggerConditions(conditions);
                    triggerCPane(player, itemMap);
                } else {
                    Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
                    conditions2.put(condition.replace("-conditions", ""), conditions);
                    itemMap.setCommandConditions(conditions2);
                    commandCPane(player, itemMap, commandAction);
                }
            }));
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_FENCE" : "85"), 1, false, false, "&b&lNOTEQUAL", "&7", "&7*The first value must be", "&7NOTEQUAL to the second value", "&7for the condition to be met."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE").with(Holder.INPUT_EXAMPLE, "400");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                List<String> conditions = (condition.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition) != null ? itemMap.getCommandConditions().get(condition) : new ArrayList<>());
                conditions.add(value + ":" + "NOTEQUAL" + ":" + ChatColor.stripColor(event.getMessage()));
                if (condition.equalsIgnoreCase("disposable-conditions")) {
                    itemMap.setDisposableConditions(conditions);
                    disposableCPane(player, itemMap);
                } else if (condition.equalsIgnoreCase("trigger-conditions")) {
                    itemMap.setTriggerConditions(conditions);
                    triggerCPane(player, itemMap);
                } else {
                    Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
                    conditions2.put(condition.replace("-conditions", ""), conditions);
                    itemMap.setCommandConditions(conditions2);
                    commandCPane(player, itemMap, commandAction);
                }
            }));
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_STAIRS" : "53"), 1, false, false, "&b&lOVER", "&7", "&7*The first value must be", "&7OVER the second value", "&7for the condition to be met.", "&7", "&c&l&nNOTE:&7 This only works if both", "&7values referenced are integers."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE").with(Holder.INPUT_EXAMPLE, "400");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                List<String> conditions = (condition.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition) != null ? itemMap.getCommandConditions().get(condition) : new ArrayList<>());
                conditions.add(value + ":" + "OVER" + ":" + ChatColor.stripColor(event.getMessage()));
                if (condition.equalsIgnoreCase("disposable-conditions")) {
                    itemMap.setDisposableConditions(conditions);
                    disposableCPane(player, itemMap);
                } else if (condition.equalsIgnoreCase("trigger-conditions")) {
                    itemMap.setTriggerConditions(conditions);
                    triggerCPane(player, itemMap);
                } else {
                    Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
                    conditions2.put(condition.replace("-conditions", ""), conditions);
                    itemMap.setCommandConditions(conditions2);
                    commandCPane(player, itemMap, commandAction);
                }
            }));
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "OAK_BOAT" : "333"), 1, false, false, "&b&lUNDER", "&7", "&7*The first value must be", "&7UNDER to the second value", "&7for the condition to be met.", "&7", "&c&l&nNOTE:&7 This only works if both", "&7values referenced are integers."),
                    event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE").with(Holder.INPUT_EXAMPLE, "400");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SECOND VALUE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                List<String> conditions = (condition.equalsIgnoreCase("disposable-conditions") ? itemMap.getDisposableConditions() : condition.equalsIgnoreCase("trigger-conditions") ? itemMap.getTriggerConditions() : itemMap.getCommandConditions().get(condition) != null ? itemMap.getCommandConditions().get(condition) : new ArrayList<>());
                conditions.add(value + ":" + "UNDER" + ":" + ChatColor.stripColor(event.getMessage()));
                if (condition.equalsIgnoreCase("disposable-conditions")) {
                    itemMap.setDisposableConditions(conditions);
                    disposableCPane(player, itemMap);
                } else if (condition.equalsIgnoreCase("trigger-conditions")) {
                    itemMap.setTriggerConditions(conditions);
                    triggerCPane(player, itemMap);
                } else {
                    Map<String, List<String>> conditions2 = itemMap.getCommandConditions();
                    conditions2.put(condition.replace("-conditions", ""), conditions);
                    itemMap.setCommandConditions(conditions2);
                    commandCPane(player, itemMap, commandAction);
                }
            }));
            conditionsPane.addButton(new Button(fillerPaneBItem));
            conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the " + condition.replace("-", " ").replace(".", "") + " condition menu."),
                    event -> {
                        if (condition.equalsIgnoreCase("disposable-conditions")) {
                            disposableCPane(player, itemMap);
                        } else if (condition.equalsIgnoreCase("trigger-conditions")) {
                            triggerCPane(player, itemMap);
                        } else {
                            commandCPane(player, itemMap, commandAction);
                        }
                    }));
            conditionsPane.addButton(new Button(fillerPaneBItem), 7);
            conditionsPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the " + condition.replace("-", " ").replace(".", "") + " condition menu."),
                    event -> {
                        if (condition.equalsIgnoreCase("disposable-conditions")) {
                            disposableCPane(player, itemMap);
                        } else if (condition.equalsIgnoreCase("trigger-conditions")) {
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
     * This Pane is for modifying a list of commands.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param stage   - The stage to be matched.
     */
    private static void altCommandPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface commandListPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage == 4) {
                commandListPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> activeCommands(player)));
            } else {
                commandListPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the click type menu."), event -> togglePane(player, itemMap)));
            }
            commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&lNew Line", "&7", "&7*Add a new command to be executed."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 4 ? "ACTIVE COMMAND" : "TOGGLE COMMAND")).with(Holder.INPUT_EXAMPLE, (stage == 4 ? "gamemode creative %player%" : "pvp"));
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (stage == 4) {
                    final List<String> commands = ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands");
                    commands.add(ChatColor.stripColor(event.getMessage()));
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands", commands);
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> altCommandPane(player, itemMap, stage));
                } else {
                    final List<String> toggleCommands = itemMap.getToggleCommands();
                    toggleCommands.add(ChatColor.stripColor(event.getMessage()));
                    itemMap.setToggleCommands(toggleCommands);
                }
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 4 ? "ACTIVE COMMAND" : "TOGGLE COMMAND"));
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                altCommandPane(event.getPlayer(), itemMap, stage);
            }));
            final List<String> commandsList = (stage == 4 ? ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands") : itemMap.getToggleCommands());
            for (String command : commandsList) {
                commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&f" + command, "&7", "&7*Click to&l modify &7this command."), event -> modifyAltCommandsPane(player, itemMap, command, stage)));
            }
        });
        commandListPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying a command.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     * @param command - The command being modified.
     * @param stage   - The stage to be matched.
     */
    private static void modifyAltCommandsPane(final Player player, final ItemMap itemMap, final String command, final int stage) {
        Interface modPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            modPane.addButton(new Button(fillerPaneGItem), 4);
            modPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&f" + command, "&7", "&7*You are modifying this command.")));
            modPane.addButton(new Button(fillerPaneGItem), 7);
            modPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, false, "&fModify", "&7", "&7*Sets the command to", "&7another text entry."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 4 ? "MODIFIED ACTIVE COMMAND" : "MODIFIED TOGGLE COMMAND")).with(Holder.INPUT_EXAMPLE, (stage == 4 ? "gamemode survival %player%" : "pvp on"));
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (stage == 4) {
                    final List<String> commands = ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands");
                    commands.remove(command);
                    commands.add(ChatColor.stripColor(event.getMessage()));
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands", commands);
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> altCommandPane(player, itemMap, stage));
                } else {
                    final List<String> toggleCommands = itemMap.getToggleCommands();
                    toggleCommands.remove(command);
                    toggleCommands.add(ChatColor.stripColor(event.getMessage()));
                    itemMap.setToggleCommands(toggleCommands);
                }
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, (stage == 4 ? "MODIFIED ACTIVE COMMAND" : "MODIFIED TOGGLE COMMAND"));
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                altCommandPane(player, itemMap, stage);
            }));
            modPane.addButton(new Button(fillerPaneGItem));
            modPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&fDelete", "&7", "&7*Click to&c delete &7this command."), event -> {
                if (stage == 4) {
                    final List<String> commands = ItemJoin.getCore().getConfig("config.yml").getStringList("Active-Commands.commands");
                    commands.remove(command);
                    File fileFolder = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "config.yml");
                    FileConfiguration dataFile = YamlConfiguration.loadConfiguration(fileFolder);
                    dataFile.set("Active-Commands.commands", commands);
                    ItemJoin.getCore().getConfiguration().saveFile(dataFile, fileFolder, "config.yml");
                    PluginData.getInfo().softReload();
                    SchedulerUtils.runLater(2L, () -> altCommandPane(player, itemMap, stage));
                } else {
                    final List<String> toggleCommands = itemMap.getToggleCommands();
                    toggleCommands.remove(command);
                    itemMap.setToggleCommands(toggleCommands);
                }
                altCommandPane(player, itemMap, stage);
            }));
            modPane.addButton(new Button(fillerPaneGItem), 3);
            if (stage == 4) {
                modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> altCommandPane(player, itemMap, stage)));
            } else {
                modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the toggle commands menu."), event -> altCommandPane(player, itemMap, stage)));
            }
            modPane.addButton(new Button(fillerPaneBItem), 7);
            if (stage == 4) {
                modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the active commands menu."), event -> altCommandPane(player, itemMap, stage)));
            } else {
                modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the toggle commands menu."), event -> altCommandPane(player, itemMap, stage)));
            }
        });
        modPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the custom toggle commands.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void togglePane(final Player player, final ItemMap itemMap) {
        Interface togglePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            togglePane.addButton(new Button(fillerPaneBItem), 2);
            togglePane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, false, "&e&lCommands", "&7", "&7*Define specific commands which", "&7players can use to enable or disable", "&7the custom item upon execution."), event -> altCommandPane(player, itemMap, 0)));
            togglePane.addButton(new Button(fillerPaneBItem));
            togglePane.addButton(new Button(ItemHandler.getItem("PAPER", 1, (!StringUtils.nullCheck(itemMap.getToggleMessage()).equals("NONE")), false, "&b&lMessage",
                    "&7", "&7*Set a custom message that", "&7will be sent to the player", "&7upon executing a toggle command.",
                    "&9&lMESSAGE: &f" + StringUtils.nullCheck(itemMap.getToggleMessage())), event -> {
                if (!StringUtils.nullCheck(itemMap.getToggleMessage()).equals("NONE")) {
                    itemMap.setToggleMessage(null);
                    togglePane(player, itemMap);
                } else {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TOGGLE MESSAGE").with(Holder.INPUT_EXAMPLE, "&a%item% has been toggled!");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }
            }, event -> {
                itemMap.setToggleMessage(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TOGGLE MESSAGE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                togglePane(event.getPlayer(), itemMap);
            }));
            togglePane.addButton(new Button(fillerPaneBItem));
            togglePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, (!StringUtils.nullCheck(itemMap.getToggleNode()).equals("NONE")), false,
                    "&b&lPermissions", "&7", "&7*Set a custom permissions node", "&7that each player needs in order",
                    "&7to execute the toggle commands.", "&9&lPERMISSIONS: &f" + StringUtils.nullCheck(itemMap.getToggleNode())), event -> {
                if (!StringUtils.nullCheck(itemMap.getToggleNode()).equals("NONE")) {
                    itemMap.setTogglePerm(null);
                    togglePane(player, itemMap);
                } else {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TOGGLE PERMISSION").with(Holder.INPUT_EXAMPLE, "itemjoin.toggle");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }
            }, event -> {
                itemMap.setTogglePerm(ChatColor.stripColor(event.getMessage()));
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "TOGGLE PERMISSION");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                togglePane(event.getPlayer(), itemMap);
            }));
            togglePane.addButton(new Button(fillerPaneBItem), 2);
            togglePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            togglePane.addButton(new Button(fillerPaneBItem), 7);
            togglePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        togglePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting n custom armor trim material.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void trimPane(final Player player, final ItemMap itemMap) {
        Interface trimPane = new Interface(true, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            trimPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            for (org.bukkit.inventory.meta.trim.TrimMaterial material : Objects.requireNonNull(ItemHandler.getTrimMaterials())) {
                trimPane.addButton(new Button(ItemHandler.getItem(ItemHandler.TrimMaterial.valueOf(material.getKey().toString().replace("minecraft:", "").toUpperCase()).getMaterial().name(), 1, false, false, "&f" + org.apache.commons.lang.StringUtils.capitalize(material.getKey().toString().replace("minecraft:", "")), "&7", "&7*Click to set this as", "&7the armor trim material."), event -> trimPatternPane(player, itemMap, material)));
            }
        });
        trimPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting a custom armor trim pattern.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void trimPatternPane(final Player player, final ItemMap itemMap, final org.bukkit.inventory.meta.trim.TrimMaterial material) {
        Interface trimPatternPane = new Interface(true, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            trimPatternPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the trim material menu."), event -> trimPane(player, itemMap)));
            for (org.bukkit.inventory.meta.trim.TrimPattern pattern : Objects.requireNonNull(ItemHandler.getTrimPatterns())) {
                trimPatternPane.addButton(new Button(ItemHandler.getItem(ItemHandler.TrimPattern.valueOf(pattern.getKey().toString().replace("minecraft:", "").toUpperCase()).getMaterial().name(), 1, false, false, "&f" + org.apache.commons.lang.StringUtils.capitalize(pattern.getKey().toString().replace("minecraft:", "")), "&7", "&7*Click to set this as", "&7the armor trim pattern."), event -> {
                    final Map<String, String> trimPattern = new HashMap<>();
                    trimPattern.put(material.getKey().toString().replace("minecraft:", "").toUpperCase(), pattern.getKey().toString().replace("minecraft:", "").toUpperCase());
                    itemMap.setTrimPattern(trimPattern);
                    creatingPane(player, itemMap);
                }));
            }
        });
        trimPatternPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the custom recipe.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void recipePane(final Player player, final ItemMap itemMap) {
        Interface recipePane = new Interface(false, 4, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            recipePane.addButton(new Button(fillerPaneBItem), 3);
            for (int i = 0; i < 9; i++) {
                final int k = i;
                String stack = "CHEST";
                ItemStack stack1 = null;
                if (itemMap.getRecipe().get(0).size() > i && itemMap.getRecipe().get(0).get(i) != 'X') {
                    final ItemRecipe itemRecipe = itemMap.getIngredients().get(itemMap.getRecipe().get(0).get(i));
                    if (itemRecipe != null) {
                        final ItemMap copyMap = ItemUtilities.getUtilities().getItemMap(itemRecipe.getMap());
                        if (copyMap != null) {
                            stack1 = copyMap.getItemStack(player);
                            stack1.setAmount(itemRecipe.getCount());
                        } else {
                            stack = itemRecipe.getMaterial().name() + (itemRecipe.getData() > 0 ? (":" + itemRecipe.getData()) : "") + "#" + itemRecipe.getCount();
                        }
                    }
                }
                if (stack1 != null) {
                    ItemMeta meta = stack1.getItemMeta();
                    if (meta != null) {
                        ItemHandler.addLore(stack1, "&9&lDISPLAY: &f" + meta.getDisplayName(), "&7", "&7*Create a recipe that can be used.");
                        meta.setDisplayName(StringUtils.translateLayout((itemMap.getRecipe().get(0).size() > i ? "&e&l" + itemMap.getRecipe().get(0).get(i) : "&e&lX"), player));
                        stack1.setItemMeta(meta);

                        recipePane.addButton(new Button(stack1, event -> {
                            if ((itemMap.getRecipe().get(0).size() > k && itemMap.getRecipe().get(0).get(k) != 'X')) {
                                setIngredients(player, itemMap, "AIR", k);
                            } else {
                                ingredientPane(player, itemMap, k);
                            }
                        }));
                    }
                } else {
                    recipePane.addButton(new Button(ItemHandler.getItem((stack.contains("#") ? stack.split("#")[0] : stack), (stack.contains("#") ? Integer.parseInt(stack.split("#")[1]) : 1), false, false,
                            (itemMap.getRecipe().get(0).size() > i ? "&e&l" + itemMap.getRecipe().get(0).get(i) : "&e&lX"), "&7", "&7*Create a recipe that can be used."), event -> {
                        if ((itemMap.getRecipe().get(0).size() > k && itemMap.getRecipe().get(0).get(k) != 'X')) {
                            setIngredients(player, itemMap, "AIR", k);
                        } else {
                            ingredientPane(player, itemMap, k);
                        }
                    }));
                }
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
            recipePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            recipePane.addButton(new Button(fillerPaneBItem), 7);
            recipePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        recipePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the custom recipe.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void ingredientPane(final Player player, final ItemMap itemMap, final int k) {
        Interface ingredientPane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            ingredientPane.addButton(new Button(fillerPaneBItem), 3);
            ingredientPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GRASS_BLOCK" : "2"), 1, false, false, "&b&lMaterial", "&7", "&7*Select a material type", "&7to be defined in the recipe."), event -> materialPane(player, itemMap, 3, k)));
            ingredientPane.addButton(new Button(fillerPaneBItem));
            ingredientPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, false, "&b&lCustom Item", "&7", "&7*Select a custom item", "&7to be defined in the recipe."), event -> startModify(player, itemMap, k)));
            ingredientPane.addButton(new Button(fillerPaneBItem), 3);
            ingredientPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> recipePane(player, itemMap)));
            ingredientPane.addButton(new Button(fillerPaneBItem), 7);
            ingredientPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the recipe menu."), event -> recipePane(player, itemMap)));
        });
        ingredientPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for creating NBT Properties.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void nbtPane(final Player player, final ItemMap itemMap) {
        Interface nbtPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            Map<Object, Object> properties = itemMap.getNBTValues();
            nbtPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            nbtPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, true, false, "&e&l&nNew Property", "&7", "&7*Add a new NBT Property to the custom item."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NBT PROPERTY").with(Holder.INPUT_EXAMPLE, "TranslatableDisplayName:&aUltra &cItem");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ChatColor.stripColor(event.getMessage()).contains(":")) {
                    String[] propertyParts = ChatColor.stripColor(event.getMessage()).split(":");
                    properties.put(propertyParts[0], propertyParts[1]);
                }
                itemMap.setNBTValues(properties);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NBT PROPERTY");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                nbtPane(event.getPlayer(), itemMap);
            }));
            for (Object key : properties.keySet()) {
                nbtPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "COMMAND_BLOCK" : "137"), 1, false, false, "&f" + key + ":" + properties.get(key), "&7", "&7*Click to modify or delete", "&7this custom NBT Property."), event -> modifyProperty(player, itemMap, key)));
            }
        });
        nbtPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying NBT Properties.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyProperty(final Player player, final ItemMap itemMap, final Object key) {
        Interface modifyProperty = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            Map<Object, Object> properties = itemMap.getNBTValues();
            modifyProperty.addButton(new Button(fillerPaneGItem), 3);
            modifyProperty.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&c&l&nModify", "&7", "&7*Modify this NBT Property.", "&7", "&9&lProperty: &a" + "&f" + key + ":" + properties.get(key)), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NBT PROPERTY").with(Holder.INPUT_EXAMPLE, "TranslatableDisplayName:&aUltra &cItem");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (ChatColor.stripColor(event.getMessage()).contains(":")) {
                    properties.remove(key);
                    String[] propertyParts = ChatColor.stripColor(event.getMessage()).split(":");
                    properties.put(propertyParts[0], propertyParts[1]);
                }
                itemMap.setNBTValues(properties);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "NBT PROPERTY");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                nbtPane(event.getPlayer(), itemMap);
            }));
            modifyProperty.addButton(new Button(fillerPaneGItem));
            modifyProperty.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this custom NBT Property.", "&7", "&9&lProperty: &a" + "&f" + key + ":" + properties.get(key)), event -> {
                properties.remove(key);
                itemMap.setNBTValues(properties);
                nbtPane(player, itemMap);
            }));
            modifyProperty.addButton(new Button(fillerPaneGItem), 3);
            modifyProperty.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the NBT Properties menu."), event -> nbtPane(player, itemMap)));
            modifyProperty.addButton(new Button(fillerPaneBItem), 7);
            modifyProperty.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the NBT Properties menu."), event -> nbtPane(player, itemMap)));
        });
        modifyProperty.open(player);
    }

    /**
     * Sets the recipe pattern and ingredients.
     *
     * @param player   - The Player to have the Pane opened.
     * @param itemMap  - The ItemMap currently being modified.
     * @param material - The material to be set.
     * @param position - The position in the crafting table being set.
     */
    private static void setIngredients(final Player player, final ItemMap itemMap, final String material, final int position) {
        Map<Character, ItemRecipe> ingredients = itemMap.getIngredients();
        List<Character> recipe = itemMap.getRecipe().get(0);
        char character = 'A';
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            if (alphabet != 'X' && !ingredients.containsKey(alphabet)) {
                character = alphabet;
                break;
            }
        }
        boolean containsMaterial = false;
        char existingCharacter = character;
        for (Character characters : ingredients.keySet()) {
            final ItemRecipe itemRecipe = ingredients.get(characters);
            if ((itemRecipe.getMaterial() != null && itemRecipe.getMaterial().name().equalsIgnoreCase(material)) || (itemRecipe.getMaterial() == null && itemRecipe.getMap().equalsIgnoreCase(material))) {
                character = characters;
                containsMaterial = true;
                existingCharacter = characters;
                break;
            }
        }
        if (!StringUtils.containsIgnoreCase(material, "AIR")) {
            final char finalCharacter = (!containsMaterial ? character : existingCharacter);
            Interface ingredPane = new Interface(true, 6, exitButton, GUIName, player);
            SchedulerUtils.runAsync(() -> {
                ingredPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item recipe menu."), event -> creatingPane(player, itemMap)));
                for (int i = 1; i <= 64; i++) {
                    final int k = i;
                    ingredPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lCount: &a&l" + k, "&7", "&7*Click to set the", "&7ingredient count (stack size)."), event -> {
                        ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(material);
                        if (ingredMap == null) {
                            ingredients.put(finalCharacter, new ItemRecipe(null, ItemHandler.getMaterial(material, null), (byte) 0, k));
                        } else {
                            ingredients.put(finalCharacter, new ItemRecipe(material, null, (byte) 0, k));
                        }
                        while (position >= recipe.size()) {
                            recipe.add('X');
                        }
                        recipe.set(position, (!StringUtils.containsIgnoreCase(material, "AIR") ? finalCharacter : 'X'));
                        itemMap.setRecipe(recipe);
                        itemMap.setIngredients(ingredients);
                        recipePane(player, itemMap);
                    }));
                }
            });
            ingredPane.open(player);
        } else if (StringUtils.containsIgnoreCase(material, "AIR")) {
            int count = 0;
            for (Character recipes : recipe) {
                if (recipes.equals(recipe.get(position))) {
                    count++;
                }
            }
            if (count == 1) {
                ingredients.remove(recipe.get(position));
            }
            while (position >= recipe.size()) {
                recipe.add('X');
            }
            recipe.set(position, (!StringUtils.containsIgnoreCase(material, "AIR") ? character : 'X'));
            itemMap.setRecipe(recipe);
            itemMap.setIngredients(ingredients);
            recipePane(player, itemMap);
        }
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying banner items.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void bannerPane(final Player player, final ItemMap itemMap) {
        Interface bannerPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            bannerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            for (PatternType pattern : ItemHandler.getPatterns()) {
                String patternString = "NONE";
                if (!StringUtils.nullCheck(itemMap.getBannerPatterns().toString()).equals("NONE")) {
                    for (Pattern patterns : itemMap.getBannerPatterns()) {
                        if (patterns.getPattern() == pattern) {
                            patternString = patterns.getColor() + ":" + ItemHandler.getPatternName(patterns);
                            break;
                        }
                    }
                }
                final String checkPattern = patternString;
                bannerPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, (!checkPattern.equals("NONE")), false, "&f" + ItemHandler.getPatternName(pattern), "&7", "&7*Click to add this as", "&7a banner pattern.", (!checkPattern.equals("NONE") ?
                        "&9&lInformation: &a" + checkPattern : "")), event -> {
                    if (!checkPattern.equals("NONE")) {
                        List<Pattern> patternList = itemMap.getBannerPatterns();
                        if (!StringUtils.nullCheck(itemMap.getBannerPatterns().toString()).equals("NONE")) {
                            for (Pattern patterns : patternList) {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void patternPane(final Player player, final ItemMap itemMap, final PatternType pattern) {
        Interface colorPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the banner patterns menu."), event -> bannerPane(player, itemMap)));
            for (DyeColor color : DyeColor.values()) {
                colorPane.addButton(new Button(ItemHandler.getItem(ServerUtils.hasSpecificUpdate("1_13") ? color.name() + "_DYE" : "351:8", 1, false, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your banner pattern."), event -> {
                    List<Pattern> patterns = itemMap.getBannerPatterns();
                    patterns.add(new Pattern(color, pattern));
                    itemMap.setBannerPatterns(patterns);
                    bannerPane(player, itemMap);
                }));
            }
        });
        colorPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for selecting the Potion Type.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void potionPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface potionPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage != 1) {
                potionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            } else {
                potionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> otherPane(player, itemMap)));
            }
            for (PotionEffectType potion : CompatUtils.values(PotionEffectType.class)) {
                if (potion != null) {
                    String potionString = "NONE";
                    if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
                        for (PotionEffect potions : itemMap.getPotionEffect()) {
                            if (potions.getType() == potion) {
                                potionString = CompatUtils.getName(potions) + ":" + potions.getAmplifier() + ":" + (potions.getDuration());
                                break;
                            }
                        }
                    }
                    final String checkPotion = potionString;
                    potionPane.addButton(new Button(ItemHandler.getItem("GLASS_BOTTLE", 1, (!checkPotion.equals("NONE")), false, "&f" + CompatUtils.getName(potion), "&7", "&7*Add this potion effect", "&7to the item.",
                            (!checkPotion.equals("NONE") ? "&9&lInformation: &a" + checkPotion : "")), event -> {
                        if (!checkPotion.equals("NONE")) {
                            List<PotionEffect> potionEffects = itemMap.getPotionEffect();
                            if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
                                for (PotionEffect potions : potionEffects) {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void teleportPane(final Player player, final ItemMap itemMap, final int stage) {
        Interface teleportPane = new Interface(false, 1, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (stage == 1) {
                teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> {
                    setItemFlags(itemMap);
                    otherPane(player, itemMap);
                }));
            } else {
                teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
                    setItemFlags(itemMap);
                    creatingPane(player, itemMap);
                }));
            }
            teleportPane.addButton(new Button(fillerPaneGItem), 2);
            teleportPane.addButton(new Button(ItemHandler.getItem("BLAZE_POWDER", 1, false, false, "&b&lTeleport Effect", "&7", "&7*The effect to play at the", "&7arrow landed location when", "&7the player is teleported.",
                    "&9&lTeleport-Effect: &a" + StringUtils.nullCheck(itemMap.getTeleportEffect())), event -> particlePane(player, itemMap, stage)));
            teleportPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, itemMap.isTeleport(), false, "&a&l&nTeleport", "&7",
                    "&a&lTrue&f: &7Teleports the Player to the location", "&7that the arrow landed.",
                    "&cNOTE: &7This only works if the arrow is fired by a Bow.",
                    "&9&lENABLED: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()), event -> {
                itemMap.setTeleport(!itemMap.isTeleport());
                teleportPane(player, itemMap, stage);
            }));
            teleportPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, false, "&a&lTeleport Sound", "&7", "&7*The sound to play at the", "&7arrow landed location when", "&7the player is teleported.",
                    "&9&lTeleport-Sound: &a" + StringUtils.nullCheck(itemMap.getTeleportSound()),
                    "&9&lVolume: &a" + ((!StringUtils.nullCheck(itemMap.getTeleportSound()).equals("NONE")) ? itemMap.getTeleportVolume() : "NONE"),
                    "&9&lPitch: &a" + ((!StringUtils.nullCheck(itemMap.getTeleportSound()).equals("NONE")) ? itemMap.getTeleportPitch() : "NONE")), event -> {
                soundPane(player, itemMap, stage);
                if (!StringUtils.nullCheck(itemMap.getTeleportSound()).equals("NONE")) {
                    itemMap.setTeleportSound(null);
                    teleportPane(player, itemMap, stage);
                } else {
                    soundPane(player, itemMap, stage);
                }
            }));
            teleportPane.addButton(new Button(fillerPaneGItem), 2);
            if (stage == 1) {
                teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> {
                    setItemFlags(itemMap);
                    otherPane(player, itemMap);
                }));
            } else {
                teleportPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void levelPane(final Player player, final ItemMap itemMap, final PotionEffectType potion, final int stage) {
        Interface levelPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            levelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> potionPane(player, itemMap, stage)));
            levelPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Level", "&7", "&7*Click to set a custom level (strength)", "&7value for the potion effect.", "&7", "&c&lNote: &7Any duration LONGER than", "&71800 seconds (30 minutes) will", "&7result in an infinite duration."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "EFFECT LEVEL").with(Holder.INPUT_EXAMPLE, "16");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "EFFECT LEVEL");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    durationPane(event.getPlayer(), itemMap, potion, Integer.parseInt(ChatColor.stripColor(event.getMessage())), stage);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    levelPane(event.getPlayer(), itemMap, potion, stage);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                levelPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:6"), k, false, false, "&d&lLevel: &a&l" + k, "&7", "&7*Click to set the", "&7level (strength) of the potion effect."), event -> {
                    itemMap.setInteractCooldown(k);
                    durationPane(player, itemMap, potion, k, stage);
                }));
            }
        });
        levelPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for setting the Potion Duration.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void durationPane(final Player player, final ItemMap itemMap, final PotionEffectType potion, int level, final int stage) {
        Interface durationPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> potionPane(player, itemMap, stage)));
            durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the potion effect."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "EFFECT DURATION").with(Holder.INPUT_EXAMPLE, "110");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "EFFECT DURATION");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    potionPane(event.getPlayer(), itemMap, stage);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                    durationPane(event.getPlayer(), itemMap, potion, level, stage);
                }
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                durationPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7duration of the potion effect."), event -> {
                    List<PotionEffect> effects = itemMap.getPotionEffect();
                    effects.add(new PotionEffect(potion, k, level));
                    itemMap.setPotionEffect(effects);
                    potionPane(player, itemMap, stage);
                }));
            }
        });
        durationPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for selecting the Firework Power.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void powerPane(final Player player, final ItemMap itemMap) {
        Interface powerPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            powerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
            powerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Power", "&7", "&7*Click to set a custom power", "&7value for the firework."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIREWORK POWER").with(Holder.INPUT_EXAMPLE, "96");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    itemMap.setFireworkPower(Integer.parseInt(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "FIREWORK POWER");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                otherPane(player, itemMap);
            }));
            for (int i = 1; i <= 64; i++) {
                final int k = i;
                powerPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11"), k, false, false, "&9&lPower Level: &a&l" + k, "&7", "&7*Click to set the", "&7power level of the firework."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void colorPane(final Player player, final ItemMap itemMap) {
        Interface colorPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
            for (DyeColor color : DyeColor.values()) {
                colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? color.name() + "_DYE" : "351:8"), 1, StringUtils.containsValue(itemMap.getFireworkColor(), color.name()), false, "&f" + color.name(),
                        "&7", "&7*This will be the color", "&7of your firework charge.", "&9&lENABLED: &a" + (StringUtils.containsValue(itemMap.getFireworkColor(), color.name()) + "").toUpperCase()), event -> {
                    List<DyeColor> colors = itemMap.getFireworkColor();
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void designPane(final Player player, final ItemMap itemMap) {
        Interface designPane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            designPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
            for (Type type : Type.values()) {
                designPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, false, "&f" + type.name(), "&7", "&7*This will be the type (pattern)", "&7of your firework."), event -> {
                    itemMap.setFireworkType(type);
                    otherPane(player, itemMap);
                }));
            }
        });
        designPane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying book items.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void pagePane(final Player player, final ItemMap itemMap) {
        Interface pagePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            pagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
            pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&l&nNew Page", "&7", "&7*Add a new page to the book."), event -> linePane(player, itemMap, true, itemMap.getListPages().size())));
            for (int i = 1; i <= itemMap.getListPages().size(); i++) {
                final int k = i;
                pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&b&lPage " + i, "&7", "&7*Click to modify the contents", "&7of this book page."), event -> linePane(player, itemMap, false, k - 1)));
            }
        });
        pagePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying book page lines.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void linePane(final Player player, final ItemMap itemMap, final boolean isNew, final int page) {
        Interface linePane = new Interface(true, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            List<List<String>> pages = itemMap.getListPages();
            linePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the book pages menu."), event -> pagePane(player, itemMap)));
            if (isNew) {
                linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
                    player.closeInventory();
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE").with(Holder.INPUT_EXAMPLE, "&eWelcome to the Server!");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                }, event -> {
                    List<String> newPage = new ArrayList<>();
                    newPage.add(ChatColor.stripColor(event.getMessage()));
                    pages.add(newPage);
                    itemMap.setListPages(pages);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    linePane(event.getPlayer(), itemMap, false, page);
                }));
            } else {
                List<String> selectPage = pages.get(page);
                linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this page from the book.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
                    pages.remove(page);
                    itemMap.setListPages(pages);
                    pagePane(player, itemMap);
                }));
                if (selectPage.size() < 14) {
                    linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, false, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lLine: &a" + (selectPage.size() + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE").with(Holder.INPUT_EXAMPLE, "&eWelcome to the Server!");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }, event -> {
                        selectPage.add(ChatColor.stripColor(event.getMessage()));
                        pages.set(page, selectPage);
                        itemMap.setListPages(pages);
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                        linePane(event.getPlayer(), itemMap, false, page);
                    }));
                }
                for (int i = 1; i <= selectPage.size(); i++) {
                    final int k = i;
                    linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&f" + selectPage.get(k - 1), "&7", "&7*Click to modify or delete", "&7this line in the book page.", "&7", "&9&lLine: &a" + k +
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void modifyPagesPane(final Player player, final ItemMap itemMap, final int line, final int page) {
        Interface linePane = new Interface(false, 2, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            List<List<String>> pages = itemMap.getListPages();
            List<String> selectPage = pages.get(page);
            linePane.addButton(new Button(fillerPaneGItem), 3);
            linePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, false, "&c&l&nModify", "&7", "&7*Modify this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE").with(Holder.INPUT_EXAMPLE, "&eWelcome to the Server!");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                selectPage.set(line, ChatColor.stripColor(event.getMessage()));
                pages.set(page, selectPage);
                itemMap.setListPages(pages);
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "PAGE LINE");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                linePane(event.getPlayer(), itemMap, false, page);
            }));
            linePane.addButton(new Button(fillerPaneGItem));
            linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, false, "&c&l&nDelete", "&7", "&7*Delete this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
                selectPage.remove(selectPage.get(line));
                pages.set(page, selectPage);
                itemMap.setListPages(pages);
                linePane(player, itemMap, false, page);
            }));
            linePane.addButton(new Button(fillerPaneGItem), 3);
            linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> linePane(player, itemMap, false, page)));
            linePane.addButton(new Button(fillerPaneBItem), 7);
            linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> linePane(player, itemMap, false, page)));
        });
        linePane.open(player);
    }

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying item attributes.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void attributePane(final Player player, final ItemMap itemMap, final boolean isLeather) {
        Interface attributePane = new Interface(true, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            if (isLeather) {
                attributePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the other settings menu."), event -> otherPane(player, itemMap)));
            } else {
                attributePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            }
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                for (Attribute attribute : CompatUtils.values(Attribute.class)) {
                    String checkAttribute = (itemMap.getAttributes().containsKey(CompatUtils.getName(attribute)) ? (CompatUtils.getName(attribute) + ":" + itemMap.getAttributes().get(CompatUtils.getName(attribute))) : "NONE");
                    attributePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.getAttributes().containsKey(CompatUtils.getName(attribute)), false, "&f" + CompatUtils.getName(attribute), "&7", "&7*Add this custom attribute to the item.",
                            (!checkAttribute.equals("NONE") ? "&9&lInformation: &a" + checkAttribute : "")), event -> {
                        if (itemMap.getAttributes().containsKey(CompatUtils.getName(attribute))) {
                            Map<String, Double> attributeList = itemMap.getAttributes();
                            attributeList.remove(CompatUtils.getName(attribute));
                            attributePane(player, itemMap, isLeather);
                        } else {
                            strengthPane(player, itemMap, CompatUtils.getName(attribute), isLeather);
                        }
                    }));
                }
            } else {
                String[] attributes = new String[]{"GENERIC_ATTACK_DAMAGE", "GENERIC_FOLLOW_RANGE", "GENERIC_MAX_HEALTH", "GENERIC_MOVEMENT_SPEED"};
                for (String attribute : attributes) {
                    String checkAttribute = (itemMap.getAttributes().containsKey(attribute) ? (attribute + ":" + itemMap.getAttributes().get(attribute)) : "NONE");
                    attributePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.getAttributes().containsKey(attribute), false, "&f" + attribute, "&7", "&7*Add this custom attribute to the item.",
                            (!checkAttribute.equals("NONE") ? "&9&lInformation: &a" + checkAttribute : "")), event -> {
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

//  ============================================== //
//                Book Pages Menus      	       //
//  ============================================== //

    /**
     * Opens the Pane for the Player.
     * This Pane is for modifying item attributes strength.
     *
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void strengthPane(final Player player, final ItemMap itemMap, final String attribute, final boolean isLeather) {
        Interface strengthPane = new Interface(true, 6, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            strengthPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the custom attributes menu."), event -> attributePane(player, itemMap, isLeather)));
            strengthPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4"), 1, false, false, "&e&lCustom Strength", "&7", "&7*Click to set a custom strength", "&7value for the custom attribute."), event -> {
                player.closeInventory();
                final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "STRENGTH").with(Holder.INPUT_EXAMPLE, "14.0");
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
            }, event -> {
                if (StringUtils.isInt(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player)) || StringUtils.isDouble(StringUtils.translateLayout(ChatColor.stripColor(event.getMessage()), player))) {
                    Map<String, Double> attributeList = itemMap.getAttributes();
                    attributeList.put(attribute, Double.parseDouble(ChatColor.stripColor(event.getMessage())));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "STRENGTH");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                } else {
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, ChatColor.stripColor(event.getMessage()));
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.noInteger", player, placeHolders);
                }
                attributePane(event.getPlayer(), itemMap, isLeather);
            }));
            for (double i = 1; i < 90; i++) {
                final double k = i;
                strengthPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "PINK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:6"), 1, false, false, "&9&lStrength: &a&l" + k, "&7", "&7*Click to set the strength", "&7 of the custom attribute."), event -> {
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
     * @param player  - The Player to have the Pane opened.
     * @param itemMap - The ItemMap currently being modified.
     */
    private static void otherPane(final Player player, final ItemMap itemMap) {
        Interface otherPane = new Interface(false, 3, exitButton, GUIName, player);
        SchedulerUtils.runAsync(() -> {
            otherPane.addButton(new Button(fillerPaneGItem), 4);
            otherPane.addButton(new Button(headerStack(player, itemMap)));
            otherPane.addButton(new Button(fillerPaneGItem), 4);
            if (itemMap.getMaterial().toString().contains("WRITTEN_BOOK")) {
                otherPane.addButton(new Button(fillerPaneGItem), 3);
                otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&e&lPages", "&7", "&7*Define custom pages for the book.",
                        "&9&lPages: &a" + (!StringUtils.nullCheck(itemMap.getPages() + "").equals("NONE") ? "YES" : "NONE")), event -> pagePane(player, itemMap)));
                otherPane.addButton(new Button(fillerPaneGItem));
                otherPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, false, "&a&lAuthor", "&7", "&7*Define the author of the book.", "&9&lAuthor: &a" + StringUtils.nullCheck(itemMap.getAuthor())), event -> {
                    if (!StringUtils.nullCheck(itemMap.getAuthor()).equals("NONE")) {
                        itemMap.setAuthor(null);
                        otherPane(player, itemMap);
                    } else {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "AUTHOR").with(Holder.INPUT_EXAMPLE, "RockinChaos");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }
                }, event -> {
                    itemMap.setAuthor(ChatColor.stripColor(event.getMessage()));
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "AUTHOR");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    otherPane(event.getPlayer(), itemMap);
                }));
                otherPane.addButton(new Button(fillerPaneGItem), 3);
            } else if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
                StringBuilder potionList = new StringBuilder();
                StringBuilder potionString = new StringBuilder();
                if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
                    for (PotionEffect potions : itemMap.getPotionEffect()) {
                        potionString.append(CompatUtils.getName(potions)).append(":").append(potions.getAmplifier()).append(":").append(potions.getDuration()).append(", ");
                    }
                    for (String split : StringUtils.softSplit(StringUtils.nullCheck(potionString.toString()))) {
                        potionList.append("&a").append(split).append(" /n ");
                    }
                }
                otherPane.addButton(new Button(fillerPaneGItem), 2);
                otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "GOLDEN_HELMET" : "314"), 1, false, false, "&b&lSkull Owner", "&7", "&7*Define a skull owner for the", "&7head adding that persons skin.", "&7", "&7You can only define skull owner",
                        "&7or skull texture, this will", "&7remove any skull textures.", "&9&lSkull-Owner: &a" + StringUtils.nullCheck(itemMap.getSkull())), event -> {
                    if (itemMap.getDynamicOwners() != null && !itemMap.getDynamicOwners().isEmpty()) {
                        animatedSkullPane(player, itemMap, true);
                    } else {
                        if (!StringUtils.nullCheck(itemMap.getSkull()).equals("NONE")) {
                            itemMap.setSkull(null);
                            otherPane(player, itemMap);
                        } else {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER").with(Holder.INPUT_EXAMPLE, "RockinChaos");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        }
                    }
                }, event -> {
                    itemMap.setSkull(ChatColor.stripColor(event.getMessage()));
                    itemMap.setSkullTexture(null);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL OWNER");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    otherPane(event.getPlayer(), itemMap);
                }));
                otherPane.addButton(new Button(fillerPaneGItem));
                otherPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, false, "&a&lSkull Texture", "&7", "&7*Add a skull texture for the", "&7head as a custom skin.", "&7", "&7You can only define skull texture",
                        "&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like&a minecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.", "&9&lSkull-Texture: &a" +
                                (!StringUtils.nullCheck(itemMap.getSkullTexture()).equals("NONE") ? (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : "")), event -> {
                    if (itemMap.getDynamicTextures() != null && !itemMap.getDynamicTextures().isEmpty()) {
                        animatedSkullPane(player, itemMap, false);
                    } else {
                        if (!StringUtils.nullCheck(itemMap.getSkullTexture()).equals("NONE")) {
                            itemMap.setSkullTexture(null);
                            otherPane(player, itemMap);
                        } else {
                            player.closeInventory();
                            final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE").with(Holder.INPUT_EXAMPLE, "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=");
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                        }
                    }
                }, event -> {
                    itemMap.setSkullTexture(ChatColor.stripColor(event.getMessage()));
                    itemMap.setSkull(null);
                    final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "SKULL TEXTURE");
                    ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                    otherPane(event.getPlayer(), itemMap);
                }));
                otherPane.addButton(new Button(fillerPaneGItem));
                otherPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the item.", "&9&lPotion-Effects: &a" + StringUtils.nullCheck(potionList.toString())),
                        event -> potionPane(player, itemMap, 1)));
                otherPane.addButton(new Button(fillerPaneGItem), 2);
            } else if (itemMap.getMaterial().toString().contains("TIPPED_ARROW")) {
                StringBuilder potionList = new StringBuilder();
                StringBuilder potionString = new StringBuilder();
                if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
                    for (PotionEffect potions : itemMap.getPotionEffect()) {
                        potionString.append(CompatUtils.getName(potions)).append(":").append(potions.getAmplifier()).append(":").append(potions.getDuration()).append(", ");
                    }
                    for (String split : StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length())))) {
                        potionList.append("&a").append(split).append(" /n ");
                    }
                }
                otherPane.addButton(new Button(fillerPaneGItem), 3);
                otherPane.addButton(new Button(ItemHandler.getItem("BLAZE_POWDER", 1, false, false, "&e&lEffects", "&7", "&7*Add custom effects", "&7to the arrow tip.", "&9&lTipped-Effect: &a" + StringUtils.nullCheck(potionList.toString())),
                        event -> potionPane(player, itemMap, 1)));
                otherPane.addButton(new Button(fillerPaneGItem));
                otherPane.addButton(new Button(ItemHandler.getItem("ENDER_PEARL", 1, false, false, "&e&lTeleport", "&7", "&7*Set the arrow to teleport", "&7the player upon landing.", "&9&lEnabled: &a" + String.valueOf(itemMap.isTeleport()).toUpperCase()),
                        event -> teleportPane(player, itemMap, 1)));
                otherPane.addButton(new Button(fillerPaneGItem), 3);
            } else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
                StringBuilder colorList = new StringBuilder();
                if (!StringUtils.nullCheck(itemMap.getFireworkColor().toString()).equals("NONE")) {
                    for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getFireworkColor().toString()))) {
                        colorList.append("&a").append(split).append(" /n ");
                    }
                }
                otherPane.addButton(new Button(fillerPaneGItem), 2);
                otherPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, false, "&a&lType", "&7", "&7*Set the style of the explosion.", "&9&lType: &a" + StringUtils.nullCheck(itemMap.getFireworkType() + "")), event -> {
                    if (!StringUtils.nullCheck(itemMap.getFireworkType() + "").equals("NONE")) {
                        itemMap.setFireworkType(null);
                        otherPane(player, itemMap);
                    } else {
                        designPane(player, itemMap);
                    }
                }));
                otherPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.getFireworkFlicker(), false, "&a&lFlicker", "&7", "&7*Show the flicker effect as", "&7the firework particles dissipate", "&7after the explosion.",
                        "&9&lENABLED: &a" + itemMap.getFireworkFlicker()), event -> {
                    itemMap.setFireworkFlicker(!itemMap.getFireworkFlicker());
                    otherPane(player, itemMap);
                }));
                otherPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, itemMap.getFireworkTrail(), false, "&a&lTrail", "&7", "&7*Show the trail (smoke) of", "&7the firework when launched.", "&9&lENABLED: &a" + itemMap.getFireworkTrail()), event -> {
                    itemMap.setFireworkTrail(!itemMap.getFireworkTrail());
                    otherPane(player, itemMap);
                }));
                otherPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, false, "&a&lPower", "&7", "&7*Set the power (distance)", "&7that the firework travels.", "&9&lPower: &a" + StringUtils.nullCheck(itemMap.getFireworkPower() + "&7")), event -> {
                    if (!StringUtils.nullCheck(itemMap.getFireworkPower() + "&7").equals("NONE")) {
                        itemMap.setFireworkPower(0);
                        otherPane(player, itemMap);
                    } else {
                        powerPane(player, itemMap);
                    }
                }));
                otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "LIME_DYE" : "351:10"), 1, false, false, "&a&lColor(s)", "&7", "&7*Define the individual colors of the", "&7firework effect type.",
                        "&9&lColor(s): &a" + (!StringUtils.nullCheck(colorList.toString()).equals("NONE") ? colorList.toString() : "NONE")), event -> colorPane(player, itemMap)));
                otherPane.addButton(new Button(fillerPaneGItem), 2);
            } else if (itemMap.getMaterial().toString().contains("LEATHER_")) {
                Interface colorPane = new Interface(true, 6, exitButton, GUIName, player);
                colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> otherPane(player, itemMap)));
                for (DyeColor color : DyeColor.values()) {
                    colorPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? color.name() + "_DYE" : "351:8"), 1, false, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your leather armor."), event -> {
                        itemMap.setLeatherColor(color.name());
                        itemMap.setLeatherHex(null);
                        otherPane(player, itemMap);
                    }));
                }
                otherPane.addButton(new Button(fillerPaneGItem), 3);
                otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "YELLOW_DYE" : "351:11"), 1, false, false, "&a&lDye", "&7", "&7*Add a custom color to", "&7your leather armor.", "&9&lLeather-Color: &a" +
                        (!StringUtils.nullCheck(itemMap.getLeatherColor()).equals("NONE") ? StringUtils.nullCheck(itemMap.getLeatherColor()) : StringUtils.nullCheck(itemMap.getLeatherHex()))), event -> {
                    if (itemMap.getLeatherColor() != null) {
                        itemMap.setLeatherColor(null);
                        otherPane(player, itemMap);
                    } else {
                        colorPane.open(player);
                    }
                }));
                if (!ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot")) {
                    StringBuilder attributeList = new StringBuilder();
                    StringBuilder attributeString = new StringBuilder();
                    if (!StringUtils.nullCheck(itemMap.getAttributes().toString()).equals("NONE")) {
                        for (String attribute : itemMap.getAttributes().keySet()) {
                            attributeString.append(attribute).append(":").append(itemMap.getAttributes().get(attribute)).append(", ");
                        }
                        for (String split : StringUtils.softSplit(StringUtils.nullCheck(attributeString.substring(0, attributeString.length())))) {
                            attributeList.append("&a").append(split).append(" /n ");
                        }
                    }
                    otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "ENCHANTED_GOLDEN_APPLE" : "322:1"), 1, false, false, "&a&lAttributes", "&7", "&7*Add a custom attribute to", "&7your armor or weapon.", (!StringUtils.nullCheck(attributeList.toString()).equals("NONE") ? "&9&lAttributes: &a" + attributeList : "")), event -> attributePane(player, itemMap, true)));
                } else {
                    otherPane.addButton(new Button(fillerPaneGItem));
                }
                otherPane.addButton(new Button(ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "WRITABLE_BOOK" : "386"), 1, false, false, "&a&lHex Color", "&7", "&7*Add a custom hex color", "&7to your leather armor.", "&9&lLeather-Color: &a" +
                        (!StringUtils.nullCheck(itemMap.getLeatherHex()).equals("NONE") ? StringUtils.nullCheck(itemMap.getLeatherHex()) : StringUtils.nullCheck(itemMap.getLeatherColor()))), event -> {
                    if (itemMap.getLeatherHex() != null) {
                        itemMap.setLeatherHex(null);
                        otherPane(player, itemMap);
                    } else {
                        player.closeInventory();
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "HEX COLOR").with(Holder.INPUT_EXAMPLE, "#033dfc");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputType", player, placeHolders);
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputExample", player, placeHolders);
                    }
                }, event -> {
                    if (itemMap.getLeatherHex() == null) {
                        itemMap.setLeatherHex(ChatColor.stripColor(event.getMessage()));
                        itemMap.setLeatherColor(null);
                        final PlaceHolder placeHolders = new PlaceHolder().with(Holder.INPUT, "HEX COLOR");
                        ItemJoin.getCore().getLang().sendLangMessage("commands.menu.inputSet", player, placeHolders);
                        otherPane(event.getPlayer(), itemMap);
                    }
                }));
                otherPane.addButton(new Button(fillerPaneGItem), 3);
            }
            otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
            otherPane.addButton(new Button(fillerPaneBItem), 7);
            otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> creatingPane(player, itemMap)));
        });
        otherPane.open(player);
    }

    /**
     * Gets the Header ItemStack.
     *
     * @param player  - The player getting the Header Stack.
     * @param itemMap - The ItemMap to be formatted.
     * @return The formatted Header ItemStack.
     */
    private static ItemStack headerStack(final Player player, final ItemMap itemMap) {
        StringBuilder slotList = new StringBuilder();
        StringBuilder slotString = new StringBuilder();
        itemMap.renderItemStack();
        ItemStack item = new ItemStack(Material.STONE);
        final String itemMaterial = ItemHandler.getMaterialName(itemMap.getTempItem());
        if (!StringUtils.nullCheck(itemMap.getMultipleSlots().toString()).equals("NONE")) {
            for (String slot : itemMap.getMultipleSlots()) {
                slotString.append(slot).append(", ");
            }
            if (slotString.length() >= 2) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(slotString.substring(0, slotString.length() - 2)))) {
                    slotList.append("&a").append(split).append(" /n ");
                }
            }
        }
        StringBuilder itemflagsList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getItemFlags()).equals("NONE")) {
            for (String split : StringUtils.softSplit(itemMap.getItemFlags())) {
                itemflagsList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder triggersList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getTriggers()).equals("NONE")) {
            for (String split : StringUtils.softSplit(itemMap.getTriggers())) {
                triggersList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder disabledList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()).equals("NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()))) {
                disabledList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder enabledList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()).equals("NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
                enabledList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder enabledRegionList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getEnabledRegions().toString()).equals("NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnabledRegions().toString()))) {
                enabledRegionList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder disabledRegionList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getDisabledRegions().toString()).equals("NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getDisabledRegions().toString()))) {
                disabledRegionList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder enchantList = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getEnchantments().toString()).equals("NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getEnchantments().toString()))) {
                enchantList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder potionList = new StringBuilder();
        StringBuilder potionString = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getPotionEffect().toString()).equals("NONE")) {
            for (PotionEffect potions : itemMap.getPotionEffect()) {
                potionString.append(CompatUtils.getName(potions)).append(":").append(potions.getAmplifier()).append(":").append(potions.getDuration()).append(", ");
            }
            if (potionString.length() >= 2) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(potionString.substring(0, potionString.length() - 2)))) {
                    potionList.append("&a").append(split).append(" /n ");
                }
            }
        }
        StringBuilder attributeList = new StringBuilder();
        StringBuilder attributeString = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getAttributes().toString()).equals("NONE")) {
            for (String attribute : itemMap.getAttributes().keySet()) {
                attributeString.append(attribute).append(":").append(itemMap.getAttributes().get(attribute)).append(", ");
            }
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(attributeString.toString()))) {
                attributeList.append("&a").append(split).append(" /n ");
            }
        }
        StringBuilder patternList = new StringBuilder();
        StringBuilder patternString = new StringBuilder();
        if (!StringUtils.nullCheck(itemMap.getBannerPatterns().toString()).equals("NONE")) {
            for (Pattern patterns : itemMap.getBannerPatterns()) {
                patternString.append(patterns.getColor()).append(":").append(ItemHandler.getPatternName(patterns)).append(", ");
            }
            if (patternString.length() >= 2) {
                for (String split : StringUtils.softSplit(StringUtils.nullCheck(patternString.substring(0, patternString.length() - 2)))) {
                    patternList.append("&a").append(split).append(" /n ");
                }
            }
        }
        StringBuilder colorList = new StringBuilder();
        if (!Objects.equals(StringUtils.nullCheck(itemMap.getFireworkColor().toString()), "NONE")) {
            for (String split : StringUtils.softSplit(StringUtils.nullCheck(itemMap.getFireworkColor().toString()))) {
                colorList.append("&a").append(split).append(" /n ");
            }
        }
        boolean useCommands = true;
        if (itemMap.getCommands().length > 0) {
            for (ItemCommand command : itemMap.getCommands()) {
                if (command.getRawCommand().equalsIgnoreCase("default: ")) {
                    useCommands = false;
                }
            }
        } else if (!(itemMap.getCommands().length > 0)) {
            useCommands = false;
        }
        boolean useToggle = itemMap.getToggleCommands() != null && !itemMap.getToggleCommands().isEmpty();
        StringBuilder mobs = new StringBuilder();
        for (EntityType entity : itemMap.getMobsDrop().keySet()) {
            mobs.append(entity.name()).append(", ");
        }
        StringBuilder blocks = new StringBuilder();
        for (Material material : itemMap.getBlocksDrop().keySet()) {
            blocks.append(material.name()).append(", ");
        }
        String armorMeta = "";
        if (ServerUtils.hasSpecificUpdate("1_20") && ItemHandler.isArmor(itemMap.getMaterial().toString())) {
            if (itemMap.getTrimPattern() != null && !itemMap.getTrimPattern().isEmpty()) {
                final Map.Entry<String, String> entry = itemMap.getTrimPattern().entrySet().iterator().next();
                armorMeta = entry.getKey() + ":" + entry.getValue();
            }
        }
        try {
            item = ItemHandler.getItem(itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""), 1, false, true, "&7*&6&l&nItem Information", "&7", "&9&lNode: &a" + itemMap.getConfigName(), "&9&lMaterial: &a"
                            + itemMap.getMaterial().toString() + ((itemMap.getDataValue() != null && itemMap.getDataValue() != 0) ? ":" + itemMap.getDataValue() : ""),
                    (itemMap.getMultipleSlots() != null && !itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSlot: &a" + itemMap.getSlot().toUpperCase()), (itemMap.getCount(player) != 1 && itemMap.getCount(player) != 0) ? "&9&lCount: &a" + itemMap.getCount(player) : "",
                    ((!StringUtils.nullCheck(itemMap.getCustomName()).equals("NONE") && (!itemMaterial.equalsIgnoreCase(itemMap.getCustomName()))) ? "&9&lName: &a" + itemMap.getCustomName() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getCustomLore().toString()), "NONE") ? "&9&lLore: &a" + (StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").length() > 40 ? StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").substring(0, 40) : StringUtils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",")) : ""),
                    (!StringUtils.nullCheck(itemMap.getDurability() + "&7").equals("NONE") ? "&9&lDurability: &a" + itemMap.getDurability() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getData() + "&7"), "NONE") ? "&9&lTexture Data: &a" + itemMap.getData() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getModelData() + "&7"), "NONE") ? "&9&LModel Data: &a" + itemMap.getModelData() : ""), (useCommands ? "&9&lCommands: &aYES" : ""), (useToggle ? "&9&lToggleable: &aYES" : ""),
                    (!StringUtils.nullCheck(itemMap.getItemCost()).equals("NONE") ? "&9&lCommands-Item: &a" + itemMap.getItemCost() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getCommandCost(player) + "&7"), "NONE") ? "&9&lCommands-Cost: &a" + itemMap.getCommandCost(player) : ""),
                    (!StringUtils.nullCheck(itemMap.getCommandReceive() + "&7").equals("NONE") ? "&9&lCommands-Receive: &a" + itemMap.getCommandReceive() : ""),
                    (!StringUtils.nullCheck(itemMap.getCommandSequence() + "").equals("NONE") ? "&9&lCommands-Sequence: &a" + itemMap.getCommandSequence() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getCommandCooldown() + "&7"), "NONE") ? "&9&lCommands-Cooldown: &a" + itemMap.getCommandCooldown() + " second(s)" : ""),
                    (!StringUtils.nullCheck(itemMap.getCooldownMessage()).equals("NONE") ? "&9&lCooldown-Message: &a" + itemMap.getCooldownMessage() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getCommandSound() + ""), "NONE") ? "&9&lCommands-Sound: &a" + itemMap.getCommandSound() : ""),
                    (!StringUtils.nullCheck(itemMap.getCommandParticle()).equals("NONE") ? "&9&lCommands-Particle: &a" + itemMap.getCommandParticle() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getEnchantments().toString()), "NONE") ? "&9&lEnchantments: &a" + enchantList : ""),
                    (!StringUtils.nullCheck(itemMap.getItemFlags()).equals("NONE") ? "&9&lItemflags: &a" + itemflagsList : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getTriggers()), "NONE") ? "&9&lTriggers: &a" + triggersList : ""),
                    (!StringUtils.nullCheck(itemMap.getPermissionNode()).equals("NONE") ? "&9&lPermission Node: &a" + itemMap.getPermissionNode() : ""),
                    (!StringUtils.nullCheck(itemMap.getDisabledWorlds().toString()).equals("NONE") ? "&9&lDisabled Worlds: &a" + disabledList : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getEnabledWorlds().toString()), "NONE") ? "&9&lEnabled Worlds: &a" + enabledList : ""),
                    (!StringUtils.nullCheck(itemMap.getDisabledRegions().toString()).equals("NONE") ? "&9&lDisabled Regions: &a" + disabledRegionList : ""), (!StringUtils.nullCheck(itemMap.getEnabledRegions().toString()).equals("NONE") ? "&9&lEnabled Regions: &a" + enabledRegionList : ""), (!itemMap.getDynamicMaterials().isEmpty() ? "&9&lMaterial Animations: &aYES" : ""),
                    (!itemMap.getDynamicNames().isEmpty() ? "&9&lName Animations: &aYES" : ""), (!itemMap.getDynamicLores().isEmpty() ? "&9&lLore Animations: &aYES" : ""),
                    (!itemMap.getDynamicOwners().isEmpty() || !itemMap.getDynamicTextures().isEmpty() ? "&9&lSkull Animations: &aYES" : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getLimitModes()), "NONE") ? "&9&lLimit-Modes: &a" + itemMap.getLimitModes() : ""),
                    (!StringUtils.nullCheck(itemMap.getProbability() + "&a%").equals("NONE") ? "&9&lProbability: &a" + itemMap.getProbability() + "%" : ""),
                    (!StringUtils.nullCheck(itemMap.getInteractCooldown() + "&7").equals("NONE") ? "&9&lUse-Cooldown: &a" + itemMap.getInteractCooldown() : ""),
                    (!StringUtils.nullCheck(itemMap.getLeatherColor()).equals("NONE") ? "&9&lLeather Color: &a" + itemMap.getLeatherColor() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getLeatherHex()), "NONE") ? "&9&lLeather Color: &a" + itemMap.getLeatherHex() : ""),
                    (!StringUtils.nullCheck(itemMap.getMapImage()).equals("NONE") ? "&9&lMap-Image: &a" + itemMap.getMapImage() : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getChargeColor() + ""), "NONE") ? "&9&lCharge Color: &a" + itemMap.getChargeColor() : ""),
                    (!StringUtils.nullCheck(patternList.toString()).equals("NONE") ? "&9&lBanner Meta: &a" + patternList : ""), (!StringUtils.nullCheck(armorMeta).equals("NONE") ? "&9&lArmor Meta: &a" + armorMeta : ""), (!Objects.equals(StringUtils.nullCheck(potionList.toString()), "NONE") ? "&9&lPotion-Effects: &a" + potionList : ""), (itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty() ? "&9&lRecipe: &aYES" : ""),
                    ((!StringUtils.isEmpty(mobs)) ? "&9&lMobs Drop: &a" + mobs.substring(0, mobs.length() - 2) : ""), ((!StringUtils.isEmpty(blocks)) ? "&9&lBlocks Drop: &a" + blocks.substring(0, blocks.length() - 2) : ""),
                    (!StringUtils.nullCheck(itemMap.getCommandConditions() + "").equals("NONE") ? "&9&lCommand Conditions: &aYES" : ""), (!StringUtils.nullCheck(itemMap.getCommandPermissions() + "").equals("NONE") ? "&9&lCommand Permissions: &aYES" : ""), (!Objects.equals(StringUtils.nullCheck(itemMap.getDisposableConditions() + ""), "NONE") ? "&9&lDisposable Conditions: &aYES" : ""),
                    (!StringUtils.nullCheck(itemMap.getTriggerConditions() + "").equals("NONE") ? "&9&lTrigger Conditions: &aYES" : ""),
                    (!StringUtils.nullCheck(itemMap.getNBTValues() + "").equals("NONE") ? "&9&lNBT Properties: &aYES" : ""), (!StringUtils.nullCheck(itemMap.getContents() + "").equals("NONE") ? "&9&lContents: &aYES" : ""),
                    (!StringUtils.nullCheck(attributeList.toString()).equals("NONE") ? "&9&lAttributes: &a" + attributeList : ""), (!StringUtils.nullCheck(itemMap.getPages() + "").equals("NONE") ? "&9&lBook Pages: &aYES" : ""),
                    (!StringUtils.nullCheck(itemMap.getAuthor()).equals("NONE") ? "&9&lBook Author: &a" + itemMap.getAuthor() : ""), (!StringUtils.nullCheck(itemMap.getSkull()).equals("NONE") ? "&9&lSkull-Owner: &a" + itemMap.getSkull() : ""),
                    (!StringUtils.nullCheck(itemMap.getSkullTexture()).equals("NONE") ? "&9&lSkull-Texture: &a" + (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : ""),
                    (!StringUtils.nullCheck(itemMap.getFireworkType() + "").equals("NONE") ? "&9&lFirework Type: &a" + itemMap.getFireworkType().name() : ""),
                    (!StringUtils.nullCheck(itemMap.getFireworkPower() + "&7").equals("NONE") ? "&9&lFirework Power: &a" + itemMap.getFireworkPower() : ""), (!StringUtils.nullCheck(colorList.toString()).equals("NONE") ? "&9&lFirework Color(s): &a" + colorList : ""),
                    (itemMap.getFireworkTrail() ? "&9&lFirework Trail: &aENABLED" : ""), (itemMap.getFireworkFlicker() ? "&9&lFirework Flicker: &aENABLED" : ""));
        } catch (Exception e) {
            ServerUtils.sendDebugTrace(e);
        }
        if (ServerUtils.hasSpecificUpdate("1_14") && itemMap.getModelData() != null) {
            final int modelData = Integer.parseInt(StringUtils.translateLayout(itemMap.getModelData(), player));
            ItemMeta itemMeta = item.getItemMeta();
            if (modelData != 0 && itemMeta != null) {
                itemMeta.setCustomModelData(modelData);
                item.setItemMeta(itemMeta);
            }
        }
        if (itemMap.getDurability() != null && (itemMap.getData() == null || itemMap.getData() == 0)) {
            ItemMeta itemMeta = item.getItemMeta();
            if (ServerUtils.hasSpecificUpdate("1_13") && itemMeta != null) {
                ((Damageable) itemMeta).setDamage(itemMap.getDurability());
                item.setItemMeta(itemMeta);
            } else {
                LegacyAPI.setDurability(item, itemMap.getDurability());
            }
        }
        if (itemMap.getData() != null && itemMap.getData() > 0) {
            ItemMeta itemMeta = item.getItemMeta();
            if (ServerUtils.hasSpecificUpdate("1_13") && itemMeta != null) {
                ((Damageable) itemMeta).setDamage(itemMap.getData());
                item.setItemMeta(itemMeta);
            } else {
                LegacyAPI.setDurability(item, Short.parseShort(itemMap.getData() + ""));
            }
        }
        if (ItemHandler.isSkull(itemMap.getMaterial())) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMap.getSkull() != null && itemMeta != null) {
                ItemHandler.setSkullOwner(itemMeta, player, StringUtils.translateLayout(itemMap.getSkull(), player));
            } else if (itemMap.getSkullTexture() != null && !itemMap.isHeadDatabase()) {
                try {
                    if (itemMeta != null) {
                        final UUID uuid = UUID.randomUUID();
                        final GameProfile gameProfile = new GameProfile(uuid, uuid.toString().replaceAll("_", "").replaceAll("-", "").substring(0, 16));
                        gameProfile.getProperties().put("textures", new Property("textures", itemMap.getSkullTexture()));
                        Field declaredField = itemMeta.getClass().getDeclaredField("profile");
                        declaredField.setAccessible(true);
                        declaredField.set(itemMeta, gameProfile);
                    }
                } catch (Exception e) {
                    ServerUtils.sendDebugTrace(e);
                }
            } else if (itemMap.isHeadDatabase() && itemMap.getSkullTexture() != null) {
                HeadDatabaseAPI api = new HeadDatabaseAPI();
                ItemStack sk = api.getItemHead(itemMap.getSkullTexture());
                item = (sk != null ? sk : item.clone());
            }
            item.setItemMeta(itemMeta);
        }
        if (ServerUtils.hasSpecificUpdate("1_20") && ItemHandler.isArmor(itemMap.getMaterial().toString())) {
            if (itemMap.getTrimPattern() != null && !itemMap.getTrimPattern().isEmpty()) {
                final Map.Entry<String, String> entry = itemMap.getTrimPattern().entrySet().iterator().next();
                ItemHandler.setArmorTrim(item, entry.getKey(), entry.getValue());
            }
        }
        item.setAmount(itemMap.getCount(player));
        return item;
    }

//  ==========================================================================================================================================================================================================================================================

    /**
     * Checks if the ItemMap is a special item.
     *
     * @param itemMap - The ItemMap to be checked.
     * @return If the ItemMap is a "special" item.
     */
    private static boolean specialItem(final ItemMap itemMap) {
        return itemMap.getMaterial().toString().contains("WRITTEN_BOOK") || itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")
                || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")
                || itemMap.getMaterial().toString().contains("TIPPED_ARROW") || itemMap.getMaterial().toString().contains("LEATHER_") || !ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot");
    }

    /**
     * Attempts to close all online players inventories,
     * if they are found to have the plugin UI open.
     */
    public static void closeMenu() {
        PlayerHandler.forOnlinePlayers(player -> {
            if (isOpen(player) || modifyMenu(player)) {
                player.closeInventory();
            } else if (typingMenu(player)) {
                typingMenu.get(PlayerHandler.getPlayerID(player)).closeQuery(player);
            }
        });
    }

    /**
     * Sets the Player to the Modify Menu.
     *
     * @param bool   - If the Player is in the Menu.
     * @param player - The Player to be set to the Modify Menu.
     */
    public static void setModifyMenu(final boolean bool, final Player player) {
        SchedulerUtils.run(() -> {
            if (bool) {
                modifyMenu.add(PlayerHandler.getPlayerID(player));
            } else if (!modifyMenu.isEmpty()) {
                modifyMenu.remove(PlayerHandler.getPlayerID(player));
            }
        });
    }

    /**
     * Sets the Player to the Typing Menu.
     *
     * @param bool   - If the Player is in the Menu.
     * @param player - The Player to be set to the Typing Menu.
     */
    public static void setTypingMenu(final boolean bool, final Player player, final Interface interFace) {
        SchedulerUtils.run(() -> {
            if (bool) {
                typingMenu.put(PlayerHandler.getPlayerID(player), interFace);
            } else if (!typingMenu.isEmpty()) {
                typingMenu.remove(PlayerHandler.getPlayerID(player));
            }
        });
    }

    /**
     * Checks if the Player is in the Modify Menu.
     *
     * @param player - The Player to be checked.
     * @return If the Player is in the Modify Menu.
     */
    public static boolean modifyMenu(final Player player) {
        return modifyMenu.contains(PlayerHandler.getPlayerID(player));
    }

    /**
     * Checks if the Player is in the typing Menu.
     *
     * @param player - The Player to be checked.
     * @return If the Player is in the typing Menu.
     */
    public static boolean typingMenu(final Player player) {
        return typingMenu.get(PlayerHandler.getPlayerID(player)) != null;
    }

    /**
     * Checks if the Player has the GUI Menu open.
     *
     * @param player - The Player to be checked.
     * @return If the GUI Menu is open.
     */
    public static boolean isOpen(final Player player) {
        if (GUIName == null) {
            GUIName = StringUtils.colorFormat("&7           &0&n ItemJoin Menu");
        }
        synchronized (CompatUtils.class) {
            return player != null && CompatUtils.getInventoryTitle(player).equalsIgnoreCase(StringUtils.colorFormat(GUIName));
        }
    }

//  ==============================================================================================================================================================================================================================================================


}