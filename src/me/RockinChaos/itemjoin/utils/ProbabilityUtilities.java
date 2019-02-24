package me.RockinChaos.itemjoin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbabilityUtilities {
	
	private class Chance {
		private int upperLimit;
		private int lowerLimit;
		private Object element;
		
		public Chance(Object element, int lowerLimit, int upperLimit) {
			this.element = element;
			this.upperLimit = upperLimit;
			this.lowerLimit = lowerLimit;
		}
		
		public int getUpperLimit() { return this.upperLimit; }
		public int getLowerLimit() { return this.lowerLimit; }
		public Object getElement() { return this.element; }
		@Override
		public String toString() { return "[" + Integer.toString(this.lowerLimit) + "|" + Integer.toString(this.upperLimit) + "]: " + this.element.toString(); }
	}
	
	private List < Chance > chances;
	private int sum;
	private Random random;
	
	public ProbabilityUtilities() {
		this.random = new Random();
		this.chances = new ArrayList < > ();
		this.sum = 0;
	}
	
	public ProbabilityUtilities(long seed) {
		this.random = new Random(seed);
		this.chances = new ArrayList < > ();
		this.sum = 0;
	}
	
	public void addChance(Object element, int chance) {
		if (!this.chances.contains(element)) {
			this.chances.add(new Chance(element, this.sum, this.sum + chance));
			this.sum = this.sum + chance;
		}
	}
	
	public Object getRandomElement() {
		int index = this.random.nextInt(this.sum);
		for (Chance chance: this.chances) {
			if (chance.getLowerLimit() <= index && chance.getUpperLimit() > index) {
				return chance.getElement();
			}
		}
		return null;
	}
	
	public int getOptions() {
		return this.sum;
	}
	
	public int getChoices() {
		return this.chances.size();
	}
}