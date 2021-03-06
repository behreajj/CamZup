package camzup.pfriendly;

import java.util.function.IntFunction;
import java.util.regex.Pattern;

import camzup.core.Color;
import camzup.core.Gradient;
import camzup.core.ISvgWritable;
import camzup.core.IUtils;
import camzup.core.Sdf;
import camzup.core.Utils;
import camzup.core.Vec2;

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

      // QUERY Is there a set opaque or remove alpha func in pimage?
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
    * Resizes the image to a requested size in pixels using bicubic
    * interpolation.
    *
    * @param w width
    * @param h height
    */
   @Override
   public void resize ( final int w, final int h ) {

      ZImage.resizeBicubic(this, w, h);
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
    * Convert an image in the {@link PConstants#ALPHA} format to an image in
    * the {@link PConstants#ARGB} format.
    *
    * @param target the image
    *
    * @return the conversion
    */
   public static PImage alphaToArgb ( final PImage target ) {

      if ( target.format != PConstants.ALPHA ) { return target; }

      target.loadPixels();
      final int[] pixels = target.pixels;
      final int len = pixels.length;
      for ( int i = 0; i < len; ++i ) {
         final int val = pixels[i];
         pixels[i] = val << 0x18 | val << 0x10 | val << 0x08 | val;
      }
      target.format = PConstants.ARGB;
      target.updatePixels();
      return target;
   }

   /**
    * Finds the aspect ratio of an image, it's width divided by its height.
    *
    * @param img the image
    *
    * @return the aspect ratio
    */
   public static float aspect ( final PImage img ) {

      return Utils.div(( float ) img.width, ( float ) img.height);
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

      return ZImage.checker(Color.toHexInt(a), Color.toHexInt(b), cols, rows,
         target);
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
    * the image dimensions should be a power of 2 (32, 64, 128, 256, 512).
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

      target.loadPixels();

      final int w = target.pixelWidth;
      final int h = target.pixelHeight;
      final int pd = target.pixelDensity;

      /*
       * Rows and columns should have a maximum bound, because it will be easy
       * to transpose color and columns & rows arguments.
       */
      final int limit = 2 * pd;
      final int vcols = cols < 2 ? 2 : cols > w / limit ? w / limit : cols;
      final int vrows = rows < 2 ? 2 : rows > h / limit ? h / limit : rows;

      final int[] px = target.pixels;
      final int len = px.length;

      // QUERY: Do vrows and vcols also need to account for pixel density?
      // This will have to go untested for now.
      final int wch = w / vcols;
      final int hchw = w * h / vrows;

      for ( int i = 0; i < len; ++i ) {
         px[i] = ( i % w / wch + i / hchw ) % 2 == 0 ? a : b;
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
    * Generates a conic gradient, where the factor rotates on the z axis
    * around an origin point. Best used with square images; for other aspect
    * ratios, the origin should be adjusted accordingly.
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

      final int w = target.pixelWidth;
      final int h = target.pixelHeight;
      final int[] pixels = target.pixels;
      final int len = pixels.length;

      final float aspect = w / ( float ) h;
      final float wInv = aspect / ( w - 1.0f );
      final float hInv = 1.0f / ( h - 1.0f );
      final float xo = ( xOrigin * 0.5f + 0.5f ) * aspect * 2.0f - 1.0f;

      for ( int i = 0; i < len; ++i ) {
         final float xn = wInv * ( i % w );
         final float yn = hInv * ( i / w );
         pixels[i] = Gradient.eval(grd, Sdf.conic(xn + xn - xo - 1.0f, 1.0f
            - ( yn + yn + yOrigin ), radians));
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
   public static PImage falseColor ( final Gradient grd, final IntFunction <
      Float > clrEval, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = Gradient.eval(grd, clrEval.apply(px[i]));
      }
      target.updatePixels();
      return target;
   }

   /**
    * Recolors an image in-place with a color gradient. Also known as a
    * gradient map. The evaluation factor is the product of a pixel's
    * luminance and its transparency.
    *
    * @param grd    the color gradient
    * @param target the target image
    *
    * @return the augmented image
    *
    * @see Color#sRgbLuminance(int)
    */
   public static PImage falseColor ( final Gradient grd, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         final float alpha = ( px[i] >> 0x18 & 0xff ) * IUtils.ONE_255;
         float lum = Color.sRgbLuminance(px[i]);
         lum = lum <= 0.0031308f ? lum * 12.92f : ( float ) ( Math.pow(lum,
            0.4166666666666667d) * 1.055d - 0.055d );
         px[i] = Gradient.eval(grd, alpha * lum);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Fills an image in place with a color.
    *
    * @param target the target image
    * @param fll    the fill color
    *
    * @return the image
    */
   public static PImage fill ( final Color fll, final PImage target ) {

      return ZImage.fill(Color.toHexInt(fll), target);
   }

   /**
    * Fills an image with a color in place.
    *
    * @param fll    the fill color
    * @param target the target image
    *
    * @return the image
    */
   public static PImage fill ( final int fll, final PImage target ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) { px[i] = fll; }
      target.updatePixels();
      return target;
   }

   /**
    * Flips an image horizontally, on the x axis. Modifies the target image in
    * place.
    *
    * @param target the output image
    *
    * @return the flipped image
    */
   public static PImage flipX ( final PImage target ) {

      target.loadPixels();

      final int h = target.pixelHeight;
      final int w = target.pixelWidth;
      final int[] pixels = target.pixels;
      final int[] flipped = new int[pixels.length];

      for ( int i = 0, y = 0; y < h; ++y ) {
         final int yw = y * w;
         for ( int x = w - 1; x > -1; --x, ++i ) {
            flipped[yw + x] = pixels[i];
         }
      }

      target.pixels = flipped;
      target.updatePixels();

      return target;
   }

   /**
    * Flips an image vertically, on the y axis. Modifies the target image in
    * place.
    *
    * @param target the output image
    *
    * @return the flipped image
    */
   public static PImage flipY ( final PImage target ) {

      target.loadPixels();

      final int h = target.pixelHeight;
      final int w = target.pixelWidth;
      final int[] pixels = target.pixels;
      final int[] flipped = new int[pixels.length];

      for ( int i = 0, y = h - 1; y > -1; --y ) {
         final int yw = y * w;
         for ( int x = 0; x < w; ++x, ++i ) { flipped[yw + x] = pixels[i]; }
      }

      target.pixels = flipped;
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

      return ZImage.fromText(font, text, Color.toHexInt(fillClr));
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

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading);
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

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading,
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

      return ZImage.fromText(font, text, Color.toHexInt(fillClr), leading,
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

      /* wMax may have been left at zero initial value. */
      wMax = wMax < 1 ? 32 : wMax;
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
                     final int[] srcPx = source.pixels;
                     final int srcLen = srcPx.length;
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
                        trgPx[ ( yStart + idxSrc / wSrc ) * wMax + xCursor
                           + idxSrc % wSrc] |= srcPx[idxSrc] << 0x18 | vClr;
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
    * Adjusts gamma of an image. Raises red, green and blue to the power of
    * the gamma; includes alpha if the flag is true. Clamps results to stay in
    * gamut.
    *
    * @param source    the source image
    * @param gamma     the gamma
    * @param amplitude the amplitude
    * @param offset    the offset
    * @param alpha     adjust the alpha
    *
    * @return the image
    */
   public static PImage gammaAdjust ( final PImage source, final float gamma,
      final float amplitude, final float offset, final boolean alpha ) {

      source.loadPixels();

      final int[] px = source.pixels;
      final int len = px.length;

      final double gd = gamma;
      final double ad = amplitude;
      final double od = offset;

      for ( int i = 0; i < len; ++i ) {
         final int c = px[i];

         double a = ( c >> 0x18 & 0xff ) * IUtils.ONE_255_D;
         double r = ( c >> 0x10 & 0xff ) * IUtils.ONE_255_D;
         double g = ( c >> 0x08 & 0xff ) * IUtils.ONE_255_D;
         double b = ( c & 0xff ) * IUtils.ONE_255_D;

         r = Math.pow(r, gd) * ad + od;
         g = Math.pow(g, gd) * ad + od;
         b = Math.pow(b, gd) * ad + od;

         if ( r < 0.0d ) {
            r = 0.0d;
         } else if ( r > 1.0d ) { r = 1.0d; }

         if ( g < 0.0d ) {
            g = 0.0d;
         } else if ( g > 1.0d ) { g = 1.0d; }

         if ( b < 0.0d ) {
            b = 0.0d;
         } else if ( b > 1.0d ) { b = 1.0d; }

         if ( alpha ) {
            a = Math.pow(a, gd) * ad + od;
            if ( a < 0.0d ) {
               a = 0.0d;
            } else if ( a > 1.0d ) { a = 1.0d; }
         }

         px[i] = ( int ) ( a * 255.0d + 0.5d ) << 0x18 | ( int ) ( r * 255.0d
            + 0.5d ) << 0x10 | ( int ) ( g * 255.0d + 0.5d ) << 0x08
            | ( int ) ( b * 255.0d + 0.5d );
      }

      source.updatePixels();
      return source;
   }

   /**
    * Converts an image to gray scale. Does not change the image format or
    * reduce the number of bytes used to store color; all colors remain
    * 32-bit.
    *
    * @param target the output image
    *
    * @return the image
    */
   public static PImage grayscale ( final PImage target ) {

      return ZImage.grayscale(target, false);
   }

   /**
    * Converts an image to gray scale, with the option of stretching its
    * contrast, i.e., normalizing its grays according to a minimum and
    * maximum. Does not change the image format or reduce the number of bytes
    * used to store color; all colors remain 32-bit.
    *
    * @param target    the output image
    * @param normalize the normalization flag
    *
    * @return the image
    *
    * @see Color#sRgbLuminance(int)
    */
   public static PImage grayscale ( final PImage target,
      final boolean normalize ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;

      if ( normalize ) {

         /* Find minimum and maximum luminance. */
         float lumMin = Float.MAX_VALUE;
         float lumMax = Float.MIN_VALUE;
         final float[] lums = new float[len];
         for ( int i = 0; i < len; ++i ) {
            final float lum = Color.sRgbLuminance(px[i]);
            if ( lum < lumMin ) { lumMin = lum; }
            if ( lum > lumMax ) { lumMax = lum; }
            lums[i] = lum;
         }

         /* Map luminance to [0.0, 1.0] from [minimum, maximum]. */
         final float diff = lumMax - lumMin;
         final float denom = diff != 0.0f ? 1.0f / diff : 0.0f;
         for ( int i = 0; i < len; ++i ) {
            final float lum = ( lums[i] - lumMin ) * denom;
            final int viLin = ( int ) ( lum * 0xff + 0.5f );
            final int viStd = Color.linearToStandard(viLin);
            px[i] = px[i] & 0xff000000 | viStd << 0x10 | viStd << 0x08 | viStd;
         }

      } else {
         for ( int i = 0; i < len; ++i ) {
            final float lum = Color.sRgbLuminance(px[i]);
            final int viLin = ( int ) ( lum * 0xff + 0.5f );
            final int viStd = Color.linearToStandard(viLin);
            px[i] = px[i] & 0xff000000 | viStd << 0x10 | viStd << 0x08 | viStd;
         }
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
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

      final int w = target.pixelWidth;
      final int[] px = target.pixels;
      final int len = px.length;

      final float bx = xOrigin - xDest;
      final float by = yOrigin - yDest;

      final float bbInv = 1.0f / Utils.max(IUtils.EPSILON, bx * bx + by * by);

      final float bxbbinv = bx * bbInv;
      final float bybbinv = by * bbInv;

      final float xobx = xOrigin * bxbbinv;
      final float yoby = yOrigin * bybbinv;
      final float bxwInv2 = 2.0f / ( w - 1.0f ) * bxbbinv;
      final float byhInv2 = 2.0f / ( target.pixelHeight - 1.0f ) * bybbinv;

      for ( int i = 0; i < len; ++i ) {
         px[i] = Gradient.eval(grd, Utils.clamp01(xobx + bxbbinv - bxwInv2 * ( i
            % w ) + ( yoby + byhInv2 * ( i / w ) - bybbinv )));
      }

      target.updatePixels();
      return target;
   }

   /**
    * Generates a horizontal linear gradient.
    *
    * @param grd    the gradient
    * @param target the target image
    *
    * @return the image
    */
   public static PImage linear ( final Gradient grd, final PImage target ) {

      target.loadPixels();
      final int w = target.pixelWidth;
      final int[] pixels = target.pixels;
      final int len = pixels.length;
      final float wInv = 1.0f / ( w - 1.0f );
      for ( int i = 0; i < len; ++i ) {
         pixels[i] = Gradient.eval(grd, i % w * wInv);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Generates a linear gradient from an origin point to a destination point.
    * The origin and destination should be in the range [-1.0, 1.0]. The
    * scalar projection is clamped to [0.0, 1.0].
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
    * Converts an image from linear RGB to
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB).
    *
    * @param target      the image
    * @param adjustAlpha include alpha in the adjustment
    *
    * @return the standard image
    */
   public static PImage lRgbaTosRgba ( final PImage target,
      final boolean adjustAlpha ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = Color.lRgbaTosRgba(px[i], adjustAlpha);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Multiplies the red, green and blue channels of each pixel in the image
    * by its alpha channel.
    *
    * @param source the source image
    *
    * @return the pre-multiplied image
    */
   public static PImage premul ( final PImage source ) {

      source.loadPixels();
      final int[] px = source.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         final int ai = px[i] >> 0x18 & 0xff;
         if ( ai < 1 ) {
            px[i] = 0x00000000;
         } else if ( ai < 0xff ) {
            final int ri = px[i] >> 0x10 & 0xff;
            final int gi = px[i] >> 0x08 & 0xff;
            final int bi = px[i] & 0xff;
            final float divisor = ai * IUtils.ONE_255;

            px[i] = ai << 0x18 | ( int ) ( ri * divisor + 0.5f ) << 0x10
               | ( int ) ( gi * divisor + 0.5f ) << 0x08 | ( int ) ( bi
                  * divisor + 0.5f );
         }
      }
      source.updatePixels();
      return source;
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

      final int w = target.pixelWidth;
      final int[] px = target.pixels;
      final int len = px.length;

      final float hInv2 = 2.0f / ( target.pixelHeight - 1.0f );
      final float wInv2 = 2.0f / ( w - 1.0f );

      final float r2 = radius + radius;
      final float rsqInv = 1.0f / Utils.max(IUtils.EPSILON, r2 * r2);

      final float yon1 = yOrigin - 1.0f;
      final float xop1 = xOrigin + 1.0f;

      for ( int i = 0; i < len; ++i ) {
         final float ay = yon1 + hInv2 * ( i / w );
         final float ax = xop1 - wInv2 * ( i % w );
         px[i] = Gradient.eval(grd, 1.0f - ( ax * ax + ay * ay ) * rsqInv);
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
    * Resizes an image to new dimensions in pixels using
    * <a href="https://www.wikiwand.com/en/Bicubic_interpolation">bicubic
    * interpolation</a>.
    *
    * @param target the image
    * @param wPx    the new pixel width
    * @param hPx    the new pixel height
    *
    * @return the target
    */
   public static PImage resizeBicubic ( final PImage target, final int wPx,
      final int hPx ) {

      /*
       * References: https://stackoverflow.com/questions/
       * 17640173/implementation-of-bi-cubic-resize
       * https://blog.demofox.org/2015/08/15/
       * resizing-images-with-bicubic-interpolation/
       */

      if ( target instanceof PGraphics ) {
         System.err.println("Do not resize PGraphics with this method.");
         return target;
      }

      target.loadPixels();
      final int pd = target.pixelDensity;
      final int sw = target.pixelWidth;
      final int sh = target.pixelHeight;
      final int dw = ( wPx < 2 ? 2 : wPx ) * pd;
      final int dh = ( hPx < 2 ? 2 : hPx ) * pd;

      if ( dw == sw && dh == sh ) { return target; }

      final int srcFmt = target.format;
      final int[] srcpx = target.pixels;

      final int frameSize = 4;
      final int[] frame = new int[frameSize];

      /*
       * Introducing a bias was not present in the reference code, but it helped
       * with edge haloes in earlier versions of this algorithm.
       */
      // final float bias = 0.00405f;
      final float bias = 0.0f;
      final float tx = sw / ( dw * ( 1.0f + bias ) );
      final float ty = sh / ( dh * ( 1.0f + bias ) );

      /* Despite the name, RGB images retain alpha, and so have 4 channels. */
      byte chnlCount;
      switch ( srcFmt ) {
         case PConstants.RGB:
         case PConstants.ARGB:
            chnlCount = 4;
            break;

         case PConstants.ALPHA:
            chnlCount = 1;
            break;

         default:
            chnlCount = 4;
      }

      /*
       * The original algorithm consists of 4 nested for loops: rows (height),
       * columns (width), kernel, channel. This flattens them to one loop.
       */
      final int newPxlLen = dw * dh;
      final int[] clrs = new int[newPxlLen * frameSize];
      final int len2 = frameSize * chnlCount;
      final int len3 = dw * len2;
      final int len4 = dh * len3;

      final int swn1 = sw - 1;
      final int shn1 = sh - 1;

      for ( int k = 0; k < len4; ++k ) {
         final int g = k / len3; /* row index */
         final int m = k - g * len3; /* temporary */
         final int h = m / len2; /* column index */
         final int n = m - h * len2; /* temporary */
         final int j = n % frameSize; /* kernel index */

         /* Row. */
         final float gf = g;
         final int y = ( int ) ( ty * gf );
         final float dy = ty * ( gf - bias ) - y;
         final float dysq = dy * dy;

         /* Column. */
         final float hf = h;
         final int x = ( int ) ( tx * hf );
         final float dx = tx * ( hf - bias ) - x;
         final float dxsq = dx * dx;

         final int zu = y - 1 + j;
         final int z = zu < 0 ? 0 : zu > shn1 ? shn1 : zu;

         /*
          * Channel index multiplied by size of byte, as it will be used to
          * unpack color channels.
          */
         final int i8 = Byte.SIZE * ( n / frameSize );
         final int zw = z * sw;

         final int x0 = x < 0 ? 0 : x > swn1 ? swn1 : x;
         int a0 = srcpx[zw + x0] >> i8 & 0xff;

         final int x1u = x - 1;
         final int x1 = x1u < 0 ? 0 : x1u > swn1 ? swn1 : x1u;
         int d0 = srcpx[zw + x1] >> i8 & 0xff;

         final int x2u = x + 1;
         final int x2 = x2u < 0 ? 0 : x2u > swn1 ? swn1 : x2u;
         int d2 = srcpx[zw + x2] >> i8 & 0xff;

         final int x3u = x + 2;
         final int x3 = x3u < 0 ? 0 : x3u > swn1 ? swn1 : x3u;
         int d3 = srcpx[zw + x3] >> i8 & 0xff;

         /* Subtract a0 no matter the boundary condition. */
         d0 -= a0;
         d2 -= a0;
         d3 -= a0;

         float d36 = IUtils.ONE_SIX * d3;
         float a1 = -IUtils.ONE_THIRD * d0 + d2 - d36;
         float a2 = 0.5f * ( d0 + d2 );
         float a3 = -IUtils.ONE_SIX * d0 - 0.5f * d2 + d36;

         int sample = a0 + ( int ) ( a1 * dx + a2 * dxsq + a3 * ( dx * dxsq ) );
         frame[j] = sample < 0 ? 0 : sample > 255 ? 255 : sample;

         a0 = frame[1];
         d0 = frame[0] - a0;
         d2 = frame[2] - a0;
         d3 = frame[3] - a0;

         d36 = IUtils.ONE_SIX * d3;
         a1 = -IUtils.ONE_THIRD * d0 + d2 - d36;
         a2 = 0.5f * ( d0 + d2 );
         a3 = -IUtils.ONE_SIX * d0 - 0.5f * d2 + d36;

         // rowStride = dw * chnlCount
         // g * rowStride + h * chnlCount + i
         sample = a0 + ( int ) ( a1 * dy + a2 * dysq + a3 * ( dy * dysq ) );
         clrs[k / frameSize] = sample < 0 ? 0 : sample > 255 ? 255 : sample;
      }

      final int[] trgpx = new int[newPxlLen];
      switch ( srcFmt ) {

         case PConstants.ALPHA:

            System.arraycopy(clrs, 0, trgpx, 0, newPxlLen);

            break;

         case PConstants.RGB:

            for ( int i = 0, j = 0; i < newPxlLen; ++i, j += 4 ) {
               trgpx[i] = clrs[j] | clrs[j + 1] << 0x08 | clrs[j + 2] << 0x10
                  | 0xff000000;
            }

            break;

         case PConstants.ARGB:
         default:

            for ( int i = 0, j = 0; i < newPxlLen; ++i, j += 4 ) {
               trgpx[i] = clrs[j] | clrs[j + 1] << 0x08 | clrs[j + 2] << 0x10
                  | clrs[j + 3] << 0x18;
            }

            break;
      }

      target.pixels = trgpx;
      target.width = dw / pd;
      target.height = dh / pd;
      target.pixelWidth = dw;
      target.pixelHeight = dh;
      target.updatePixels();
      return target;
   }

   /**
    * Resizes an image to new dimensions in pixels using nearest neighbor.
    *
    * @param target the image
    * @param wPx    the new pixel width
    * @param hPx    the new pixel height
    *
    * @return the target
    */
   public static PImage resizeNearest ( final PImage target, final int wPx,
      final int hPx ) {

      if ( target instanceof PGraphics ) {
         System.err.println("Do not resize PGraphics with this method.");
         return target;
      }

      target.loadPixels();
      final int pd = target.pixelDensity;
      final int dw = ( wPx < 2 ? 2 : wPx ) * pd;
      final int dh = ( hPx < 2 ? 2 : hPx ) * pd;

      final int sw = target.pixelWidth;
      final int sh = target.pixelHeight;

      if ( dw == sw && dh == sh ) { return target; }

      final int[] srcpx = target.pixels;
      final int len = dw * dh;
      final int[] trgpx = new int[len];

      /*
       * Subtracting one leads to incorrect bottom-right pixel from very small
       * images.
       */
      final float bias = 0.00075f;
      final float tx = sw / ( dw * ( 1.0f + bias ) );
      final float ty = sh / ( dh * ( 1.0f + bias ) );
      for ( int k = 0; k < len; ++k ) {
         final int nx = ( int ) ( k % dw * tx - bias );
         final int ny = ( int ) ( k / dw * ty - bias );
         trgpx[k] = srcpx[ny * sw + nx];
      }

      target.pixels = trgpx;
      target.width = dw / pd;
      target.height = dh / pd;
      target.pixelWidth = dw;
      target.pixelHeight = dh;
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
    */
   public static PImage rgb ( final PImage target ) {

      target.loadPixels();

      final int[] px = target.pixels;
      final int len = px.length;
      final int w = target.pixelWidth;

      final float hInv = 0xff / ( target.pixelHeight - 1.0f );
      final float wInv = 0xff / ( w - 1.0f );

      for ( int i = 0; i < len; ++i ) {
         px[i] = 0xff000080 | ( int ) ( 0.5f + wInv * ( i % w ) ) << 0x10
            | ( int ) ( 255.5f - hInv * ( i / w ) ) << 0x08;
      }

      target.format = PConstants.ARGB;
      target.updatePixels();
      return target;
   }

   /**
    * Scales an image by a percentage of its original dimensions.
    *
    * @param target the image
    * @param prc    the percentage
    *
    * @return the image
    */
   public static PImage scaleBicubic ( final PImage target, final float prc ) {

      return ZImage.scaleBicubic(target, prc, prc);
   }

   /**
    * Scales an image by percentages of its original dimensions.
    *
    * @param target the image
    * @param xPrc   the x percent
    * @param yPrc   the y percent
    *
    * @return the image
    *
    * @see ZImage#resizeBicubic(PImage, int, int)
    */
   public static PImage scaleBicubic ( final PImage target, final float xPrc,
      final float yPrc ) {

      return ZImage.resizeBicubic(target, ( int ) ( 0.5f + target.width
         * xPrc ), ( int ) ( 0.5f + target.height * yPrc ));
   }

   /**
    * Scales an image by a percentage of its original dimensions.
    *
    * @param target the image
    * @param v      the percentage
    *
    * @return the image
    */
   public static PImage scaleBicubic ( final PImage target, final Vec2 v ) {

      return ZImage.scaleBicubic(target, v.x, v.y);
   }

   /**
    * Scales an image by a percentage of its original dimensions.
    *
    * @param target the image
    * @param prc    the percentage
    *
    * @return the image
    */
   public static PImage scaleNearest ( final PImage target, final float prc ) {

      return ZImage.scaleNearest(target, prc, prc);
   }

   /**
    * Scales an image by percentages of its original dimensions.
    *
    * @param target the image
    * @param xPrc   the x percent
    * @param yPrc   the y percent
    *
    * @return the image
    *
    * @see ZImage#resizeNearest(PImage, int, int)
    */
   public static PImage scaleNearest ( final PImage target, final float xPrc,
      final float yPrc ) {

      return ZImage.resizeNearest(target, ( int ) ( 0.5f + target.width
         * xPrc ), ( int ) ( 0.5f + target.height * yPrc ));
   }

   /**
    * Scales an image by a percentage of its original dimensions.
    *
    * @param target the image
    * @param v      the percentage
    *
    * @return the image
    */
   public static PImage scaleNearest ( final PImage target, final Vec2 v ) {

      return ZImage.scaleNearest(target, v.x, v.y);
   }

   /**
    * Converts an image from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB) to
    * linear RGB.
    *
    * @param target      the image
    * @param adjustAlpha include the alpha channel in the adjustment
    *
    * @return the linear image
    */
   public static PImage sRgbaTolRgba ( final PImage target,
      final boolean adjustAlpha ) {

      target.loadPixels();
      final int[] px = target.pixels;
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) {
         px[i] = Color.sRgbaTolRgba(px[i], adjustAlpha);
      }
      target.updatePixels();
      return target;
   }

   /**
    * Tints an image to a color.
    *
    * @param source  the source image
    * @param tintClr the tint color
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final Color tintClr ) {

      return ZImage.tint(source, Color.toHexInt(tintClr), 0.5f);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] .
    *
    * @param source  the source image
    * @param tintClr the tint color
    * @param fac     the factor
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final Color tintClr,
      final float fac ) {

      return ZImage.tint(source, Color.toHexInt(tintClr), fac);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] .
    *
    * @param source  the source image
    * @param tintClr the tint color
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final int tintClr ) {

      return ZImage.tint(source, tintClr, 0.5f);
   }

   /**
    * Tints an image to a color by a factor in [0.0, 1.0] . For images with
    * alpha, the minimum alpha between the source and tint color are used.
    *
    * @param source  the source image
    * @param tintClr the tint color
    * @param fac     the factor
    *
    * @return the image
    */
   public static PImage tint ( final PImage source, final int tintClr,
      final float fac ) {

      /* Do not optimize until you settle on an appropriate tinting formula. */

      /* Right operand. Decompose tint color. */
      final int ya = tintClr >> 0x18 & 0xff;
      final int yr = tintClr >> 0x10 & 0xff;
      final int yg = tintClr >> 0x08 & 0xff;
      final int yb = tintClr & 0xff;

      /* Convert from [0, 255] to [0.0, 1.0] . */
      final float yaf = ya * IUtils.ONE_255;
      final float yrf = yr * IUtils.ONE_255;
      final float ygf = yg * IUtils.ONE_255;
      final float ybf = yb * IUtils.ONE_255;

      final int srcFmt = source.format;
      final float t = Utils.clamp01(fac);
      final float u = 1.0f - t;

      source.loadPixels();
      final int[] pixels = source.pixels;
      final int len = pixels.length;

      switch ( srcFmt ) {

         case PConstants.ALPHA:

            final int trgb = 0x00ffffff & tintClr;
            for ( int i = 0; i < len; ++i ) {
               final float xaf = pixels[i] * IUtils.ONE_255;
               final float zaf = Utils.min(xaf, yaf);
               pixels[i] = ( int ) ( zaf * 0xff + 0.5f ) << 0x18 | trgb;
            }

            break;

         case PConstants.RGB:

            for ( int i = 0; i < len; ++i ) {
               final int rgb = pixels[i];

               /* Left operand. Decompose color. */
               final float xrf = IUtils.ONE_255 * ( rgb >> 0x10 & 0xff );
               final float xgf = IUtils.ONE_255 * ( rgb >> 0x08 & 0xff );
               final float xbf = IUtils.ONE_255 * ( rgb & 0xff );

               /* Lerp from left to right by factor t. */
               final float zrf = u * xrf + t * yrf;
               final float zgf = u * xgf + t * ygf;
               final float zbf = u * xbf + t * ybf;

               /* @formatter:off */
               pixels[i] = 0xff000000
                         | ( int ) ( zrf * 0xff + 0.5f ) << 0x10
                         | ( int ) ( zgf * 0xff + 0.5f ) << 0x08
                         | ( int ) ( zbf * 0xff + 0.5f );
               /* @formatter:on */
            }

            break;

         case PConstants.ARGB:
         default:

            for ( int i = 0; i < len; ++i ) {
               final int argb = pixels[i];

               /* Left operand. Decompose color. */
               final float xaf = IUtils.ONE_255 * ( argb >> 0x18 & 0xff );
               final float xrf = IUtils.ONE_255 * ( argb >> 0x10 & 0xff );
               final float xgf = IUtils.ONE_255 * ( argb >> 0x08 & 0xff );
               final float xbf = IUtils.ONE_255 * ( argb & 0xff );

               /* Lerp from left to right by factor t. */
               final float zaf = Utils.min(xaf, yaf);
               final float zrf = u * xrf + t * yrf;
               final float zgf = u * xgf + t * ygf;
               final float zbf = u * xbf + t * ybf;

               /* @formatter:off */
               pixels[i] = ( int ) ( zaf * 0xff + 0.5f ) << 0x18
                         | ( int ) ( zrf * 0xff + 0.5f ) << 0x10
                         | ( int ) ( zgf * 0xff + 0.5f ) << 0x08
                         | ( int ) ( zbf * 0xff + 0.5f );
               /* @formatter:on */
            }

            break;

      }

      source.updatePixels();
      source.format = PConstants.ARGB;

      return source;
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
    * Converts an image to a SVG, where a rectangle with non-zero alpha
    * represents a pixel. Intended for use with small images in the pixel art
    * style that need to be scaled while maintaining crisp edges.
    *
    * @param source the source image
    * @param scale  the scale
    *
    * @return the string
    */
   public static String toSvgString ( final PImage source, final float scale ) {

      source.loadPixels();
      final int pw = source.pixelWidth;
      final int ph = source.pixelHeight;

      final StringBuilder svgp = new StringBuilder(1024);
      svgp.append("<svg ");
      svgp.append("xmlns=\"http://www.w3.org/2000/svg\" ");
      svgp.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
      svgp.append("shape-rendering=\"crispEdges\" ");
      svgp.append("stroke=\"none\" ");
      svgp.append("width=\"");
      svgp.append(Utils.toFixed(source.width * scale,
         ISvgWritable.FIXED_PRINT));
      svgp.append("\" height=\"");
      svgp.append(Utils.toFixed(source.height * scale,
         ISvgWritable.FIXED_PRINT));
      svgp.append("\" viewBox=\"0 0 ");
      svgp.append(pw);
      svgp.append(' ');
      svgp.append(ph);
      svgp.append("\">\n");

      final int[] px = source.pixels;
      final int len = px.length;
      for ( int k = 0; k < len; ++k ) {
         final int hex = px[k];
         final int ai = hex >> 0x18 & 0xff;
         if ( ai > 0 ) {
            final int i0 = k / pw;
            final int j0 = k % pw;

            svgp.append("<path");
            svgp.append(" d=\"M ");
            svgp.append(j0).append(' ').append(i0);
            svgp.append(" h 1 v 1 h -1 Z\" ");

            if ( ai < 255 ) {
               svgp.append("fill-opacity=\"");
               svgp.append(Utils.toFixed(ai * IUtils.ONE_255,
                  ISvgWritable.FIXED_PRINT));
               svgp.append("\" ");
            }

            svgp.append("fill=\"");
            Color.toHexWeb(svgp, hex);
            svgp.append("\" />\n");
         }
      }

      svgp.append("</svg>");
      return svgp.toString();
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
    * @param dx      horizontal pixel offset
    * @param dy      vertical pixel offset
    *
    * @return the target pixels
    */
   public static int[] wrap ( final int[] source, final int wSource,
      final int hSource, final int[] target, final int wTarget, final int dx,
      final int dy ) {

      if ( wSource < 1 || hSource < 1 ) { return target; }

      final int trgLen = target.length;
      for ( int i = 0; i < trgLen; ++i ) {
         int ymod = ( i / wTarget - dy ) % hSource;
         if ( ( ymod ^ hSource ) < 0 && ymod != 0 ) { ymod += hSource; }

         int xmod = ( i % wTarget + dx ) % wSource;
         if ( ( xmod ^ wSource ) < 0 && xmod != 0 ) { xmod += wSource; }

         target[i] = source[xmod + wSource * ymod];
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
      ZImage.wrap(source.pixels, source.pixelWidth, source.pixelHeight,
         target.pixels, target.pixelWidth, dx, dy);
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
