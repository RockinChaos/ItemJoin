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

import com.google.common.io.Files;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PermissionsHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.ChatComponent.ClickAction;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.core.utils.api.PasteAPI;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.menus.Menu;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatExecutor implements CommandExecutor {

    private final HashMap<String, Boolean> confirmationRequests = new HashMap<>();

    /**
     * Called when the CommandSender executes a command.
     *
     * @param sender  - Source of the command.
     * @param command - Command which was executed.
     * @param label   - Alias of the command which was used.
     * @param args    - Passed command arguments.
     * @return true if the command is valid.
     */
    @Override
    public boolean onCommand(@Nonnull final CommandSender sender, @Nonnull final Command command, @Nonnull final String label, @Nonnull final String[] args) {
        final Execute executor = this.matchExecutor(args);
        if (Execute.DEFAULT.accept(sender, args, 0)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, ("&aItemJoin v" + ItemJoin.getCore().getPlugin().getDescription().getVersion() + "&e by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help&a for the help menu.", "&eClick to View the Help Menu.", "/itemjoin help", ClickAction.RUN_COMMAND);
        } else if (Execute.HELP.accept(sender, args, 1)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, ("&aItemJoin v" + ItemJoin.getCore().getPlugin().getDescription().getVersion() + "&e by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu.", "&aExecuting this command shows this help menu!", "/itemjoin help", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Dump &7- &eGets a debug link for support.", "&aSends a paste link of their configuration files. \n&cThis should be sent to the plugin developer and NOT SHARED PUBLICLY.", "/itemjoin dump", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files.", "&aFully reloads the plugin, fetching \n&aany changes made to the .yml files. \n\n&aBe sure to save changes made to your .yml files!", "/itemjoin reload", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates.", "&aChecks to see if there are any updates available for this plugin.", "/itemjoin updates", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Upgrade &7- &eUpdates to latest version.", "&aAttempts to Upgrade this plugin to the latest version. \n&aYou will need to restart the server for this process to complete.", "/itemjoin upgrade", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 2&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 2", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 1/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 2)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin List &7- &eView existing items and their worlds.", "&aView an entire list of the existing \n&acustom items and their respective worlds.", "/itemjoin list", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging.", "&aDisplays the world that your Player is currently in. \n&aUseful for debugging, such as comparing to your enabled-worlds.", "/itemjoin world", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Menu &7- &eOpens the GUI Creator for custom items.", "&aCreate custom items in-game without the need \n&ato manually edit the .yml files.", "/itemjoin menu", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists your permissions.", "&aLists the Permissions for your Player. \n\n&aGreen&b means you have permission whereas \n&cRed&b means you do not have permission.", "/itemjoin permissions", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Query <Item> &7- &eDisplays the custom item data.", "&aDisplays the important item info for the existing custom item-node.", "/itemjoin query ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Info &7- &eGets data-info of the held item.", "&aDisplays the important item info for the item you are currently holding. \n&aUseful for finding the data-value and id of the item to be used.", "/itemjoin info", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 3&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 3", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 2/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 3)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item.", "&aGives you the custom item in its designated slot. \n\n&aThis will not work if you already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <Qty> &7- &eGives amount of said item.", "&aGives you the custom item in its designated slot with the specified quantity. \n\n&aThis &cWILL&a work if you already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> &7- &eGives to said player.", "&aGives the custom item to the specified player name. \n\n&aThis will not work if they already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> <Qty> &7- &eGives qty to player.", "&aGives the custom item to the specified player name with the specified quantity. \n\n&aThis &cWILL&a work if they already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 4&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 4", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 3/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 4)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.", "&aRemoves the custom item from your inventory. \n\n&aThis will not work if you do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.", "&aRemoves the custom item from your inventory with the specified quantity. \n\n&aThis will not work if you do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.", "&aRemoves the custom item from the specified player name. \n\n&aThis will not work if they do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> <Qty> &7- &eRemoves qty.", "&aRemoves the custom item from the specified player name with the specified quantity. \n\n&aThis will not work if they do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 5&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 5", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 4/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 5)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.", "&aGives the custom item to all online players.", "/itemjoin getonline ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives qty to all online.", "&aGives the custom item with the specified quantity to all online players.", "/itemjoin getonline ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.", "&aRemoves the custom item from all online players.", "/itemjoin removeonline ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> <Qty> &7- &eRemoves qty.", "&aRemoves the custom item with the specified quantity from all online players.", "/itemjoin removeonline ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 6&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 6", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 5/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 6)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.", "&aGives you all the custom items.", "/itemjoin getall", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.", "&aGives all custom items to the specified player name.", "/itemjoin getall ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.", "&aRemoves all custom items from your inventory.", "/itemjoin removeall", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.", "&aRemoves all custom items from the specified player name.", "/itemjoin removeall ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 7&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 7", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 6/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 7)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.", "&aEnables ItemJoin for all players. \nPlayers will be able to get custom items.", "/itemjoin enable", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.", "&aEnables ItemJoin for the specified player name. \nThis player will be able to get custom items.", "/itemjoin enable ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.", "&aEnables ItemJoin for the specified player name in the specified world. \nThis player will be able to get custom items in this world.", "/itemjoin enable ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 8&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 8", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 7/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 8)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.", "&aDisables ItemJoin for all players. \nPlayers will NOT be able to get custom items.", "/itemjoin disable", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.", "&aDisables ItemJoin for the specified player name. \nThis player will NOT be able to get custom items.", "/itemjoin disable ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.", "&aDisables ItemJoin for the specified player name in the specified world. \nThis player will NOT be able to get custom items in this world.", "/itemjoin disable ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 9&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 9", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 8/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 9)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges ALL Player Data from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge map-id <Image> &7- &eMap-Images data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the map-id data for the custom-image from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge map-id ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-join data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-join ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge first-world <User> &7- &eFirst-World data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-world data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-world ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the ip-limits data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge ip-limits ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Help 10&a for the next page.", "&eClick to View the Next Page.", "/itemjoin help 10", ClickAction.RUN_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Help Menu 9/10 &a&l]&a&l&m---------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 10)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge enabled-players <User> &7- &eThe Data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the enabled-players data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge enabled-players ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l/ItemJoin Purge first-commands <User> &7- &eThe Data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-commands data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-commands ", ClickAction.SUGGEST_COMMAND);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aFound a bug? Report it @");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues", "&eClick to Submit a Bug or Feature Request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 10/10 &a&l]&a&l&m--------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
        } else if (Execute.DUMP.accept(sender, args, 0)) {
            this.dump(sender);
        } else if (Execute.RELOAD.accept(sender, args, 0)) {
            PluginData.getInfo().hardReload(false);
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.configReload", sender);
        } else if (Execute.MENU.accept(sender, args, 0)) {
            Menu.startMenu(sender);
            ItemJoin.getCore().getLang().sendLangMessage("commands.menu.openMenu", sender);
        } else if (Execute.INFO.accept(sender, args, 0)) {
            this.info(sender);
        } else if (Execute.QUERY.accept(sender, args, 0)) {
            this.query(sender, args);
        } else if (Execute.WORLD.accept(sender, args, 0)) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            String[] placeHolders = ItemJoin.getCore().getLang().newString();
            placeHolders[0] = ((Player) sender).getWorld().getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands.world.worldHeader", sender, placeHolders);
            ItemJoin.getCore().getLang().sendLangMessage("commands.world.worldRow", sender, placeHolders);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
        } else if (Execute.LIST.accept(sender, args, 1)) {
            this.list(sender, 1);
        } else if (Execute.LIST.accept(sender, args, 2)) {
            this.list(sender, Integer.parseInt(args[1]));
        } else if (Execute.PERMISSIONS.accept(sender, args, 1)) {
            this.permissions(sender, 1);
        } else if (Execute.PERMISSIONS.accept(sender, args, 2)) {
            this.permissions(sender, Integer.parseInt(args[1]));
        } else if (Execute.PURGE.accept(sender, args, 0)) {
            if (args.length == 1) {
                this.purge(sender, "Database", "All Players");
            } else if (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") || args[1].equalsIgnoreCase("enabled-players")
                    || args[1].equalsIgnoreCase("first-commands")) {
                this.purge(sender, args[1], args[2]);
            }
        } else if (Execute.ENABLE.accept(sender, args, 0)) {
            this.enable(sender, (args.length >= 2 ? args[1] : "ALL"), (args.length == 3 ? args[2] : "Global"), args.length);
        } else if (Execute.DISABLE.accept(sender, args, 0)) {
            this.disable(sender, (args.length >= 2 ? args[1] : "ALL"), (args.length == 3 ? args[2] : "Global"), args.length);
        } else if (Execute.GETONLINE.accept(sender, args, 0)) {
            if (args.length >= 2) {
                this.handleOnline(sender, args, false);
            }
        } else if (Execute.GET.accept(sender, args, 0)) {
            this.handleItems(sender, args, false);
        } else if (Execute.GETALL.accept(sender, args, 0)) {
            this.handleAllItems(sender, args, false);
        } else if (Execute.REMOVEONLINE.accept(sender, args, 0)) {
            if (args.length >= 2) {
                this.handleOnline(sender, args, true);
            }
        } else if (Execute.REMOVE.accept(sender, args, 0)) {
            this.handleItems(sender, args, true);
        } else if (Execute.REMOVEALL.accept(sender, args, 0)) {
            this.handleAllItems(sender, args, true);
        } else if (Execute.UPDATE.accept(sender, args, 0)) {
            String[] placeHolders = ItemJoin.getCore().getLang().newString();
            placeHolders[21] = sender.getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands.updates.checkRequest", Bukkit.getServer().getConsoleSender(), placeHolders);
            SchedulerUtils.runAsync(() -> ItemJoin.getCore().getUpdater().checkUpdates(sender, true));
        } else if (Execute.UPGRADE.accept(sender, args, 0)) {
            String[] placeHolders = ItemJoin.getCore().getLang().newString();
            placeHolders[21] = sender.getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands.updates.updateRequest", Bukkit.getServer().getConsoleSender(), placeHolders);
            SchedulerUtils.runAsync(() -> ItemJoin.getCore().getUpdater().forceUpdates(sender));
        } else if (Execute.DEBUG.accept(sender, args, 0)) {
            if (ServerUtils.devListening()) {
                ItemJoin.getCore().getLang().dispatchMessage(sender, ItemJoin.getCore().getData().getPluginPrefix() + " &aYou are &nnow listening&a for debug messages.");
            } else {
                ItemJoin.getCore().getLang().dispatchMessage(sender, ItemJoin.getCore().getData().getPluginPrefix() + "&cYou are &nno longer&c listening for debug messages.");
            }
        } else if (executor == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.unknownCommand", sender);
        } else if (!executor.playerRequired(sender, args)) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noPlayer", sender);
            if (executor.equals(Execute.GET)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.get.usageSyntax", sender);
            } else if (executor.equals(Execute.GETALL)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.get.usageSyntax", sender);
            } else if (executor.equals(Execute.REMOVE)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.remove.usageSyntax", sender);
            } else if (executor.equals(Execute.REMOVEALL)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.remove.usageSyntax", sender);
            }
        } else if (!executor.hasSyntax(args, 0)) {
            if (executor.equals(Execute.GET)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.get.badSyntax", sender);
            } else if (executor.equals(Execute.GETONLINE)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.get.badOnlineSyntax", sender);
            } else if (executor.equals(Execute.REMOVE)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.remove.badSyntax", sender);
            } else if (executor.equals(Execute.REMOVEONLINE)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.remove.badOnlineSyntax", sender);
            } else if (executor.equals(Execute.PURGE)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.default.unknownCommand", sender);
            } else if (executor.equals(Execute.QUERY)) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.query.badSyntax", sender);
            }
        } else if (!executor.hasPermission(sender, args)) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noPermission", sender);
        }
        return true;
    }

    /**
     * Called when the CommandSender fails to execute a command.
     *
     * @param args - Passed command arguments.
     * @return The found Executor.
     */
    private Execute matchExecutor(final String[] args) {
        for (Execute command : Execute.values()) {
            if (command.acceptArgs(args)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Called when the CommandSender executes the Dump command.
     *
     * @param sender - Source of the command.
     */
    private void dump(final CommandSender sender) {
        try {
            final Map<String, String> files = new HashMap<>();
            files.put("latest.log", Files.asCharSource(new File("logs/latest.log"), StandardCharsets.UTF_8).read());
            FileConfiguration configData = YamlConfiguration.loadConfiguration(new File(ItemJoin.getCore().getPlugin().getDataFolder() + "/config.yml"));
            configData.set("Database.user", "**********");
            configData.set("Database.pass", "**********");
            files.put("config.yml", configData.saveToString());
            files.put("lang.yml", Files.asCharSource(new File(ItemJoin.getCore().getPlugin().getDataFolder() + "/" + ItemJoin.getCore().getLang().getFile()), StandardCharsets.UTF_8).read());
            files.put("items.yml", Files.asCharSource(new File(ItemJoin.getCore().getPlugin().getDataFolder() + "/items.yml"), StandardCharsets.UTF_8).read());
            final PasteAPI pasteURI = new PasteAPI(sender, Collections.singletonList("ExploitFixer"), files);
            final String pasteURL = pasteURI.getPaste();
            ServerUtils.logInfo(sender.getName() + " has generated a debug paste at " + pasteURL);
            if (!(sender instanceof ConsoleCommandSender)) {
                ItemJoin.getCore().getLang().dispatchMessage(sender, "%prefix% &a" + pasteURL, "&eClick me to copy the url.", pasteURL, ClickAction.OPEN_URL);
            }
        } catch (Exception e) {
            ServerUtils.logSevere("{ChatExecutor} Failed to execute the DUMP command.");
            ServerUtils.sendSevereTrace(e);
        }
    }

    /**
     * Called when the CommandSender executes the Info command.
     *
     * @param sender - Source of the command.
     */
    private void info(final CommandSender sender) {
        final ItemStack handItem = PlayerHandler.getHandItem((Player) sender);
        if (handItem.getType() != Material.AIR) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, " ");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            String[] placeHolders = ItemJoin.getCore().getLang().newString();
            placeHolders[3] = handItem.getType().toString();
            ItemJoin.getCore().getLang().sendLangMessage("commands.info.material", sender, placeHolders);
            if (!ServerUtils.hasSpecificUpdate("1_13")) {
                placeHolders[3] = LegacyAPI.getDataValue(handItem) + "";
                ItemJoin.getCore().getLang().sendLangMessage("commands.info.data", sender, placeHolders);
            }
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, " ");
        } else {
            ItemJoin.getCore().getLang().sendLangMessage("commands.item.noItemHeld", sender);
        }
    }

    /**
     * Called when the CommandSender executes the Query command.
     *
     * @param sender - Source of the command.
     * @param args   - Passed command arguments.
     */
    private void query(final @Nonnull CommandSender sender, final @Nonnull String[] args) {
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(args[1]);
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[3] = (itemMap != null ? itemMap.getConfigName() : args[1]);
        if (itemMap != null) {
            placeHolders[4] = ((itemMap.getDynamicMaterials() == null || itemMap.getDynamicMaterials().isEmpty()) ? itemMap.getMaterial().name() : itemMap.getDynamicMaterials().toString().replace("[", "").replace("{", "").replace("]", "").replace("}", ""));
            placeHolders[17] = ((itemMap.getMultipleSlots() == null || itemMap.getMultipleSlots().isEmpty()) ? itemMap.getSlot() : itemMap.getMultipleSlots().toString().replace("[", "").replace("{", "").replace("]", "").replace("}", ""));
            if (sender instanceof Player) {
                if (itemMap.hasPermission(((Player) sender), ((Player) sender).getWorld())) {
                    placeHolders[18] = "&a[✔] " + PermissionsHandler.customPermissions(itemMap.getPermissionNode(), ((Player) sender).getWorld().getName() + "." + itemMap.getConfigName());
                } else {
                    placeHolders[18] = "&c[✘] " + PermissionsHandler.customPermissions(itemMap.getPermissionNode(), ((Player) sender).getWorld().getName() + "." + itemMap.getConfigName());
                }
            } else {
                placeHolders[18] = "&a[✔] itemjoin.console" + "." + itemMap.getConfigName();
            }
            ItemJoin.getCore().getLang().dispatchMessage(sender, " ");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Query Data &a&l]&a&l&m----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().sendLangMessage("commands.query.node", sender, placeHolders);
            ItemJoin.getCore().getLang().sendLangMessage("commands.query.material", sender, placeHolders);
            ItemJoin.getCore().getLang().sendLangMessage("commands.query.slot", sender, placeHolders);
            ItemJoin.getCore().getLang().sendLangMessage("commands.query.permission", sender, placeHolders);
            ItemJoin.getCore().getLang().dispatchMessage(sender, "");
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Query Data &a&l]&a&l&m----------------[");
            ItemJoin.getCore().getLang().dispatchMessage(sender, " ");
        } else {
            ItemJoin.getCore().getLang().sendLangMessage("commands.item.noItem", sender, placeHolders);
        }
    }

    /**
     * Called when the CommandSender executes the list command.
     *
     * @param sender - Source of the command.
     */
    private void list(final CommandSender sender, final int page) {
        ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
        int maxPage = PluginData.getInfo().getListPages();
        int lineCount = 0;
        boolean worldSent = false;
        for (World world : Bukkit.getWorlds()) {
            if (((lineCount + 1) != (page * 15))) {
                boolean itemFound = false;
                String[] placeHolders = ItemJoin.getCore().getLang().newString();
                placeHolders[0] = world.getName();
                if (!(lineCount > (page * 15)) && (page == 1 || !(lineCount < ((page - 1) * 15)))) {
                    ItemJoin.getCore().getLang().sendLangMessage("commands.list.worldHeader", sender, placeHolders);
                    lineCount++;
                    worldSent = true;
                } else {
                    lineCount++;
                }
                List<String> inputListed = new ArrayList<>();
                for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                    if (!inputListed.contains(itemMap.getConfigName()) && itemMap.inWorld(world)) {
                        if (page == 1 && !(lineCount >= page * 15) || page != 1 && !(lineCount > page * 15) && !(lineCount < (page - 1) * 15)) {
                            if (!worldSent) {
                                placeHolders[0] = world.getName();
                                if (!(lineCount > (page * 15)) && (page == 1 || !(lineCount < ((page - 1) * 15)))) {
                                    ItemJoin.getCore().getLang().sendLangMessage("commands.list.worldHeader", sender, placeHolders);
                                    lineCount++;
                                    worldSent = true;
                                }
                            }
                            placeHolders[3] = itemMap.getConfigName();
                            placeHolders[4] = itemMap.getConfigName();
                            inputListed.add(itemMap.getConfigName());
                            ItemJoin.getCore().getLang().sendLangMessage("commands.list.itemRow", sender, placeHolders);
                        }
                        lineCount++;
                        itemFound = true;
                    }
                }
                if (!itemFound) {
                    ItemJoin.getCore().getLang().sendLangMessage("commands.list.noItems", sender);
                    lineCount++;
                }
            }
        }
        if (page != maxPage) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin List " + (page + 1) + "&a for the next page.", "&eClick to View the Next Page.", "/itemjoin list " + (page + 1), ClickAction.RUN_COMMAND);
        }
        ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]----------------&a&l[&e List Menu " + page + "/" + maxPage + " &a&l]&a&l&m---------------[");
    }

    /**
     * Called when the CommandSender executes the Permissions command.
     *
     * @param sender - Source of the command.
     * @param page   - The page number to be displayed.
     */
    private void permissions(final CommandSender sender, final int page) {
        ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
        int maxPage = PluginData.getInfo().getPermissionPages();
        if (page == 1) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.*") ? "&a[✔]" : "&c[✘]") + " ItemJoin.*");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.all") ? "&a[✔]" : "&c[✘]") + " ItemJoin.All");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.use") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Use");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.reload") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Reload");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.updates") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Updates");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.upgrade") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Upgrade");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.permissions") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Permissions");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.query") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Query");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.get") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Get");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.remove") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Remove");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.enable") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Enable");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.disable") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Disable");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.get.others") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Get.Others");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Remove.Others");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.enable.others") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Enable.Others");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.disable.others") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Disable.Others");
            ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.bypass.inventorymodify") ? "&a[✔]" : "&c[✘]") + " ItemJoin.Bypass.InventoryModify");
            for (World world : Bukkit.getWorlds()) {
                ItemJoin.getCore().getLang().dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin." + world.getName() + ".*")
                        && ((ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP") && sender.isOp()
                        ? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP"))
                        || (ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items") && !sender.isOp()
                        ? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items")))
                        ? "&a[✔]" : "&c[✘]") + " ItemJoin." + world.getName() + ".*");
            }
        } else if (page != 0) {
            List<String> customPermissions = new ArrayList<>();
            List<String> inputMessage = new ArrayList<>();
            for (World world : Bukkit.getServer().getWorlds()) {
                ItemMap probable = null;
                for (Object itemMap : ItemJoin.getCore().getChances().getItems().keySet()) {
                    if (((ItemMap) itemMap).hasItem(((Player) sender), true)) {
                        probable = (ItemMap) itemMap;
                    }
                }
                if (probable == null) {
                    probable = (ItemMap) ItemJoin.getCore().getChances().getRandom(((Player) sender));
                }
                for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
                    if ((item.getPermissionNode() == null || !customPermissions.contains(item.getPermissionNode())) && item.inWorld(world) && ((probable != null && item.getConfigName().equals(probable.getConfigName())) || item.getProbability() == -1)) {
                        if (item.getPermissionNode() != null) {
                            customPermissions.add(item.getPermissionNode());
                        }
                        if (item.hasPermission(((Player) sender), world)) {
                            inputMessage.add("&a[✔] " + PermissionsHandler.customPermissions(item.getPermissionNode(), world.getName() + "." + item.getConfigName()));
                        } else {
                            inputMessage.add("&c[✘] " + PermissionsHandler.customPermissions(item.getPermissionNode(), world.getName() + "." + item.getConfigName()));
                        }
                    }
                }
            }
            for (int i = (page == 2 ? 0 : ((page - 2) * 15) + 1); i <= ((page - 1) * 15 <= inputMessage.size() ? (page - 1) * 15 : (inputMessage.size() - 1)); i++) {
                ItemJoin.getCore().getLang().dispatchMessage(sender, inputMessage.get(i));
            }
        }
        if (page != maxPage) {
            ItemJoin.getCore().getLang().dispatchMessage(sender, "&aType &a&l/ItemJoin Permissions " + (page + 1) + "&a for the next page.", "&eClick to View the Next Page.", "/itemjoin permissions " + (page + 1), ClickAction.RUN_COMMAND);
        }
        ItemJoin.getCore().getLang().dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu " + page + "/" + maxPage + " &a&l]&a&l&m-----------[");
    }

    /**
     * Called when the CommandSender executes the Purge command.
     *
     * @param sender - Source of the command.
     * @param table  - The table being purged of data.
     * @param args   - The player name having their data purged.
     */
    private void purge(final CommandSender sender, final String table, final String args) {
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[1] = args;
        placeHolders[10] = table;
        OfflinePlayer foundPlayer = null;
        if (!table.equalsIgnoreCase("Database")) {
            placeHolders[9] = "/ij purge " + table + (table.equalsIgnoreCase("map-ids") ? " <image>" : " <player>");
            if (!table.equalsIgnoreCase("map-ids")) {
                foundPlayer = LegacyAPI.getOfflinePlayer(args);
            }
            if (!table.equalsIgnoreCase("map-ids") && foundPlayer == null && !args.equalsIgnoreCase("ALL")) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
                return;
            }
        } else {
            placeHolders[9] = "/ij purge";
        }
        if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
            if (!table.equalsIgnoreCase("Database")) {
                final String playerId = PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args));
                DataObject dataObject = (table.replace("-", "_").equalsIgnoreCase("map_ids")
                        ? new DataObject(Table.MAP_IDS, null, "", args, "") : (table.replace("-", "_").equalsIgnoreCase("first_join")
                        ? new DataObject(Table.FIRST_JOIN, playerId, "", "", "") : (table.replace("-", "_").equalsIgnoreCase("first_world")
                        ? new DataObject(Table.FIRST_WORLD, playerId, "", "", "") : (table.replace("-", "_").equalsIgnoreCase("ip_limits")
                        ? new DataObject(Table.IP_LIMITS, playerId, "", "", "") : (table.replace("-", "_").equalsIgnoreCase("enabled_players")
                        ? new DataObject(Table.ENABLED_PLAYERS, playerId, "", "", "", "") : (table.replace("-", "_").equalsIgnoreCase("first_commands")
                        ? new DataObject(Table.FIRST_COMMANDS, playerId, "", "", "") : null))))));
                if (dataObject != null) {
                    ItemJoin.getCore().getSQL().removeData(dataObject);
                }
            } else {
                ItemJoin.getCore().getData().setStarted(false);
                ItemJoin.getCore().getSQL().purgeDatabase();
                {
                    SchedulerUtils.runAsync(() -> {
                        ItemJoin.getCore().getSQL().refresh();
                        {
                            SchedulerUtils.runAsyncLater(2L, () -> SchedulerUtils.runSingleAsync(() -> ItemJoin.getCore().getData().setStarted(true)));
                        }
                    });
                }
            }
            ItemJoin.getCore().getLang().sendLangMessage("commands.database.purgeSuccess", sender, placeHolders);
            this.confirmationRequests.remove(table + sender.getName());
        } else {
            this.confirmationRequests.put(table + sender.getName(), true);
            ItemJoin.getCore().getLang().sendLangMessage("commands.database.purgeWarn", sender, placeHolders);
            ItemJoin.getCore().getLang().sendLangMessage("commands.database.purgeConfirm", sender, placeHolders);
            SchedulerUtils.runLater(100L, () -> {
                if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
                    ItemJoin.getCore().getLang().sendLangMessage("commands.database.purgeTimeOut", sender);
                    this.confirmationRequests.remove(table + sender.getName());
                }
            });
        }
    }

    /**
     * Called when the CommandSender executes the Enable command.
     *
     * @param sender    - Source of the command.
     * @param player    - The player attempting to be disabled.
     * @param world     - The world attempting to be disabled.
     * @param arguments - The max length of arguments in the command line.
     */
    private void enable(final CommandSender sender, final String player, final String world, final int arguments) {
        Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayerString(player) : null);
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[1] = (arguments >= 2 ? player : sender.getName());
        placeHolders[0] = world;
        if (arguments >= 2 && argsPlayer == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            return;
        }
        DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
        if (dataObject == null || Boolean.valueOf(dataObject.getEnabled()).equals(false)) {
            ItemJoin.getCore().getSQL().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
            ItemJoin.getCore().getSQL().saveData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
            ItemJoin.getCore().getLang().sendLangMessage("commands.enabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders);
            if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                placeHolders[1] = sender.getName();
                ItemJoin.getCore().getLang().sendLangMessage("commands.enabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders);
            }
        } else {
            ItemJoin.getCore().getLang().sendLangMessage("commands.enabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders);
        }
    }

    /**
     * Called when the CommandSender executes the Disable command.
     *
     * @param sender    - Source of the command.
     * @param player    - The player attempting to be disabled.
     * @param world     - The world attempting to be disabled.
     * @param arguments - The max length of arguments in the command line.
     */
    private void disable(final CommandSender sender, final String player, final String world, final int arguments) {
        Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayerString(player) : null);
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[1] = (arguments >= 2 ? player : sender.getName());
        placeHolders[0] = world;
        if (arguments >= 2 && argsPlayer == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            return;
        }
        DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
        if (dataObject == null || Boolean.valueOf(dataObject.getEnabled()).equals(true)) {
            ItemJoin.getCore().getSQL().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
            ItemJoin.getCore().getSQL().saveData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
            ItemJoin.getCore().getLang().sendLangMessage("commands.disabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders);
            if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                placeHolders[1] = sender.getName();
                ItemJoin.getCore().getLang().sendLangMessage("commands.disabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders);
            }
        } else {
            ItemJoin.getCore().getLang().sendLangMessage("commands.disabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders);
        }
    }

    /**
     * Called when the CommandSender executes the Get command.
     *
     * @param sender - Source of the command.
     * @param args   - Passed command arguments.
     * @param remove - If the item is expected to be removed.
     */
    private void handleItems(final CommandSender sender, final String[] args, final boolean remove) {
        Player argsPlayer = (args.length >= 3 ? PlayerHandler.getPlayerString(args[2]) : (Player) sender);
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[1] = (args.length >= 3 && argsPlayer != null ? argsPlayer.getName() : (args.length >= 3 ? args[2] : sender.getName()));
        placeHolders[3] = args[1];
        int amount = (((args.length >= 3 && argsPlayer == null) || (args.length > 3)) && StringUtils.isInt(args[args.length - 1]) ? Integer.parseInt(args[args.length - 1]) : 0);
        if (args.length >= 3 && !StringUtils.isInt(args[2]) && argsPlayer == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            return;
        } else if (argsPlayer == null && sender instanceof Player) {
            argsPlayer = (Player) sender;
        }
        boolean messageSent = false;
        ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(args[1]);
        if (itemMapExist == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.item.noItem", sender, placeHolders);
            return;
        }
        final Map<String, Integer> arbitraryMap = new HashMap<>();
        for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
            if (itemMap.getConfigName().equalsIgnoreCase(args[1]) && (!arbitraryMap.containsKey(itemMap.getConfigName()) || arbitraryMap.get(itemMap.getConfigName()) != 0) && argsPlayer != null) {
                if (itemMap.getSlot().equalsIgnoreCase("ARBITRARY") && !arbitraryMap.containsKey(itemMap.getConfigName())) {
                    int arbitrary = ItemUtilities.getUtilities().getArbitrary(itemMap);
                    int count = 0;
                    for (ItemStack inPlayerInventory : argsPlayer.getInventory().getContents()) {
                        if (itemMap.isSimilar(argsPlayer, inPlayerInventory)) {
                            count++;
                        }
                    }
                    arbitraryMap.put(itemMap.getConfigName(), (arbitrary - count));
                }
                String customName = StringUtils.translateLayout(itemMap.getCustomName(), argsPlayer);
                placeHolders[3] = customName;
                if ((remove && itemMap.hasItem(argsPlayer, true)) || (!remove && (itemMap.conditionMet(argsPlayer, "trigger-conditions", true, false) && (ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer, false)))))) {
                    if (remove || !PermissionsHandler.permissionEnabled("Permissions.Commands-Get") || (itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.permissionEnabled("Permissions.Commands-Get"))) {
                        if (itemMap.isAlwaysGive() && !StringUtils.isInt(args[args.length - 1])) {
                            amount = itemMap.getCount(argsPlayer);
                        }
                        if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0 && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory())) {
                            final ItemStack topItem = argsPlayer.getOpenInventory().getTopInventory().getItem(0);
                            if (topItem != null && !topItem.getType().equals(Material.AIR)) {
                                ItemHandler.returnCraftingItem(argsPlayer, 0, topItem.clone(), 0L);
                            }
                        }
                        if (remove) {
                            itemMap.removeFrom(argsPlayer, amount);
                        } else {
                            itemMap.giveTo(argsPlayer, amount);
                            if (arbitraryMap.containsKey(itemMap.getConfigName())) {
                                final int arbitraryCount = arbitraryMap.get(itemMap.getConfigName()) - 1;
                                arbitraryMap.put(itemMap.getConfigName(), arbitraryCount);
                            }
                        }
                        placeHolders[11] = Integer.toString((amount == 0 ? 1 : amount));
                        placeHolders[1] = sender.getName();
                        if (!messageSent) {
                            ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedYou" : "get.givenYou"), argsPlayer, placeHolders);
                        }
                        if (!messageSent && (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) {
                            placeHolders[1] = argsPlayer.getName();
                            ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedTarget" : "get.givenTarget"), sender, placeHolders);
                        }
                        PlayerHandler.quickCraftSave(argsPlayer);
                    } else if (!messageSent) {
                        ItemJoin.getCore().getLang().sendLangMessage("commands.get." + (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission" : "noPermission"), sender, placeHolders);
                    }
                } else if (!messageSent && (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) {
                    placeHolders[1] = sender.getName();
                    ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval" : "get.targetTriedGive"), argsPlayer, placeHolders);
                    placeHolders[1] = argsPlayer.getName();
                    ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.targetFailedInventory" : "get.targetFailedInventory"), sender, placeHolders);
                } else if (!messageSent) {
                    placeHolders[1] = sender.getName();
                    ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.failedInventory" : "get.failedInventory"), sender, placeHolders);
                }
                if (!messageSent) {
                    messageSent = true;
                }
            }
        }
    }

    /**
     * Called when the CommandSender executes the removeOnline command.
     *
     * @param sender - Source of the command.
     * @param args   - Passed command arguments.
     * @param remove - If the item is expected to be removed.
     */
    private void handleOnline(final CommandSender sender, final String[] args, final boolean remove) {
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[3] = args[1];
        List<String> handledPlayers = new ArrayList<>();
        List<String> failedPlayers = new ArrayList<>();
        int amount = (args.length == 3 ? Integer.parseInt(args[2]) : 0);
        PlayerHandler.forOnlinePlayers(argsPlayer -> {
            boolean messageSent = false;
            ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(args[1]);
            if (itemMapExist == null) {
                ItemJoin.getCore().getLang().sendLangMessage("commands.item.noItem", sender, placeHolders);
                return;
            }
            placeHolders[3] = StringUtils.translateLayout(itemMapExist.getCustomName(), argsPlayer);
            placeHolders[1] = sender.getName();
            placeHolders[11] = (amount == 0 ? "&lAll" : Integer.toString(amount));
            for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                if (itemMap.getConfigName().equalsIgnoreCase(args[1])) {
                    if (remove || !PermissionsHandler.permissionEnabled("Permissions.Commands-Get") || (itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.permissionEnabled("Permissions.Commands-Get"))) {
                        if ((remove && itemMap.hasItem(argsPlayer, true)) || (!remove && (itemMap.conditionMet(argsPlayer, "trigger-conditions", true, false) && (ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer, false)))))) {
                            if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0 && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory())) {
                                final ItemStack topItem = argsPlayer.getOpenInventory().getTopInventory().getItem(0);
                                if (topItem != null && !topItem.getType().equals(Material.AIR)) {
                                    ItemHandler.returnCraftingItem(argsPlayer, 0, topItem.clone(), 0L);
                                }
                            }
                            if (remove) {
                                itemMap.removeFrom(argsPlayer, amount);
                            } else {
                                itemMap.giveTo(argsPlayer, amount);
                            }
                            if (!messageSent && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                                ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedYou" : "get.givenYou"), argsPlayer, placeHolders);
                            }
                            if (!messageSent && !handledPlayers.contains(argsPlayer.getName())) {
                                handledPlayers.add(argsPlayer.getName());
                            }
                            PlayerHandler.quickCraftSave(argsPlayer);
                        } else if (!messageSent) {
                            if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                                ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval" : "get.targetTriedGive"), argsPlayer, placeHolders);
                            }
                            if (!failedPlayers.contains(argsPlayer.getName())) {
                                failedPlayers.add(argsPlayer.getName());
                            }
                        }
                        if (!messageSent) {
                            messageSent = true;
                        }
                    }
                }
            }
        });
        placeHolders[12] = handledPlayers.toString().replace("]", "").replace("[", "");
        if (!handledPlayers.isEmpty()) {
            ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedOnline" : "get.givenOnline"), sender, placeHolders);
        } else if (!failedPlayers.isEmpty()) {
            ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.onlineFailedInventory" : "get.onlineFailedInventory"), sender, placeHolders);
        }
    }

    /**
     * Called when the CommandSender executes the removeOnline command.
     *
     * @param sender - Source of the command.
     * @param args   - Passed command arguments.
     * @param remove - If the item is expected to be removed.
     */
    private void handleAllItems(final CommandSender sender, final String[] args, final boolean remove) {
        Player argsPlayer = (args.length >= 2 ? PlayerHandler.getPlayerString(args[1]) : (Player) sender);
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[1] = (args.length >= 2 ? args[1] : sender.getName());
        if (argsPlayer == null) {
            ItemJoin.getCore().getLang().sendLangMessage("commands.default.noTarget", sender, placeHolders);
            return;
        }
        boolean itemGiven = false;
        boolean failedPermissions = false;
        if (!remove && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory())) {
            final ItemStack topItem = argsPlayer.getOpenInventory().getTopInventory().getItem(0);
            if (topItem != null && !topItem.getType().equals(Material.AIR)) {
                ItemHandler.returnCraftingItem(argsPlayer, 0, topItem.clone(), 0L);
            }
        }
        ItemMap probable = null;
        for (Object itemMap : ItemJoin.getCore().getChances().getItems().keySet()) {
            if (((ItemMap) itemMap).hasItem(argsPlayer, true)) {
                probable = (ItemMap) itemMap;
            }
        }
        if (probable == null) {
            probable = (ItemMap) ItemJoin.getCore().getChances().getRandom(argsPlayer);
        }
        final Map<String, Integer> arbitraryMap = new HashMap<>();
        for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
            if ((!arbitraryMap.containsKey(itemMap.getConfigName()) || arbitraryMap.get(itemMap.getConfigName()) != 0) && (remove || itemMap.inWorld(argsPlayer.getWorld()) && (probable != null && itemMap.getConfigName().equals(probable.getConfigName()) || itemMap.getProbability() == -1) && ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && (!PermissionsHandler.permissionEnabled("Permissions.Commands-Get") || itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.permissionEnabled("Permissions.Commands-Get")))) {
                if (itemMap.getSlot().equalsIgnoreCase("ARBITRARY") && !arbitraryMap.containsKey(itemMap.getConfigName())) {
                    int arbitrary = ItemUtilities.getUtilities().getArbitrary(itemMap);
                    int count = 0;
                    for (ItemStack inPlayerInventory : argsPlayer.getInventory().getContents()) {
                        if (itemMap.isSimilar(argsPlayer, inPlayerInventory)) {
                            count++;
                        }
                    }
                    arbitraryMap.put(itemMap.getConfigName(), (arbitrary - count));
                }
                if ((remove && itemMap.hasItem(argsPlayer, true)) || ((!remove && !itemMap.hasItem(argsPlayer, false)) || (!remove && itemMap.isAlwaysGive()))) {
                    if (remove || itemMap.conditionMet(argsPlayer, "trigger-conditions", true, false)) {
                        if (remove) {
                            itemMap.removeFrom(argsPlayer);
                        } else {
                            itemMap.giveTo(argsPlayer);
                            if (arbitraryMap.containsKey(itemMap.getConfigName())) {
                                final int arbitraryCount = arbitraryMap.get(itemMap.getConfigName()) - 1;
                                arbitraryMap.put(itemMap.getConfigName(), arbitraryCount);
                            }
                        }
                        if (!itemGiven) {
                            itemGiven = true;
                        }
                        PlayerHandler.quickCraftSave(argsPlayer);
                    }
                }
            } else if (!failedPermissions && !itemMap.hasPermission(argsPlayer, argsPlayer.getWorld())) {
                failedPermissions = true;
            }
        }
        if (itemGiven) {
            failedPermissions = false;
            placeHolders[1] = sender.getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedYou_All" : "get.givenYou_All"), argsPlayer, placeHolders);
            if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                placeHolders[1] = argsPlayer.getName();
                ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.removedTarget_All" : "get.givenTarget_All"), sender, placeHolders);
            }
        } else if (!failedPermissions) {
            placeHolders[1] = argsPlayer.getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? (remove ? "remove.targetFailedInventory_All" : "get.targetFailedInventory_All") : (remove ? "remove.failedInventory_All" : "get.failedInventory_All")), sender, placeHolders);
            if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
                placeHolders[1] = sender.getName();
                ItemJoin.getCore().getLang().sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval_All" : "get.targetFailedInventory_All"), argsPlayer, placeHolders);
            }
        }
        if (failedPermissions) {
            placeHolders[1] = argsPlayer.getName();
            ItemJoin.getCore().getLang().sendLangMessage("commands.get." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission_All" : "noPermission_All"), sender, placeHolders);
        }
    }

    /**
     * Defines the config Command type for the command.
     */
    public enum Execute {
        DEFAULT("", "itemjoin.use", false),
        HELP("help", "itemjoin.use", false),
        DUMP("dump", "itemjoin.dump", false),
        RELOAD("rl, reload", "itemjoin.reload", false),
        MENU("menu, creator", "itemjoin.menu", true),
        INFO("info", "itemjoin.use", true),
        QUERY("query", "itemjoin.query", false),
        WORLD("world, worlds", "itemjoin.use", true),
        LIST("list", "itemjoin.list", false),
        PERMISSIONS("permission, permissions", "itemjoin.permissions", true),
        PURGE("purge", "itemjoin.purge", false),
        ENABLE("enable", "itemjoin.enable, itemjoin.enable.others", false),
        DISABLE("disable", "itemjoin.disable, itemjoin.disable.others", false),
        GET("get, give", "itemjoin.get, itemjoin.get.others", true),
        GETALL("getAll, giveAll", "itemjoin.get, itemjoin.get.others", true),
        GETONLINE("getOnline, giveOnline", "itemjoin.get.others", false),
        REMOVE("remove", "itemjoin.remove, itemjoin.remove.others", true),
        REMOVEALL("removeAll", "itemjoin.remove, itemjoin.remove.others", true),
        REMOVEONLINE("removeOnline", "itemjoin.remove.others", false),
        UPDATE("update, updates", "itemjoin.updates", false),
        UPGRADE("upgrade", "itemjoin.upgrade", false),
        DEBUG("debug", "itemjoin.use", true);
        private final String command;
        private final String permission;
        private final boolean player;

        /**
         * Creates a new Execute instance.
         *
         * @param command    - The expected command argument.
         * @param permission - The expected command permission requirement.
         * @param player     - If the command is specific to a player instance, cannot be executed by console.
         */
        Execute(final String command, final String permission, final boolean player) {
            this.command = command;
            this.permission = permission;
            this.player = player;
        }

        /**
         * Called when the CommandSender executes a command.
         *
         * @param sender - Source of the command.
         * @param args   - Passed command arguments.
         * @param page   - The page number to be expected.
         */
        public boolean accept(final CommandSender sender, final String[] args, final int page) {
            return (((args.length == 0 && this.equals(Execute.DEFAULT) && this.hasPermission(sender, args)) || (args.length != 0 && StringUtils.splitIgnoreCase(this.command, args[0], ",")) && this.hasSyntax(args, page) && this.playerRequired(sender, args) && this.hasPermission(sender, args)));
        }

        /**
         * Checks if the executed command is the same as the executor.
         *
         * @param args - Passed command arguments.
         */
        public boolean acceptArgs(final String[] args) {
            return (args.length == 0 || StringUtils.splitIgnoreCase(this.command, args[0], ","));
        }

        /**
         * Checks if the Command being executed has the proper formatting or syntax.
         *
         * @param args - Passed command arguments.
         * @param page - The page number to be expected.
         */
        private boolean hasSyntax(final String[] args, final int page) {
            return args.length >= 2 && (args[1].equalsIgnoreCase(String.valueOf(page)) || page == 2 && StringUtils.isInt(args[1]) && Integer.parseInt(args[1]) != 0 && Integer.parseInt(args[1]) != 1 && (this.equals(Execute.PERMISSIONS) && Integer.parseInt(args[1]) <= PluginData.getInfo().getPermissionPages() || this.equals(Execute.LIST) && Integer.parseInt(args[1]) <= PluginData.getInfo().getListPages()) || page == 1 && this.equals(Execute.PERMISSIONS) && StringUtils.isInt(args[1]) && Integer.parseInt(args[1]) == 0 || !StringUtils.isInt(args[1]) && !this.equals(Execute.PURGE)) || args.length < 2 && !this.equals(Execute.GET) && !this.equals(Execute.GETONLINE) && !this.equals(Execute.REMOVE) && !this.equals(Execute.REMOVEONLINE) && !this.equals(Execute.QUERY) || this.equals(Execute.PURGE) && args.length >= 3 && (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") || args[1].equalsIgnoreCase("enabled-players") || args[1].equalsIgnoreCase("first-commands"));
        }

        /**
         * Checks if the Player has permission to execute the Command.
         *
         * @param sender - Source of the command.
         * @param args   - Passed command arguments.
         */
        public boolean hasPermission(final CommandSender sender, final String[] args) {
            String[] permissions = this.permission.replace(" ", "").split(",");
            boolean multiPerms = this.permission.contains(",");
            return (multiPerms && (!this.equals(Execute.GET) && !this.equals(Execute.REMOVE) && (args.length >= 2 && !StringUtils.isInt(args[1]) && PermissionsHandler.hasPermission(sender, permissions[1]) || (args.length < 2 || StringUtils.isInt(args[1])) && PermissionsHandler.hasPermission(sender, permissions[0])) || (this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && (args.length == 3 && StringUtils.isInt(args[2]) || args.length == 2) && PermissionsHandler.hasPermission(sender, permissions[0]) || (args.length == 3 && !StringUtils.isInt(args[2]) || args.length >= 3) && PermissionsHandler.hasPermission(sender, permissions[1]))) || (!multiPerms && PermissionsHandler.hasPermission(sender, this.permission) && (!this.equals(Execute.DEBUG) || PermissionsHandler.isDeveloper(sender)));
        }

        /**
         * Checks if the Command requires the instance to be a Player.
         *
         * @param sender - Source of the command.
         * @param args   - Passed command arguments.
         */
        public boolean playerRequired(final CommandSender sender, final String[] args) {
            return (!this.player || (!(sender instanceof ConsoleCommandSender))
                    || ((this.equals(Execute.GETALL) || this.equals(Execute.REMOVEALL)) && args.length >= 2)
                    || ((this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && !(((args.length == 3 && PlayerHandler.getPlayerString(args[2]) == null && StringUtils.isInt(args[2])) || args.length == 2))));
        }
    }
}