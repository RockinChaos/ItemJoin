package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Interface implements Listener {

    /**
     * Handles the inventory close action for the virtualInventory.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if (Menu.modifyMenu(player)) {
            SchedulerUtils.runAsyncLater(40L, () -> {
                if (!Menu.isOpen(player)) {
                    Menu.setModifyMenu(false, (Player) event.getPlayer());
                    for (final ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                        if (itemMap.getAnimationHandler() != null && itemMap.getAnimationHandler().get(player) != null) {
                            itemMap.getAnimationHandler().get(player).setMenu(false, 0);
                        }
                    }
                }
            });
        }
    }
}