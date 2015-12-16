package me.xsilverslayerx.itemjoin.utils.multiverse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.xsilverslayerx.itemjoin.ItemJoin;
import me.xsilverslayerx.itemjoin.utils.ItemNames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MvListeners implements Listener{

    private Map<String, Long> playersOnCooldown = new HashMap<String, Long>();
	private int cdtime = 0;

 // Listeners //

     @EventHandler(priority=EventPriority.HIGHEST)
     public void onPlayerRespawn(PlayerRespawnEvent event)
     {
	    	Player player = event.getPlayer();
   	      	if(ItemJoin.pl.getConfig().getBoolean("give-on-respawn") == true && (ItemJoin.pl.giveonrespawn.contains(player.getWorld().getName()))){
   	    		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-give-on-respawn") == true && event.getPlayer().isOp()) {
   	      		}
   	              else {
       if (ItemJoin.pl.worlds.contains(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName())))
       {
         ItemStack[] itemz = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName().trim()));
         event.getPlayer().getInventory().clear();
         for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
             if (!event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + "." + i) && !event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + ".*") || itemz[i] == null) continue;
             event.getPlayer().getInventory().setItem(i, itemz[i]);
             ArmorSetup(event.getPlayer());
         }
       }
     }
   }
}

     @EventHandler(priority=EventPriority.MONITOR)
     public void onPlayerChangedWorldEvent (PlayerChangedWorldEvent event)
     {
       	    	Player player = event.getPlayer();
       	      	if(ItemJoin.pl.getConfig().getBoolean("clear-on-world-change") == true && (ItemJoin.pl.clearonworldchange.contains(player.getWorld().getName()))){
       	    		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-clear-on-world-change") == true && event.getPlayer().isOp()) {
       	      		}
       	              else {
       	            player.getWorld();
       	            PlayerInventory pinve = player.getInventory();
       	            pinve.clear();
       	            pinve.setHelmet(null);
       	            pinve.setChestplate(null);
       	            pinve.setLeggings(null);
       	            pinve.setBoots(null);
       	         }
       	      }
       	  {
     	if(ItemJoin.pl.getConfig().getBoolean("give-on-world-change") == true && (ItemJoin.pl.giveonworldchange.contains(event.getPlayer().getWorld().getName()))){
 	    		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-give-on-world-change") == true && event.getPlayer().isOp()) {
     	      		}
     	              else {
         player.getWorld();
         if (ItemJoin.pl.worlds.contains(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName())))
       {
         player.getWorld();
         ItemStack[] itemz = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName().trim()));
         for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
             if (!event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + "." + i) && !event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + ".*") || itemz[i] == null) continue;
             event.getPlayer().getInventory().setItem(i, itemz[i]);
             ArmorSetup(event.getPlayer());
           }
         }
       }
     }
   }
}
	@EventHandler(priority=EventPriority.MONITOR)
     public void onPlayerJoin(PlayerJoinEvent event)
     {
    	 ItemJoin.pl.PlayerJoin = event.getPlayer();
    	 ItemJoin.pl.PlayerJoin2 = event.getPlayer().getDisplayName();
         ItemJoin.pl.loadItemsConfigSetup();
    	 if (ItemJoin.pl.hasMultiverse = true) {
             MultiverseCore multiverseCore = (MultiverseCore)ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Core");
             ItemJoin.pl.listen = multiverseCore.getPlayerListener();
             ItemJoin.pl.mvplayermap = ItemJoin.pl.listen.getPlayerWorld();
    	 } else {
    	 }
         ItemJoin.pl.worlds = ItemJoin.pl.getSpecialConfig("items.yml").getStringList("worldlist");
         ItemJoin.pl.clearonjoin = ItemJoin.pl.getConfig().getStringList("clear-on-join-worldlist");
         ItemJoin.pl.clearonworldchange = ItemJoin.pl.getConfig().getStringList("clear-on-world-change-worldlist");
         ItemJoin.pl.giveonworldchange = ItemJoin.pl.getConfig().getStringList("give-on-world-change-worldlist");
         ItemJoin.pl.preventdeathdrops = ItemJoin.pl.getConfig().getStringList("prevent-death-drops-worldlist");
         ItemJoin.pl.preventinventorymodify = ItemJoin.pl.getConfig().getStringList("prevent-inventory-modify-worldlist");
         ItemJoin.pl.preventpickups = ItemJoin.pl.getConfig().getStringList("prevent-pickups-worldlist");
         ItemJoin.pl.preventijplacement = ItemJoin.pl.getConfig().getStringList("prevent-itemjoin-itemplacement-worldlist");
         ItemJoin.pl.preventdrops = ItemJoin.pl.getConfig().getStringList("prevent-drops-worldlist");
     	Player player = event.getPlayer();
        ItemStack[] inventory = event.getPlayer().getInventory().getContents();
        ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(event.getPlayer().getDisplayName()));
       	if(ItemJoin.pl.getConfig().getBoolean("clear-on-join") == true && (ItemJoin.pl.clearonjoin.contains(player.getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-clear-on-join") == true && event.getPlayer().isOp()) {
         		}
                 else {
             player.getWorld();
             PlayerInventory pinve = player.getInventory();
             pinve.clear();
             pinve.setHelmet(null);
             pinve.setChestplate(null);
             pinve.setLeggings(null);
             pinve.setBoots(null);
          }
       	}
       	{
            if (ItemJoin.pl.getConfig().getBoolean("First-Join-Only") == true && (ItemJoin.pl.worlds.contains(event.getPlayer().getWorld().getName()) && ItemJoin.pl.getSpecialConfig("FirstJoin.yml").getString(event.getPlayer().getWorld().getName() + "." + event.getPlayer().getName().toString()) == null))
            {
                ItemStack[] itemz = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName().trim()));
                event.getPlayer().getInventory().clear();
                for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
                    if (!event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + "." + i) && !event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + ".*") || itemz[i] == null) continue;
                    event.getPlayer().getInventory().setItem(i, itemz[i]);
                    ArmorSetup(event.getPlayer());
                }
        		File playerFile =  new File (ItemJoin.pl.getDataFolder(), "FirstJoin.yml");
                FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
                playerData.set(event.getPlayer().getWorld().getName() + "." + event.getPlayer().getName().toString() + "." + "UniqueId", event.getPlayer().getUniqueId().toString());
                try {
                    playerData.save(playerFile);
                } catch (IOException e1) {
                    ItemJoin.pl.getServer().getLogger().severe("Could not save " + event.getPlayer().getName() + " to the data file FirstJoin.yml!");
                    e1.printStackTrace();
                }
            }
    	 else if(ItemJoin.pl.getConfig().getBoolean("First-Join-Only") == false) {
       if (ItemJoin.pl.worlds.contains(ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName())))
       {
         boolean setItem = false;
         int count = 0;
         for (int i = 0; i < inventory.length; i++) {
             if (toSet[i] == null || !event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + "." + i) && !event.getPlayer().hasPermission("itemjoin." + ItemJoin.pl.mvplayermap.get(event.getPlayer().getPlayerListName()) + ".*")) continue;
             {
             setItem = true;
             for (int j = 0; j < inventory.length; j++) {
               if ((inventory[j] != null) && 
                 (inventory[j].isSimilar(toSet[i]) || inventory[j].getType() == Material.SKULL_ITEM && ((SkullMeta) inventory[j].getItemMeta()).hasOwner() && ((SkullMeta) inventory[j].getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.isSkullOwner)))
               {
                 if (toSet[i].hasItemMeta())
                 {
                   if (!inventory[j].hasItemMeta()) {
                     continue;
                   }
                   if ((inventory[j].getItemMeta().hasDisplayName()) && (toSet[i].getItemMeta().hasDisplayName()) ? 
                     !inventory[j].getItemMeta().getDisplayName().equals(toSet[i].getItemMeta().getDisplayName()) : 
                     (inventory[j].getItemMeta().hasDisplayName()) || (toSet[i].getItemMeta().hasDisplayName())) {
                     continue;
                   }
                   if ((inventory[j].getItemMeta().hasLore()) && (toSet[i].getItemMeta().hasLore()))
                   {
                     if (!inventory[j].getItemMeta().getLore().equals(toSet[i].getItemMeta().getLore())) {
                       continue;
                     }
                   }
                   else
                   {
                     if (inventory[j].getItemMeta().hasLore()) {
                       continue;
                     }
                     if (toSet[i].getItemMeta().hasLore()) {
                       continue;
                     }
                   }
                 }
                 else
                 {
                   if (inventory[j].hasItemMeta()) {
                     continue;
                   }
                 }
                 setItem = false;
                 break;
               }
             }
             if (setItem) {
               if (inventory[i] != null)
               {
                 boolean seta = false;
                 for (int j = 0; j < inventory.length; j++) {
                   if (inventory[j] == null)
                   {
                     inventory[j] = toSet[i];
                     seta = true;
                     break;
                   }
                 }
                 if (!seta) {
                   count++;
                 }
               }
               else
               {
                 inventory[i] = toSet[i];
               }
             }
           }
         }
         if (count != 0)
         {
           event.getPlayer().sendMessage(ChatColor.RED + "Your inventory is full!");
           event.getPlayer().sendMessage(ChatColor.RED + "Couldn't give you " + count + " items!");
         }
         for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
           event.getPlayer().getInventory().setItem(i, inventory[i]);
         }
         ArmorSetup(event.getPlayer());
       }
      }
   }
}

	public void ArmorSetup(Player player)
	{
		EntityEquipment Equipment = player.getEquipment();
        ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(player.getDisplayName()));
        if (toSet[103 - 1] != null && Equipment.getHelmet() != null && !Equipment.getHelmet().isSimilar(toSet[103 - 1])) {
           	Equipment.setHelmet(toSet[103 - 1]);
          }
         else if (toSet[103 - 1] != null && Equipment.getHelmet() == null)
         {
        	 Equipment.setHelmet(toSet[103 - 1]);
         }
          if (toSet[102 - 1] != null && Equipment.getChestplate() != null && !Equipment.getChestplate().isSimilar(toSet[102 - 1])) {
           	Equipment.setChestplate(toSet[102 - 1]);
          }
          else if (toSet[102 - 1] != null && Equipment.getChestplate() == null)
          {
         	 Equipment.setChestplate(toSet[102 - 1]);
          }
          if (toSet[101 - 1] != null && Equipment.getLeggings() != null && !Equipment.getLeggings().isSimilar(toSet[101 - 1])) {
           	Equipment.setLeggings(toSet[101 - 1]);
          }
          else if (toSet[101 - 1] != null && Equipment.getLeggings() == null)
          {
         	 Equipment.setLeggings(toSet[101 - 1]);
          }
         if (toSet[100 - 1] != null && Equipment.getBoots() != null && !Equipment.getBoots().isSimilar(toSet[100 - 1])) {
          	Equipment.setBoots(toSet[100 - 1]);
         }
         else if (toSet[100 - 1] != null && Equipment.getBoots() == null)
         {
        	 Equipment.setBoots(toSet[100 - 1]);
         }
	}
	
    @SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPreventPlayerPlace(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ItemJoin.pl.getConfig().getBoolean("prevent-itemjoin-itemplacement") == true && ItemJoin.pl.preventijplacement.contains(event.getPlayer().getWorld().getName())){
	        	 if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-itemjoin-itemplacement") == true && event.getPlayer().isOp()) {
         		}
                else if (event.getPlayer().getItemInHand().getType().isBlock() || event.getPlayer().getItemInHand().getType() == Material.SKULL_ITEM && ((SkullMeta) event.getPlayer().getItemInHand().getItemMeta()).hasOwner() && ((SkullMeta) event.getPlayer().getItemInHand().getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.isSkullOwner)){
                	ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.mvplayermap.get(event.getPlayer().getDisplayName()));
    				ItemStack[] inventory = event.getPlayer().getInventory().getContents();
    		         for (int i = 0; i < inventory.length; i++) {
    		            	 for (int j = 0; j < inventory.length; j++) {
    		            	 if ((inventory[j] != null) && (toSet[i] != null) && (inventory[j].isSimilar(toSet[i])) && event.getPlayer().getInventory().getItemInHand().isSimilar(inventory[j]) || event.getPlayer().getItemInHand().getType() == Material.SKULL_ITEM && ((SkullMeta) event.getPlayer().getItemInHand().getItemMeta()).hasOwner() && ((SkullMeta) event.getPlayer().getItemInHand().getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.isSkullOwner)) {
    		                     		event.setCancelled(true);
    		                     		event.getPlayer().updateInventory();
    		                        	 }
    		                    else {
    		            	 }
    		             }
    		         }
               }
	      }
}
	
     @EventHandler(priority=EventPriority.HIGHEST)
     public void onPreventPlayerDropping(PlayerDropItemEvent event)
     {
     	if(ItemJoin.pl.getConfig().getBoolean("prevent-drops") == true && (ItemJoin.pl.preventdrops.contains(event.getPlayer().getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-drops") == true && event.getPlayer().isOp()) {
     		}
             else {
             	event.setCancelled(true);
   	       }
       }
}

     @EventHandler(priority=EventPriority.HIGH)
     public void onPreventModifyInventory(InventoryClickEvent event)
     {
     	if(ItemJoin.pl.getConfig().getBoolean("prevent-inventory-modify") == true && (ItemJoin.pl.preventinventorymodify.contains(event.getWhoClicked().getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-inventory-modify") == true && event.getWhoClicked().isOp()) {
         		}
                 else {
                 	event.setCancelled(true);
       }
    }
}

     @EventHandler(priority=EventPriority.HIGH)
 	  public void onPreventItemPickups(PlayerPickupItemEvent event)
 	  {
         if(ItemJoin.pl.getConfig().getBoolean("prevent-pickups") == true && (ItemJoin.pl.preventpickups.contains(event.getPlayer().getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-pickups") == true && event.getPlayer().isOp()) {
         		}
                 else {
                 	event.setCancelled(true);
 	  }
    }
}
 	  
     @EventHandler(priority=EventPriority.HIGH)
 	  public void onPreventDeathDrops(PlayerDeathEvent event)
 	  {
     	if(ItemJoin.pl.getConfig().getBoolean("prevent-death-drop") == true && (ItemJoin.pl.preventdeathdrops.contains(event.getEntity().getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-death-drop") == true && event.getEntity().isOp()) {
         		}
                 else {
                     event.setDroppedExp(0);
                     event.getDrops().clear();
 	  }
    }
}
     
 // Bind Commands //

     @EventHandler(priority=EventPriority.HIGHEST)
     public void onInteractdoBoundCommands(PlayerInteractEvent event)
     {
       Player p = event.getPlayer();
       String world = p.getWorld().getName();
       for (int slot = 1; slot < 103; slot++) {
  		 long playersCooldownList = 0;
  		 if (playersOnCooldown.containsKey(event.getPlayer().getName() + world + slot)) {
			 playersCooldownList = playersOnCooldown.get(event.getPlayer().getName() + world + slot);
		 }
         p.getWorld();
		 cdtime = ItemJoin.pl.getSpecialConfig("items.yml").getInt(world + ".items." + slot + ".commands-cooldown");
		 int cdmillis = cdtime * 1000;
         if (p.getItemInHand().getType() == Material.getMaterial(ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".id")))
         {
        	 if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        	 {
               List<?> command = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + slot + ".commands"));
               if (System.currentTimeMillis()-playersCooldownList>=cdmillis) {
              for (int v = 0; v < command.size(); v++) {
                 String commands = ((String)command.get(v)).replace("%player%", p.getName());
                 commands = ItemJoin.pl.translateCodes(commands);
                 p.performCommand(commands);
                 playersOnCooldown.put(event.getPlayer().getName() + world + slot, System.currentTimeMillis());
                   }
                  } 
               else if (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name") != null && ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
            	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
      			 String inhand = ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name");
      			 String cooldownmsg = (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemNames.lookupWithAmount(event.getItem())));
      			cooldownmsg = ItemJoin.pl.translateCodes(cooldownmsg);
      			 event.getPlayer().sendMessage(cooldownmsg);
               }
               else if (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
            	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
      			 String cooldownmsg = (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", ItemNames.lookupWithAmount(event.getItem()).replace("%itemraw%", ItemNames.lookupWithAmount(event.getItem()))));
      			cooldownmsg = ItemJoin.pl.translateCodes(cooldownmsg);
      			 event.getPlayer().sendMessage(cooldownmsg);
               }
               else {
                 }
               }
             } else {
         }
     }
 }
}
