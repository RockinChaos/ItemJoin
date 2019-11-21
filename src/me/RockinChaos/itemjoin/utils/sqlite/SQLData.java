package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class SQLData {
	
	private Map < String, Integer > mapImages = new HashMap < String, Integer >();
	private Map < String, List <String> > ipLimitAddresses = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstJoinPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstCommandPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstWorldPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > enabledPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > returnItems = new HashMap < String, List <String> >();
	private List <String> executeStatementsLater = new ArrayList<String>();
	
	public SQLData() {
		this.createTables();
		this.convertYAMLS();
		this.loadMapImages();
		this.loadFirstJoinPlayers();
		this.loadFirstWorldPlayers();
		this.loadFirstCommandPlayers();
		this.loadIPLimitAddresses();
		this.loadEnabledPlayers();
		this.loadReturnItems();
	
		try { SQLite.getDatabase("database").closeConnection(); } catch (Exception e) { } 
		this.runTaskSaveStatements();
	}
	
	private void createTables() {
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS first_join (`World_Name` varchar(32), `Player_Name` varchar(32), `Player_UUID` varchar(32), `Item_Name` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS first_world (`World_Name` varchar(32), `Player_Name` varchar(32), `Player_UUID` varchar(32), `Item_Name` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ip_limits (`World_Name` varchar(32), `IP_Address` varchar(32), `Player_UUID` varchar(32), `Item_Name` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS first_commands (`World_Name` varchar(32), `Player_UUID` varchar(32), `Command_String` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS enabled_players (`World_Name` varchar(32), `Player_Name` varchar(32), `Player_UUID` varchar(32), `isEnabled` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS return_items (`World_Name` varchar(32), `Region_Name` varchar(32), `Player_UUID` varchar(32), `Inventory64` varchar(32), `Time_Stamp` varchar(32));");
		SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS map_ids (`Map_IMG` varchar(32), `Map_ID` varchar(32));");
		this.alterTables();
	}
	
	private void alterTables() {
		if (!SQLite.getDatabase("database").columnExists("SELECT Time_Stamp FROM first_join")) {
			SQLite.getDatabase("database").executeStatement("ALTER TABLE first_join ADD Time_Stamp datatype;");
			SQLite.getDatabase("database").executeStatement("ALTER TABLE first_world ADD Time_Stamp datatype;");
			SQLite.getDatabase("database").executeStatement("ALTER TABLE ip_limits ADD Time_Stamp datatype;");
			SQLite.getDatabase("database").executeStatement("ALTER TABLE first_commands ADD Time_Stamp datatype;");
			SQLite.getDatabase("database").executeStatement("ALTER TABLE enabled_players ADD Time_Stamp datatype;");
		} 
	}
	
	public void executeLaterStatements() {
		if (executeStatementsLater != null && !executeStatementsLater.isEmpty()) {
			for (String statement : executeStatementsLater) {
				SQLite.getDatabase("database").executeStatement(statement);
			}
			ConfigHandler.getLogger().sqLiteSaving();
			executeStatementsLater.clear();
			try { SQLite.getDatabase("database").closeConnection(); } catch (Exception e) { }
		}
	}
	
	private void runTaskSaveStatements() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	executeLaterStatements();
            	runTaskSaveStatements();
            }
        }, 36000L);
	}
	
	private void loadMapImages() {
		List<List<String>> selectedMapImages = SQLite.getDatabase("database").queryTableData("SELECT * FROM map_ids", "Map_IMG", "Map_ID");
		if (selectedMapImages != null && !selectedMapImages.isEmpty()) {
			for (List<String> sl1 : selectedMapImages) {
				if (!imageNumberExists(sl1.get(0))) {
					mapImages.put(sl1.get(0), Utils.returnInteger(sl1.get(1)));
				}	
			}
		}
	}
	
	private void loadFirstJoinPlayers() {
		List < List < String > > selectedFirstJoinPlayers = SQLite.getDatabase("database").queryTableData("SELECT * FROM first_join", "Item_Name", "Player_UUID");
		if (selectedFirstJoinPlayers != null && !selectedFirstJoinPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstJoinPlayers) {
				if (firstJoinPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = firstJoinPlayers.get(sl1.get(1));
					h1.add(sl1.get(0));
					firstJoinPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(0));
					firstJoinPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
	private void loadFirstWorldPlayers() {
		List<List<String>> selectedFirstWorldPlayers = SQLite.getDatabase("database").queryTableData("SELECT * FROM first_world", "Item_Name", "Player_UUID", "World_Name");
		if (selectedFirstWorldPlayers != null && !selectedFirstWorldPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstWorldPlayers) {
				if (firstWorldPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = firstWorldPlayers.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(0));
					firstWorldPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(0));
					firstWorldPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
	private void loadFirstCommandPlayers() {
		List<List<String>> selectedFirstCommandPlayers = SQLite.getDatabase("database").queryTableData("SELECT * FROM first_commands", "Command_String", "Player_UUID", "World_Name");
		if (selectedFirstCommandPlayers != null && !selectedFirstCommandPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstCommandPlayers) {
				if (firstCommandPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = firstCommandPlayers.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(0));
					firstCommandPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(0));
					firstCommandPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
	private void loadIPLimitAddresses() {
		List<List<String>> selectedIPLimitAddresses = SQLite.getDatabase("database").queryTableData("SELECT * FROM ip_limits", "Item_Name", "Player_UUID", "World_Name", "IP_Address");
		if (selectedIPLimitAddresses != null && !selectedIPLimitAddresses.isEmpty()) {
			for (List<String> sl1 : selectedIPLimitAddresses) {
				if (ipLimitAddresses.get(sl1.get(1)) != null) {
					List <String> h1 = ipLimitAddresses.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(3) + "." + sl1.get(0));
					ipLimitAddresses.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(3) + "." + sl1.get(0));
					ipLimitAddresses.put(sl1.get(1), h1);
				}
			}
		}
	}
	
	private void loadEnabledPlayers() {
		List<List<String>> selectedEnabledPlayers = SQLite.getDatabase("database").queryTableData("SELECT * FROM enabled_players", "Player_UUID", "World_Name", "isEnabled");
		if (selectedEnabledPlayers != null && !selectedEnabledPlayers.isEmpty()) {
			for (List<String> sl1 : selectedEnabledPlayers) {
				if (enabledPlayers.get(sl1.get(0)) != null) {
					List <String> h1 = enabledPlayers.get(sl1.get(0));
					h1.add(sl1.get(1) + "." + sl1.get(2));
					enabledPlayers.put(sl1.get(0), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(1) + "." + sl1.get(2));
					enabledPlayers.put(sl1.get(0), h1);
				}
			}
		}
	}
	
	private void loadReturnItems() {
		List<List<String>> selectedReturnItems = SQLite.getDatabase("database").queryTableData("SELECT * FROM return_items", "Player_UUID", "World_Name", "Region_Name", "Inventory64");
		if (selectedReturnItems != null && !selectedReturnItems.isEmpty()) {
			for (List<String> sl1 : selectedReturnItems) {
				if (returnItems.get(sl1.get(0)) != null) {
					List <String> h1 = returnItems.get(sl1.get(0));
					h1.add(sl1.get(1) + "." + sl1.get(2) + "." + sl1.get(3));
					returnItems.put(sl1.get(0), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(1) + "." + sl1.get(2) + "." + sl1.get(3));
					returnItems.put(sl1.get(0), h1);
				}
			}
		}
	}
	
	public void saveFirstJoinData(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstJoin()) {
			executeStatementsLater.add("INSERT INTO first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getName().toString() + "','" + PlayerHandler.getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (firstJoinPlayers.get(PlayerHandler.getPlayerID(player)) != null) {
				List <String> h1 = firstJoinPlayers.get(PlayerHandler.getPlayerID(player));
				h1.add(itemMap.getConfigName());
				firstJoinPlayers.put(PlayerHandler.getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(itemMap.getConfigName());
				firstJoinPlayers.put(PlayerHandler.getPlayerID(player), h1);
			}
		}
	}
	
	public void saveFirstWorldData(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstWorld()) {
			executeStatementsLater.add("INSERT INTO first_world (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getName().toString() + "','" + PlayerHandler.getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (firstWorldPlayers.get(PlayerHandler.getPlayerID(player)) != null) {
				List <String> h1 = firstWorldPlayers.get(PlayerHandler.getPlayerID(player));
				h1.add(player.getWorld().getName() + "." + itemMap.getConfigName());
				firstWorldPlayers.put(PlayerHandler.getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(player.getWorld().getName() + "." + itemMap.getConfigName());
				firstWorldPlayers.put(PlayerHandler.getPlayerID(player), h1);
			}
		}
	}
	
	public void saveIpLimitData(Player player, ItemMap itemMap) {
		if (itemMap.isIpLimted()) {
			executeStatementsLater.add("INSERT INTO ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getAddress().getHostString() + "','" + PlayerHandler.getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (ipLimitAddresses.get(PlayerHandler.getPlayerID(player)) != null) {
				List <String> h1 = ipLimitAddresses.get(PlayerHandler.getPlayerID(player));
				h1.add(player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName());
				ipLimitAddresses.put(PlayerHandler.getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName());
				ipLimitAddresses.put(PlayerHandler.getPlayerID(player), h1);
			}
		}
	}
	
	public void saveFirstCommandData(Player player, String command) {
		executeStatementsLater.add("INSERT INTO first_commands (`World_Name`, `Player_UUID`, `Command_String`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + PlayerHandler.getPlayerID(player) + "','" + command + "','" + new Timestamp(System.currentTimeMillis()) + "')");
		if (firstCommandPlayers.get(PlayerHandler.getPlayerID(player)) != null) {
			List <String> h1 = firstCommandPlayers.get(PlayerHandler.getPlayerID(player));
			h1.add(player.getWorld().getName() + "." + command);
			firstCommandPlayers.put(PlayerHandler.getPlayerID(player), h1);
		} else {
			List <String> h1 = new ArrayList<String>();
			h1.add(player.getWorld().getName() + "." + command);
			firstCommandPlayers.put(PlayerHandler.getPlayerID(player), h1);
		}
	}
	
	public void saveReturnItems(Player player, String world, String region, Inventory inventory) {
		String inventory64 = world + "." + region + "." + ItemHandler.sterilizeInventory(inventory);
		executeStatementsLater.add("INSERT INTO return_items (`World_Name`, `Region_Name`, `Player_UUID`, `Inventory64`, `Time_Stamp`) VALUES ('" + world + "','" + region + "','" + PlayerHandler.getPlayerID(player) + "','" + ItemHandler.sterilizeInventory(inventory) + "','" + new Timestamp(System.currentTimeMillis()) + "')");
		if (returnItems.get(PlayerHandler.getPlayerID(player)) != null && Utils.containsIgnoreCase(returnItems.get(PlayerHandler.getPlayerID(player)).toString(), world + "." + region)) {
			return;
	    } else if (returnItems.get(PlayerHandler.getPlayerID(player)) != null) {
			List <String> h1 = returnItems.get(PlayerHandler.getPlayerID(player));
			h1.add(inventory64);
			returnItems.put(PlayerHandler.getPlayerID(player), h1);
		} else {
			List <String> h1 = new ArrayList<String>();
			h1.add(inventory64);
			returnItems.put(PlayerHandler.getPlayerID(player), h1);
		}
	}
	
	public void saveToDatabase(Player player, String worldName, String boolValue, String type) {
			String realPlayer = "ALL"; String realName = "ALL";
			if (player != null) { realPlayer = PlayerHandler.getPlayerID(player); realName = player.getName().toString(); }
			if (type.contains("enabled-players") || type.contains("disabled-players")) {
				if (enabledPlayers.get(realPlayer) != null) {
					List <String> h1 = enabledPlayers.get(realPlayer);
					if (Utils.containsIgnoreCase(h1.toString(), worldName + ".")) {
						for (int i = 0; i <= h1.size(); i++) {
							if (Utils.containsIgnoreCase(h1.get(i), worldName + ".")) {
								h1.remove(i);
							}
						}
					}
					h1.add(worldName + "." + boolValue);
					enabledPlayers.put(realPlayer, h1);
					executeStatementsLater.add("UPDATE enabled_players SET IsEnabled='" + boolValue + "' WHERE World_Name='" + worldName + "' AND Player_UUID='" + realPlayer + "'");
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(worldName + "." + boolValue);
					enabledPlayers.put(realPlayer, h1);
					executeStatementsLater.add("INSERT INTO enabled_players (`World_Name`, `Player_Name`, `Player_UUID`, `isEnabled`, `Time_Stamp`) VALUES ('" + worldName + "','" + realName + "','" + realPlayer + "','" + boolValue + "','" + new Timestamp(System.currentTimeMillis()) + "')");
				}
			}
	}
	
	public void saveItemData(Player player, ItemMap itemMap) {
		this.saveFirstJoinData(player, itemMap);
		this.saveFirstWorldData(player, itemMap);
		this.saveIpLimitData(player, itemMap);
	}
	
	public void purgeDatabaseData(String section, OfflinePlayer player) {
		String UUID = PlayerHandler.getOfflinePlayerID(player);
		if (section.equalsIgnoreCase("first_join") && firstJoinPlayers.values() != null && !firstJoinPlayers.isEmpty()) {
			executeStatementsLater.add("DELETE FROM " + section + " WHERE Player_UUID='" + UUID + "';");
			firstJoinPlayers.remove(UUID);
		} else if (section.equalsIgnoreCase("first_world") && firstWorldPlayers.values() != null && !firstWorldPlayers.isEmpty()) {
			executeStatementsLater.add("DELETE FROM " + section + " WHERE Player_UUID='" + UUID + "';");
			firstWorldPlayers.remove(UUID);
		} else if (section.equalsIgnoreCase("ip_limits") && ipLimitAddresses.values() != null && !ipLimitAddresses.isEmpty()) {
			executeStatementsLater.add("DELETE FROM " + section + " WHERE Player_UUID='" + UUID + "';");
			ipLimitAddresses.remove(UUID);
		}
	}
	
	public void removeReturnItems(Player player, String world, String region) {
		List <String> h1 = returnItems.get(PlayerHandler.getPlayerID(player));
		if (returnItems.values() != null && !returnItems.isEmpty() && h1 != null && !h1.isEmpty()) {
			executeStatementsLater.add("DELETE FROM return_Items WHERE Player_UUID='" + PlayerHandler.getPlayerID(player) + "' AND World_Name='" + world + "' AND Region_Name='" + region + "';");
			for (String inventory : h1) {
				if (Utils.containsIgnoreCase(inventory, world + "." + region + ".")) {
					h1.remove(inventory);
					break;
				}
			}
			returnItems.put(PlayerHandler.getPlayerID(player), h1);
		}
	}
	
	public Boolean hasFirstCommanded(Player player, String command) {
		if (firstCommandPlayers.get(PlayerHandler.getPlayerID(player)) != null
				&& Utils.containsIgnoreCase(firstCommandPlayers.get(PlayerHandler.getPlayerID(player)).toString(), player.getWorld().getName() + "." + command.replace("first-join: ", "").replace("first-join:", ""))) {
			return true;
		}
		return false;
	}
	
	public Boolean hasFirstJoined(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstJoin() && firstJoinPlayers.get(PlayerHandler.getPlayerID(player)) != null 
				&& Utils.containsIgnoreCase(firstJoinPlayers.get(PlayerHandler.getPlayerID(player)).toString(), itemMap.getConfigName())) {
			return true;
		}
		return false;
	}
	
	public Boolean hasFirstWorld(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstWorld() && firstWorldPlayers.get(PlayerHandler.getPlayerID(player)) != null 
			&& Utils.containsIgnoreCase(firstWorldPlayers.get(PlayerHandler.getPlayerID(player)).toString(), player.getWorld().getName() + "." + itemMap.getConfigName())) {
			return true;
		}
		return false;
	}
	
	public Inventory getReturnItems(Player player, String world, String region) {
		if (returnItems.get(PlayerHandler.getPlayerID(player)) != null) {
			for (String inventory : returnItems.get(PlayerHandler.getPlayerID(player))) {
				if (Utils.containsIgnoreCase(inventory, world) && Utils.containsIgnoreCase(inventory, region)) {
					return ItemHandler.deserializeInventory(inventory.replace(world + "." + region + ".", ""));
				}
			}
		}
		return null;
	}
	
	public Map<String, List<String>> getFirstPlayers() {
		return firstJoinPlayers;
	}
	
	public Map<String, List<String>> getFirstWorlds() {
		return firstWorldPlayers;
	}
	
	public Map<String, List<String>> getLimitPlayers() {
		return ipLimitAddresses;
	}
	
	public Boolean isIPLimited(Player player, ItemMap itemMap) {
		if (itemMap.isIpLimted()) {
			for (String playerValue : ipLimitAddresses.keySet()) {
				if (Utils.containsIgnoreCase(ipLimitAddresses.get(playerValue).toString(), player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName())
						|| Utils.containsIgnoreCase(ipLimitAddresses.get(playerValue).toString(), player.getWorld().getName() + "." + player.getAddress().getHostString().replace(".", "") + "." + itemMap.getConfigName())) {
					if (PlayerHandler.getPlayerID(player).equalsIgnoreCase(playerValue)) {
						return false;
					} else { return true; }
				}
			}
		}
		return false;
	}
	
	public void saveMapImage(ItemMap itemMap) {
		if (!imageNumberExists(itemMap.getMapImage())) {
			executeStatementsLater.add("INSERT INTO map_ids (`Map_IMG`, `Map_ID`) VALUES ('" + itemMap.getMapImage() + "','" + itemMap.getMapID() + "')");
			mapImages.put(itemMap.getMapImage(), itemMap.getMapID());
		}
	}
	
	public void purgeMapImage(ItemMap itemMap) {
		if (imageNumberExists(itemMap.getMapImage()) && mapImages.values() != null && !mapImages.isEmpty()) {
			executeStatementsLater.add("DELETE FROM map_ids WHERE Map_IMG='" + itemMap.getMapImage() + "';");
			mapImages.remove(itemMap.getMapImage());
		}
	}
	
	public Boolean imageNumberExists(String image) {
		if (mapImages.get(image) != null) {
			return true;
		}
		return false;
	}
	
	public int getImageNumber(String image) {
		if (mapImages.get(image) != null) {
			return mapImages.get(image);
		}
		return 0;
	}
	
	public boolean isEnabled(final Player player) {
		if (enabledPlayers.get(PlayerHandler.getPlayerID(player)) != null) {
			if (Utils.containsIgnoreCase(enabledPlayers.get(PlayerHandler.getPlayerID(player)).toString(), player.getWorld().getName() + "." + "false")
					|| Utils.containsIgnoreCase(enabledPlayers.get(PlayerHandler.getPlayerID(player)).toString(), "Global" + "." + "false") || enabledPlayers.get("ALL") != null && Utils.containsIgnoreCase(enabledPlayers.get("ALL").toString(), "Global" + "." + "false")) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isWritable(final String world, final String playerString) {
		if (enabledPlayers.get(playerString) != null && Utils.containsIgnoreCase(enabledPlayers.get(playerString).toString(), world + "." + "false")) {
				return false;
		}
		return true;
	}

	public void convertYAMLS() {
		File firstJoin = new File(ItemJoin.getInstance().getDataFolder(), "first-join.yml");
		File ipLimit = new File(ItemJoin.getInstance().getDataFolder(), "ip-limit.yml");
		boolean converting = false;
		if (firstJoin.exists() || ipLimit.exists()) {
			if (firstJoin.exists()) {
				convertFirstJoinData(firstJoin);
				ConfigHandler.getLogger().sqLiteConverting("first-join");
			}
			if (ipLimit.exists()) {
				convertIpLimitData(ipLimit);
				ConfigHandler.getLogger().sqLiteConverting("ip-limit");
			}
			converting = true;
			ConfigHandler.getLogger().sqLiteConversion();
		}
		if (converting == true) { ConfigHandler.getLogger().sqLiteComplete(); }
	}
	
	private void convertFirstJoinData(File firstJoin) {
		try {
			YamlConfiguration configSection = YamlConfiguration.loadConfiguration(firstJoin);
			for (String worldsec: configSection.getKeys(false)) {
				ConfigurationSection world = configSection.getConfigurationSection(worldsec);
				for (String itemsec: world.getKeys(false)) {
					ConfigurationSection item = world.getConfigurationSection(itemsec);
					for (String uuidsec: item.getKeys(false)) {
						ConfigurationSection uuid = item.getConfigurationSection(uuidsec);
						OfflinePlayer player = ItemJoin.getInstance().getServer().getOfflinePlayer(UUID.fromString(uuid.getName()));
						if (!SQLite.getDatabase("database").dataExists("SELECT * FROM first_join WHERE World_Name='" + world.getName() + "' AND Player_UUID='" + uuid.getName() + "' AND Item_Name='" + item.getName() + "';")) {
							SQLite.getDatabase("database").executeStatement("INSERT INTO first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + player.getName().toString() + "','" + uuid.getName() + "','" + item.getName() + "')");
							SQLite.getDatabase("database").closeConnection();
						}
					}
				}
			}
			File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
			if (!userfiles.exists()) {
				userfiles.mkdirs();
			}
			String newGen = "converted" + Utils.getRandom(0, 100) + "-first-join.yml";
			File newFile = new File(userfiles, newGen);
			firstJoin.renameTo(newFile);
		} catch (Exception e) { ConfigHandler.getLogger().sqLiteConvertFailed(e, "first-join"); }
	}
	
	private void convertIpLimitData(File ipLimit) {
		try {
			YamlConfiguration configSection = YamlConfiguration.loadConfiguration(ipLimit);
			for (String worldsec: configSection.getKeys(false)) {
				ConfigurationSection world = configSection.getConfigurationSection(worldsec);
				for (String itemsec: world.getKeys(false)) {
					ConfigurationSection item = world.getConfigurationSection(itemsec);
					for (String ipaddrsec: item.getKeys(false)) {
						ConfigurationSection ipaddr = item.getConfigurationSection(ipaddrsec);
						if (!SQLite.getDatabase("database").dataExists("SELECT * FROM ip_limits WHERE World_Name='" + world.getName() + "' AND IP_Address='" + ipaddr.getName() + "' AND Item_Name='" + item.getName() + "';")) {
							SQLite.getDatabase("database").executeStatement("INSERT INTO ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + ipaddr.getName() + "','" + ipaddr.get("Current User") + "','" + item.getName() + "')");
							SQLite.getDatabase("database").closeConnection();
						}
					}
				}
			}
			File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
			if (!userfiles.exists()) {
				userfiles.mkdirs();
			}
			String newGen = "converted" + Utils.getRandom(0, 100) + "-ip-limit.yml";
			File newFile = new File(userfiles, newGen);
			ipLimit.renameTo(newFile);
		} catch (Exception e) { ConfigHandler.getLogger().sqLiteConvertFailed(e, "ip-limit"); }
	}
}