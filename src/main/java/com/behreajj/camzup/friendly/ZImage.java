package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Rgb;
import com.behreajj.camzup.core.Utils;
import processing.core.*;
import processing.core.PFont.Glyph;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * An extension of PImage, specializing in color gradients and blitting text to
 * an image.
 */
public class ZImage extends PImage {

    /**
     * Default horizontal alignment when creating an image from text.
     */
    public static final int DEFAULT_ALIGN = PConstants.LEFT;

    /**
     * Default spacing between characters, in pixels, when creating an image
     * from text.
     */
    public static final int DEFAULT_KERNING = 0;

    /**
     * Default spacing between lines, in pixels, when creating an image from
     * text.
     */
    public static final int DEFAULT_LEADING = 8;

    /**
     * Constructs an image from its dimensions, width and height.
     *
     * @param width  the image width
     * @param height the image height
     */
    public ZImage(final int width, final int height) {

        super(width, height);
    }

    /**
     * Constructs an image from the dimensions and format.
     *
     * @param width  the image width
     * @param height the image height
     * @param format the format
     */
    public ZImage(final int width, final int height, final int format) {

        super(width, height, format);
    }

    /**
     * Constructs an image from the dimensions, format and pixel density.
     *
     * @param width        the image width
     * @param height       the image height
     * @param format       the format
     * @param pixelDensity the pixel density
     */
    public ZImage(
        final int width,
        final int height,
        final int format,
        final int pixelDensity) {

        super(width, height, format, pixelDensity);
    }

    /**
     * The default constructor.
     */
    protected ZImage() {
    }

    /**
     * Convert an image in the {@link PConstants#ALPHA} format to an image in
     * the {@link PConstants#ARGB} format. Returns images of othe formats
     * unchanged.
     *
     * @param image the image
     * @return the conversion
     */
    public static PImage alphaToArgb(final PImage image) {

        /* Depart from (source, target) signature for this function. */
        if (image.format != PConstants.ALPHA
            || image.pixelWidth < 1
            || image.pixelHeight < 1) {
            return image;
        }

        image.loadPixels();
        final int[] px = image.pixels;
        final int len = px.length;
        for (int i = 0; i < len; ++i) {
            final int a = px[i];
            px[i] = a << 0x18 | a << 0x10 | a << 0x08 | a;
        }
        image.format = PConstants.ARGB;
        image.updatePixels();
        return image;
    }

    /**
     * Copies a source image's pixels to a target. If the target is a
     * {@link PGraphics}, then wraps the source image without changing the
     * target's dimensions.<br>
     * <br>
     * Does not copy the parent PApplet field.
     *
     * @param source the source image
     * @param target the target image
     * @return the copied image.
     * @see ZImage#wrap(PImage, int, int, PImage)
     */
    public static PImage copy(final PImage source, final PImage target) {

        if (source == target) {
            return target;
        }

        /*
         * An image can have dimensions of -1, -1 when invalid. See
         * https://processing.org/reference/loadImage_.html
         */
        if (source == null
            || source.pixelWidth < 1
            || source.pixelHeight < 1) {
            return ZImage.fill(0xffffffff, target);
        }

        if (target instanceof PGraphics
            && (source.pixelWidth != target.pixelWidth
            || source.pixelHeight != target.pixelHeight)) {
            return ZImage.wrap(source, 0, 0, target);
        }

        source.loadPixels();
        target.loadPixels();
        final int len = source.pixels.length;
        target.pixels = new int[len];
        System.arraycopy(source.pixels, 0, target.pixels, 0, len);
        target.format = source.format;
        target.pixelDensity = source.pixelDensity;
        target.pixelWidth = source.pixelWidth;
        target.pixelHeight = source.pixelHeight;
        target.width = source.width;
        target.height = source.height;
        target.updatePixels();

        return target;
    }

