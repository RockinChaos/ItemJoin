package me.RockinChaos.itemjoin.handlers;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Registers;
import me.RockinChaos.itemjoin.utils.Econ;
import net.milkbowl.vault.economy.EconomyResponse;

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
        if (ItemJoin.isInt(ConfigHandler.getConfig("items.yml").getString(world + ".items." + args + ".id"))) {
        	material = Material.getMaterial(ConfigHandler.getConfig("items.yml").getInt(world + ".items." + args + ".id"));
           } else {
        	material = Material.getMaterial(ConfigHandler.getConfig("items.yml").getString(world + ".items." + args + ".id"));
           }
			return material;
	}
	
	@SuppressWarnings("deprecation")
	public static Player getOfflinePlayer(String player) {
		return (Player) ItemJoin.pl.getServer().getOfflinePlayer(player);
	}
	
	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, Material mat) {
		player.setItemInHand(new ItemStack(mat));
	}
	
	public static void setOffhandItem(Player player, ItemStack toSet) {
		player.getInventory().setItemInOffHand(toSet);
	}
	
	public static ItemStack getOffhandItem(Player player) {
		return player.getInventory().getItemInOffHand();
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
	
	@SuppressWarnings("deprecation")
	public static double getBalance(Player player) {
		double balance = Econ.econ.getBalance(player.getName());
		return balance;
	}
	
	@SuppressWarnings("deprecation")
	public static EconomyResponse withdrawBalance(Player player, int cost) {
		EconomyResponse balance = Econ.econ.withdrawPlayer(player.getName(), cost);;
		return balance;
	}
}
