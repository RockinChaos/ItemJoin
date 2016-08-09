package me.RockinChaos.itemjoin.Listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.BungeeCord;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Commands;
import me.RockinChaos.itemjoin.utils.Econ;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractCmds implements Listener {
    private Map<String, Long> playersOnCooldown = new HashMap<String, Long>();
	private int cdtime = 0;
	public String hitplayer = "ItemJoin";

	@EventHandler(priority=EventPriority.NORMAL)
	public void onInteractCmds(PlayerInteractEvent event)
	 {
	  ItemStack item = event.getItem();
	  final Player player = event.getPlayer();
	  final String world = WorldHandler.getWorld(player.getWorld().getName());
	  boolean BadItem = item == null;
	  String action = event.getAction().toString();
	    if (WorldHandler.isWorld(world) && !BadItem)
	     {
	      setupCommands(player, world, item, action);
	}
  }
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerHitPlayerEvent(EntityDamageByEntityEvent event) {
	Entity Damager = event.getDamager(); 
	Entity Damaged = event.getEntity(); 
	if (Damager instanceof Player && Damaged instanceof Player) {
	Player player = (Player) Damaged;
	hitplayer = player.getName();
	}
 }

   public boolean isCommandable(String action, ConfigurationSection items) {
	   boolean isCommandable = false;
	   String actions = items.getString(".commands-action");
	   if (actions != null && actions.toUpperCase().contains(action)) {
		   isCommandable = true;
	  } else if (actions == null && !action.toUpperCase().equals("PHYSICAL")) {
		  isCommandable = true;
	  }
   return isCommandable;   
  }

   public boolean onCooldown(ConfigurationSection items, Player player, String item, ItemStack item1) {
	     boolean onCooldown = true;
  		 long playersCooldownList = 0L;
  		 if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item)) {
			 playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
		 }
		 cdtime = items.getInt(".commands-cooldown");
		 int cdmillis = cdtime * 1000;
		 if (System.currentTimeMillis()-playersCooldownList>=cdmillis) {
			 onCooldown = false;
		 } else {
  	        	if (items.getString(".cooldown-message") != null) {
		          	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
		    			 String inhand = items.getString(".name");
		    			 String cooldownmsg = (items.getString(".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemJoin.getName(item1)));
		    			cooldownmsg = ItemJoin.pl.formatPlaceholders(cooldownmsg, player);
		    			 player.sendMessage(cooldownmsg);
 		  }
		 }
		return onCooldown;
   }

   public void setupCommands(Player player, String world, ItemStack item1, String action) {
       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
       if (selection != null) {
       for (String item : selection.getKeys(false)) 
        {
        	ConfigurationSection items = selection.getConfigurationSection(item);
        	ItemStack item2 = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
        	if (item2 != null && CheckItem.isSimilar(item1, item2, items, player) && isCommandable(action, items)) {
        		if (!onCooldown(items, player, item, item1)) {
        		chargePlayer(items, item, player);
        		}
        	  }
           }
        }
   }

public boolean chargeCost(ConfigurationSection items, String item, Player player) {
       boolean Charged = false;
	   if (items.getString(".commands-cost") != null) {
		   int cost = items.getInt(".commands-cost");
	       if (PlayerHandlers.getBalance(player) >= cost) {
	           PlayerHandlers.withdrawBalance(player, cost);
	           player.sendMessage(Commands.itemCostSuccess.replace("%amount%", items.getString(".commands-cost")));
	           Charged = true;
	         } else if (!(PlayerHandlers.getBalance(player) >= cost)) {
	            player.sendMessage(Commands.itemCostFailed.replace("%cost%", items.getString(".commands-cost")).replace("%amount%", "" + PlayerHandlers.getBalance(player)));
	            Charged = false;
	        }
	   }
	       return Charged;
   }

public boolean isChargeable(ConfigurationSection items) {
	boolean isChargeable = false;
	   if (items.getString(".commands-cost") != null 
			   && Econ.isVaultAPI()) {
		   isChargeable = true;
	   }
	   return isChargeable;
}

public void chargePlayer(ConfigurationSection items, String item, Player player) {
	if (isChargeable(items) 
			&& chargeCost(items, item, player) == true) {
		convertCommands(items, item, player);
	} else if (!isChargeable(items)) {
		convertCommands(items, item, player);
	}
}

   public void convertCommands(ConfigurationSection items, String item, Player player) {
		List<String> command = items.getStringList(".commands");
		for (String Identify : command) {
  	       String[] parts = Identify.split("console: ");
  	       String[] parts2 = Identify.split("player: ");
  	       String[] parts3 = Identify.split("console:");
  	       String[] parts4 = Identify.split("player:");
  	       String[] parts5 = Identify.split("message: ");
  	       String[] parts6 = Identify.split("message:");
  	       String[] parts7 = Identify.split("server: ");
  	       String[] parts8 = Identify.split("server:");
   	       if (Identify.toLowerCase().contains("console: ")) {
	    	   try {
	    		   dispatchConsoleCommands(parts[1], player, item);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		dispatchConsoleCommands("", player, item);
		          }
   	       } else if (Identify.toLowerCase().contains("player: ")) {
	    	   try {
	    		   dispatchPlayerCommands(parts2[1], player, item);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		dispatchPlayerCommands("", player, item);
		          }
	       } else if (Identify.toLowerCase().contains("console:")) {
	    	   try {
	    		   dispatchConsoleCommands(parts3[1], player, item);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		dispatchConsoleCommands("", player, item);
		          }
	       } else if (Identify.toLowerCase().contains("player:")) {
	    	   try {
	    		   dispatchPlayerCommands(parts4[1], player, item);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		dispatchPlayerCommands("", player, item);
		          }
	       } else if (Identify.toLowerCase().contains("message: ")) {
	    	   try {
	    	   player.sendMessage(ItemJoin.pl.formatPlaceholders(parts5[1], player).replace("%player%", player.getName()).replace("%hitplayer%", hitplayer));
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		player.sendMessage(ItemJoin.pl.formatPlaceholders(" ", player).replace("%player%", player.getName()).replace("%hitplayer%", hitplayer));
		          }
	       } else if (Identify.toLowerCase().contains("message:")) {
	    	   try {
	    	   player.sendMessage(ItemJoin.pl.formatPlaceholders(parts6[1], player).replace("%player%", player.getName()).replace("%hitplayer%", hitplayer));
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		player.sendMessage(ItemJoin.pl.formatPlaceholders(" ", player).replace("%player%", player.getName()).replace("%hitplayer%", hitplayer));
		          }
	       } else if (Identify.toLowerCase().contains("server: ")) {
	    	   try {
	    		   BungeeCord.SwitchServers(player, parts7[1]);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		BungeeCord.SwitchServers(player, "");
		          }
	       } else if (Identify.toLowerCase().contains("server:")) {
	    	   try {
	    		   BungeeCord.SwitchServers(player, parts8[1]);
	        	} catch (ArrayIndexOutOfBoundsException e) {
	        		BungeeCord.SwitchServers(player, "");
		          }
	       } else if (!Identify.toLowerCase().contains("player: ") 
	    		   && !Identify.toLowerCase().contains("console: ") 
	    		   && !Identify.toLowerCase().contains("player:")
	    		   && !Identify.toLowerCase().contains("console:")
    		       && !Identify.toLowerCase().contains("message: ")
    		       && !Identify.toLowerCase().contains("message:")
    		       && !Identify.toLowerCase().contains("server: ")
    		       && !Identify.toLowerCase().contains("server:")) {
               dispatchPlayerCommands(Identify, player, item);
		}
      }
   }

   public void dispatchPlayerCommands(String parts, Player player, String item) {
       String Command = parts.replace("%player%", player.getName()).replace("%hitplayer%", hitplayer);
       Command = ItemJoin.pl.formatPlaceholders(Command, player);
       player.performCommand(Command);
       playersOnCooldown.put(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item, System.currentTimeMillis());
   }
   
   public void dispatchConsoleCommands(String parts, Player player, String item) {
       String Command = parts.replace("%player%", player.getName()).replace("%hitplayer%", hitplayer);
       Command = ItemJoin.pl.formatPlaceholders(Command, player);
       Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
       playersOnCooldown.put(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item, System.currentTimeMillis());
   }
}
