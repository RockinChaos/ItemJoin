package me.RockinChaos.itemjoin.handlers;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.domedd.betternick.BetterNick;
import de.domedd.betternick.api.nickedplayer.NickedPlayer;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Legacy;
import net.milkbowl.vault.economy.EconomyResponse;

public class PlayerHandler {
	
	private static final int PLAYER_CRAFT_INV_SIZE = 5;
	
    public static boolean isCraftingInv(InventoryView view) {
        return view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE;
    }
	
	public static boolean isCreativeMode(Player player) {
		final GameMode gamemode = player.getGameMode();
		final GameMode creative = GameMode.CREATIVE;
		if (gamemode == creative) {
			return true;
		}
		return false;
	}
	
	public static boolean isAdventureMode(Player player) {
		final GameMode gamemode = player.getGameMode();
		final GameMode adventure = GameMode.ADVENTURE;
		if (gamemode == adventure) {
			return true;
		}
		return false;
	}
	
	public static void setItemInHand(Player player, Material mat) {
		Legacy.setLegacyInHandItem(player, new ItemStack(mat));
	}
	
	public static void setHeldItemSlot(Player player) {
		if (ItemUtilities.getHeldSlot() != -1 && ItemUtilities.getHeldSlot() <= 8 && ItemUtilities.getHeldSlot() >= 0) {
			player.getInventory().setHeldItemSlot(ItemUtilities.getHeldSlot());
		}
	}
	
	public static ItemStack getHandItem(Player player) {
		if (ServerHandler.hasCombatUpdate() && player.getInventory().getItemInMainHand().getType() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerHandler.hasCombatUpdate() && player.getInventory().getItemInOffHand().getType() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.hasCombatUpdate()) {
			return Legacy.getLegacyInHandItem(player);
		}
		return null;
	}
	
	public static void setHandItem(Player player, ItemStack item) {
		if (ServerHandler.hasCombatUpdate() && player.getInventory().getItemInMainHand().getType() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			player.getInventory().setItemInMainHand(item);
		} else if (ServerHandler.hasCombatUpdate() && player.getInventory().getItemInOffHand().getType() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			player.getInventory().setItemInOffHand(item);
		} else if (!ServerHandler.hasCombatUpdate()) {
			Legacy.setLegacyInHandItem(player, item);
		}
	}
	
	public static ItemStack getPerfectHandItem(Player player, String type) {
		if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("HAND")) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerHandler.hasCombatUpdate() && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.hasCombatUpdate()) {
			return Legacy.getLegacyInHandItem(player);
		}
		return null;
	}
	
	public static ItemStack getMainHandItem(Player player) {
		if (ServerHandler.hasCombatUpdate()) {
			return player.getInventory().getItemInMainHand();
		} else if (!ServerHandler.hasCombatUpdate()) {
			return Legacy.getLegacyInHandItem(player);
		}
		return null;
	}
	
	public static void setMainHandItem(Player player, ItemStack item) {
		if (ServerHandler.hasCombatUpdate()) {
			player.getInventory().setItemInMainHand(item);;
		} else if (!ServerHandler.hasCombatUpdate()) {
			Legacy.setLegacyInHandItem(player, item);
		}
	}
	
	public static void setOffHandItem(Player player, ItemStack item) {
		if (ServerHandler.hasCombatUpdate()) {
			player.getInventory().setItemInOffHand(item);;
		} else if (!ServerHandler.hasCombatUpdate()) {
			Legacy.setLegacyInHandItem(player, item);
		}
	}
	
	public static ItemStack getOffHandItem(Player player) {
		if (ServerHandler.hasCombatUpdate()) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.hasCombatUpdate()) {
			return Legacy.getLegacyInHandItem(player);
		}
		return null;
	}
	
	public static void setOffhandItem(Player player, ItemStack toSet) {
		if (ServerHandler.hasCombatUpdate()) {
			player.getInventory().setItemInOffHand(toSet);
		}
	}
	
	public static void updateExperienceLevels(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	player.setExp(player.getExp());
            	player.setLevel(player.getLevel());
            }
        }, 1L);
	}
	
	public static void updateInventory(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	Legacy.updateLegacyInventory(player);
            }
        }, 1L);
	}
	
	public static void delayUpdateInventory(final Player player, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	Legacy.updateLegacyInventory(player);
            }
        }, delay);
	}
	
	public static boolean getNewSkullMethod() {
		try {
			if (Class.forName("org.bukkit.inventory.meta.SkullMeta").getMethod("getOwningPlayer") != null) { return true; }
		} catch (Exception e) { }
		return false;
	}
	
	public static String getSkullOwner(ItemStack item) {
		if (ServerHandler.hasSpecificUpdate("1_12") && item != null && item.hasItemMeta() && ItemHandler.isSkull(item.getType()) 
				&& ((SkullMeta) item.getItemMeta()).hasOwner() && getNewSkullMethod() != false) {
			String owner =  ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName();
			if (owner != null) { return owner; }
		} else if (item != null && item.hasItemMeta() 
				&& ItemHandler.isSkull(item.getType())
				&& ((SkullMeta) item.getItemMeta()).hasOwner()) {
			String owner = Legacy.getLegacySkullOwner(((SkullMeta) item.getItemMeta()));
			if (owner != null) { return owner; }
		} 
		return "NULL";
	}

	public static Player getPlayerString(String playerName) {
		Player args = null;
		try { args = Bukkit.getPlayer(UUID.fromString(playerName)); } catch (Exception e) {}
		if (playerName != null && ConfigHandler.getDepends().nickEnabled()) {
			NickedPlayer np = new NickedPlayer(Legacy.getLegacyPlayer(playerName));
			if (np.isNicked()) {
			return Legacy.getLegacyPlayer(np.getRealName());
			} else {
				return Legacy.getLegacyPlayer(playerName);
			}
		} else if (args == null) { return Legacy.getLegacyPlayer(playerName); }
		return args;
	}
	
	public static String getPlayerID(Player player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && ConfigHandler.getDepends().nickEnabled()) {
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
	
	public static String getOfflinePlayerID(OfflinePlayer player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && ConfigHandler.getDepends().nickEnabled()) {
			NickedPlayer np = new NickedPlayer((BetterNick) player);
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
	
	public static OfflinePlayer getOfflinePlayer(String playerName) {
		Collection<?> playersOnlineNew;
		OfflinePlayer[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Object objPlayer: playersOnlineNew) {
					Player player = ((Player)objPlayer);
					if (player.getName().equalsIgnoreCase(playerName)) {
						return player;
					}
				}
			} else {
				playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (OfflinePlayer player: playersOnlineOld) {
					if (player.getName().equalsIgnoreCase(playerName)) {
						return player;
					}
				}
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); } 
		return null;
	}
	
	public static double getBalance(Player player) {
		return ConfigHandler.getDepends().getVault().getEconomy().getBalance(player);
	}
	
	public static EconomyResponse withdrawBalance(Player player, int cost) {
		return ConfigHandler.getDepends().getVault().getEconomy().withdrawPlayer(player, cost);
	}
}