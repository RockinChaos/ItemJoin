package me.RockinChaos.itemjoin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderImageMaps extends MapRenderer {

	private static String writeImage;
	private static int id;
	public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
	public static HashMap < Player, Integer > hasRendered = new HashMap < Player, Integer > ();
	public static void setImage(String image, int imageID) {
		writeImage = image;
		id = imageID;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (hasRendered.get(player) == null || hasRendered.get(player) != null && !hasRendered.get(player).toString().contains(id + "")) {
			try {
				if (!writeImage.equalsIgnoreCase("default.png")) {
					hasRendered.put(player, id);
					canvas.drawImage(0, 0, ImageIO.read(new File(ItemJoin.pl.getDataFolder(), String.valueOf(writeImage))));
					ServerHandler.sendDebugMessage("rendering map; " + writeImage);
				} else if (writeImage.equalsIgnoreCase("default.png") && ItemJoin.pl.getResource("default.png") != null) {
					hasRendered.put(player, id);
					canvas.drawImage(0, 0, ImageIO.read(ItemJoin.pl.getResource("default.png")));
					ServerHandler.sendDebugMessage("rendering map; default.png");
				}
			} catch (IOException e) {
				sendErrors();
			} catch (IllegalArgumentException e) {
				sendErrors();
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static MapView MapView(Player player, int id) {
		ItemJoin.pl.getServer().createMap(player.getWorld());
		MapView view = ItemJoin.pl.getServer().getMap((short) id);
		return view;
	}
	
	public static void clearMaps(Player player) {
		hasRendered.remove(player);
	}
	public static void sendErrors() {
		ServerHandler.sendConsoleMessage("&4[ERROR; 7753c61] There was a problem rending your map(s)!");
		ServerHandler.sendConsoleMessage("&4Please check and make sure your image size is no larger than 128x128 pixels.");
		ServerHandler.sendConsoleMessage("&4If you are still experiencing this error please contact the plugin developer!");
	}
	}