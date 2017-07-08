package me.RockinChaos.itemjoin.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Econ;
import net.milkbowl.vault.economy.EconomyResponse;

public class PlayerHandler {

	@SuppressWarnings("deprecation")
	public static void updateInventory(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, (Runnable)new Runnable() {
            public void run() {
                player.updateInventory();
            }
        }, 1L);
	}
	
	@SuppressWarnings("deprecation")
	public static void delayUpdateInventory(final Player player, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, (Runnable)new Runnable() {
            public void run() {
                player.updateInventory();
            }
        }, delay);
	}
	
	@SuppressWarnings("deprecation")
	public static void setPerfectHandItem(Player player, ItemStack toSet, String type) {
		if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("Hand")) {
			player.getInventory().setItemInMainHand(toSet);
		} else if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("Off_Hand")) {
			player.getInventory().setItemInOffHand(toSet);
		} else if (!ServerHandler.hasCombatUpdate()) {
			player.getInventory().setItemInHand(toSet);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void setInHandItem(Player player, ItemStack toSet) {
		player.getInventory().setItemInHand(toSet);
	}
	
	public static void setOffhandItem(Player player, ItemStack toSet) {
		player.getInventory().setItemInOffHand(toSet);
	}
	
	public static void setMainHandItem(Player player, ItemStack toSet) {
		player.getInventory().setItemInMainHand(toSet);
	}
	
	@SuppressWarnings("deprecation")
	public static Player getPlayerString(String StringPlayer) {
		Player args = Bukkit.getPlayerExact(StringPlayer);
		return args;
	}
	
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, Material mat) {
		player.setItemInHand(new ItemStack(mat));
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
