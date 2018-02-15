package me.RockinChaos.itemjoin.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

import de.domedd.betternick.api.nickedplayer.NickedPlayer;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Econ;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Reflection;
import net.milkbowl.vault.economy.EconomyResponse;

public class PlayerHandler {
	
	public static boolean cancell = false;
	private static HashMap < String, GameProfile > gameProfiles = new HashMap < String, GameProfile > ();

	@SuppressWarnings("deprecation")
	public static void updateInventory(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable)new Runnable() {
            public void run() {
                player.updateInventory();
            }
        }, 1L);
	}
	
	@SuppressWarnings("deprecation")
	public static void delayUpdateInventory(final Player player, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable)new Runnable() {
            public void run() {
                player.updateInventory();
            }
        }, delay);
	}
	
	@SuppressWarnings("deprecation")
	public static void setPerfectHandItem(Player player, ItemStack toSet, String type) {
		if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("HAND")) {
			player.getInventory().setItemInMainHand(toSet);
		} else if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			player.getInventory().setItemInOffHand(toSet);
		} else if (!ServerHandler.hasCombatUpdate()) {
			player.getInventory().setItemInHand(toSet);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getPerfectHandItem(Player player, String type) {
		if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("HAND")) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.hasCombatUpdate()) {
			return player.getInventory().getItemInHand();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void setInHandItem(Player player, ItemStack toSet) {
		player.getInventory().setItemInHand(toSet);
	}
	
	public static void setOffhandItem(Player player, ItemStack toSet) {
		if (ServerHandler.hasCombatUpdate()) {
		player.getInventory().setItemInOffHand(toSet);
		}
	}
	
	public static void setMainHandItem(Player player, ItemStack toSet) {
		if (ServerHandler.hasCombatUpdate()) {
		player.getInventory().setItemInMainHand(toSet);
		}
	}
	
	public static boolean isCreativeMode(Player player) {
		final GameMode gamemode = player.getGameMode();
		final GameMode creative = GameMode.CREATIVE;
		if (gamemode == creative) {
			return true;
		}
		return false;
	}
	
	public static boolean getNewSkullMethod() {
		try {
			if (Class.forName("org.bukkit.inventory.meta.SkullMeta").getMethod("getOwningPlayer") != null) {
			return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static String getSkullOwner(ItemStack item) {
		if (ServerHandler.hasAltUpdate("1_12") && item != null && item.hasItemMeta() && item.getType().equals(Material.SKULL_ITEM) 
				&& ((SkullMeta) item.getItemMeta()).hasOwner() && getNewSkullMethod() != false) {
			String owner =  ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName();
			if (owner != null) { return owner; }
		} else if (item != null && item.hasItemMeta() 
				&& item.getType().equals(Material.SKULL_ITEM) 
				&& ((SkullMeta) item.getItemMeta()).hasOwner()) {
			String owner = ((SkullMeta) item.getItemMeta()).getOwner();
			if (owner != null) { return owner; }
		} 
		return "NULL";
	}
	
	@SuppressWarnings("deprecation")
	public static ItemMeta setSkullOwner(ItemMeta tempmeta, String owner) {
        try {
		    Method fetchProfile= Reflection.getOBC("entity.CraftPlayer").getDeclaredMethod("getProfile");
            Field declaredField = tempmeta.getClass().getDeclaredField("profile");
            declaredField.setAccessible(true);
            if (ItemJoin.getInstance().getServer().getPlayer(owner) != null) { declaredField.set(tempmeta, fetchProfile.invoke(ItemJoin.getInstance().getServer().getPlayer(owner))); }
            else if (ItemJoin.getInstance().getServer().getPlayer(owner) == null) {
            	if(gameProfiles.get(owner) == null) {
            	GameProfile profile = new GameProfile(ItemJoin.getInstance().getServer().getOfflinePlayer(owner).getUniqueId(), owner);
            	setSkin(profile, ItemJoin.getInstance().getServer().getOfflinePlayer(owner).getUniqueId());
                gameProfiles.put(owner, profile);
            	}
                declaredField.set(tempmeta, gameProfiles.get(owner));
            }
        } catch (Exception e) { if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }}
		return tempmeta;
	}
	
	public static boolean setSkin(GameProfile profile, UUID uuid) {
	    try {
	        HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
	        if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
	            String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
	            String skin = reply.split("\"value\":\"")[1].split("\"")[0];
	            String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
	            profile.getProperties().put("textures", new Property("textures", skin, signature));
	            return true;
	        } else {
	            System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
	            return false;
	        }
	    } catch (IOException e) { if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
	        return false;
	    }
	}
	
	@SuppressWarnings("deprecation")
	public static Player getPlayerString(String player) {
		Player args = null;
		try { args = Bukkit.getPlayer(UUID.fromString(player)); } catch (Exception e) {}
		if (player != null && Hooks.hasBetterNick()) {
			NickedPlayer np = new NickedPlayer(Bukkit.getPlayer(player));
			if (np.isNicked()) {
			return Bukkit.getPlayer(np.getRealName());
			} else {
				return Bukkit.getPlayer(player);
			}
		} else if (args == null) { return Bukkit.getPlayer(player); }
		return args;
	}
	
	public static String getPlayerID(Player player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && Hooks.hasBetterNick()) {
			NickedPlayer np = new NickedPlayer(player);
			if (np.isNicked()) {
			return np.getRealName();
			} else {
				return player.getName();
			}
		} else if (player != null) {
			return player.getName();
		}
		return "";
	}
	
	@SuppressWarnings("deprecation")
	public static Player getPlayer(Player player) {
		Player args = null;
		try { args = Bukkit.getPlayer(player.getUniqueId()); } catch (Exception e) {}
		if (player != null && Hooks.hasBetterNick()) {
			NickedPlayer np = new NickedPlayer(player);
			if (np.isNicked()) {
			return Bukkit.getPlayer(np.getRealName());
			} else {
				return Bukkit.getPlayer(player.getName());
			}
		} else if (args == null) { return Bukkit.getPlayer(player.getName()); }
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
