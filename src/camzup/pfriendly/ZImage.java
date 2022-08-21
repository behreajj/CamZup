package camzup.pfriendly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import camzup.core.Bounds3;
import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.IUtils;
import camzup.core.Octree;
import camzup.core.Pixels;
import camzup.core.Pixels.MapLuminance;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PFont.Glyph;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * An extension of PImage, specializing in color gradients and blitting
 * text to an image.
 */
public class ZImage extends PImage {

   /**
    * Constructs an image from its dimensions, width and height.
    *
    * @param width  the image width
    * @param height the image height
    */
   public ZImage ( final int width, final int height ) {

      super(width, height);
   }

   /**
    * Constructs an image from the dimensions and format.
    *
    * @param width  the image width
    * @param height the image height
    * @param format the format
    */
   public ZImage ( final int width, final int height, final int format ) {

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
   public ZImage ( final int width, final int height, final int format,
      final int pixelDensity ) {

      super(width, height, format, pixelDensity);
   }

   /**
    * The default constructor.
    */
   protected ZImage ( ) {}

   /**
    * Tests this image for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      final PImage p = ( PImage ) obj;
      if ( this.format != p.format || this.width != p.width || this.pixelDensity
         != p.pixelDensity || this.pixelHeight != p.pixelHeight
         || this.pixelWidth != p.pixelWidth || this.height != p.height ) {
         return false;
      }

      final int[] apx = this.pixels;
      final int[] bpx = p.pixels;
      if ( apx == bpx ) { return true; }
      if ( apx == null || bpx == null ) { return false; }

      final int len = apx.length;
      if ( bpx.length != len ) { return false; }

      for ( int i = 0; i < len; ++i ) {
         if ( apx[i] != bpx[i] ) { return false; }
      }
      return true;
   }

   /**
    * Returns a hash code for this image.
    *
    * @return the hash code
    *
    * @see Arrays#hashCode(int[])
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = 1;
      result = prime * result + this.format;
      result = prime * result + this.pixelDensity;
      result = prime * result + this.pixelHeight;
      result = prime * result + this.pixelWidth;
      result = prime * result + this.height;
      result = prime * result + this.width;
      result = prime * result + Arrays.hashCode(this.pixels);
      return result;
   }

   /**
    * Resizes the image to a requested size in pixels using bicubic
    * interpolation.
    *
    * @param w width
    * @param h height
    */
   @Override
   public void resize ( final int w, final int h ) {

      ZImage.resizeBilinear(this, w, h, this);
   }

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return ZImage.toString(this); }

   /**
    * Update the pixels[] buffer to the PImage. The overridden functionality
    * eliminates unnecessary checks.
    */
   @Override
   public void updatePixels ( ) {

      if ( !this.modified ) {

         this.mx1 = 0;
         this.mx2 = this.pixelWidth;
         this.my1 = 0;
         this.my2 = this.pixelHeight;
         this.modified = true;

      } else {

         if ( 0 < this.mx1 ) { this.mx1 = 0; }
         if ( 0 > this.mx2 ) {
            this.mx2 = this.pixelWidth <= 0 ? this.pixelWidth : 0;
         }

         if ( 0 < this.my1 ) { this.my1 = 0; }
         if ( 0 > this.my2 ) {
            this.my2 = this.pixelHeight <= 0 ? this.pixelHeight : 0;
         }

         if ( this.pixelWidth < this.mx1 ) {
            this.mx1 = 0 >= this.pixelWidth ? 0 : this.pixelWidth;
         }
         if ( this.pixelWidth > this.mx2 ) { this.mx2 = this.pixelWidth; }

         if ( this.pixelHeight < this.my1 ) {
            this.my1 = 0 >= this.pixelHeight ? 0 : this.pixelHeight;
         }
         if ( this.pixelHeight > this.my2 ) { this.my2 = this.pixelHeight; }
      }
   }

   /**
    * Gets the parent applet of this PImage.
    *
    * @return the parent
    */
   PApplet getParent ( ) { return this.parent; }

   /**
    * Sets the parent of this PImage. The parent reference is needed for the
    * save function.
    *
    * @param parent the PApplet
    *
    * @return this image
    */
   PImage setParent ( final PApplet parent ) {

      this.parent = parent;
      return this;
   }

   /**
    * Default color for dark checker squares, {@value ZImage#CHECKER_DARK} or
    * 1.0 / 3.0 of 255.
    */
   public static final int CHECKER_DARK = 0xff555555;

   /**
    * Default color for light checker squares, {@value ZImage#CHECKER_LIGHT}
    * or 2.0 / 3.0 of 255.
    */
   public static final int CHECKER_LIGHT = 0xffaaaaaa;

   /**
    * Default horizontal alignment when creating an image from text:
    * {@link PConstants#LEFT}, {@value PConstants#LEFT}.
    */
   public static final int DEFAULT_ALIGN = PConstants.LEFT;

   /**
    * Default spacing between characters, in pixels, when creating an image
    * from text: {@value ZImage#DEFAULT_KERNING}.
    */
   public static final int DEFAULT_KERNING = 0;

   /**
    * Default spacing between lines, in pixels, when creating an image from
    * text: {@value ZImage#DEFAULT_LEADING}.
    */
   public static final int DEFAULT_LEADING = 8;

