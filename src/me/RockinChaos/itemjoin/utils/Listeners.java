package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

import me.RockinChaos.itemjoin.ItemJoin;

public class Listeners implements Listener {

    private Map<String, Long> playersOnCooldown = new HashMap<String, Long>();
	private int cdtime = 0;

 // Listeners //

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event)
    {
	    	final Player player = event.getPlayer();
	    	final String world = player.getWorld().getName().toLowerCase();
		        ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
  	    		if(ItemJoin.pl.worlds.contains(world)) {
  	    			setGArmor(player, world, "respawn");
  	    			for (int i = 1; i < 37; i++) {
  	    			Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  					List<?> RespawnList = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
  					String Respawn = RespawnList.toString();
  					if (FirstJoin != true && Respawn.contains("respawn")) {
  						if (Respawn.contains("AllowOPBypass") && player.isOp() || Respawn.contains("CreativeBypass")) {
  						} else if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  						player.getInventory().setItem(i - 1, items[i - 1]);
  						}
  	    	     }
 	         }
	   	}
}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onChangedWorld(PlayerChangedWorldEvent event)
    {
	    	final Player player = event.getPlayer();
	    	final String world = player.getWorld().getName().toLowerCase();
		    ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
			Boolean FirstJoinMode = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
   	      	if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-world-change") == true && (ItemJoin.pl.worlds.contains(world))){
   	    		if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
   	      		} else {
   	            PlayerInventory pinve = player.getInventory();
   	            pinve.clear();
   	            pinve.setHelmet(null);
   	            pinve.setChestplate(null);
   	            pinve.setLeggings(null);
   	            pinve.setBoots(null);
   	         }
   	      }
	    		if(ItemJoin.pl.worlds.contains(world)) {
	    			setGArmor(player, world, "world-changed");
  	    			for (int i = 100; i < 104; i++) {
  					Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  					if (FirstJoin == true && FirstJoinMode == true) {
 				  		 String FirstFindPlayer = ItemJoin.pl.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
 				  		if (FirstFindPlayer == null) {
  						if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  							if (i == 100) {
  						    	player.getEquipment().setBoots(items[i - 1]);
  								} else if (i == 101) {
  						 	    player.getEquipment().setLeggings(items[i - 1]);
  								} else if (i == 102) {
  						 	 	player.getEquipment().setChestplate(items[i - 1]);
  						 	    } else if (i == 103) {
  						 	 	player.getEquipment().setHelmet(items[i - 1]);
  						   }
  						 }
  						}
  					  }
  	    		    }
  	    			for (int i = 1; i < 37; i++) {
  	    		    Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  					List<?> onChangedList = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
  					String onChanged = onChangedList.toString();
  					if (FirstJoinMode != true && onChanged.contains("world-changed")) {
  						if (onChanged.contains("AllowOPBypass") && player.isOp() || onChanged.contains("CreativeBypass")) {
  						} else if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  						player.getInventory().setItem(i - 1, items[i - 1]);
  						}
  					} else if (FirstJoin == true && FirstJoinMode == true) {
  				  		 String FirstFindPlayer = ItemJoin.pl.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
  				  		if (FirstFindPlayer == null) {
  	  				    if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  	  					  player.getInventory().setItem(i - 1, items[i - 1]);
  					}
  				  }
  				}
  	    	}
  	   if (FirstJoinMode == true) {
  		 String FirstFindPlayer = ItemJoin.pl.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
  		if (FirstFindPlayer == null) {
  		File playerFile =  new File (ItemJoin.pl.getDataFolder(), "FirstJoin.yml");
  		FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
  		playerData.set(world + "." + player.getName().toString() + "." + "UniqueId", player.getUniqueId().toString());
  		try {
  		playerData.save(playerFile);
  		} catch (IOException e1) {
  		ItemJoin.pl.getServer().getLogger().severe("Could not save " + player.getName() + " to the data file FirstJoin.yml!");
  		e1.printStackTrace();
  	   }
  	   }
  	   }
   	 }
}

	@EventHandler
     public void onPlayerJoin(PlayerJoinEvent event)
     {
    	 ItemJoin.pl.PlayerJoin = event.getPlayer();
    	 ItemJoin.pl.PlayerJoin2 = event.getPlayer().getName();
         ItemJoin.pl.CacheItems();
         final Player player = event.getPlayer();
         final String world = player.getWorld().getName().toLowerCase();
         ItemStack[] inventory = player.getInventory().getContents();
         ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
         Boolean FirstJoinMode = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
       	if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-join") == true && (ItemJoin.pl.worlds.contains(world))){
     		if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
         		}
                 else {
                     player.getInventory().clear();
                     player.getInventory().setHelmet(null);
                     player.getInventory().setChestplate(null);
                     player.getInventory().setLeggings(null);
                     player.getInventory().setBoots(null);
          }
       	}
        if (FirstJoinMode == true) {
		 String FirstFindPlayer = ItemJoin.pl.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
	  		if(ItemJoin.pl.worlds.contains(world) && FirstJoinMode == true && FirstFindPlayer == null) {
	  			for (int i = 100; i < 104; i++) {
					Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
					if (FirstJoin == true) {
						if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
							if (i == 100) {
						    	player.getEquipment().setBoots(toSet[i - 1]);
								} else if (i == 101) {
						 	    player.getEquipment().setLeggings(toSet[i - 1]);
								} else if (i == 102) {
						 	 	player.getEquipment().setChestplate(toSet[i - 1]);
						 	    } else if (i == 103) {
						 	 	player.getEquipment().setHelmet(toSet[i - 1]);
						   }
						}
					}
	  		}
	  			for (int i = 0; i < player.getInventory().getSize(); i++) {
					Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + (i + 1) + ".First-Join-Item");
					if (FirstJoin == true) {
						if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
							player.getInventory().setItem(i, toSet[i]);
						}
		       }
	  			}
	  	  	   if (FirstJoinMode == true && FirstFindPlayer == null) {
	  	   		File playerFile =  new File (ItemJoin.pl.getDataFolder(), "FirstJoin.yml");
	  	   		FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
	  	   		playerData.set(world + "." + player.getName().toString() + "." + "UniqueId", player.getUniqueId().toString());
	  	   		try {
	  	   		playerData.save(playerFile);
	  	   		} catch (IOException e1) {
	  	   		ItemJoin.pl.getServer().getLogger().severe("Could not save " + player.getName() + " to the data file FirstJoin.yml!");
	  	   		e1.printStackTrace();
	  	   	    }
	  	  	   }
	  		}
        }
       if (ItemJoin.pl.worlds.contains(world))
       {
         boolean setItem = false;
         int count = 0;
         for (int i = 0; i < inventory.length; i++) {
             if (toSet[i] == null || !player.hasPermission("itemjoin." + world + "." + i) && !player.hasPermission("itemjoin." + world + ".*")) continue;
             {
             setItem = true;
             for (int j = 0; j < inventory.length; j++) {
               if ((inventory[j] != null) && 
                 (inventory[j].isSimilar(toSet[i]) || isSkullSimilar(player, inventory[j])))
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
           player.sendMessage(ChatColor.RED + "Your inventory is full!");
           player.sendMessage(ChatColor.RED + "Couldn't give you " + count + " items!");
         }
         for (int i = 0; i < player.getInventory().getSize(); i++) {
           Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + (i + 1) + ".First-Join-Item");
           if (FirstJoin != true) {
           player.getInventory().setItem(i, inventory[i]);
           }
         }
         ArmorSetup(player);
       }
}

	 @EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName().toLowerCase();
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "placement";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        @SuppressWarnings("deprecation")
			public void run()
	          {
	            player.updateInventory();
	          }
	     }, 1L);
	 }
}

	  @EventHandler
	  public void onInventoryModify(InventoryClickEvent event) 
	  {
	    ItemStack item = event.getCurrentItem();
	    final Player player = (Player) event.getWhoClicked();
	    final String world = player.getWorld().getName().toLowerCase();
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "inventory-modify";
	      if (!BadItem && !isAllowedItem(player, item, world, modifier, frase))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        @SuppressWarnings("deprecation")
			public void run()
	          {
	            player.updateInventory();
	          }
	      }, 1L);
	}
}

	  @EventHandler(ignoreCancelled=true)
	  public void onDrop(PlayerDropItemEvent event) 
	  {
	    ItemStack item = event.getItemDrop().getItemStack();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName().toLowerCase();
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "self-drops";
	      if (!BadItem && !isAllowedItem(player, item, world, modifier, frase))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        @SuppressWarnings("deprecation")
			public void run()
	          {
	            player.updateInventory();
	          }
	      }, 1L);
     }
}

	  @EventHandler(ignoreCancelled=true)
	  public void onDeathDrops(PlayerDeathEvent event) 
	  {
		  List<ItemStack> drops = event.getDrops();
		  ListIterator<ItemStack> litr = drops.listIterator();
	    final Player player = event.getEntity();
	    final String world = player.getWorld().getName().toLowerCase();
	    String modifier = ".prevent-modifiers";
	    String frase = "death-drops";
        while(litr.hasNext()){
            ItemStack stack = litr.next();
    	    boolean BadItem = stack == null;
	      if (!BadItem && !isAllowedItem(player, stack, world, modifier, frase))
	      {
	    	  litr.remove();
	    }
    }
}

	  @EventHandler(ignoreCancelled=true)
	  public void onPickup(PlayerPickupItemEvent event)
	   {
		  final Player player = event.getPlayer();
		  final String world = player.getWorld().getName().toLowerCase();
		  boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "prevent-pickups") == true && (ItemJoin.pl.worlds.contains(world))){
	      if(ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "AllowOPBypass") == true
	      		&& player.isOp()
	      		|| ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "CreativeBypass") == true
	      		&& Creative) {
	       } else {
	         event.setCancelled(true);
	  	 }
	 }
}

	  @EventHandler
	  public void onInteractCommands(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = (Player) event.getPlayer();
	    final String world = player.getWorld().getName().toLowerCase();
	    boolean BadItem = item == null;
	    ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
	    String action = event.getAction().toString();
	    for (int i = 1; i < 37; i++) {
	      if (!BadItem && item.isSimilar(items[i - 1])
	    		  && isCommandable(player, world, i, action)
	    		  || !BadItem && isSkullSimilar(player, item)
	    		  && isCommandable(player, world, i, action))
	      {
	        sendCommands(player, world, item);
	   }
   }
}

	   public boolean isAllowedItem(Player player, ItemStack item, String world, String modifier, String frase) {
		   Boolean Allowed = true;
		   boolean Creative = player.getGameMode() == GameMode.CREATIVE;
		   ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		   for (int i = 1; i < 38; i++) {
				List<?> Modifiers = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + i + modifier));
				String Mods = Modifiers.toString();
				if (Mods.contains(frase) && item.isSimilar(items[i - 1]) || Mods.contains(frase) && isSkullSimilar(player, item)) {
					if (Mods.contains("AllowOPBypass") && player.isOp() || Mods.contains("CreativeBypass") && Creative) {
					} else {
						Allowed = false;
					}
				}
		   }
		   for (int A = 100; A < 104; A++) {
				List<?> Modifiers = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + A + modifier));
				String Mods = Modifiers.toString();
				if (Mods.contains(frase) && item.isSimilar(items[A - 1])|| Mods.contains(frase) && isSkullSimilar(player, item)) {
					if (Mods.contains("AllowOPBypass") && player.isOp() || Mods.contains("CreativeBypass") && Creative) {
					} else {
						Allowed = false;
					}
				}
		  }
	    return Allowed;
}

	   public boolean isSkullSimilar(Player player, ItemStack item) {
		   boolean isSimilar = false;
		   if (item.getType() == Material.SKULL_ITEM 
				   && ((SkullMeta) item.getItemMeta()).hasOwner() 
				   && ((SkullMeta) item.getItemMeta()).hasDisplayName()
			       && ((SkullMeta) item.getItemMeta()).getDisplayName().contains(ItemJoin.encodeItemData(ItemJoin.secretMsg))) {
			   isSimilar = true;
		  }
   return isSimilar;	   
}

	   public void setGArmor(Player player, String world, String frase) {
		   ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		   Boolean FirstJoinMode = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
		if (FirstJoinMode != true) {
   		 for (int i = 100; i < 104; i++) {
   			Boolean FirstJoin = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
				List<?> Modifiers = ((List<?>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
				String Mods = Modifiers.toString();
				if (FirstJoin != true && FirstJoinMode != true && Mods.contains(frase) || FirstJoin != true && FirstJoinMode != true &&Mods.contains(frase)) {
					if (Mods.contains("AllowOPBypass") && player.isOp()) {
					} else {
						if (i == 100) {
	    		        	 player.getEquipment().setBoots(items[i - 1]);
						}
						if (i == 101) {
	 	    		         player.getEquipment().setLeggings(items[i - 1]);
						}
	 	    		       if (i == 102) {
	 	 	    		     player.getEquipment().setChestplate(items[i - 1]);
	 	    		       }
	 	 	    		  if (i == 103) {
	 	 	    		     player.getEquipment().setHelmet(items[i - 1]);
	 	 	    		  }
					    }
					  }
			    }
			}
	   }
}
	   
		public void ArmorSetup(Player player)
		{
			EntityEquipment Equipment = player.getEquipment();
			final String world = player.getWorld().getName().toLowerCase();
	        ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
	        Boolean FirstJoin103 = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + "103" + ".First-Join-Item");
	        Boolean FirstJoin102 = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + "102" + ".First-Join-Item");
	        Boolean FirstJoin101 = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + "101" + ".First-Join-Item");
	        Boolean FirstJoin100 = ItemJoin.pl.getSpecialConfig("items.yml").getBoolean(world + ".items." + "100" + ".First-Join-Item");
	        if (toSet[103 - 1] != null
	       		 && Equipment.getHelmet() != null
	       		 && Equipment.getHelmet().hasItemMeta()
	       		 && !Equipment.getHelmet().isSimilar(toSet[103 - 1])
	       		 && FirstJoin103 != true
	       		 || toSet[103 - 1] != null
	       	     && Equipment.getHelmet() != null
	       		 && Equipment.getHelmet().hasItemMeta()
	       		 && FirstJoin103 != true
	       		 && !isSkullSimilar(player, Equipment.getHelmet())) {
	         	Equipment.setHelmet(toSet[103 - 1]);
	        } else if (toSet[103 - 1] != null) {
	           Equipment.setHelmet(toSet[103 - 1]);
	        }
	        if (toSet[102 - 1] != null
	       		 && Equipment.getChestplate() != null
	       		 && Equipment.getChestplate().hasItemMeta()
	       		 && FirstJoin102 != true
	       		 && !Equipment.getChestplate().isSimilar(toSet[102 - 1])
	       		 || toSet[102 - 1] != null
	       	     && Equipment.getChestplate() != null
	       		 && Equipment.getChestplate().hasItemMeta()
	       		 && FirstJoin102 != true
	       		 && !isSkullSimilar(player, Equipment.getChestplate())) {
	         	Equipment.setChestplate(toSet[102 - 1]);
	        } else if (toSet[102 - 1] != null) {
	           Equipment.setChestplate(toSet[102 - 1]);
	        }
	        if (toSet[101 - 1] != null
	       		 && Equipment.getLeggings() != null
	       		 && Equipment.getLeggings().hasItemMeta()
	       		 && FirstJoin101 != true
	       		 && !Equipment.getLeggings().isSimilar(toSet[101 - 1])
	       		 || toSet[101 - 1] != null
	       	     && Equipment.getLeggings() != null
	       		 && Equipment.getLeggings().hasItemMeta()
	       		 && FirstJoin101 != true
	       		 && !isSkullSimilar(player, Equipment.getLeggings())) {
	         	Equipment.setLeggings(toSet[101 - 1]);
	        } else if (toSet[101 - 1] != null) {
	           Equipment.setLeggings(toSet[101 - 1]);
	        }
	         if (toSet[100 - 1] != null
	        		 && Equipment.getBoots() != null
	        		 && Equipment.getBoots().hasItemMeta()
	        		 && FirstJoin100 != true
	        		 && !Equipment.getBoots().isSimilar(toSet[100 - 1])
	        		 || toSet[100 - 1] != null
	        	     && Equipment.getBoots() != null
	        		 && Equipment.getBoots().hasItemMeta()
	        		 && FirstJoin100 != true
	        		 && !isSkullSimilar(player, Equipment.getBoots())) {
	          	Equipment.setBoots(toSet[100 - 1]);
	         } else if (toSet[100 - 1] != null) {
	            Equipment.setBoots(toSet[100 - 1]);
	  }
}

 // Commands //

	   public boolean isCommandable(Player player, String world, int slot, String action) {
		   boolean isCommandable = false;
		   String actions = ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".commands-action");
		   if (actions != null && actions.toUpperCase().contains(action)) {
			   isCommandable = true;
		  } else if (actions == null && !action.toUpperCase().equals("PHYSICAL")) {
			  isCommandable = true;
		  }
	return isCommandable;   
}
  
		   public void sendCommands(Player player, String world, ItemStack item) {
			   for (int slot = 1; slot < 38; slot++) {
		  	  		 long playersCooldownList = 0;
		  	  		 if (playersOnCooldown.containsKey(player.getName() + world + slot)) {
		  				 playersCooldownList = playersOnCooldown.get(player.getName() + world + slot);
		  			 }
		  			 cdtime = ItemJoin.pl.getSpecialConfig("items.yml").getInt(world + ".items." + slot + ".commands-cooldown");
		  			 int cdmillis = cdtime * 1000;
		  			 ItemStack[] toConfig = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		  	         if (player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1]))
		  	         {
		  	      	    ItemJoin.pl.PlayerJoin = player;
		  	    	    ItemJoin.pl.PlayerJoin2 = player.getName();
		  	        	List<String> command = ((List<String>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + slot + ".commands"));
		                if (System.currentTimeMillis()-playersCooldownList>=cdmillis) {
		            	   for (String Identify: command) {
		            	       String[] parts = Identify.split("console: ");
		            	       String[] parts2 = Identify.split("player: ");
		            	       String[] parts3 = Identify.split("console:");
		            	       String[] parts4 = Identify.split("player:");
		            	       if (Identify.toLowerCase().contains("console: ")) {
			            	       String Command = parts[1].replace("%player%", player.getName());
			            	       Command = ItemJoin.pl.translateCodes(Command);
					               Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
					               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
		            	       } else if (Identify.toLowerCase().contains("player: ")) {
			            	       String Command = parts2[1].replace("%player%", player.getName());
			            	       Command = ItemJoin.pl.translateCodes(Command);
					               player.performCommand(Command);
					               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
		            	       } else if (Identify.toLowerCase().contains("console:")) {
			            	       String Command = parts3[1].replace("%player%", player.getName());
			            	       Command = ItemJoin.pl.translateCodes(Command);
					               Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
					               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
		            	       } else if (Identify.toLowerCase().contains("player:")) {
			            	       String Command = parts4[1].replace("%player%", player.getName());
			            	       Command = ItemJoin.pl.translateCodes(Command);
					               player.performCommand(Command);
					               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
		            	       } else if (!Identify.toLowerCase().contains("player: ") && !Identify.toLowerCase().contains("console: ") && !Identify.toLowerCase().contains("player:") && !Identify.toLowerCase().contains("console:")) {
			            	       String Command = Identify.replace("%player%", player.getName());
			            	       Command = ItemJoin.pl.translateCodes(Command);
					               player.performCommand(Command);
					               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
		            	       }
		            	   }
		                }
		                else if (player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1])) {
			  	        	if (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
			          	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
			    			 String inhand = ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name");
			    			 String cooldownmsg = (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemJoin.getName(item)));
			    			cooldownmsg = ItemJoin.pl.translateCodes(cooldownmsg);
			    			 player.sendMessage(cooldownmsg);
			  	        	}
		  	             }
		  	         }
			   }
			   for (int slot = 100; slot < 104; slot++) {
		  	  		 long playersCooldownList = 0;
		  	  		 if (playersOnCooldown.containsKey(player.getName() + world + slot)) {
		  				 playersCooldownList = playersOnCooldown.get(player.getName() + world + slot);
		  			 }
		  			 cdtime = ItemJoin.pl.getSpecialConfig("items.yml").getInt(world + ".items." + slot + ".commands-cooldown");
		  			 int cdmillis = cdtime * 1000;
		  			 ItemStack[] toConfig = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		  	         if (player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1]))
		  	         {
		  	        	List<String> command = ((List<String>)ItemJoin.pl.getSpecialConfig("items.yml").getStringList(world + ".items." + slot + ".commands"));
		                if (System.currentTimeMillis()-playersCooldownList>=cdmillis) {
			            	   for (String Identify: command) {
			            	       String[] parts = Identify.split("console: ");
			            	       String[] parts2 = Identify.split("player: ");
			            	       String[] parts3 = Identify.split("console:");
			            	       String[] parts4 = Identify.split("player:");
			            	       if (Identify.toLowerCase().contains("console: ")) {
				            	       String Command = parts[1].replace("%player%", player.getName());
				            	       Command = ItemJoin.pl.translateCodes(Command);
						               Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
						               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
			            	       } else if (Identify.toLowerCase().contains("player: ")) {
				            	       String Command = parts2[1].replace("%player%", player.getName());
				            	       Command = ItemJoin.pl.translateCodes(Command);
						               player.performCommand(Command);
						               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
			            	       } else if (Identify.toLowerCase().contains("console:")) {
				            	       String Command = parts3[1].replace("%player%", player.getName());
				            	       Command = ItemJoin.pl.translateCodes(Command);
						               Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
						               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
			            	       } else if (Identify.toLowerCase().contains("player:")) {
				            	       String Command = parts4[1].replace("%player%", player.getName());
				            	       Command = ItemJoin.pl.translateCodes(Command);
						               player.performCommand(Command);
						               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
			            	       } else if (!Identify.toLowerCase().contains("player: ") && !Identify.toLowerCase().contains("console: ") && !Identify.toLowerCase().contains("player:") && !Identify.toLowerCase().contains("console:")) {
				            	       String Command = Identify.replace("%player%", player.getName());
				            	       Command = ItemJoin.pl.translateCodes(Command);
						               player.performCommand(Command);
						               playersOnCooldown.put(player.getName() + world + slot, System.currentTimeMillis());
			            	       }
			            	   }
			                }
		                else if (player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1])) {
			  	        	if (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
			          	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
			    			 String inhand = ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name");
			    			 String cooldownmsg = (ItemJoin.pl.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemJoin.getName(item)));
			    			cooldownmsg = ItemJoin.pl.translateCodes(cooldownmsg);
			    			 player.sendMessage(cooldownmsg);
			  	        	}
		  	         }
		  	       }
			  }
	   }
}
