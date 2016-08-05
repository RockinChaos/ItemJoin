package me.RockinChaos.itemjoin.CacheItems;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CacheItems {

	  public static void run(Player player)
	    {
	     for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
	     {
	       String world = WorldHandler.getWorld((String)ItemJoin.pl.worlds.get(i));
	       String worldCheck = WorldHandler.checkWorld(i);
	       if (ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(worldCheck) != null
	    		   && ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(worldCheck + ".items") != null) {
	       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(worldCheck + ".items");
	       if (selection == null ) {
	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "You have defined the world " + ChatColor.YELLOW + world + ChatColor.RED + " under world-list.");
	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Yet the section for defining items under " + ChatColor.YELLOW + world + ChatColor.RED + " does not exist!");
	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please consult the documentations for help.");
	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Items for " + ChatColor.YELLOW + world + ChatColor.RED + " will not be set!");
	       } else if (selection != null ) {
	       for (String item : selection.getKeys(false))
	       {
	     	ConfigurationSection items = selection.getConfigurationSection(item);
	         int dataValue = items.getInt(".data-value");
	         String slot = items.getString(".slot");
	         Material tempmat = PlayerHandlers.getMaterial(items);
		     String Modifiers = ((List<?>)items.getStringList(".itemflags")).toString();
		     String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		     String vers = pkgname.substring(pkgname.lastIndexOf('.') + 1);
		     if (!Registers.hasCombatUpdate() && items.getString(".slot").equalsIgnoreCase("Offhand")) {  
		    	 ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Your server is running " + ChatColor.YELLOW + "MC " + vers + ChatColor.RED + " and this does not have Offhand support!");
		    	 ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Because of this, the item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
		      } else if (CheckItem.CheckMaterial(tempmat, worldCheck, item) && CheckItem.CheckSlot(slot, worldCheck, item)) 
	          {
	           ItemStack tempitem = new ItemStack(tempmat, items.getInt(".count", 1),(short)dataValue);
	           ItemMeta tempmeta = null;
	           if (Modifiers.contains("unbreakable")) {
					tempitem = setUnbreakable.Unbreakable(tempitem);
		       }
	           if (tempmat == Material.WRITTEN_BOOK) {
	        	   tempmeta = (BookMeta) tempitem.getItemMeta();
	           } else if (tempmat == Material.FIREWORK){
	        	   tempmeta = (FireworkMeta) tempitem.getItemMeta();
	           } else if (tempmat == Material.LEATHER_HELMET
						|| tempmat == Material.LEATHER_CHESTPLATE
						|| tempmat == Material.LEATHER_LEGGINGS
						|| tempmat == Material.LEATHER_BOOTS) {
	        		   tempmeta = (LeatherArmorMeta) tempitem.getItemMeta();
	           } else if (tempmat == Material.TIPPED_ARROW) {
	        	   tempmeta = (PotionMeta) tempitem.getItemMeta();
	           } else {
	        	   tempmeta = tempitem.getItemMeta();
	           }
	           if (items.getStringList(".lore") != null)
	           {
	             List<String> templist = items.getStringList(".lore");
	             List<String> templist2 = new ArrayList<String>();
	             for (int k = 0; k < templist.size(); k++)
	             {
	               String name = (String)templist.get(k);
	               name = ItemJoin.pl.formatPlaceholders(name, player);
	               templist2.add(name);
	             }
	             tempmeta.setLore(templist2);
	            }
				if (Modifiers.contains("hide-attributes") 
						|| Modifiers.contains("hide attributes") 
						|| Modifiers.contains("attributes") 
						|| Modifiers.contains("hideattributes")) {
			        tempmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			        tempmeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			        tempmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			        tempmeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			        tempmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			        tempmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
				}
				if (items.getString(".arrow.potion-effect") != null && tempmat == Material.TIPPED_ARROW) {
					String effectType = items.getString(".arrow.potion-effect").toUpperCase();
					int powerLevel = items.getInt(".arrow.power");
					int time = items.getInt(".arrow.time");
					if (items.getString(".arrow.time") == null) {
						time = 20;
					}
					if (items.getString(".arrow.power") == null) {
						powerLevel = 1;
					}
					((PotionMeta) tempmeta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(effectType), time*160, powerLevel), true);
				}
		        if (items.getString(".firework.type") != null && tempmat == Material.FIREWORK)
		         {
		        	String stringType = items.getString(".firework.type").toUpperCase();
		        	boolean flicker = items.getBoolean(".firework.flicker");
		        	boolean trail = items.getBoolean(".firework.trail");
		        	int power = items.getInt(".firework.distance");
		            Type buildType = Type.valueOf(stringType);
		            List<String> tempcolorslist = items.getStringList(".firework.colors");
		            List<Color> clist = new ArrayList<Color>();
		             for (int k = 0; k < tempcolorslist.size(); k++)
		             {
		               String color = tempcolorslist.get(k).toUpperCase();
		               clist.add(DyeColor.valueOf(color).getFireworkColor());
		             }
	                FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(clist).withFade(clist).with(buildType).build();
	                ((FireworkMeta) tempmeta).clearEffects();
	                ((FireworkMeta) tempmeta).addEffect(effect);
	                ((FireworkMeta) tempmeta).setPower(power);
		         }
	           if (items.getString(".name") != null)
	           {
	             String name = items.getString(".name");
	             name = ItemJoin.pl.formatPlaceholders(name, player);
	             tempmeta.setDisplayName(name + ItemJoin.encodeItemData(ItemJoin.secretMsg));
	           } else {
	        	   String lookup = ItemJoin.getName(tempitem);
	        	   String name = ItemJoin.pl.formatPlaceholders("&f" + lookup + ItemJoin.encodeItemData(ItemJoin.secretMsg), player);
	        	   tempmeta.setDisplayName(name);
	           }
				if (tempmat == Material.LEATHER_HELMET
						|| tempmat == Material.LEATHER_CHESTPLATE
						|| tempmat == Material.LEATHER_LEGGINGS
						|| tempmat == Material.LEATHER_BOOTS){
					if (items.getString(".leather-color") != null) {
					String leathercolor = items.getString(".leather-color").toUpperCase();
					((LeatherArmorMeta) tempmeta).setColor(DyeColor.valueOf(leathercolor).getFireworkColor());
					}
	             }
	           if (items.getString(".custom-map-image") != null && tempmat == Material.MAP)
	           {
	        	try {
	            MapView view = PlayerHandlers.MapView(tempitem);
	        	String mapIMG = items.getString(".custom-map-image");
	   			 if (mapIMG.equalsIgnoreCase("default.png") || new File(ItemJoin.pl.getDataFolder(), mapIMG).exists()) {
	   			  RenderImageMaps.setImage(mapIMG);
	   	          view.addRenderer(new RenderImageMaps());
	   			 }
	        	} catch (NullPointerException e) {
	        		// Hidden for now to "Disable" the maps //
	        		// This error DOES still occur! //
	        		// Waiting on a fix... //
		   			//ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Something has gone wrong with the maps!");
		   			//ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "This means your custom map will not be rendered!");
		   			//ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please contact the plugin developer immediately!");
		          }
			   }
	           if (items.getString(".author") != null && tempmat == Material.WRITTEN_BOOK)
	           {
	        	   ((BookMeta) tempmeta).setAuthor(ItemJoin.pl.formatPlaceholders(items.getString(".author"), player));
	           }
	           if (items.getString(".pages") != null && tempmat == Material.WRITTEN_BOOK)
	           {
	               List<String> templist = items.getStringList(".pages");
	               List<String> templist2 = new ArrayList<String>();
	               templist2.add("cleanSlate");
	               for (int k = 0; k < templist.size(); k++)
	               {
	            	   String pageSetup = (String)templist.get(k);
	            	   if (pageSetup.contains("newpage: ") || pageSetup.contains("newline: ") || pageSetup.contains("newpage:") || pageSetup.contains("newline:") || pageSetup.contains(":endthebook:")) {
	            		   if (pageSetup.contains("newpage: ") && !templist2.contains("cleanSlate") || pageSetup.contains("newpage:") && !templist2.contains("cleanSlate")) {
	            			   ((BookMeta) tempmeta).addPage(ItemJoin.pl.formatPlaceholders(templist2.toString().replace("[", "").replace("]", "").replaceAll(", ", " "), player));
	                    	   templist2.clear();
	            		   } else if (pageSetup.contains(":endthebook:")) {
	            			   ((BookMeta) tempmeta).addPage(ItemJoin.pl.formatPlaceholders(templist2.toString().replace("[", "").replace("]", "").replaceAll(", ", " "), player));
	                    	   templist2.clear();
	            		   } else if (templist2.contains("cleanSlate")) {
	            			   templist2.clear();
	            		   }
	            		   templist2.add(pageSetup.replace("newline: ", "\n").replace("newpage: ", "").replace("newline:", "\n").replace("newpage:", ""));
	            	   }
	        	   }
	           }
	           if (items.getString(".skull-owner") != null  && tempmat == Material.SKULL_ITEM)
	           {
	               String owner = items.getString(".skull-owner");
	               owner = ItemJoin.pl.formatPlaceholders(owner, player);
	              ((SkullMeta) tempmeta).setOwner(owner);
	           }
	           tempitem.setItemMeta(tempmeta);
	           if (items.getString(".enchantment") != null)
	           {
	        	   List<String> enchantments = items.getStringList(".enchantment");
	        	   for (String enchantment: enchantments) {
	        	       String[] parts = enchantment.split(":");
	        	       String name = parts[0].toUpperCase();
	        	       int level = 1;
	        	       if (enchantment.contains(":")) {
	            	       try {
	            	           level = Integer.parseInt(parts[1]);
	            	       }
	            	       catch (NumberFormatException ex) {
	            	    	   ItemJoin.pl.Console.sendMessage("An error occurred in the config, " + parts[1] + " is not a number and a number was expected!");
	            	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + parts[1] + ChatColor.RED + " is not a number and a number was expected!");
	            	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + ChatColor.GREEN + "Enchantment: " + parts[0] + " will now be enchanted by level 1.");
	            	       }
	        	       }
	        	       if (Enchantment.getByName(name) != null) {
	        	       tempitem.addUnsafeEnchantment(Enchantment.getByName(name), level);
	        	       }
	        	       if (Enchantment.getByName(name) == null) {
	        	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + name + ChatColor.RED + " is an incorrect enchantment name!");
	        	    	   ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
	        	       }
	        	   }
	           }
				ItemJoin.pl.items.put(world + "." + player.getName().toString() + ".items." + item, tempitem);
	         }
	        }
	      }
	    }
	  }
	}
}
