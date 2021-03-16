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
package me.RockinChaos.itemjoin.utils.api;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class EffectAPI {
	
   /**
    * Spawns a fake particle.
    * 
    * @param player - The Player spawning the Particle.
    * @param commandParticle - The Particle to be built.
    */
	public static void spawnParticle(final Player player, final String commandParticle) {
		if (StringUtils.getUtils().containsIgnoreCase(commandParticle, "FIREWORK")) {
			particleFirework(player, commandParticle);
		} else {
			try {
				String[] particleParts = commandParticle.split(":");
				org.bukkit.Particle particle;
				int particleLife = 1;
				particle = org.bukkit.Particle.valueOf(particleParts[0]);
				if (particleParts[1] != null && !particleParts[1].isEmpty() && StringUtils.getUtils().isInt(particleParts[1])) { particleLife = Integer.parseInt(particleParts[1]); }
				player.getWorld().spawnParticle(particle, player.getLocation(), particleLife);
			} catch (Exception e) {
				ServerUtils.logSevere("{EffectAPI} There was an issue executing the commands-particle you defined.");
				ServerUtils.logWarn("{EffectAPI} " + commandParticle + " is not a particle in " + ReflectionUtils.getServerVersion() + ".");
				ServerUtils.sendDebugTrace(e);
			}
		}
	}

   /**
    * Launches a fake Firework.
    * 
    * @param player - The Player launching the Firework.
    * @param commandParticle - The Firework particle to be built.
    */
	private static void particleFirework(final Player player, final String commandParticle) {
		String[] projectileParts = commandParticle.split(":");
		Color startColor = Color.PURPLE;
		Color endColor = Color.GREEN;
		Type effectType = FireworkEffect.Type.STAR;
		int detonationDelay = 0;
		if (projectileParts[1] != null && !projectileParts[1].isEmpty()) { startColor = DyeColor.valueOf(projectileParts[1].toUpperCase()).getFireworkColor(); }
		if (projectileParts[2] != null && !projectileParts[2].isEmpty()) { endColor = DyeColor.valueOf(projectileParts[2].toUpperCase()).getFireworkColor(); }
		if (projectileParts[3] != null && !projectileParts[3].isEmpty()) { effectType = FireworkEffect.Type.valueOf(projectileParts[3]); }
		if (projectileParts[4] != null && !projectileParts[4].isEmpty() && StringUtils.getUtils().isInt(projectileParts[4])) { detonationDelay = Integer.parseInt(projectileParts[4]); }
		FireworkEffect effect = FireworkEffect.builder().withColor(startColor).withFade(endColor).with(effectType).trail(true).flicker(true).build();
        final Firework fw = (Firework)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(1);
        fw.setFireworkMeta(meta);
        SchedulerUtils.runLater((detonationDelay * 20), () -> { 
        	fw.detonate();
        });
	}
}
