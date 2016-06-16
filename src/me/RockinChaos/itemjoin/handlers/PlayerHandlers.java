package me.RockinChaos.itemjoin.handlers;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Registers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class PlayerHandlers {

	@SuppressWarnings("deprecation")
	public static ItemStack getMainHand(Player player) {
		   ItemStack item = null;
			  if (Registers.hasCombatUpdate()) {
				  item = player.getInventory().getItemInMainHand();
			  } else if (!Registers.hasCombatUpdate()) {
				  item = player.getInventory().getItemInHand();
				 }
			return item;
	   }
	
	public static ItemStack getOffHand(Player player) {
		   ItemStack item = null;
			  if (Registers.hasCombatUpdate()) {
				  item = player.getInventory().getItemInOffHand();
				}
			return item;
	}
	
	@SuppressWarnings("deprecation")
	public static Material getMaterial(ConfigurationSection items) {
		Material material = null;
        if (ItemJoin.isInt(items.getString(".id"))) {
        	material = Material.getMaterial(items.getInt(".id"));
        } else {
        	material = Material.getMaterial(items.getString(".id"));
        }
			return material;
	}
	
	@SuppressWarnings("deprecation")
	public static Material getLocateMaterial(String world, String args) {
		Material material = null;
        if (ItemJoin.isInt(ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + args + ".id"))) {
        	material = Material.getMaterial(ItemJoin.getSpecialConfig("items.yml").getInt(world + ".items." + args + ".id"));
           } else {
        	material = Material.getMaterial(ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + args + ".id"));
           }
			return material;
	}
	
	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	public static MapView MapView(ItemStack tempitem) {
		MapView view =  ItemJoin.pl.getServer().getMap(tempitem.getDurability());
		return view;
	}
	
	@SuppressWarnings("deprecation")
	public static Player PlayerHolder() {
		Player holder = ItemJoin.pl.getServer().getPlayer("ItemJoin");
		return holder;
	}
	
	@SuppressWarnings("deprecation")
	public static Player StringPlayer(String StringPlayer) {
		Player args = Bukkit.getPlayerExact(StringPlayer);
		return args;
	}
}
