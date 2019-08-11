package me.RockinChaos.itemjoin.guicreator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class Page {
    private List<Button> buttons = new ArrayList<>();
    private int maxSize;

    Page(int maxSize) { this.maxSize = maxSize; }

    public void handleClick(InventoryClickEvent event) {
    	if (event.getRawSlot() > event.getInventory().getSize()) { return; }
    	if (event.getSlot() >= this.buttons.size()) { return; }
    	Button button = this.buttons.get(event.getSlot());
    	button.onClick(event);
    }
    
    public boolean addButton(Button button) {
    	if (!this.hasSpace()) { return false; }
    	this.buttons.add(button);
    	return true;
    }
    
    public boolean removeButton(Button button) {
    	return this.buttons.remove(button);
    }
    
    public void render(Inventory inventory) {
    	for (int i = 0; i < this.buttons.size(); i++) {
    		Button button = this.buttons.get(i);
    		inventory.setItem(i, button.getItemStack());
    	}
    }
    
    private boolean hasSpace() {
    	return this.buttons.size() < (this.maxSize * 9);
    }
    
    public boolean isEmpty() {
    	return this.buttons.isEmpty();
    }
}