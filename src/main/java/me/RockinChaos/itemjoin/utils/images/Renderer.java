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
package me.RockinChaos.itemjoin.utils.images;

import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Renderer extends MapRenderer {

    private final List<Integer> Rendered = new ArrayList<>();
    private GIF gifImage = null;
    private boolean repeatForever = false;
    private int currentFrame = 0;
    private int toRepeat = 0;
    private int ticksToWait = 0;
    private boolean stopRendering = false;
    private int id = 0;
    private String staticImage = null;
    private BufferedImage imgCache = null;

    /**
     * Creates a new ImageMap instance.
     * This is a STATIC image only.
     *
     * @param image   - The image to be rendered, ex: 'default.jpg'.
     * @param imageID - The id of the MapView.
     */
    public Renderer(final String image, final int imageID) {
        this.id = imageID;
        this.staticImage = image;
        if (image != null && !image.equalsIgnoreCase("default.jpg")) {
            try {
                this.imgCache = ImageIO.read(new File(ItemJoin.getCore().getPlugin().getDataFolder(), image));
            } catch (IOException e) {
                ServerUtils.sendDebugTrace(e);
            }
        } else if (image != null && image.equalsIgnoreCase("default.jpg") && ItemJoin.getCore().getPlugin().getResource("files/generated/default.jpg") != null) {
            try {
                this.imgCache = ImageIO.read(Objects.requireNonNull(ItemJoin.getCore().getPlugin().getResource("files/generated/default.jpg")));
            } catch (IOException e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Creates a new ImageMap instance.
     * This is a GIF and/or ANIMATED image.
     *
     * @param image      - The image to be rendered, ex: 'default.jpg'.
     * @param startFrame - The frame of the GIF to start at.
     * @param repeat     - The number of times to repeat the GIF animation.
     */
    public Renderer(final String image, final int startFrame, final int repeat) {
        this.gifImage = new GIF(image);
        this.currentFrame = startFrame;
        this.toRepeat = repeat;
        this.ticksToWait = (this.gifImage.get(this.currentFrame).getDelay() / 1000 * 20);
        this.repeatForever = this.toRepeat < 0;
        if (this.gifImage == null) {
            ServerUtils.logSevere("{Renderer} GIF image must not be null.");
        }
        if (!(startFrame >= 0 && startFrame < this.gifImage.getFrameCount())) {
            ServerUtils.logSevere("{Renderer} Frame index out of bounds.");
        }
        ServerUtils.logDebug("{Renderer} Rendering custom-map-image; " + image + " with the id " + this.id);
    }

    /**
     * Renders the MapView to the MapCanvas.
     *
     * @param mapView   - The MapView being rendered.
     * @param mapCanvas - The canvas to be drawn to.
     * @param player    - The Player having their map rendered.
     */
    @Override
    public void render(@Nonnull final MapView mapView, @Nonnull final MapCanvas mapCanvas, @Nonnull final Player player) {
        if (this.gifImage != null) {
            if (this.ticksToWait-- > 0 || this.stopRendering) {
                return;
            }
            if (this.currentFrame >= this.gifImage.getFrameCount()) {
                this.currentFrame = 0;
                if (!this.repeatForever && --this.toRepeat == 0) {
                    this.stopRendering = true;
                    return;
                }
            }
            GIF.Frame frame = this.gifImage.get(this.currentFrame++);
            mapCanvas.drawImage(0, 0, frame.getImage());
            this.ticksToWait = (frame.getDelay() / 1000 * 20);
        } else if (this.Rendered.isEmpty() || !this.Rendered.contains(this.id)) {
            try {
                this.Rendered.add(this.id);
                mapView.setScale(MapView.Scale.NORMAL);
                mapCanvas.drawImage(0, 0, this.imgCache);
                ServerUtils.logDebug("{Renderer} Rendering custom-map-image; " + this.staticImage + " with the id " + this.id);
            } catch (Exception e) {
                ServerUtils.logSevere("{Renderer} There was a problem rending your map(s)!");
                ServerUtils.logWarn("{Renderer} Please check and make sure your image size is no larger than 128x128 pixels.");
                ServerUtils.sendDebugTrace(e);
            }
        }
    }
}