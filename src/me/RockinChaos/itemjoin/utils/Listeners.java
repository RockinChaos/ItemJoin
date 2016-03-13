package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
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
	    	final String world = getWorld(player.getWorld().getName());
		        ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
  	    		if(isWorld(world)) {
  	    			setGArmor(player, world, "respawn");
  	    			for (int i = 1; i < 37; i++) {
  	    			Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  	    			Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
  					List<?> RespawnList = ((List<?>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
  					String Respawn = RespawnList.toString();
  					if (Respawn.contains("respawn")) {
  						if (Respawn.contains("AllowOPBypass") && player.isOp() || Respawn.contains("CreativeBypass")) {
  						} else if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  						if (FirstJoinMode != true)
  							player.getInventory().setItem(i - 1, items[i - 1]);
  						} else if (FirstJoin != true) {
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
	    	final String world = getWorld(player.getWorld().getName());
   	      	if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-world-change") == true && (isWorld(world))){
   	    		if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
   	      		} else {
   	            PlayerInventory pinve = player.getInventory();
   	            pinve.clear();
   	            pinve.setHelmet(null);
   	            pinve.setChestplate(null);
   	            pinve.setLeggings(null);
   	            pinve.setBoots(null);
   	         }
   	      }
	    		if(isWorld(world)) {
	    		    ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
	    			Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
	    			setGArmor(player, world, "world-changed");
  	    			for (int i = 100; i < 104; i++) {
  					Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  					if (FirstJoin == true && FirstJoinMode == true) {
 				  		 String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
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
  						 	    } else if (i == 104) {
  						 	    	player.getInventory().setItemInOffHand(items[i - 1]);
  						   }
  						 }
  						}
  					  }
  	    		    }
  	    			for (int i = 1; i < 37; i++) {
  	    		    Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
  					List<?> onChangedList = ((List<?>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
  					String onChanged = onChangedList.toString();
  					if (FirstJoinMode != true && onChanged.contains("world-changed")) {
  						if (onChanged.contains("AllowOPBypass") && player.isOp() || onChanged.contains("CreativeBypass")) {
  						} else if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  						player.getInventory().setItem(i - 1, items[i - 1]);
  						}
  					} else if (FirstJoin == true && FirstJoinMode == true) {
  				  		 String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
  				  		if (FirstFindPlayer == null) {
  	  				    if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
  	  					  player.getInventory().setItem(i - 1, items[i - 1]);
  					}
  				  }
  				}
  	    	}
  	   if (FirstJoinMode == true) {
  		 String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
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
         final String world = getWorld(player.getWorld().getName());
       	if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-join") == true && (isWorld(world))){
     		if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
         		}
                 else {
                     player.getInventory().clear();
                     player.getInventory().setHelmet(null);
                     player.getInventory().setChestplate(null);
                     player.getInventory().setLeggings(null);
                     player.getInventory().setBoots(null);
          }
       	}
        ItemStack[] inventory = player.getInventory().getContents();
        ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
        Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
        if (FirstJoinMode == true) {
		 String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(world + "." + player.getName().toString());
	  		if(isWorld(world) && FirstJoinMode == true && FirstFindPlayer == null) {
	  			for (int i = 100; i < 104; i++) {
					Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
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
  						 	    } else if (i == 104) {
  						 	    	player.getInventory().setItemInOffHand(toSet[i - 1]);
						   }
						}
					}
	  		}
	  			for (int i = 0; i < player.getInventory().getSize(); i++) {
					Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + (i + 1) + ".First-Join-Item");
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
       if (isWorld(world))
       {
         boolean setItem = false;
         int count = 0;
         for (int i = 0; i < inventory.length; i++) {
             if (toSet[i] == null || !player.hasPermission("itemjoin." + world + "." + i) && !player.hasPermission("itemjoin." + world + ".*")) continue;
             {
             setItem = true;
             for (int j = 0; j < inventory.length; j++) {
               if ((inventory[j] != null) && 
                 (isSkullSimilar(player, inventory[j], j + 1, world) || inventory[j].isSimilar(toSet[i])))
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
           Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + (i + 1) + ".First-Join-Item");
           if (FirstJoinMode != true) {
           player.getInventory().setItem(i, inventory[i]);
           } else if (FirstJoin != true) {
               player.getInventory().setItem(i, inventory[i]);
               }
         }
         ArmorSetup(player);
       }
}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
	    final Player player = event.getPlayer();
	    ItemStack item = player.getItemInHand();
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-blocks";
	      if (isWorld(world) && !BadItem && isTool(item) && !isAllowedItem(player, item, world, modifier, frase))
	      {
	    	item.setDurability((short) -1);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        public void run()
	          {
	            player.updateInventory();
	          }
	     }, 1L);
	 }
}

	@SuppressWarnings("deprecation")
	@EventHandler
	  public void onWeaponArmorDamaged(EntityDamageByEntityEvent event) 
	  {
		 ItemStack item = null;
		 Player player = null;
		 if (event.getDamager() instanceof Player) {
	     item = ((Player) event.getDamager()).getItemInHand();
	     player = (Player) event.getDamager();
		 } else if (event.getEntity() instanceof Player) {
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
             for (ItemStack i : armor){
                     item = i;
             }
			 player = (Player) event.getEntity();
		 }
		final Player update = player;
		final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-living";
	    if(event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
	      if (isWorld(world) && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
	      {
	    	  if (event.getDamager() instanceof Player) {
	    		  item.setDurability((short) -1);
            } else if (event.getEntity() instanceof Player) {
                    ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
                    for (ItemStack i : armor){
                            i.setDurability((short) -1);
                    }
	  	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
		        {
		        public void run()
		          {
		            update.updateInventory();
		          }
		     }, 1L);
            }
	 }
	}
}

	 @EventHandler
	  public void onBowShootDamaged(EntityShootBowEvent event) 
	  {
	    ItemStack item = event.getBow();
	    final Player player = (Player) event.getEntity();
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-living";
	    if(event.getEntity() instanceof Player) {
	      if (isWorld(world) && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
	      {
          	item.setDurability((short) -1);
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
}

	 @EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "placement";
	      if (isWorld(world) && event.getAction() == Action.RIGHT_CLICK_BLOCK && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
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
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "inventory-modify";
	      if (isWorld(world) && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
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
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    String modifier = ".prevent-modifiers";
	    String frase = "self-drops";
	      if (isWorld(world) && !BadItem && !isAllowedItem(player, item, world, modifier, frase))
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
	    final String world = getWorld(player.getWorld().getName());
	    String modifier = ".prevent-modifiers";
	    String frase = "death-drops";
	    if (isWorld(world)) {
        while(litr.hasNext()){
            ItemStack stack = litr.next();
    	    boolean BadItem = stack == null;
	      if (!BadItem && !isAllowedItem(player, stack, world, modifier, frase))
	      {
	    	  litr.remove();
	    }
    }
  }
}

	  @EventHandler(ignoreCancelled=true)
	  public void onPickup(PlayerPickupItemEvent event)
	   {
		  final Player player = event.getPlayer();
		  final String world = getWorld(player.getWorld().getName());
		  boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "prevent-pickups") == true && isWorld(world)){
	      if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "AllowOPBypass") == true
	      		&& player.isOp()
	      		|| ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "CreativeBypass") == true
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
	    final String world = getWorld(player.getWorld().getName());
	    boolean BadItem = item == null;
	    ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
	    String action = event.getAction().toString();
	    for (int i = 1; i < 37; i++) {
	      if (isWorld(world) && !BadItem
	    		  && isSkullSimilar(player, item, i, world)
	    		  && isCommandable(player, getWorld(world), i, action)
	    		  || isWorld(world) && !BadItem
	    		  && item.isSimilar(items[i - 1])
	    		  && isCommandable(player, getWorld(world), i, action))
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
				List<?> Modifiers = ((List<?>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + i + modifier));
				String Mods = Modifiers.toString();
				if (Mods.contains(frase) && isSkullSimilar(player, item, i, world) || Mods.contains(frase) && item.isSimilar(items[i - 1])) {
					if (Mods.contains("AllowOPBypass") && player.isOp() || Mods.contains("CreativeBypass") && Creative) {
					} else {
						Allowed = false;
					}
				}
		   }
		   for (int A = 100; A < 104; A++) {
				List<?> Modifiers = ((List<?>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + A + modifier));
				String Mods = Modifiers.toString();
				if (Mods.contains(frase) && isSkullSimilar(player, item, A, world) || Mods.contains(frase) && item.isSimilar(items[A - 1])) {
					if (Mods.contains("AllowOPBypass") && player.isOp() || Mods.contains("CreativeBypass") && Creative) {
					} else {
						Allowed = false;
					}
				}
		  }
	    return Allowed;
}

	   public boolean isSkullSimilar(Player player, ItemStack item, int slot, String world) {
		   boolean isSimilar = false;
		   if (item.getType() == Material.SKULL_ITEM 
				   && Material.getMaterial(ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".id")) == Material.SKULL_ITEM
				   && item.hasItemMeta()
				   && item.getItemMeta().hasDisplayName()
			       && ((SkullMeta) item.getItemMeta()).getDisplayName().contains(ItemJoin.encodeItemData(ItemJoin.secretMsg))) {
			   isSimilar = true;
		  }
   return isSimilar;	   
}

	   public void setGArmor(Player player, String world, String frase) {
		   ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		   Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
		if (FirstJoinMode != true) {
   		 for (int i = 100; i < 104; i++) {
   			Boolean FirstJoin = ItemJoin.getSpecialConfig("items.yml").getBoolean(world + ".items." + i + ".First-Join-Item");
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + i) || player.hasPermission("itemjoin." + world + ".*")) {
				List<?> Modifiers = ((List<?>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + i + ".give-on-modifiers"));
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
				 	      if (i == 104) {
					 	    	player.getInventory().setItemInOffHand(items[i - 1]);
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
			final String world = getWorld(player.getWorld().getName());
	        ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
	        Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
	        if (isWorld(world)) {
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + "103") || player.hasPermission("itemjoin." + world + ".*")) {
	        if (toSet[103 - 1] != null
	       		 && Equipment.getHelmet() != null
	       		 && Equipment.getHelmet().hasItemMeta()
	       		 && !isSkullSimilar(player, Equipment.getHelmet(), 103, world)
	       		 && FirstJoinMode != true
	       		 || toSet[103 - 1] != null
	       	     && Equipment.getHelmet() != null
	       		 && Equipment.getHelmet().hasItemMeta()
	       		 && FirstJoinMode != true
	    	     && !Equipment.getHelmet().isSimilar(toSet[103 - 1])) {
	         	Equipment.setHelmet(toSet[103 - 1]);
	        } else if (toSet[103 - 1] != null) {
	           Equipment.setHelmet(toSet[103 - 1]);
	        }
			}
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + "104") || player.hasPermission("itemjoin." + world + ".*")) {
		        if (toSet[104 - 1] != null
		       		 && Equipment.getItemInOffHand() != null
		       		 && Equipment.getItemInOffHand().hasItemMeta()
		       		 && !isSkullSimilar(player, Equipment.getHelmet(), 104, world)
		       		 && FirstJoinMode != true
		       		 || toSet[104 - 1] != null
		       	     && Equipment.getItemInOffHand() != null
		       		 && Equipment.getItemInOffHand().hasItemMeta()
		       		 && FirstJoinMode != true
		    	     && !Equipment.getItemInOffHand().isSimilar(toSet[104 - 1])) {
		         	Equipment.setItemInOffHand(toSet[104 - 1]);
		        } else if (toSet[104 - 1] != null) {
		           Equipment.setItemInOffHand(toSet[104 - 1]);
		        }
				}
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + "102") || player.hasPermission("itemjoin." + world + ".*")) {
	        if (toSet[102 - 1] != null
	       		 && Equipment.getChestplate() != null
	       		 && Equipment.getChestplate().hasItemMeta()
	       		 && FirstJoinMode != true
	       		 && !isSkullSimilar(player, Equipment.getChestplate(), 102, world)
	       		 || toSet[102 - 1] != null
	       	     && Equipment.getChestplate() != null
	       		 && Equipment.getChestplate().hasItemMeta()
	       		 && FirstJoinMode != true
	    	     && !Equipment.getChestplate().isSimilar(toSet[102 - 1])) {
	         	Equipment.setChestplate(toSet[102 - 1]);
	        } else if (toSet[102 - 1] != null) {
	           Equipment.setChestplate(toSet[102 - 1]);
	        }
			}
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + "101") || player.hasPermission("itemjoin." + world + ".*")) {
	        if (toSet[101 - 1] != null
	       		 && Equipment.getLeggings() != null
	       		 && Equipment.getLeggings().hasItemMeta()
	       		 && FirstJoinMode != true
	       		 && !isSkullSimilar(player, Equipment.getLeggings(), 101, world)
	       		 || toSet[101 - 1] != null
	       	     && Equipment.getLeggings() != null
	       		 && Equipment.getLeggings().hasItemMeta()
	       		 && FirstJoinMode != true
	       		 && !Equipment.getLeggings().isSimilar(toSet[101 - 1])) {
	         	Equipment.setLeggings(toSet[101 - 1]);
	        } else if (toSet[101 - 1] != null) {
	           Equipment.setLeggings(toSet[101 - 1]);
	        }
			}
			if (player.hasPermission("itemjoin." + world) || player.hasPermission("itemjoin." + world + "." + "100") || player.hasPermission("itemjoin." + world + ".*")) {
	         if (toSet[100 - 1] != null
	        		 && Equipment.getBoots() != null
	        		 && Equipment.getBoots().hasItemMeta()
	        		 && FirstJoinMode != true
	        		 && !isSkullSimilar(player, Equipment.getBoots(), 100, world)
	        		 || toSet[100 - 1] != null
	        	     && Equipment.getBoots() != null
	        		 && Equipment.getBoots().hasItemMeta()
	        		 && FirstJoinMode != true
	        		 && !Equipment.getBoots().isSimilar(toSet[100 - 1])) {
	          	Equipment.setBoots(toSet[100 - 1]);
	         } else if (toSet[100 - 1] != null) {
	            Equipment.setBoots(toSet[100 - 1]);
	  }
	}
   }
}

	@SuppressWarnings("deprecation")
	public boolean isTool(ItemStack item) {	
	 boolean isTool = false;
	 ArrayList<Integer> Tools = new ArrayList<Integer>();
	 Tools.add(268);
	 Tools.add(269);
	 Tools.add(270);
	 Tools.add(271);
	 Tools.add(290);
	 Tools.add(272);
	 Tools.add(273);
	 Tools.add(274);
	 Tools.add(275);
	 Tools.add(291);
	 Tools.add(267);
	 Tools.add(257);
	 Tools.add(256);
	 Tools.add(258);
	 Tools.add(292);
	 Tools.add(283);
	 Tools.add(284);
	 Tools.add(285);
	 Tools.add(286);
	 Tools.add(293);
	 Tools.add(276);
	 Tools.add(277);
	 Tools.add(278);
	 Tools.add(279);
	 Tools.add(294);
	 if (Tools.contains(item.getTypeId())) {
	   isTool = true;
	}
	return isTool; 
}

	public boolean isWorld(String world) {	
	 boolean isWorld = false;
     for (String worlds : ItemJoin.pl.worlds) {
	   if (worlds.equalsIgnoreCase(world)) {
		 isWorld = true;
	   }
    }
	return isWorld; 
}
	public static String getWorld(String world) {	
	 String value = world;
	 for (String key : ItemJoin.getSpecialConfig("items.yml").getKeys(false)) {
	    if (key.equalsIgnoreCase(value)) {
	      value = key;
	    }
     }
    return value;
}

 // Commands //

	   public boolean isCommandable(Player player, String world, int slot, String action) {
		   boolean isCommandable = false;
		   String actions = ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".commands-action");
		   if (actions != null && actions.toUpperCase().contains(action)) {
			   isCommandable = true;
		  } else if (actions == null && !action.toUpperCase().equals("PHYSICAL")) {
			  isCommandable = true;
		  }
	return isCommandable;   
}
  
		   @SuppressWarnings("deprecation")
		public void sendCommands(Player player, String world, ItemStack item) {
			   for (int slot = 1; slot < 38; slot++) {
		  	  		 long playersCooldownList = 0;
		  	  		 if (playersOnCooldown.containsKey(player.getName() + world + slot)) {
		  				 playersCooldownList = playersOnCooldown.get(player.getName() + world + slot);
		  			 }
		  			 cdtime = ItemJoin.getSpecialConfig("items.yml").getInt(world + ".items." + slot + ".commands-cooldown");
		  			 int cdmillis = cdtime * 1000;
		  			 ItemStack[] toConfig = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		  	         if (player.getItemInHand() !=null && isSkullSimilar(player, player.getItemInHand(), slot, world) || player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1]))
		  	         {
		  	      	    ItemJoin.pl.PlayerJoin = player;
		  	    	    ItemJoin.pl.PlayerJoin2 = player.getName();
		  	        	List<String> command = ((List<String>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + slot + ".commands"));
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
		                else if (player.getItemInHand() !=null && isSkullSimilar(player, player.getItemInHand(), slot, world) || player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1])) {
			  	        	if (ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
			          	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
			    			 String inhand = ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name");
			    			 String cooldownmsg = (ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemJoin.getName(item)));
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
		  			 cdtime = ItemJoin.getSpecialConfig("items.yml").getInt(world + ".items." + slot + ".commands-cooldown");
		  			 int cdmillis = cdtime * 1000;
		  			 ItemStack[] toConfig = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
		  	         if (player.getItemInHand() !=null && isSkullSimilar(player, player.getItemInHand(), slot, world) || player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1]))
		  	         {
		  	        	List<String> command = ((List<String>)ItemJoin.getSpecialConfig("items.yml").getStringList(world + ".items." + slot + ".commands"));
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
		                else if (player.getItemInHand() !=null && isSkullSimilar(player, player.getItemInHand(), slot, world)  || player.getItemInHand() !=null && player.getItemInHand().isSimilar(toConfig[slot - 1])) {
			  	        	if (ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message") != null) {
			          	   int timeLeft = (int) (cdtime-((System.currentTimeMillis()-playersCooldownList)/1000));
			    			 String inhand = ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".name");
			    			 String cooldownmsg = (ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + slot + ".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemJoin.getName(item)));
			    			cooldownmsg = ItemJoin.pl.translateCodes(cooldownmsg);
			    			 player.sendMessage(cooldownmsg);
			  	        	}
		  	         }
		  	       }
			  }
	   }
}
