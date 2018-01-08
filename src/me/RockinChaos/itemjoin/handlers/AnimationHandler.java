package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.utils.Utils;

public class AnimationHandler {
	
	private static HashMap < String, Boolean > setCanceled = new HashMap < String, Boolean > ();
	private static HashMap < String, Integer > isActive = new HashMap < String, Integer > ();
	public static HashMap < String, ItemStack > cursorItem = new HashMap < String, ItemStack > ();
	private static HashMap < String, ArrayList<Integer> > runningID = new HashMap < String, ArrayList<Integer> > ();
	
	public static void CloseAnimations(Player player) {
		if (isActive.get(PlayerHandler.getPlayerID(player)) != null) { setCanceled.put(PlayerHandler.getPlayerID(player), true); isActive.remove(PlayerHandler.getPlayerID(player)); }
	}

	public static void OpenAnimations(Player player) {
		CloseAnimations(player);
		int newAnimation = Utils.getRandom(1, 100000);
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				int Arbitrary = 0;
				String ItemFlags = items.getString(".itemflags");
				String ItemID;
				if (ItemHandler.containsIgnoreCase(ItemFlags, "animate") || ItemHandler.containsIgnoreCase(ItemFlags, "dynamic") || ItemHandler.containsIgnoreCase(ItemFlags, "animated")) {
					if (WorldHandler.inWorld(items, player.getWorld().getName()) && PermissionsHandler.hasPermission(items, item, player) && items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						for (String slot: slots) {
							if (slot.equalsIgnoreCase("Arbitrary")) {
								Arbitrary = Arbitrary + 1;
								ItemID = slot + Arbitrary;
							} else { ItemID = slot; }
							setAnimations(items, player, ItemID, newAnimation);
						}
					}
				}
			}
		}
	}
	
	private static void setAnimations(ConfigurationSection items, Player player, String ItemID, int newAnimation) {
		setCanceled.put(PlayerHandler.getPlayerID(player), false);
		isActive.put(PlayerHandler.getPlayerID(player), newAnimation);
		setNameAnimate(items, player, ItemID, newAnimation);
		setLoreAnimate(items, player, ItemID, newAnimation);
}
	
	private static void setNameAnimate(ConfigurationSection items, Player player, String ItemID, int newAnimation) {
		if (ConfigHandler.getNameSection(items) != null) {
		long ticks = 0;
		Iterator <String> it = ConfigHandler.getNameSection(items).getKeys(false).iterator();
		while (it.hasNext() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && isActive.get(PlayerHandler.getPlayerID(player)).equals(newAnimation)) {
			String name = it.next();
			ticks = ticks + getAnimateTicks(items.getString(".name." + name));
			AnimateTask(true, items, player, it.hasNext(), name, ItemID, ticks, newAnimation);
		}
		} else if (setCanceled.get(PlayerHandler.getPlayerID(player)) != true) {
			AnimateTask(true, items, player, false, null, ItemID, getAnimateTicks(items.getString(".name")), newAnimation);
		}
	}
	
	private static void setLoreAnimate(ConfigurationSection items, Player player, String ItemID, int newAnimation) {
		if (ConfigHandler.getLoreSection(items) != null) {
		long ticks = 0;
		Iterator <String> it = ConfigHandler.getLoreSection(items).getKeys(false).iterator();
		while (it.hasNext() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && isActive.get(PlayerHandler.getPlayerID(player)).equals(newAnimation)) {
			String name = it.next();
			ticks = ticks + getAnimateTicks(items.getStringList(".lore." + name).get(0));
			AnimateTask(false, items, player, it.hasNext(), name, ItemID, ticks, newAnimation);
		}
		} else if (setCanceled.get(PlayerHandler.getPlayerID(player)) != true) {
			AnimateTask(false, items, player, false, null, ItemID, getAnimateTicks(items.getStringList(".lore").get(0)), newAnimation);
		}
	}

	private static void AnimateTask(final boolean isName, final ConfigurationSection items, final Player player, final boolean hasNext, final String nameString, final String ItemID, final long UpdateDelay, final int newAnimation) {
		final int taskId = Utils.getRandom(1, 100000);
		RunningTask(taskId, items, player, isName, newAnimation);
		new BukkitRunnable() {
			public void run() {
				CompleteRunningTask(taskId, items, player, isName, newAnimation);
				if (setCanceled.get(PlayerHandler.getPlayerID(player)) != true && isActive.get(PlayerHandler.getPlayerID(player)).equals(newAnimation)) {
					AnimateItems(player, items, isName, nameString, ItemID);
					if (!hasNext) { if (isName) { setNameAnimate(items, player, ItemID, newAnimation); } else { setLoreAnimate(items, player, ItemID, newAnimation); }}
				} else if (setCanceled.get(PlayerHandler.getPlayerID(player)) == true || !isActive.get(PlayerHandler.getPlayerID(player)).equals(newAnimation)) {
					if (!hasNext) {
						if (runningID.get(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + true).isEmpty() 
						&& runningID.get(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + false).isEmpty()) {
							runningID.remove(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + true);
							runningID.remove(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + false);
							ServerHandler.sendDebugMessage(player.getName() + "'s Animations have finished for the item " + items.getName() + " with the AnimateId " + newAnimation);
						}
					}
				}
			}
		}.runTaskLater(ItemJoin.getInstance(), UpdateDelay);
	}
	
	private static void RunningTask(int taskId, ConfigurationSection items, Player player, boolean isName, int newAnimation) {
		ArrayList < Integer > templist = new ArrayList < Integer > ();
		if (runningID.get(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + isName) != null) { templist = runningID.get(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + isName); }
		templist.add(taskId);
		runningID.put(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + isName, templist);
	}
	
	private static void CompleteRunningTask(int taskId, ConfigurationSection items, Player player, boolean isName, int newAnimation) {
		ArrayList < Integer > templist2 = new ArrayList < Integer > (runningID.get(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + isName));
		templist2.remove((Object) taskId);
		runningID.put(PlayerHandler.getPlayerID(player) + newAnimation + items.getName() + isName, templist2);
	}
	
	private static void AnimateItems(Player player, ConfigurationSection items, boolean isName, String nameString, String ItemID) {
		ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + items.getName());
		// ============== Animate Within the Player Inventory ============== //
		for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
			if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
				if (isName) { setNameData(items, inPlayerInventory, player, nameString, ItemID); } 
				else { setLoreData(items, inPlayerInventory, player, nameString); }
			}
		}
		// =============== Animate Within the Player's Armor =============== //
		for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
			if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
				if (isName) { setNameData(items, inPlayerInventory, player, nameString, ItemID); } 
				else { setLoreData(items, inPlayerInventory, player, nameString); }
			}
		}
		// ========== Animate Within the Player Crafting/Chests ============ //
		for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
			if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
				if (isName) { setNameData(items, inPlayerInventory, player, nameString, ItemID); } 
				else { setLoreData(items, inPlayerInventory, player, nameString); }
			}
		}
		// ============== Animate Within the Player's Cursor =============== //
		if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && inStoredItems != null && ItemHandler.isSimilar(player.getItemOnCursor(), inStoredItems)) {
			ItemStack item = new ItemStack(player.getItemOnCursor());
			if (cursorItem.get(PlayerHandler.getPlayerID(player)) != null && ItemHandler.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player)), inStoredItems)) { item = new ItemStack(cursorItem.get(PlayerHandler.getPlayerID(player))); }
			if (isName) { setNameData(items, item, player, nameString, ItemID); } 
			else { setLoreData(items, item, player, nameString); }
			cursorItem.put(PlayerHandler.getPlayerID(player), item);
		}
		// ============== This has Concluded all Animations.. ============== //
	}
	
	private static void setNameData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, String nameString, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		tempmeta = CreateItems.setName(items, tempmeta, inPlayerInventory, player, ItemID, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
	}
	
	private static void setLoreData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, String nameString) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		tempmeta = CreateItems.setLore(items, tempmeta, player, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
	}
	
	private static long getAnimateTicks(String decrypt) {
		if (decrypt != null && decrypt.contains("<delay:" + Utils.returnInteger(decrypt) + ">")) { return Utils.returnInteger(decrypt); }
		return 100;
	}
}