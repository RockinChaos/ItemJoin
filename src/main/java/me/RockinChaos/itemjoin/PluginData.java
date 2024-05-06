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
package me.RockinChaos.itemjoin;

import com.google.common.collect.ImmutableMap;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.ItemHandler.JSONEvent;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.listeners.Interfaces;
import me.RockinChaos.core.utils.ReflectionUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.MetricsAPI;
import me.RockinChaos.core.utils.api.MetricsAPI.SimplePie;
import me.RockinChaos.core.utils.api.ProtocolAPI;
import me.RockinChaos.core.utils.protocol.ProtocolManager;
import me.RockinChaos.core.utils.sql.Database;
import me.RockinChaos.itemjoin.item.ItemDesigner;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.listeners.*;
import me.RockinChaos.itemjoin.listeners.plugins.ChestSortAPI;
import me.RockinChaos.itemjoin.listeners.plugins.legacy.Legacy_ChestSortAPI;
import me.RockinChaos.itemjoin.listeners.triggers.*;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;

import static me.RockinChaos.core.handlers.PlayerHandler.forOnlinePlayers;
import static me.RockinChaos.core.utils.SchedulerUtils.*;

public class PluginData {

    private static PluginData info;
    private final List<String> nbtInfo = Collections.singletonList("ItemJoin Name");
    private int listLength = 1;
    private int permissionLength = 2;

    /**
     * Gets the instance of the ItemData.
     *
     * @return The ItemData instance.
     */
    public static PluginData getInfo() {
        if (info == null) {
            info = new PluginData();
        }
        return info;
    }

    /**
     * Gets the number of list pages.
     *
     * @return The number of list pages.
     */
    public int getListPages() {
        return this.listLength;
    }

    /**
     * Gets the number of permission pages.
     *
     * @return The number of permission pages.
     */
    public int getPermissionPages() {
        return this.permissionLength;
    }

    /**
     * Gets the items.yml path for the auto generated item and its corresponding Integer.
     *
     * @param i - The Integer to be set as the item path.
     * @return The Integer to be set as the auto generated item path.
     */
    public int getPath(final int i) {
        if (ItemJoin.getCore().getConfig("items.yml").getString("items.item_" + i) != null) {
            return getPath(i + 1);
        }
        return i;
    }

    /**
     * Gets the List of NBT Data specified for items.
     *
     * @return The List of NBT Data specified for items.
     */
    public List<String> getNBTList() {
        return nbtInfo;
    }

