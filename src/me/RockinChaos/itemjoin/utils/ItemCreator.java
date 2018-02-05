package me.RockinChaos.itemjoin.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.RockinChaos.itemjoin.ItemJoin;

public class ItemCreator {
	
	// This is a currently unimplemented feature that is currently in development so it is blocked so only the DEV can work on it.
	// This will soon be the items GUI creator that allows you to create items in game for ItemJoin!
	public static void LaunchCreator(CommandSender sender) {
		
		// Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 45, "§nItemJoin Menu"); size ill need
		Inventory inv = ItemJoin.getInstance().getServer().createInventory(null, 27, "§nItemJoin Menu");
		
		Player player = (Player) sender; // add a check to makesure it isnt console and is really a player.
		
        inv.setItem(13, createItemTest("View Available Items", Material.DIAMOND_SWORD));
        inv.setItem(12, createItemTest("Example Shovel", Material.DIAMOND_SPADE));
        inv.setItem(14, createItemTest("Example Axe", Material.DIAMOND_AXE));
        player.openInventory(inv);
	}
	
    public static ItemStack createItemTest(String name, Material mat) {
        ItemStack i = new ItemStack(mat, 1);
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(name);
        i.setItemMeta(iMeta);
        return i;
    }

}
