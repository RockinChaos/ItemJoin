package me.RockinChaos.itemjoin.guicreator;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button {
    private static int counter;
    private final int ID = counter++;
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> action;

    public Button(ItemStack itemStack) {
    	this(itemStack, event -> {});
    }
    
    public Button(ItemStack itemStack, Consumer < InventoryClickEvent > action) {
    	this.itemStack = itemStack;
    	this.action = action;
    }
    
    public ItemStack getItemStack() {
    	return this.itemStack;
    }
    
    public void setAction(Consumer < InventoryClickEvent > action) {
    	this.action = action;
    }
    
    public void onClick(InventoryClickEvent event) {
    	this.action.accept(event);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) { return true; }
    	if (!(obj instanceof Button)) { return false; }
    	Button button = (Button) obj;
    	return this.ID == button.ID;
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(this.ID);
    }
}