package me.RockinChaos.itemjoin.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class YAMLGenerator {
	
	public static void generateItemsFile() {
		File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
        FileConfiguration itemsData = YamlConfiguration.loadConfiguration(itemsFile);
        
		if (ServerHandler.hasAquaticUpdate()) {
			itemsData.set("items.ultra-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.map-item.id", "FILLED_MAP");
			itemsData.set("items.gamemode-item.id", "FIREWORK_STAR");
			itemsData.set("items.gamemode-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.bungeecord-item.id", "PURPLE_STAINED_GLASS");
			itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.filler-pane-item.id.1", "<delay:40>BLACK_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item.id.2", "<delay:20>BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item.id.3", "<delay:20>GREEN_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item.id.4", "<delay:20>MAGENTA_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item.id.5", "<delay:20>ORANGE_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item.id.6", "<delay:20>RED_STAINED_GLASS_PANE");
			itemsData.set("items.banner-item.id", "WHITE_BANNER");
			itemsData.set("items.skulldb-item.id", "PLAYER_HEAD");
			itemsData.set("items.arrow-item.id", "TIPPED_ARROW");
			itemsData.set("items.arrow-item.name", "&fDeath Arrow");
			itemsData.set("items.arrow-item.potion-effect", "WITHER:1:20");
			itemsData.set("items.firework-item.id", "FIREWORK_ROCKET");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, LIGHT_GRAY, GREEN");
			itemsData.set("items.golden-apple-item.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
			itemsData.set("items.skull-item.id", "PLAYER_HEAD");
			itemsData.set("items.filler-pane-item-two.id", "YELLOW_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item-three.id", "BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.filler-pane-item-four.id", "PINK_STAINED_GLASS_PANE");
		} else if (ServerHandler.hasCombatUpdate()) {
			itemsData.set("items.ultra-item.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-item.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-item.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.filler-pane-item.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.filler-pane-item.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.filler-pane-item.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.filler-pane-item.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.filler-pane-item.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.filler-pane-item.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item.id", "BANNER");
			itemsData.set("items.skulldb-item.id", "SKULL_ITEM:3");
			itemsData.set("items.arrow-item.id", "TIPPED_ARROW");
			itemsData.set("items.arrow-item.name", "&fDeath Arrow");
			itemsData.set("items.arrow-item.potion-effect", "WITHER:1:20");
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.golden-apple-item.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.filler-pane-item-two.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.filler-pane-item-three.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.filler-pane-item-four.id", "STAINED_GLASS_PANE:6");
		} else if (ServerHandler.hasSpecificUpdate("1_8")) {
			itemsData.set("items.ultra-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-item.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.filler-pane-item.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.filler-pane-item.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.filler-pane-item.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.filler-pane-item.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.filler-pane-item.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.filler-pane-item.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item.id", "BANNER");
			itemsData.set("items.skulldb-item.id", "SKULL_ITEM:3");
			itemsData.set("items.arrow-item.id", "ARROW");
			itemsData.set("items.arrow-item.name", "&fArrow");
			itemsData.set("items.arrow-item.potion-effect", null);
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.golden-apple-item.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, INVISIBILITY:1:410, REGENERATION:1:160");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.filler-pane-item-two.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.filler-pane-item-three.id", "STAINED_GLASS_PANE:3");
			itemsData.set("items.filler-pane-item-four.id", "STAINED_GLASS_PANE:6");
			itemsData.set("items.offhand-item", null);
		} else if (ServerHandler.hasSpecificUpdate("1_7")) {
			itemsData.set("items.ultra-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-item.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.filler-pane-item.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.filler-pane-item.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.filler-pane-item.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.filler-pane-item.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.filler-pane-item.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.filler-pane-item.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item", null);
			itemsData.set("items.melooooon-item.id", 382);
			itemsData.set("items.melooooon-item.slot", 20);
			itemsData.set("items.melooooon-item.name", "&aWater Melooooon!");
			itemsData.set("items.melooooon-item.commands.multi-click.-", "'message: &aIts a Water Melooooon!'");
			itemsData.set("items.melooooon-item.commands-sequence", "RANDOM");
			itemsData.set("items.melooooon-item.itemflags", "hide-attributes, self-drops, CreativeBypass");
			itemsData.set("items.melooooon-item.triggers", "join, respawn, world-change, region-enter");
			itemsData.set("items.melooooon-item.enabled-worlds", "world, world_nether, world_the_end");
			itemsData.set("items.skulldb-item.id", "SKULL_ITEM:3");
			itemsData.set("items.arrow-item.id", "ARROW");
			itemsData.set("items.arrow-item.name", "&fArrow");
			itemsData.set("items.arrow-item.potion-effect", null);
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.golden-apple-item.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, INVISIBILITY:1:410, REGENERATION:1:160");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.filler-pane-item-two.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.filler-pane-item-three.id", "STAINED_GLASS_PANE:3");
			itemsData.set("items.filler-pane-item-four.id", "STAINED_GLASS_PANE:6");
			itemsData.set("items.offhand-item", null);
		}

		try {
			itemsData.save(itemsFile);
			ConfigHandler.loadConfig("items.yml");
			ConfigHandler.getConfig("items.yml").options().copyDefaults(false);
		} catch (Exception e) {
			ItemJoin.getInstance().getServer().getLogger().severe("Could not save important data changes to the data file items.yml!");
			e.printStackTrace();
		}
	}
}