package me.xsilverslayerx.utils;

import java.util.List;
import me.xsilverslayerx.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class Listeners implements Listener{
	
 // Listeners //  

     @EventHandler(priority=EventPriority.HIGHEST)
     public void onPlayerRespawn(PlayerRespawnEvent event)
     {
       if (ItemJoin.pl.worlds.contains(ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName())))
       {
         ItemStack[] itemz = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName().trim()));
         event.getPlayer().getInventory().clear();
         for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
           if (((event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + "." + i)) || (event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + ".*"))) && 
             (itemz[i] != null)) {
             event.getPlayer().getInventory().setItem(i, itemz[i]);
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
       	              // Do nothing because he is OP //
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
     	              // Do nothing because he is OP //
     	      		}
     	              else {
         player.getWorld();
         if (ItemJoin.pl.worlds.contains(ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName())))
       {
         player.getWorld();
         ItemStack[] itemz = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName().trim()));
         for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
           if (((event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + "." + i)) || (event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + ".*"))) && 
             (itemz[i] != null)) {
             event.getPlayer().getInventory().setItem(i, itemz[i]);
            }
           }
         }
       }
     }
   }
 }

     @EventHandler(priority=EventPriority.MONITOR)
     public void onPlayerJoin(PlayerJoinEvent event)
     {
         MultiverseCore multiverseCore = (MultiverseCore)ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Core");
         ItemJoin.pl.listen = multiverseCore.getPlayerListener();
         ItemJoin.pl.playermap = ItemJoin.pl.listen.getPlayerWorld();
         ItemJoin.pl.worlds = ItemJoin.getItemsConfig().getStringList("worldlist");
         ItemJoin.pl.clearonjoin = ItemJoin.pl.getConfig().getStringList("clear-on-join-worldlist");
         ItemJoin.pl.clearonworldchange = ItemJoin.pl.getConfig().getStringList("clear-on-world-change-worldlist");
         ItemJoin.pl.giveonworldchange = ItemJoin.pl.getConfig().getStringList("give-on-world-change-worldlist");
         ItemJoin.pl.preventdeathdrops = ItemJoin.pl.getConfig().getStringList("prevent-death-drops-worldlist");
         ItemJoin.pl.preventinventorymodify = ItemJoin.pl.getConfig().getStringList("prevent-inventory-modify-worldlist");
         ItemJoin.pl.preventpickups = ItemJoin.pl.getConfig().getStringList("prevent-pickups-worldlist");
         ItemJoin.pl.preventdrops = ItemJoin.pl.getConfig().getStringList("prevent-drops-worldlist");
     	Player player = event.getPlayer();
       	if(ItemJoin.pl.getConfig().getBoolean("clear-on-join") == true && (ItemJoin.pl.clearonjoin.contains(player.getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-clear-on-join") == true && event.getPlayer().isOp()) {
                 // Do nothing because he is OP //
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
       ItemJoin.pl.playermap = ItemJoin.pl.listen.getPlayerWorld();
       if (ItemJoin.pl.worlds.contains(ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName())))
       {
         boolean setItem = false;
         ItemStack[] inventory = event.getPlayer().getInventory().getContents();
         ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(ItemJoin.pl.playermap.get(event.getPlayer().getDisplayName()));
         int count = 0;
         for (int i = 0; i < inventory.length; i++) {
           if ((toSet[i] != null) && (
             (event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + "." + i)) || (event.getPlayer().hasPermission("" + (String)ItemJoin.pl.playermap.get(event.getPlayer().getPlayerListName()) + ".*"))))
           {
             setItem = true;
             for (int j = 0; j < inventory.length; j++) {
               if ((inventory[j] != null) && 
                 (inventory[j].isSimilar(toSet[i])))
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
       }
      }
     }
     
     @EventHandler(priority=EventPriority.HIGHEST)
     public void onPreventPlayerDropping(PlayerDropItemEvent event)
     {
     	if(ItemJoin.pl.getConfig().getBoolean("prevent-drops") == true && (ItemJoin.pl.preventdrops.contains(event.getPlayer().getWorld().getName()))){
     		if(ItemJoin.pl.getConfig().getBoolean("AllowOPBypass-prevent-drops") == true && event.getPlayer().isOp()) {
             // Do nothing because he is OP //
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
                 // Do nothing because he is OP //
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
                 // Do nothing because he is OP //
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
                 // Do nothing because he is OP //
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
       for (int slot = 0; slot < 36; slot++) {
         p.getWorld();
         if (p.getItemInHand().getType() == Material.getMaterial(ItemJoin.getItemsConfig().getString(world + ".items." + slot + ".id")))
         {
               List<?> command = ((List<?>)ItemJoin.getItemsConfig().getStringList(world + ".items." + slot + ".commands"));
              for (int v = 0; v < command.size(); v++) {
                 String commands = ((String)command.get(v)).replace("%player%", p.getName());
                 p.performCommand(commands);
               }
            }
         else {
         	// This means there is no Commands: (StringList) for the item in the items.yml //
         }
     }
 }
}
