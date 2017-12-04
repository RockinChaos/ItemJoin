package me.RockinChaos.itemjoin.cacheitems;

import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderImageMaps extends MapRenderer {

	private static String writeImage;
	private static int id;
	private static HashMap < Player, Integer > hasRendered = new HashMap < Player, Integer > ();
	
	public static void setImage(String image, int imageID) {
		writeImage = image;
		id = imageID;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (hasRendered.get(player) == null || hasRendered.get(player) != null && !hasRendered.get(player).toString().contains(id + "")) {
			try {
				if (writeImage != null && !writeImage.equalsIgnoreCase("default.png")) {
					hasRendered.put(player, id);
					canvas.drawImage(0, 0, ImageIO.read(new File(ItemJoin.getInstance().getDataFolder(), String.valueOf(writeImage))));
					ServerHandler.sendDebugMessage("rendering map; " + writeImage);
				} else if (writeImage != null && writeImage.equalsIgnoreCase("default.png") && ItemJoin.getInstance().getResource("default.png") != null) {
					hasRendered.put(player, id);
					canvas.drawImage(0, 0, ImageIO.read(ItemJoin.getInstance().getResource("default.png")));
					ServerHandler.sendDebugMessage("rendering map; default.png");
				}
			} catch (Exception e) {
				sendErrors();
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static MapView MapView(Player player, int id) {
		ItemJoin.getInstance().getServer().createMap(player.getWorld());
		MapView view = ItemJoin.getInstance().getServer().getMap((short) id);
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