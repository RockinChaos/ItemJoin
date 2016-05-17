package me.RockinChaos.itemjoin.Listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.BungeeCord;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractCmds implements Listener {
    private Map<String, Long> playersOnCooldown = new HashMap<String, Long>();
	private int cdtime = 0;

	@EventHandler
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
       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
       if (selection != null) {
       for (String item : selection.getKeys(false)) 
        {
        	ConfigurationSection items = selection.getConfigurationSection(item);
        	ItemStack item2 = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
        	if (item2 != null && CheckItem.isSimilar(item1, item2, items, player) && isCommandable(action, items)) {
        		if (!onCooldown(items, player, item, item1)) {
        		convertCommands(items, item, player);
        		}
        	}
          }
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
   	    	dispatchConsoleCommands(parts[1], player, item);
   	       } else if (Identify.toLowerCase().contains("player: ")) {
   	    	dispatchPlayerCommands(parts2[1], player, item);
	       } else if (Identify.toLowerCase().contains("console:")) {
	    	   dispatchConsoleCommands(parts3[1], player, item);
	       } else if (Identify.toLowerCase().contains("player:")) {
	    	   dispatchPlayerCommands(parts4[1], player, item);
	       } else if (Identify.toLowerCase().contains("message: ")) {
	    	   player.sendMessage(ItemJoin.pl.formatPlaceholders(parts5[1], player));
	       } else if (Identify.toLowerCase().contains("message:")) {
	    	   player.sendMessage(ItemJoin.pl.formatPlaceholders(parts6[1], player));
	       } else if (Identify.toLowerCase().contains("server: ")) {
	    	   BungeeCord.SwitchServers(player, parts7[1]);
	       } else if (Identify.toLowerCase().contains("server:")) {
	    	   BungeeCord.SwitchServers(player, parts8[1]);
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
       String Command = parts.replace("%player%", player.getName());
       Command = ItemJoin.pl.formatPlaceholders(Command, player);
       player.performCommand(Command);
       playersOnCooldown.put(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item, System.currentTimeMillis());
   }
   
   public void dispatchConsoleCommands(String parts, Player player, String item) {
       String Command = parts.replace("%player%", player.getName());
       Command = ItemJoin.pl.formatPlaceholders(Command, player);
       Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
       playersOnCooldown.put(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item, System.currentTimeMillis());
   }
}
