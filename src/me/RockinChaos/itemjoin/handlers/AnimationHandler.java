package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private static HashMap < String, Boolean > isActive = new HashMap < String, Boolean > ();
	private static HashMap < String, Boolean > isWaiting = new HashMap < String, Boolean > ();
	private static HashMap < String, ArrayList<Integer> > runningID = new HashMap < String, ArrayList<Integer> > ();

	public static void cancelRefresh(Player player) {
		if (isActive.get(PlayerHandler.getPlayerID(player)) != null && isActive.get(PlayerHandler.getPlayerID(player))) { setCanceled.put(PlayerHandler.getPlayerID(player), true); }
	}

	public static void refreshItems(final Player player) {
		cancelRefresh(player);
			if (isWaiting.get(PlayerHandler.getPlayerID(player)) == null && isActive.get(PlayerHandler.getPlayerID(player)) != null && isActive.get(PlayerHandler.getPlayerID(player))) {
				isWaiting.put(PlayerHandler.getPlayerID(player), true);
				new BukkitRunnable() {
					public void run() {
						final Player p = PlayerHandler.getPlayerString(PlayerHandler.getPlayerID(player));
						if (p != null && p.isOnline() && !p.isDead() && !isActive.get(PlayerHandler.getPlayerID(player))) {
							isWaiting.remove(PlayerHandler.getPlayerID(player));
							setUpdating(p);
							ServerHandler.sendDebugMessage("Restarting Animations..");
							this.cancel();
						}
					}
				}.runTaskTimer(ItemJoin.getInstance(), 50L, 50L);
			} else if (isWaiting.get(PlayerHandler.getPlayerID(player)) == null) { setUpdating(player); }
	}

	private static void setUpdating(Player player) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				int Arbitrary = 0;
				String ItemFlags = items.getString(".itemflags");
				String ItemID;
				if (ItemHandler.containsIgnoreCase(ItemFlags, "animate") || ItemHandler.containsIgnoreCase(ItemFlags, "animated")) {
					if (WorldHandler.inWorld(items, player.getWorld().getName()) && items.getString(".slot") != null && PermissionsHandler.hasPermission(items, item, player)) {
					String slotlist = items.getString(".slot").replace(" ", "");
					String[] slots = slotlist.split(",");
					for (String slot: slots) {
						if (slot.equalsIgnoreCase("Arbitrary")) {
							Arbitrary = Arbitrary + 1;
							ItemID = slot + Arbitrary;
						} else {
							ItemID = slot;
						}
						final String finalID = ItemID;
						isActive.put(PlayerHandler.getPlayerID(player), true); setCanceled.put(PlayerHandler.getPlayerID(player), false);
						setNameAnimate(items, player, finalID);
						setLoreAnimate(items, player, finalID);
				  }
				}
			   }
			}
		}
	}
	
	private static void setNameAnimate(ConfigurationSection items, Player player, String ItemID) {
		long ticks = 0;
		if (getNameSection(items) != null) {
		Iterator <String> it = getNameSection(items).getKeys(false).iterator();
		while (it.hasNext()) {
			String name = it.next();
			ticks = ticks + getAnimateTicks(items.getString(".name." + name));
			setDelayRun(1, items, player, it.hasNext(), name, ItemID, ticks);
		}
		} else {
			ticks = getAnimateTicks(items.getString(".name"));
			setDelayRun(1, items, player, false, null, ItemID, ticks);
		}
	}
	
	private static void setLoreAnimate(ConfigurationSection items, Player player, String ItemID) {
		long ticks = 0;
		if (getLoreSection(items) != null) {
		Iterator <String> it = getLoreSection(items).getKeys(false).iterator();
		while (it.hasNext()) {
			String name = it.next();
			ticks = ticks + getAnimateTicks(items.getStringList(".lore." + name).get(0));
			setDelayRun(2, items, player, it.hasNext(), name, ItemID, ticks);
		}
		} else {
			ticks = getAnimateTicks(items.getStringList(".lore").get(0));
			setDelayRun(2, items, player, false, null, ItemID, ticks);
		}
	}
	
	private static void setDelayRun(final int AnimateID, final ConfigurationSection items, final Player player, final boolean hasRestart, final String nameString, final String ItemID, final long UpdateDelay) {
		final int taskid = Utils.getRandom(1, 100000);
		ArrayList < Integer > templist = new ArrayList < Integer > ();
		if (runningID.get(PlayerHandler.getPlayerID(player) + AnimateID) != null) {
			templist = runningID.get(PlayerHandler.getPlayerID(player) + AnimateID);
		}
		templist.add(taskid);
		runningID.put(PlayerHandler.getPlayerID(player) + AnimateID, templist);
		new BukkitRunnable() {
			public void run() {
				ArrayList < Integer > templist2 = new ArrayList < Integer > (runningID.get(PlayerHandler.getPlayerID(player) + AnimateID));
				templist2.remove((Object) taskid);
				runningID.put(PlayerHandler.getPlayerID(player) + AnimateID, templist2);
				if (player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && !player.isDead()) {
					boolean hasAnimated = false;
					ItemStack animatedItem = null;
					ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + items.getName());
					for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							if (!hasAnimated) { animatedItem = inPlayerInventory; }
							hasAnimated = true;
							PlayerHandler.updateActualSlot(player, animatedItem, "PlayerInventory");
						}
					}
					for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							if (!hasAnimated) { animatedItem = inPlayerInventory; }
							hasAnimated = true;
							PlayerHandler.updateActualSlot(player, animatedItem, "PlayerArmor");
						}
					}
					for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							if (!hasAnimated) { animatedItem = inPlayerInventory; }
							hasAnimated = true;
							PlayerHandler.updateActualSlot(player, animatedItem, "OpenInventory");
						}
					}
					if (player.getItemOnCursor() != null && inStoredItems != null && ItemHandler.isSimilar(player.getItemOnCursor(), inStoredItems)) {
						if (AnimateID == 1) { setNameData(items, player.getItemOnCursor(), player, hasRestart, hasAnimated, nameString, ItemID); } 
						else if (AnimateID == 2) { setLoreData(items, player.getItemOnCursor(), player, hasRestart, hasAnimated, nameString, ItemID); }
						hasAnimated = true;
					}
					if (!hasRestart && !hasAnimated && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && !player.isDead()) {
						new BukkitRunnable() {
							public void run() {
						if (AnimateID == 1) {
							setNameAnimate(items, player, ItemID);
							ServerHandler.sendDebugMessage("Failed to Animate, Restarted Name Animations... ");
						} else if (AnimateID == 2) {
							setLoreAnimate(items, player, ItemID);
							ServerHandler.sendDebugMessage("Failed to Animate, Restarted Lore Animations... ");
						}
							}
						}.runTaskLater(ItemJoin.getInstance(), 80L);
					}
				} else if (!player.isOnline() || setCanceled.get(PlayerHandler.getPlayerID(player)) == true || player.isDead()) {
					if (!hasRestart && runningID.get(PlayerHandler.getPlayerID(player) + 1).isEmpty() && runningID.get(PlayerHandler.getPlayerID(player) + 2).isEmpty()) {
						runningID.remove(PlayerHandler.getPlayerID(player) + 1);
						runningID.remove(PlayerHandler.getPlayerID(player) + 2);
						isActive.put(PlayerHandler.getPlayerID(player), false);
						ServerHandler.sendDebugMessage("Animation Runnables for the player " + player.getName() + " have finished, animate restart will be allowed if requested.");
					}
				}
			}
		}.runTaskLater(ItemJoin.getInstance(), UpdateDelay);
	}
	
	private static void setNameData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, boolean hasRestart, boolean hasAnimated, String nameString, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		CreateItems.setName(items, tempmeta, inPlayerInventory, player, ItemID, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
		if (!hasAnimated && !hasRestart && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && !player.isDead()) { setNameAnimate(items, player, ItemID); }
	}
	
	private static void setLoreData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, boolean hasRestart, boolean hasAnimated, String nameString, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		CreateItems.setLore(items, tempmeta, player, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
		if (!hasAnimated && !hasRestart && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player)) != true && !player.isDead()) { setLoreAnimate(items, player, ItemID); }
	}
	
	public static ConfigurationSection getNameSection(ConfigurationSection items) {
		return ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".name");
	}
	
	public static ConfigurationSection getLoreSection(ConfigurationSection items) {
		return ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".lore");
	}
	
	public static long getAnimateTicks(String decrypt) {
		if (decrypt != null && decrypt.contains("<delay:" + Utils.returnInteger(decrypt) + ">")) { return Utils.returnInteger(decrypt); }
		return 20;
	}
}