    /**
     * Checks if OP and or Creative Bypass is enabled for Prevent actions.
     *
     * @param player - The player being checked for bypass privileges.
     * @return If OP and or Creative is defined for the Prevention actions.
     */
    public boolean isPreventBypass(final Player player) {
        return ((!StringUtils.containsIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Bypass"), "OP") || !player.isOp()) && (!StringUtils.containsIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent.Bypass"), "CREATIVE") || !PlayerHandler.isCreativeMode(player)));
    }

    /**
     * Checks if the Player Bypass is enabled for Prevent actions.
     *
     * @param player      - The player being checked for bypass privileges.
     * @param preventName - The prevention being checked.
     * @return If Prevent Name specified is allowed to bypass.
     */
    public boolean isPreventString(final Player player, final String preventName) {
        final String preventString = ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + preventName);
        return StringUtils.splitIgnoreCase(preventString, "TRUE", ",") || StringUtils.splitIgnoreCase(preventString, player.getWorld().getName(), ",") || StringUtils.splitIgnoreCase(preventString, "ALL", ",") || StringUtils.splitIgnoreCase(preventString, "GLOBAL", ",");
    }

    /**
     * Checks if the specified trigger commands is enabled.
     *
     * @param type - The commands trigger.
     * @return If the trigger type is enabled.
     */
    public boolean triggerEnabled(final String type) {
        final String triggers = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.triggers");
        final String enabledWorlds = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds");
        return StringUtils.containsIgnoreCase(triggers, type)
                && (enabledWorlds != null && !enabledWorlds.equalsIgnoreCase("DISABLED") && !enabledWorlds.equalsIgnoreCase("FALSE"));
    }

    /**
     * Checks if the specified clear items type is enabled.
     *
     * @param type - The item clearing trigger.
     * @return If the clear type is enabled.
     */
    public boolean clearEnabled(final String type) {
        final String clearType = ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items." + type);
        return clearType != null && !clearType.equalsIgnoreCase("DISABLED") && !clearType.equalsIgnoreCase("FALSE");
    }

    /**
     * Gets the Hotbar slot that is defined to be set.
     *
     * @return The Integer hotbar value.
     */
    public int getHotbarSlot() {
        final String heldItemSlot = ItemJoin.getCore().getConfig("config.yml").getString("Settings.HeldItem-Slot");
        if (heldItemSlot != null && !heldItemSlot.equalsIgnoreCase("DISABLED") && StringUtils.isInt(heldItemSlot)) {
            return ItemJoin.getCore().getConfig("config.yml").getInt("Settings.HeldItem-Slot");
        }
        return -1;
    }

    /**
     * Gets the Hotbar Triggers that is defined to be set.
     *
     * @return The String list of hotbar triggers.
     */
    public String getHotbarTriggers() {
        final String triggers = ItemJoin.getCore().getConfig("config.yml").getString("Settings.HeldItem-Triggers");
        if (triggers != null && !triggers.equalsIgnoreCase("DISABLED") && !triggers.equalsIgnoreCase("FALSE")) {
            return triggers;
        }
        return "";
    }

    /**
     * Sets the executed command to be logged or "shown" in the console window.
     *
     * @param logCommand - the command that won't be logged.
     */
    public void setLoggable(final String logCommand) {
        if (!ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Log-Commands")) {
            ArrayList<String> temp = new ArrayList<>();
            if (ItemJoin.getCore().getFilter().getHidden().get("commands-list") != null && !ItemJoin.getCore().getFilter().getHidden().get("commands-list").contains(logCommand)) {
                temp = ItemJoin.getCore().getFilter().getHidden().get("commands-list");
            }
            temp.add(logCommand);
            ItemJoin.getCore().getFilter().addHidden("commands-list", temp);
        }
    }

    /**
     * Sets the JSON String of the Message,
     * sending the message to the player with JSON Formatting.
     *
     * @param message    - The Message being modified.
     * @param configName - The interface being referenced.
     */
    public String getJSONMessage(final String message, final String configName) {
        StringBuilder textBuilder = new StringBuilder("[\"\"");
        Map<Integer, String> JSONBuilder = new HashMap<>();
        String formatLine = message;
        if (ItemHandler.containsJSONEvent(formatLine)) {
            while (ItemHandler.containsJSONEvent(formatLine)) {
                for (JSONEvent jsonType : JSONEvent.values()) {
                    Matcher matchPattern = java.util.regex.Pattern.compile(jsonType.matchType + "(.*?)>").matcher(formatLine);
                    if (matchPattern.find()) {
                        String inputResult = matchPattern.group(1);
                        JSONBuilder.put(JSONBuilder.size(), ((jsonType != JSONEvent.TEXT && jsonType != JSONEvent.COLOR) ? (",\"" + jsonType.event + "\":{\"action\":\""
                                + jsonType.action + "\",\"value\":\"" + inputResult + "\"}") : ("," + (jsonType != JSONEvent.COLOR ? "{" : "") + "\"" + jsonType.action + "\":\"" + inputResult + "\"")));
                        formatLine = formatLine.replace(jsonType.matchType + inputResult + ">", "<JSONEvent>");
                        ItemHandler.safetyCheckURL(configName, jsonType, inputResult);
                    }
                }
            }
            if (!formatLine.isEmpty() && !formatLine.trim().isEmpty()) {
                boolean definingText = false;
                String[] JSONEvents = formatLine.split("<JSONEvent>");
                if (!(org.apache.commons.lang.StringUtils.countMatches(formatLine, "<JSONEvent>") <= JSONEvents.length)) {
                    StringBuilder adjustLine = new StringBuilder();
                    for (String s : formatLine.split("JSONEvent>")) {
                        adjustLine.append(s).append("JSONEvent> ");
                    }
                    JSONEvents = adjustLine.toString().split("<JSONEvent>");
                }
                for (int i = 0; i < JSONEvents.length; i++) {
                    if (!JSONEvents[i].isEmpty() && !JSONEvents[i].trim().isEmpty()) {
                        textBuilder.append((i == 0) ? "," : "},").append("{\"").append("text").append("\":\"").append(JSONEvents[i]).append((JSONBuilder.get(i) != null
                                && JSONBuilder.get(i).contains("\"text\"")) ? "\"}" : "\"").append(JSONBuilder.get(i) != null ? JSONBuilder.get(i) : "");
                    } else if (JSONBuilder.get(i) != null) {
                        if (JSONBuilder.get(i).contains("\"text\"") && !definingText) {
                            textBuilder.append(JSONBuilder.get(i));
                            definingText = true;
                        } else if (JSONBuilder.get(i).contains("\"text\"") && definingText) {
                            textBuilder.append("}").append(JSONBuilder.get(i));
                            definingText = false;
                        } else {
                            textBuilder.append(JSONBuilder.get(i));
                        }
                    }
                }
                textBuilder.append("}");
            }
        } else if (message.contains("raw:")) {
            return message.replace("raw: ", "").replace("raw:", "");
        } else {
            if (formatLine.contains("&#")) {
                String[] hexFormat = formatLine.split("&#");
                for (String line : hexFormat) {
                    if (!line.isEmpty()) {
                        if (line.length() > 6 && line.substring(0, 6).matches("^[0-9a-fA-F]+$")) {
                            textBuilder.append("," + "{\"text\":\"").append(line.substring(6)).append("\",").append("\"color\":\"#").append(line, 0, 6).append("\"}");
                        } else {
                            textBuilder.append("," + "{\"text\":\"").append(line).append("\"}");
                        }
                    }
                }
            } else {
                textBuilder.append("," + "{\"text\":\"").append(formatLine).append("\"}");
            }
        }
        return textBuilder + "]";
    }

    /**
     * Removes the Crafting Items from ALL Online Players.
     *
     * @param saveCrafting - If the Crafting Items should be saved to be returned later.
     */
    public void purgeCraftItems(final boolean saveCrafting) {
        forOnlinePlayers(player -> {
            if (saveCrafting) {
                final Inventory inventory = ItemHandler.getCraftInventory(player);
                if (inventory != null) {
                    ItemJoin.getCore().getSQL().saveData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", ItemHandler.serializeInventory(inventory)));
                }
            }
            ItemHandler.removeCraftItems(player);
        });
    }

    /**
     * Restores the crafting items for the specified player.
     *
     * @param player - The player having their crafting items restored.
     * @param type   - The trigger type.
     */
    public void restoreCraftItems(final Player player, final TriggerType type) {
        if (!type.equals(TriggerType.QUIT)) {
            final DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", ""));
            final Inventory inventory = (dataObject != null ? ItemHandler.deserializeInventory(dataObject.getInventory64()) : null);
            if (dataObject != null && ItemHandler.restoreCraftItems(player, inventory)) {
                ItemJoin.getCore().getSQL().removeData(dataObject);
            }
        }
    }

    /**
     * Saves any existing players that are on cooldown for each item.
     */
    public void saveCooldowns() {
        for (final ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
            for (final String key : itemMap.getPlayersOnCooldown().keySet()) {
                if (System.currentTimeMillis() - itemMap.getPlayersOnCooldown().get(key) <= itemMap.getCommandCooldown() * 1000) {
                    ItemJoin.getCore().getSQL().saveData(new DataObject(Table.ON_COOLDOWN, key, "GLOBAL", itemMap.getConfigName(), itemMap.getCommandCooldown().toString(), itemMap.getPlayersOnCooldown().get(key).toString()));
                }
            }
        }
    }

    /**
     * Checks if the Player has items enabled.
     *
     * @param player - The player being checked.
     * @param item   - The item being checked.
     * @return If the Player has items enabled.
     */
    public boolean isEnabled(final Player player, final String item) {
        final DataObject dataItem = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), player.getWorld().getName(), item, Boolean.toString(true)));
        final DataObject dataPlayer = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), player.getWorld().getName(), "ALL", Boolean.toString(true)));
        final DataObject dataGlobal = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), "Global", "ALL", Boolean.toString(true)));
        final DataObject dataALL = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, null, "Global", "ALL", Boolean.toString(true)));
        final boolean enabled = (((dataItem != null ? Boolean.parseBoolean(dataItem.getEnabled()) : (dataPlayer != null ? Boolean.parseBoolean(dataPlayer.getEnabled()) : ((dataGlobal != null ? Boolean.parseBoolean(dataGlobal.getEnabled()) : (dataALL == null || Boolean.parseBoolean(dataALL.getEnabled()))))))));
        if (!enabled) {
            ServerUtils.logDebug("{ItemMap} " + player.getName() + " will not receive any items, they have custom items are disabled.");
        }
        return enabled;
    }

    /**
     * Sets the number of list and permission pages.
     */
    public void setPages() {
        runAsync(() -> {
            final ConfigurationSection itemsPath = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection("items");
            final int customItems = (itemsPath != null ? itemsPath.getKeys(false).size() : 0);
            if (customItems > 15) {
                this.permissionLength = (int) Math.ceil((double) customItems / 15) + 1;
            }
            int listCount = 0;
            for (final World world : Bukkit.getWorlds()) {
                final List<String> listItems = new ArrayList<>();
                listCount++;
                for (final ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                    if (!listItems.contains(itemMap.getConfigName()) && itemMap.inWorld(world)) {
                        listCount++;
                        listItems.add(itemMap.getConfigName());
                    }
                }
            }
            if (listCount > 15) {
                this.listLength = (int) Math.ceil((double) listCount / 15);
            }
        });
    }

    /**
     * Gets all active database data for the plugin directly from the SQL database connection.
     *
     * @return The Map of database data.
     */
    public Map<String, List<Object>> getDatabaseData() {
        final Map<String, List<Object>> databaseData = new HashMap<>();
        for (final Table tableEnum : Table.values()) {
            final String table = tableEnum.tableName();
            final List<HashMap<String, String>> selectTable = Database.getDatabase().queryTableData("SELECT * FROM " + ItemJoin.getCore().getData().getTablePrefix() + table, tableEnum.headers().replace("`", ""));
            if (!selectTable.isEmpty()) {
                for (final HashMap<String, String> sl1 : selectTable) {
                    DataObject dataObject = null;
                    if (tableEnum.equals(Table.FIRST_JOIN)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Item_Name"));
                    } else if (tableEnum.equals(Table.FIRST_WORLD)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"));
                    } else if (tableEnum.equals(Table.IP_LIMITS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("IP_Address"));
                    } else if (tableEnum.equals(Table.FIRST_COMMANDS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Command_String"));
                    } else if (tableEnum.equals(Table.ENABLED_PLAYERS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("isEnabled"));
                    } else if (tableEnum.equals(Table.RETURN_ITEMS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Region_Name"), sl1.get("Inventory64"));
                    } else if (tableEnum.equals(Table.RETURN_CRAFTITEMS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Inventory64"));
                    } else if (tableEnum.equals(Table.RETURN_SWITCH_ITEMS)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Inventory64"));
                    } else if (tableEnum.equals(Table.ON_COOLDOWN)) {
                        dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("Cooldown"), sl1.get("Duration"));
                    } else if (tableEnum.equals(Table.MAP_IDS)) {
                        dataObject = new DataObject(tableEnum, null, null, sl1.get("Map_IMG"), sl1.get("Map_ID"));
                    }
                    final List<Object> dataSet = (databaseData.get(table) != null ? databaseData.get(table) : new ArrayList<>());
                    dataSet.add(dataObject);
                    databaseData.put(table, dataSet);
                }
            }
        }
        return databaseData;
    }

    /**
     * Warns the server owner if ExploitFixer is misconfigured and is conflicting with the plugin.
     */
    private void warnExploitUsers() {
        if (ItemJoin.getCore().getDependencies().exploitFixerEnabled()) {
            final FileConfiguration exploitConfig = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ExploitFixer")).getConfig();
            if (exploitConfig.getString("itemsfix.enabled") != null && exploitConfig.getBoolean("itemsfix.enabled")) {
                ServerUtils.logSevere("{DependAPI} ExploitFixer has been detected with itemsfix enabled! ItemJoin and other custom items plugins WILL BREAK with this feature enabled."
                        + "Please set itemsfix.enabled to false in the config.yml of ExploitFixer to resolve this conflict.");
            }
        }
    }

    /**
     * Registers the command executors and events.
     */
    public void registerEvents() {
        Objects.requireNonNull(ItemJoin.getCore().getPlugin().getCommand("itemjoin")).setExecutor(new ChatExecutor());
        Objects.requireNonNull(ItemJoin.getCore().getPlugin().getCommand("itemjoin")).setTabCompleter(new ChatTab());
        ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interfaces(), ItemJoin.getCore().getPlugin());
        ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interface(), ItemJoin.getCore().getPlugin());
    }

    /**
     * Registers new instances of the plugin classes.
     *
     * @param silent - If any messages should be sent.
     */
    public void registerClasses(final boolean silent) {
        final boolean isRunning = ItemJoin.getCore().isStarted();
        ServerUtils.clearErrorStatements();
        ItemJoin.getCore().getData().refresh();
        ItemJoin.getCore().getFilter().refresh();
        ItemJoin.getCore().getBungee().refresh();
        ItemJoin.getCore().getData().setStarted(false);
        ItemJoin.getCore().getData().setPluginPrefix("&7[&eItemJoin&7]");
        ItemJoin.getCore().getData().setConfig(ImmutableMap.of("config.yml", 8, "items.yml", 8, "lang.yml", 8));
        ItemJoin.getCore().getData().setLanguages(Arrays.asList("English", "Spanish", "Russian", "French", "Dutch", "Portuguese", "Italian", "Chinese", "SimplifiedChinese", "TraditionalChinese", "TwChinese", "CnChinese"));
        ItemJoin.getCore().getData().setPermissions(Arrays.asList("itemjoin.use", "itemjoin.dump", "itemjoin.reload", "itemjoin.updates", "itemjoin.upgrade", "itemjoin.menu", "itemjoin.purge", "itemjoin.get", "itemjoin.get.others",
                "itemjoin.remove", "itemjoin.remove.others", "itemjoin.disable", "itemjoin.disable.others", "itemjoin.enable", "itemjoin.enable.others", "itemjoin.list", "itemjoin.query"));
        ItemJoin.getCore().getData().setAlterTables(this.getAlterTables());
        ItemJoin.getCore().getData().setCreateTables(this.getCreateTables());
        ItemJoin.getCore().getData().setUpdateConfig(this.generateItemsFile(), "items.yml");
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        // -=-=-=-=-=-=   Copy The Configuration Files to Disk and Load them into Memory.   =-=-=-=-=-=- //
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        ItemJoin.getCore().getConfiguration().reloadFiles(); // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //
        ItemJoin.getCore().getData().setCheckforUpdates(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.CheckforUpdates"));
        ItemJoin.getCore().getData().setDebug(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.Debugging"));
        ItemJoin.getCore().getData().setIgnoreErrors(ItemJoin.getCore().getConfig("config.yml").getBoolean("General.ignoreErrors"));
        ItemJoin.getCore().getData().setDataTags(ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.DataTags"));
        ItemJoin.getCore().getData().setSQL(ItemJoin.getCore().getConfig("config.yml").getString("Database.MySQL") != null && ItemJoin.getCore().getConfig("config.yml").getBoolean("Database.MySQL"));
        ItemJoin.getCore().getData().setTablePrefix(ItemJoin.getCore().getConfig("config.yml").getString("Database.prefix") != null ? ItemJoin.getCore().getConfig("config.yml").getString("Database.prefix") : "ij_");
        ItemJoin.getCore().getData().setSQLHost(ItemJoin.getCore().getConfig("config.yml").getString("Database.host"));
        ItemJoin.getCore().getData().setSQLPort(ItemJoin.getCore().getConfig("config.yml").getString("Database.port"));
        ItemJoin.getCore().getData().setSQLUser(ItemJoin.getCore().getConfig("config.yml").getString("Database.user"));
        ItemJoin.getCore().getData().setSQLPass(ItemJoin.getCore().getConfig("config.yml").getString("Database.pass"));
        ItemJoin.getCore().getData().setSQLDatabase(ItemJoin.getCore().getConfig("config.yml").getString("Database.database"));
        ItemJoin.getCore().getDependencies().refresh();
        runAsync(() -> {
            final ConfigurationSection itemsPath = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection("items");
            int customItems = (itemsPath != null ? itemsPath.getKeys(false).size() : 0);
            final String compileVersion = Objects.requireNonNull(YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(ItemJoin.getCore().getPlugin().getResource("plugin.yml")))).getString("nms-version")).split("-")[0].replace(".", "_");
            final String serverVersion = ServerUtils.getVersion();
            if (!silent) {
                if (StringUtils.containsIgnoreCase(compileVersion, "spigot_version")) {
                    ServerUtils.logInfo("Running a developer version ... skipping NMS check.");
                } else if (!compileVersion.equalsIgnoreCase(serverVersion) && ServerUtils.hasPreciseUpdate(compileVersion)) {
                    ServerUtils.logSevere("Detected a unsupported version of Minecraft!");
                    ServerUtils.logSevere("Attempting to run in NMS compatibility mode...");
                    ServerUtils.logSevere("Things may not work as expected, please check for plugin updates.");
                }
                ItemJoin.getCore().getDependencies().sendUtilityDepends();
                this.warnExploitUsers();
                ServerUtils.logInfo(customItems + " Custom item(s) loaded!");
            }
            this.registerPrevent();
            if (isRunning) {
                if (ItemJoin.getCore().getSQL().refresh()) {
                    ItemJoin.getCore().getData().setDatabaseData(this.getDatabaseData());
                    {
                        ItemJoin.getCore().getSQL().load();
                    }
                }
            } else {
                ItemJoin.getCore().getSQL();
                {
                    ItemJoin.getCore().getData().setDatabaseData(this.getDatabaseData());
                    {
                        ItemJoin.getCore().getSQL().load();
                    }
                }
            }
        });
        {
            new ItemDesigner();
            {
                runSingleAsync(() -> {
                    ItemJoin.getCore().getData().setStarted(true);
                    forOnlinePlayers(player -> ItemUtilities.getUtilities().setStatistics(player));
                    this.setPages();
                });
                {
                    runAsyncLater(100L, () -> {
                        final MetricsAPI metrics = new MetricsAPI(ItemJoin.getCore().getPlugin(), 4115);
                        metrics.addCustomChart(new SimplePie("items", () -> ItemUtilities.getUtilities().getItems().size() + " "));
                        metrics.addCustomChart(new SimplePie("itemPermissions", () -> ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items") ? "True" : "False"));
                        ItemJoin.getCore().getDependencies().addCustomCharts(metrics);
                        ServerUtils.sendErrorStatements(null);
                    });
                }
            }
        }
    }

    /**
     * Registers the GLOBAL prevention actions.
     */
    private void registerPrevent() {
        if (((this.clearEnabled("Join") || this.triggerEnabled("Join") || this.triggerEnabled("First-Join")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "JOIN")) && StringUtils.isRegistered(PlayerJoin.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getCore().getPlugin());
        }
        if (((this.clearEnabled("World-Switch") || this.triggerEnabled("World-Switch")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "WORLD-SWITCH")) && StringUtils.isRegistered(WorldSwitch.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getCore().getPlugin());
        }
        if (((this.clearEnabled("Quit") || this.triggerEnabled("Quit")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "QUIT")) && StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getCore().getPlugin());
        }
        if ((StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "RESPAWN") || this.triggerEnabled("Respawn")) && StringUtils.isRegistered(Respawn.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getCore().getPlugin());
        }
        if (StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "GAMEMODE-SWITCH") && StringUtils.isRegistered(LimitSwitch.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getCore().getPlugin());
        }
        if ((this.clearEnabled("Region-Enter") || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "REGION-ENTER")) && StringUtils.isRegistered(PlayerGuard.class.getSimpleName()) && ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getCore().getPlugin());
        }
        if (StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "REGION-LEAVE") && StringUtils.isRegistered(PlayerGuard.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getCore().getPlugin());
        }
        if (StringUtils.isRegistered(PlayerLogin.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerLogin(), ItemJoin.getCore().getPlugin());
        }
        if ((!StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Chat"), "FALSE", ",") && !StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Chat"), "DISABLED", ","))) {
            if (StringUtils.isRegistered(Chat.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Chat(), ItemJoin.getCore().getPlugin());
            }
        }
        if ((!StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Pickups"), "FALSE", ",") && !StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Pickups"), "DISABLED", ","))) {
            //noinspection ConstantValue
            if (ServerUtils.hasSpecificUpdate("1_12") && ReflectionUtils.getBukkitClass("event.entity.EntityPickupItemEvent") != null && StringUtils.isRegistered(Pickups.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.getCore().getPlugin());
            } else {
                LegacyAPI.registerPickups();
            }
        }
        if ((!StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "itemMovement"), "FALSE", ",") && !StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "itemMovement"), "DISABLED", ","))) {
            if (StringUtils.isRegistered(Clicking.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Clicking(), ItemJoin.getCore().getPlugin());
                if (!ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolManager.isDead()) {
                    ProtocolManager.handleProtocols();
                } else if (ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolAPI.isHandling()) {
                    ProtocolAPI.handleProtocols();
                }
            }
        }
        if ((!StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Self-Drops"), "FALSE", ",") && !StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Self-Drops"), "DISABLED", ","))
                || (!StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Death-Drops"), "FALSE", ",") && !StringUtils.splitIgnoreCase(ItemJoin.getCore().getConfig("config.yml").getString("Prevent." + "Death-Drops"), "DISABLED", ","))) {
            if (StringUtils.isRegistered(Drops.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getCore().getPlugin());
            }
        }
    }

    /**
     * Registers Events that are utilized by the specified ItemMap.
     *
     * @param itemMap - The ItemMap that needs its events registered.
     */
    @SuppressWarnings("ConstantValue")
    public void registerListeners(final ItemMap itemMap) {
        final String enabledWorlds = ItemJoin.getCore().getConfig("config.yml").getString("Active-Commands.enabled-worlds");
        if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnJoin()) || itemMap.isAutoRemove() || (enabledWorlds != null && (!enabledWorlds.equalsIgnoreCase("DISABLED") && !enabledWorlds.equalsIgnoreCase("FALSE")))) && StringUtils.isRegistered(PlayerJoin.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getCore().getPlugin());
        }
        if ((((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnRespawn()) || itemMap.isDeathKeepable()) || itemMap.isAutoRemove()) && StringUtils.isRegistered(Respawn.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getCore().getPlugin());
        }
        if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnTeleport()) || itemMap.isAutoRemove()) && StringUtils.isRegistered(PlayerTeleport.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerTeleport(), ItemJoin.getCore().getPlugin());
        }
        if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnWorldSwitch()) || itemMap.isAutoRemove()) && StringUtils.isRegistered(WorldSwitch.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getCore().getPlugin());
        }
        if (!itemMap.isGiveOnDisabled() && (itemMap.isGiveOnRegionEnter() || itemMap.isGiveOnRegionLeave() || itemMap.isGiveOnRegionAccess() || itemMap.isGiveOnRegionEgress())
                && StringUtils.isRegistered(PlayerGuard.class.getSimpleName()) && ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getCore().getPlugin());
        }
        if (!itemMap.isGiveOnDisabled() && itemMap.isUseOnLimitSwitch() && StringUtils.isRegistered(LimitSwitch.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getCore().getPlugin());
        }
        if ((itemMap.isAnimated() || itemMap.isDynamic()) && StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.mobsDrop() && StringUtils.isRegistered(Entities.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Entities(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.blocksDrop() && StringUtils.isRegistered(Breaking.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Breaking(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.isCraftingItem() && StringUtils.isRegistered(Crafting.class.getSimpleName())) {
            PlayerHandler.cycleCrafting();
            runLater(40L, () -> {
                forOnlinePlayers(player -> this.restoreCraftItems(player, TriggerType.DEFAULT));
                if (!ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolManager.isDead()) {
                    ProtocolManager.handleProtocols();
                } else if (ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolAPI.isHandling()) {
                    ProtocolAPI.handleProtocols();
                }
            });
            if (StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getCore().getPlugin());
            }
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Crafting(), ItemJoin.getCore().getPlugin());
        }
        if ((itemMap.isMovement() || itemMap.isStackable() || itemMap.isEquip() || itemMap.isInventoryClose())) {
            if (StringUtils.isRegistered(Clicking.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Clicking(), ItemJoin.getCore().getPlugin());
                if (!ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolManager.isDead()) {
                    ProtocolManager.handleProtocols();
                } else if (ItemJoin.getCore().getDependencies().protocolEnabled() && ProtocolAPI.isHandling()) {
                    ProtocolAPI.handleProtocols();
                }
            }
            if (ItemJoin.getCore().getDependencies().chestSortEnabled()) {
                boolean newAPI = true;
                try {
                    ReflectionUtils.getCanonicalClass("de.jeff_media.chestsort.api.ChestSortEvent");
                } catch (IllegalArgumentException e) {
                    ReflectionUtils.getCanonicalClass("de.jeff_media.ChestSortAPI.ChestSortEvent");
                    newAPI = false;
                }
                if (newAPI && StringUtils.isRegistered(ChestSortAPI.class.getSimpleName())) {
                    ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new ChestSortAPI(), ItemJoin.getCore().getPlugin());
                } else if (!newAPI && StringUtils.isRegistered(Legacy_ChestSortAPI.class.getSimpleName())) {
                    ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Legacy_ChestSortAPI(), ItemJoin.getCore().getPlugin());
                }
            }
        }
        if (ServerUtils.hasSpecificUpdate("1_12") && itemMap.isStackable() && StringUtils.isRegistered(Stackable.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Stackable(), ItemJoin.getCore().getPlugin());
        } else if (itemMap.isStackable()) {
            LegacyAPI.registerStackable();
        }
        if (itemMap.isSplittable() && StringUtils.isRegistered(Splittable.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Splittable(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.isNotHat() && StringUtils.isRegistered(Processes.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Processes(), ItemJoin.getCore().getPlugin());
        }
        if ((itemMap.isDeathKeepable() || itemMap.isDeathDroppable() || itemMap.isSelfDroppable()) && StringUtils.isRegistered(Drops.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.getCommands() != null && itemMap.getCommands().length != 0) {
            if (StringUtils.isRegistered(Commands.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Commands(), ItemJoin.getCore().getPlugin());
            }
        }
        if ((itemMap.isCancelEvents() || itemMap.isSelectable() || itemMap.getInteractCooldown() != 0)) {
            if (StringUtils.isRegistered(Interact.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Interact(), ItemJoin.getCore().getPlugin());
            }
        }
        if ((itemMap.isPlaceable() || itemMap.isCountLock()) && StringUtils.isRegistered(Placement.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getCore().getPlugin());
        }
        if ((itemMap.isCountLock() || itemMap.isTeleport()) && StringUtils.isRegistered(Projectile.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Projectile(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.isCountLock() || itemMap.isCustomConsumable()) {
            if (ServerUtils.hasSpecificUpdate("1_11") && StringUtils.isRegistered(Consumes.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Consumes(), ItemJoin.getCore().getPlugin());
            } else {
                LegacyAPI.registerConsumes();
            }
        }
        if (((itemMap.isItemRepairable() || itemMap.isItemCraftable()) || (itemMap.getIngredients() != null || !itemMap.getIngredients().isEmpty())) && StringUtils.isRegistered(Recipes.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getCore().getPlugin());
        }
        if (itemMap.isItemStore() || itemMap.isItemModify()) {
            if (StringUtils.isRegistered(Storable.class.getSimpleName())) {
                ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Storable(), ItemJoin.getCore().getPlugin());
            }
        }
        if (itemMap.isMovement() && ServerUtils.hasSpecificUpdate("1_9") && ReflectionUtils.getBukkitClass("event.player.PlayerSwapHandItemsEvent") != null && StringUtils.isRegistered(Offhand.class.getSimpleName())) {
            ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new Offhand(), ItemJoin.getCore().getPlugin());
        }
    }

    /**
     * Generates the Data for the FileConfiguration that is specific
     * to the current Server version.
     *
     * @return The Runnable to generate the items file.
     */
    public Runnable generateItemsFile() {
        return () -> {
            final File itemsFile = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
            final FileConfiguration itemsData = YamlConfiguration.loadConfiguration(itemsFile);
            if (ServerUtils.hasSpecificUpdate("1_14")) {
                itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.map-item.id", "FILLED_MAP");
                itemsData.set("items.gamemode-token.id", "FIREWORK_STAR");
                itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.bungeecord-item.id", "PURPLE_STAINED_GLASS");
                itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.animated-panes.id.1", "<delay:40>BLACK_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.2", "<delay:20>BLUE_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.3", "<delay:20>GREEN_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.4", "<delay:20>MAGENTA_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.5", "<delay:20>ORANGE_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.6", "<delay:20>RED_STAINED_GLASS_PANE");
                itemsData.set("items.banner-item.id", "WHITE_BANNER");
                itemsData.set("items.animated-sign.id", "OAK_SIGN");
                itemsData.set("items.skull-item.id", "PLAYER_HEAD");
                itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
                itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
                itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
                itemsData.set("items.firework-item.id", "FIREWORK_ROCKET");
                itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, LIGHT_GRAY, GREEN");
                itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
                itemsData.set("items.profile-item.id", "PLAYER_HEAD");
                itemsData.set("items.random-pane-1.id", "YELLOW_STAINED_GLASS_PANE");
                itemsData.set("items.random-pane-2.id", "BLUE_STAINED_GLASS_PANE");
                itemsData.set("items.random-pane-3.id", "PINK_STAINED_GLASS_PANE");
            } else if (ServerUtils.hasSpecificUpdate("1_13")) {
                itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.map-item.id", "FILLED_MAP");
                itemsData.set("items.gamemode-token.id", "FIREWORK_STAR");
                itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.bungeecord-item.id", "PURPLE_STAINED_GLASS");
                itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
                itemsData.set("items.animated-panes.id.1", "<delay:40>BLACK_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.2", "<delay:20>BLUE_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.3", "<delay:20>GREEN_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.4", "<delay:20>MAGENTA_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.5", "<delay:20>ORANGE_STAINED_GLASS_PANE");
                itemsData.set("items.animated-panes.id.6", "<delay:20>RED_STAINED_GLASS_PANE");
                itemsData.set("items.banner-item.id", "WHITE_BANNER");
                itemsData.set("items.animated-sign.id", "SIGN");
                itemsData.set("items.skull-item.id", "PLAYER_HEAD");
                itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
                itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
                itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
                itemsData.set("items.firework-item.id", "FIREWORK_ROCKET");
                itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, LIGHT_GRAY, GREEN");
                itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
                itemsData.set("items.profile-item.id", "PLAYER_HEAD");
                itemsData.set("items.random-pane-1.id", "YELLOW_STAINED_GLASS_PANE");
                itemsData.set("items.random-pane-2.id", "BLUE_STAINED_GLASS_PANE");
                itemsData.set("items.random-pane-3.id", "PINK_STAINED_GLASS_PANE");
            } else if (ServerUtils.hasSpecificUpdate("1_9")) {
                itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_PLING");
                itemsData.set("items.map-item.id", "MAP");
                itemsData.set("items.gamemode-token.id", "FIREWORK_CHARGE");
                itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_PLING");
                itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
                itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_PLING");
                itemsData.set("items.animated-panes.id.1", "<delay:40>STAINED_GLASS_PANE:15");
                itemsData.set("items.animated-panes.id.2", "<delay:20>STAINED_GLASS_PANE:11");
                itemsData.set("items.animated-panes.id.3", "<delay:20>STAINED_GLASS_PANE:13");
                itemsData.set("items.animated-panes.id.4", "<delay:20>STAINED_GLASS_PANE:2");
                itemsData.set("items.animated-panes.id.5", "<delay:20>STAINED_GLASS_PANE:1");
                itemsData.set("items.animated-panes.id.6", "<delay:20>STAINED_GLASS_PANE:14");
                itemsData.set("items.banner-item.id", "BANNER");
                itemsData.set("items.animated-sign.id", "SIGN");
                itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
                itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
                itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
                itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
                itemsData.set("items.firework-item.id", "FIREWORK");
                itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
                itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
                itemsData.set("items.profile-item.id", "SKULL_ITEM:3");
                itemsData.set("items.random-pane-1.id", "STAINED_GLASS_PANE:4");
                itemsData.set("items.random-pane-2.id", "STAINED_GLASS_PANE:4");
                itemsData.set("items.random-pane-3.id", "STAINED_GLASS_PANE:6");
            } else {
                itemsData.set("items.devine-item.commands-sound", "NOTE_PLING");
                itemsData.set("items.devine-item.attributes", null);
                itemsData.set("items.map-item.id", "MAP");
                itemsData.set("items.gamemode-token.id", "FIREWORK_CHARGE");
                itemsData.set("items.gamemode-token.commands-sound", "NOTE_PLING");
                itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
                itemsData.set("items.bungeecord-item.commands-sound", "NOTE_PLING");
                itemsData.set("items.animated-panes.id.1", "<delay:40>STAINED_GLASS_PANE:15");
                itemsData.set("items.animated-panes.id.2", "<delay:20>STAINED_GLASS_PANE:11");
                itemsData.set("items.animated-panes.id.3", "<delay:20>STAINED_GLASS_PANE:13");
                itemsData.set("items.animated-panes.id.4", "<delay:20>STAINED_GLASS_PANE:2");
                itemsData.set("items.animated-panes.id.5", "<delay:20>STAINED_GLASS_PANE:1");
                itemsData.set("items.animated-panes.id.6", "<delay:20>STAINED_GLASS_PANE:14");
                itemsData.set("items.banner-item.id", "BANNER");
                itemsData.set("items.animated-sign.id", "SIGN");
                itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
                itemsData.set("items.potion-arrow.id", "ARROW");
                itemsData.set("items.potion-arrow.name", "&fArrow");
                itemsData.set("items.potion-arrow.potion-effect", null);
                itemsData.set("items.firework-item.id", "FIREWORK");
                itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
                itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, INVISIBILITY:1:410, REGENERATION:1:160");
                itemsData.set("items.profile-item.id", "SKULL_ITEM:3");
                itemsData.set("items.random-pane-1.id", "STAINED_GLASS_PANE:4");
                itemsData.set("items.random-pane-2.id", "STAINED_GLASS_PANE:3");
                itemsData.set("items.random-pane-3.id", "STAINED_GLASS_PANE:6");
                itemsData.set("items.offhand-item", null);
            }
            try {
                itemsData.save(itemsFile);
                ItemJoin.getCore().getConfiguration().getSource("items.yml");
                ItemJoin.getCore().getConfig("items.yml").options().copyDefaults(false);
            } catch (Exception e) {
                ItemJoin.getCore().getPlugin().getServer().getLogger().severe("Could not save important data changes to the data file items.yml!");
                ServerUtils.sendSevereTrace(e);
            }
        };
    }

    /**
     * Creates the data set for the SQL database.
     *
     * @return The Runnable to create the data set.
     */
    public Runnable getCreateTables() {
        return () -> {
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "ip_limits (`World_Name` varchar(1000), `IP_Address` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "first_commands (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Command_String` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "return_items (`World_Name` varchar(1000), `Region_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(4000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "return_craftitems (`Player_UUID` varchar(1000), `Inventory64` varchar(4000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "return_switch_items (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(4000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "on_cooldown (`World_Name` varchar(1000), `Item_Name` varchar(1000), `Player_UUID` varchar(1000), `Cooldown` varchar(1000), `Duration` varchar(1000), `Time_Stamp` varchar(1000));");
            Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ItemJoin.getCore().getData().getTablePrefix() + "map_ids (`Map_IMG` varchar(1000), `Map_ID` varchar(1000), `Time_Stamp` varchar(1000));");
        };
    }

    /**
     * Alters the existing data set to fit the new SQL standards.
     *
     * @return The Runnable to alter the existing data set.
     */
    public Runnable getAlterTables() {
        return () -> {
            if (Database.getDatabase().tableExists(ItemJoin.getCore().getData().getTablePrefix() + "first_join") && Database.getDatabase().columnExists("SELECT Player_Name FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_join")) {
                Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_join_backup (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
                Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "first_join_backup SELECT Player_UUID,Item_Name,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_join;");
                Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_join");
                Database.getDatabase().executeStatement("CREATE TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
                Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "first_join SELECT Player_UUID,Item_Name,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_join_backup;");
                Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_join_backup");
            }
            if (Database.getDatabase().tableExists(ItemJoin.getCore().getData().getTablePrefix() + "first_world") && Database.getDatabase().columnExists("SELECT Player_Name FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_world")) {
                Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_world_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
                Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "first_world_backup SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_world;");
                Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_world");
                Database.getDatabase().executeStatement("CREATE TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
                Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "first_world SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "first_world_backup;");
                Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "first_world_backup");
            }
            if (Database.getDatabase().tableExists(ItemJoin.getCore().getData().getTablePrefix() + "enabled_players")) {
                if (!Database.getDatabase().columnExists("SELECT Item_Name FROM " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players")) {
                    Database.getDatabase().executeStatement("ALTER TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players" + " ADD Item_Name varchar(1000);");
                }
                if (Database.getDatabase().columnExists("SELECT Player_Name FROM " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players")) {
                    Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
                    Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players_backup SELECT World_Name,Player_UUID,Item_Name,isEnabled,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players;");
                    Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players");
                    Database.getDatabase().executeStatement("CREATE TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
                    Database.getDatabase().executeStatement("INSERT INTO " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players SELECT World_Name,Player_UUID,Item_Name,isEnabled,Time_Stamp FROM " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players_backup;");
                    Database.getDatabase().executeStatement("DROP TABLE " + ItemJoin.getCore().getData().getTablePrefix() + "enabled_players_backup");
                }
            }
        };
    }

    /**
     * Softly reloads the configuration files.
     * Usefully when editing booleans.
     */
    public void softReload() {
        ItemJoin.getCore().getConfiguration().reloadFiles();
        this.registerPrevent();
    }

    /**
     * Harshly reloads the configuration files and registers events
     * Usefully when reloading the plugin or deleting items.
     *
     * @param silent - If any messages should be sent.
     */
    public void hardReload(final boolean silent) {
        PluginData.getInfo().saveCooldowns();
        ItemUtilities.getUtilities().closeAnimations();
        ItemUtilities.getUtilities().delToggleCommands();
        ItemUtilities.getUtilities().clearItems();
        {
            ItemJoin.getCore().getConfiguration().reloadFiles();
            {
                run(() -> PluginData.getInfo().registerClasses(silent));
            }
        }
    }
}