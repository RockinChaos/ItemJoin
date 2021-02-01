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
package me.RockinChaos.itemjoin.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class FileData {
	
	private static FileData data;
	
   /**
    * Generates the Data for the FileConfiguration that is specific
    * to the current Server version.
    * 
    */
	public void generateItemsFile() {
		File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
        FileConfiguration itemsData = YamlConfiguration.loadConfiguration(itemsFile);
        
        if (ServerHandler.getServer().hasSpecificUpdate("1_14")) {
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
        } else if (ServerHandler.getServer().hasSpecificUpdate("1_13")) {
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
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
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
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_8")) {
			itemsData.set("items.devine-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.devine-item.attributes", "{GENERIC_ATTACK_DAMAGE:15.2}");
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
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_7")) {
			itemsData.set("items.devine-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.devine-item.attributes", "{GENERIC_ATTACK_DAMAGE:15.2}");
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
			itemsData.set("items.banner-item", null);
			itemsData.set("items.melooooon-item.id", 382);
			itemsData.set("items.melooooon-item.slot", 20);
			itemsData.set("items.melooooon-item.name", "&aWater Melooooon!");
			itemsData.set("items.melooooon-item.interact.-", "'message: &aIts a Water Melooooon!'");
			itemsData.set("items.melooooon-item.commands-sequence", "RANDOM");
			itemsData.set("items.melooooon-item.itemflags", "hide-attributes, self-drops, CreativeBypass");
			itemsData.set("items.melooooon-item.triggers", "join, respawn, world-change, region-enter");
			itemsData.set("items.melooooon-item.enabled-worlds", "world, world_nether, world_the_end");
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
			ConfigHandler.getConfig(false).getSource("items.yml");
			ConfigHandler.getConfig(false).getFile("items.yml").options().copyDefaults(false);
		} catch (Exception e) {
			ItemJoin.getInstance().getServer().getLogger().severe("Could not save important data changes to the data file items.yml!");
			e.printStackTrace();
		}
	}
	
   /**
    * Gets the instance of the YAMLGenerator.
    * 
    * @return The YAMLGenerator instance.
    */
    public static FileData getData() { 
        if (data == null) { data = new FileData(); }
        return data; 
    } 
}