   /**
    * Adjusts the contrast of an image by a factor. Uses the CIE LAB color
    * space. The adjustment factor is expected to be in [-1.0, 1.0].
    *
    * @param source the source image
    * @param fac    the contrast factor
    * @param target the target image
    *
    * @return the adjusted image
    *
    * @see Pixels#adjustContrast(int[], float, int[])
    */
   public static PImage adjustContrast ( final PImage source, final float fac,
      final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.adjustContrast(target.pixels, fac, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.adjustContrast(pxSrc, fac, new int[pxSrc.length]);
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
    * Adjusts a source image's colors in CIE LCH. Assigns the results to a
    * target image.
    *
    * @param source the source image
    * @param adjust the adjustment
    * @param target the target image
    *
    * @return the adjusted image
    *
    * @see Pixels#adjustLch(int[], Vec4, int[])
    */
   public static PImage adjustLch ( final PImage source, final Vec4 adjust,
      final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.adjustLch(target.pixels, adjust, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.adjustLch(pxSrc, adjust, new int[pxSrc.length]);
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
    * Convert an image in the {@link PConstants#ALPHA} format to an image in
    * the {@link PConstants#ARGB} format.
    *
    * @param image the image
    *
    * @return the conversion
    */
   public static PImage alphaToArgb ( final PImage image ) {

      /* Depart from (source, target) signature for this function. */
      if ( image.format != PConstants.ALPHA ) { return image; }

      image.loadPixels();
      final int[] px = image.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         final int a = px[i];
         px[i] = a << 0x18 | a << 0x10 | a << 0x08 | a;
      }
      image.format = PConstants.ARGB;
      image.updatePixels();
      return image;
   }

   /**
    * Finds the aspect ratio of an image, its width divided by its height.
    *
    * @param image the image
    *
    * @return the aspect ratio
    */
   public static float aspect ( final PImage image ) {

      return Utils.div(( float ) image.width, ( float ) image.height);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param cols   the column count
    * @param rows   the row count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final Color a, final Color b, final int cols,
      final int rows, final PImage target ) {

      return ZImage.checker(Color.toHexIntSat(a), Color.toHexIntSat(b), cols,
         rows, target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final Color a, final Color b, final int count,
      final PImage target ) {

      return ZImage.checker(a, b, count, count, target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes. Ideally,
    * the image dimensions should be a power of 2 (32, 64, 128, 256, 512). The
    * color is expected to be a 32-bit color integer.
    *
    * @param a      the first color
    * @param b      the second color
    * @param cols   the column count
    * @param rows   the row count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int a, final int b, final int cols,
      final int rows, final PImage target ) {

      // TODO: Shift to Pixels class.

      target.loadPixels();

      final int w = target.pixelWidth;
      final int h = target.pixelHeight;
      final int pd = target.pixelDensity;

      /*
       * Rows and columns should have a maximum bound, because it will be easy
       * to transpose color and columns & rows arguments.
       */
      final int limit = 2 * pd;
      final int vCols = cols < 2 ? 2 : cols > w / limit ? w / limit : cols;
      final int vRows = rows < 2 ? 2 : rows > h / limit ? h / limit : rows;

      /*
       * Alternatively, these could be adjusted according to the image's present
       * pixel format; instead of changing the format.
       */
      final int va = a;
      final int vb = b;

      final int[] px = target.pixels;
      final int len = px.length;

      // QUERY: Do vrows and vcols also need to account for pixel density?
      // This will have to go untested for now.
      final int wch = w / vCols;
      final int hchw = w * h / vRows;

      for ( int i = 0; i < len; ++i ) {
         /* % 2 can be replaced by & 1 for even or odd. */
         px[i] = ( i % w / wch + i / hchw & 1 ) == 0 ? va : vb;
      }

      target.format = PConstants.ARGB;
      target.updatePixels();
      return target;
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes.
    *
    * @param a      the first color
    * @param b      the second color
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int a, final int b, final int count,
      final PImage target ) {

      return ZImage.checker(a, b, count, count, target);
   }

   /**
    * Renders a checker pattern on an image for diagnostic purposes. Uses
    * {@value ZImage#CHECKER_DARK} and {@value ZImage#CHECKER_LIGHT} as
    * default colors.
    *
    * @param count  the count
    * @param target the output image
    *
    * @return the output image
    */
   public static PImage checker ( final int count, final PImage target ) {

      return ZImage.checker(ZImage.CHECKER_DARK, ZImage.CHECKER_LIGHT, count,
         count, target);
   }

   /**
    * Dithers an image with the <a href=
    * "https://www.wikiwand.com/en/Floyd-Steinberg">Floyd-Steinberg</a>
    * method. Builds an octree in CIE L*a*b* from an input palette, then finds
    * the nearest color according to Euclidean distance (see <a href=
    * "https://www.wikiwand.com/en/Color_difference#/CIE76">CIE76</a>).
    *
    * @param target  image
    * @param palette palette
    *
    * @return the image
    */
   public static PImage dither ( final PImage target, final Color[] palette ) {

      final float fs_1_16 = 0.0625f;
      final float fs_3_16 = 0.1875f;
      final float fs_5_16 = 0.3125f;
      final float fs_7_16 = 0.4375f;

      final int octCapacity = 16;
      final float queryRad = 175.0f;

      final Color lrgb = new Color();
      final Vec4 xyz = new Vec4();
      final Vec4 lab = new Vec4();

      final int palLen = palette.length;
      final HashMap < Integer, Integer > ptToHexDict = new HashMap <>(palLen,
         0.75f);

      /* Ensure that at least 8 colors will be returned from the octree. */
      final Octree octree = new Octree(Bounds3.cieLab(new Bounds3()),
         octCapacity);
      octree.subdivide(1, octCapacity);

      for ( int i = 0; i < palLen; ++i ) {
         final Color palEntry = palette[i];
         Color.sRgbaTolRgba(palEntry, false, lrgb);
         Color.lRgbaToXyza(lrgb, xyz);
         Color.xyzaToLaba(xyz, lab);

         final Vec3 point = new Vec3(lab.x, lab.y, lab.z);
         ptToHexDict.put(point.hashCode(), Color.toHexIntSat(palEntry));
         octree.insert(point);
      }

      octree.cull();

      target.loadPixels();
      final int[] px = target.pixels;
      final int w = target.pixelWidth;
      final int h = target.pixelHeight;
      final int pxLen = px.length;

      final Color srgb = new Color();
      final Vec3 query = new Vec3();

      for ( int k = 0; k < pxLen; ++k ) {
         final int srcHex = px[k];
         final int rSrc = srcHex >> 0x10 & 0xff;
         final int gSrc = srcHex >> 0x08 & 0xff;
         final int bSrc = srcHex & 0xff;

         srgb.set(rSrc * IUtils.ONE_255, gSrc * IUtils.ONE_255, bSrc
            * IUtils.ONE_255, 1.0f);
         Color.sRgbaTolRgba(srgb, false, lrgb);
         Color.lRgbaToXyza(lrgb, xyz);
         Color.xyzaToLaba(xyz, lab);
         query.set(lab.x, lab.y, lab.z);

         int trgHex = 0x00000000;
         int rTrg = 0;
         int gTrg = 0;
         int bTrg = 0;

         final Vec3[] nearestPts = octree.queryRange(query, queryRad);
         if ( nearestPts.length > 0 ) {
            final Integer nearestHash = nearestPts[0].hashCode();
            if ( ptToHexDict.containsKey(nearestHash) ) {
               final int nearestHex = ptToHexDict.get(nearestHash);
               rTrg = nearestHex >> 0x10 & 0xff;
               gTrg = nearestHex >> 0x08 & 0xff;
               bTrg = nearestHex & 0xff;
               trgHex = srcHex & 0xff000000 | nearestHex & 0x00ffffff;
            }
         }

         px[k] = trgHex;
         final float rErr = rSrc - rTrg;
         final float gErr = gSrc - gTrg;
         final float bErr = bSrc - bTrg;

         final int x = k % w;
         final int y = k / w;
         final int yp1 = y + 1;
         final int xp1 = x + 1;
         final boolean xp1InBounds = xp1 < w;
         final boolean yp1InBounds = yp1 < h;
         final int yp1w = yp1 * w;

         if ( xp1InBounds ) {
            final int k0 = xp1 + y * w;
            final int neighbor0 = px[k0];

            final int rn0 = neighbor0 >> 0x10 & 0xff;
            final int gn0 = neighbor0 >> 0x08 & 0xff;
            final int bn0 = neighbor0 & 0xff;

            // Add 0.5f to round up to this and 3 other neighbors?
            int rne0 = rn0 + ( int ) ( rErr * fs_7_16 );
            int gne0 = gn0 + ( int ) ( gErr * fs_7_16 );
            int bne0 = bn0 + ( int ) ( bErr * fs_7_16 );

            if ( rne0 < 0 ) {
               rne0 = 0;
            } else if ( rne0 > 255 ) { rne0 = 255; }
            if ( gne0 < 0 ) {
               gne0 = 0;
            } else if ( gne0 > 255 ) { gne0 = 255; }
            if ( bne0 < 0 ) {
               bne0 = 0;
            } else if ( bne0 > 255 ) { bne0 = 255; }

            px[k0] = neighbor0 & 0xff000000 | rne0 << 0x10 | gne0 << 0x08
               | bne0;

            if ( yp1InBounds ) {
               final int k3 = xp1 + yp1w;
               final int neighbor3 = px[k3];

               final int rn3 = neighbor3 >> 0x10 & 0xff;
               final int gn3 = neighbor3 >> 0x08 & 0xff;
               final int bn3 = neighbor3 & 0xff;

               int rne3 = rn3 + ( int ) ( rErr * fs_1_16 );
               int gne3 = gn3 + ( int ) ( gErr * fs_1_16 );
               int bne3 = bn3 + ( int ) ( bErr * fs_1_16 );

               if ( rne3 < 0 ) {
                  rne3 = 0;
               } else if ( rne3 > 255 ) { rne3 = 255; }
               if ( gne3 < 0 ) {
                  gne3 = 0;
               } else if ( gne3 > 255 ) { gne3 = 255; }
               if ( bne3 < 0 ) {
                  bne3 = 0;
               } else if ( bne3 > 255 ) { bne3 = 255; }

               px[k3] = neighbor3 & 0xff000000 | rne3 << 0x10 | gne3 << 0x08
                  | bne3;
            }
         }

         if ( yp1InBounds ) {
            final int k2 = x + yp1w;
            final int neighbor2 = px[k2];

            final int rn2 = neighbor2 >> 0x10 & 0xff;
            final int gn2 = neighbor2 >> 0x08 & 0xff;
            final int bn2 = neighbor2 & 0xff;

            int rne2 = rn2 + ( int ) ( rErr * fs_5_16 );
            int gne2 = gn2 + ( int ) ( gErr * fs_5_16 );
            int bne2 = bn2 + ( int ) ( bErr * fs_5_16 );

            if ( rne2 < 0 ) {
               rne2 = 0;
            } else if ( rne2 > 255 ) { rne2 = 255; }
            if ( gne2 < 0 ) {
               gne2 = 0;
            } else if ( gne2 > 255 ) { gne2 = 255; }
            if ( bne2 < 0 ) {
               bne2 = 0;
            } else if ( bne2 > 255 ) { bne2 = 255; }

            px[k2] = neighbor2 & 0xff000000 | rne2 << 0x10 | gne2 << 0x08
               | bne2;

            if ( x > 0 ) {
               final int k1 = x - 1 + yp1w;
               final int neighbor1 = px[k1];

               final int rn1 = neighbor1 >> 0x10 & 0xff;
               final int gn1 = neighbor1 >> 0x08 & 0xff;
               final int bn1 = neighbor1 & 0xff;

               int rne1 = rn1 + ( int ) ( rErr * fs_3_16 );
               int gne1 = gn1 + ( int ) ( gErr * fs_3_16 );
               int bne1 = bn1 + ( int ) ( bErr * fs_3_16 );

               if ( rne1 < 0 ) {
                  rne1 = 0;
               } else if ( rne1 > 255 ) { rne1 = 255; }
               if ( gne1 < 0 ) {
                  gne1 = 0;
               } else if ( gne1 > 255 ) { gne1 = 255; }
               if ( bne1 < 0 ) {
                  bne1 = 0;
               } else if ( bne1 > 255 ) { bne1 = 255; }

               px[k1] = neighbor1 & 0xff000000 | rne1 << 0x10 | gne1 << 0x08
                  | bne1;
            }
         }
      }

      target.format = PConstants.ARGB;
      target.updatePixels();
      return target;
   }

   /**
    * Extracts a palette from an image with an octree in CIE LAB. The size of
    * the palette depends on the capacity of each node in the octree. Does not
    * retain alpha component of image pixels. Colors produced may not be in
    * gamut.
    *
    * @param source   the source image
    * @param capacity the octree capacity
    *
    * @return the color array
    */
   public static Color[] extractPalette ( final PImage source,
      final int capacity ) {

      return ZImage.extractPalette(source, capacity, 256);
   }

   /**
    * Extracts a palette from an image with an octree in CIE LAB. The size of
    * the palette depends on the capacity of each node in the octree. Does not
    * retain alpha component of image pixels. The threshold describes the
    * minimum number of unique colors in the image beneath which it is
    * preferable to not engage the octree. Once the octree has been used,
    * colors produced may not be in gamut.
    *
    * @param source    the source image
    * @param capacity  the octree capacity
    * @param threshold the minimum threshold
    *
    * @return the color array
    */
   public static Color[] extractPalette ( final PImage source,
      final int capacity, final int threshold ) {

      source.loadPixels();
      return Pixels.extractPalette(source.pixels, capacity, threshold);
   }

   /**
    * Fills an image in place with a color.
    *
    * @param target the target image
    * @param c      the fill color
    *
    * @return the image
    *
    * @see Color#toHexIntSat(Color)
    */
   public static PImage fill ( final Color c, final PImage target ) {

      return ZImage.fill(Color.toHexIntSat(c), target);
   }

   /**
    * Fills an image with a color in place. The color is expected to be a
    * 32-bit color integer.
    *
    * @param c      the fill color
    * @param target the target image
    *
    * @return the image
    *
    * @see Pixels#fill(int, int[])
    */
   public static PImage fill ( final int c, final PImage target ) {

      target.loadPixels();
      Pixels.fill(c, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Flips an image horizontally, on the x axis.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the flipped image
    */
   public static PImage flipX ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.flipX(target.pixels, target.pixelWidth, target.pixelHeight,
            target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.flipX(pxSrc, w, h, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Flips an image horizontally, on the y axis.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the flipped image
    */
   public static PImage flipY ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.flipY(target.pixels, target.pixelWidth, target.pixelHeight,
            target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.flipY(pxSrc, w, h, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet. Defaults to the fill color white.
    *
    * @param font the Processing font
    * @param text the string of text
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text ) {

      return ZImage.fromText(font, text, 0xffffffff, ZImage.DEFAULT_LEADING,
         ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr ) {

      return ZImage.fromText(font, text, Color.toHexIntSat(fillClr));
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and is measured in pixels; negative values are not allowed.<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading ) {

      return ZImage.fromText(font, text, Color.toHexIntSat(fillClr), leading);
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
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, Color.toHexIntSat(fillClr), leading,
         kerning);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed. The
    * horizontal text alignment may be either center {@link PConstants#CENTER}
    * ( {@value PConstants#CENTER} ), right {@link PConstants#RIGHT} (
    * {@value PConstants#RIGHT} ) or left {@link PConstants#LEFT} (
    * {@value PConstants#LEFT} ).<br>
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
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color fillClr, final int leading, final int kerning,
      final int textAlign ) {

      return ZImage.fromText(font, text, Color.toHexIntSat(fillClr), leading,
         kerning, textAlign);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr ) {

      return ZImage.fromText(font, text, fillClr, ZImage.DEFAULT_LEADING,
         ZImage.DEFAULT_KERNING, ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and is measured in pixels; negative values are not allowed.<br>
    * <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font    the Processing font
    * @param text    the string of text
    * @param fillClr the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading ) {

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
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, fillClr, leading, kerning,
         ZImage.DEFAULT_ALIGN);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. The leading
    * and kerning are measured in pixels; negative values are not allowed. The
    * horizontal text alignment may be either center {@link PConstants#CENTER}
    * ( {@value PConstants#CENTER} ), right {@link PConstants#RIGHT} (
    * {@value PConstants#RIGHT} ) or left {@link PConstants#LEFT} (
    * {@value PConstants#LEFT} ).<br>
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
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int fillClr, final int leading, final int kerning,
      final int textAlign ) {

      /*
       * Validate inputs: colors with no alpha not allowed; negative leading and
       * kerning not allowed; try to guard against empty Strings. Remove alpha
       * from tint; source image alpha will be used.
       */
      final String vTxt = text.trim();
      if ( vTxt.isEmpty() ) { return new PImage(32, 32, PConstants.ARGB, 1); }
      final int vLead = leading < 0 ? 1 : leading + 1;
      final int vKern = kerning < 0 ? 0 : kerning;
      final int vClr = ( fillClr >> 0x18 & 0xff ) != 0 ? 0x00ffffff & fillClr
         : 0x00ffffff;

      /* Carriage returns, or line breaks, have 3 variants: \r, \n, or \r\n . */
      final Pattern patternLnBr = Pattern.compile("[\n|\r]+");
      final String[] linesSplit = patternLnBr.split(vTxt, 0);
      final int lineCount = linesSplit.length;

      /* 3D array: lines contain words which contain letters. */
      final char[][][] characters = new char[lineCount][][];
      final Pattern patternSpace = Pattern.compile("\\s+");
      for ( int i = 0; i < lineCount; ++i ) {
         final String[] words = patternSpace.split(linesSplit[i], 0);
         final int charCount = words.length;
         final char[][] charLine = characters[i] = new char[charCount][];
         for ( int j = 0; j < charCount; ++j ) {
            charLine[j] = words[j].toCharArray();
         }
      }

      final int fontSize = font.getSize();

      /* Determine width of a space. */
      final Glyph whiteSpace = font.getGlyph('-');
      final int spaceWidth = whiteSpace != null ? whiteSpace.width
         : ( int ) ( fontSize * IUtils.ONE_THIRD );

      /*
       * If a line contains only a space, then maxHeight below may wind up as
       * zero, and need to be replaced with a blank line.
       */
      final Glyph emptyLine = font.getGlyph('E');
      final int defaultHeight = emptyLine != null ? emptyLine.height : fontSize;

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
      for ( int i = 0; i < lineCount; ++i ) {
         final char[][] charLine = characters[i];
         final int wordCount = charLine.length;
         final Glyph[][] glyphLine = new Glyph[wordCount][];
         glyphs[i] = glyphLine;

         int sumWidths = 0;
         int maxHeight = 0;
         int maxDescent = 0;

         /* Loop through words. */
         for ( int j = 0; j < wordCount; ++j ) {
            final char[] charWord = charLine[j];
            final int charCount = charWord.length;
            final Glyph[] glyphWord = new Glyph[charCount];
            glyphLine[j] = glyphWord;

            /* Loop through letters. */
            for ( int k = 0; k < charCount; ++k ) {
               final char character = charWord[k];
               final Glyph glyph = font.getGlyph(character);
               glyphWord[k] = glyph;

               if ( glyph != null ) {

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
                  if ( height > maxHeight ) { maxHeight = height; }
                  sumWidths += glyph.width + vKern + glyph.leftExtent;
                  if ( glyphDescent > maxDescent ) {
                     maxDescent = glyphDescent;
                  }
                  lastRowPadding = maxDescent;
               }
            }

            /* Add a space between words. */
            sumWidths += spaceWidth + vKern;
         }

         /* maxHeight may be initial value. */
         if ( maxHeight < 1 ) { maxHeight = defaultHeight; }
         lineHeights[i] = maxHeight;
         hTotal += maxHeight;

         lineWidths[i] = sumWidths;
         if ( sumWidths > wMax ) { wMax = sumWidths; }
      }

      hTotal += lastRowPadding;

      /*
       * Offset the xCursor's initial position depending on horizontal
       * alignment.
       */
      final int[] lineOffsets = new int[lineCount];
      switch ( textAlign ) {

         case PConstants.CENTER: /* 3 */

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = ( wMax - lineWidths[i] ) / 2;
            }

            break;

         case PConstants.RIGHT: /* 39 */

            for ( int i = 0; i < lineCount; ++i ) {
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
      for ( int i = 0; i < lineCount; ++i ) {
         final Glyph[][] glyphLine = glyphs[i];
         final int wordCount = glyphLine.length;
         final int lineHeight = lineHeights[i];

         /* Reset xCursor every carriage return. */
         int xCursor = lineOffsets[i];

         /* Loop through words. */
         for ( int j = 0; j < wordCount; ++j ) {
            final Glyph[] glyphWord = glyphLine[j];
            final int charCount = glyphWord.length;

            /* Loop through letters. */
            for ( int k = 0; k < charCount; ++k ) {
               final Glyph glyph = glyphWord[k];

               if ( glyph != null ) {
                  xCursor += glyph.leftExtent;
                  final PImage source = glyph.image;

                  if ( source != null ) {
                     // target.loadPixels();
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
                     for ( int idxSrc = 0; idxSrc < srcLen; ++idxSrc ) {

                        /*
                         * Shift source image from gray scale, stored in the
                         * blue channel, to ARGB. Composite target and source,
                         * then composite in tint color.
                         */
                        pxTrg[ ( yStart + idxSrc / wSrc ) * wMax + xCursor
                           + idxSrc % wSrc] |= pxSrc[idxSrc] << 0x18 | vClr;
                     }

                     // target.updatePixels(xCursor, yCursor, wSrc, hSrc);
                  }
                  /* End of null check for glyph image. */
                  xCursor += glyph.width + vKern;
               }
               /* End of null check for glyph. */
            }
            /* End of letters loop. */
            xCursor += spaceWidth + vKern;
         }
         /* End of words loop. */
         yCursor += lineHeight;
      }
      /* End of lines loop. */

      target.updatePixels();
      return target;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param grd    the gradient
    * @param easing the easing function
    * @param target the target image
    *
    * @return the conic gradient
    */
   public static PImage gradientConic ( final Gradient grd,
      final Color.AbstrEasing easing, final PImage target ) {

      return ZImage.gradientConic(grd, 0.0f, 0.0f, IUtils.HALF_PI, easing,
         target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param xOrig   the origin x coordinate
    * @param yOrig   the origin y coordinate
    * @param radians the angle in radians
    * @param easing  the easing function
    * @param target  the target image
    *
    * @return the conic gradient
    *
    * @see Pixels#gradientConic(Gradient, float, float, float,
    *      Color.AbstrEasing, int, int, int[])
    */
   public static PImage gradientConic ( final Gradient grd, final float xOrig,
      final float yOrig, final float radians, final Color.AbstrEasing easing,
      final PImage target ) {

      target.loadPixels();
      Pixels.gradientConic(grd, xOrig, yOrig, radians, easing,
         target.pixelWidth, target.pixelHeight, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param xOrig   the origin x coordinate
    * @param yOrig   the origin y coordinate
    * @param radians the angle in radians
    * @param target  the target image
    *
    * @return the conic gradient
    */
   public static PImage gradientConic ( final Gradient grd, final float xOrig,
      final float yOrig, final float radians, final PImage target ) {

      return ZImage.gradientConic(grd, xOrig, yOrig, radians,
         new Color.MixSrgba(), target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the conic gradient
    */
   public static PImage gradientConic ( final Gradient grd,
      final PImage target ) {

      return ZImage.gradientConic(grd, 0.0f, 0.0f, IUtils.HALF_PI, target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param orig    the origin
    * @param radians the angle in radians
    * @param easing  the easing function
    * @param target  the target image
    *
    * @return the conic gradient
    *
    * @see Pixels#gradientConic(Gradient, float, float, float,
    *      Color.AbstrEasing, int, int, int[])
    */
   public static PImage gradientConic ( final Gradient grd, final Vec2 orig,
      final float radians, final Color.AbstrEasing easing,
      final PImage target ) {

      return ZImage.gradientConic(grd, orig.x, orig.y, radians, easing, target);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
    *
    * @param grd     the gradient
    * @param orig    the origin
    * @param radians the angle in radians
    * @param target  the target image
    *
    * @return the conic gradient
    */
   public static PImage gradientConic ( final Gradient grd, final Vec2 orig,
      final float radians, final PImage target ) {

      return ZImage.gradientConic(grd, orig.x, orig.y, radians, target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param easing the easing function
    * @param target the target image
    *
    * @return the linear gradient
    */
   public static PImage gradientLinear ( final Gradient grd,
      final Color.AbstrEasing easing, final PImage target ) {

      return ZImage.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, easing,
         target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param xDest  the destination x coordinate
    * @param yDest  the destination y coordinate@param easing
    * @param easing the easing function
    * @param target the target image
    *
    * @return the linear gradient
    *
    * @see Pixels#gradientLinear(Gradient, float, float, float, float,
    *      Color.AbstrEasing, int, int, int[])
    */
   public static PImage gradientLinear ( final Gradient grd, final float xOrig,
      final float yOrig, final float xDest, final float yDest,
      final Color.AbstrEasing easing, final PImage target ) {

      target.loadPixels();
      Pixels.gradientLinear(grd, xOrig, yOrig, xDest, yDest, easing,
         target.pixelWidth, target.pixelHeight, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param xDest  the destination x coordinate
    * @param yDest  the destination y coordinate@param easing
    * @param target the target image
    *
    * @return the linear gradient
    */
   public static PImage gradientLinear ( final Gradient grd, final float xOrig,
      final float yOrig, final float xDest, final float yDest,
      final PImage target ) {

      return ZImage.gradientLinear(grd, xOrig, yOrig, xDest, yDest,
         new Color.MixSrgba(), target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the linear gradient
    */
   public static PImage gradientLinear ( final Gradient grd,
      final PImage target ) {

      return ZImage.gradientLinear(grd, -1.0f, 0.0f, 1.0f, 0.0f, target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param dest   the destination
    * @param easing the easing function
    * @param target the target image
    *
    * @return the linear gradient
    */
   public static PImage gradientLinear ( final Gradient grd, final Vec2 orig,
      final Vec2 dest, final Color.AbstrEasing easing, final PImage target ) {

      return ZImage.gradientLinear(grd, orig.x, orig.y, dest.x, dest.y, easing,
         target);
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param dest   the destination
    * @param target the target image
    *
    * @return the linear gradient
    */
   public static PImage gradientLinear ( final Gradient grd, final Vec2 orig,
      final Vec2 dest, final PImage target ) {

      return ZImage.gradientLinear(grd, orig.x, orig.y, dest.x, dest.y, target);
   }

   /**
    * Maps the colors of a source image to a gradient using a mapping
    * function. The mapping function accepts a pixel as an argument and
    * returns a factor to be given to a gradient evaluation method.
    *
    * @param source the source pixels
    * @param grd    the gradient
    * @param easing the easing function
    * @param map    the mapping function
    * @param target the target pixels
    *
    * @return the mapped pixels
    */
   public static PImage gradientMap ( final PImage source, final Gradient grd,
      final Color.AbstrEasing easing, final IntFunction < Float > map,
      final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.gradientMap(target.pixels, grd, easing, map, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.gradientMap(pxSrc, grd, easing, map,
         new int[pxSrc.length]);
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
    * Maps the colors of a source image to a gradient using a mapping
    * function. Defaults to using the source image's perceptual luminance as
    * an input factor to the gradient evaluation.
    *
    * @param source the source pixels
    * @param grd    the gradient
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the mapped pixels
    */
   public static PImage gradientMap ( final PImage source, final Gradient grd,
      final Color.AbstrEasing easing, final PImage target ) {

      return ZImage.gradientMap(source, grd, easing, new MapLuminance(),
         target);
   }

   /**
    * Maps the colors of a source image to a gradient using a mapping
    * function. Defaults to using the source image's perceptual luminance as
    * an input factor to the gradient evaluation.
    *
    * @param source the source pixels
    * @param grd    the gradient
    * @param target the target pixels
    *
    * @return the mapped pixels
    */
   public static PImage gradientMap ( final PImage source, final Gradient grd,
      final PImage target ) {

      return ZImage.gradientMap(source, grd, new Color.MixSrgba(), target);
   }

   /**
    * Generates a radial gradient from an origin point.
    *
    * @param grd    the gradient
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the radial gradient
    */
   public static PImage gradientRadial ( final Gradient grd,
      final Color.AbstrEasing easing, final PImage target ) {

      return ZImage.gradientRadial(grd, 0.0f, 0.0f, 1.0f, easing, target);
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param radius the radius
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the radial gradient
    *
    * @see Pixels#gradientRadial(Gradient, float, float, float,
    *      Color.AbstrEasing, int, int, int[])
    */
   public static PImage gradientRadial ( final Gradient grd, final float xOrig,
      final float yOrig, final float radius, final Color.AbstrEasing easing,
      final PImage target ) {

      target.loadPixels();
      Pixels.gradientRadial(grd, xOrig, yOrig, radius, easing,
         target.pixelWidth, target.pixelHeight, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param xOrig  the origin x coordinate
    * @param yOrig  the origin y coordinate
    * @param radius the radius
    * @param target the target pixels
    *
    * @return the radial gradient
    */
   public static PImage gradientRadial ( final Gradient grd, final float xOrig,
      final float yOrig, final float radius, final PImage target ) {

      return ZImage.gradientRadial(grd, xOrig, yOrig, radius,
         new Color.MixSrgba(), target);
   }

   /**
    * Generates a radial gradient from an origin point.
    *
    * @param grd    the gradient
    * @param target the target pixels
    *
    * @return the radial gradient
    */
   public static PImage gradientRadial ( final Gradient grd,
      final PImage target ) {

      return ZImage.gradientRadial(grd, 0.0f, 0.0f, 1.0f, target);
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param radius the radius
    * @param easing the easing function
    * @param target the target pixels
    *
    * @return the radial gradient
    */
   public static PImage gradientRadial ( final Gradient grd, final Vec2 orig,
      final float radius, final Color.AbstrEasing easing,
      final PImage target ) {

      return ZImage.gradientRadial(grd, orig.x, orig.y, radius, easing, target);
   }

   /**
    * Generates a radial gradient from an origin point. The origin should be
    * in the range [-1.0, 1.0]. Does not account for aspect ratio, so an image
    * that isn't 1:1 will result in an ellipsoid.
    *
    * @param grd    the gradient
    * @param orig   the origin
    * @param radius the radius
    * @param target the target pixels
    *
    * @return the radial gradient
    */
   public static PImage gradientRadial ( final Gradient grd, final Vec2 orig,
      final float radius, final PImage target ) {

      return ZImage.gradientRadial(grd, orig.x, orig.y, radius, target);
   }

   /**
    * Converts a color image from color to gray.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the gray image
    *
    * @see Pixels#grayscale(int[], int[])
    */
   public static PImage grayscale ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.grayscale(target.pixels, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.grayscale(pxSrc, new int[pxSrc.length]);
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
    * Inverts the colors of an image.
    *
    * @param source the source image
    * @param l      invert l flag
    * @param a      invert a flag
    * @param b      invert b flag
    * @param alpha  invert alpha flag
    * @param target the target image
    *
    * @return the inverted image
    */
   public static PImage invert ( final PImage source, final boolean l,
      final boolean a, final boolean b, final boolean alpha,
      final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.invertLab(target.pixels, l, a, b, alpha, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.invertLab(pxSrc, l, a, b, alpha,
         new int[pxSrc.length]);
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
    * Inverts the colors of an image. Excludes alpha from the inversion.
    *
    * @param source the source image
    * @param l      invert l flag
    * @param a      invert a flag
    * @param b      invert b flag
    * @param target the target image
    *
    * @return the inverted image
    */
   public static PImage invert ( final PImage source, final boolean l,
      final boolean a, final boolean b, final PImage target ) {

      return ZImage.invert(source, l, a, b, false, target);
   }

   /**
    * Inverts the colors of an image. Excludes alpha from the inversion.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the inverted image
    */
   public static PImage invert ( final PImage source, final PImage target ) {

      return ZImage.invert(source, true, true, true, target);
   }

   /**
    * Converts an image from linear RGB to
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB).
    *
    * @param source      the source image
    * @param adjustAlpha include the alpha channel in the adjustment
    * @param target      the target image
    *
    * @return the linear image
    *
    * @see Pixels#lRgbaTosRgba(int, boolean)
    */
   public static PImage lRgbaTosRgba ( final PImage source,
      final boolean adjustAlpha, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.lRgbaTosRgba(target.pixels, adjustAlpha, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.lRgbaTosRgba(pxSrc, adjustAlpha,
         new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Converts an image from linear RGB to
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB).
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the linear image
    *
    * @see Pixels#lRgbaTosRgba(int, boolean)
    */
   public static PImage lRgbaTosRgba ( final PImage source,
      final PImage target ) {

      return ZImage.lRgbaTosRgba(source, false, target);
   }

   /**
    * Masks the pixels of an under image with the alpha channel of the over
    * image. Offsets the over image by pixel coordinates relative to the top
    * left corner. The target image is set to the size of the under image.
    *
    * @param under  the under image
    * @param over   the over image
    * @param x      the mask horizontal offset
    * @param y      the mask vertical offset
    * @param target the target image
    *
    * @return the masked image
    */
   public static PImage mask ( final PImage under, final PImage over,
      final int x, final int y, final PImage target ) {

      // TODO: For efficiency, wouldn't the mask image be the size of
      // the intersection between under an over image?

      if ( target instanceof PGraphics ) {
         System.err.println("Don't use PGraphics as a target of this method.");
         return target;
      }

      under.loadPixels();
      over.loadPixels();

      final int xShift = x;
      final int yShift = y;

      final int[] pxUnder = under.pixels;
      final int pxLenUnder = pxUnder.length;
      final int wUnder = under.pixelWidth;
      final int hUnder = under.pixelHeight;

      final int[] pxOver = over.pixels;
      // final int pxLenOver = pxOver.length;
      final int wOver = over.pixelWidth;
      final int hOver = over.pixelHeight;

      final int[] pxTarget = new int[pxLenUnder];
      for ( int i = 0; i < pxLenUnder; ++i ) {
         final int yUnder = i / wUnder;
         final int yOver = yUnder - yShift;

         final int xUnder = i % wUnder;
         final int xOver = xUnder - xShift;

         if ( yOver > -1 && yOver < hOver && xOver > -1 && xOver < wOver ) {
            final int hexOver = pxOver[yOver * wOver + xOver];
            final int hexUnder = pxUnder[i];

            pxTarget[i] = hexOver & 0xff000000 | hexUnder & 0x00ffffff;
         }
      }

      target.pixels = pxTarget;
      target.width = under.width;
      target.height = under.height;
      target.pixelDensity = under.pixelDensity;
      target.pixelWidth = wUnder;
      target.pixelHeight = hUnder;
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Masks the pixels of an under image with the alpha channel of the over
    * image. The target image is set to the size of the under image.
    *
    * @param under  the under image
    * @param over   the over image
    * @param target the target image
    *
    * @return the masked image
    */
   public static PImage mask ( final PImage under, final PImage over,
      final PImage target ) {

      return ZImage.mask(under, over, 0, 0, target);
   }

   /**
    * Mirrors an image across the axis described by an origin and destination.
    * Coordinates are expected to be in the range [0.0, 1.0]. Out-of-bounds
    * pixels are omitted from the mirror.
    *
    * @param source  the source image
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param xDest   the destination x
    * @param yDest   the destination y
    * @param flip    the flip reflection flag
    * @param target  the output image
    *
    * @return the mirrored image
    *
    * @see Pixels#mirror(int[], int, int, float, float, float, float, boolean,
    *      int[])
    */
   public static PImage mirror ( final PImage source, final float xOrigin,
      final float yOrigin, final float xDest, final float yDest,
      final boolean flip, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.mirror(target.pixels, target.pixelWidth, target.pixelHeight,
            xOrigin, yOrigin, xDest, yDest, flip, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.mirror(pxSrc, w, h, xOrigin, yOrigin, xDest, yDest,
         flip, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Mirrors an image across the axis described by an origin and destination.
    * Coordinates are expected to be in the range [0.0, 1.0]. Out-of-bounds
    * pixels are omitted from the mirror.
    *
    * @param source  the source image
    * @param xOrigin the origin x
    * @param yOrigin the origin y
    * @param xDest   the destination x
    * @param yDest   the destination y
    * @param target  the output image
    *
    * @return the mirrored image
    */
   public static PImage mirror ( final PImage source, final float xOrigin,
      final float yOrigin, final float xDest, final float yDest,
      final PImage target ) {

      return ZImage.mirror(source, xOrigin, yOrigin, xDest, yDest, false,
         target);
   }

   /**
    * Mirrors an image across the axis described by an origin and destination.
    * Coordinates are expected to be in the range [0.0, 1.0]. Out-of-bounds
    * pixels are omitted from the mirror.
    *
    * @param source the source image
    * @param origin the origin point
    * @param dest   the destination point
    * @param flip   the flip reflection flag
    * @param target the output image
    *
    * @return the mirrored image
    */
   public static PImage mirror ( final PImage source, final Vec2 origin,
      final Vec2 dest, final boolean flip, final PImage target ) {

      return ZImage.mirror(source, origin.x, origin.y, dest.x, dest.y, flip,
         target);
   }

   /**
    * Mirrors an image across the axis described by an origin and destination.
    * Coordinates are expected to be in the range [0.0, 1.0]. Out-of-bounds
    * pixels are omitted from the mirror.
    *
    * @param source the source image
    * @param origin the origin point
    * @param dest   the destination point
    * @param target the output image
    *
    * @return the mirrored image
    */
   public static PImage mirror ( final PImage source, final Vec2 origin,
      final Vec2 dest, final PImage target ) {

      return ZImage.mirror(source, origin.x, origin.y, dest.x, dest.y, target);
   }

   /**
    * Mirrors, or reflects, an image on the x axis across a pivot. The pivot
    * is expected to be in [0.0, 1.0].
    *
    * @param source the source image
    * @param x      the x pivot
    * @param flip   the flip reflection flag
    * @param target the target image
    *
    * @return the mirrored image
    *
    * @see Utils#round(float)
    * @see Pixels#mirrorX(int[], int, int, boolean, int[])
    */
   public static PImage mirrorX ( final PImage source, final float x,
      final boolean flip, final PImage target ) {

      final int xPx = Utils.round(x * ( source.pixelWidth + 1 ) - 0.5f);

      if ( source == target ) {
         target.loadPixels();
         Pixels.mirrorX(target.pixels, target.pixelWidth, xPx, flip,
            target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.mirrorX(pxSrc, w, xPx, flip,
         new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Mirrors, or reflects, an image on the y axis across a pivot. The pivot
    * is expected to be in [0.0, 1.0].
    *
    * @param source the source image
    * @param y      the y pivot
    * @param flip   the flip reflection flag
    * @param target the target image
    *
    * @return the mirrored image
    *
    * @see Utils#round(float)
    * @see Pixels#mirrorY(int[], int, int, int, boolean, int[])
    */
   public static PImage mirrorY ( final PImage source, final float y,
      final boolean flip, final PImage target ) {

      final int yPx = Utils.round( ( 1.0f - y ) * ( source.pixelHeight + 1 )
         - 0.5f);

      if ( source == target ) {
         target.loadPixels();
         Pixels.mirrorY(target.pixels, target.pixelWidth, target.pixelHeight,
            yPx, flip, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.mirrorY(pxSrc, w, h, yPx, flip,
         new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Multiplies the red, green and blue channels of each pixel in a source
    * image by the alpha channel.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the image
    *
    * @see Pixels#premul(int[], int[])
    */
   public static PImage premul ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.premul(target.pixels, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.premul(pxSrc, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Resizes an image to a target width and height. The arguments are scaled
    * by the image's pixel density within the method. Takes the absolute value
    * of the arguments; does not flip an image based on sign.
    *
    * @param source the source image
    * @param wTrg   the target width
    * @param hTrg   the target height
    * @param target the target image
    *
    * @return the resized image
    */
   public static PImage resize ( final PImage source, final int wTrg,
      final int hTrg, final PImage target ) {

      return ZImage.resizeBilinear(source, wTrg, hTrg, target);
   }

   /**
    * Resizes an image to a target width and height. The arguments are scaled
    * by the image's pixel density within the method. Takes the absolute value
    * of the arguments; does not flip an image based on sign.
    *
    * @param source the source image
    * @param wTrg   the target width
    * @param hTrg   the target height
    * @param target the target image
    *
    * @return the resized image
    *
    * @see Pixels#resizeBilinear(int[], int, int, int, int)
    */
   public static PImage resizeBilinear ( final PImage source, final int wTrg,
      final int hTrg, final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      final int wvTrg = wTrg < 0 ? -wTrg < 1 ? 1 : -wTrg : wTrg < 1 ? 1 : wTrg;
      final int hvTrg = hTrg < 0 ? -hTrg < 1 ? 1 : -hTrg : hTrg < 1 ? 1 : hTrg;

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int pd = source.pixelDensity;
      final int[] pxSrc = source.pixels;
      final int wpd = wvTrg * pd;
      final int hpd = hvTrg * pd;
      target.pixels = Pixels.resizeBilinear(pxSrc, w, h, wpd, hpd);
      target.format = source.format;
      target.pixelDensity = pd;
      target.pixelWidth = wpd;
      target.pixelHeight = hpd;
      target.width = wvTrg;
      target.height = hvTrg;
      target.updatePixels();

      return target;
   }

   /**
    * Generates a diagnostic image where a pixel's location on the x-axis
    * correlates to the color red; on the y-axis, to green.
    *
    * @param target the output image
    *
    * @return the image
    *
    * @see Pixels#rgb(int, int, int, int[])
    */
   public static PImage rgb ( final PImage target ) {

      target.loadPixels();
      Pixels.rgb(target.pixelWidth, target.pixelHeight, 0x80, target.pixels);
      target.format = PConstants.ARGB;
      target.updatePixels();

      return target;
   }

   /**
    * Rotates a source image around its center by an angle in radians.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the rotated image
    */
   public static PImage rotate ( final PImage source, final float angle,
      final PImage target ) {

      return ZImage.rotateBilinear(source, angle, target);
   }

   /**
    * Rotates an image by 180 degrees.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the rotated image
    *
    * @see Pixels#rotate180(int[], int[])
    */
   public static PImage rotate180 ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.rotate180(target.pixels, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.rotate180(pxSrc, new int[pxSrc.length]);
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
    * Rotates an image by 270 degrees counter-clockwise (or 90 degrees
    * clockwise).
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the rotated image
    *
    * @see Pixels#rotate270(int[], int, int, int[])
    */
   public static PImage rotate270 ( final PImage source, final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.rotate270(pxSrc, w, h, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Rotates an image by 90 degrees counter-clockwise.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the rotated image
    *
    * @see Pixels#rotate90(int[], int, int, int[])
    */
   public static PImage rotate90 ( final PImage source, final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.rotate90(pxSrc, w, h, new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Rotates a source image around its center by an angle in radians.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the rotated image
    *
    * @see Pixels#rotateBilinear(int[], int, int, float, Vec2)
    */
   public static PImage rotateBilinear ( final PImage source, final float angle,
      final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int pd = source.pixelDensity;
      final int[] pxSrc = source.pixels;
      final Vec2 dim = new Vec2();
      target.pixels = Pixels.rotateBilinear(pxSrc, w, h, angle, dim);
      target.format = source.format;
      target.pixelDensity = pd;
      target.pixelWidth = ( int ) dim.x;
      target.pixelHeight = ( int ) dim.y;
      target.width = target.pixelWidth / pd;
      target.height = target.pixelHeight / pd;
      target.updatePixels();

      return target;
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param wPrc   the width percentage
    * @param hPrc   the height percentage
    * @param target the target image
    *
    * @return the scaled image
    */
   public static PImage scale ( final PImage source, final float wPrc,
      final float hPrc, final PImage target ) {

      return ZImage.scaleBilinear(source, wPrc, hPrc, target);
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param prc    the width percentage
    * @param target the target image
    *
    * @return the scaled image
    */
   public static PImage scale ( final PImage source, final Vec2 prc,
      final PImage target ) {

      return ZImage.scaleBilinear(source, prc, target);
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param wPrc   the width percentage
    * @param hPrc   the height percentage
    * @param target the target image
    *
    * @return the scaled image
    *
    * @see Utils#round(float)
    */
   public static PImage scaleBilinear ( final PImage source, final float wPrc,
      final float hPrc, final PImage target ) {

      return ZImage.resizeBilinear(source, Utils.round(wPrc * source.width),
         Utils.round(hPrc * source.height), target);
   }

   /**
    * Scales an image by a percentage of its original width and height.
    * Percentages are expected to be within [0.0, 1.0].
    *
    * @param source the source image
    * @param prc    the width percentage
    * @param target the target image
    *
    * @return the scaled image
    */
   public static PImage scaleBilinear ( final PImage source, final Vec2 prc,
      final PImage target ) {

      return ZImage.scaleBilinear(source, prc.x, prc.y, target);
   }

   /**
    * Skews a source image horizontally.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed image
    */
   public static PImage skewX ( final PImage source, final float angle,
      final PImage target ) {

      return ZImage.skewXBilinear(source, angle, target);
   }

   /**
    * Skews a source image horizontally.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed image
    *
    * @see Pixels#skewXBilinear(int[], int, int, float, Vec2)
    */
   public static PImage skewXBilinear ( final PImage source, final float angle,
      final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int pd = source.pixelDensity;
      final int[] pxSrc = source.pixels;
      final Vec2 dim = new Vec2();
      target.pixels = Pixels.skewXBilinear(pxSrc, w, h, angle, dim);
      target.format = source.format;
      target.pixelDensity = pd;
      target.pixelWidth = ( int ) dim.x;
      target.pixelHeight = ( int ) dim.y;
      target.width = target.pixelWidth / pd;
      target.height = target.pixelHeight / pd;
      target.updatePixels();

      return target;
   }

   /**
    * Skews a source image vertically.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed image
    */
   public static PImage skewY ( final PImage source, final float angle,
      final PImage target ) {

      return ZImage.skewYBilinear(source, angle, target);
   }

   /**
    * Skews a source image vertically.
    *
    * @param source the source image
    * @param angle  the angle in radians
    * @param target the target image
    *
    * @return the skewed image
    *
    * @see Pixels#skewYBilinear(int[], int, int, float, Vec2)
    */
   public static PImage skewYBilinear ( final PImage source, final float angle,
      final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int pd = source.pixelDensity;
      final int[] pxSrc = source.pixels;
      final Vec2 dim = new Vec2();
      target.pixels = Pixels.skewYBilinear(pxSrc, w, h, angle, dim);
      target.format = source.format;
      target.pixelDensity = pd;
      target.pixelWidth = ( int ) dim.x;
      target.pixelHeight = ( int ) dim.y;
      target.width = target.pixelWidth / pd;
      target.height = target.pixelHeight / pd;
      target.updatePixels();

      return target;
   }

   /**
    * Converts an image from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB) to
    * linear RGB.
    *
    * @param source      the source image
    * @param adjustAlpha include the alpha channel in the adjustment
    * @param target      the target image
    *
    * @return the linear image
    *
    * @see Pixels#sRgbaTolRgba(int, boolean)
    */
   public static PImage sRgbaTolRgba ( final PImage source,
      final boolean adjustAlpha, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.sRgbaTolRgba(target.pixels, adjustAlpha, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.sRgbaTolRgba(pxSrc, adjustAlpha,
         new int[pxSrc.length]);
      target.format = source.format;
      target.pixelDensity = source.pixelDensity;
      target.pixelWidth = w;
      target.pixelHeight = h;
      target.width = source.width;
      target.height = source.height;
      target.updatePixels();

      return target;
   }

   /**
    * Converts an image from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB) to
    * linear RGB.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the linear image
    */
   public static PImage sRgbaTolRgba ( final PImage source,
      final PImage target ) {

      return ZImage.sRgbaTolRgba(source, false, target);
   }

   /**
    * Finds the minimum, maximum and mean lightness in a source image. If
    * factor is positive, stretches color to maximum lightness range in [0.0,
    * 100.0]. If factor is negative, compresses color to mean. Assigns result
    * to target array. The factor is expected to be in [-1.0, 1.0].<br>
    * <br>
    * If difference between minimum and maximum lightness is negligible,
    * copies source array to target.
    *
    * @param source the source image
    * @param fac    the factor
    * @param target the target image
    *
    * @return the normalized image
    *
    * @see Pixels#stretchContrast(int[], float, int[])
    */
   public static PImage stretchContrast ( final PImage source, final float fac,
      final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.stretchContrast(target.pixels, fac, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.stretchContrast(pxSrc, fac, new int[pxSrc.length]);
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
    * Finds the minimum and maximum lightness in a source image. If difference
    * between minimum and maximum lightness is negligible, copies source array
    * to target.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the normalized image
    */
   public static PImage stretchContrast ( final PImage source,
      final PImage target ) {

      return ZImage.stretchContrast(source, 1.0f, target);

   }

   /**
    * Tints an image to a color by a factor. The image's alpha channel is
    * converted to a factor to mix the colors.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param fac    the factor
    * @param target the target image
    *
    * @return the tinted image
    *
    * @see Color#toHexIntSat(Color)
    */
   public static PImage tint ( final PImage source, final Color tint,
      final float fac, final PImage target ) {

      return ZImage.tint(source, Color.toHexIntSat(tint), fac, target);
   }

   /**
    * Tints an image to a color. The image's alpha channel is converted to a
    * factor to mix the colors.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param target the target image
    *
    * @return the tinted image
    *
    * @see Color#toHexIntSat(Color)
    */
   public static PImage tint ( final PImage source, final Color tint,
      final PImage target ) {

      return ZImage.tint(source, Color.toHexIntSat(tint), target);
   }

   /**
    * Tints an image to a color by a factor.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param fac    the factor
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final float fac, final PImage target ) {

      final int valTint = source.format == PConstants.RGB ? 0xff000000 | tint
         : tint;

      if ( source == target ) {
         target.loadPixels();
         Pixels.tintLab(target.pixels, valTint, fac, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.tintLab(pxSrc, valTint, fac,
         new int[pxSrc.length]);
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
    * Tints an image to a color by a factor. The image's alpha channel is
    * converted to a factor to mix the colors.
    *
    * @param source the source image
    * @param tint   the tint color
    * @param target the target image
    *
    * @return the tinted image
    */
   public static PImage tint ( final PImage source, final int tint,
      final PImage target ) {

      final int a255 = tint >> 0x18 & 0xff;
      final float fac = a255 * IUtils.ONE_255;
      return ZImage.tint(source, 0xff000000 | tint, fac, target);
   }

   /**
    * Returns a string representation of an image, including its format,
    * width, height and pixel density.
    *
    * @param img the PImage
    *
    * @return the string
    */
   public static String toString ( final PImage img ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ format: ");
      sb.append(img.format);
      sb.append(", width: ");
      sb.append(img.width);
      sb.append(", height: ");
      sb.append(img.height);
      sb.append(", pixelDensity: ");
      sb.append(img.pixelDensity);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Removes excess transparent pixels from an image.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the trimmed image
    *
    * @see Pixels#trimAlpha(int[], int, int, Vec2)
    */
   public static PImage trimAlpha ( final PImage source, final PImage target ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int pd = source.pixelDensity;
      final int[] pxSrc = source.pixels;
      final Vec2 dim = new Vec2();
      target.pixels = Pixels.trimAlpha(pxSrc, w, h, dim);
      target.format = source.format;
      target.pixelDensity = pd;
      target.pixelWidth = ( int ) dim.x;
      target.pixelHeight = ( int ) dim.y;
      target.width = target.pixelWidth / pd;
      target.height = target.pixelHeight / pd;
      target.updatePixels();

      return target;
   }

   /**
    * Divides the red, green and blue channels of each pixel in a source image
    * by the alpha channel. Reverse pre-multiplication.
    *
    * @param source the source image
    * @param target the target image
    *
    * @return the image
    *
    * @see Pixels#unpremul(int[], int[])
    */
   public static PImage unpremul ( final PImage source, final PImage target ) {

      if ( source == target ) {
         target.loadPixels();
         Pixels.unpremul(target.pixels, target.pixels);
         target.updatePixels();
         return target;
      }

      if ( target instanceof PGraphics ) {
         System.err.println("Do not use PGraphics with this method.");
         return target;
      }

      source.loadPixels();
      target.loadPixels();
      final int w = source.pixelWidth;
      final int h = source.pixelHeight;
      final int[] pxSrc = source.pixels;
      target.pixels = Pixels.unpremul(pxSrc, new int[pxSrc.length]);
      target.format = source.format;
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
    *
    * @return the image
    *
    * @see Pixels#wrap(int[], int, int, int, int, int, int[])
    */
   public static PImage wrap ( final PImage source, final int dx, final int dy,
      final PImage target ) {

      source.loadPixels();
      target.loadPixels();
      Pixels.wrap(source.pixels, source.pixelWidth, source.pixelHeight, dx, dy,
         target.pixelWidth, target.pixels);
      target.format = source.format;
      target.updatePixels();

      return target;
   }

}
