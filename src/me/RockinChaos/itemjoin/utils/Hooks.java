package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.CancelInteract;
import me.RockinChaos.itemjoin.listeners.ConsumeApples;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.InteractCmds;
import me.RockinChaos.itemjoin.listeners.InventoryClick;
import me.RockinChaos.itemjoin.listeners.Pickups;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.SwapHands;
import me.RockinChaos.itemjoin.listeners.giveitems.PlayerJoin;
import me.RockinChaos.itemjoin.listeners.giveitems.Respawn;
import me.RockinChaos.itemjoin.listeners.giveitems.RegionEnter;
import me.RockinChaos.itemjoin.listeners.giveitems.WorldChange;

public class Hooks {
	public static boolean hasVault;
	public static boolean hasMultiverse;
	public static boolean hasInventories;
	public static boolean hasPlaceholderAPI;
	public static boolean hasPerWorldPlugins;
	public static boolean hasPerWorldInventory;
	public static boolean hasAuthMe;
	public static boolean hasWorldGuard;

	public static void getHooks() {
		hookVault();
		hookPlaceholderAPI();
		hookMultiverseCore();
		hookMultiverseInv();
		hookPerWorldPlugins();
		hookPerWorldInventory();
		hookAuthMe();
		hookWorldGuard();
	}

	public static void getRegisters() {
	    ItemJoin.pl.getCommand("itemjoin").setExecutor(new Commands());
		ItemJoin.pl.getCommand("ij").setExecutor(new Commands());
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new WorldChange(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new InventoryClick(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new InteractCmds(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new CancelInteract(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.pl);
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new ConsumeApples(), ItemJoin.pl);
		
		if (ServerHandler.hasCombatUpdate()) {
		ItemJoin.pl.getServer().getPluginManager().registerEvents(new SwapHands(), ItemJoin.pl);
		}
		if (hasWorldGuard == true) {
			ItemJoin.pl.getServer().getPluginManager().registerEvents(new RegionEnter(), ItemJoin.pl);
		}
	}

	public static void hookVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null && ConfigHandler.getConfig("config.yml").getBoolean("Vault") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Vault!");
			hasVault = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("Vault") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find Vault or no economy plugin is attached.");
			hasVault = false;
		}
	}

	public static void hookPlaceholderAPI() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ConfigHandler.getConfig("config.yml").getBoolean("PlaceholderAPI") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PlaceholderAPI!");
			hasPlaceholderAPI = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("PlaceholderAPI") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PlaceholderAPI.");
			hasPlaceholderAPI = false;
		}
	}

	public static void hookMultiverseCore() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ConfigHandler.getConfig("config.yml").getBoolean("Multiverse-Core") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Multiverse-Core!");
			hasMultiverse = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("Multiverse-Core") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find Multiverse-Core.");
			hasMultiverse = false;
		}
	}

	public static void hookMultiverseInv() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && ConfigHandler.getConfig("config.yml").getBoolean("Multiverse-Inventories") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into Multiverse-Inventories!");
			hasInventories = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("Multiverse-Inventories") == true) {
			hasInventories = false;
			ServerHandler.sendConsoleMessage("&4Could not find Multiverse-Inventories.");
		}
	}

	public static void hookPerWorldPlugins() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null && ConfigHandler.getConfig("config.yml").getBoolean("PerWorldPlugins") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PerWorldPlugins!");
			hasPerWorldPlugins = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("PerWorldPlugins") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PerWorldPlugins.");
			hasPerWorldPlugins = false;
		}
	}

	public static void hookPerWorldInventory() {
		if (Bukkit.getServer().getPluginManager().getPlugin("PerWorldInventory") != null && ConfigHandler.getConfig("config.yml").getBoolean("PerWorldInventory") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into PerWorldInventory!");
			hasPerWorldInventory = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("PerWorldInventories") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find PerWorldInventory.");
			hasPerWorldInventory = false;
		}
	}

	public static void hookAuthMe() {
		if (Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null && ConfigHandler.getConfig("config.yml").getBoolean("AuthMe") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into AuthMe!");
			hasAuthMe = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("AuthMe") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find AuthMe.");
			hasAuthMe = false;
		}
	}
	
	public static void hookWorldGuard() {
		if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard-Regions") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
			ServerHandler.sendConsoleMessage("&aHooked into WorldGuard!");
			hasWorldGuard = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard-Regions") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
			ServerHandler.sendConsoleMessage("&4Error; Found WorldGuard but WorldEdit is not found!");
			ServerHandler.sendConsoleMessage("&4WorldEdit is required for WorldGuard to function.");
			hasWorldGuard = false;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard-Regions") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
			ServerHandler.sendConsoleMessage("&4Could not find WorldGuard.");
			hasWorldGuard = false;
		}
	}
}
