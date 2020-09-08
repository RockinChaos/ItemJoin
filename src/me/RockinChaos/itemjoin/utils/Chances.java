/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.item.ItemMap;

public class Chances {
	private List < Chance > chances;
	private int sum;
	private Random random;
	
	private Map < ItemMap, Integer > probabilityItems = new HashMap < ItemMap, Integer > ();
	
	private static Chances chance;
	
   /**
    * Creates a new Chances instance.
    * 
    */
	public Chances() { }
	
   /**
    * Initializes the Chances instance.
    * 
    */
	public void newChance() {
		this.random = new Random();
		this.chances = new ArrayList < > ();
		this.sum = 0;
	}
	
   /**
    * Initializes the Chances instance.
    * 
    * @param seed - The random seed.
    */
	public void newChance(final long seed) {
		this.random = new Random(seed);
		this.chances = new ArrayList < > ();
		this.sum = 0;
	}
	
   /**
    * Adds an Object and its Chance to the pool of Objects to be randomly selected.
    * 
    * @param element - The Object to be selected.
    * @param chance - The Integer chance the Object has to be selected.
    */
	public void addChance(final Object element, final int chance) {
		if (!this.chances.contains(element)) {
			this.chances.add(new Chance(element, this.sum, this.sum + chance));
			this.sum += chance;
		}
	}
	
   /**
    * Gets a randomly selected Object.
    * 
    * @return The randomly selected Object.
    */
	public Object getRandomElement() {
		int index = this.random.nextInt(this.sum);
		for (Chance chance : this.chances) {
			if (chance != null && chance.getLowerLimit() <= index && chance.getUpperLimit() > index) {
				return chance.getElement();
			}
		}
		return null;
	}
	
   /**
    * Gets the sum.
    * 
    * @return The options of the sum.
    */
	public int getOptions() {
		return this.sum;
	}
	
   /**
    * Gets the ItemMap and the Probability HashMap.
    * 
    * @return The ItemMaps and their Probabilities as a HashMap.
    */
	public Map<ItemMap, Integer> getItems() {
		return this.probabilityItems;
	}
	
   /**
    * Adds an ItemMap and its Probability to the HashMap pool.
    * 
    * @param itemMap - The ItemMap to be selected.
    * @param i - The chance the ItemMap has to be selected.
    */
	public void putItem(final ItemMap itemMap, final int i) {
		this.probabilityItems.put(itemMap, i);
	}
	
   /**
    * Checks if the ItemMap is a probability item.
    * 
    * @param itemMap - The ItemMap being checked.
    * @param probability - The Probability ItemMap being compared.
    * @return If the ItemMap is a probability item.
    */
	public boolean isProbability(final ItemMap itemMap, final ItemMap probability) {
		if ((probability != null && itemMap.getConfigName().equals(probability.getConfigName())) || itemMap.getProbability().equals(-1)) {
			return true;
		}
		return false;
	}
	
   /**
    * Selects a Random ItemMap from the list of Probability items.
    * 
    * @param player - The Player to have its Probability Item chosen.
    */
	public ItemMap getRandom(final Player player) {
		this.newChance();
		if (probabilityItems != null && !probabilityItems.isEmpty()) {
			for (ItemMap itemMap: probabilityItems.keySet()) {
				if (itemMap.hasItem(player)) { 
					return itemMap; 
				}
				if (probabilityItems.get(itemMap) != null) { 
					this.addChance(itemMap, probabilityItems.get(itemMap)); 
				}
			}
			return ((ItemMap) this.getRandomElement());
		}
		return null;
	}
	
   /**
    * Gets the instance of the Chances.
    * 
    * @return The Chances instance.
    */
    public static Chances getChances() { 
        if (chance == null) {
        	chance = new Chances(); 
        }
        return chance; 
    } 
	
   /**
    * The Chances class.
    * 
    */
	private class Chance {
		private int upperLimit;
		private int lowerLimit;
		private Object element;
		
	   /**
    	* Creates a new Chance instance.
    	* 
    	* @param element - The Object to be chosen.
    	* @param lowerLimit - The lowest probability. 
    	* @param upperLimit - The highest probability.
    	*/
		private Chance(final Object element, final int lowerLimit, final int upperLimit) {
			this.element = element;
			this.upperLimit = upperLimit;
			this.lowerLimit = lowerLimit;
		}
		
	   /**
    	* Gets the highest probability.
    	* 
    	* @return The highest probability.
    	*/
		private int getUpperLimit() { return this.upperLimit; }
		
	   /**
    	* Gets the lowest probability.
    	* 
    	* @return The lowest probability.
    	*/
		private int getLowerLimit() { return this.lowerLimit; }
		
	   /**
    	* Gets the Object to be chosen.
    	* 
    	* @return The Object to be chosen.
    	*/
		private Object getElement() { return this.element; }
		
	   /**
    	* Handles the toString of a Map Element.
    	* 
    	* @return The newly formatted Map Element as a String instance.
    	*/
		@Override
		public String toString() { return "[" + Integer.toString(this.lowerLimit) + "|" + Integer.toString(this.upperLimit) + "]: " + this.element.toString(); }
	}
}