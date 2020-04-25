package camzup.pfriendly;

import java.util.function.Function;

import java.awt.Image;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.IUtils;
import camzup.core.Sdf;
import camzup.core.Utils;
import camzup.core.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PFont.Glyph;
import processing.core.PImage;

/**
 * An extension of PImage, specializing in color gradients and blitting
 * text to an image.
 */
public class ZImage extends PImage {

   /**
    * Constructs an image from the native AWT image.
    *
    * @param img the image
    */
   public ZImage ( final Image img ) { super(img); }

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
   protected ZImage ( ) { super(); }

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
    * A temporary color used for converting color data to and from hexadecimal
    * values.
    */
   private static final Color clr;

   static {
      clr = new Color();
   }

   /**
    * Finds the aspect ratio of an image, it's width divided by its height.
    *
    * @param img the image
    *
    * @return the aspect ratio
    */
   public static float aspect ( final PImage img ) {

      return Utils.div(img.width, img.height);
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param xOrigin the origin x coordinate
    * @param yOrigin the origin y coordinate
    * @param radians the angular offset
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage conic ( final float xOrigin, final float yOrigin,
      final float radians, final Gradient grd, final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float hInv = 1.0f / ( h - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      for ( int i = 0, y = 0; y < h; ++y ) {

         final float yn = y * hInv;
         final float t = 1.0f - ( yn + yn + yOrigin );

         for ( int x = 0; x < w; ++x, ++i ) {

            final float xn = x * wInv;
            final float s = xn + xn - xOrigin - 1.0f;

            Gradient.eval(grd, Sdf.conic(s, t, radians), ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }
      target.updatePixels();
      return target;
   }

   /**
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point.
    *
    * @param origin  the origin
    * @param radians the angular offset
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage conic ( final Vec2 origin, final float radians,
      final Gradient grd, final PImage target ) {

      return ZImage.conic(origin.x, origin.y, radians, grd, target);
   }

   /**
    * Recolors an image in-place with a color gradient. The color is converted
    * to a factor in [0.0, 1.0] by an evaluation function.
    *
    * @param grd     the color gradient
    * @param clrEval the color evaluator
    * @param target  the target image
    *
    * @return the augmented image
    */
   public static PImage falseColor ( final Gradient grd, final Function <
      Integer, Float > clrEval, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         Gradient.eval(grd, clrEval.apply(px[i]), ZImage.clr);
         px[i] = Color.toHexInt(ZImage.clr);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Recolors an image in-place with a color gradient. The image's luminance
    * is used as the factor.
    *
    * @param grd    the color gradient
    * @param target the target image
    *
    * @return the augmented image
    */
   public static PImage falseColor ( final Gradient grd, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         Gradient.eval(grd, Color.luminance(px[i]), ZImage.clr);
         px[i] = Color.toHexInt(ZImage.clr);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Fills an image in place with a color.
    *
    * @param target the target image
    * @param clr    the fill color
    *
    * @return the image
    */
   public static PImage fill ( final Color clr, final PImage target ) {

      return ZImage.fill(Color.toHexInt(clr), target);
   }

   /**
    * Fills an image with a gradient in place. The gradient is horizontal.
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the image
    */
   public static PImage fill ( final Gradient grd, final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float wInv = 1.0f / ( w - 1.0f );
      for ( int i = 0, y = 0; y < h; ++y ) {
         for ( int x = 0; x < w; ++x, ++i ) {
            Gradient.eval(grd, x * wInv, ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Fills an image with a color in place.
    *
    * @param clr    the fill color
    * @param target the target image
    *
    * @return the image
    */
   public static PImage fill ( final int clr, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = clr;
      }
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
    * @param font the Processing font
    * @param text the string of text
    * @param clr  the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color clr ) {

      return ZImage.fromText(font, text, Color.toHexInt(clr));
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
    * @param clr     the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color clr, final int leading ) {

      return ZImage.fromText(font, text, Color.toHexInt(clr), leading);
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
    * @param clr     the color
    * @param leading spacing between lines
    * @param kerning spacing between characters
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color clr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, Color.toHexInt(clr), leading, kerning);
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
    * @param clr       the color
    * @param leading   spacing between lines
    * @param kerning   spacing between characters
    * @param textAlign the horizontal alignment
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final Color clr, final int leading, final int kerning,
      final int textAlign ) {

      return ZImage.fromText(font, text, Color.toHexInt(clr), leading, kerning,
         textAlign);
   }

   /**
    * Blits glyph images from a {@link PFont} onto a single image. <br>
    * Images created with this method do not have a reference to a parent
    * PApplet.
    *
    * @param font the Processing font
    * @param text the string of text
    * @param clr  the color
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int clr ) {

      return ZImage.fromText(font, text, clr, ZImage.DEFAULT_LEADING,
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
    * @param clr     the color
    * @param leading spacing between lines
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int clr, final int leading ) {

      return ZImage.fromText(font, text, clr, leading, ZImage.DEFAULT_KERNING,
         ZImage.DEFAULT_ALIGN);
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
    * @param clr     the color
    * @param leading spacing between lines
    * @param kerning spacing between characters
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int clr, final int leading, final int kerning ) {

      return ZImage.fromText(font, text, clr, leading, kerning,
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
    * @param clr       the color
    * @param leading   spacing between lines
    * @param kerning   spacing between characters
    * @param textAlign the horizontal alignment
    *
    * @return the new image
    */
   public static PImage fromText ( final PFont font, final String text,
      final int clr, final int leading, final int kerning,
      final int textAlign ) {

      /*
       * This does not use an output target because of difficulties finding a
       * way to resize an existing image efficiently. PImage's resize function
       * is undefined and the issue is complicated by PImageAWT .
       */

      /*
       * Validate inputs: colors with no alpha not allowed; negative leading and
       * kerning not allowed; try to guard against empty Strings. Remove alpha
       * from tint; source image alpha will be used.
       */
      final String vTxt = text.trim();
      if ( vTxt.isEmpty() ) { return new PImage(32, 32, PConstants.ARGB, 1); }
      final int vLead = leading < 0 ? 1 : leading + 1;
      final int vKern = kerning < 0 ? 0 : kerning;
      final int vClr = ( clr >> 0x18 & 0xff ) != 0 ? 0x00ffffff & clr
         : 0x00ffffff;

      /*
       * Carriage returns, or line breaks, could come in 3 variations: \r, \n,
       * or \r\n .
       */
      final String[] linesSplit = vTxt.split("\r\n|\n|\r");
      final int lineCount = linesSplit.length;
      final char[][][] characters = new char[lineCount][][];
      for ( int i = 0; i < lineCount; ++i ) {
         final String[] words = linesSplit[i].split("\\s+");
         final int charCount = words.length;
         final char[][] charLine = characters[i] = new char[charCount][];
         for ( int j = 0; j < charCount; ++j ) {
            charLine[j] = words[j].toCharArray();
         }
      }

      /* Determine width of a space. */
      final Glyph whiteSpace = font.getGlyph('-');
      final int spaceWidth = whiteSpace != null ? whiteSpace.width
         : ( int ) ( font.getSize() * IUtils.ONE_THIRD );

      final Glyph[][][] glyphs = new Glyph[lineCount][][];

      /*
       * The last line's descenders are chopped off if padding isn't added to
       * the bottom of the image.
       */
      int lastRowPadding = 0;

      /* Total height and max width will decide the image's dimensions. */
      final int[] lineHeights = new int[lineCount];
      final int[] lineWidths = new int[lineCount];
      int hTotal = 0;
      int wMax = Integer.MIN_VALUE;

      /* Loop through lines. */
      for ( int i = 0; i < lineCount; ++i ) {
         final char[][] charLine = characters[i];
         final int wordCount = charLine.length;
         final Glyph[][] glyphLine = new Glyph[wordCount][];
         glyphs[i] = glyphLine;

         int sumWidths = 0;
         int maxHeight = Integer.MIN_VALUE;
         int maxDescent = Integer.MIN_VALUE;

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
                  maxHeight = height > maxHeight ? height : maxHeight;
                  sumWidths += glyph.width + vKern + glyph.leftExtent;

                  maxDescent = glyphDescent > maxDescent ? glyphDescent
                     : maxDescent;
                  lastRowPadding = maxDescent;
               }
            }

            /* Add a space between words. */
            sumWidths += spaceWidth + vKern;
         }

         lineHeights[i] = maxHeight;
         hTotal += maxHeight;

         lineWidths[i] = sumWidths;
         wMax = sumWidths > wMax ? sumWidths : wMax;
      }

      hTotal += lastRowPadding;

      /*
       * Offset the xCursor's initial position by an offset depending on
       * horizontal alignment.
       */
      final int[] lineOffsets = new int[lineCount];
      switch ( textAlign ) {

         case PConstants.CENTER:

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = ( wMax - lineWidths[i] ) / 2;
            }

            break;

         case PConstants.RIGHT:

            for ( int i = 0; i < lineCount; ++i ) {
               lineOffsets[i] = wMax - lineWidths[i];
            }

            break;

         case PConstants.LEFT:

         default:

      }

      final PImage target = new PImage(wMax, hTotal, PConstants.ARGB, 1);
      target.loadPixels();
      final int[] trgPx = target.pixels;
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
                     final int hSrc = source.pixelHeight;
                     final int[] srcPx = source.pixels;
                     final int yStart = yCursor + lineHeight - glyph.topExtent;

                     /*
                      * Loop through source image height and width. Target index
                      * is manually calculated in the inner loop using the
                      * formula index = x + y * width.
                      */
                     for ( int idxSrc = 0, ySrc = 0, yTrg = yStart; ySrc < hSrc;
                        ++ySrc, ++yTrg ) {
                        final int idxOffTrg = yTrg * wMax;
                        for ( int xSrc = 0, xTrg = xCursor; xSrc < wSrc;
                           ++xSrc, ++idxSrc, ++xTrg ) {
                           final int idxTrg = idxOffTrg + xTrg;

                           /*
                            * Shift source image from grey scale, stored in the
                            * blue channel, to ARGB. Composite target and
                            * source, then composite in tint color.
                            */
                           trgPx[idxTrg] = trgPx[idxTrg] | srcPx[idxSrc] << 0x18
                              | vClr;
                        }
                     }

                     // target.updatePixels(xCursor, yCursor, wSrc, hSrc);
                  }
                  xCursor += glyph.width + vKern;
               }
            }
            xCursor += spaceWidth + vKern;
         }
         yCursor += lineHeight;
      }
      target.updatePixels();

      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The value is clamped to a range [0.0, 1.0] .
    *
    * @param xOrigin the origin x coordinate
    * @param yOrigin the origin y coordinate
    * @param xDest   the destination x coordinate
    * @param yDest   the destination y coordinate
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage linear ( final float xOrigin, final float yOrigin,
      final float xDest, final float yDest, final Gradient grd,
      final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] pixels = target.pixels;

