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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GIF {

    private final List<Frame> frames;

    /**
     * Creates a new GIFImage.
     *
     * @param image - The image file name with extension to be located.
     */
    public GIF(String image) {
        GIFDecoder decoder = new GIFDecoder(ItemJoin.getCore().getPlugin().getDataFolder(), image);
        this.frames = IntStream.range(0, decoder.getFrameCount()).mapToObj((i) -> new Frame(decoder.getFrame(i), decoder.getDelay(i))).toList();
    }

    /**
     * Returns the amount of frames this gif holds.
     */
    public int getFrameCount() {
        return this.frames.size();
    }

    /**
     * Returns the frame at the position of the specified index.
     *
     * @param index the index of the {@link Frame} to retrieve.
     * @return a never-{@code null} {@link Frame} that gives access to its image and delay.
     * @throws IndexOutOfBoundsException if the index is out of bounds for the list of frames.
     */
    public Frame get(int index) {
        return this.frames.get(index);
    }

    /**
     * A class for decoding an animated gif.
     *
     * @see GIF
     * @see Frame
     */

    public static class GIFDecoder {
        private final int STATUS_OK = 0;
        private final int STATUS_FORMAT_ERROR = 1;
        private final byte[] block = new byte[256];
        private final ArrayList<Frame> frames;
        private BufferedInputStream in;
        private int status;
        private int width;
        private int height;
        private boolean gctFlag;
        private int gctSize;
        private int[] gct;
        private int[] lct;
        private int[] act;
        private int bgIndex;
        private int bgColor;
        private int lastBgColor;
        private boolean interlace;
        private int ix, iy, iw, ih;
        private Rectangle lastRect;
        private BufferedImage image;
        private BufferedImage lastImage;
        private int blockSize = 0;
        private int dispose = 0;
        private int lastDispose = 0;
        private boolean transparency = false;
        private int delay = 0;
        private int transIndex;
        private short[] prefix;
        private byte[] suffix;
        private byte[] pixelStack;
        private byte[] pixels;
        private int frameCount;

        private GIFDecoder(File folder, String image) {
            this.status = this.STATUS_OK;
            this.frameCount = 0;
            this.frames = new ArrayList<>();
            this.gct = null;
            this.lct = null;
            FileInputStream fileStream = null;
            try {
                fileStream = new FileInputStream(new File(folder, image));
            } catch (FileNotFoundException e) {
                ServerUtils.sendSevereTrace(e);
            }
            BufferedInputStream bufferedStream = null;
            if (fileStream != null) {
                bufferedStream = new BufferedInputStream(fileStream);
                this.in = bufferedStream;
                this.readHeader();
                if (this.status == this.STATUS_OK) {
                    this.readContents();
                    if (this.frameCount < 0) {
                        this.status = this.STATUS_FORMAT_ERROR;
                    }
                }
            } else {
                this.status = 2;
            }
            try {
                if (fileStream != null) {
                    fileStream.close();
                    bufferedStream.close();
                }
            } catch (IOException ignored) {
            }
        }

        /**
         * Gets display duration for specified frame.
         *
         * @param n - int index of frame
         * @return delay in milliseconds
         */
        private int getDelay(int n) {
            this.delay = -1;
            if ((n >= 0) && (n < this.frameCount)) {
                this.delay = this.frames.get(n).delay;
            }
            return this.delay;
        }

        /**
         * Gets the number of frames read from file.
         *
         * @return frame count
         */
        private int getFrameCount() {
            return this.frameCount;
        }

        /**
         * Creates new frame image from current data (and previous frames as specified by their disposition codes).
         */
        private void setPixels() {
            int[] dest = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
            if (this.lastDispose > 0) {
                if (this.lastDispose == 3) {
                    int n = this.frameCount - 2;
                    if (n > 0) {
                        this.lastImage = this.getFrame(n - 1);
                    } else {
                        this.lastImage = null;
                    }
                }
                if (this.lastImage != null) {
                    int[] prev = ((DataBufferInt) this.lastImage.getRaster().getDataBuffer()).getData();
                    System.arraycopy(prev, 0, dest, 0, width * height);
                    if (this.lastDispose == 2) {
                        Graphics2D g = this.image.createGraphics();
                        Color c;
                        if (this.transparency) {
                            c = new Color(0, 0, 0, 0);
                        } else {
                            c = new Color(this.lastBgColor);
                        }
                        g.setColor(c);
                        g.setComposite(AlphaComposite.Src);
                        g.fill(this.lastRect);
                        g.dispose();
                    }
                }
            }

            int pass = 1;
            int inc = 8;
            int inline = 0;
            for (int i = 0; i < this.ih; i++) {
                int line = i;
                if (this.interlace) {
                    if (inline >= this.ih) {
                        pass++;
                        switch (pass) {
                            case 2:
                                inline = 4;
                                break;
                            case 3:
                                inline = 2;
                                inc = 4;
                                break;
                            case 4:
                                inline = 1;
                                inc = 2;
                        }
                    }
                    line = inline;
                    inline += inc;
                }
                line += this.iy;
                if (line < this.height) {
                    int k = line * this.width;
                    int dx = k + this.ix;
                    int dlm = dx + this.iw;
                    if ((k + this.width) < dlm) {
                        dlm = k + this.width;
                    }
                    int sx = i * this.iw;
                    while (dx < dlm) {
                        int index = ((int) this.pixels[sx++]) & 0xff;
                        int c = this.act[index];
                        if (c != 0) {
                            dest[dx] = c;
                        }
                        dx++;
                    }
                }
            }
        }

        /**
         * Gets the image contents of frame n.
         *
         * @param n - The frame to be fetched.
         * @return BufferedImage representation of frame, or null if n is invalid.
         */
        private BufferedImage getFrame(int n) {
            BufferedImage im = null;
            if ((n >= 0) && (n < this.frameCount)) {
                im = this.frames.get(n).image;
            }
            return im;
        }

        /**
         * Decodes LZW image data into pixel array.
         */
        private void decodeImageData() {
            int NullCode = -1;
            int nPix = this.iw * this.ih;
            int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;
            if ((this.pixels == null) || (this.pixels.length < nPix)) {
                this.pixels = new byte[nPix];
            }
            int maxStackSize = 4096;
            if (this.prefix == null) this.prefix = new short[maxStackSize];
            if (this.suffix == null) this.suffix = new byte[maxStackSize];
            if (this.pixelStack == null) this.pixelStack = new byte[maxStackSize + 1];
            data_size = this.read();
            clear = 1 << data_size;
            end_of_information = clear + 1;
            available = clear + 2;
            old_code = NullCode;
            code_size = data_size + 1;
            code_mask = (1 << code_size) - 1;
            for (code = 0; code < clear; code++) {
                this.prefix[code] = 0;
                this.suffix[code] = (byte) code;
            }
            datum = bits = count = first = top = pi = bi = 0;
            for (i = 0; i < nPix; ) {
                if (top == 0) {
                    if (bits < code_size) {
                        if (count == 0) {
                            count = this.readBlock();
                            if (count <= 0) {
                                break;
                            }
                            bi = 0;
                        }
                        datum += (((int) this.block[bi]) & 0xff) << bits;
                        bits += 8;
                        bi++;
                        count--;
                        continue;
                    }
                    code = datum & code_mask;
                    datum >>= code_size;
                    bits -= code_size;
                    if ((code > available) || (code == end_of_information))
                        break;
                    if (code == clear) {
                        code_size = data_size + 1;
                        code_mask = (1 << code_size) - 1;
                        available = clear + 2;
                        old_code = NullCode;
                        continue;
                    }
                    if (old_code == NullCode) {
                        this.pixelStack[top++] = this.suffix[code];
                        old_code = code;
                        first = code;
                        continue;
                    }
                    in_code = code;
                    if (code == available) {
                        this.pixelStack[top++] = (byte) first;
                        code = old_code;
                    }
                    while (code > clear) {
                        this.pixelStack[top++] = this.suffix[code];
                        code = this.prefix[code];
                    }
                    first = ((int) this.suffix[code]) & 0xff;
                    if (available >= maxStackSize) {
                        this.pixelStack[top++] = (byte) first;
                        continue;
                    }
                    this.pixelStack[top++] = (byte) first;
                    this.prefix[available] = (short) old_code;
                    this.suffix[available] = (byte) first;
                    available++;
                    if (((available & code_mask) == 0)
                            && (available < maxStackSize)) {
                        code_size++;
                        code_mask += available;
                    }
                    old_code = in_code;
                }
                top--;
                this.pixels[pi++] = this.pixelStack[top];
                i++;
            }
            for (i = pi; i < nPix; i++) {
                this.pixels[i] = 0;
            }
        }

        /**
         * Reads a single byte from the input stream.
         *
         * @return The single byte read from the input stream.
         */
        private int read() {
            int curByte = 0;
            try {
                curByte = this.in.read();
            } catch (IOException e) {
                this.status = this.STATUS_FORMAT_ERROR;
            }
            return curByte;
        }

        /**
         * Reads next variable length block from input.
         *
         * @return number of bytes stored in "buffer"
         */
        private int readBlock() {
            this.blockSize = this.read();
            int n = 0;
            if (this.blockSize > 0) {
                try {
                    int count;
                    while (n < this.blockSize) {
                        count = this.in.read(this.block, n, this.blockSize - n);
                        if (count == -1) {
                            break;
                        }
                        n += count;
                    }
                } catch (IOException ignored) {
                }
                if (n < this.blockSize) {
                    this.status = this.STATUS_FORMAT_ERROR;
                }
            }
            return n;
        }

        /**
         * Reads color table as 256 RGB integer values
         *
         * @param nColors - int number of colors to read
         * @return int array containing 256 colors (packed ARGB with full alpha)
         */
        private int[] readColorTable(int nColors) {
            int nBytes = 3 * nColors;
            int[] tab = null;
            byte[] c = new byte[nBytes];
            int n = 0;
            try {
                n = this.in.read(c);
            } catch (IOException ignored) {
            }
            if (n < nBytes) {
                this.status = this.STATUS_FORMAT_ERROR;
            } else {
                tab = new int[256];
                int i = 0;
                int j = 0;
                while (i < nColors) {
                    int r = ((int) c[j++]) & 0xff;
                    int g = ((int) c[j++]) & 0xff;
                    int b = ((int) c[j++]) & 0xff;
                    tab[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
                }
            }
            return tab;
        }

        /**
         * Main file parser.  Reads GIF content blocks.
         */
        private void readContents() {
            boolean done = false;
            while (!(done || (this.status != this.STATUS_OK))) {
                int code = this.read();
                switch (code) {
                    case 0x2C:
                        this.readImage();
                        break;
                    case 0x21:
                        code = this.read();
                        switch (code) {
                            case 0xf9:
                                this.readGraphicControlExtension();
                                break;
                            case 0xff:
                                this.readBlock();
                                StringBuilder app = new StringBuilder();
                                for (int i = 0; i < 11; i++) {
                                    app.append((char) this.block[i]);
                                }
                                if (app.toString().equals("NETSCAPE2.0")) {
                                    this.readExtension();
                                } else {
                                    this.skip();
                                }
                                break;
                            default:
                                this.skip();
                        }
                        break;
                    case 0x3b:
                        done = true;
                        break;
                    case 0x00:
                        break;
                    default:
                        this.status = this.STATUS_FORMAT_ERROR;
                }
            }
        }

        /**
         * Reads Graphics Control Extension values.
         */
        private void readGraphicControlExtension() {
            this.read();
            int packed = this.read();
            this.dispose = (packed & 0x1c) >> 2;
            if (this.dispose == 0) {
                this.dispose = 1;
            }
            this.transparency = (packed & 1) != 0;
            this.delay = this.readShort() * 10;
            this.transIndex = this.read();
            this.read();
        }

        /**
         * Reads GIF file header information.
         */
        private void readHeader() {
            StringBuilder id = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                id.append((char) this.read());
            }
            if (!id.toString().startsWith("GIF")) {
                this.status = this.STATUS_FORMAT_ERROR;
                return;
            }
            this.readLSD();
            if (this.gctFlag && this.status == this.STATUS_OK) {
                this.gct = this.readColorTable(this.gctSize);
                this.bgColor = this.gct[this.bgIndex];
            }
        }

        /**
         * Reads next frame image.
         */
        private void readImage() {
            this.ix = this.readShort();
            this.iy = this.readShort();
            this.iw = this.readShort();
            this.ih = this.readShort();
            int save = 0;
            int packed = this.read();
            boolean lctFlag = (packed & 0x80) != 0;
            this.interlace = (packed & 0x40) != 0;
            int lctSize = 2 << (packed & 7);
            if (lctFlag) {
                this.lct = this.readColorTable(lctSize);
                this.act = this.lct;
            } else {
                this.act = this.gct;
                if (this.bgIndex == this.transIndex)
                    this.bgColor = 0;
            }
            if (this.transparency) {
                save = this.act[this.transIndex];
                this.act[this.transIndex] = 0;
            }
            if (this.act == null) {
                this.status = this.STATUS_FORMAT_ERROR;
            }
            if (this.status != this.STATUS_OK) {
                return;
            }
            this.decodeImageData();
            this.skip();
            if (this.status != this.STATUS_OK) {
                return;
            }
            this.frameCount++;
            this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB_PRE);
            this.setPixels();
            this.frames.add(new Frame(this.image, this.delay));
            if (this.transparency) {
                this.act[this.transIndex] = save;
            }
            this.resetFrame();
        }

        /**
         * Reads Logical Screen Descriptor.
         */
        private void readLSD() {
            this.width = this.readShort();
            this.height = this.readShort();
            int packed = this.read();
            this.gctFlag = (packed & 0x80) != 0;
            this.gctSize = 2 << (packed & 7);
            this.bgIndex = this.read();
            this.read();
        }

        /**
         * Reads an extension to obtain iteration count.
         */
        private void readExtension() {
            do {
                this.readBlock();
            }
            while ((this.blockSize > 0) && this.status == this.STATUS_OK);
        }

        /**
         * Reads next 16-bit value, LSD first.
         *
         * @return The read integer.
         */
        private int readShort() {
            return this.read() | (this.read() << 8);
        }

        /**
         * Resets frame state for reading next image.
         */
        private void resetFrame() {
            this.lastDispose = this.dispose;
            this.lastRect = new Rectangle(this.ix, this.iy, this.iw, this.ih);
            this.lastImage = this.image;
            this.lastBgColor = this.bgColor;
            this.lct = null;
        }

        /**
         * Skips variable length blocks up to and including next zero length block.
         */
        private void skip() {
            do {
                this.readBlock();
            }
            while ((this.blockSize > 0) && this.status == this.STATUS_OK);
        }
    }

    /**
     * A class representing a single frame in an animated gif.
     *
     * @see GIF
     */
    public static class Frame {
        private final BufferedImage image;
        private final int delay;

        /**
         * Creates a new Frame instance.
         *
         * @param image the image this frame displays.
         * @param delay - A duration in milliseconds, i.e. how long this frame should be displayed.
         * @throws IllegalArgumentException if the given duration/delay is not positive.
         */
        private Frame(BufferedImage image, int delay) {
            this.image = image;
            this.delay = delay;
            if (image == null) {
                ServerUtils.logSevere("{GIF} Image must not be null.");
            }
            if (!(delay > 0)) {
                ServerUtils.logSevere("{GIF} Duration must be positive.");
            }
        }

        /**
         * Gets the duration in milliseconds indicating how long this particular frame should be displayed in a gif.
         *
         * @return The Duration the frame should exist.
         */
        public int getDelay() {
            return this.delay;
        }

        /**
         * Gets the image of this frame.
         *
         * @return The BufferedImage for this frame.
         */
        public BufferedImage getImage() {
            return this.image;
        }
    }
}