    /**
     * Fills an image with a color in place. The color is expected to be a
     * 32-bit color integer.
     *
     * @param c      the fill color
     * @param target the target image
     * @return the image
     */
    public static PImage fill(final int c, final PImage target) {

        target.loadPixels();
        final int len = target.pixels.length;
        for (int i = 0; i < len; ++i) {
            target.pixels[i] = c;
        }
        target.format = PConstants.ARGB;
        target.updatePixels();

        return target;
    }

    /**
     * Fills an image in place with a color.
     *
     * @param target the target image
     * @param c      the fill color
     * @return the image
     */
    public static PImage fill(final Rgb c, final PImage target) {

        return ZImage.fill(c.toHexIntSat(), target);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. <br>
     * Images created with this method do not have a reference to a parent
     * PApplet. Defaults to the fill color white.
     *
     * @param font the Processing font
     * @param text the string of text
     * @return the new image
     */
    public static PImage fromText(final PFont font, final String text) {

        return ZImage.fromText(
            font,
            text,
            0xffffffff,
            ZImage.DEFAULT_LEADING,
            ZImage.DEFAULT_KERNING,
            ZImage.DEFAULT_ALIGN);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final int fillClr) {

        return ZImage.fromText(
            font,
            text,
            fillClr,
            ZImage.DEFAULT_LEADING,
            ZImage.DEFAULT_KERNING,
            ZImage.DEFAULT_ALIGN);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * is measured in pixels; negative values are not allowed.<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @param leading spacing between lines
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final int fillClr,
        final int leading) {

        return ZImage.fromText(font, text, fillClr, leading,
            ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * and kerning are measured in pixels; negative values are not allowed<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @param leading spacing between lines
     * @param kerning spacing between characters
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final int fillClr,
        final int leading,
        final int kerning) {

        return ZImage.fromText(font, text, fillClr, leading, kerning,
            ZImage.DEFAULT_ALIGN);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * and kerning are measured in pixels; negative values are not allowed.
     * The horizontal text alignment may be either center
     * {@link PConstants#CENTER} ( {@value PConstants#CENTER} ), right
     * {@link PConstants#RIGHT} ( {@value PConstants#RIGHT} ) or left
     * {@link PConstants#LEFT} ( {@value PConstants#LEFT} ).<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font      the Processing font
     * @param text      the string of text
     * @param fillClr   the color
     * @param leading   spacing between lines
     * @param kerning   spacing between characters
     * @param textAlign the horizontal alignment
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final int fillClr,
        final int leading,
        final int kerning,
        final int textAlign) {

        /*
         * Validate inputs: colors with no alpha not allowed; negative leading
         * and kerning not allowed; try to guard against empty Strings. Remove
         * alpha from tint; source image alpha will be used.
         */
        final String vTxt = text.trim();
        if (vTxt.isEmpty()) {
            return new PImage(32, 32, PConstants.ARGB, 1);
        }
        final int vLead = leading < 0 ? 1 : leading + 1;
        final int vKern = Math.max(kerning, 0);
        final int vClr = (fillClr >> 0x18 & 0xff) != 0
            ? 0x00ffffff & fillClr
            : 0x00ffffff;

        /* Carriage returns, or line breaks, have 3 variants: \r, \n, or \r\n . */
        final Pattern patternLnBr = Pattern.compile("[\n|\r]+");
        final String[] linesSplit = patternLnBr.split(vTxt, 0);
        final int lineCount = linesSplit.length;

        /* 3D array: lines contain words which contain letters. */
        final char[][][] characters = new char[lineCount][][];
        final Pattern patternSpace = Pattern.compile("\\s+");
        for (int i = 0; i < lineCount; ++i) {
            final String[] words = patternSpace.split(linesSplit[i], 0);
            final int charCount = words.length;
            final char[][] charLine = characters[i] = new char[charCount][];
            for (int j = 0; j < charCount; ++j) {
                charLine[j] = words[j].toCharArray();
            }
        }

        final int fontSize = font.getSize();

        /* Determine width of a space. */
        final Glyph whiteSpace = font.getGlyph('i');
        final int spaceWidth = whiteSpace != null
            ? whiteSpace.width
            : (int) (fontSize * Utils.ONE_THIRD);

        /*
         * If a line contains only a space, then maxHeight below may wind up as
         * zero, and need to be replaced with a blank line.
         */
        final Glyph emptyLine = font.getGlyph('E');
        final int defaultHeight = emptyLine != null
            ? emptyLine.height
            : fontSize;

        /*
         * The last line's descenders are chopped off if padding isn't added to
         * the bottom of the image.
         */
        int lastRowPadding = 0;

        /* Total height and max width will decide the image's dimensions. */
        final int[] lineHeights = new int[lineCount];
        final int[] lineWidths = new int[lineCount];
        int hTotal = 0;
        int wMax = 0;

        /* Loop through lines. */
        final Glyph[][][] glyphs = new Glyph[lineCount][][];
        for (int i = 0; i < lineCount; ++i) {
            final char[][] charLine = characters[i];
            final int wordCount = charLine.length;
            final Glyph[][] glyphLine = new Glyph[wordCount][];
            glyphs[i] = glyphLine;

            int sumWidths = 0;
            int maxHeight = 0;
            int maxDescent = 0;

            /* Loop through words. */
            for (int j = 0; j < wordCount; ++j) {
                final char[] charWord = charLine[j];
                final int charCount = charWord.length;
                final Glyph[] glyphWord = new Glyph[charCount];
                glyphLine[j] = glyphWord;

                /* Loop through letters. */
                for (int k = 0; k < charCount; ++k) {
                    final char character = charWord[k];
                    final Glyph glyph = font.getGlyph(character);
                    glyphWord[k] = glyph;

                    if (glyph != null) {

                        /*
                         * All values are considered from the image's top-left corner.
                         * Top extent is the amount to move down from the top edge to
                         * find the space occupied by the glyph. Left extent is the
                         * amount to move left from the left edge to find the glyph.
                         * See {@link PFont} for diagram.
                         */
                        final int height = glyph.height + vLead;
                        final int glyphDescent = height - glyph.topExtent;

                        /*
                         * The height of a line is determined by its tallest glyph;
                         * the width of a line is the sum of each glyph's width, plus
                         * kerning, plus left extents.
                         */
                        if (height > maxHeight) {
                            maxHeight = height;
                        }
                        sumWidths += glyph.width + vKern + glyph.leftExtent;
                        if (glyphDescent > maxDescent) {
                            maxDescent = glyphDescent;
                        }
                        lastRowPadding = maxDescent;
                    }
                }

                /* Add a space between words. */
                sumWidths += spaceWidth + vKern;
            }

            /* maxHeight may be initial value. */
            if (maxHeight < 1) {
                maxHeight = defaultHeight;
            }
            lineHeights[i] = maxHeight;
            hTotal += maxHeight;

            lineWidths[i] = sumWidths;
            if (sumWidths > wMax) {
                wMax = sumWidths;
            }
        }

        hTotal += lastRowPadding;

        /*
         * Offset the xCursor's initial position depending on horizontal
         * alignment.
         */
        final int[] lineOffsets = new int[lineCount];
        switch (textAlign) {
            case PConstants.CENTER: /* 3 */
                for (int i = 0; i < lineCount; ++i) {
                    lineOffsets[i] = (wMax - lineWidths[i]) / 2;
                }

                break;

            case PConstants.RIGHT: /* 39 */
                for (int i = 0; i < lineCount; ++i) {
                    lineOffsets[i] = wMax - lineWidths[i];
                }

                break;

            case PConstants.LEFT: /* 37 */

            default:
        }

        /* wMax may have been left at zero initial value. */
        wMax = wMax < 1 ? 32 : wMax;
        final PImage target = new PImage(wMax, hTotal, PConstants.ARGB, 1);
        target.loadPixels();
        final int[] pxTrg = target.pixels;
        int yCursor = 0;

        /* Loop through lines. */
        for (int i = 0; i < lineCount; ++i) {
            final Glyph[][] glyphLine = glyphs[i];
            final int lineHeight = lineHeights[i];

            /* Reset xCursor every carriage return. */
            int xCursor = lineOffsets[i];

            /* Loop through words. */
            for (final Glyph[] glyphWord : glyphLine) {
                /* Loop through letters. */
                for (final Glyph glyph : glyphWord) {
                    if (glyph != null) {
                        xCursor += glyph.leftExtent;
                        final PImage source = glyph.image;

                        if (source != null) {
                            source.loadPixels();

                            /*
                             * {@link PImage#set(int, int, PImage)} cannot be used
                             * because glyph descenders or ascenders may overlap.
                             */
                            final int wSrc = source.pixelWidth;
                            final int[] pxSrc = source.pixels;
                            final int srcLen = pxSrc.length;
                            final int yStart = yCursor + lineHeight - glyph.topExtent;

                            /*
                             * Loop through source image height and width. Target index
                             * is manually calculated in the inner loop using the
                             * formulae index = x + y * width and x = index % width, y
                             * = index / width.
                             */
                            for (int idxSrc = 0; idxSrc < srcLen; ++idxSrc) {

                                /*
                                 * Shift source image from gray scale, stored in the
                                 * blue channel, to ARGB. Composite target and source,
                                 * then composite in tint color.
                                 */
                                pxTrg[(yStart + idxSrc / wSrc) * wMax + xCursor
                                    + idxSrc % wSrc] |= pxSrc[idxSrc] << 0x18 | vClr;
                            }
                        } /* End of null check for glyph image. */
                        xCursor += glyph.width + vKern;
                    } /* End of null check for glyph. */
                } /* End of letters loop. */
                xCursor += spaceWidth + vKern;
            } /* End of words loop. */
            yCursor += lineHeight;
        } /* End of lines loop. */

        target.updatePixels();
        return target;
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image.<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final Rgb fillClr) {

        return ZImage.fromText(font, text, fillClr.toHexIntSat());
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * is measured in pixels; negative values are not allowed.<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @param leading spacing between lines
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final Rgb fillClr,
        final int leading) {

        return ZImage.fromText(font, text, fillClr.toHexIntSat(), leading);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * and kerning are measured in pixels; negative values are not allowed<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font    the Processing font
     * @param text    the string of text
     * @param fillClr the color
     * @param leading spacing between lines
     * @param kerning spacing between characters
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final Rgb fillClr,
        final int leading,
        final int kerning) {

        return ZImage.fromText(font, text, fillClr.toHexIntSat(),
            leading, kerning);
    }

    /**
     * Blits glyph images from a {@link PFont} onto a single image. The leading
     * and kerning are measured in pixels; negative values are not allowed.
     * The horizontal text alignment may be either center
     * {@link PConstants#CENTER} ( {@value PConstants#CENTER} ), right
     * {@link PConstants#RIGHT} ( {@value PConstants#RIGHT} ) or left
     * {@link PConstants#LEFT} ( {@value PConstants#LEFT} ).<br>
     * <br>
     * Images created with this method do not have a reference to a parent
     * PApplet.
     *
     * @param font      the Processing font
     * @param text      the string of text
     * @param fillClr   the color
     * @param leading   spacing between lines
     * @param kerning   spacing between characters
     * @param textAlign the horizontal alignment
     * @return the new image
     */
    public static PImage fromText(
        final PFont font,
        final String text,
        final Rgb fillClr,
        final int leading,
        final int kerning,
        final int textAlign) {

        return ZImage.fromText(font, text, fillClr.toHexIntSat(), leading,
            kerning, textAlign);
    }

    /**
     * Multiplies the red, green and blue channels of each pixel in a source
     * image by the alpha channel.
     *
     * @param source the source image
     * @param target the target image
     * @return the image
     * @see ZImage#premul(int[], int[])
     */
    public static PImage premul(final PImage source, final PImage target) {

        if (source == target) {
            target.loadPixels();
            ZImage.premul(target.pixels, target.pixels);
            target.format = PConstants.ARGB;
            target.updatePixels();
            return target;
        }

        if (target instanceof PGraphics) {
            System.err.println("Do not use PGraphics with this method.");
            return target;
        }

        source.loadPixels();
        target.loadPixels();
        final int w = source.pixelWidth;
        final int h = source.pixelHeight;
        final int[] pxSrc = source.pixels;
        target.pixels = ZImage.premul(pxSrc, new int[pxSrc.length]);
        target.format = PConstants.ARGB;
        target.pixelDensity = source.pixelDensity;
        target.pixelWidth = w;
        target.pixelHeight = h;
        target.width = source.width;
        target.height = source.height;
        target.updatePixels();

        return target;
    }

    /**
     * Returns a string representation of an image, including its format, width,
     * height and pixel
     * density.
     *
     * @param img the PImage
     * @return the string
     */
    public static String toString(final PImage img) {

        return "{\"format\":"
            + img.format
            + ",\"width\":"
            + img.width
            + ",\"height\":"
            + img.height
            + ",\"pixelDensity\":"
            + img.pixelDensity
            + '}';
    }

    /**
     * Divides the red, green and blue channels of each pixel in a source image
     * by the alpha channel.
     * Reverse pre-multiplication.
     *
     * @param source the source image
     * @param target the target image
     * @return the image
     * @see ZImage#unpremul(int[], int[])
     */
    public static PImage unpremul(final PImage source, final PImage target) {

        if (source == target) {
            target.loadPixels();
            ZImage.unpremul(target.pixels, target.pixels);
            target.format = PConstants.ARGB;
            target.updatePixels();
            return target;
        }

        if (target instanceof PGraphics) {
            System.err.println("Do not use PGraphics with this method.");
            return target;
        }

        source.loadPixels();
        target.loadPixels();
        final int w = source.pixelWidth;
        final int h = source.pixelHeight;
        final int[] pxSrc = source.pixels;
        target.pixels = ZImage.unpremul(pxSrc, new int[pxSrc.length]);
        target.format = PConstants.ARGB;
        target.pixelDensity = source.pixelDensity;
        target.pixelWidth = w;
        target.pixelHeight = h;
        target.width = source.width;
        target.height = source.height;
        target.updatePixels();

        return target;
    }

    /**
     * Blits a source image's pixels onto a target image's pixels, using
     * integer floor modulo to wrap the source image. The source image can be
     * offset horizontally and/or vertically, creating the illusion of
     * parallax.
     *
     * @param source the source image
     * @param dx     horizontal pixel offset
     * @param dy     vertical pixel offset
     * @param target the target image
     * @return the image
     */
    public static PImage wrap(
        final PImage source,
        final int dx, final int dy,
        final PImage target) {

        source.loadPixels();
        target.loadPixels();
        final int wSrc = source.pixelWidth;
        final int hSrc = source.pixelHeight;
        final int wTrg = target.pixelWidth;
        final int trgLen = target.pixels.length;
        for (int i = 0; i < trgLen; ++i) {
            int yMod = (i / wTrg + dy) % hSrc;
            if ((yMod ^ hSrc) < 0 && yMod != 0) {
                yMod += hSrc;
            }

            int xMod = (i % wTrg - dx) % wSrc;
            if ((xMod ^ wSrc) < 0 && xMod != 0) {
                xMod += wSrc;
            }

            target.pixels[i] = source.pixels[xMod + wSrc * yMod];
        }
        target.format = source.format;
        target.updatePixels();

        return target;
    }

    /**
     * Multiplies the red, green and blue channels of each pixel by its alpha
     * channel.
     *
     * @param source the source pixels
     * @param target the target pixels
     * @return the pre-multiplied image
     */
    protected static int[] premul(final int[] source, final int[] target) {

        final int srcLen = source.length;
        if (srcLen == target.length) {
            for (int i = 0; i < srcLen; ++i) {
                final int srcHex = source[i];
                final int ai = srcHex >> 0x18 & 0xff;
                if (ai < 1) {
                    target[i] = 0x00000000;
                } else if (ai < 0xff) {
                    final float af = ai * Utils.ONE_255;
                    int rp = (int) ((srcHex >> 0x10 & 0xff) * af + 0.5f);
                    int gp = (int) ((srcHex >> 0x08 & 0xff) * af + 0.5f);
                    int bp = (int) ((srcHex & 0xff) * af + 0.5f);

                    if (rp > 0xff) {
                        rp = 0xff;
                    }
                    if (gp > 0xff) {
                        gp = 0xff;
                    }
                    if (bp > 0xff) {
                        bp = 0xff;
                    }

                    target[i] = ai << 0x18 | rp << 0x10 | gp << 0x08 | bp;
                } else {
                    target[i] = srcHex;
                }
            }
        }

        return target;
    }

    /**
     * Divides the red, green and blue channels of each pixel in the image by
     * its alpha channel. Reverse pre-multiplication.
     *
     * @param source the source pixels
     * @param target the target pixels
     * @return the unpremultiplied pixels
     */
    protected static int[] unpremul(final int[] source, final int[] target) {

        final int srcLen = source.length;
        if (srcLen == target.length) {
            for (int i = 0; i < srcLen; ++i) {
                final int srcHex = source[i];
                final int ai = srcHex >> 0x18 & 0xff;
                if (ai < 1) {
                    target[i] = 0x00000000;
                } else if (ai < 0xff) {
                    final float af = 255.0f / ai;
                    int ru = (int) ((srcHex >> 0x10 & 0xff) * af + 0.5f);
                    int gu = (int) ((srcHex >> 0x08 & 0xff) * af + 0.5f);
                    int bu = (int) ((srcHex & 0xff) * af + 0.5f);

                    if (ru > 0xff) {
                        ru = 0xff;
                    }
                    if (gu > 0xff) {
                        gu = 0xff;
                    }
                    if (bu > 0xff) {
                        bu = 0xff;
                    }

                    target[i] = ai << 0x18 | ru << 0x10 | gu << 0x08 | bu;
                } else {
                    target[i] = srcHex;
                }
            }
        }

        return target;
    }

    /**
     * Tests this image for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final PImage p = (PImage) obj;
        if (this.format != p.format
            || this.width != p.width
            || this.pixelDensity != p.pixelDensity
            || this.pixelHeight != p.pixelHeight
            || this.pixelWidth != p.pixelWidth
            || this.height != p.height) {
            return false;
        }

        final int[] apx = this.pixels;
        final int[] bpx = p.pixels;
        if (apx == bpx) {
            return true;
        }
        if (apx == null || bpx == null) {
            return false;
        }

        final int len = apx.length;
        if (bpx.length != len) {
            return false;
        }

        for (int i = 0; i < len; ++i) {
            if (apx[i] != bpx[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code for this image.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + this.format;
        result = prime * result + this.pixelDensity;
        result = prime * result + this.pixelHeight;
        result = prime * result + this.pixelWidth;
        result = prime * result + this.height;
        result = prime * result + this.width;
        return prime * result + Arrays.hashCode(this.pixels);
    }

    /**
     * Returns a string representation of an image, including its format,
     * width, height and pixel density.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return ZImage.toString(this);
    }

    /**
     * Gets the parent applet of this PImage.
     *
     * @return the parent
     */
    PApplet getParent() {
        return this.parent;
    }

    /**
     * Sets the parent of this PImage. The parent reference is needed for the
     * save function.
     *
     * @param parent the PApplet
     * @return this image
     */
    PImage setParent(final PApplet parent) {

        if (parent != null) {
            this.parent = parent;
        }
        return this;
    }
}
