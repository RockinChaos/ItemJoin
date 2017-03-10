package me.RockinChaos.itemjoin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.Listeners.CancelInteract;
import me.RockinChaos.itemjoin.Listeners.Drops;
import me.RockinChaos.itemjoin.Listeners.InteractCmds;
import me.RockinChaos.itemjoin.Listeners.InventoryClick;
import me.RockinChaos.itemjoin.Listeners.Pickups;
import me.RockinChaos.itemjoin.Listeners.Placement;
import me.RockinChaos.itemjoin.Listeners.SwapHands;
import me.RockinChaos.itemjoin.Listeners.JoinItem.ChangedWorld;
import me.RockinChaos.itemjoin.Listeners.JoinItem.FirstJoin;
import me.RockinChaos.itemjoin.Listeners.JoinItem.JoinItem;
import me.RockinChaos.itemjoin.Listeners.JoinItem.Respawn;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Registers {
	
    public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
	
	public static void checkHooks() {
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Vault") != null && ItemJoin.pl.getConfig().getBoolean("Vault") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into Vault!");
	    	  ItemJoin.hasVault = true;
			 } else if (ItemJoin.pl.getConfig().getBoolean("Vault") == true) {
		     Console.sendMessage(Prefix + ChatColor.RED + "Could not find Vault or no economy plugin is attached.");
			 ItemJoin.hasVault = false;
			 }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into PlaceholderAPI!");
	    	  ItemJoin.hasPlaceholderAPI = true;
			 } else if (ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
		     Console.sendMessage(Prefix + ChatColor.RED + "Could not find PlaceholderAPI.");
			 ItemJoin.hasPlaceholderAPI = false;
			 }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into Multiverse-Core!");
	    	  ItemJoin.hasMultiverse = true;
	      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
	    	  Console.sendMessage(Prefix + ChatColor.RED + "Could not find Multiverse-Core.");
	    	  ItemJoin.hasMultiverse = false;
	      }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into Multiverse-Inventories!");
	    	  ItemJoin.hasInventories = true;
	      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
	    	  ItemJoin.hasInventories = false;
	    	  Console.sendMessage(Prefix + ChatColor.RED + "Could not find Multiverse-Inventories.");
	      }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null && ItemJoin.pl.getConfig().getBoolean("PerWorldPlugins") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into PerWorldPlugins!");
	    	  ItemJoin.hasPerWorldPlugins = true;
			 } else if (ItemJoin.pl.getConfig().getBoolean("PerWorldPlugins") == true) {
		     Console.sendMessage(Prefix + ChatColor.RED + "Could not find PerWorldPlugins.");
			 ItemJoin.hasPerWorldPlugins = false;
			 }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PerWorldInventory") != null && ItemJoin.pl.getConfig().getBoolean("PerWorldInventory") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into PerWorldInventory!");
	    	  ItemJoin.hasPerWorldInventory = true;
			 } else if (ItemJoin.pl.getConfig().getBoolean("PerWorldInventories") == true) {
		     Console.sendMessage(Prefix + ChatColor.RED + "Could not find PerWorldInventory.");
			 ItemJoin.hasPerWorldInventory = false;
			 }
		  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("AuthMe") != null && ItemJoin.pl.getConfig().getBoolean("AuthMe") == true) {
			  Console.sendMessage(Prefix + ChatColor.GREEN + "Hooked into AuthMe!");
	    	  ItemJoin.hasAuthMe = true;
			 } else if (ItemJoin.pl.getConfig().getBoolean("AuthMe") == true) {
		     Console.sendMessage(Prefix + ChatColor.RED + "Could not find AuthMe.");
			 ItemJoin.hasAuthMe = false;
			 }
		}

	   public static void configFile() {
		      ConfigHandler.loadConfig("config.yml");
		      File cFile = new File(ItemJoin.pl.getDataFolder(), "config.yml");
		      if (cFile.exists() && ConfigHandler.getConfig("config.yml").getInt("config-Version") != 5) {
		      if (ItemJoin.pl.getResource("config.yml") != null) {
		        String newGen = "config" + ItemJoin.getRandom(1500000,10000000) + ".yml";
		        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
		           if (!newFile.exists()) {
		        	   cFile.renameTo(newFile);
		              File configFile = new File(ItemJoin.pl.getDataFolder(), "config.yml");
		              configFile.delete();
		             Console.sendMessage(Prefix + ChatColor.GREEN + "Your config.yml is out dated!");
		       	     Console.sendMessage(Prefix + ChatColor.GREEN + "New options may be avaliable, Generating a new one!");
		           }
		        }
		      }
		  	  ItemJoin.pl.saveDefaultConfig();
			  ItemJoin.pl.getConfig().options().copyDefaults(false);
		}

		   public static void firstJoinFile() {
			      ConfigHandler.loadConfig("FirstJoin.yml");
				  if (ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled") == true) {
					  ItemJoin.pl.getServer().getPluginManager().registerEvents(new FirstJoin(),ItemJoin.pl);
					  ConfigHandler.loadConfig("FirstJoin.yml");
					  ConfigHandler.getConfig("FirstJoin.yml").options().copyDefaults(false);
					  }
			}
		   
		   public static void itemsFile() {
			      ConfigHandler.loadConfig("items.yml");
			      File itemsFile = new File(ItemJoin.pl.getDataFolder(), "items.yml");
			      if (itemsFile.exists() && ConfigHandler.getConfig("items.yml").getInt("items-Version") != 5) {
			      if (ItemJoin.pl.getResource("items.yml") != null) {
			        String newGen = "items" + ItemJoin.getRandom(1500000,10000000) + ".yml";
			        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
			           if (!newFile.exists()) {
			        	  itemsFile.renameTo(newFile);
			              File configFile = new File(ItemJoin.pl.getDataFolder(), "items.yml");
			              configFile.delete();
			             Console.sendMessage(Prefix + ChatColor.GREEN + "Your items.yml is out dated!");
			       	     Console.sendMessage(Prefix + ChatColor.GREEN + "New options may be avaliable, Generating a new one!");
			           }
			        }
			      }
				  ConfigHandler.loadConfig("items.yml");
				  ConfigHandler.getConfig("items.yml").options().copyDefaults(false);
				  ItemJoin.pl.worlds = ConfigHandler.getConfig("items.yml").getStringList("world-list");
			}
		   
		public static void enLangFile() {
			      ConfigHandler.loadConfig("en-lang.yml");
			      File enLang = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
			      if (enLang.exists() && ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English") && ConfigHandler.getConfig("en-lang.yml").getInt("en-Version") != 5) {
			      if (ItemJoin.pl.getResource("en-lang.yml") != null) {
			        String newGen = "en-lang" + ItemJoin.getRandom(1500000,10000000) + ".yml";
			        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
			           if (!newFile.exists()) {
			    	      enLang.renameTo(newFile);
			              File configFile = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
			              configFile.delete();
			              Console.sendMessage(Prefix + ChatColor.GREEN + "You are using an outdated or bad en-lang!");
			       	   Console.sendMessage(Prefix + ChatColor.GREEN + "New options may be avaliable, Generating a new one!");
			           }
			        }
			      }
				  if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
					  ConfigHandler.loadConfig("en-lang.yml");
					  ConfigHandler.getConfig("en-lang.yml").options().copyDefaults(false);
					  Commands.RegisterEnLang(PlayerHandlers.PlayerHolder());
				  }
		   }

		public static void delangFile() {
			
		      File enLang = new File(ItemJoin.pl.getDataFolder(), "de-lang.yml");
		      if (enLang.exists() && ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("German") && ConfigHandler.getConfig("en-lang.yml").getInt("de-Version") != 5) {
		      if (ItemJoin.pl.getResource("de-lang.yml") != null) {
		        String newGen = "de-lang" + ItemJoin.getRandom(1500000,10000000) + ".yml";
		        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
		           if (!newFile.exists()) {
		    	      enLang.renameTo(newFile);
		              File configFile = new File(ItemJoin.pl.getDataFolder(), "de-lang.yml");
		              configFile.delete();
		              Console.sendMessage(Prefix + ChatColor.GREEN + "You are using an outdated or bad de-lang!");
		       	      Console.sendMessage(Prefix + ChatColor.GREEN + "New options may be avaliable, Generating a new one!");
		           }
		        }
		      }
			  if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
				  ConfigHandler.loadConfig("en-lang.yml");
				  ConfigHandler.getConfig("en-lang.yml").options().copyDefaults(false);
				  Commands.RegisterEnLang(PlayerHandlers.PlayerHolder());
			  }
	   }
		
		   public static boolean SecretMsg() {
			   boolean isSecret = false;
				  if (ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Get-Items." + "ItemJoin-Specific-Items") == true) {
					  ItemJoin.secretMsg = "ItemJoin";
					  isSecret = true;
				  } else {
					  ItemJoin.secretMsg = "";
					  isSecret = false;
				  }
				return isSecret;
		   }
		   
		   public static boolean isPlaceholderAPI() {
			   boolean hasPlaceholderAPI = false;
				  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null 
						  && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
			    	  hasPlaceholderAPI = true;
					 }
				return hasPlaceholderAPI;
		   }
		   
		   public static void checdkUpdates() {
			   try {
		            HttpURLConnection con = (HttpURLConnection) new URL(
		                    "http://www.spigotmc.org/api/general.php").openConnection();
		            con.setDoOutput(true);
		            con.setRequestMethod("POST");
		            con.getOutputStream()
		                    .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + 12661)
		                            .getBytes("UTF-8"));
		            String version = new BufferedReader(new InputStreamReader(
		                    con.getInputStream())).readLine();
		            String cversion = ItemJoin.pl.getServer().getVersion();
		            if (!(version.length() >= cversion.length())) {
		            	Console.sendMessage(Prefix + ChatColor.GREEN + "Update is available.");
		            }
		        } catch (Exception ex) {
		            Console.sendMessage(Prefix + ChatColor.GREEN + "Failed to check for a update on spigot.");
		        }
		   }
		   
		   public static void registerEvents() {
			      ItemJoin.pl.getCommand("itemjoin").setExecutor(new Commands());
				  ItemJoin.pl.getCommand("ij").setExecutor(new Commands());
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new JoinItem(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new ChangedWorld(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new Respawn(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new InventoryClick(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new Drops(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new Pickups(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new Placement(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new InteractCmds(),ItemJoin.pl);
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new CancelInteract(),ItemJoin.pl);
				  if (hasCombatUpdate()) {
				  ItemJoin.pl.getServer().getPluginManager().registerEvents(new SwapHands(),ItemJoin.pl);
				  }
		   }

		   public static boolean hasCombatUpdate() {
			   boolean hasCombatUpdate = false;
			   String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
			   String combatVersion = "v1_9_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
			   String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
			   if (Integer.parseInt(version) > Integer.parseInt(combatVersion)) {
				hasCombatUpdate = true;
				}
			return hasCombatUpdate;
		   }
		   public static boolean hasBetterVersion() {
			   boolean hasCombatUpdate = false;
			   String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
			   String combatVersion = "v1_7_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
			   String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
			   if (Integer.parseInt(version) > Integer.parseInt(combatVersion)) {
				hasCombatUpdate = true;
				}
			return hasCombatUpdate;
		   }
}
