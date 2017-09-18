package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.CancelInteract;
import me.RockinChaos.itemjoin.listeners.ConsumeApples;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.InteractCmds;
import me.RockinChaos.itemjoin.listeners.InvClickCreative;
import me.RockinChaos.itemjoin.listeners.InvClickSurvival;
import me.RockinChaos.itemjoin.listeners.Pickups;
import me.RockinChaos.itemjoin.listeners.Deprecated_Pickups;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.ItemStore;
import me.RockinChaos.itemjoin.listeners.SwapHands;
import me.RockinChaos.itemjoin.listeners.giveitems.PlayerJoin;
import me.RockinChaos.itemjoin.listeners.giveitems.Respawn;
import me.RockinChaos.itemjoin.listeners.giveitems.RegionEnter;
import me.RockinChaos.itemjoin.listeners.giveitems.WorldChange;

public class Hooks {
	private static boolean hasVault;
	private static boolean hasMultiverse;
	private static boolean hasInventories;
	private static boolean hasPlaceholderAPI;
	private static boolean hasPerWorldPlugins;
	private static boolean hasPerWorldInventory;
	private static boolean hasAuthMe;
	private static boolean hasMyWorlds;
	private static boolean hasxInventories;
	private static boolean hasTokenEnchant;
	private static boolean hasWorldGuard;

	public static void getHooks() {
		hookVault();
		hookPlaceholderAPI();
		hookMultiverseCore();
		hookMultiverseInv();
		hookPerWorldPlugins();
		hookPerWorldInventory();
		hookAuthMe();
		hookWorldGuard();
		hookMyWorlds();
		hookxInventories();
		hookTokenEnchant();
	}
	
	public static boolean hasVault() {
		return hasVault;
	}
	
	public static boolean hasMultiverse() {
		return hasMultiverse;
	}
	
	public static boolean hasInventories() {
		return hasInventories;
	}
	
	public static boolean hasPlaceholderAPI() {
		return hasPlaceholderAPI;
	}
	
	
	public static boolean hasPerWorldPlugins() {
		return hasPerWorldPlugins;
	}
	
	public static boolean hasPerWorldInventory() {
		return hasPerWorldInventory;
	}
	
	public static boolean hasAuthMe() {
		return hasAuthMe;
	}
	
	public static boolean hasMyWorlds() {
		return hasMyWorlds;
	}
	
	public static boolean hasxInventories() {
		return hasxInventories;
	}
	
	public static boolean hasTokenEnchant() {
		return hasTokenEnchant;
	}
	
	public static boolean hasWorldGuard() {
		return hasWorldGuard;
	}
	
	private static Class<?> getEventClass(String name) {
	    try {
	    return Class.forName("org.bukkit.event." + name);
		} catch (ClassNotFoundException e) {
			ServerHandler.sendDebugMessage(name + " Does not exist in this version of Minecraft!");
		}
		return null;
	}

	public static void getRegisters() {
	    ItemJoin.getInstance().getCommand("itemjoin").setExecutor(new Commands());
		ItemJoin.getInstance().getCommand("ij").setExecutor(new Commands());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldChange(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InvClickSurvival(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InvClickCreative(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InteractCmds(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new CancelInteract(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new ConsumeApples(), ItemJoin.getInstance());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new ItemStore(), ItemJoin.getInstance());

		if (ServerHandler.hasWorldOfColorUpdate() && getEventClass("entity.EntityPickupItemEvent") != null) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.getInstance());
		} else {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Deprecated_Pickups(), ItemJoin.getInstance());
		}

		if (ServerHandler.hasCombatUpdate() && getEventClass("player.PlayerSwapHandItemsEvent") != null) {
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new SwapHands(), ItemJoin.getInstance());
		}

		if (hasWorldGuard == true) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new RegionEnter(), ItemJoin.getInstance());
		}
	}
	
	public static void hookMyWorlds() {
		if (Bukkit.getServer().getPluginManager().getPlugin("My_Worlds") != null && ConfigHandler.getConfig("config.yml").getBoolean("MyWorlds") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into MyWorlds!");
			hasMyWorlds = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("MyWorlds") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find MyWorlds.");
			hasMyWorlds = false;
		}
	}
	
	public static void hookxInventories() {
		if (Bukkit.getServer().getPluginManager().getPlugin("xInventories") != null && ConfigHandler.getConfig("config.yml").getBoolean("xInventories") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into xInventories!");
			hasxInventories = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("xInventories") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find xInventories.");
			hasxInventories = false;
		}
	}
	
	public static void hookTokenEnchant() {
		if (Bukkit.getServer().getPluginManager().getPlugin("TokenEnchant") != null && ConfigHandler.getConfig("config.yml").getBoolean("TokenEnchant") == true) {
			ServerHandler.sendConsoleMessage("&aHooked into TokenEnchant!");
			hasTokenEnchant = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("TokenEnchant") == true) {
			ServerHandler.sendConsoleMessage("&4Could not find TokenEnchant.");
			hasTokenEnchant = false;
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
		if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
			ServerHandler.sendConsoleMessage("&aHooked into WorldGuard!");
			hasWorldGuard = true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
			ServerHandler.sendConsoleMessage("&4Error; Found WorldGuard but WorldEdit is not found!");
			ServerHandler.sendConsoleMessage("&4WorldEdit is required for WorldGuard to function.");
			hasWorldGuard = false;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("WorldGuard") == true && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
			ServerHandler.sendConsoleMessage("&4Could not find WorldGuard.");
			hasWorldGuard = false;
		}
	}
}
