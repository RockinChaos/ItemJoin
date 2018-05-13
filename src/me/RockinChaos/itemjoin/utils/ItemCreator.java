package me.RockinChaos.itemjoin.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;

public class ItemCreator {
	
	public static String name = "            §nItemJoin Menu§7   §a[BETA]";
	public static String create = "§a§nCreate";
	public static String modify = "§c§nModify";
	public static String view = "§e§nView";
	public static String returnMenu = "§c§nReturn to Menu";
	public static String setTheItem = "§c§lSet The Item";
	// This is a currently unimplemented feature that is currently in development so it is blocked so only the DEV can work on it.
	// This will soon be the items GUI creator that allows you to create items in game for ItemJoin!
	public static void LaunchCreator(CommandSender sender) {
		
		// Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 45, "§nItemJoin Menu"); size ill need
		Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 9, name);
		
		Player player = (Player) sender; // add a check to makesure it isnt console and is really a player.
		
        inv.setItem(3, cMenuItem(create, "&7Create an item", Material.BOOK_AND_QUILL, player));
        inv.setItem(4, cMenuItem(modify, "&7Modify an Existing Item", Material.PAPER, player));
        inv.setItem(5, cMenuItem(view, "&7View Currently Defined Items", Material.BOOK, player));
        inv.setItem(8, cMenuItem("&c&nExit", "", Material.BARRIER, player));
        player.openInventory(inv);
	}
	
	
	public static void LaunchViewing(CommandSender sender, String worldd) {
		
		// Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 45, "§nItemJoin Menu"); size ill need
		Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 54, name);
		
		Player player = (Player) sender; // add a check to makesure it isnt console and is really a player.
		
		
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasItemsPermission(items, item, player)) {
					if(ItemHandler.containsIgnoreCase(items.getString(".triggers"), "join") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "on-join") || items.getString(".triggers") == null) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (Utils.isCustomSlot(slot) && inStoredItems != null) {
								
								if (slot.equalsIgnoreCase("Helmet")) { inv.setItem(9, inStoredItems); }
								if (slot.equalsIgnoreCase("Chestplate")) { inv.setItem(10, inStoredItems); }
								if (slot.equalsIgnoreCase("Leggings")) { inv.setItem(11, inStoredItems); }
								if (slot.equalsIgnoreCase("Boots")) { inv.setItem(12, inStoredItems); }
								if (slot.equalsIgnoreCase("Offhand")) { inv.setItem(13, inStoredItems); }
								if (slot.equalsIgnoreCase("Arbitrary") && Integer.parseInt(ItemID.replace("Arbitrary", "")) <= 8) {		
									for (int i = 0; i < 8; i++) {
										if (slot.equalsIgnoreCase("Arbitrary") && inv.getItem(46 + i) == null) { inv.setItem(46 + i, inStoredItems); break; } }
								} else {
									for (int i = 0; i < 35; i++) {
										if (slot.equalsIgnoreCase("Arbitrary") && inv.getItem(45 - i) == null) { inv.setItem(45 - i, inStoredItems); break; } }
								}
								
							} else if (Utils.isInt(slot) && inStoredItems != null) {
								int parsedSlot = Integer.parseInt(slot);
								if (parsedSlot <= 8) { inv.setItem(parsedSlot + 45, inStoredItems); } 
								else { inv.setItem(parsedSlot + 9, inStoredItems); }
							}
						}
					}
				}
			}
		}
	  }
		
		inv.setItem(0, cMenuItem(returnMenu, "", Material.BARRIER, player)); // set item id, slot and enabled-worlds
        inv.setItem(3, cMenuItem("&c&nPrevious Page", "", Material.ARROW, player));
        inv.setItem(4, cMenuItem("&e&nInfo", worldd + ", Info Stuff, What world, Etc.", Material.BOOK, player));
        inv.setItem(5, cMenuItem("&c&nNext Page", "", Material.ARROW, player));
        player.openInventory(inv);
        
	}
	
	
	
	
	public static void LaunchCreating(CommandSender sender, String worldd) {
		
		Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 54, name);
		
		Player player = (Player) sender; // add a check to makesure it isnt console and is really a player.
		
		inv.setItem(0, cMenuItem(returnMenu, "", Material.BARRIER, player));
		
		inv.setItem(4, cMenuItem(setTheItem, "", Material.PAPER, player)); // set item id, slot and enabled-worlds
		
		inv.setItem(12, cMenuItem("&cSetup the Name", "", Material.NAME_TAG, player));
		inv.setItem(13, cMenuItem("&cSetup the Lore", "", Material.BOOK_AND_QUILL, player));
		inv.setItem(14, cMenuItem("&cSetup the Count", "", Material.DIAMOND, player));
		inv.setItem(19, cMenuItem("&cSetup the Durability", "", Material.DIAMOND_SWORD, player));
		inv.setItem(20, cMenuItem("&cSetup the Commands", "", Material.BOOK, player));
		inv.setItem(21, cMenuItem("&cSetup the Enchantments", "", Material.ENCHANTED_BOOK, player));
		inv.setItem(22, cMenuItem("&cSetup the ItemFlags", "", Material.CHEST, player));
		inv.setItem(23, cMenuItem("&cSetup the Triggers", "", Material.REDSTONE, player));
		inv.setItem(24, cMenuItem("&cSetup the Custom-Permission", "", Material.REDSTONE_TORCH_ON, player));
		inv.setItem(25, cMenuItem("&cSetup the enabled-regions", "", Material.LONG_GRASS, player));
		
        player.openInventory(inv);
        
	}
	
	public static void LaunchSetupItem(CommandSender sender, String worldd) {
		
		Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 54, name);
		
		Player player = (Player) sender; // add a check to makesure it isnt console and is really a player.
		
		Material[] allMaterials = Material.values();
		int s = 0;
		for (Material mat: allMaterials) {
			inv.setItem(s, cMenuItem("none", "none", mat, player));
			if (inv.getItem(s) != null) {
			s++;
			}
			if (s == 45) {
				break;
			}
		}
		
		inv.setItem(45, cMenuItem(returnMenu, "", Material.BARRIER, player));
		inv.setItem(48, cMenuItem("&c&nPrevious Page", "", Material.ARROW, player));
        inv.setItem(50, cMenuItem("&c&nNext Page", "", Material.ARROW, player));
        player.openInventory(inv);
        
	}
	
    public static ItemStack cMenuItem(String name, String lore, Material mat, Player player) {
    	
    	ArrayList<String> loreList = new ArrayList<String>();
    	String[] lores = lore.split(", ");
    	for (String loree: lores) {
    		loreList.add(Utils.format(loree, player));
    	}
    	
        ItemStack i = new ItemStack(mat, 1);
        ItemMeta iMeta = i.getItemMeta();
        if (!name.equals("none")) {
        name = Utils.format(name, player);
        iMeta.setDisplayName(name);
        }
        if (!lore.equals("none")) {
        iMeta.setLore(loreList);
        }
        
        if (!name.equals("none") && !lore.equals("none")) {
        i.setItemMeta(iMeta);
        }
        return i;
    }

}
