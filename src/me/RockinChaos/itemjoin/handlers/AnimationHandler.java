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
	
	public static HashMap < String, ItemStack > cursorItem = new HashMap < String, ItemStack > ();
	private static HashMap < String, Boolean > setCanceled = new HashMap < String, Boolean > ();
	private static HashMap < String, Boolean > isActive = new HashMap < String, Boolean > ();
	private static HashMap < String, Boolean > isWaiting = new HashMap < String, Boolean > ();
	private static HashMap < String, ArrayList<Integer> > runningID = new HashMap < String, ArrayList<Integer> > ();

	public static void setAnimations(Player player) {
		CloseAnimations(player);
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
						OpenAnimation(player, items, finalID);
				  }
				}
			   }
			}
		}
	}
	
	public static void CloseAnimations(Player player) {
		if (isActive.get(PlayerHandler.getPlayerID(player)) != null && isActive.get(PlayerHandler.getPlayerID(player))) { setCanceled.put(PlayerHandler.getPlayerID(player), true); }
	}
	
	private static void OpenAnimation(final Player player, final ConfigurationSection items, final String finalID) {
			if (isWaiting.get(PlayerHandler.getPlayerID(player) + items.getName()) == null && isActive.get(PlayerHandler.getPlayerID(player) + items.getName()) != null && isActive.get(PlayerHandler.getPlayerID(player) + items.getName())) {
				isWaiting.put(PlayerHandler.getPlayerID(player) + items.getName(), true);
				new BukkitRunnable() {
					public void run() {
						final Player p = PlayerHandler.getPlayer(player);
						if (p != null && p.isOnline() && !p.isDead() && !isActive.get(PlayerHandler.getPlayerID(player) + items.getName())) {
							isWaiting.remove(PlayerHandler.getPlayerID(player) + items.getName());
							isActive.put(PlayerHandler.getPlayerID(player) + items.getName(), true); setCanceled.put(PlayerHandler.getPlayerID(player) + items.getName(), false);
							setNameAnimate(items, player, finalID);
							setLoreAnimate(items, player, finalID);
							ServerHandler.sendDebugMessage("Restarting Animations..");
							this.cancel();
						}
					}
				}.runTaskTimer(ItemJoin.getInstance(), 50L, 50L);
			} else if (isWaiting.get(PlayerHandler.getPlayerID(player) + items.getName()) == null) { 
			isActive.put(PlayerHandler.getPlayerID(player) + items.getName(), true); setCanceled.put(PlayerHandler.getPlayerID(player) + items.getName(), false);
			setNameAnimate(items, player, finalID);
			setLoreAnimate(items, player, finalID); }
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
		if (runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + AnimateID) != null) {
			templist = runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + AnimateID);
		}
		templist.add(taskid);
		runningID.put(PlayerHandler.getPlayerID(player) + items.getName() + AnimateID, templist);
		new BukkitRunnable() {
			public void run() {
				ArrayList < Integer > templist2 = new ArrayList < Integer > (runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + AnimateID));
				templist2.remove((Object) taskid);
				runningID.put(PlayerHandler.getPlayerID(player) + items.getName() + AnimateID, templist2);
				if (player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player) + items.getName()) != true && !player.isDead()) {
					boolean hasAnimated = false;
					ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + items.getName());
					for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							hasAnimated = true;
						}
					}
					for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							hasAnimated = true;
						}
					}
					for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
						if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							if (AnimateID == 1) { setNameData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); } 
							else if (AnimateID == 2) { setLoreData(items, inPlayerInventory, player, hasRestart, hasAnimated, nameString, ItemID); }
							hasAnimated = true;
						}
					}
					if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && inStoredItems != null && ItemHandler.isSimilar(player.getItemOnCursor(), inStoredItems)) {
						ItemStack item = new ItemStack(player.getItemOnCursor());
						if (cursorItem.get(PlayerHandler.getPlayerID(player)) != null && ItemHandler.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player)), inStoredItems)) { item = new ItemStack(cursorItem.get(PlayerHandler.getPlayerID(player))); }
						if (AnimateID == 1) { setNameData(items, item, player, hasRestart, hasAnimated, nameString, ItemID); } 
						else if (AnimateID == 2) { setLoreData(items, item, player, hasRestart, hasAnimated, nameString, ItemID); }
						cursorItem.put(PlayerHandler.getPlayerID(player), item);
						hasAnimated = true;
					}
					if (!hasRestart && !hasAnimated && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player) + items.getName()) != true && !player.isDead()) {
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
				} else if (!player.isOnline() || setCanceled.get(PlayerHandler.getPlayerID(player) + items.getName()) == true || player.isDead()) {
					try {
					if (!hasRestart 
							&& runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + 1) == null && runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + 2) == null
							|| !hasRestart && runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + 1).isEmpty() && runningID.get(PlayerHandler.getPlayerID(player) + items.getName() + 2).isEmpty()) {
						runningID.remove(PlayerHandler.getPlayerID(player) + items.getName() + 1);
						runningID.remove(PlayerHandler.getPlayerID(player) + items.getName() + 2);
						isActive.put(PlayerHandler.getPlayerID(player) + items.getName(), false);
						ServerHandler.sendDebugMessage("Animation Runnables for the player " + player.getName() + " have finished for the item " + items.getName() + ", animate restart will be allowed if requested.");
					}
					} catch (Exception e) { if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); } }
				}
			}
		}.runTaskLater(ItemJoin.getInstance(), UpdateDelay);
	}
	
	private static void setNameData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, boolean hasRestart, boolean hasAnimated, String nameString, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		tempmeta = CreateItems.setName(items, tempmeta, inPlayerInventory, player, ItemID, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
		if (!hasAnimated && !hasRestart && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player) + items.getName()) != true && !player.isDead()) { setNameAnimate(items, player, ItemID); }
	}
	
	private static void setLoreData(ConfigurationSection items, ItemStack inPlayerInventory, Player player, boolean hasRestart, boolean hasAnimated, String nameString, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		tempmeta = CreateItems.setLore(items, tempmeta, player, nameString);
		inPlayerInventory.setItemMeta(tempmeta);
		if (!hasAnimated && !hasRestart && player.isOnline() && setCanceled.get(PlayerHandler.getPlayerID(player) + items.getName()) != true && !player.isDead()) { setLoreAnimate(items, player, ItemID); }
	}
	
	private static ConfigurationSection getNameSection(ConfigurationSection items) {
		return ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".name");
	}
	
	private static ConfigurationSection getLoreSection(ConfigurationSection items) {
		return ConfigHandler.getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".lore");
	}
	
	private static long getAnimateTicks(String decrypt) {
		if (decrypt != null && decrypt.contains("<delay:" + Utils.returnInteger(decrypt) + ">")) { return Utils.returnInteger(decrypt); }
		return 20;
	}
}