      final float hInv = 1.0f / ( h - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float bx = xOrigin - xDest;
      final float by = yOrigin - yDest;
      final float bbInv = 1.0f / Utils.max(IUtils.DEFAULT_EPSILON, bx * bx + by
         * by);

      for ( int i = 0, y = 0; y < h; ++y ) {

         final float yn = y * hInv;
         final float ay = yOrigin - ( 1.0f - ( yn + yn ) );

         for ( int x = 0; x < w; ++x, ++i ) {

            final float xn = x * wInv;
            final float ax = xOrigin - ( xn + xn - 1.0f );

            Gradient.eval(grd, Utils.clamp01( ( ax * bx + ay * by ) * bbInv),
               ZImage.clr);
            pixels[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The value is clamped to a range [0.0, 1.0] .
    *
    * @param origin the origin
    * @param dest   the destination
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the image
    */
   public static PImage linear ( final Vec2 origin, final Vec2 dest,
      final Gradient grd, final PImage target ) {

      return ZImage.linear(origin.x, origin.y, dest.x, dest.y, grd, target);
   }

   /**
    * Generates a radial gradient. This does not account for aspect ratio, so
    * an image that is not 1:1 will result in an ellipsoid.
    *
    * @param xOrigin the x coordinate
    * @param yOrigin the y coordinate
    * @param radius  the radius
    * @param grd     the gradient
    * @param target  the output image
    *
    * @return the image
    */
   public static PImage radial ( final float xOrigin, final float yOrigin,
      final float radius, final Gradient grd, final PImage target ) {

      target.loadPixels();

      final int h = target.height;
      final int w = target.width;
      final int[] px = target.pixels;

      final float hInv = 1.0f / ( h - 1.0f );
      final float wInv = 1.0f / ( w - 1.0f );

      final float r2 = radius + radius;
      final float invrsq = 1.0f / Utils.max(IUtils.DEFAULT_EPSILON, r2 * r2);

      for ( int i = 0, y = 0; y < h; ++y ) {

         final float yn = y * hInv;
         final float ay = yOrigin - ( 1.0f - ( yn + yn ) );
         final float aysq = ay * ay;

         for ( int x = 0; x < w; ++x, ++i ) {

            final float xn = x * wInv;
            final float ax = xOrigin - ( xn + xn - 1.0f );

            Gradient.eval(grd, 1.0f - ( ax * ax + aysq ) * invrsq, ZImage.clr);
            px[i] = Color.toHexInt(ZImage.clr);
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a radial gradient. This does not account for aspect ratio, so
    * an image that is not 1:1 will result in an ellipsoid.
    *
    * @param origin the origin
    * @param radius the radius
    * @param grd    the gradient
    * @param target the output image
    *
    * @return the image
    */
   public static PImage radial ( final Vec2 origin, final float radius,
      final Gradient grd, final PImage target ) {

      return ZImage.radial(origin.x, origin.y, radius, grd, target);
   }

   /**
    * Generates a diagnostic image where a pixel's location on the x-axis
    * correlates to the color red; on the y-axis, to green.
    *
    * @param target the output image
    *
    * @return the image
    */
   public static PImage rgb ( final PImage target ) {

      target.loadPixels();

      final int[] px = target.pixels;
      final int h = target.height;
      final int w = target.width;

      final float hInv = 0xff / ( h - 1.0f );
      final float wInv = 0xff / ( w - 1.0f );

      for ( int i = 0, y = h - 1; y > -1; --y ) {
         final int grbl = 0xff000080 | ( int ) ( y * hInv + 0.5f ) << 0x8;
         for ( int x = 0; x < w; ++x, ++i ) {
            px[i] = ( int ) ( x * wInv + 0.5f ) << 0x10 | grbl;
         }
      }

      target.updatePixels();
      return target;
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

      /* @formatter:off */
      return new StringBuilder(64)
         .append("{ format: ")
         .append(img.format)
         .append(", width: ")
         .append(Utils.toPadded(img.width, 4))
         .append(", height: ")
         .append(Utils.toPadded(img.height, 4))
         .append(", pixelDensity: ")
         .append(img.pixelDensity)
         .append(' ')
         .append('}')
         .toString();
      /* @formatter:on */
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source  source pixel array
    * @param wSource source image width
    * @param hSource source image height
    * @param target  target pixel array
    * @param wTarget target image width
    * @param hTarget target image height
    * @param dx      horizontal pixel offset
    * @param dy      vertical pixel offset
    *
    * @return the target pixels
    */
   public static int[] wrap ( final int[] source, final int wSource,
      final int hSource, final int[] target, final int wTarget,
      final int hTarget, final int dx, final int dy ) {

      if ( wSource < 1 || hSource < 1 ) { return target; }

      for ( int i = 0, y = 0; y < hTarget; ++y ) {

         // final int ny = wSource * Math.floorMod(y - dy, hSource);
         int ymod = ( y - dy ) % hSource;
         if ( ( ymod ^ hSource ) < 0 && ymod != 0 ) { ymod += hSource; }
         final int ny = wSource * ymod;

         for ( int x = 0; x < wTarget; ++x, ++i ) {

            // target[i] = source[Math.floorMod(x + dx, wSource) + ny];
            int xmod = ( x + dx ) % wSource;
            if ( ( xmod ^ wSource ) < 0 && xmod != 0 ) { xmod += wSource; }
            target[i] = source[xmod + ny];
         }
      }
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final float dx, final float dy ) {

      return ZImage.wrap(source, target, ( int ) dx, ( int ) dy);
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param dx     horizontal pixel offset
    * @param dy     vertical pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final int dx, final int dy ) {

      source.loadPixels();
      target.loadPixels();
      ZImage.wrap(source.pixels, source.width, source.height, target.pixels,
         target.width, target.height, dx, dy);
      target.updatePixels();
      // source.updatePixels();
      return target;
   }

   /**
    * Blits a source image's pixels onto a target image's pixels, using
    * integer floor modulo to wrap the source image. The source image can be
    * offset horizontally and/or vertically, creating the illusion of
    * parallax.
    *
    * @param source the source image
    * @param target the target image
    * @param d      pixel offset
    *
    * @return the image
    */
   public static PImage wrap ( final PImage source, final PImage target,
      final Vec2 d ) {

      return ZImage.wrap(source, target, ( int ) d.x, ( int ) d.y);
   }

}
