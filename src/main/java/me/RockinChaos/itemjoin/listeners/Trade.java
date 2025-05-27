package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;

public class Trade implements Listener {

    /**
     * Prevents the player from trading custom items with the item-store itemflag.
     *
     * @param event - TradeSelectEvent
     */
    @EventHandler()
    private void onTradeSelect(TradeSelectEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack[] cloneContents = null;
        final Player player = (Player) event.getWhoClicked();
        final ItemStack[] playerContents = player.getInventory().getContents();
        for (final ItemStack ingredient : event.getMerchant().getRecipe(event.getIndex()).getIngredients()) {
            boolean vanillaItem = false;
            boolean customItem = false;
            for (ItemStack item : playerContents) {
                if (item != null && (item.getType() == ingredient.getType())) {
                    if (!ItemUtilities.getUtilities().isAllowed(player, item, "item-store")) {
                        customItem = true;
                    } else {
                        vanillaItem = true;
                    }
                }
            }
            if (customItem && !vanillaItem) {
                cloneContents = null;
                event.setCancelled(true);
                break;
            } else if (cloneContents == null) {
                cloneContents = ItemHandler.cloneContents(playerContents);
            }
        }
        final ItemStack[] contents = cloneContents;
        SchedulerUtils.run(() -> {
            if (contents != null && (!ItemUtilities.getUtilities().isAllowed(player, CompatUtils.getTopInventory(event.getView()).getItem(0), "item-store") || !ItemUtilities.getUtilities().isAllowed(player, CompatUtils.getTopInventory(event.getView()).getItem(1), "item-store"))) {
                ItemHandler.restoreContents(player.getInventory(), contents);
                CompatUtils.getTopInventory(player).clear();
                PlayerHandler.updateInventory(player, 1L);
            }
        });
    }
}