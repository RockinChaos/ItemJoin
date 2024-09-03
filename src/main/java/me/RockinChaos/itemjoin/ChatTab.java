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

import me.RockinChaos.core.handlers.PermissionsHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class ChatTab implements TabCompleter {

    /**
     * Called when a Player tries to TabComplete.
     *
     * @param sender  - Source of the command.
     * @param command - Command which was executed.
     * @param label   - Alias of the command which was used.
     * @param args    - Passed command arguments.
     * @return The String List of TabComplete commands.
     */
    @Override
    public List<String> onTabComplete(@Nonnull final CommandSender sender, @Nonnull final Command command, @Nonnull final String label, @Nonnull final String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        if (args.length == 2 && args[0].equalsIgnoreCase("help") && PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
            commands.addAll(Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && PermissionsHandler.hasPermission(sender, "itemjoin.permissions")) {
            for (int i = 1; i <= PluginData.getInfo().getPermissionPages(); i++) {
                commands.add(Integer.toString(i));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("list") && PermissionsHandler.hasPermission(sender, "itemjoin.list")) {
            for (int i = 1; i <= PluginData.getInfo().getListPages(); i++) {
                commands.add(Integer.toString(i));
            }
        } else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("purge") && PermissionsHandler.hasPermission(sender, "itemjoin.purge")) {
            if (args.length == 2) {
                commands.addAll(Arrays.asList("map-ids", "first-join", "first-world", "ip-limits", "enabled-players", "first-commands"));
            } else if (!args[1].equalsIgnoreCase("map-ids")) {
                commands.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
                PlayerHandler.forOfflinePlayers(player -> commands.add(player.getName()));
                PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
            } else {
                List<Object> dataList = new ArrayList<>();
                try {
                    dataList = ItemJoin.getCore().getSQL().getDataList(new DataObject(Table.valueOf("IJ_" + args[1].toUpperCase().replace("-", "_"))));
                } catch (Exception ignored) {
                }
                for (Object dataObject : dataList) {
                    final Player playerString = PlayerHandler.getPlayerString(((DataObject) dataObject).getPlayerId());
                    String objectString = (args[1].equalsIgnoreCase("map-ids") ? ((DataObject) dataObject).getMapIMG() :
                            (playerString != null ? playerString.getName() : ((DataObject) dataObject).getPlayerId()));
                    commands.add(objectString);
                }
            }
        } else if ((args.length == 2 || args.length == 3) && (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable"))) {
            if (args.length == 2 && ((PermissionsHandler.hasPermission(sender, "itemjoin.enable.others") && args[0].equalsIgnoreCase("enable"))
                    || (PermissionsHandler.hasPermission(sender, "itemjoin.disable.others") && args[0].equalsIgnoreCase("disable")))) {
                commands.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
                PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
            } else {
                for (World world : Bukkit.getServer().getWorlds()) {
                    commands.add(world.getName());
                }
            }
        } else if ((args.length == 2 || args.length == 3 || args.length == 4) && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))) {
            if (args.length == 2) {
                for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                    commands.add(itemMap.getConfigName());
                }
            } else if (args.length == 3 && ((PermissionsHandler.hasPermission(sender, "itemjoin.get.others") && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline")))
                    || (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others") && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))))) {
                commands.addAll(Arrays.asList("2", "4", "8", "16"));
                if (!args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline")) {
                    commands.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
                    PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
                }
            } else if (args.length == 4 && !StringUtils.isInt(args[2]) && !args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline") && ((PermissionsHandler.hasPermission(sender, "itemjoin.get.others")
                    && args[0].equalsIgnoreCase("get")) || (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others") && args[0].equalsIgnoreCase("remove")))) {
                commands.addAll(Arrays.asList("2", "3", "4", "6", "8", "16", "32", "64"));
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("getAll") && PermissionsHandler.hasPermission(sender, "itemjoin.get.others") || args[0].equalsIgnoreCase("removeAll")
                && PermissionsHandler.hasPermission(sender, "itemjoin.remove.others"))) {
            commands.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
            PlayerHandler.forOnlinePlayers(player -> commands.add(player.getName()));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("query") && PermissionsHandler.hasPermission(sender, "itemjoin.query"))) {
            for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                commands.add(itemMap.getConfigName());
            }
        } else if (args.length == 1) {
            if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
                commands.addAll(Arrays.asList("help", "info", "world"));
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.permissions")) {
                commands.add("permissions");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.purge")) {
                commands.add("purge");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.enable")) {
                commands.add("enable");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.disable")) {
                commands.add("disable");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.get")) {
                commands.addAll(Arrays.asList("get", "getAll"));
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) {
                commands.add("getOnline");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.remove")) {
                commands.addAll(Arrays.asList("remove", "removeAll"));
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
                commands.add("removeOnline");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.dump")) {
                commands.add("dump");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.reload")) {
                commands.add("reload");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.menu")) {
                commands.add("menu");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.query")) {
                commands.add("query");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.list")) {
                commands.add("list");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.updates")) {
                commands.add("updates");
            }
            if (PermissionsHandler.hasPermission(sender, "itemjoin.upgrade")) {
                commands.add("upgrade");
            }
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}